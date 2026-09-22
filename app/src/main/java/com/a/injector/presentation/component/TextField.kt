package com.a.injector.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    MaterialTheme(
        motionScheme = MotionScheme.standard()
    ) {
        TextField(
            modifier = modifier
                .fillMaxWidth(),
            label = if (label != null) {
                { Text(text = label) }
            } else null,
            placeholder = if (placeholder != null) {
                { Text(text = placeholder) }
            } else null,
            value = value,
            onValueChange = { onValueChange(it) },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation
        )
    }
}

@Composable
fun TransparentTextField(
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    MaterialTheme(
        motionScheme = MotionScheme.standard()
    ) {
        OutlinedTextField(
            modifier = modifier,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            label = if (label != null) {
                { Text(text = label) }
            } else null,
            placeholder = if (placeholder != null) {
                { Text(text = placeholder) }
            } else null,
            value = value,
            onValueChange = { onValueChange(it) },
            trailingIcon = trailingIcon
        )
    }
}