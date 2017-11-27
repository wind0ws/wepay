package com.threshold.pay.util

/**
 * XmlUtil
 * Created by Threshold on 2017/9/30.
 */
object  XmlUtil {
    fun escaped(xmlContent: String): String = xmlContent.replace("&","&amp;")
            .replace("<","&lt;").replace(">","&gt;")
}