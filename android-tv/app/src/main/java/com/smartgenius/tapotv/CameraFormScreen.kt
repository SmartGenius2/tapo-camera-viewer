package com.smartgenius.tapotv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun CameraFormScreen(
    initial: Camera?,
    onSave: (Camera) -> Unit,
    onDelete: (Camera) -> Unit,
    onCancel: () -> Unit,
) {
    BackHandler(onBack = onCancel)

    var name by remember { mutableStateOf(initial?.name ?: "") }
    var host by remember { mutableStateOf(initial?.host ?: "") }
    var port by remember { mutableStateOf((initial?.port ?: 554).toString()) }
    var user by remember { mutableStateOf(initial?.user ?: "") }
    var password by remember { mutableStateOf(initial?.password ?: "") }
    var path by remember { mutableStateOf(initial?.path ?: "stream1") }
    var subPath by remember { mutableStateOf(initial?.subPath ?: "stream2") }

    val portNum = port.toIntOrNull()
    val valid = name.isNotBlank() && host.isNotBlank() && user.isNotBlank() &&
        password.isNotBlank() && path.isNotBlank() && subPath.isNotBlank() &&
        portNum != null && portNum in 1..65535

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 58.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            stringResource(if (initial == null) R.string.form_add_title else R.string.form_edit_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        Field(stringResource(R.string.field_name), name, { name = it })
        Field(stringResource(R.string.field_ip), host, { host = it.trim() }, KeyboardType.Uri)
        Field(stringResource(R.string.field_port), port, { port = it.filter(Char::isDigit) }, KeyboardType.Number)
        Field(stringResource(R.string.field_user), user, { user = it })
        Field(stringResource(R.string.field_password), password, { password = it }, secret = true)
        Field(stringResource(R.string.field_stream_full), path, { path = it.trim() })
        Field(stringResource(R.string.field_stream_grid), subPath, { subPath = it.trim() }, last = true)

        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TvButton(
                stringResource(R.string.save),
                enabled = valid,
                onClick = {
                    onSave(
                        Camera(
                            id = initial?.id ?: UUID.randomUUID().toString(),
                            name = name.trim(),
                            host = host,
                            port = portNum!!,
                            user = user,
                            password = password,
                            path = path,
                            subPath = subPath,
                        )
                    )
                },
            )
            TvButton(stringResource(R.string.cancel), onCancel, primary = false)
            if (initial != null) {
                TvButton(stringResource(R.string.delete), { onDelete(initial) }, primary = false)
            }
        }
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    type: KeyboardType = KeyboardType.Text,
    secret: Boolean = false,
    last: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (secret) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (secret) KeyboardType.Password else type,
            imeAction = if (last) ImeAction.Done else ImeAction.Next,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}
