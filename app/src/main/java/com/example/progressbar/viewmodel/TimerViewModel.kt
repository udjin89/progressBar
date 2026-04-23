package com.example.progressbar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressbar.datastore.DataStoreManager
import com.example.progressbar.datastore.DataStoreManager.loadTimerState
import com.example.progressbar.datastore.DataStoreManager.saveTimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
//    private var totalDuration: Long = 10000L
    // Restoration flag (prevent double-restore)
    private var isRestored = false

    // ✅ Minimal hot Flow: Cold Flow from DataStoreManager → StateFlow
    val totalDuration: StateFlow<Long> = DataStoreManager.observeTimerTotalDuration()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,  // Start immediately (simplest)
            initialValue = 30_000L              // Must match DEFAULT_TIMER_DURATION
        )

    // ✅ Hot Flow: Reactive thresholds for UI
    val thresholds: StateFlow<List<Long>> = DataStoreManager.observeTimerThresholds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf(100L, 1000L)  // Must match default
        )

    init {
        Log.w("DBG", "init TimerViewModel")
        viewModelScope.launch { restoreTimer() }
    }

    suspend fun saveTotalDuration(newDuration: Long) {
        DataStoreManager.saveTimerTotalDuration(newDuration)
    }

    // ✅ Save thresholds to DataStore
    suspend fun saveThresholds(newThresholds: List<Long>) {
        DataStoreManager.saveTimerThresholds(newThresholds)
    }

    // ✅ Update single threshold by index (helper for your dialog)
    suspend fun updateThreshold(index: Int, newValue: Long) {
        val current = thresholds.value.toMutableList()
        if (index in current.indices) {
            current[index] = newValue
            saveThresholds(current)  // Triggers reactive update
        }
    }
    // ─────────────────────────────────────────────────────
    //  LOAD: Restore timer from DataStore
    // ─────────────────────────────────────────────────────
    private suspend fun restoreTimer(){
        //totalDuration = DataStoreManager.getTimerTotalDuration()

        val (savedElapsed, savedAnchor, wasRunning) = loadTimerState()

        Log.i("DBG", "Restore Timer: \n" +
                "\tTotal: $totalDuration\n" +
                "\tElapsed: $savedElapsed\n" +
                "\tAnchor: $savedAnchor\n" +
                "\tisRunning: $wasRunning")
        //
        if (!wasRunning || savedAnchor == null) {
            _state.value = _state.value.copy(
                totalDurationMillis = totalDuration.value,
                progress = getProgress(savedElapsed, totalDuration.value),
                elapsedMillis = savedElapsed.coerceAtLeast(0L),
                isRunning = false,
                isFinished = savedElapsed >= totalDuration.value)
            return
        }

        // If was running → calculate true elapsed via wall-clock
        val now = System.currentTimeMillis()
        val trueElapsed = (now - savedAnchor).coerceAtLeast(0L)
        val cappedElapsed = minOf(trueElapsed, totalDuration.value)

        // ✅ Progress recalculated from source data
        val progress = getProgress(cappedElapsed, totalDuration.value)

        _state.value = TimerState(
            elapsedMillis = cappedElapsed,
            progress = progress,  // ✅ Always derived, never persisted
            totalDurationMillis = totalDuration.value,
            isFinished = cappedElapsed >= totalDuration.value,
            isRunning = cappedElapsed < totalDuration.value
        )

        if (cappedElapsed < totalDuration.value) {
            anchorTime = now - cappedElapsed
            startTimerCoroutine()
        } else {
            saveTimerState(elapsed = cappedElapsed, anchor = null, isRunning = false)
        }
    }
//    private fun loadTimerConfig() = viewModelScope.launch {
//        totalDuration = DataStoreManager.getTimerTotalDuration()
//        Log.d("DBG", "Load timer config! totalDuration ${totalDuration}")
//        _state.value = _state.value.copy(totalDurationMillis = totalDuration)
//    }

    fun onClickButton(){
        if(_state.value.isFinished) resetC()
         else if(_state.value.isRunning) pause()
                else start()
    }

    private fun start() {
        if (_state.value.isRunning || _state.value.isFinished) return

        val now = System.currentTimeMillis()
        val currentElapsed = _state.value.elapsedMillis

        anchorTime = now - currentElapsed

        val shouldReset = currentElapsed == 0L

        _state.value = _state.value.copy(
            elapsedMillis = if(shouldReset) 0L else currentElapsed,
            progress = if(shouldReset) 0f else getProgress(currentElapsed, totalDuration.value),
            isRunning = true,
            isFinished = false
        )
        Log.d("DBG", "State: ellapsed: ${state.value.elapsedMillis} + progress: ${state.value.progress}")

        viewModelScope.launch {
            saveTimerState(
                elapsed = currentElapsed,
                anchor = anchorTime,
                isRunning = true
            )
        }

        startTimerCoroutine()
    }

    private fun startTimerCoroutine(){
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (currentCoroutineContext().isActive) {
                if (!_state.value.isRunning) break
                delay(16L) //  smooth UI, minimal CPU overhead

                val calc = calculateTimerState(anchorTime, totalDuration.value)

                // Update state (this triggers UI update!)
                _state.value = _state.value.copy(
                    elapsedMillis = calc.elapsed,
                    progress = calc.progress)

                if (calc.elapsed >= totalDuration.value) {
                    _state.value = _state.value.copy(isRunning = false, isFinished = true)
                    break
                }
            }
        }
    }
    private fun pause() {
        timerJob?.cancel()

        val calc = calculateTimerState(anchorTime, totalDuration.value)

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
    private fun saveTimerState(elapsed : Long){
        viewModelScope.launch {
            saveTimerState(elapsed, null, false)
        }
    }

    fun reset() {
        timerJob?.cancel()
        saveTimerState(0L)
    }

    fun resetC() {
        viewModelScope.launch {
            resetInitialState()
        }
    }

    suspend fun resetInitialState(){
        Log.e("DBG", "resetInitialState: $anchorTime")
        timerJob?.cancel()
        anchorTime = 0L
        resetStateUI()
        //saveTimerState(0L)
//        restoreTimer()
    }

    private fun resetStateUI(){
        Log.e("DBG", "RESET UI: $anchorTime")
        _state.value = TimerState(
            elapsedMillis = 0L,
            progress = 0f,
            //totalDurationMillis = totalDuration.value,
            isFinished = false,
            isRunning = false
        )
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
        val progress = getProgress(elapsed, totalTimeMillis)

        return TimerCalculation(elapsed, progress)
    }

    fun getTotalDuration() : Long {
        return totalDuration.value
    }

    private fun getProgress(elapsed : Long, total: Long): Float {
        return (elapsed.toDouble() / total).toFloat().coerceIn(0f, 1f)
    }
}