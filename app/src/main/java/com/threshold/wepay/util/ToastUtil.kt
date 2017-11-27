package com.threshold.wepay.util

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.NonNull
import android.support.annotation.StringRes
import android.text.TextUtils
import android.widget.Toast

/**
 * Util for Toast
 *
 *
 * This is Share Toast,which means if two toast showing too closely ,it will just show last toast.
 * for example,if you write code like:
 * <pre>
 * `void showMessage(){
 * ToastUtil.showShort(context,"Hello");
 * ToastUtil.showShort(context,"World");
 * }
` *
</pre> *
 * It will only show "World",because these two Toast are too closely to show.

 */
object ToastUtil {

    private var sToast: Toast? = null

    fun showShort(ctx: Context, @StringRes resId: Int) {
        show(ctx, ctx.getString(resId), Toast.LENGTH_SHORT)
    }

    fun showShort(ctx: Context,@NonNull msg: CharSequence) {
        show(ctx, msg, Toast.LENGTH_SHORT)
    }

    fun showLong(ctx: Context,@NonNull msg: CharSequence) {
        show(ctx, msg, Toast.LENGTH_LONG)
    }

    fun showLong(ctx: Context, @StringRes resId: Int) {
        show(ctx, ctx.getString(resId), Toast.LENGTH_LONG)
    }

    fun show(ctx: Context, @NonNull msg: CharSequence, duration: Int) {
        if (TextUtils.isEmpty(msg)) {
            return
        }
        checkToast(ctx)
        sToast!!.setText(msg)
        sToast!!.duration = duration
        sToast!!.show()
    }

    fun show(ctx: Context, @StringRes resId: Int, duration: Int) {
        show(ctx, ctx.getString(resId), duration)
    }

    fun show(ctx: Context,@NonNull msg: CharSequence) {
        val duration = if (msg.length <= 6) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        show(ctx, msg, duration)
    }

    fun show(ctx: Context, @StringRes resId: Int) {
        show(ctx, ctx.getString(resId))
    }

    fun show(ctx: Context, @StringRes resId: Int, vararg formatArgs: Any) {
        show(ctx, ctx.getString(resId, *formatArgs))
    }

    @SuppressLint("ShowToast")
    private fun checkToast(ctx: Context) {
        if (sToast == null) {
            sToast = Toast.makeText(ctx.applicationContext, "", Toast.LENGTH_SHORT)
        }
    }
}