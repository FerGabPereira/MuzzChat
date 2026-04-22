package com.fernandopereira.muzzchat.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State : UiState, Action : UiAction>(
    initialState: State,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    protected fun submitState(reducer: State.() -> State) {
        val currentState = _uiState.value
        val newState = currentState.reducer()

        if (newState == currentState) return

        _uiState.value = newState
    }

    abstract fun onAction(action: Action)
}
