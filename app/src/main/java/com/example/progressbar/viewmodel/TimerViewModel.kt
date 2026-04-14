package com.example.progressbar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressbar.datastore.DataStoreManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class TimerState(
    val progress: Float = 0f,
    val elapsedMillis: Long = 0L,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false
)

class TimerViewModel(): ViewModel() {
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var anchorTime: Long = 0L

    // Config (loaded from DataStore)
    private var totalTimeMillis: Long = 10000L
    // Restoration flag (prevent double-restore)
    private var isRestored = false

    init {
        //Load config + state on ViewModel creation
        loadTimerConfig()
    }

    // ─────────────────────────────────────────────────────
    //  LOAD: Restore timer from DataStore
    // ─────────────────────────────────────────────────────

    private fun loadTimerConfig() = viewModelScope.launch {
        Log.d("DBG", "Load timer config!")
        totalTimeMillis = DataStoreManager.getTimerTotalDuration()

    }

    fun onClickButton(){
        if(_state.value.isRunning) pause()
        else start()
    }

    fun start() {
        if (_state.value.isRunning || _state.value.isFinished) return
        // Anchor time survives pauses & backgrounding without drift
        // When user taps "Start":
        anchorTime = System.currentTimeMillis() - _state.value.elapsedMillis

        timerJob = viewModelScope.launch {
            _state.value = _state.value.copy(isRunning = true)
            while (currentCoroutineContext().isActive) {
                delay(16L) //  smooth UI, minimal CPU overhead
                val now = System.currentTimeMillis()
                val elapsed = minOf(now - anchorTime, totalTimeMillis)
                val progress = (elapsed.toFloat() / totalTimeMillis).coerceIn(0f, 1f)

                _state.value = _state.value.copy(elapsedMillis = elapsed, progress = progress)

                if (elapsed >= totalTimeMillis) {
                    _state.value = _state.value.copy(isRunning = false, isFinished = true)
                    break
                }
            }
        }

    }

    fun pause() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun reset() {
        timerJob?.cancel()
        _state.value = TimerState()
    }

    //before VM is destroyed
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}