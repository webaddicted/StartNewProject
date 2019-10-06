package com.webaddicted.kotlinproject.global.common

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.webaddicted.kotlinproject.R
import com.webaddicted.kotlinproject.global.common.AppApplication.Companion.context
import java.lang.reflect.Modifier
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Deepak Sharma on 01/07/19.
 */
class GlobalUtility {

    companion object {

        fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun getDate(context: Context, mDobEtm: TextView) {
            val datePickerDialog = DatePickerDialog(context, R.style.TimePicker, { view, year, month, dayOfMonth ->
                mDobEtm.text =
                    "$dayOfMonth/$month/$year"
            }, Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)
            datePickerDialog.show()
        }

        fun timePicker(activity: Activity,timeListener: TimePickerDialog.OnTimeSetListener): TimePickerDialog {
            val calendar = Calendar.getInstance()
            return TimePickerDialog(
                activity,
                R.style.TimePicker,
                timeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(context)
            )
        }

        /**
         * convert date formate
         *
         * @param date         date any formate string
         * @param inputFormat  input date formate
         * @param outputFormat output date formate
         * @return output date formate
         */
        fun dateFormate(date: String, inputFormat: String, outputFormat: String): String {
            var initDate: Date? = null
            try {
                initDate = SimpleDateFormat(inputFormat).parse(date)
            } catch (e: java.text.ParseException) {
                e.printStackTrace()
            }

            val formatter = SimpleDateFormat(outputFormat)
            return formatter.format(initDate)
        }

        /**
         * convertTime formate
         *
         * @param timeHHMM
         * @return
         */
        fun timeFormat12(timeHHMM: String): String {
            try {
                val sdf = SimpleDateFormat("H:mm")
                val dateObj = sdf.parse(timeHHMM)

                return SimpleDateFormat("K:mm: a").format(dateObj)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return ""
        }

        /**
         * convertTime formate
         *
         * @param timeHHMM
         * @return
         */
        fun timeFormat24(timeHHMM: String): String {
            val sdf = SimpleDateFormat("hh:mm a")
            var testDate: Date? = null
            try {
                testDate = sdf.parse(timeHHMM)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            val formatter = SimpleDateFormat("HH:MM")
            return formatter.format(testDate)
        }
//    {START HIDE SHOW KEYBOARD}

        /**
         * Method to hide keyboard
         *
         * @param activity Context of the calling class
         */
        fun hideKeyboard(activity: Activity) {
            try {
                val inputManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
            } catch (ignored: Exception) {
                Log.d("TAG", "hideKeyboard: " + ignored.message)
            }

        }

        /***
         * Show SoftInput Keyboard
         * @param activity reference of current activity
         */
        fun showKeyboard(activity: Activity?) {
            if (activity != null) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        }

//    {END HIDE SHOW KEYBOARD}


//      {START STRING TO JSON & JSON TO STRING}

        /**
         * @param json  json String converted by Gson to string
         * @param clazz referance of class type like MyBean.class
         * @param <T>
         * @return bean referance
        </T> */
        fun <T> stringToJson(json: String, clazz: Class<T>): T {
            return GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .create().fromJson(json, clazz)
        }

        /**
         * @param clazz referance of any bean
         * @return
         */
        fun jsonToString(clazz: Class<*>): String {
            return Gson().toJson(clazz)
        }

        //{END STRING TO JSON & JSON TO STRING}


        //block up when loder show on screen
        /**
         * handle ui
         *
         * @param activity
         * @param view
         * @param isBlockUi
         */
        fun handleUI(activity: Activity, view: View, isBlockUi: Boolean) {
            if (isBlockUi) {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                view.visibility = View.VISIBLE
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                view.visibility = View.GONE
            }
        }

        /**
         * provide binding of layout
         *
         * @param context reference of activity
         * @param layout  layout
         * @return viewBinding
         */
        fun getLayoutBinding(context: Context, layout: Int): ViewDataBinding {
            return DataBindingUtil.inflate(
                LayoutInflater.from(context),
                layout,
                null, false
            )
        }

        /**
         * @param sizeOfRandomString length of random string
         * @return generate a random string
         */
        fun getRandomString(sizeOfRandomString: Int): String {
            val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"
            val random = Random()
            val sb = StringBuilder(sizeOfRandomString)
            for (i in 0 until sizeOfRandomString)
                sb.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)])
            return sb.toString()
        }


        /**
         * two digit random number
         *
         * @return random number
         */
        fun getTwoDigitRandomNo(): Int {
            return Random().nextInt(90) + 10
        }


        /**
         * show string in different color using spannable
         *
         * @param textView     view
         * @param txtSpannable string text
         * @param starText     start index of text
         * @param endText      end index of text
         */
        fun setSpannable(textView: TextView, txtSpannable: String, starText: Int, endText: Int) {
            val spannableString = SpannableString(txtSpannable)
            val foregroundSpan = ForegroundColorSpan(Color.GREEN)
            //            BackgroundColorSpan backgroundSpan = new BackgroundColorSpan(Color.GRAY);
            spannableString.setSpan(foregroundSpan, starText, endText, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            //            spannableString.setSpan(backgroundSpan, starText, endText, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.text = spannableString
        }

        /**
         * button click fade animation
         *
         * @param view view reference
         */
        fun btnClickAnimation(view: View) {
            val fadeAnimation = AnimationUtils.loadAnimation(view.context, R.anim.fade_in)
            view.startAnimation(fadeAnimation)
        }
    }


}