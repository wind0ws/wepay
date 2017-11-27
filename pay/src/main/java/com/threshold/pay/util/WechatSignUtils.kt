package com.threshold.pay.util

import java.util.*

object WechatSignUtils{
    /**
     * 微信支付md5签名算法sign
     * @param characterEncoding 字符集，比如 “UTF-8”
     * @param parameters SortedMap<Object></Object>,Object> 可排序的Map
     * @return 签名字符串
     */
    @JvmOverloads
    fun signByMD5( parameters: SortedMap<out Any, Any>, appSecretKey: String,characterEncoding: String = "UTF-8"): String {
        val sb = appendSecretKey(parameters, appSecretKey)
        return DigestUtils.md5(sb.toString().toByteArray(charset(characterEncoding))).toString().toUpperCase()
//        return MD5Util.md5Encode(sb.toString(), characterEncoding).toUpperCase()
    }

    @JvmOverloads
    fun signBySha256(parameters: SortedMap<out Any, Any>, appSecretKey: String,characterEncoding: String = "UTF-8"): String {
        val sb = appendSecretKey(parameters, appSecretKey)
        return DigestUtils.sha256(sb.toString().toByteArray(charset(characterEncoding))).toString().toUpperCase()
    }

    private fun appendSecretKey(parameters: SortedMap<out Any, Any>, appSecretKey: String): StringBuilder {
        val sb = sortParams(parameters)
        sb.append("key=").append(appSecretKey)
        return sb
    }

    private fun sortParams(parameters: SortedMap<out Any, Any>): StringBuilder {
        val sb = StringBuilder()
        val es = parameters.entries//所有参与传参的参数按照accsii排序（升序）
        for (entry in es) {
            val k = entry.key as String
            val v = entry.value
            if (null != v && "" != v
                    && "sign" != k && "key" != k) {
                sb.append(k).append("=").append(v as String).append("&")
            }
        }
        return sb
    }

    fun createNonceStr(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}