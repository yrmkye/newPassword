package com.example.newpassword.page

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.newpassword.R
import com.example.newpassword.data.PasswordInfoDao
import com.example.newpassword.data.PasswordItemInfo
import com.example.newpassword.data.RouteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 详情页
 */
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsPage(
    passwordDao: PasswordInfoDao,
    navController: NavController,
    name: String,
) {
    var title by remember { mutableStateOf("") }
    var passList: List<PasswordItemInfo> by remember { mutableStateOf(emptyList()) }
//    val listState = passwordDao.getNumByName(name).collectAsState(initial = emptyList())
//    val passList by remember { derivedStateOf { listState.value } }

    CoroutineScope(Dispatchers.IO).launch {
        passList = passwordDao.getDecryptedByName(name)
    }

    var deleteInfo by remember { mutableStateOf(PasswordItemInfo()) }
    val show = remember { mutableStateOf(false) }

    if (passList.isNotEmpty()) {
        title = passList[0].appName
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "回到首页"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            itemsIndexed(passList) { _, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = RectangleShape
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(text = title, style = MaterialTheme.typography.titleMedium)
                        MyTextField(
                            value = item.account, label = "账户",
                            Modifier
                                .padding(bottom = 2.dp, top = 8.dp)
                                .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))

                        )
                        MyTextField(
                            value = item.password, label = "密码",
                            Modifier
                                .padding(top = 2.dp, bottom = 4.dp)
                                .clip(
                                    RoundedCornerShape(
                                        bottomEnd = 16.dp,
                                        bottomStart = 16.dp
                                    )
                                ),
                            true
                        )
                        MyTextField(
                            value = item.remark, label = "备注",
                            Modifier
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            FilledTonalButton(onClick = {
                                navController.navigate(RouteConfig.ROUTE_EDIT_PAGE_ID + item.id)
                            }) {
                                Row {
                                    Icon(Icons.Default.Edit, contentDescription = "修改信息")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "修改")
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledTonalButton(
                                onClick = {
                                    show.value = !show.value
                                    deleteInfo = item

                                }) {
                                Row {
                                    Icon(Icons.Default.Clear, contentDescription = "删除信息")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "删除")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
    if (show.value) {
        AlertDialog(
            onDismissRequest = { show.value = false },
            confirmButton = {
                Button(onClick = {
                    show.value = false
                    if (passList.size == 1) {
                        navController.popBackStack()
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        passwordDao.delete(deleteInfo)
                        passList -= deleteInfo
                    }
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                Button(onClick = { show.value = false }) {
                    Text("取消")
                }
            },
            text = {
                Text(text = "是否删除这条信息")
            },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Icon(Icons.Default.Clear, contentDescription = "警告")
            }
        )
    }
}


@Composable
private fun MyTextField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    status: Boolean = false
) {
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    val clipboardManager =
        LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    TextField(
        modifier = modifier
            .fillMaxWidth(),
        value = value,
        onValueChange = {},
        singleLine = true,
        label = { Text(text = label) },
        readOnly = true,
        visualTransformation = if (status == passwordVisible ) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (status)
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = if (passwordVisible) R.drawable.outline_visibility_off_24 else R.drawable.outline_visibility_24),
                            contentDescription = if (passwordVisible) "隐藏密码" else "显示密码"
                        )
                    }
                IconButton(onClick = {
                    val clip = ClipData.newPlainText(label, value)
                    clipboardManager.setPrimaryClip(clip)
                    Toast.makeText(context, "已复制 $label 到剪贴板", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_content_copy_24),
                        contentDescription = "复制$label",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent, // 底部指示器颜色
            unfocusedIndicatorColor = Color.Transparent, // 底部指示器颜色
        ),
        shape = RectangleShape
    )
}
