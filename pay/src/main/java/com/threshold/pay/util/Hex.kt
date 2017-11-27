package com.threshold.pay.util

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * Created by Threshold on 2017/10/9.
 */

class Hex(private val charsetName: String = DEFAULT_CHARSET_NAME) {


    /**
     * Converts an array of character bytes representing hexadecimal values into an array of bytes of those same values.
     * The returned array will be half the length of the passed array, as it takes two characters to represent any given
     * byte. An exception is thrown if the passed char array has an odd number of elements.
     *
     * @param array
     * An array of character bytes containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
     * @throws RuntimeException
     * Thrown if an odd number of characters is supplied to this function
     * @see .decodeHex
     */
    @Throws(RuntimeException::class)
    fun decode(array: ByteArray): ByteArray {
        try {
            return decodeHex(String(array, Charset.forName(charsetName)).toCharArray())
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e.message, e)
        }

    }

    /**
     * Converts a String or an array of character bytes representing hexadecimal values into an array of bytes of those
     * same values. The returned array will be half the length of the passed String or array, as it takes two characters
     * to represent any given byte. An exception is thrown if the passed char array has an odd number of elements.
     *
     * @param object
     * A String or, an array of character bytes containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
     * @throws RuntimeException
     * Thrown if an odd number of characters is supplied to this function or the object is not a String or
     * char[]
     * @see .decodeHex
     */
    @Throws(RuntimeException::class)
    fun decode(`object`: Any): Any {
        try {
            val charArray = (`object` as? String)?.toCharArray() ?: `object` as CharArray
            return decodeHex(charArray)
        } catch (e: ClassCastException) {
            throw RuntimeException(e.message, e)
        }

    }

    /**
     * Converts an array of bytes into an array of bytes for the characters representing the hexadecimal values of each
     * byte in order. The returned array will be double the length of the passed array, as it takes two characters to
     * represent any given byte.
     *
     *
     * The conversion from hexadecimal characters to the returned bytes is performed with the charset named by
     * [.getCharsetName].
     *
     *
     * @param array
     * a byte[] to convert to Hex characters
     * @return A byte[] containing the bytes of the hexadecimal characters
     * @throws IllegalStateException
     * if the charsetName is invalid. This API throws [IllegalStateException] instead of
     * [UnsupportedEncodingException] for backward compatibility.
     * @see .encodeHex
     */
    fun encode(array: ByteArray): ByteArray? {
        return StringUtils.getBytesUnchecked(encodeHexString(array), charsetName)
    }

    /**
     * Converts a String or an array of bytes into an array of characters representing the hexadecimal values of each
     * byte in order. The returned array will be double the length of the passed String or array, as it takes two
     * characters to represent any given byte.
     *
     *
     * The conversion from hexadecimal characters to bytes to be encoded to performed with the charset named by
     * [.getCharsetName].
     *
     *
     * @param object
     * a String, or byte[] to convert to Hex characters
     * @return A char[] containing hexadecimal characters
     * @throws RuntimeException
     * Thrown if the given object is not a String or byte[]
     * @see .encodeHex
     */
    @Throws(RuntimeException::class)
    fun encode(`object`: Any): Any {
        try {
            val byteArray = (`object` as? String)?.toByteArray(charset(charsetName)) ?: `object` as ByteArray
            return encodeHex(byteArray)
        } catch (e: ClassCastException) {
            throw RuntimeException(e.message, e)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e.message, e)
        }

    }

    /**
     * Returns a string representation of the object, which includes the charset name.
     *
     * @return a string representation of the object.
     */
    override fun toString(): String {
        return super.toString() + "[charsetName=" + this.charsetName + "]"
    }

    companion object {

        val DEFAULT_CHARSET_NAME = CharEncoding.UTF_8
        /**
         * Used to build output as Hex
         */
        private val DIGITS_LOWER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

        /**
         * Used to build output as Hex
         */
        private val DIGITS_UPPER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

        /**
         * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
         * returned array will be half the length of the passed array, as it takes two characters to represent any given
         * byte. An exception is thrown if the passed char array has an odd number of elements.
         *
         * @param data
         * An array of characters containing hexadecimal digits
         * @return A byte array containing binary data decoded from the supplied char array.
         * @throws RuntimeException
         * Thrown if an odd number or illegal of characters is supplied
         */
        @Throws(RuntimeException::class)
        fun decodeHex(data: CharArray): ByteArray {

            val len = data.size

            if (len and 0x01 != 0) {
                throw RuntimeException("Odd number of characters.")
            }

            val out = ByteArray(len shr 1)

            // two characters form the hex value.
            var i = 0
            var j = 0
            while (j < len) {
                var f = toDigit(data[j], j) shl 4
                j++
                f = f or toDigit(data[j], j)
                j++
                out[i] = (f and 0xFF).toByte()
                i++
            }

            return out
        }

        /**
         * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
         * The returned array will be double the length of the passed array, as it takes two characters to represent any
         * given byte.
         *
         * @param data
         * a byte[] to convert to Hex characters
         * @param toLowerCase
         * `true converts to lowercase, false to uppercase
         * @return A char[] containing hexadecimal characters
         * @since 1.4
        ` */
        @JvmOverloads
        fun encodeHex(data: ByteArray, toLowerCase: Boolean = true): CharArray {
            return encodeHex(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
        }

        /**
         * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
         * The returned array will be double the length of the passed array, as it takes two characters to represent any
         * given byte.
         *
         * @param data
         * a byte[] to convert to Hex characters
         * @param toDigits
         * the output alphabet
         * @return A char[] containing hexadecimal characters
         * @since 1.4
         */
        protected fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
            val l = data.size
            val out = CharArray(l shl 1)
            // two characters form the hex value.
            var i = 0
            var j = 0
            while (i < l) {
                out[j++] = toDigits[(0xF0 and data[i].toInt()).ushr(4)]
                out[j++] = toDigits[0x0F and data[i].toInt()]
                i++
            }
            return out
        }

        /**
         * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
         * String will be double the length of the passed array, as it takes two characters to represent any given byte.
         *
         * @param data
         * a byte[] to convert to Hex characters
         * @return A String containing hexadecimal characters
         * @since 1.4
         */
        fun encodeHexString(data: ByteArray): String {
            return String(encodeHex(data))
        }

        /**
         * Converts a hexadecimal character to an number.
         *
         * @param ch
         * A character to convert to an number digit
         * @param index
         * The index of the character in the source
         * @return An number
         * @throws RuntimeException
         * Thrown if ch is an illegal hex character
         */
        @Throws(RuntimeException::class)
        protected fun toDigit(ch: Char, index: Int): Int {
            val digit = Character.digit(ch, 16)
            if (digit == -1) {
                throw RuntimeException("Illegal hexadecimal charcter $ch at index $index")
            }
            return digit
        }
    }
}

