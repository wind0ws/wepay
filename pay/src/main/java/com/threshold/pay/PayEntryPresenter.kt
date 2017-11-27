package com.threshold.pay

import com.alipay.sdk.app.PayTask
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.threshold.pay.alipay.result.PayResult
import com.threshold.pay.order.OrderInfo
import com.threshold.pay.util.WeakHandler
import android.text.TextUtils
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import okhttp3.RequestBody
import okhttp3.MediaType
import okhttp3.Request
import com.threshold.pay.wechat.UnifiedOrderResp

/**
 * 支付任务调度者
 */
internal class PayEntryPresenter(private val activity: PayEntryActivity) {

    private var handler: WeakHandler? = null
    private var okHttpClient: OkHttpClient? = null
    private var isPaying = false

    fun payByAli(alipayConfig: AliPayConfig, orderInfo: OrderInfo, payStatusListener: PayStatusListener?) {
        if (isPaying || payStatusListener == null) {
            return
        }
        initHandlerIfNecessary()
        Thread {
            handler!!.post { activity.showProgressDialog(R.string.wepay_waiting) }
            isPaying = true
            try {
                val aliSignedOrderInfo = orderInfo.toAlipayOrderInfo(alipayConfig)
                val payTask = PayTask(activity)
                val payResultMap = payTask.payV2(aliSignedOrderInfo, true)
                val payResult = PayResult.parse(payResultMap)
                /**
                对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
//            val resultInfo = payResult.result// 同步返回需要验证的信息
                val resultStatus = payResult.resultStatus
                // 判断resultStatus 为9000则代表支付成功，6001代表用户主动取消
                if (TextUtils.equals(resultStatus, "9000")) {
                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    handler!!.post { payStatusListener.onPaySucceed(orderInfo, PayType.ALIPAY, payResult) }
                } else {
                    // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    handler!!.post { payStatusListener.onPayFailed(orderInfo, PayType.ALIPAY, payResult) }
                }
            } catch (ex: Exception) {
                handler!!.post { payStatusListener.onPayFailed(orderInfo, PayType.ALIPAY, ex) }
            }
            isPaying = false
            activity.finish()
        }.start()
    }

    private fun initHandlerIfNecessary() {
        if (handler == null) {
            handler = WeakHandler(activity.mainLooper)
        }
    }


    fun payByWechat(wechatPayPayConfig: WechatPayConfig, orderInfo: OrderInfo, payStatusListener: PayStatusListener?) {
        if (isPaying || payStatusListener == null) {
            return
        }
        if (!activity.wxApi!!.isWXAppInstalled || !activity.wxApi!!.isWXAppSupportAPI) {
            payStatusListener.onPayFailed(orderInfo, PayType.WECHAT, RuntimeException(activity.getString(R.string.wepay_pay_error_wechat_not_installer_or_support)))
            activity.finish()
            return
        }
        initHandlerIfNecessary()
        Thread {
            isPaying = true
            handler!!.post { activity.showProgressDialog(R.string.wepay_waiting) }
            val unifiedOrderReq = orderInfo.toWechatUnifiedOrderReq(wechatPayPayConfig)
            val unifiedOrderReqXml = unifiedOrderReq.toXml()
            if (okHttpClient == null) {
                okHttpClient = OkHttpClient.Builder()
                        .readTimeout(10, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build()
            }
            val mediaType = MediaType.parse("application/xml")
            val body = RequestBody.create(mediaType, unifiedOrderReqXml)
            val unifiedOrderRequest = Request.Builder()
                    .url("https://api.mch.weixin.qq.com/pay/unifiedorder")
                    .post(body)
                    .addHeader("content-number", "application/xml")
                    .addHeader("cache-control", "no-cache")
                    .build()
            try {
                handler!!.post { activity.showProgressDialog(R.string.wepay_request_unified_order) }
                val response = okHttpClient!!.newCall(unifiedOrderRequest).execute()
                val unifiedOrderStringResp = response.body()?.string()
                unifiedOrderStringResp?.let {
                    val unifiedOrderResp = UnifiedOrderResp.parseUnifiedOrderXmlResp(it)
                    if (unifiedOrderResp.isGetPrepayId()) {
                        val payReq = unifiedOrderResp.toSignedPayReq(wechatPayPayConfig)
                        handler!!.post { activity.wxApi!!.sendReq(payReq) }
                    } else {
                        handler!!.post { payStatusListener.onPayFailed(orderInfo, PayType.WECHAT, unifiedOrderResp.failReturn!!) }
                        activity.finish()
                    }
                }
            } catch (ex: Exception) {
                handler!!.post { payStatusListener.onPayFailed(orderInfo, PayType.WECHAT, ex) }
                activity.finish()
            }
            isPaying = false
        }.start()
    }


    fun handleWechatResponse(resp: BaseResp?, orderInfo: OrderInfo, payStatusListener: PayStatusListener) {
        if (resp == null || resp.type != ConstantsAPI.COMMAND_PAY_BY_WX) {
            return
        }
        when (resp.errCode) {
        /**
         * 0 成功，
         * -1 错误：可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
         * -2 用户取消：无需处理。发生场景：用户不支付了，点击取消，返回APP。
         */
            0 -> {//支付成功
                payStatusListener.onPaySucceed(orderInfo, PayType.WECHAT, resp)
            }
            else -> {
                payStatusListener.onPayFailed(orderInfo, PayType.WECHAT, resp)
            }
        }
    }

}