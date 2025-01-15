package com.franklinndiwe.insight.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AdLoadingState
import com.franklinndiwe.insight.utils.AppUtils.CreateQuota
import com.franklinndiwe.insight.utils.AppUtils.DownloadQuoteQuota
import com.franklinndiwe.insight.utils.AppUtils.ImportQuota
import com.franklinndiwe.insight.utils.AppUtils.categoryQuota
import com.franklinndiwe.insight.utils.AppUtils.defaultShape
import com.franklinndiwe.insight.utils.unit
import java.text.NumberFormat

/**
 * A Reusable [Scaffold] for quota and purchase activities
 * @param adLoadingState State of the Ad to watch, one of these [AdLoadingState.Loading],
 * [AdLoadingState.Success], [AdLoadingState.Error] and [AdLoadingState.Idle].
 * @param watchAd Action that invokes both loading and watching of Ads.
 * @param showQuotaDialog Show a dialog in some certain situations, namely,
 * insufficient quota, quota info.
 * @param onDismissQuotaDialog Function that dismisses the Quota [AlertDialog]
 * especially by inverting [showQuotaDialog]
 * @param showPurchaseItemDialog Function that shows an [AlertDialog] to confirm a purchase
 * @param onDismissPurchaseItemDialog Function that dismisses the purchase item [AlertDialog]
 * especially by inverting [showPurchaseItemDialog]
 * @param onConfirmPurchase Function that verifies purchase and grants the user the item requested
 * @param isInsufficient If the Quota [AlertDialog] should show an insufficient quota info.
 */
@Composable
fun QuotaRenewScaffold(
    context: Context,
    adLoadingState: AdLoadingState,
    snackbarHostState: SnackbarHostState,
    nestedScrollConnection: NestedScrollConnection,
    watchAd: unit,
    showQuotaDialog: Boolean = false,
    onDismissQuotaDialog: unit,
    showPurchaseItemDialog: Boolean = false,
    onDismissPurchaseItemDialog: unit = {},
    onConfirmPurchase: unit = {},
    isInsufficient: Boolean = true,
    topBar: @Composable unit,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    numberOfQuotes: Int? = null,
    isFont: Boolean = false,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(key1 = adLoadingState) {
        if (adLoadingState == AdLoadingState.Error) {
            val result = snackbarHostState.showSnackbar(
                context.getString(R.string.load_ad_failed_message), actionLabel = context.getString(
                    R.string.retry
                ), duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) watchAd()
        } else if (adLoadingState is AdLoadingState.Success) Toast.makeText(
            context,
            context.getString(R.string.reward_message, adLoadingState.rewardItem.amount.toString()),
            Toast.LENGTH_LONG
        ).show()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection), topBar = topBar, snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(snackbarData = it)
            }
        }, floatingActionButton = floatingActionButton, bottomBar = bottomBar
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
            if (adLoadingState == AdLoadingState.Loading) AdLoadingIndicator()
            if (showQuotaDialog) {
                QuotaAlertDialog(
                    isInsufficient = isInsufficient,
                    onDismissRequest = onDismissQuotaDialog,
                    watchAd = watchAd
                )
            }
            if (showPurchaseItemDialog) {
                ThemePurchaseDialog(
                    onDismissRequest = onDismissPurchaseItemDialog,
                    onConfirm = onConfirmPurchase,
                    numberOfQuotes = numberOfQuotes,
                    isFont = isFont
                )
            }
        }
    }
}

@Composable
fun Quota(quota: Int, onClick: unit) {
    Box(
        Modifier
            .clip(defaultShape)
            .clickable(onClick = onClick), contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.bolt),
                contentDescription = stringResource(
                    id = R.string.app_quota
                )
            )
            Text(text = quota.toString(), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun ThemePurchaseDialog(
    isFont: Boolean,
    numberOfQuotes: Int?,
    onDismissRequest: unit,
    onConfirm: unit,
) {
    AlertDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = {
            onDismissRequest()
            onConfirm()
        }) { Text(text = stringResource(R.string.confirm)) }
    }, text = {
        val completionWord = if (isFont) R.string.font else R.string.theme
        Text(
            text = if (numberOfQuotes != null) stringResource(
                R.string.unlocking_this_category_will_cost_you,
                NumberFormat.getNumberInstance().format(
                    (categoryQuota(numberOfQuotes))
                ),
                NumberFormat.getNumberInstance().format(numberOfQuotes)
            ) else stringResource(R.string.unlock_question, stringResource(completionWord))
        )
    }, dismissButton = {
        TextButton(onClick = onDismissRequest) {
            Text(text = stringResource(R.string.cancel))
        }
    }, title = { Text(text = stringResource(R.string.confirm_purchase)) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdLoadingIndicator() {
    BasicAlertDialog(onDismissRequest = {}) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                Modifier
                    .wrapContentSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppLoadingIndicator()
                Text(
                    text = stringResource(id = R.string.loading_ad),
                    style = MaterialTheme.typography.bodyLarge

                )
            }
        }
    }
}

@Composable
fun QuotaAlertDialog(
    isInsufficient: Boolean,
    onDismissRequest: unit,
    watchAd: unit,
) {
    val list = listOf(
        Pair(R.string.downloading_quotes, DownloadQuoteQuota),
        Pair(R.string.create_colors_and_gradients, CreateQuota),
        Pair(R.string.importing_fonts_and_images, ImportQuota),
    )
    AlertDialog(onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
                watchAd()
            }) {
                Text(text = stringResource(id = R.string.watch_ad))
            }
        },
        title = { Text(text = stringResource(id = if (isInsufficient) R.string.insufficient_quota else R.string.app_quota)) },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(if (isInsufficient) R.string.insufficient_quota_note else R.string.quotas_note))
                if (!isInsufficient) list.forEach {
                    Row(
                        Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(id = it.first),
                            Modifier.weight(1f),
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp),
                            shape = defaultShape
                        ) {
                            Row(
                                Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = it.second.toString(), fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.bolt),
                                    contentDescription = stringResource(
                                        id = R.string.app_quota
                                    )
                                )
                            }
                        }
                    }
                }
                if (!isInsufficient) Text(text = stringResource(id = R.string.quotas_note2))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = if (isInsufficient) R.string.cancel else R.string.dismiss))
            }
        },
        icon = { AppQuotaIcon(isInsufficient) })

}

@Composable
fun AppQuotaIcon(isInsufficient: Boolean = false) {
    val color =
        LocalContentColor.provides(if (isInsufficient) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
    CompositionLocalProvider(value = color) {
        Box(
            Modifier.border(
                width = 1.dp, color = LocalContentColor.current, shape = defaultShape
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.bolt),
                contentDescription = null,
                Modifier.padding(4.dp)
            )
        }
    }
}