package com.threshold.pay.util


object CharEncoding {
    /**
     * CharEncodingISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1.
     *
     *
     * Every implementation of the Java platform is required to support this character encoding.
     *
     *
     * @see [Standard charsets](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html) */
    val ISO_8859_1 = "ISO-8859-1"

    /**
     *
     *
     * Seven-bit ASCII, also known as ISO646-US, also known as the Basic Latin block of the Unicode character set.
     *
     *
     *
     * Every implementation of the Java platform is required to support this character encoding.
     *
     *
     * @see [Standard charsets](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html)
     **/
    val US_ASCII = "US-ASCII"

    /**
     *
     *
     * Sixteen-bit Unicode Transformation Format, The byte order specified by a mandatory initial byte-order mark
     * (either order accepted on input, big-endian used on output)
     *
     *
     *
     * Every implementation of the Java platform is required to support this character encoding.
     *
     *
     * @see [Standard charsets](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html)
     * */
    val UTF_16 = "UTF-16"

    /**
     *
     *
     * Sixteen-bit Unicode Transformation Format, big-endian byte order.
     *
     *
     *
     * Every implementation of the Java platform is required to support this character encoding.
     *
     *
     * @see [Standard charsets](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html)
     * */
    val UTF_16BE = "UTF-16BE"

    /**
     *
     *
     * Sixteen-bit Unicode Transformation Format, little-endian byte order.
     *
     *
     *
     * Every implementation of the Java platform is required to support this character encoding.
     *
     *
     * @see [Standard charsets](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html)
     * */
    val UTF_16LE = "UTF-16LE"

    /**
     *
     *
     * Eight-bit Unicode Transformation Format.
     *
     *
     *
     * Every implementation of the Java platform is required to support this character encoding.
     *
     *
     * @see [Standard charsets](http://java.sun.com/j2se/1.4.2/docs/api/java/nio/charset/Charset.html)
     * */
    val UTF_8 = "UTF-8"
}
