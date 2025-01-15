package com.franklinndiwe.insight.ui.states

import androidx.compose.runtime.Immutable
import com.franklinndiwe.insight.data.Suggestion

@Immutable
data class CreateQuoteUIState(
    val quote: String = "",
    val category: Suggestion = Suggestion(0, ""),
    val category2: Suggestion = Suggestion(0, ""),
    val author: Suggestion = Suggestion(0, ""),
    val isError: Boolean = false,
)
