package com.threshold.pay

import com.tencent.mm.opensdk.modelbase.BaseResp
import com.threshold.pay.order.OrderInfo
import java.io.Serializable

/**
 * 支付方式
 */
enum class PayType(val number: Int) {
    ALIPAY(0), WECHAT(1)
}

/**
 * 支付状态监听.
 * 当payType支付方式为支付宝时,result 为支付宝的返回信息[com.threshold.pay.alipay.result.PayResult]
 * 当payType支付方式为微信时,result 为微信相关返回值，比如[com.threshold.pay.wechat.UnifiedOrderResp.FailReturn] 或 [BaseResp]
 */
interface PayStatusListener {
    fun onPaySucceed(orderInfo: OrderInfo, payType: PayType, result: Any)

    fun onPayFailed(orderInfo: OrderInfo, payType: PayType, result: Any)
}

/**
 * 授权认证监听
 */
interface AuthStatusListener {
    fun onAuthSucceed(result: Any)

    fun onAuthFailed(result: Any)
}

/**
 * 支付宝授权认证配置
 * @param appId 商户签约拿到的app_id，如：2013081700024223
 * @param partnerId 商户签约拿到的pid，如：2088102123816631
 * @param targetId 商户唯一标识，如：kkkkk091125
 */
class AliAuthConfig(appId: String, val partnerId: String, val targetId: String, rsaPrivateKey: String, isRSA2: Boolean = false) : AliPayConfig(appId, rsaPrivateKey, isRSA2)

open class AliPayConfig(val appId: String, val rsaPrivateKey: String, val isRSA2: Boolean = false) : Serializable


class WechatPayConfig(val appId: String, val appSecret: String, val corpId: String, val merchantId: String,
                      val notifyUrl: String, val signType: WechatSignType = WechatSignType.MD5) : Serializable

/**
 * 微信签名方式
 */
enum class WechatSignType(val string: String) {
    MD5("MD5"), HMAC_SHA256("HMAC-SHA256")
}