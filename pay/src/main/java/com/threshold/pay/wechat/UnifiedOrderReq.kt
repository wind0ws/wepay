package com.threshold.pay.wechat

import com.threshold.pay.WechatSignType
import com.threshold.pay.WechatPayConfig
import com.threshold.pay.order.OrderInfo
import com.threshold.pay.util.IPUtils
import com.threshold.pay.util.WechatSignUtils
import com.threshold.pay.util.XmlUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * 微信统一预下单请求体.(所有可选类型均可不填,sign除外)
 * @param appId Y 微信开放平台审核通过的应用APPID
 * @param mchId Y 微信支付分配的商户号
 * @param deviceInfo N 终端设备号，默认请传“WEB”
 * @param nonceStr Y 随机字符串，不长于32位，推荐随机数生成算法
 * @param sign Y 签名
 * @param signType N 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
 * @param body Y 商品描述交易字段格式根据不同的应用场景按照以下格式：APP——需传入应用市场上的APP名字-实际商品名称，天天爱消除-游戏充值。
 * @param detail N 商品详细描述，对于使用单品优惠的商户，该字段必须按照规范上传
 * @param attach N 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
 * @param outTradeNo Y 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一
 * @param feeType N 符合ISO 4217标准的三位字母代码，默认人民币：CNY
 * @param totalFee Y 订单总金额，单位为分
 * @param spBillCreateIp Y 用户端实际ip
 * @param timeStart N 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010
 * @param timeExpire N 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010
 * @param goodsTag N 订单优惠标记，代金券或立减优惠功能的参数
 * @param notifyUrl Y 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
 * @param tradeType Y 支付类型
 * @param limitPay N no_credit--指定不能使用信用卡支付
 * @param sceneInfo N 该字段用于统一下单时上报场景信息，目前支持上报实际门店信息。
{
"store_id": "", //门店唯一标识，选填，String(32)
"store_name":""//门店名称，选填，String(64)
}
 *
 */
data class UnifiedOrderReq(val appId: String, val mchId: String, val deviceInfo: String?,
                           val nonceStr: String, var sign: String?, val signType: String?,
                           val body: String, val detail: String?, val attach: String?,
                           val outTradeNo: String, val feeType: String?, val totalFee: Int,
                           val spBillCreateIp: String, val timeStart: String?, val timeExpire: String?,
                           val goodsTag: String?, val notifyUrl: String, val tradeType: String,
                           val limitPay: String?, val sceneInfo: String?
) {

    companion object {
        val WECHAT_DATE_FORMAT = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        fun transformOrderInfoWithSignature(payConfig: WechatPayConfig, orderInfo: OrderInfo): UnifiedOrderReq {
            val expire = Date(orderInfo.timeStamp.time + orderInfo.timeout * 60_000)
            val req = UnifiedOrderReq(payConfig.appId, payConfig.merchantId, "WEB", WechatSignUtils.createNonceStr(),
                    null, payConfig.signType.string, orderInfo.subject, orderInfo.description, null,
                    orderInfo.outTradeNo, "CNY", orderInfo.toWechatPrice(), IPUtils.getIPV4Address(),
                    WECHAT_DATE_FORMAT.format(orderInfo.timeStamp), WECHAT_DATE_FORMAT.format(expire),
                    null, payConfig.notifyUrl, "APP", null, null)
            when (payConfig.signType) {
                WechatSignType.MD5 -> req.sign = WechatSignUtils.signByMD5(req.toSortedMap(), payConfig.appSecret)
                WechatSignType.HMAC_SHA256 -> req.sign = WechatSignUtils.signBySha256(req.toSortedMap(), payConfig.appSecret)
            }
            return req
        }
    }

    private var sortedMap: SortedMap<String, Any>? = null

    fun toSortedMap(): SortedMap<String, Any> {
        if (sortedMap == null) {
            sortedMap = TreeMap<String, Any>()
            with(sortedMap!!) {
                put("appid", appId)
                put("mch_id", mchId)
                put("device_info", deviceInfo)
                put("nonce_str", nonceStr)
                signType?.let { put("sign_type", signType) }
                put("body", body)
                detail?.let { put("detail", detail) }
                attach?.let { put("attach", attach) }
                put("out_trade_no", outTradeNo)
                feeType?.let { put("fee_type", feeType) }
                put("total_fee", totalFee)
                put("spbill_create_ip", spBillCreateIp)
                timeStart?.let { put("time_start", timeStart) }
                timeExpire?.let { put("time_expire", timeExpire) }
                goodsTag?.let { put("goods_tag", goodsTag) }
                put("notify_url", notifyUrl)
                put("trade_type", tradeType)
                limitPay?.let { put("limit_pay", limitPay) }
                sceneInfo?.let { put("scene_info", sceneInfo) }
            }
        }
        return sortedMap!!
    }

    fun toXml(): String {
        if (sign.isNullOrEmpty()) {
            throw IllegalStateException("signature is null. Sign this before convert it to xml!!!")
        }
        return buildString {
            appendln("<xml>")

            val sortedMap = toSortedMap()
            val entries = sortedMap.entries
            entries.forEach { entry ->
                val key = entry.key
                val value = entry.value
                append("<$key>").append(XmlUtil.escaped(value as String)).append("</$key>").appendln()
            }

            append("<sign>").append(sign!!).append("</sign>").appendln()

            appendln("</xml>")
        }
    }

    /**
    <xml>
    <appid>wx2421b1c4370ec43b</appid>
    <attach>支付测试</attach>
    <body>APP支付测试</body>
    <mch_id>10000100</mch_id>
    <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
    <notify_url>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notify_url>
    <out_trade_no>1415659990</out_trade_no>
    <spbill_create_ip>14.23.150.211</spbill_create_ip>
    <total_fee>1</total_fee>
    <trade_type>APP</trade_type>
    <sign>0CB01533B8C1EF103065174F50BCA001</sign>
    </xml>
     */

}