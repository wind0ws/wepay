package com.threshold.pay.order

import com.threshold.pay.AliPayConfig
import com.threshold.pay.WechatPayConfig
import com.threshold.pay.alipay.OrderUtils
import com.threshold.pay.wechat.UnifiedOrderReq
import java.io.Serializable
import java.util.*

/**
 * 订单信息
 * @param outTradeNo 商户订单唯一ID，由商户自己定义。推荐使用时间戳
 * @param price 单位为分：阿里以元为单位，微信以分为单位
 * @param subject 订单标题
 * @param description 订单详细描述
 * @param timeStamp 订单创建时间
 * @param timeout 订单超时取消支付时间，单位分钟，默认30分钟
 * @param tag 订单备注信息（不提交给支付宝和微信，仅供应用内使用）
 */
data class OrderInfo(val userId:String, val outTradeNo:String, val price:Int,
                     val subject:String, var description:String, val timeStamp:Date, var timeout:Int = 30, var tag:Any? = null):Serializable{

    fun toAliPrice(): String = "${price.toDouble() / 100.00}"

    fun toWechatPrice() = price

    fun toAlipayOrderInfo(payConfig: AliPayConfig) = OrderUtils.getSignedOrderInfo(payConfig,this)

    fun toWechatUnifiedOrderReq(payConfig: WechatPayConfig) = UnifiedOrderReq.transformOrderInfoWithSignature(payConfig,this)

}