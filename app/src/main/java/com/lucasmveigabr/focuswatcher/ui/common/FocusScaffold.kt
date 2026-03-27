package com.lucasmveigabr.focuswatcher.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lucasmveigabr.focuswatcher.R
import com.lucasmveigabr.focuswatcher.ui.theme.FocusWatcherTheme

@Composable
fun FocusScaffold(content: @Composable (paddingValues: PaddingValues) -> Unit) {
    FocusWatcherTheme {
        Scaffold(topBar = {
            FocusSessionToolbar()
        }) { paddingValues ->
            content(paddingValues)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FocusSessionToolbar() {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) }
    )
}