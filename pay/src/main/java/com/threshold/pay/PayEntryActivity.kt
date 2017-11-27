package com.threshold.pay

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.threshold.pay.order.OrderInfo
import com.threshold.pay.util.ProgressDialogBuilder
import java.io.Serializable

/**
 * 支付入口Activity
 */
class PayEntryActivity : Activity(), IWXAPIEventHandler {

    companion object {
        private val EXTRA_ARG_PAY_TYPE = PayType::class.java.simpleName
        private val PAY_TYPE_ALIPAY = PayType.ALIPAY.number
        private val PAY_TYPE_WECHAT = PayType.WECHAT.number

        private val EXTRA_ARG_PAY_CONFIG = "PAY_CONFIG"
        private val EXTRA_ARG_ORDER_INFO = "ORDER_INFO"

        var outerWXApiEventHandler: IWXAPIEventHandler? = null
        /**
         * 支付状态监听
         */
        var payStatusListener: PayStatusListener? = null

        /**
         * 调起支付宝支付
         */
        fun startAliPay(context: Context, orderInfo: OrderInfo, payConfig: AliPayConfig) {
            val intent = buildIntent(context, payConfig, orderInfo)
            intent.putExtra(EXTRA_ARG_PAY_TYPE, PAY_TYPE_ALIPAY)
            context.startActivity(intent)
        }

        /**
         * 调起微信支付
         */
        fun startWeChatPay(context: Context, orderInfo: OrderInfo, payConfig: WechatPayConfig) {
            val intent = buildIntent(context, payConfig, orderInfo)
            intent.putExtra(EXTRA_ARG_PAY_TYPE, PAY_TYPE_WECHAT)
            context.startActivity(intent)
        }

        private fun buildIntent(context: Context, payConfig: Serializable, orderInfo: OrderInfo): Intent {
            val intent = Intent(context, PayEntryActivity::class.java)
            with(intent) {
                putExtra(EXTRA_ARG_PAY_CONFIG, payConfig)
                putExtra(EXTRA_ARG_ORDER_INFO, orderInfo)
            }
            return intent
        }
    }

    private var payType = 0
    var wxApi: IWXAPI? = null
        private set
    private var orderInfo: OrderInfo? = null
    private var wechatPayPayConfig: WechatPayConfig? = null
    private var alipayConfig: AliPayConfig? = null
    private val payPresenter = PayEntryPresenter(this)
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        payType = intent.getIntExtra(EXTRA_ARG_PAY_TYPE, PAY_TYPE_ALIPAY)
        orderInfo = intent.getSerializableExtra(EXTRA_ARG_ORDER_INFO) as? OrderInfo
        if (orderInfo == null) {
            throw IllegalStateException("You should pass in OrderInfo for pay.Hint: You should use factory method to start this activity!")
        }
        when (payType) {
            PAY_TYPE_WECHAT -> {
                wechatPayPayConfig = intent.getSerializableExtra(EXTRA_ARG_PAY_CONFIG) as? WechatPayConfig
                if (wechatPayPayConfig == null) {
                    throw IllegalStateException("No pay config for wechat.")
                }
                wxApi = WXAPIFactory.createWXAPI(this, wechatPayPayConfig!!.appId)
                payPresenter.payByWechat(wechatPayPayConfig!!, orderInfo!!, payStatusListener)
            }
            PAY_TYPE_ALIPAY -> {
                alipayConfig = intent.getSerializableExtra(EXTRA_ARG_PAY_CONFIG) as? AliPayConfig
                if (alipayConfig == null) {
                    throw IllegalStateException("No pay config for alipay.")
                }
                payPresenter.payByAli(alipayConfig!!, orderInfo!!, payStatusListener)
            }
            else -> {
                throw IllegalStateException("Unknown pay type: $payType, now only support alipay and wechat.")
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (payType == PAY_TYPE_WECHAT && intent != null) {
            wxApi?.handleIntent(intent, this)
        }
    }

    override fun onReq(req: BaseReq?) {
        outerWXApiEventHandler?.onReq(req)
    }

    override fun onResp(resp: BaseResp?) {
        outerWXApiEventHandler?.let {
            it.onResp(resp)
            finish()
            return
        }
        if (orderInfo != null && payStatusListener != null) {
            payPresenter.handleWechatResponse(resp, orderInfo!!, payStatusListener!!)
        }
        finish()
    }


    fun showProgressDialog(@StringRes messageResId: Int) {
        closeProgressDialog()
        progressDialog = ProgressDialogBuilder.build(this) {
            this@build.messageResId = messageResId
            cancelable = false
            canceledOnTouchOutside = false
            indeterminate = true
        }
        progressDialog?.show()
    }

    private fun closeProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun onDestroy() {
        closeProgressDialog()
        super.onDestroy()
        payStatusListener = null
    }

}