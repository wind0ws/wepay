package com.threshold.pay.util

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.support.annotation.StringRes

/**
 * Builder mode for create [ProgressDialog]
 * example usage :
 * <pre>
 * `ProgressDialog progressDialog = new ProgressDialogBuilder(getContext())
 * .setTitle("下载")
 * .setMessage("下载中，请稍后。。。")
 * .setIndeterminate(false)
 * .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
 * .setProgress(0)
 * .setSecondaryProgress(0)
 * .setMax(100)
 * .setCancelable(true)
 * .setCanceledOnTouchOutside(true)
 * .setOnCancelListener(dialog -> {
 *      Logger.d("Cancel");
 * })
 * .setOnDismissListener(dialog -> {
 *      Logger.d("Dismissed");
 * })
 * .build();
 *
 * progressDialog.show();
 *
 * //increase progress percentage
 * new Thread(() -> {
 * while (progressDialog.getProgress() <= progressDialog.getMax()) {
 * if (!progressDialog.isShowing()) { //remember check whether progress dialog is active
 *      Logger.w("ProgressDialog已经不再可用");
 *      return;
 * }
 * try {
 *      Thread.sleep(2500); //mock time-consuming operation
 * } catch (InterruptedException e) {
 *      e.printStackTrace();
 * }
 * getActivity().runOnUiThread(()->{
 * progressDialog.incrementSecondaryProgressBy(10);
 * progressDialog.incrementProgressBy(8);
 * if (progressDialog.getProgress() == progressDialog.getMax()) {
 *      progressDialog.dismiss();
 * }
 * });
 * }
 * }).start();`
 * </pre>
 *
 */

class ProgressDialogBuilder(context: Context ,theme: Int = 0) {

    companion object {
        fun build(context: Context ,theme: Int = 0, block:ProgressDialogBuilder.() -> Unit):ProgressDialog{
            return ProgressDialogBuilder(context, theme).apply(block).build()
        }
    }

    private var progressDialog: ProgressDialog = ProgressDialog(context,theme)

    var title:CharSequence? = null
    @StringRes
    var titleResId:Int? = null
    var message:CharSequence? = null
    @StringRes
    var messageResId:Int? =null
    var indeterminate:Boolean? = null
    var max:Int? = null
    var progress:Int? = null
    var secondaryProgress:Int?= null
    var progressStyle:Int? = null
    var cancelable:Boolean? = null
    var canceledOnTouchOutside:Boolean? =null
    var onDismissListener: DialogInterface.OnDismissListener? = null
    var onCancelListener: DialogInterface.OnCancelListener? = null

    private fun switchToDeterminateMode() {
        if (progressDialog.isIndeterminate) {
            progressDialog.isIndeterminate = false
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)//SPINNER doesn't show any progress,just show spinner
        }
    }

    fun build(): ProgressDialog {
        with(progressDialog){
            title?.let { setTitle(it) }
            titleResId?.let { setTitle(it) }
            message?.let { setMessage(it) }
            messageResId?.let { setMessage(context.getString(it)) }
            indeterminate?.let { isIndeterminate = it }
            this@ProgressDialogBuilder.max?.let {
                max = it
                switchToDeterminateMode()
            }
            this@ProgressDialogBuilder.progress?.let {
                progress = it
                switchToDeterminateMode()
            }
            this@ProgressDialogBuilder.secondaryProgress?.let {
                secondaryProgress = it
                switchToDeterminateMode()
            }
            progressStyle?.let { setProgressStyle(it) }
            cancelable?.let { setCancelable(it) }
            canceledOnTouchOutside?.let { setCanceledOnTouchOutside(it) }
            onDismissListener?.let { setOnDismissListener(it) }
            onCancelListener?.let { setOnCancelListener(it) }
        }
        return progressDialog
    }

}