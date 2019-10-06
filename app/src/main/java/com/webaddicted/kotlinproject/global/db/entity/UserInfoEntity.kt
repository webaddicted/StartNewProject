package com.webaddicted.kotlinproject.global.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.webaddicted.kotlinproject.global.constant.DbConstant

@Entity(tableName = DbConstant.USER_INFO_TABLE)
 class UserInfoEntity {
    @PrimaryKey(autoGenerate = true)
    public var id: Int = 0
   public var name: String? = null
   public  var nickname: String? = null
   public  var mobileno: String? = null
   public  var email: String? = null
   public  var password: String? = null
}