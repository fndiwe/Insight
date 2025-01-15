package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.franklinndiwe.insight.R
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    title: String,
    isHome: Boolean = false,
    numberOfQuotes: Int? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigateBack: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(title = {
        if (numberOfQuotes != null) Column {
            Text(
                text = title, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pluralStringResource(
                    id = R.plurals.quote,
                    numberOfQuotes,
                    NumberFormat.getNumberInstance().format(numberOfQuotes)
                ), fontWeight = FontWeight.Normal, fontSize = 14.sp
            )
        } else Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = if (isHome) FontWeight.SemiBold else FontWeight.Normal
        )
    }, actions = actions, navigationIcon = {
        if (onNavigateBack != null) IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.go_back)
            )
        }
    }, scrollBehavior = scrollBehavior)
}