package com.example.newpassword.page


import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.newpassword.R
import com.example.newpassword.data.AppInfo
import com.example.newpassword.data.MyTextField
import com.example.newpassword.data.PasswordInfoDao
import com.example.newpassword.data.PasswordItemInfo
import com.example.newpassword.data.PasswordStrengthCheckerTextField
import com.example.newpassword.data.RouteConfig
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 修改页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPage(
    navController: NavController,
    passwordDao: PasswordInfoDao,
    id: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val show = remember { mutableStateOf(false) }
    var passwordItem by remember { mutableStateOf(PasswordItemInfo()) }
    var appList by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    LaunchedEffect(id) {
        if (id >= 0) {
            withContext(Dispatchers.IO) {
                passwordItem = passwordDao.getDecryptedById(id)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = passwordItem.appName) },
                navigationIcon = {

                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "回到首页"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showDialog = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baselin_shuffle_24),
                            contentDescription = "随机密码"
                        )
                    }
                    IconButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val massage = when {
                                passwordItem.appName.isEmpty() -> "网站或应用不能为空"
                                passwordItem.password.isEmpty() -> "密码不能为空"
                                else -> null
                            }
                            if (passwordItem.id == 0) { // 插入
//                                passwordDao.insertPasswordItemInfo(passwordItem)
                                passwordDao.insertEncryptedPasswordItemInfo(passwordItem)
                            } else { // 更改
//                                passwordDao.updatePasswordItemInfo(passwordItem)
                                passwordDao.updateEncryptedPasswordItemInfo(passwordItem)
                            }
                        }
                        navController.navigate(RouteConfig.ROUTE_HOMW_PAGE) { popUpTo(RouteConfig.ROUTE_EDIT_PAGE) }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_save_24),
                            contentDescription = "保存"
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "网站或应用", style = MaterialTheme.typography.titleMedium)
                    MyTextField(value = passwordItem.appName, label = "网站或应用") {
                        passwordItem = passwordItem.copy(appName = it)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                show.value = true
                                if (appList.isEmpty()) {
                                    isLoading = true
                                    scope.launch {
                                        val apps =
                                            withContext(Dispatchers.IO) { getInstalledApps(context) }
                                        appList = apps
                                        isLoading = false
                                    }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_apps_24),
                            contentDescription = "选择应用",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "选择应用")
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "请确保您保存的密码是正确的",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    MyTextField(value = passwordItem.account, label = "账号") {
                        passwordItem = passwordItem.copy(account = it)
                    }
                    PasswordStrengthCheckerTextField(value = passwordItem.password) {
                        passwordItem = passwordItem.copy(password = it)
                    }
                    MyTextField(value = passwordItem.remark, label = "备注") {
                        passwordItem = passwordItem.copy(remark = it)
                    }
                }
            }

        }
    )
    when {
        show.value && !isLoading -> {
            AlertDialog(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = { show.value = false },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        onClick = { show.value = !show.value }
                    ) {
                        Text("关闭", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                text = {
                    AppList(
                        onItem = {
                            show.value = !show.value
                            passwordItem = passwordItem.copy(appName = it.appName)
                            Log.i("YRMKYE", "选择: $it")
                        },
                        appList = appList
                    )
                }
            )
        }

        isLoading -> {
            AlertDialog(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = { },
                confirmButton = { },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "正在加载应用列表...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )
        }

        showDialog -> {
            PasswordGeneratorDialog(
                onDismissRequest = { showDialog = false },
                onPasswordGenerated = { password ->
                    passwordItem = passwordItem.copy(password = password)
                    showDialog = false
                }
            )
        }

    }
}


@Composable
fun AppList(onItem: (AppInfo) -> Unit, appList: List<AppInfo>) {
    val modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clip(RoundedCornerShape(10.dp))

    LazyColumn {
        items(appList) { appInfo ->
            ListItem(
                modifier = modifier.clickable { onItem(appInfo) },
                headlineContent = {
                    Text(
                        text = appInfo.appName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                leadingContent = {
                    Image(
                        painter = rememberDrawablePainter(drawable = appInfo.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            )
        }
    }
}


fun getInstalledApps(context: Context): List<AppInfo> {
    val packageManager: PackageManager = context.packageManager
    val flags = PackageManager.GET_META_DATA
    // 检查权限
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        if (context.checkSelfPermission("android.permission.QUERY_ALL_PACKAGES") != PackageManager.PERMISSION_GRANTED) {
            // 提示用户请求权限
            Toast.makeText(context, "缺少 QUERY_ALL_PACKAGES 权限，请授予", Toast.LENGTH_SHORT)
                .show()
            return emptyList()
        }
    }
    val packages: List<PackageInfo> = packageManager.getInstalledPackages(flags)

    return packages.filter { packageInfo ->
        packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null
    }.mapNotNull { packageInfo ->
        packageInfo.applicationInfo?.let { applicationInfo ->
            val appName = packageManager.getApplicationLabel(applicationInfo).toString()
            val icon = packageManager.getApplicationIcon(applicationInfo)
            AppInfo(
                packageName = packageInfo.packageName,
                appName = appName,
                icon = icon
            )
        }
    }
}

