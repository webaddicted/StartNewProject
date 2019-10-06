package com.webaddicted.kotlinproject.view.activity

import android.os.Handler
import android.view.View
import androidx.databinding.ViewDataBinding
import com.webaddicted.kotlinproject.R
import com.webaddicted.kotlinproject.databinding.ActivitySplashBinding
import com.webaddicted.kotlinproject.global.constant.AppConstant
import com.webaddicted.kotlinproject.view.base.BaseActivity

/**
 * Created by Deepak Sharma on 01/07/19.
 */
class SplashActivity : BaseActivity() {
    public val TAG: String = SplashActivity::class.java.simpleName
    private lateinit var mBinding: ActivitySplashBinding
    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun isNetworkConnected(isInternetConnected: Boolean) {
        showInternetSnackbar(isInternetConnected, mBinding.txtNoInternet)
    }

    override fun initUI(binding: ViewDataBinding) {
        mBinding = binding as ActivitySplashBinding
        init()
    }

    private fun init() {
        navigateToNext()
    }

    /**
     * navigate to welcome activity after Splash timer Delay
     */
    private fun navigateToNext() {
        Handler().postDelayed({
            HomeActivity.newIntent(this)
            finish()
        }, AppConstant.SPLASH_DELAY)
    }
}