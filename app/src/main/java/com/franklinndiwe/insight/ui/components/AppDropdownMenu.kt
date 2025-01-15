package com.franklinndiwe.insight.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.franklinndiwe.insight.data.AppDropdownMenuItem

@Composable
fun AppDropdownMenu(
    dropdownMenuItems: List<AppDropdownMenuItem>,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded, onDismissRequest = onDismissRequest
    ) {
        dropdownMenuItems.forEach {
            DropdownMenuItem(text = {
                Text(
                    text = stringResource(id = it.text),
                    fontFamily = it.fontFamily
                )
            }, leadingIcon = {
                it.trailingComposable?.invoke()
                it.trailingIcon?.let { it1 ->
                    Icon(
                        imageVector = it1, contentDescription = null
                    )
                }
            }, trailingIcon = {
                it.leadingIcon?.let { it1 ->
                    Icon(
                        imageVector = it1, contentDescription = null
                    )
                }
            }, onClick = {
                it.onClick()
                onDismissRequest()
            })
        }
    }
}