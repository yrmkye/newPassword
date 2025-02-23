package com.example.newpassword.data

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.newpassword.R

@Composable
fun MyTextField(value: String, label: String, valueChange: (String) -> Unit) {
    var value1 by remember { mutableStateOf(value) }
    // 确保当外部 value 发生变化时，value1 也会同步更新
    DisposableEffect(value) {
        value1 = value
        onDispose {}
    }
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        value = value1,
        onValueChange = {
            value1 = it
            valueChange(it)
        },
        singleLine = true,
        label = { Text(text = label, style = MaterialTheme.typography.labelLarge) },
        trailingIcon = {
            if (value1.isNotEmpty())
                IconButton(onClick = {
                    value1 = ""
                    valueChange("")
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "清空$label")
                }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun PasswordStrengthCheckerTextField(
    value: String,
    valueChange: (String) -> Unit
) {
    var value1 by remember { mutableStateOf(value) }
    var strengthMessage by remember { mutableStateOf("") }
    var strengthLevel by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    // 确保当外部 value 发生变化时，value1 也会同步更新
    DisposableEffect(value) {
        value1 = value
        if (value1.isNotEmpty()) {
            val (message, level) = evaluatePasswordStrength(value1)
            strengthMessage = message
            strengthLevel = level
        }
        onDispose {}
    }
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            value = value1,
            onValueChange = {
                value1 = it
                val (message, level) = evaluatePasswordStrength(it)
                strengthMessage = message
                strengthLevel = level
                valueChange(it)
            },
            label = { Text("密码") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (value1.isNotEmpty())
                        IconButton(onClick = {
                            value1 = ""
                            valueChange("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "清空密码")
                        }
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = if (passwordVisible) R.drawable.outline_visibility_off_24 else R.drawable.outline_visibility_24),
                            contentDescription = if (passwordVisible) "隐藏密码" else "显示密码"
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "密码强度: $strengthLevel", color = MaterialTheme.colorScheme.primary)
        Text(text = strengthMessage, color = MaterialTheme.colorScheme.secondary)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

fun evaluatePasswordStrength(password: String): Pair<String, String> {
    return when {
        password.length < 8 -> Pair("密码太短，建议至少8个字符", "弱")
        !password.any { it.isDigit() } -> Pair("密码应包含数字", "中")
        !password.any { it.isLetter() } -> Pair("密码应包含字母", "中")
        !password.any { !it.isLetterOrDigit() } -> Pair("密码应包含特殊字符", "中")
        password.length >= 12 && password.any { it.isDigit() } && password.any { it.isLetter() } && password.any { !it.isLetterOrDigit() } -> Pair(
            "密码强度良好",
            "强"
        )

        else -> Pair("密码强度一般", "中")
    }
}