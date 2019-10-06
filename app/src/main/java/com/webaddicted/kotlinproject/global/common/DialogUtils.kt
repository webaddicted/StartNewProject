package com.webaddicted.kotlinproject.global.common

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.android.boxlty.view.interfaces.AlertDialogListener
import com.webaddicted.kotlinproject.R


class DialogUtil {
    companion object {
        private val TAG = DialogUtil::class.java.simpleName
//    {START SHOW DIALOG STYLE}
//    apply on resume method

        /**
         * show dialog with transprant background
         *
         * @param activity reference of activity
         * @param dialog   reference of dialog
         */
        fun modifyDialogBounds(activity: Activity, dialog: Dialog) {
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        activity,
                        android.R.color.transparent
                    )
                )
            )
            dialog.window!!.decorView.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val lp = WindowManager.LayoutParams()
            val window = dialog.window
            lp.copyFrom(window!!.attributes)
            //This makes the dialog take up the full width
            //lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.width = (dialog.context.resources.displayMetrics.widthPixels * 0.83).toInt()
            //  lp.height = (int) (dialog.getContext().getResources().getDisplayMetrics().heightPixels * 0.55);
            window.attributes = lp
        }

        fun alertFunction(
            context: Context?,
            title: String,
            messgae: String,
            btnOk: String,
            btnCancel: String,
            alertDialogListener: AlertDialogListener
        ): AlertDialog {
            var dialogAnimation = R.style.DialogFadeAnimation
            val builder = AlertDialog.Builder(context, R.style.AlertDialogStyle)
            builder.setCancelable(false)
            builder.setTitle(title)
            builder.setMessage(messgae)
            builder.setPositiveButton(btnOk) { dialog, which -> alertDialogListener.okClick() }
            builder.setNegativeButton(btnCancel) { dialog, which -> alertDialogListener.cancelClick() }
            val dialogs = builder.create()
            dialogs.getWindow()!!.getAttributes().windowAnimations = dialogAnimation
            dialogs.show()
            return dialogs
        }
    }
}
