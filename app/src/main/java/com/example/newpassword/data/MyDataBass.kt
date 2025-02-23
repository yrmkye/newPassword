package com.example.newpassword.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    version = 1,
    entities = [PasswordItemInfo::class],
    exportSchema = false
)
abstract class MyDataBass : RoomDatabase() {

    abstract fun passwordInfoDao(): PasswordInfoDao

    companion object {
        private var dataBass: MyDataBass? = null
        fun getDatabase(context: Context): MyDataBass {
            if (dataBass == null) {
                dataBass =
                    Room.databaseBuilder(context, MyDataBass::class.java, "password_db.db")
                        .build()
            }
            return dataBass as MyDataBass
        }
    }

}
