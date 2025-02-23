package com.example.newpassword.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlin.random.Random

@Composable
fun PasswordGeneratorDialog(
    onDismissRequest: () -> Unit,
    onPasswordGenerated: (String) -> Unit
) {
    var includeLowerCase by remember { mutableStateOf(true) }
    var includeUpperCase by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSpecialChars by remember { mutableStateOf(true) }
    var passwordLength by remember { mutableIntStateOf(12) }
    var generatedPassword by remember { mutableStateOf(TextFieldValue("")) }
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "随机密码生成器", style = MaterialTheme.typography.headlineMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "长度")
                    BasicTextField(
                        value = passwordLength.toString(),
                        onValueChange = {
                            passwordLength = it.toIntOrNull() ?: 12
                        },
                        modifier = Modifier.width(50.dp),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "小写字母")
                    Checkbox(
                        checked = includeLowerCase,
                        onCheckedChange = { includeLowerCase = it }
                    )

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "大写字母")
                    Checkbox(
                        checked = includeUpperCase,
                        onCheckedChange = { includeUpperCase = it }
                    )

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "数字")
                    Checkbox(
                        checked = includeNumbers,
                        onCheckedChange = { includeNumbers = it }
                    )

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "特殊字符")
                    Checkbox(
                        checked = includeSpecialChars,
                        onCheckedChange = { includeSpecialChars = it }
                    )

                }

                Button(onClick = {
                    generatedPassword = TextFieldValue(
                        generatePassword(
                            passwordLength,
                            includeLowerCase,
                            includeUpperCase,
                            includeNumbers,
                            includeSpecialChars
                        )
                    )
                }) {
                    Text(text = "生成密码")
                }

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    value = generatedPassword,
                    onValueChange = { generatedPassword = it },
                    readOnly = true,
                    label = { Text("生成的密码",style = MaterialTheme.typography.labelLarge) },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        onDismissRequest()
                    }) {
                        Text(text = "取消")
                    }

                    Button(onClick = {
                        onPasswordGenerated(generatedPassword.text)
                    }) {
                        Text(text = "保存")
                    }
                }
            }
        }
    }
}

fun generatePassword(
    length: Int,
    includeLowerCase: Boolean,
    includeUpperCase: Boolean,
    includeNumbers: Boolean,
    includeSpecialChars: Boolean
): String {
    val lowerCase = "abcdefghijklmnopqrstuvwxyz"
    val upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val numbers = "0123456789"
    val specialChars = "!@#$%^&*()-_=+[]{}|;:,.<>?/"

    val charPool = buildString {
        if (includeLowerCase) append(lowerCase)
        if (includeUpperCase) append(upperCase)
        if (includeNumbers) append(numbers)
        if (includeSpecialChars) append(specialChars)
    }

    return (1..length).map { Random.nextInt(charPool.length) }.map(charPool::get)
        .joinToString("")
}
