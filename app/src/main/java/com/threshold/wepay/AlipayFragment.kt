package com.threshold.wepay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.threshold.logger.PrettyLogger
import com.threshold.logger.debug
import com.threshold.logger.error
import com.threshold.logger.info
import com.threshold.pay.AliPayConfig
import com.threshold.pay.PayEntryActivity
import com.threshold.pay.PayStatusListener
import com.threshold.pay.PayType
import com.threshold.pay.alipay.OrderUtils
import com.threshold.pay.order.OrderInfo
import com.threshold.wepay.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_alipay.*
import java.util.*

/**
 * 支付宝支付
 */
class AlipayFragment : Fragment(),PrettyLogger, PayStatusListener {

    companion object {
        val alipayAppId = "112015120300910625"
        val rsaPrivateKey = "11MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOG+n1YDztJzCdSG\n" +
        "/ix9CRYs37NI+tCIok7zmxUEzJvQ0dDJtHS26vSk00I8d4cxK3IgBm0ajCjvhqX/\n" +
        "Jd4syWMLhbqIdl58gwAjDVrItL8AQPk+NQ4SU56wzQiTTk24tq66Jz6uKXK4dC3m\n" +
        "sA+x2JsP/hJT1CPZ27KBLViB3we3AgMBAAECgYATsiQIxzBGYE8dQeHvU8F1M9kY\n" +
        "tWdcxm8S4GJJhS+s8J090Dt0a3k4eyELiGcNXpbh0DV/U2+jKrLezNIq/lFbrvBN\n" +
        "jG4c81bmAhaq0AIVrDTrBMg8HioKCTYAGTazc2gXRpZcyKWozznVtOrf/LQNyfVg\n" +
        "AfxxL114oBqlwZHewQJBAPdg2x3ZKX07cnip1tDidbuRZmLzoYjQY8kgVvBU8m+x\n" +
        "DrErt++2YhiCDBSGyZSDFOuvIFuWKv6X7iUbaFtggLECQQDpnL9T4rgcYylVpuZv\n" +
        "DQo3w+8v/tqn3DnGRhwjEpPe73pOwHT4LyCGChZqWzKdBNMlsF5BG+TOyf2Yixgq\n" +
        "c2jnAkAhT6iR32FH3M5wUyZ7IFOMO2sXHgl9e9pEBhfO6xFuWuBImUN+gwe4lyJ7\n" +
        "TR8t4Wzaw81Op+0INXs7AfLOgzMxAkEAulTlIpKWiUXqpT+/UtpSyRIGCQE9BGCa\n" +
        "0HQBY1QhD9Nxebj2UzK7vU30fRaKs0Uy6T/YnhQSl4HzPSXMu6dcBwJBALdaO87b\n" +
        "QbNetU/TKKXLyel9hrZ3KX8eEtqyB+N9X0m92tIVwJ3N+ZYmf+HDUJjDeh8SZ7J0\n" +
        "0ofb3ehmNPJfccE="
    }

    override fun onPaySucceed(orderInfo: OrderInfo,payType: PayType, result: Any) {
        ToastUtil.showShort(context,"支付成功。")
        info("支付宝支付成功")
        debug { "支付类型: $payType \n订单信息$orderInfo\n支付宝原始返回：\n$result" }
    }

    override fun onPayFailed(orderInfo: OrderInfo,payType: PayType, result: Any) {
        ToastUtil.showShort(context,"支付失败。请查看Logcat")
        error { "支付宝支付失败" }
        error { "支付类型: $payType \n订单信息: $orderInfo\n支付宝原始返回：\n$result" }
    }

    private var payConfig:AliPayConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_alipay, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        payConfig = AliPayConfig(alipayAppId, rsaPrivateKey,false)
        payV2.setOnClickListener{ payV2(it) }
        authV2.setOnClickListener { authV2(it) }
        h5pay.setOnClickListener{ h5Pay(it) }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
//        mListener = null
    }

    fun payV2(view: View): Unit {
        debug { "支付宝payV2" }
        //注意金额单位为分。 1元 = 100分
        val orderInfo = OrderInfo("admin",OrderUtils.createOutTradeNo(),123,"驾培宝订单缴费-admin-2017.10.11",
                "订单详情描述", Date())
        PayEntryActivity.payStatusListener = this
        debug { "调起支付宝" }
        PayEntryActivity.startAliPay(context,orderInfo,payConfig!!)
    }

    fun authV2(view: View): Unit {
        info { "支付宝authV2" }
        ToastUtil.show(context,"暂未实现")
    }

    fun h5Pay(view: View): Unit {
        debug { "支付宝h5Pay" }
        val intent = Intent(context, AliH5PayDemoActivity::class.java)
        val extras = Bundle()
        /**
         * url 是要测试的网站，在 Demo App 中会使用 AliH5PayDemoActivity 内的 WebView 打开。
         *
         * 可以填写任一支持支付宝支付的网站（如淘宝或一号店），在网站中下订单并唤起支付宝；
         * 或者直接填写由支付宝文档提供的“网站 Demo”生成的订单地址
         * （如 https://mclient.alipay.com/h5Continue.htm?h5_route_token=303ff0894cd4dccf591b089761dexxxx）
         * 进行测试。
         *
         * AliH5PayDemoActivity 中的 MyWebViewClient.shouldOverrideUrlLoading() 实现了拦截 URL 唤起支付宝，
         * 可以参考它实现自定义的 URL 拦截逻辑。
         */
        val url = "http://m.taobao.com"
        extras.putString("url", url)
        intent.putExtras(extras)
        startActivity(intent)
    }


//    companion object {
//
//        private val ARG_PARAM1 = "param1"
//        private val ARG_PARAM2 = "param2"
//
//
//        fun newInstance(param1: String, param2: String): AlipayFragment {
//            val fragment = AlipayFragment()
//            val args = Bundle()
//            args.putString(ARG_PARAM1, param1)
//            args.putString(ARG_PARAM2, param2)
//            fragment.arguments = args
//            return fragment
//        }
//    }
}
