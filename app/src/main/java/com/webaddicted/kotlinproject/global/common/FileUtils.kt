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
        private val APP_FOLDER = "kotlinProhect"
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
        /**
         * Method to save bitmap
         *
         * @param bmp
         * @return
         */
        fun savebitmap(bmp: Bitmap): File? {
            val extStorageDirectory = Environment.getExternalStorageDirectory().toString() + "/criiio/profile"
            var outStream: OutputStream? = null

            // String temp = null;
            val file = File(extStorageDirectory, System.currentTimeMillis().toString() + "_img.png")

            try {
                outStream = FileOutputStream(file)
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                outStream.flush()
                outStream.close()

            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

            return file
        }
        /**
         * Helper method for saving the image.
         *
         * @param context The application context.
         * @param image   The image to be saved.
         * @return The path of the saved image.
         */
        internal fun saveImage(context: Context, image: Bitmap): File {
            var imageFile: File? = null
            var savedImagePath: String? = null
            val imageFileName = "Img" + System.currentTimeMillis() + ".jpg"
            imageFile = File(subFolder(), imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            updateGallery(context, savedImagePath)
            return imageFile
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