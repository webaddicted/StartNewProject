package com.webaddicted.kotlinproject.view.base

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.android.boxlty.global.common.MediaPickerUtils
import com.webaddicted.kotlinproject.R
import com.webaddicted.kotlinproject.global.common.FileUtils
import com.webaddicted.kotlinproject.global.common.GlobalUtility
import com.webaddicted.kotlinproject.global.common.PermissionHelper
import com.webaddicted.kotlinproject.global.db.dao.UserInfoDao
import com.webaddicted.kotlinproject.global.sharedpref.PreferenceMgr
import org.koin.android.ext.android.inject
import java.io.File

/**
 * Created by Deepak Sharma on 15/1/19.
 */
abstract class BaseFragment : Fragment(), View.OnClickListener , PermissionHelper.Companion.PermissionListener,
    MediaPickerUtils.ImagePickerListener {
    private lateinit var mBinding: ViewDataBinding
    protected val mediaPicker: MediaPickerUtils by inject()
    protected val preferenceMgr: PreferenceMgr  by inject()
    abstract fun getLayout(): Int
    protected abstract fun onViewsInitialized(binding: ViewDataBinding?, view: View)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onViewsInitialized(mBinding, view)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        activity?.let { GlobalUtility.hideKeyboard(it) }
    }

    protected fun navigateFragment(layoutContainer: Int, fragment: Fragment, isEnableBackStack: Boolean) {
        if (getActivity() != null) {
            (getActivity() as BaseActivity).navigateFragment(layoutContainer, fragment, isEnableBackStack)
        }
    }

    protected fun navigateAddFragment(layoutContainer: Int, fragment: Fragment, isEnableBackStack: Boolean) {
        if (getActivity() != null) {
            (getActivity() as BaseActivity).navigateAddFragment(layoutContainer, fragment, isEnableBackStack)
        }
    }

    protected fun navigateChildFragment(layoutContainer: Int, fragment: Fragment, isEnableBackStack: Boolean) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(layoutContainer, fragment)
        if (isEnableBackStack)
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
        fragmentTransaction.commitAllowingStateLoss()
    }

    override fun onClick(v: View) {
//        GlobalUtility.Companion.btnClickAnimation(v)
    }

    fun checkStoragePermission() {
        val multiplePermission = ArrayList<String>()
        multiplePermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        multiplePermission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        multiplePermission.add(Manifest.permission.CAMERA)
        if (PermissionHelper.checkMultiplePermission(activity!!, multiplePermission)) {
            FileUtils.createApplicationFolder()
            onPermissionGranted(multiplePermission)
        } else
            PermissionHelper.requestMultiplePermission(activity!!, multiplePermission, this)
    }
    override fun onPermissionGranted(mCustomPermission: List<String>) {
        FileUtils.createApplicationFolder()
    }


    override fun onPermissionDenied(mCustomPermission: List<String>) {
    }

    override fun imagePath(filePath: List<File>) {
    }

    fun checkLocationPermission() {
        (getActivity() as BaseActivity).checkLocationPermission()
    }

    protected fun getUserDao():UserInfoDao {
        return (getActivity() as BaseActivity).getDbInstance().userInfoDao()
    }

    fun getPlaceHolder(imageLoaderPos: Int): String {
        val imageLoader = getResources().getStringArray(R.array.image_loader)
        return imageLoader[imageLoaderPos]
    }
}
