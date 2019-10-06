package com.webaddicted.kotlinproject.global.common

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.WindowManager
import android.webkit.MimeTypeMap
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Deepak Sharma on 01/07/19.
 */
class FileUtils {
    /**
     * Method to get size of file in kb
     *
     * @param file
     * @return
     */
    fun getFileSizeInKb(file: File): Long {
        return file.length() / 1024
    }

    companion object {
        private val APP_FOLDER = "StartProject"
        private val SUB_PROFILE = "/profile"
        private val SEPARATOR = "/"
        private val JPEG = ".jpeg"
        private val PNG = ".png"

        /**
         * This method is used to create application specific folder on filesystem
         */
        fun createApplicationFolder() {
            var f = File(Environment.getExternalStorageDirectory().toString(), File.separator + APP_FOLDER)
            f.mkdirs()
            f = File(Environment.getExternalStorageDirectory().toString(), File.separator + APP_FOLDER + SUB_PROFILE)
            f.mkdirs()
        }

        /**
         * Method to return file object
         *
         * @return File object
         */
        fun appFolder(): File {
            return File(Environment.getExternalStorageDirectory().toString(), File.separator + APP_FOLDER)
        }

        /**
         * Method to return file from sub folder
         *
         * @return File object
         */
        fun subFolder(): File {
            return File(Environment.getExternalStorageDirectory().toString(), File.separator + APP_FOLDER + SUB_PROFILE)
        }
        fun saveImage(image: Bitmap?, folderPath: File?): File {
            var savedImagePath: String? = null
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val imageFileName = "JPEG_$timeStamp.jpg"
            val imageFile = File(folderPath, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut = FileOutputStream(imageFile)
                image?.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return imageFile
        }
        /**
         * Method to update phone gallery after capturing file
         *
         * @param context
         * @param imagePath
         */
        fun updateGallery(context: Context, imagePath: String?) {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(imagePath)
            val contentUri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        }
    }

    //    {END CAPTURE IMAGE PROCESS}
}