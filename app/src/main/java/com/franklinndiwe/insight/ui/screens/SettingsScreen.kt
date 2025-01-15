package com.franklinndiwe.insight.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AppDropdownMenuItem
import com.franklinndiwe.insight.data.QuoteImageCategory
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.ui.components.AppDropdownMenu
import com.franklinndiwe.insight.ui.components.AppLoadingIndicator
import com.franklinndiwe.insight.ui.components.AppTopAppBar
import com.franklinndiwe.insight.utils.AppUtils.listOfContrast
import com.franklinndiwe.insight.utils.AppUtils.listOfFonts
import com.franklinndiwe.insight.utils.AppUtils.shadowElevation
import com.franklinndiwe.insight.utils.AppUtils.tonalElevation
import com.franklinndiwe.insight.utils.FollowScreenOption
import com.franklinndiwe.insight.utils.Theme
import com.franklinndiwe.insight.utils.ThemeType
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.SettingsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class
)
@Composable
fun SettingsScreen(
    context: Context,
    setting: Setting,
    navigateToFollowScreen: (FollowScreenOption) -> Unit,
    navigateBack: unit,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val postNotificationsPermissionsState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else ""
    ) {
        // If the user does not accept the request to post notifications turn off daily quote reminder and cancel schedule or vise versa
        settingsViewModel.updateDailyQuoteReminder(context, setting, it)
    }
    var showTimePicker by rememberSaveable {
        mutableStateOf(false)
    }
    var expandThemeDropdownMenu by remember {
        mutableStateOf(false)
    }
    var expandThemeTypeDropdownMenu by remember {
        mutableStateOf(false)
    }
    var expandFontDropdownMenu by remember {
        mutableStateOf(false)
    }
    var expandContrastDropdownMenu by remember {
        mutableStateOf(false)
    }
    val quoteImageCategoryUtils = settingsViewModel.quoteImageCategoryRepositoryUtils
    val imageCategories by quoteImageCategoryUtils.quoteImageCategories.collectAsState(initial = emptyList())
    val themes = listOf(
        Pair(R.string.system_default, ThemeType.SystemDefault),
        Pair(R.string.light, ThemeType.LightTheme),
        Pair(R.string.dark, ThemeType.DarkTheme)
    )
    val theme = themes.find { it.second == setting.themeType }!!
    var showImageCategoriesDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun updateSetting(setting: Setting) = settingsViewModel.settingUtils.updateSetting(setting)

    val timePickerState = rememberTimePickerState(
        initialHour = setting.reminderTimeHour, initialMinute = setting.reminderTimeMinute
    )
    Scaffold(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        AppTopAppBar(
            title = stringResource(R.string.settings),
            onNavigateBack = navigateBack,
            scrollBehavior = scrollBehavior
        )
    }, snackbarHost = {
        SnackbarHost(snackbarHostState) {
            Snackbar(snackbarData = it)
        }
    }) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(16.dp)
            ) {
                //General settings
                item {
                    SettingsColumn {
                        SectionDivider(title = R.string.general)
                        //Dynamic Color Switch
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) SettingRow {
                            SettingLeftText(text = stringResource(R.string.dynamic_color))
                            Switch(setting.dynamicColor,
                                { updateSetting(setting.copy(dynamicColor = it)) })
                        }
                        //Theme selection row
                        SettingRow {
                            SettingLeftText(text = stringResource(R.string.theme))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SettingRightText(text = stringResource(id = theme.first))
                                SettingDropdown(expandThemeDropdownMenu, themes.map {
                                    AppDropdownMenuItem(it.first) {
                                        updateSetting(setting.copy(themeType = it.second))
                                    }
                                }, onExpand = {
                                    expandThemeDropdownMenu = !expandThemeDropdownMenu
                                }, onDismissRequest = { expandThemeDropdownMenu = false })
                            }
                        }
                        //Fonts dropdown menu
                        SettingRow {
                            SettingLeftText(text = stringResource(R.string.font))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SettingRightText(
                                    text = stringResource(
                                        id = try {
                                            listOfFonts[setting.fontIndex]
                                        } catch (_: IndexOutOfBoundsException) {
                                            listOfFonts[0]
                                        }.first
                                    )
                                )
                                SettingDropdown(expandFontDropdownMenu, listOfFonts.map {
                                    AppDropdownMenuItem(it.first, fontFamily = it.second) {
                                        updateSetting(
                                            setting.copy(
                                                fontIndex = listOfFonts.indexOf(
                                                    it
                                                )
                                            )
                                        )
                                    }
                                }, onExpand = {
                                    expandFontDropdownMenu = !expandFontDropdownMenu
                                }, onDismissRequest = { expandFontDropdownMenu = false })
                            }
                        }
                        //Contrast dropdown menu
                        if (!setting.dynamicColor) SettingRow {
                            SettingLeftText(text = stringResource(R.string.contrast))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SettingRightText(text = listOfContrast[setting.contrastIndex].first.name)
                                SettingDropdown(expandContrastDropdownMenu, listOfContrast.map {
                                    AppDropdownMenuItem(it.first.text) {
                                        updateSetting(
                                            setting.copy(
                                                contrastIndex = listOfContrast.indexOf(
                                                    it
                                                )
                                            )
                                        )
                                    }
                                }, onExpand = {
                                    expandContrastDropdownMenu = !expandContrastDropdownMenu
                                }, onDismissRequest = { expandContrastDropdownMenu = false })
                            }
                        }
                    }
                }
                //Notification settings
                item {
                    SettingsColumn {
                        SectionDivider(title = R.string.notification)
                        //Daily quote notification switch
                        SettingRow {
                            SettingLeftText(
                                text = "${stringResource(id = R.string.daily_quote2)} ${
                                    stringResource(
                                        id = R.string.notification
                                    )
                                }"
                            )
                            Switch(setting.dailyQuoteReminderEnabled, {
                                scope.launch {
                                    settingsViewModel.runDailyQuoteReminderCheck(
                                        context,
                                        it,
                                        setting,
                                        postNotificationsPermissionsState,
                                        snackbarHostState
                                    ).await()
                                }
                            })
                        }
                        //Notification time
                        SettingRow {
                            SettingLeftText(
                                text = "${stringResource(id = R.string.notification)} ${
                                    stringResource(id = R.string.time)
                                }"
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val hour = setting.reminderTimeHour
                                val min = setting.reminderTimeMinute
                                SettingRightText(
                                    text = "${
                                        if (timePickerState.is24hour) hour else if (hour == 12 || hour == 0) 12 else hour.mod(
                                            12
                                        )
                                    }:${if (min < 10) "0" else ""}$min${
                                        when {
                                            timePickerState.is24hour -> ""
                                            else -> if (hour >= 12) " PM" else " AM"
                                        }
                                    }"
                                )
                                TextButton(onClick = { showTimePicker = true }) {
                                    Text(text = stringResource(R.string.change))
                                }
                            }
                        }
                    }
                }
                //Quotes settings
                item {
                    SettingsColumn {
                        SectionDivider(title = R.string.quotes)
                        //Daily quote categories
                        val categoryUtils = settingsViewModel.categoryUtils
                        val categories = remember {
                            categoryUtils.getDailyCategories()
                        }.collectAsLazyPagingItems()
                        SettingsHorizontalGrid(title = "${stringResource(R.string.daily_quote2)} ${
                            stringResource(
                                id = R.string.categories
                            )
                        }",
                            loading = categories.itemCount < 0 && categories.loadState.refresh == LoadState.Loading,
                            onClickIcon = { navigateToFollowScreen(FollowScreenOption.IS_DAILY_QUOTE_EDIT) }) {
                            items(categories.itemCount, categories.itemKey { it.id }) { i ->
                                val category = categories[i]
                                if (category != null) {
                                    SettingsHorizontalGridItem(
                                        text = category.name.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(
                                                Locale.ROOT
                                            ) else it.toString()
                                        },
                                        contentDescriptionForIcon = stringResource(id = R.string.remove_category)
                                    ) {
                                        if (categories.itemSnapshotList.items.size > 1) categoryUtils.updateCategory(
                                            category.copy(daily = false)
                                        )
                                    }
                                }
                            }
                        }
                        //ThemeType
                        SettingRow {
                            val list = Theme.entries
                            SettingLeftText(
                                text = stringResource(R.string.theme) + " " + stringResource(id = R.string.type)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SettingRightText(text = stringResource(id = setting.theme.text))
                                SettingDropdown(expandThemeTypeDropdownMenu, list.map {
                                    AppDropdownMenuItem(it.text) {
                                        updateSetting(
                                            setting.copy(theme = it)
                                        )
                                    }
                                }, onExpand = {
                                    expandThemeTypeDropdownMenu = !expandThemeTypeDropdownMenu
                                }, onDismissRequest = { expandThemeTypeDropdownMenu = false })
                            }
                        }/* Quote Image Categories | shows when Theme is Image */
                        if (setting.theme == Theme.Image) SettingsHorizontalGrid(title = stringResource(
                            R.string.image_categories
                        ),
                            icon = ImageVector.vectorResource(R.drawable.tune),
                            contentDescriptionForIcon = R.string.customize,
                            onClickIcon = { showImageCategoriesDialog = true }) {
                            if (imageCategories.none { it.included }) item(span = StaggeredGridItemSpan.FullLine) {
                                SettingRightText(text = stringResource(id = R.string.nothing_to_show))
                            } else items(imageCategories.filter { it.included }) { imageCategory ->
                                SettingsHorizontalGridItem(
                                    text = imageCategory.name,
                                    contentDescriptionForIcon = stringResource(id = R.string.remove_category)
                                ) {
                                    if (imageCategories.filter { it.included }.size > 1) quoteImageCategoryUtils.update(
                                        imageCategory.copy(included = false)
                                    )
                                }
                            }
                        }
                        //Auto Scroll Timer
                        SettingRow {
                            SettingLeftText(text = stringResource(R.string.autoscroll_timer_delay))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val delay = setting.delay
                                fun increase() = updateSetting(setting.copy(delay = delay + 1))
                                fun decrease() = updateSetting(setting.copy(delay = delay - 1))
                                IconButton(enabled = delay > 5, onClick = { decrease() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                                        contentDescription = stringResource(id = R.string.decrease_time)
                                    )
                                }
                                SettingRightText(text = "${setting.delay} secs")
                                IconButton(enabled = delay < 60, onClick = { increase() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                        contentDescription = stringResource(id = R.string.increase_time)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (showTimePicker) Surface(
                Modifier.padding(8.dp),
                shape = MaterialTheme.shapes.medium,
                shadowElevation = shadowElevation,
                tonalElevation = tonalElevation
            ) {
                Column(Modifier.padding(16.dp)) {
                    TimePicker(
                        state = timePickerState
                    )
                    Row(
                        Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        TextButton(onClick = {
                            val settingCopy = setting.copy(
                                reminderTimeHour = timePickerState.hour,
                                reminderTimeMinute = timePickerState.minute
                            )
                            updateSetting(settingCopy)
                            if (setting.dailyQuoteReminderEnabled) settingsViewModel.rescheduleNotification(
                                context, settingCopy
                            )
                            showTimePicker = false
                        }) {
                            Text(text = stringResource(R.string.confirm))
                        }
                    }
                }
            }
            if (showImageCategoriesDialog) ImageCategoriesSelector(imageCategories = imageCategories,
                updateCategory = {
                    quoteImageCategoryUtils.update(it)
                },
                onDismiss = {
                    showImageCategoriesDialog = false
                })
        }
    }
}

@Composable
private fun SectionDivider(title: Int) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = stringResource(title), style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(thickness = 2.dp)
    }
}

@Composable
fun SettingRow(
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically, content
    )
}

@Composable
private fun SettingsHorizontalGrid(
    title: String,
    icon: ImageVector = Icons.Rounded.Add,
    contentDescriptionForIcon: Int = R.string.add,
    loading: Boolean = false,
    onClickIcon: () -> Unit,
    content: LazyStaggeredGridScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SettingLeftText(text = title)
            IconButton(onClick = onClickIcon) {
                Icon(
                    imageVector = icon, contentDescription = stringResource(
                        id = contentDescriptionForIcon
                    )
                )
            }
        }
        if (loading) Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp), contentAlignment = Alignment.Center
        ) {
            AppLoadingIndicator()
        } else LazyHorizontalStaggeredGrid(
            rows = StaggeredGridCells.Fixed(2),
            Modifier
                .fillMaxWidth()
                .height(105.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalItemSpacing = 12.dp,
            content = content
        )
    }
}

@Composable
private fun SettingsHorizontalGridItem(
    text: String,
    icon: ImageVector = Icons.Rounded.Close,
    contentDescriptionForIcon: String,
    onClick: () -> Unit,
) {
    Surface(
        Modifier.wrapContentWidth(),
        shape = MaterialTheme.shapes.medium,
        shadowElevation = shadowElevation,
        contentColor = MaterialTheme.colorScheme.primary,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        val normalDp = 4.dp
        val startDp = 16.dp
        Row(
            Modifier.padding(top = normalDp, bottom = normalDp, start = startDp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text, style = MaterialTheme.typography.bodyMedium
            )
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = icon, contentDescription = contentDescriptionForIcon
                )
            }
        }
    }
}

@Composable
fun SettingsColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun SettingDropdown(
    expanded: Boolean,
    dropdownMenuItems: List<AppDropdownMenuItem>,
    onExpand: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Box {
        IconButton(onClick = onExpand) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(id = R.string.expand_dropdown)
            )
        }
        AppDropdownMenu(dropdownMenuItems, expanded, onDismissRequest)
    }
}

@Composable
fun SettingRightText(text: String, modifier: Modifier = Modifier, fontFamily: FontFamily? = null) {
    Text(
        text = text,
        modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontFamily = fontFamily
    )
}

@Composable
fun SettingLeftText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ImageCategoriesSelector(
    imageCategories: List<QuoteImageCategory>,
    updateCategory: (QuoteImageCategory) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = onDismiss) {
            Text(text = stringResource(R.string.okay))
        }
    }, Modifier.padding(vertical = 16.dp), title = {
        Text(text = "${stringResource(R.string.customize)} ${stringResource(id = R.string.image_categories)}")
    }, text = {
        @Composable
        fun StaggeredRow(content: LazyStaggeredGridScope.() -> Unit) {
            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.Fixed(3),
                Modifier.height(140.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalItemSpacing = 8.dp,
                content = content
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StaggeredRow {
                    val includedCategories = imageCategories.filter { it.included }
                    items(includedCategories) {
                        SettingsHorizontalGridItem(
                            text = it.name, contentDescriptionForIcon = stringResource(
                                id = R.string.exclude_category
                            )
                        ) {
                            if (includedCategories.size > 1) updateCategory(it.copy(included = false))
                        }
                    }

                }
            }
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(id = R.string.excluded_categories),
                    style = MaterialTheme.typography.titleMedium
                )
                val excludedCategories = imageCategories.filter { !it.included }
                if (excludedCategories.isEmpty()) SettingRightText(
                    text = stringResource(R.string.nothing_to_show),
                    Modifier.align(Alignment.CenterHorizontally)
                ) else StaggeredRow {
                    items(excludedCategories) {
                        SettingsHorizontalGridItem(
                            text = it.name,
                            Icons.Rounded.Add,
                            contentDescriptionForIcon = stringResource(
                                id = R.string.include_category
                            )
                        ) {
                            updateCategory(it.copy(included = true))
                        }
                    }
                }
            }
        }
    })
}
