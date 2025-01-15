package com.franklinndiwe.insight.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.HomeItem
import com.franklinndiwe.insight.utils.AppUtils.defaultShape
import com.franklinndiwe.insight.utils.AppUtils.shadowElevation
import com.franklinndiwe.insight.utils.AppUtils.tonalElevation
import com.franklinndiwe.insight.utils.SortOrderType
import com.franklinndiwe.insight.utils.unit

@Composable
fun HomeCard(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    list: List<HomeItem>,
    onClickHomeItem: (String) -> Unit,
    onSearch: (SortOrderType) -> Unit,
    onCreateQuote: unit,
) {
    Surface(
        modifier,
        shape = defaultShape,
        shadowElevation = shadowElevation,
        tonalElevation = tonalElevation
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val text = stringResource(id = title)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text, style = MaterialTheme.typography.titleMedium
                )
                Row {
                    val quote = stringResource(id = R.string.quotes)
                    val category = stringResource(id = R.string.categories)
                    if (text == stringResource(id = R.string.quotes)) IconButton(onClick = onCreateQuote) {
                        Icon(
                            imageVector = Icons.Rounded.Add, contentDescription = stringResource(
                                id = R.string.add_quote
                            )
                        )
                    }
                    IconButton(onClick = {
                        onSearch(
                            when (text) {
                                quote -> SortOrderType.Quote
                                category -> SortOrderType.Category
                                else -> SortOrderType.Author
                            }
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Search, contentDescription = stringResource(
                                id = R.string.search
                            )
                        )
                    }
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(list) {
                    Column(modifier = Modifier
                        .clip(defaultShape)
                        .clickable { onClickHomeItem(it.route) }
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = it.icon),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )

                        Text(
                            text = stringResource(id = it.name),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

            }
        }

    }
}