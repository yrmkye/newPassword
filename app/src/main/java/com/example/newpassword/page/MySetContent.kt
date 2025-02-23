package com.example.newpassword.page

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.newpassword.R
import com.example.newpassword.data.PasswordInfoDao
import com.example.newpassword.data.PasswordItemInfo
import com.example.newpassword.data.RouteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun MySetContentPage(
    innerPadding: PaddingValues,
    context: Context,
    passwordDao: PasswordInfoDao,
    navController: NavController,
    writeString: (String, String) -> Unit,
    readString: (String, String) -> String
) {
    var isChecked by remember {
        mutableStateOf(
            readString(
                "isBiometricEnabled",
                "false"
            )
        )
    }
    // 创建导入文件的launcher
    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) {
                // 显示错误提示，告知用户未选择文件
                Toast.makeText(context, "请选择一个文件", Toast.LENGTH_LONG).show()
            } else {
                if (uri.toString().endsWith(".csv", ignoreCase = true)) {
                    importFromCsv(context, uri, passwordDao) {
                        Toast.makeText(
                            context,
                            "导入成功",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // 显示错误提示，告知用户选择的文件不是CSV文件
                    Log.d("yrmkye", "MySetContentPage: " + uri.toString())
                    Toast.makeText(context, "请选择CSV文件", Toast.LENGTH_LONG).show()
                }
            }
        }
    // 创建导出文件的launcher
    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
            uri?.let {
                exportToCsv(context, it, passwordDao) {
                    Toast.makeText(context, "导出成功", Toast.LENGTH_LONG).show()
                }
            }
        }
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {
        item {
            Text(
                text = "数据管理",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        item {
            SettingItem(
                title = "导出数据",
                description = "将数据导出为CSV",
                onClick = {
                    exportLauncher.launch(
                        "${
                            SimpleDateFormat(
                                "yyyyMMdd_HHmmss",
                                Locale.getDefault()
                            ).format(Date())
                        }_passwords.csv"
                    )
                },
                icon = R.drawable.outline_file_upload_24
            )
        }
        item {
            SettingItem(
                title = "导入",
                description = "从CSV文件导入数据",
                onClick = { importLauncher.launch("*/*") },
                icon = R.drawable.outline_file_download_24
            )
        }
        item {
            Text(
                text = "安全设置",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        item {
            SettingItem(
                title = "设置生物校验信息",
                description = "设置或更改生物校验信息",
                onClick = {
                    val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                    context.startActivity(intent)
                },
                icon = R.drawable.outline_lock_24
            )
        }
        item {
            SwitchSettingItem(
                title = "生物校验",
                description = "启用或禁用生物校验",
                isChecked = isChecked.toBoolean(),
                onCheckedChange = {
                    if (it) {
                        if (checkBiometricSupport(context)) {
                            writeString("isBiometricEnabled", "true")
                            isChecked = "true"
                            Toast.makeText(
                                context,
                                "生物校验已打开,重启后生效",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "您的设备不支持生物校验",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        writeString("isBiometricEnabled", "false")
                        isChecked = "false"
                        Toast.makeText(
                            context,
                            "生物校验已关闭,重启后生效",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
        item {
            SettingItem(
                title = "关于",
                description = "简介与联系方式",
                onClick = {
                    navController.navigate(RouteConfig.ROUTE_ABOUT_PAGE)
                },
                icon = R.drawable.outline_info_24
            )
        }
    }


}


/**
 * 从CSV文件导入密码信息
 * @param context 上下文，用于访问文件系统
 * @param uri 文件的URI
 * @param passwordInfoDao 密码信息的数据访问对象，用于插入数据
 */
@SuppressLint("Recycle")
fun importFromCsv(
    context: Context,
    uri: Uri,
    passwordInfoDao: PasswordInfoDao,
    onCompletion: () -> Unit
) {
    val fileSize = context.contentResolver.openFileDescriptor(uri, "r")?.statSize ?: -1
    if (fileSize > 2 * 1024 * 1024) {
        Toast.makeText(context, "文件大小超过2MB", Toast.LENGTH_LONG).show()
        onCompletion()
        return
    }
    context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
        ParcelFileDescriptor.AutoCloseInputStream(pfd).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                val header = reader.readLine()
                if (header != "appName,account,password,remark,type,packageName,iconPath") {
                    Toast.makeText(context, "文件格式不正确", Toast.LENGTH_LONG).show()
                    onCompletion()
                    return
                }
                reader.forEachLine { line ->
                    val columns = line.split(",")
                    Log.d("YRMKYE", "importFromCsv: $columns")
                    if (columns.size == 7) {
                        val passwordItemInfo = PasswordItemInfo(
                            appName = columns[0],
                            account = columns[1],
                            password = columns[2],
                            remark = columns[3],
                            type = columns[4],
                            packageName = columns[5],
                            iconPath = columns[6]
                        )

                        // 使用协程调用挂起函数
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                passwordInfoDao.insertEncryptedPasswordItemInfo(passwordItemInfo)
                                Log.i("YRMKYE", "导入: $passwordItemInfo")
                            } catch (e: Exception) {
                                Log.e("YRMKYE", "导入失败: $passwordItemInfo", e)
                            }
                        }
                    }
                }
            }
        }
    }
    onCompletion()
}

/**
 * 导出密码信息到CSV文件
 * @param context 上下文，用于访问文件系统
 * @param uri 文件的URI
 * @param passwordInfoDao 密码信息的数据访问对象，用于查询数据
 */
fun exportToCsv(
    context: Context,
    uri: Uri,
    passwordInfoDao: PasswordInfoDao,
    key: String = "",
    onCompletion: () -> Unit,
) {
    CoroutineScope(Dispatchers.IO).launch {
        //获取密码列表
        val passwordList = passwordInfoDao.getAll()
        context.contentResolver.openFileDescriptor(uri, "w")?.use { pfd ->
            ParcelFileDescriptor.AutoCloseOutputStream(pfd).use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write("appName,account,password,remark,type,packageName,iconPath\n")
                    passwordList.forEach { passwordItemInfo ->
                        writer.write(
                            "${passwordItemInfo.appName},${passwordItemInfo.account},${passwordItemInfo.password}," +
                                    "${passwordItemInfo.remark},${passwordItemInfo.type},${passwordItemInfo.packageName}," +
                                    "${passwordItemInfo.iconPath}\n"
                        )
                    }
                }
            }
        }
    }
    onCompletion()
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    onClick: () -> Unit,
    icon: Int
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        leadingContent = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "导航",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        headlineContent = {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
        },
        supportingContent = {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
    )
}


@Composable
fun SwitchSettingItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth(),
        headlineContent = {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
        },
        supportingContent = {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        trailingContent = {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.54f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            )
        }
    )

}