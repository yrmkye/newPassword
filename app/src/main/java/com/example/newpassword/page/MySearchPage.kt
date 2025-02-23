package com.example.newpassword.page

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.newpassword.data.PasswordInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

/**
 * 搜索页面
 */
@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController, passwordDao: PasswordInfoDao) {
    // 焦点请求器
    val focusRequester = remember { FocusRequester() }
    // 软键盘控制器
    val softKeyboard = LocalSoftwareKeyboardController.current
    // 延迟效果，自动请求焦点并显示软键盘
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        softKeyboard?.show()
    }
    // 记忆化文本值
    var textValue by remember { mutableStateOf("") }
    val listFlow = passwordDao.getFlowList(textValue).flowOn(Dispatchers.IO).distinctUntilChanged()
        .collectAsState(initial = emptyList())
    val list by remember { derivedStateOf { listFlow.value } }

    // 搜索页的列表

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // 搜索输入框
                    TextField(
                        modifier = Modifier.focusRequester(focusRequester),
                        value = textValue,
                        onValueChange = {
                            if (it.length <= 50 && it.all { char -> char.isLetterOrDigit() || char.isWhitespace() }) {
                                textValue = it
                            }
                        },
                        placeholder = { Text(text = "在密码列表中搜索") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            // 当输入框处于焦点时，底部指示器的颜色
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0f),
                            // 当输入框不处于焦点时，底部指示器的颜色
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { softKeyboard?.hide() })
                    )
                },
                navigationIcon = {
                    // 返回按钮
                    IconButton(onClick = {
                        softKeyboard?.hide()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "回到首页")
                    }
                }
            )
        },
        content = { innerPadding ->

            // 显示搜索结果数量
            Text(
                text = list.size.toString() + "个密码",
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
            )
            // 显示搜索结果列表
            AccountList(
                navController, list,
                Modifier
                    .padding(innerPadding)
                    .padding(top = 25.dp)
            )

        }
    )
}




