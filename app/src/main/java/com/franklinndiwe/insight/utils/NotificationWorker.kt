package com.franklinndiwe.insight.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.franklinndiwe.insight.InsightApplication
import com.franklinndiwe.insight.utils.AppUtils.annotatedString
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    @OptIn(DelicateCoroutinesApi::class)
    override fun doWork(): Result {
        val container = (applicationContext as InsightApplication).container
        val settingRepository = container.settingRepository
        val quoteRepository = container.quoteRepository
        GlobalScope.launch {
            val setting = async { settingRepository.getSetting().first() }.await()
            if (setting.dailyQuoteReminderEnabled) {
                val dailyQuoteNotification = DailyQuoteNotification(applicationContext, setting)
                DailyQuoteImpl(
                    quoteRepository, settingRepository
                ).generateQuoteOfTheDay()?.let { it2 -> annotatedString(it2).toString() }
                    ?.let { it3 ->
                        dailyQuoteNotification.showNotification(
                            applicationContext, it3
                        )
                    }
                dailyQuoteNotification.scheduleNotification()
            }
        }
        return Result.success()
    }
}