package com.franklinndiwe.insight.data

import com.google.android.gms.ads.rewarded.RewardItem

sealed interface AdLoadingState {
    data object Idle : AdLoadingState
    data object Loading : AdLoadingState
    data object Error : AdLoadingState
    data class Success(val rewardItem: RewardItem) : AdLoadingState
}
