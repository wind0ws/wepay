package com.threshold.wepay

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.threshold.logger.PrettyLogger
import com.threshold.logger.debug
import com.threshold.logger.error
import com.threshold.pay.PayEntryActivity
import com.threshold.pay.PayStatusListener
import com.threshold.pay.PayType
import com.threshold.pay.WechatPayConfig
import com.threshold.pay.alipay.OrderUtils
import com.threshold.pay.order.OrderInfo
import com.threshold.wepay.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_wechat_pay.*
import java.util.*


class WechatPayFragment : Fragment(),View.OnClickListener,PrettyLogger, PayStatusListener {

    val payConfig = WechatPayConfig("appid","appSecret","corpId","merchantId",
            "http://www.notifyUrl")

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_wechat_pay, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnWechatPay.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.btnWechatPay -> onBtnWechatPayClicked()
            }
        }
    }

    private fun onBtnWechatPayClicked() {
        //注意金额单位为分。 1元 = 100分
        val orderInfo = OrderInfo("admin", OrderUtils.createOutTradeNo(),123,"订单缴费-admin-2017.10.11",
                "订单详情描述", Date())
        PayEntryActivity.payStatusListener = this
        debug { "调起微信支付" }
        PayEntryActivity.startWeChatPay(context,orderInfo,payConfig)
    }

    override fun onPaySucceed(orderInfo: OrderInfo,payType: PayType, result: Any) {
        ToastUtil.showShort(context,"支付成功。")
        debug { "微信支付成功\n支付类型: $payType \n订单信息:$orderInfo\n result：\n$result" }
    }

    override fun onPayFailed(orderInfo: OrderInfo,payType: PayType, result: Any) {
        ToastUtil.showShort(context,"支付失败。请查看Logcat")
        error { "微信支付失败\n支付类型: $payType \n订单信息: $orderInfo\n result：\n$result" }
    }

}
