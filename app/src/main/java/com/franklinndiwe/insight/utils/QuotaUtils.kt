package com.franklinndiwe.insight.utils

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.franklinndiwe.insight.BuildConfig
import com.franklinndiwe.insight.data.AdLoadingState
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class QuotaUtils(
    private val viewModelScope: CoroutineScope,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    private var rewardedAd: RewardedAd? = null
    var adLoadingState: AdLoadingState by mutableStateOf(AdLoadingState.Idle)
        private set
    val quota = userPreferencesRepository.quota.flowOn(Dispatchers.IO)

    fun shouldPerformOperation(quotaBalance: Int, quotaAmount: Int) = quotaBalance >= quotaAmount

    fun deductQuota(quota: Int, quotaToDeduct: Int) =
        viewModelScope.launch { userPreferencesRepository.saveQuota(quota - quotaToDeduct) }

    fun watchAd(context: Context) {

        val fullScreenContentCallback: FullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    // Code to be invoked when the ad showed full screen content.
                }

                override fun onAdDismissedFullScreenContent() {
                    adLoadingState = AdLoadingState.Idle
                    rewardedAd = null
                    // Code to be invoked when the ad dismissed full screen content.
                }
            }
        adLoadingState = AdLoadingState.Loading
        val debugAdUnitId = "ca-app-pub-3940256099942544/5224354917"
        RewardedAd.load(context,
            if (BuildConfig.DEBUG) debugAdUnitId else "ca-app-pub-6846281565649329/8548715104",
            AdManagerAdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    rewardedAd?.fullScreenContentCallback = fullScreenContentCallback
                    rewardedAd?.show(context as Activity) { rewardItem ->
                        val rewardItemAmount = rewardItem.amount
                        viewModelScope.launch(Dispatchers.IO) {
                            userPreferencesRepository.saveQuota(
                                quota.flowOn(Dispatchers.IO).first() + rewardItemAmount
                            )
                        }
                        adLoadingState = AdLoadingState.Success(rewardItem)
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    viewModelScope.launch {
                        //Show error
                        adLoadingState = AdLoadingState.Error
                        delay(10000L)
                        adLoadingState = AdLoadingState.Idle
                    }
                }
            })
    }
}