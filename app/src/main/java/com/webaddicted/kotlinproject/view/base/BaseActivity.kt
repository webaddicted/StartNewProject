package com.webaddicted.kotlinproject.view.base

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.android.boxlty.global.common.*
import com.webaddicted.kotlinproject.R
import com.webaddicted.kotlinproject.global.common.*
import com.webaddicted.kotlinproject.global.constant.DbConstant
import com.webaddicted.kotlinproject.global.db.database.AppDatabase
import org.koin.android.ext.android.inject
import java.io.File



/**
 * Created by Deepak Sharma on 01/07/19.
 */
abstract class BaseActivity : AppCompatActivity(), View.OnClickListener, PermissionHelper.Companion.PermissionListener,
    MediaPickerUtils.ImagePickerListener {
    protected var appDb: AppDatabase? = null
    protected val mediaPicker: MediaPickerUtils by inject()
    companion object{
        val TAG = BaseActivity::class.java.simpleName
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        supportActionBar?.hide()
        setNavigationColor(resources.getColor(R.color.app_color))
       fullScreen();
        GlobalUtility.hideKeyboard(this)
        var layoutResId = getLayout()
        var binding: ViewDataBinding? = null
        if (layoutResId != 0) {
            try {
                binding = DataBindingUtil.setContentView(this, layoutResId)
                initUI(binding)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            if (window != null) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            }
        }
    }

    protected fun setNavigationColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           window?.setNavigationBarColor(color);
        }
    }

    open abstract fun getLayout(): Int

    open abstract fun initUI(binding: ViewDataBinding)
    /**
     * placeholder type for image
     *
     * @param placeholderType position of string array placeholder
     * @return
     */
    protected fun getPlaceHolder(placeholderType: Int): String {
        val placeholderArray = getResources().getStringArray(R.array.image_loader)
        return placeholderArray[placeholderType]
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out)
    }


    fun getDbInstance(): AppDatabase {
        if (appDb == null) {
            appDb = Room.databaseBuilder(this, AppDatabase::class.java, DbConstant.DB_NAME)
                .allowMainThreadQueries().build();
        }
        return appDb as AppDatabase
    }

    override fun onClick(v: View?) {}

    fun checkStoragePermission() {
        val multiplePermission = ArrayList<String>()
        multiplePermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        multiplePermission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        multiplePermission.add(Manifest.permission.CAMERA)
        if (PermissionHelper.checkMultiplePermission(this, multiplePermission)) {
            FileUtils.createApplicationFolder()
            onPermissionGranted(multiplePermission)
        } else
            PermissionHelper.requestMultiplePermission(this, multiplePermission, this)
    }


    fun checkLocationPermission() {
        val multiplePermission = ArrayList<String>()
        multiplePermission.add(Manifest.permission.ACCESS_FINE_LOCATION)
        multiplePermission.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (PermissionHelper.checkMultiplePermission(this, multiplePermission)) {

        } else PermissionHelper.requestMultiplePermission(this, multiplePermission, this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        PermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }


    override fun onPermissionGranted(mCustomPermission: List<String>) {
        FileUtils.createApplicationFolder()
    }

    override fun onPermissionDenied(mCustomPermission: List<String>) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == mediaPicker.REQUEST_CAMERA_VIDEO || requestCode == mediaPicker.REQUEST_SELECT_FILE_FROM_GALLERY) {
                data?.let { mediaPicker.onActivityResult(this, requestCode, resultCode, it) }
            }
        }
    }

    override fun imagePath(filePath: List<File>) {
    }

    fun navigateFragment(layoutContainer: Int, fragment: Fragment, isEnableBackStack: Boolean) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out, R.anim.trans_right_in, R.anim.trans_right_out)
        fragmentTransaction.replace(layoutContainer, fragment)
        if (isEnableBackStack)
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun navigateAddFragment(layoutContainer: Int, fragment: Fragment, isEnableBackStack: Boolean) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.setCustomAnimations(R.anim.trans_left_in, R.anim.trans_left_out, R.anim.trans_right_in, R.anim.trans_right_out)
        fragmentTransaction.add(layoutContainer, fragment)
        if (isEnableBackStack)
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
        fragmentTransaction.commitAllowingStateLoss()
    }
    /**
     * broadcast receiver for check internet connectivity
     *
     * @return
     */
    private fun getNetworkStateReceiver() {
        NetworkChangeReceiver.isInternetAvailable(object :
            NetworkChangeReceiver.ConnectivityReceiverListener {
            override fun onNetworkConnectionChanged(isConnected: Boolean) {
                try {
                    isNetworkConnected(isConnected)
                }catch (exception: Exception){
                    Lg.d(TAG, "getNetworkStateReceiver : "+exception.toString())
                }
            }
        })
    }

    open abstract fun isNetworkConnected(isConnected: Boolean)

    protected fun showInternetSnackbar(internetConnected: Boolean, txtNoInternet: TextView) {
        if (internetConnected) {

            txtNoInternet.setText(getString(R.string.back_online))
            val color = arrayOf<ColorDrawable>(ColorDrawable(resources.getColor(R.color.red_ff090b)),
                ColorDrawable(resources.getColor(R.color.green_00de4a)))
            val trans = TransitionDrawable(color)
            txtNoInternet.background = (trans)
            trans.startTransition(500)
            val handler = Handler()
            handler.postDelayed({ txtNoInternet.gone() }, 1300)
        } else {
            txtNoInternet.text = getString(R.string.no_internet_connection)
            txtNoInternet.setBackgroundResource(R.color.red_ff090b)
            txtNoInternet.visible()
        }
    }

}