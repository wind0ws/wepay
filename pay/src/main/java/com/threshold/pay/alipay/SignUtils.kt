package com.threshold.pay.alipay

import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec

object SignUtils {
    private val ALGORITHM = "RSA"

    private val SIGN_ALGORITHMS = "SHA1WithRSA"

    private val SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA"

    private val DEFAULT_CHARSET = "UTF-8"

    private fun getAlgorithms(rsa2: Boolean): String {
        return if (rsa2) SIGN_SHA256RSA_ALGORITHMS else SIGN_ALGORITHMS
    }

    fun sign(content: String, privateKey: String, rsa2: Boolean): String {
        val priPKCS8 = PKCS8EncodedKeySpec(
                android.util.Base64.decode(privateKey.toByteArray(charset(DEFAULT_CHARSET)),android.util.Base64.NO_WRAP))
        val keyf = KeyFactory.getInstance(ALGORITHM)
        val priKey = keyf.generatePrivate(priPKCS8)

        val signature = java.security.Signature
                .getInstance(getAlgorithms(rsa2))

        signature.initSign(priKey)
        signature.update(content.toByteArray(charset(DEFAULT_CHARSET)))

        val signed = signature.sign()
        return android.util.Base64.encodeToString(signed,android.util.Base64.NO_WRAP)
//        return Base64.encode(signed)
    }

}