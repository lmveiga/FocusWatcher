package com.lucasmveigabr.focuswatcher

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.lucasmveigabr.focuswatcher.service.FocusSessionService
import com.lucasmveigabr.focuswatcher.ui.permission.PermissionWrapper
import com.lucasmveigabr.focuswatcher.ui.session.FocusSessionScreen
import com.lucasmveigabr.focuswatcher.ui.session.FocusSessionScreenEvents
import com.lucasmveigabr.focuswatcher.ui.session.FocusSessionViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {

    private val focusSessionViewModel: FocusSessionViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PermissionWrapper(getRequiredPermissionsList()) {
                val recordSessionState = focusSessionViewModel.screenState.collectAsState().value
                FocusSessionScreen(
                    recordSessionState,
                    onToggleButtonClicked = {
                        focusSessionViewModel.onToggleSessionClick()
                    }
                )
            }
        }

        focusSessionViewModel.onActivityCreated()
        observeEffects()
    }

    private fun getRequiredPermissionsList() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            listOf(Manifest.permission.RECORD_AUDIO)
        }

    private fun observeEffects() {
        lifecycleScope.launch {
            focusSessionViewModel.screenEffects.collect {
                when (it) {
                    FocusSessionScreenEvents.StartService -> {
                        startService(
                            Intent(this@MainActivity, FocusSessionService::class.java)
                        )
                    }

                    FocusSessionScreenEvents.StopService -> {
                        stopService(
                            Intent(this@MainActivity, FocusSessionService::class.java)
                        )
                    }
                }
            }
        }

    }

}