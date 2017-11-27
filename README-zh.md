# WePay

[![](https://jitpack.io/v/wind0ws/wepay.svg)](https://jitpack.io/#wind0ws/wepay)

[English](https://github.com/wind0ws/wepay/blob/master/README.md)|中文


# 特点

* 同时支持微信支付和支付宝支付。
* 支持监听支付状态以及支付结果。
* 简单配置，容易使用


## [开始集成](https://jitpack.io/#wind0ws/wepay)
由于使用了 [jitpack.io](https://jitpack.io/), 所以我们需要将 jitpack.io 加入到你项目根 gradle 文件中:

```groovy
allprojects {
 repositories {
    jcenter()
    //...some other repo.
    maven { url "https://jitpack.io" }
 }
}
```
接着我们将wepay依赖放入到你的应用模块的 gradle 文件中：

```groovy
    implementation "com.github.wind0ws:wepay:1.0.0"
```

> 如果你的gradle版本低于3.0, 你只需要将 ```implementation``` 关键词换成 ```compile```即可

好了，集成完毕了。

## [Hello world app](https://github.com/wind0ws/wepay/blob/master/app/src/main/java/com/threshold/wepay).

在使用这个库之前，你应该去微信支付官网和支付宝官网申请应用ID，应用密钥等信息。 有了这些信息你才能正常使用这个库。
关于怎么申请我就不在赘述了，两大官网都有详细的申请流程，有兴趣的可以参考我写的[申请文档](http://www.jianshu.com/p/22a6397df055)

支付流程：
1. 创建你的[订单对象信息](https://github.com/wind0ws/wepay/blob/master/pay/src/main/java/com/threshold/pay/order/OrderInfo.kt).
2. 创建你对应的支付配置对象: AlipayConfig 和 WechatPayConfig.
3. 让你的Activity或者Fragment实现[PayStatusListener](https://github.com/wind0ws/wepay/blob/master/pay/src/main/java/com/threshold/pay/PaymentConfig.kt) 来监听支付过程和结果。
4. 调用 [PayEntryActivity](https://github.com/wind0ws/wepay/blob/master/pay/src/main/java/com/threshold/pay/PayEntryActivity.kt) 并传入支配置和订单信息来调起对应的支付。

>完整demo请看本项目的app模块.

* ### [Alipay](https://github.com/wind0ws/wepay/blob/master/app/src/main/java/com/threshold/wepay/AlipayFragment.kt)
  ```Kotlin
    PayEntryActivity.payStatusListener = this
    val payConfig = AliPayConfig(alipayAppId, rsaPrivateKey,false)
    val orderInfo = OrderInfo("admin",OrderUtils.createOutTradeNo(),123,"OrderTitle-admin-2017.10.11",
                "Order detail", Date())
    PayEntryActivity.startAliPay(context,orderInfo,payConfig)
  ```

* ### [Wechat](https://github.com/wind0ws/wepay/blob/master/app/src/main/java/com/threshold/wepay/WechatPayFragment.kt)
  > 请注意：
  > 由于微信支付SDK限制app支付入口Activity的名字，所以为了将wepay库里的PayEntryActivity作为微信支付的入口，你需要在你的应用的manifest.xml中配置activity-alias,就像下面这这样:
  ```groovy
          <activity-alias
            android:name=".wxapi.WXPayEntryActivity"
            android:exported="true"
            android:targetActivity="com.threshold.pay.PayEntryActivity"/>
  ```
 接着我们再写微信支付代码：
  ```Kotlin
    PayEntryActivity.payStatusListener = this
    val payConfig = WechatPayConfig("appid","appSecret","corpId","merchantId",
            "http://www.notifyUrl")
    val orderInfo = OrderInfo("admin", OrderUtils.createOutTradeNo(),123,"OrderTitle-admin-2017.10.11",
                "Order detail", Date())
    PayEntryActivity.startWeChatPay(context,orderInfo,payConfig)
  ```


## Proguard
详情请参考wepay库里的 [proguard-rules.pro](https://github.com/wind0ws/wepay/blob/master/pay/proguard-rules.pro) 文件.

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
