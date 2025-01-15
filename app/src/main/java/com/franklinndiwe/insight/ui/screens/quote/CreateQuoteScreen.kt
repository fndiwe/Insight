package com.franklinndiwe.insight.ui.screens.quote

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Suggestion
import com.franklinndiwe.insight.ui.components.AppTopAppBar
import com.franklinndiwe.insight.utils.AppUtils.defaultShape
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.quote.CreateQuoteViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuoteScreen(
    context: Context,
    navigateBack: () -> Unit,
    createQuoteViewModel: CreateQuoteViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(canScroll = { false })
    val uiState by createQuoteViewModel.uiState.collectAsStateWithLifecycle()
    val quote = uiState.quote
    val categorySuggestion = uiState.category
    val category = categorySuggestion.name
    val category2Suggestion = uiState.category2
    val category2 = category2Suggestion.name
    val authorSuggestion = uiState.author
    val author = authorSuggestion.name
    val isError = uiState.isError
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(id = R.string.quote_submitted_successfully)
    var enabled by remember(quote, category, author) {
        mutableStateOf(quote.isNotBlank() && category.isNotBlank() && author.isNotBlank())
    }
    var enableTextField by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = isError) {
        if (isError && !enabled && !enableTextField) {
            enabled = true
            enableTextField = true
        }
    }
    val innerCategorySuggestions by createQuoteViewModel.categorySuggestions(category)
        .collectAsStateWithLifecycle(
            emptyList()
        )
    val innerCategory2Suggestions by createQuoteViewModel.categorySuggestions(category2)
        .collectAsStateWithLifecycle(
            emptyList()
        )
    val innerAuthorSuggestions by createQuoteViewModel.authorSuggestions(author)
        .collectAsStateWithLifecycle(
            emptyList()
        )
    val (expandedCategory, setExpandedCategory) = remember { mutableStateOf(category.isNotBlank() || innerCategorySuggestions.isNotEmpty()) }
    val (expandedCategory2, setExpandedCategory2) = remember { mutableStateOf(category2.isNotBlank() || innerCategory2Suggestions.isNotEmpty()) }
    val (expandedAuthor, setExpandedAuthor) = remember { mutableStateOf(author.isNotBlank() || innerAuthorSuggestions.isNotEmpty()) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val shape = defaultShape
    Scaffold(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        AppTopAppBar(
            title = stringResource(R.string.create_quote),
            scrollBehavior = scrollBehavior,
            onNavigateBack = navigateBack
        )
    }, snackbarHost = {
        SnackbarHost(snackbarHostState) {
            Snackbar(
                snackbarData = it
            )
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                @Composable
                fun ReusableTextField(
                    value: String = "",
                    onValueChange: (String) -> Unit = {},
                    suggestion: Suggestion? = null,
                    onSuggestionChange: ((Suggestion) -> Unit)? = null,
                    modifier: Modifier = Modifier,
                    @StringRes label: Int,
                    prefixIcon: ImageVector,
                    supportingText: String = "",
                    singleLine: Boolean = true,
                    expanded: Boolean = false,
                    isError: Boolean = false,
                    setExpanded: (Boolean) -> Unit = {},
                    suggestions: List<Suggestion> = emptyList(),
                    imeAction: ImeAction = ImeAction.Next,
                    keyboardAction: () -> Unit = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                ) {
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {}) {
                        OutlinedTextField(value = suggestion?.name ?: value,
                            onValueChange = { s ->
                                if (onSuggestionChange != null && suggestion != null) onSuggestionChange(
                                    suggestion.copy(id = 0, name = s.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.getDefault()
                                        ) else it.toString()
                                    })
                                ) else onValueChange(s)
                            },
                            if (suggestions.isEmpty()) modifier.fillMaxWidth() else modifier
                                .fillMaxWidth()
                                .menuAnchor(
                                    MenuAnchorType.PrimaryEditable
                                ),
                            enabled = enableTextField,
                            label = {
                                Text(
                                    text = stringResource(label)
                                )
                            },
                            prefix = {
                                Icon(
                                    imageVector = prefixIcon,
                                    contentDescription = null,
                                    Modifier.size(24.dp)
                                )
                            },
                            shape = shape,
                            singleLine = singleLine,
                            isError = isError,
                            supportingText = { Text(text = supportingText) },
                            keyboardOptions = KeyboardOptions(imeAction = imeAction),
                            keyboardActions = KeyboardActions {
                                keyboardAction()
                            })
                        if (suggestions.isNotEmpty()) ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { setExpanded(false) }) {
                            suggestions.forEach {
                                DropdownMenuItem(text = {
                                    Text(text = it.name.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.getDefault()
                                        ) else it.toString()
                                    })
                                }, onClick = {
                                    if (onSuggestionChange != null) {
                                        onSuggestionChange(it.copy(name = it.name.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(
                                                Locale.getDefault()
                                            ) else it.toString()
                                        }))
                                    } else onValueChange(it.name.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.getDefault()
                                        ) else it.toString()
                                    })
                                    setExpanded(false)
                                    keyboardAction()
                                }, enabled = enableTextField)
                            }
                        }
                    }
                }
                //Quote TextField
                ReusableTextField(
                    value = quote,
                    onValueChange = {
                        if (it.length <= 150) createQuoteViewModel.onChangeQuoteValue(it)
                    },
                    modifier = Modifier.height(150.dp),
                    label = R.string.quote,
                    singleLine = false,
                    isError = isError,
                    prefixIcon = ImageVector.vectorResource(R.drawable.quote),
                    supportingText = if (isError) stringResource(id = R.string.quote_exists) else stringResource(
                        R.string._150, quote.length
                    ),
                    imeAction = ImeAction.Default
                )
                //Category TextField
                ReusableTextField(suggestion = categorySuggestion,
                    onSuggestionChange = {
                        createQuoteViewModel.onChangeCategoryValue(it)
                        setExpandedCategory(it.name.isNotBlank() && innerCategorySuggestions.isNotEmpty())
                    },
                    label = R.string.category,
                    prefixIcon = ImageVector.vectorResource(R.drawable.category),
                    expanded = expandedCategory,
                    setExpanded = setExpandedCategory,
                    suggestions = innerCategorySuggestions.filter {
                        it.name.lowercase() != category2.lowercase().trim()
                    })
                //Category2 TextField
                ReusableTextField(suggestion = category2Suggestion,
                    onSuggestionChange = {
                        createQuoteViewModel.onChangeCategory2Value(it)
                        setExpandedCategory2(it.name.isNotBlank() && innerCategory2Suggestions.isNotEmpty())
                    },
                    label = R.string.other_category,
                    prefixIcon = ImageVector.vectorResource(R.drawable.category),
                    expanded = expandedCategory2,
                    setExpanded = setExpandedCategory2,
                    suggestions = innerCategory2Suggestions.filter {
                        it.name.lowercase() != category.lowercase().trim()
                    })
                //Author TextField
                ReusableTextField(
                    suggestion = authorSuggestion,
                    onSuggestionChange = {
                        createQuoteViewModel.onChangeAuthorValue(it)
                        setExpandedAuthor(it.name.isNotBlank() && innerAuthorSuggestions.isNotEmpty())
                    },
                    label = R.string.author,
                    prefixIcon = Icons.Rounded.Person,
                    imeAction = ImeAction.Done,
                    keyboardAction = {
                        focusManager.clearFocus(true)
                        keyboardController?.hide()
                    },
                    expanded = expandedAuthor,
                    setExpanded = setExpandedAuthor,
                    suggestions = innerAuthorSuggestions
                )
                //Confirm creation
                Button(
                    onClick = {
                        enableTextField = false
                        enabled = false
                        createQuoteViewModel.submit {
                            Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
                            navigateBack()
                        }
                    },
                    Modifier.fillMaxWidth(),
                    enabled,
                ) {
                    Text(text = stringResource(id = R.string.submit))
                }
            }
        }
    }
}