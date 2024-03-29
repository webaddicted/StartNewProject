package com.webaddicted.kotlinproject.global.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.webaddicted.kotlinproject.global.constant.DbConstant
import com.webaddicted.kotlinproject.global.db.dao.UserInfoDao
import com.webaddicted.kotlinproject.global.db.entity.UserInfoEntity

@Database(entities = arrayOf(UserInfoEntity::class), version = DbConstant.DB_VERSION)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userInfoDao(): UserInfoDao
}