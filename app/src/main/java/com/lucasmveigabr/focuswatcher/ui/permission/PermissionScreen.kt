@file:OptIn(ExperimentalPermissionsApi::class)

package com.lucasmveigabr.focuswatcher.ui.permission

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.lucasmveigabr.focuswatcher.R
import com.lucasmveigabr.focuswatcher.ui.common.FocusScaffold

@Composable
fun PermissionWrapper(permissionList: List<String>, content: @Composable () -> Unit) {
    val permissionState = rememberMultiplePermissionsState(
        permissionList
    )
    if (permissionState.allPermissionsGranted) {
        content()
        return
    }

    FocusScaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.permission_rationale)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                permissionState.launchMultiplePermissionRequest()
            }) {
                Text(stringResource(R.string.permission_button_click))
            }
        }
    }
}