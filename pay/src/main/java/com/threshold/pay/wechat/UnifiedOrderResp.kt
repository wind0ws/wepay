package com.threshold.pay.wechat

import com.tencent.mm.opensdk.modelpay.PayReq
import com.threshold.pay.WechatSignType
import com.threshold.pay.WechatPayConfig
import com.threshold.pay.util.WechatSignUtils
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

/**
 * 统一下单的回复
 * Created by Threshold on 2017/9/30.
 */
data class UnifiedOrderResp(val returnCode: String, val successReturn: SuccessReturn?, val failReturn: FailReturn? = null) {
    companion object {

        fun parseUnifiedOrderXmlResp(xmlResp: String): UnifiedOrderResp {
            if (xmlResp.isBlank()) {
                throw IllegalStateException("UnifiedOrder Response is blank")
            }
            val factory = DocumentBuilderFactory.newInstance()
            val documentBuilder = factory.newDocumentBuilder()
            val inputSource = InputSource(StringReader(xmlResp))
            val document = documentBuilder.parse(inputSource)
            document.documentElement.normalize()
            val root = document.documentElement

            val returnCode = getElementTextContent(root, "return_code")
            var successReturn: SuccessReturn? = null
            var failReturn: FailReturn? = null
            var successResult: SuccessResult? = null
            var failResult: FailResult? = null
            if (returnCode.equals("SUCCESS", true)) {
                val appId = getElementTextContent(root, "appid")
                val mchId = getElementTextContent(root, "mch_id")
                val deviceInfo = getElementTextContent(root, "device_info")
                val nonceStr = getElementTextContent(root, "nonce_str")
                val sign = getElementTextContent(root, "sign")
                val resultCode = getElementTextContent(root, "result_code")
                if (resultCode.equals("SUCCESS", true)) {
                    val prepayId = getElementTextContent(root, "prepay_id")
                    val tradeType = getElementTextContent(root, "trade_type")
                    successResult = SuccessResult(tradeType, prepayId)
                } else {
                    val errCode = getElementTextContent(root, "err_code")
                    val errCodeDes = getElementTextContent(root, "err_code_des")
                    failResult = FailResult(errCode, errCodeDes)
                }
                successReturn = SuccessReturn(appId, mchId, deviceInfo, nonceStr, sign, successResult, failResult)
            } else {
                val returnMsg = getElementTextContent(root, "return_msg")
                failReturn = FailReturn(returnMsg)
            }

            return UnifiedOrderResp(returnCode, successReturn, failReturn)
        }

        private fun getElementTextContent(root: Element, tagName: String): String {
            return root.getElementsByTagName(tagName).item(0).textContent.trim { it <= ' ' }
        }
    }

    fun isGetPrepayId() = successReturn?.successResult?.prepayId.isNullOrEmpty()

    fun toSignedPayReq(payConfig: WechatPayConfig): PayReq {
        if (!isGetPrepayId()) {
            throw IllegalStateException("Can't perform transform to PayReq, because UnifiedOrderResp do not have valid prepayId.")
        }
        val payReq = PayReq()
        with(successReturn!!) {
            payReq.appId = appId
            payReq.partnerId = mchId
            payReq.packageValue = "Sign=WXPay"
            payReq.nonceStr = nonceStr
            payReq.prepayId = successResult?.prepayId
            payReq.timeStamp = System.currentTimeMillis().toString()
            payReq.extData = "app data" // optional
            when (payConfig.signType) {
                WechatSignType.MD5 -> payReq.sign = WechatSignUtils.signByMD5(getPayReqParams(payReq), payConfig.appSecret)
                WechatSignType.HMAC_SHA256 -> payReq.sign = WechatSignUtils.signBySha256(getPayReqParams(payReq), payConfig.appSecret)
            }
        }
        return payReq
    }

    private fun getPayReqParams(payReq: PayReq): SortedMap<Any, Any> {
        val map = TreeMap<Any, Any>()
        map.put("appid", payReq.appId)
        map.put("partnerid", payReq.partnerId)
        map.put("package", payReq.packageValue)
        map.put("noncestr", payReq.nonceStr)
        map.put("timestamp", payReq.timeStamp)
        map.put("prepayid", payReq.prepayId)
        return map
    }
    /*

    <xml>
   <return_code><![CDATA[SUCCESS]]></return_code>
   <return_msg><![CDATA[OK]]></return_msg>
   <appid><![CDATA[wx2421b1c4370ec43b]]></appid>
   <mch_id><![CDATA[10000100]]></mch_id>
   <nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>
   <sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>
   <result_code><![CDATA[SUCCESS]]></result_code>
   <prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>
   <trade_type><![CDATA[APP]]></trade_type>
    </xml>

  失败时返回
    <xml>
          <return_code>
              <![CDATA[FAIL]]>
         </return_code>
        <return_msg>
            <![CDATA[appid参数长度有误]]>
         </return_msg>
    </xml>

    构造PayReq需要的信息（主要从上面预下单返回的xml中获取预下单ID）：
    比如从WeiXin测试服务器上获得的反馈：http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android
    {
        "appid": "wxb4ba3c02aa476ea1",
        "partnerid": "1305176001",
        "package": "Sign=WXPay",
        "noncestr": "27ae24e1ae2c3418cfaca92349f795d3",
        "timestamp": 1472092333,
        "prepayid": "wx201608251032137b3287b2580965479747",
        "sign": "0615DC386CD03142ACA9AC8597262C07"
    }

    */


    data class SuccessReturn(val appId: String, val mchId: String,
                             val deviceInfo: String?, val nonceStr: String, val sign: String,
                             val successResult: SuccessResult?, val failResult: FailResult? = null)

    data class SuccessResult(val tradeType: String, val prepayId: String)

    data class FailResult(val errCode: String, val errCodeDes: String)

    data class FailReturn(val returnMsg: String)


    /**
     * 对应 [UnifiedOrderResp.FailResult.errCode]
     */
    enum class ErrorCode(val str: String) {
        /**
         * 商户无此接口权限
         */
        NO_AUTH("NOAUTH"),
        /**
         * 余额不足
         */
        NOT_ENOUGH("NOTENOUGH"),
        /**
         * 商户订单已支付
         */
        ORDER_PAID("ORDERPAID"),
        /**
         * 订单已关闭
         */
        ORDER_CLOSED("ORDERCLOSED"),
        /**
         * 系统错误
         */
        SYSTEM_ERROR("SYSTEMERROR"),
        /**
         * APPID不存在
         */
        APPID_NOT_EXIST("APPID_NOT_EXIST"),
        /**
         * MCHID不存在
         */
        MCHID_NOT_EXIST("MCHID_NOT_EXIST"),
        /**
         * appid和mch_id不匹配
         */
        APPID_MCHID_NOT_MATCH("APPID_MCHID_NOT_MATCH"),
        /**
         * 缺少参数
         */
        LACK_PARAMS("LACK_PARAMS"),
        /**
         * 商户订单号重复
         */
        OUT_TRADE_NO_USED("OUT_TRADE_NO_USED"),
        /**
         * 签名错误
         */
        SIGN_ERROR("SIGNERROR"),
        /**
         * XML格式错误
         */
        XML_FORMAT_ERROR("XML_FORMAT_ERROR"),
        /**
         * 请使用post方法
         */
        REQUIRE_POST_METHOD("REQUIRE_POST_METHOD"),
        /**
         * post数据为空
         */
        POST_DATA_EMPTY("POST_DATA_EMPTY"),
        /**
         * 编码格式错误
         */
        NOT_UTF8("NOT_UTF8")
    }

}