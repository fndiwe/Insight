package com.franklinndiwe.insight.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.data.AdaptiveNavBarScreenData
import com.franklinndiwe.insight.data.MenuItem

@Composable
fun HomeTab(
    navItems: List<AdaptiveNavBarScreenData>,
    onClick: (MenuItem) -> Unit,
    currentScreen: MenuItem,
) {
    navItems.forEach { data ->
        val item = data.menuItem
        val selected = currentScreen.id == item.id
        val style =
            if (!selected) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            )
        Tab(
            modifier = Modifier.padding(bottom = 10.dp),
            selected = selected,
            onClick = { onClick(item) },
            selectedContentColor = MaterialTheme.colorScheme.primary,
            unselectedContentColor = MaterialTheme.colorScheme.onSurface
        ) {
            (if (selected) item.filledIcon else item.outlinedIcon)?.let {
                Icon(
                    imageVector = it, contentDescription = stringResource(item.name)
                )
            }
            Text(
                text = stringResource(item.name), style = style
            )
        }
    }
}

@Composable
fun TopTabBar(
    modifier: Modifier = Modifier,
    navItems: List<AdaptiveNavBarScreenData>,
    onClick: (MenuItem) -> Unit,
    currentScreen: MenuItem,
) {
    @Composable
    fun Indicator(tabPositions: List<TabPosition>) = Spacer(
        Modifier
            .tabIndicatorOffset(tabPositions[currentScreen.id])
            .requiredWidth(45.dp)
            .requiredHeight(5.dp)
            .background(
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
            )
    )
    if (navItems.size > 4) {
        ScrollableTabRow(modifier = modifier, selectedTabIndex = currentScreen.id, indicator = {
            Indicator(tabPositions = it)
        }) {
            HomeTab(navItems = navItems, onClick = onClick, currentScreen = currentScreen)
        }
    } else {
        TabRow(modifier = modifier, selectedTabIndex = currentScreen.id, indicator = {
            Indicator(tabPositions = it)
        }) {
            HomeTab(navItems = navItems, onClick = onClick, currentScreen = currentScreen)
        }
    }
}

@Composable
fun SlidingTabs(
    modifier: Modifier = Modifier,
    scrollEnabled: Boolean = false,
    screens: List<AdaptiveNavBarScreenData>,
    onNavigateBack: () -> Unit,
) {
    val state = rememberPagerState {
        screens.size
    }

    var currentMenuItem by remember {
        mutableStateOf(screens[state.currentPage].menuItem)
    }
    LaunchedEffect(key1 = currentMenuItem) {
        state.scrollToPage(
            currentMenuItem.id
        )
    }

    Column(modifier) {
        TopTabBar(
            Modifier.padding(horizontal = 16.dp), navItems = screens, onClick = {
                currentMenuItem = it
            }, currentScreen = currentMenuItem
        )
        HorizontalPager(
            state = state, userScrollEnabled = scrollEnabled
        ) {
            screens[it].component()
            BackHandler(true) {
                if (it > 0) {
                    currentMenuItem = screens[0].menuItem
                } else {
                    onNavigateBack()
                }
            }
        }
    }
}