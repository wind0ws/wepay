package com.threshold.pay.wechat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 *
<receiver
android:name=".wechat.AppRegisterBroadcastReceiver"
android:permission="com.tencent.mm.plugin.permission.SEND" >
<intent-filter>
<action android:name="com.tencent.mm.plugin.openapi.Intent.ACTION_REFRESH_WXAPP" />
</intent-filter>
</receiver>
 */
class AppRegisterBroadcastReceiver : BroadcastReceiver() {

    companion object {
        var APP_ID: String? = null
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        APP_ID?.let {
            val api: IWXAPI = WXAPIFactory.createWXAPI(context, null)
            // 将该app注册到微信
            api.registerApp(it)
        }
    }
}