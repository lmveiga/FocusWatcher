package com.lucasmveigabr.focuswatcher.ui.session

sealed class FocusSessionScreenEvents {

    data object StartService : FocusSessionScreenEvents()
    data object StopService : FocusSessionScreenEvents()

}