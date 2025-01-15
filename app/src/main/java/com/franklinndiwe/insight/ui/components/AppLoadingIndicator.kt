package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppLoadingIndicator(modifier: Modifier = Modifier) =
    CircularProgressIndicator(modifier.size(25.dp), color = MaterialTheme.colorScheme.onSurface)