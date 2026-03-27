package com.lucasmveigabr.focuswatcher.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucasmveigabr.focuswatcher.R
import com.lucasmveigabr.focuswatcher.ui.common.FocusScaffold

@Composable
fun FocusSessionScreen(
    state: FocusSessionScreenState,
    onToggleButtonClicked: () -> Unit = {},
) {
    if (state !is FocusSessionScreenState.Success) return
    FocusScaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RecordSessionSection(state, onToggleButtonClicked)

            Spacer(modifier = Modifier.height(24.dp))

            SessionHistorySection(state)
        }
    }
}

@Composable
private fun RecordSessionSection(
    state: FocusSessionScreenState.Success,
    onToggleButtonClicked: () -> Unit
) {
    FocusSessionStopwatch(
        formattedTime = state.formattedTime,
        modifier = Modifier.padding(top = 16.dp)
    )

    FocusSessionEvents(
        modifier = Modifier.padding(vertical = 16.dp),
        noiseDistractionCount = state.noiseDistractionCount,
        movementDistractionCount = state.movementDistractionCount
    )

    Button(onToggleButtonClicked) {
        Text(text = getButtonText(state.isStarted))
    }
}

@Composable
private fun ColumnScope.SessionHistorySection(state: FocusSessionScreenState.Success) {
    Text(
        style = MaterialTheme.typography.titleLarge,
        text = stringResource(R.string.persisted_session_title),
        modifier = Modifier.align(Alignment.Start)
    )

    Spacer(modifier = Modifier.height(8.dp))

    PersistedSessionsList(state.previousRecordings)
}

@Composable
private fun PersistedSessionsList(items: List<SessionUiModel>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(items) {
            PersistedSession(it)
        }
    }
}

@Composable
private fun PersistedSession(session: SessionUiModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = session.date)
            Text(
                text = stringResource(
                    R.string.persisted_session_duration,
                    session.formattedDuration
                )
            )
            Text(
                text = stringResource(
                    R.string.persisted_session_noise_events,
                    session.noiseEvents
                )
            )
            Text(
                text = stringResource(
                    R.string.persisted_session_movement_events,
                    session.movementEvents
                )
            )
        }
    }
}

@Composable
private fun FocusSessionEvents(
    modifier: Modifier = Modifier,
    noiseDistractionCount: Int = 0,
    movementDistractionCount: Int = 0
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ) {
        DistractionCard(
            icon = {
                Icon(Icons.Default.GraphicEq, contentDescription = "Noise Distractions")
            },
            text = stringResource(
                R.string.record_session_noise_distraction_card,
                noiseDistractionCount.toString()
            )
        )

        Spacer(modifier = Modifier.width(4.dp))

        DistractionCard(
            icon = {
                Icon(Icons.Default.Vibration, contentDescription = "Movement Distractions")
            },
            text = stringResource(
                R.string.record_session_movement_distraction_card,
                movementDistractionCount.toString()
            )
        )
    }
}

@Composable
private fun DistractionCard(
    icon: @Composable () -> Unit,
    text: String
) {
    Card {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.size(130.dp)
        ) {
            icon()
            Text(text)
        }
    }
}

@Composable
private fun FocusSessionStopwatch(
    formattedTime: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            softWrap = false,
            overflow = TextOverflow.Visible,
            modifier = Modifier
                .clearAndSetSemantics {
                    contentDescription = "Current focus time: $formattedTime"
                }
        )
    }
}

@Composable
private fun getButtonText(isStarted: Boolean) =
    if (isStarted)
        stringResource(R.string.record_session_stop_text)
    else
        stringResource(R.string.record_session_start_text)

@Preview
@Composable
fun FocusSessionScreenPreview() {
    FocusSessionScreen(
        FocusSessionScreenState.Success(
            previousRecordings = listOf(
                SessionUiModel("26/03/2026 00:01:02", "10:00", 2, 5),
                SessionUiModel("26/03/2026 01:01:02", "12:03", 4, 1),
            )
        )
    )
}