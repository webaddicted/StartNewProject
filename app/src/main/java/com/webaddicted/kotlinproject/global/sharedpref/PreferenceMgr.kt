package com.webaddicted.kotlinproject.global.sharedpref

import com.webaddicted.kotlinproject.global.constant.PreferenceConstant
import com.webaddicted.kotlinproject.model.bean.preference.PreferenceBean

/**
 * Created by Deepak Sharma on 01/07/19.
 */
class PreferenceMgr constructor(var preferenceUtils: PreferenceUtils) {
    /**
     * set user session info
     */
    fun setUserInfo(preferenceBean: PreferenceBean) {
        preferenceUtils?.setPreference(PreferenceConstant.PREF_USER_NAME, preferenceBean.name)
        preferenceUtils?.setPreference(PreferenceConstant.PREF_USER_AGE, preferenceBean.age)
        preferenceUtils?.setPreference(PreferenceConstant.PREF_USER_GENDER, preferenceBean.gender)
        preferenceUtils?.setPreference(PreferenceConstant.PREF_USER_WEIGHT, preferenceBean.weight)
        preferenceUtils?.setPreference(PreferenceConstant.PREF_USER_IS_MARRIED, preferenceBean.isMarried)
    }

    /**
     * get user session info
     */
    fun getUserInfo(): PreferenceBean {
        val preferenceBean = PreferenceBean()
        preferenceBean.name = preferenceUtils.getPreference(PreferenceConstant.PREF_USER_NAME, "")!!
        preferenceBean.age = preferenceUtils?.getPreference(PreferenceConstant.PREF_USER_AGE, 0)!!
        preferenceBean.weight = preferenceUtils?.getPreference(PreferenceConstant.PREF_USER_WEIGHT, 0L)!!
        preferenceBean.isMarried = preferenceUtils?.getPreference(PreferenceConstant.PREF_USER_IS_MARRIED, false)!!
        return preferenceBean
    }

}