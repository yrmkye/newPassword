package com.example.newpassword.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * DataStoreManager 类用于管理应用的 DataStore 实例
 * 它提供了一种单例模式来确保在整个应用中只有一个 DataStore 实例被创建和使用
 *
 * @param context 上下文，用于访问 DataStore 和其他资源
 */
class DataStoreManager(private val context: Context) {

    companion object {
        // 在 Context 上扩展 dataStore 属性，使用 preferencesDataStore 创建名为 "settings" 的 DataStore 实例
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        /**
         * getInstance 方法用于获取 DataStoreManager 的单例实例
         * 它使用了双重检查锁定机制来确保线程安全和性能
         *
         * @param context 上下文，用于创建 DataStoreManager 实例
         * @return DataStoreManager 的单例实例
         */
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataStoreManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    /**
     * dataStore 属性提供对 Context.dataStore 的访问
     * 它允许外部访问 DataStore 实例，以便进行数据存储和检索操作
     */
    val dataStore: DataStore<Preferences>
        get() = context.dataStore
}
