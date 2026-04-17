package com.example.progressbar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressbar.datastore.DataStoreManager
import com.example.progressbar.datastore.DataStoreManager.saveTimerState
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
    val totalDurationMillis: Long = 10_000L,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false
)

class TimerViewModel(): ViewModel() {
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var anchorTime: Long = 0L

    // Config (loaded from DataStore)
    private var totalDuration: Long = 10000L
    // Restoration flag (prevent double-restore)
    private var isRestored = false

    init {
        //Load config + state on ViewModel creation
        Log.w("DBG", "init TimerViewModel")
        loadTimerConfig()
    }

    // ─────────────────────────────────────────────────────
    //  LOAD: Restore timer from DataStore
    // ─────────────────────────────────────────────────────

    private fun loadTimerConfig() = viewModelScope.launch {
        totalDuration = DataStoreManager.getTimerTotalDuration()
        Log.d("DBG", "Load timer config! totalDuration ${totalDuration}")
        _state.value = _state.value.copy(totalDurationMillis = totalDuration)
    }

    fun onClickButton(){
        if(_state.value.isRunning) pause()
        else start()
    }

    private fun start() {
        if (_state.value.isRunning || _state.value.isFinished) return

        val now = System.currentTimeMillis()
        val currentElapsed = _state.value.elapsedMillis

        anchorTime = now - currentElapsed
        val calcStartProgress = calculateTimerState(anchorTime, totalDuration)

        _state.value = _state.value.copy(
            elapsedMillis = 0L,
            progress = 0f,
            isRunning = true,
            isFinished = false
        )
        Log.d("DBG", "State: ellapsed: ${state.value.elapsedMillis} + progress: ${state.value.progress}")
        timerJob = viewModelScope.launch {
            while (currentCoroutineContext().isActive) {
                delay(16L) //  smooth UI, minimal CPU overhead

                val calc = calculateTimerState(anchorTime, totalDuration)

                // Update state (this triggers UI update!)
                _state.value = _state.value.copy(
                    elapsedMillis = calc.elapsed,
                    progress = calc.progress)

                if (calc.elapsed >= totalDuration) {
                    _state.value = _state.value.copy(isRunning = false, isFinished = true)
                    break
                }
            }
        }
    }

    private fun pause() {
        timerJob?.cancel()

        val currentState = _state.value
        val calc = calculateTimerState(anchorTime, totalDuration)

        _state.value = _state.value.copy(
            elapsedMillis = calc.elapsed,
            progress = calc.progress,
            isRunning = false
        )

        saveTimerState(calc)
        anchorTime = 0L
    }

    private fun saveTimerState(calc: TimerCalculation) {
        viewModelScope.launch {
            saveTimerState(calc.elapsed, null, false)
        }
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

    private data class TimerCalculation(
        val elapsed: Long,
        val progress: Float
    )

    private fun calculateTimerState(
        anchorTime: Long,
        totalTimeMillis: Long
    ): TimerCalculation {
        val now = System.currentTimeMillis() // Get real clock time
        val elapsed = minOf(now - anchorTime, totalTimeMillis) // Calculate elapsed
        // elapsed time is divided by total time => get progress between (0.0, 1.0)
        // .coerceIn(0,1) - forces a value to stay within a range
        val progress = (elapsed.toDouble() / totalTimeMillis).toFloat().coerceIn(0f, 1f)

        return TimerCalculation(elapsed, progress)
    }

    fun getTotalDuration() : Long {
        return totalDuration
    }
}