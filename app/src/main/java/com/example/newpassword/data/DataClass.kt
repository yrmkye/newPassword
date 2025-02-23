package com.example.newpassword.data

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 密码信息实体类
 */
@Entity(tableName = "password_info_table")
data class PasswordItemInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo
    var appName: String = "",
    @ColumnInfo
    var account: String = "",
    @ColumnInfo
    var password: String = "",
    @ColumnInfo
    var remark: String = "",
    @ColumnInfo
    var type: String = "",
    @ColumnInfo
    var packageName: String = "",
    @ColumnInfo
    var iconPath: String = ""

)

data class AccountNumberInfo(
    var appName: String,
    var number: Int,
)

data class AppInfo(
    var appName: String = "",
    var icon: Drawable,
    var packageName: String = "",
    var iconPath: String = ""
)

