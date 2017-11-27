package com.threshold.wepay

import android.app.Application
import com.threshold.logger.PrettyLogger
import com.threshold.logger.adapter.AndroidLogAdapter
import com.threshold.logger.addAdapter
import com.threshold.logger.strategy.PrettyFormatStrategy


class App: Application(),PrettyLogger {

    override fun onCreate() {
        super.onCreate()
        initPrettyLogger()
    }

    private fun initPrettyLogger() {
        val prettyFormatStrategy = PrettyFormatStrategy.build {
            tag = "WePay"
            showThreadInfo = true
        }
        addAdapter(object: AndroidLogAdapter(prettyFormatStrategy){
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG && super.isLoggable(priority, tag)
            }
        })
    }

}