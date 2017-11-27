package com.threshold.pay.util

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

object StringUtils {
    /**
     * Encodes the given string into a sequence of bytes using the ISO-8859-1 charset, storing the result into a new
     * byte array.
     *
     * @param string
     * the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     * Thrown when the charset is missing, which should be never according the the Java specification.
     * @see [Standard charsets
     *
     * @see .getBytesUnchecked
    ](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html) */
    fun getBytesIso8859_1(string: String): ByteArray {
        return StringUtils.getBytesUnchecked(string, CharEncoding.ISO_8859_1)
    }

    /**
     * Encodes the given string into a sequence of bytes using the US-ASCII charset, storing the result into a new byte
     * array.
     *
     * @param string
     * the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     * Thrown when the charset is missing, which should be never according the the Java specification.
     * @see [Standard charsets
     *
     * @see .getBytesUnchecked
    ](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html) */
    fun getBytesUsAscii(string: String): ByteArray {
        return StringUtils.getBytesUnchecked(string, CharEncoding.US_ASCII)
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16 charset, storing the result into a new byte
     * array.
     *
     * @param string
     * the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     * Thrown when the charset is missing, which should be never according the the Java specification.
     * @see [Standard charsets
     *
     * @see .getBytesUnchecked
    ](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html) */
    fun getBytesUtf16(string: String): ByteArray {
        return StringUtils.getBytesUnchecked(string, CharEncoding.UTF_16)
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16BE charset, storing the result into a new byte
     * array.
     *
     * @param string
     * the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     * Thrown when the charset is missing, which should be never according the the Java specification.
     * @see [Standard charsets
     *
     * @see .getBytesUnchecked](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html) */
    fun getBytesUtf16Be(string: String): ByteArray {
        return StringUtils.getBytesUnchecked(string, CharEncoding.UTF_16BE)
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16LE charset, storing the result into a new byte
     * array.
     *
     * @param string
     * the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     * Thrown when the charset is missing, which should be never according the the Java specification.
     * @see [Standard charsets.getBytesUnchecked](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html) */
    fun getBytesUtf16Le(string: String): ByteArray {
        return StringUtils.getBytesUnchecked(string, CharEncoding.UTF_16LE)
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-8 charset, storing the result into a new byte
     * array.
     *
     * @param string
     * the String to encode
     * @return encoded bytes
     * @throws IllegalStateException
     * Thrown when the charset is missing, which should be never according the the Java specification.
     * @see [Standard charsets.getBytesUnchecked](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html) */
    fun getBytesUtf8(string: String): ByteArray {
        return StringUtils.getBytesUnchecked(string, CharEncoding.UTF_8)
    }

    /**
     * Encodes the given string into a sequence of bytes using the named charset, storing the result into a new byte
     * array.
     *
     *
     * This method catches [UnsupportedEncodingException] and rethrows it as [IllegalStateException], which
     * should never happen for a required charset name. Use this method when the encoding is required to be in the JRE.
     *
     *
     * @param string
     * the String to encode
     * @param charsetName
     * The name of a required [java.nio.charset.Charset]
     * @return encoded bytes
     * @throws IllegalStateException
     * Thrown when a [UnsupportedEncodingException] is caught, which should never happen for a
     * required charset name.
     * @see CharEncoding
     *
     * @see String.getBytes
     */
    fun getBytesUnchecked(string: String, charsetName: String): ByteArray {
        try {
            return string.toByteArray(charset(charsetName))
        } catch (e: UnsupportedEncodingException) {
            throw StringUtils.newIllegalStateException(charsetName, e)
        }
    }

    private fun newIllegalStateException(charsetName: String, e: UnsupportedEncodingException): IllegalStateException {
        return IllegalStateException(charsetName + ": " + e)
    }

    /**
     * Constructs a new `String by decoding the specified array of bytes using the given charset.
     *
     *
     * This method catches [UnsupportedEncodingException] and re-throws it as [IllegalStateException], which
     * should never happen for a required charset name. Use this method when the encoding is required to be in the JRE.
     *
     *
     * @param bytes
     * The bytes to be decoded into characters
     * @param charsetName
     * The name of a required [java.nio.charset.Charset]
     * @return A new `String decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     * Thrown when a [UnsupportedEncodingException] is caught, which should never happen for a
     * required charset name.
     * @see CharEncoding
     *
     * @see String.String
    `` */
    fun newString(bytes: ByteArray, charsetName: String): String {
        try {
            return String(bytes, Charset.forName(charsetName))
        } catch (e: UnsupportedEncodingException) {
            throw StringUtils.newIllegalStateException(charsetName, e)
        }
    }

    /**
     * Constructs a new `String by decoding the specified array of bytes using the ISO-8859-1 charset.
     *
     * @param bytes
     * The bytes to be decoded into characters
     * @return A new `String decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     * Thrown when a [UnsupportedEncodingException] is caught, which should never happen since the
     * charset is required.
    `` */
    fun newStringIso8859_1(bytes: ByteArray): String {
        return StringUtils.newString(bytes, CharEncoding.ISO_8859_1)
    }

    /**
     * Constructs a new `String by decoding the specified array of bytes using the US-ASCII charset.
     *
     * @param bytes
     * The bytes to be decoded into characters
     * @return A new `String decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     * Thrown when a [UnsupportedEncodingException] is caught, which should never happen since the
     * charset is required.
    `` */
    fun newStringUsAscii(bytes: ByteArray): String {
        return StringUtils.newString(bytes, CharEncoding.US_ASCII)
    }

    /**
     * Constructs a new `String by decoding the specified array of bytes using the UTF-16 charset.
     *
     * @param bytes
     * The bytes to be decoded into characters
     * @return A new `String decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     * Thrown when a [UnsupportedEncodingException] is caught, which should never happen since the
     * charset is required.
    `` */
    fun newStringUtf16(bytes: ByteArray): String {
        return StringUtils.newString(bytes, CharEncoding.UTF_16)
    }

    /**
     * Constructs a new `String by decoding the specified array of bytes using the UTF-16BE charset.
     *
     * @param bytes
     * The bytes to be decoded into characters
     * @return A new `String decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     * Thrown when a [UnsupportedEncodingException] is caught, which should never happen since the
     * charset is required.
    `` */
    fun newStringUtf16Be(bytes: ByteArray): String {
        return StringUtils.newString(bytes, CharEncoding.UTF_16BE)
    }

    /**
     * Constructs a new `String by decoding the specified array of bytes using the UTF-16LE charset.
     *
     * @param bytes
     * The bytes to be decoded into characters
     * @return A new `String decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     * Thrown when a [UnsupportedEncodingException] is caught, which should never happen since the
     * charset is required.
    `` */
    fun newStringUtf16Le(bytes: ByteArray): String {
        return StringUtils.newString(bytes, CharEncoding.UTF_16LE)
    }

    /**
     * Constructs a new `String by decoding the specified array of bytes using the UTF-8 charset.
     *
     * @param bytes
     * The bytes to be decoded into characters
     * @return A new `String decoded from the specified array of bytes using the given charset.
     * @throws IllegalStateException
     * Thrown when a [UnsupportedEncodingException] is caught, which should never happen since the
     * charset is required.
    `` */
    fun newStringUtf8(bytes: ByteArray): String {
        return StringUtils.newString(bytes, CharEncoding.UTF_8)
    }

    fun isAnyoneIsNullOrEmpty(vararg strs:String?):Boolean{
        strs.forEach {
            if (it.isNullOrEmpty()) {
                return true
            }
        }
        return false
    }
}
