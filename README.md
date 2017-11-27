# WePay

[![](https://jitpack.io/v/wind0ws/wepay.svg)](https://jitpack.io/#wind0ws/wepay)

English|[中文](https://github.com/wind0ws/wepay/blob/master/README-zh.md)


# Features

* Support wechat pay and alipay in one library.
* Easy to listen pay status and result.
* Easy to config and easy to use.


## [Getting started](https://jitpack.io/#wind0ws/wepay)
The first step is to include WePay into your project, for example, as a Gradle compile dependency:

Because of using [jitpack.io](https://jitpack.io/),so we need add the jitpack.io repository in your root project gradle:

```groovy
allprojects {
 repositories {
    jcenter()
    //...some other repo.
    maven { url "https://jitpack.io" }
 }
}
```
and then add wepay dependency in your module gradle:

```groovy
    implementation "com.github.wind0ws:wepay:1.0.0"
```

> for gradle version below 3.0, just replace keyword ```implementation``` to ```compile```

We are done for integration.

## Now we write the [hello world app](https://github.com/wind0ws/wepay/blob/master/app/src/main/java/com/threshold/wepay).

First and foremost: You should apply appId,appSecret for alipay and wechat pay. You can apply it on there official website.

The flow of pay is :
1. Create your [order object](https://github.com/wind0ws/wepay/blob/master/pay/src/main/java/com/threshold/pay/order/OrderInfo.kt).
2. Create your pay config: AlipayConfig and WechatPayConfig.
3. Let your activity or fragment implement [PayStatusListener](https://github.com/wind0ws/wepay/blob/master/pay/src/main/java/com/threshold/pay/PaymentConfig.kt) for listen pay status and result.
4. Start [PayEntryActivity](https://github.com/wind0ws/wepay/blob/master/pay/src/main/java/com/threshold/pay/PayEntryActivity.kt) for pay.

>The full of demo is in this project app module.

* ### [Alipay](https://github.com/wind0ws/wepay/blob/master/app/src/main/java/com/threshold/wepay/AlipayFragment.kt)
  ```Kotlin
    PayEntryActivity.payStatusListener = this
    val payConfig = AliPayConfig(alipayAppId, rsaPrivateKey,false)
    val orderInfo = OrderInfo("admin",OrderUtils.createOutTradeNo(),123,"OrderTitle-admin-2017.10.11",
                "Order detail", Date())
    PayEntryActivity.startAliPay(context,orderInfo,payConfig)
  ```

* ### [Wechat](https://github.com/wind0ws/wepay/blob/master/app/src/main/java/com/threshold/wepay/WechatPayFragment.kt)
  > Attention please:
  > because of wechat pay sdk restrict app pay entry activity name, so you should set this library PayEntryActivity alias to your manifest. such as:
  ```groovy
          <activity-alias
            android:name=".wxapi.WXPayEntryActivity"
            android:exported="true"
            android:targetActivity="com.threshold.pay.PayEntryActivity"/>
  ```
  Now we write code for wechat pay.
  ```Kotlin
    PayEntryActivity.payStatusListener = this
    val payConfig = WechatPayConfig("appid","appSecret","corpId","merchantId",
            "http://www.notifyUrl")
    val orderInfo = OrderInfo("admin", OrderUtils.createOutTradeNo(),123,"OrderTitle-admin-2017.10.11",
                "Order detail", Date())
    PayEntryActivity.startWeChatPay(context,orderInfo,payConfig)
  ```


## Proguard
See library [proguard-rules.pro](https://github.com/wind0ws/wepay/blob/master/pay/proguard-rules.pro) file.

## LICENSE

    Copyright (c) 2017-present, WePay Contributors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
