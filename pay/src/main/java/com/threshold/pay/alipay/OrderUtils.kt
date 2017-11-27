package com.threshold.pay.alipay

import com.threshold.pay.AliAuthConfig
import com.threshold.pay.AliPayConfig
import com.threshold.pay.order.OrderInfo
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

object OrderUtils {

    private val timeStampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outTradeNoDateTimeFormat = SimpleDateFormat("MMddHHmmss", Locale.getDefault())

    fun getSignedAuthInfo(authConfig: AliAuthConfig): String {
        val authInfoMap = buildAuthInfoMap(authConfig)
        val sign = getSign(authConfig,authInfoMap)
        val authInfoParam = buildOrderParam(authInfoMap)
        return "$authInfoParam&$sign"
    }

    fun getSignedOrderInfo(payConfig: AliPayConfig,orderInfo: OrderInfo): String {
        val orderParamMap = OrderUtils.buildOrderParamMap(payConfig,orderInfo)
        val sign = OrderUtils.getSign(payConfig,orderParamMap)
        val orderParam = OrderUtils.buildOrderParam(orderParamMap)
        return "$orderParam&$sign"
    }

    /**
     * 构造授权参数列表
     *
     * @return
     */
    private fun buildAuthInfoMap(authConfig: AliAuthConfig): Map<String, String> {
        val keyValues = HashMap<String, String>()

        // 商户签约拿到的app_id，如：2013081700024223
        keyValues.put("app_id", authConfig.appId)

        // 商户签约拿到的pid，如：2088102123816631
        keyValues.put("pid", authConfig.partnerId)

        // 服务接口名称， 固定值
        keyValues.put("apiname", "com.alipay.account.auth")

        // 商户类型标识， 固定值
        keyValues.put("app_name", "mc")

        // 业务类型， 固定值
        keyValues.put("biz_type", "openservice")

        // 产品码， 固定值
        keyValues.put("product_id", "APP_FAST_LOGIN")

        // 授权范围， 固定值
        keyValues.put("scope", "kuaijie")

        // 商户唯一标识，如：kkkkk091125
        keyValues.put("target_id", authConfig.targetId)

        // 授权类型， 固定值
        keyValues.put("auth_type", "AUTHACCOUNT")

        // 签名类型
        keyValues.put("sign_type", if (authConfig.isRSA2) "RSA2" else "RSA")

        return keyValues
    }



    /**
     * 构造支付订单参数列表
     */
    private fun buildOrderParamMap(payConfig: AliPayConfig,orderInfo: OrderInfo): Map<String, String> {
        val keyValues = HashMap<String, String>()

        keyValues.put("app_id", payConfig.appId)

        keyValues.put("biz_content", "{\"timeout_express\":\"${orderInfo.timeout}m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"${orderInfo.toAliPrice()}\",\"subject\":\"${orderInfo.subject}\",\"body\":\"${orderInfo.description}\",\"out_trade_no\":\"${orderInfo.outTradeNo}\"}")

        keyValues.put("charset", "utf-8")

        keyValues.put("method", "alipay.trade.app.pay")

        keyValues.put("sign_type", if (payConfig.isRSA2) "RSA2" else "RSA")

        keyValues.put("timestamp", timeStampFormat.format(orderInfo.timeStamp))

        keyValues.put("version", "1.0")

        return keyValues
    }

    /**
     * 构造支付订单参数信息
     *
     * @param map
     * 支付订单参数
     * @return
     */
    private fun buildOrderParam(map: Map<String, String>): String {
        val keys = ArrayList(map.keys)

        val sb = StringBuilder()
        for (i in 0 until keys.size - 1) {
            val key = keys[i]
            val value = map[key]
            sb.append(buildKeyValue(key, value!!, true))
            sb.append("&")
        }

        val tailKey = keys[keys.size - 1]
        val tailValue = map[tailKey]
        sb.append(buildKeyValue(tailKey, tailValue!!, true))

        return sb.toString()
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private fun buildKeyValue(key: String, value: String, isEncode: Boolean): String {
        val sb = StringBuilder()
        sb.append(key)
        sb.append("=")
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                sb.append(value)
            }

        } else {
            sb.append(value)
        }
        return sb.toString()
    }

    /**
     * 对支付参数信息进行签名
     *
     * @param map
     * 待签名授权信息
     *
     * @return
     */
    private fun getSign(payConfig: AliPayConfig,map: Map<String, String>): String {
        val keys = ArrayList(map.keys)
        // key排序
        Collections.sort(keys)

        val authInfo = StringBuilder()
        for (i in 0 until keys.size - 1) {
            val key = keys[i]
            val value = map[key]
            authInfo.append(buildKeyValue(key, value!!, false))
            authInfo.append("&")
        }

        val tailKey = keys[keys.size - 1]
        val tailValue = map[tailKey]
        authInfo.append(buildKeyValue(tailKey, tailValue!!, false))

        val oriSign = SignUtils.sign(authInfo.toString(), payConfig.rsaPrivateKey, payConfig.isRSA2)
        var encodedSign = ""

        try {
            encodedSign = URLEncoder.encode(oriSign, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return "sign=" + encodedSign
    }

    /**
     * 要求外部订单号必须唯一。
     * @return
     */
    fun createOutTradeNo(): String {
        var key = outTradeNoDateTimeFormat.format(Date())

        val r = Random()
        key += r.nextInt()
        key = key.substring(0, 15)
        return key
    }

}