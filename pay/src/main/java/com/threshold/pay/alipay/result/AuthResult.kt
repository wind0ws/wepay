package com.threshold.pay.alipay.result

import android.text.TextUtils


/**
 * Created by Threshold on 2017/9/28.
 */
class AuthResult(rawResult: Map<String, String>, removeBrackets: Boolean) {
    var resultStatus: String? = null
        private set

    var result: String? = null
        private set

    var memo: String? = null
        private set

    var resultCode: String? = null
        private set

    var authCode: String? = null
        private set

    var alipayOpenId: String? = null
        private set


    init{
        for (key in rawResult.keys) {
            when {
                TextUtils.equals(key, "resultStatus") -> resultStatus = rawResult[key]
                TextUtils.equals(key, "result") -> result = rawResult[key]
                TextUtils.equals(key, "memo") -> memo = rawResult[key]
            }
        }

        val resultValue = result!!.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (value in resultValue) {
            if (value.startsWith("alipay_open_id")) {
                alipayOpenId = removeBrackets(getValue("alipay_open_id=", value), removeBrackets)
                continue
            }
            if (value.startsWith("auth_code")) {
                authCode = removeBrackets(getValue("auth_code=", value), removeBrackets)
                continue
            }
            if (value.startsWith("result_code")) {
                resultCode = removeBrackets(getValue("result_code=", value), removeBrackets)
                continue
            }
        }

    }

    private fun removeBrackets(str: String, remove: Boolean): String? {
        var myStr = str
        if (remove) {
            if (!TextUtils.isEmpty(myStr)) {
                if (myStr.startsWith("\"")) {
                    myStr = myStr.replaceFirst("\"".toRegex(), "")
                }
                if (myStr.endsWith("\"")) {
                    myStr = myStr.substring(0, myStr.length - 1)
                }
            }
        }
        return myStr
    }

    override fun toString(): String {
        return "resultStatus={$resultStatus};memo={$memo};result={$result}"
    }

    private fun getValue(header: String, data: String): String {
        return data.substring(header.length, data.length)
    }

}