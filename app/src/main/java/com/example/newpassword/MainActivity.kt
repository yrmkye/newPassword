package com.example.newpassword

import android.os.Bundle
import android.view.Window
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.newpassword.data.DataStoreManager
import com.example.newpassword.data.MyDataBass
import com.example.newpassword.page.BiometricScreen
import com.example.newpassword.page.NavHost
import com.example.newpassword.page.checkBiometricSupport
import com.example.newpassword.ui.theme.AppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        //初始化数据库查询
        val db = MyDataBass.getDatabase(this)
        val passwordDao = db.passwordInfoDao()

        super.onCreate(savedInstanceState)
        //隐藏顶部标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        dataStoreManager = DataStoreManager.getInstance(this)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier
                        .systemBarsPadding()
                        .fillMaxSize()
                ) {
                    var isBiometricEnabled by remember { mutableStateOf(false) }

                    // 如果开启了指纹识别
                    if (readString("isBiometricEnabled", "false") == "true") {
                        if (checkBiometricSupport(LocalContext.current)) {
                            BiometricScreen() {
                                isBiometricEnabled = it
                            }
                        }
                    } else isBiometricEnabled = true

                    if (isBiometricEnabled) NavHost(context = this,
                        passwordDao = passwordDao,
                        writeString = { key, value -> writeString(key, value) },
                        readString = { key, defaultValue -> readString(key, defaultValue) })

                }
            }
        }


    }


    private fun writeString(key: String, value: String) = runBlocking {
        dataStoreManager.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    private fun readString(key: String, defaultValue: String): String = runBlocking {
        dataStoreManager.dataStore.data.first()[stringPreferencesKey(key)] ?: defaultValue
    }

    private fun removeString(key: String) = runBlocking {
        dataStoreManager.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }
}

