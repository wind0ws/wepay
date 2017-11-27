package com.threshold.pay.alipay.result

import android.text.TextUtils

/**
 * @param resultStatus 9000 代表支付成功；   6001 代表用户主动取消； 其他情况可能需要支付宝的异步 notify url 通知
 * @param
 */
data class PayResult(val resultStatus:String,val result:String,val memo:String) {

    companion object {
        fun parse(rawResult: Map<String, String>):PayResult {
            var resultStatus = ""
            var result = ""
            var memo = ""
            for (key in rawResult.keys) {
                when {
                    TextUtils.equals(key, "resultStatus") -> rawResult[key]?.let { resultStatus = it }
                    TextUtils.equals(key, "result") -> rawResult[key]?.let { result = it }
                    TextUtils.equals(key, "memo") ->  rawResult[key]?.let { memo = it }
                }
            }
            return PayResult(resultStatus, result, memo)
        }
    }

}