package com.example.progressbar.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extension: Creates a singleton DataStore per Context + name
// add the new property
// Checks if the DataStore instance already exists for this Context + name
// Creates it once if not
// Returns the same instance on every access
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_preferences", // Unique identifier
    corruptionHandler = ReplaceFileCorruptionHandler( //Strategy to handle file corruption
        produceNewData = {
            emptyPreferences().toMutablePreferences().apply {
                DataStoreManager.Keys.TIMER_ELAPSED to 0L          // Start from 0%
                DataStoreManager.Keys.TIMER_ANCHOR to null         // No active timer
                DataStoreManager.Keys.TIMER_RUNNING to false       // Timer stopped
                DataStoreManager.Keys.TIMER_TOTAL_DURATION to 3 * 60 * 60 * 1000L
                // Add other keys with safe defaults as needed
            }
        } // Reset to empty on corruption
    )
)

/**
 * Singleton manager for Preferences DataStore.
 * Thread-safe, coroutine-based, and type-safe.
 */
object DataStoreManager {
    // Type-safe preference keys (define all your keys here)
   object Keys {
        val TIMER_ELAPSED = longPreferencesKey("timer_elapsed")
        val TIMER_ANCHOR = longPreferencesKey("timer_anchor") // when timer was started
        val TIMER_RUNNING = booleanPreferencesKey("timer_running")
        val TIMER_TOTAL_DURATION = longPreferencesKey("timer_total_duration")
        val TIMER_THRESHOLDS = stringPreferencesKey("timer_thresholds")
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        // Add more keys as needed...
    }
    // Default fallback (3 hours)
    private const val DEFAULT_TIMER_DURATION = 30 * 1000L //5 * 60 * 60 * 1000L//3 * 60 * 60 * 1000L//
    // Internal reference (initialized via init())
    private lateinit var dataStore: DataStore<Preferences>

    /**
     * Initialize the manager with Application Context.
     * Call once in Application.onCreate()
     */
    fun init(context: Context) {
        if (!::dataStore.isInitialized) {
            dataStore = context.dataStore
        }
    }

    // ─────────────────────────────────────────────────────
    //  WRITE OPERATIONS (suspend functions)
    // ─────────────────────────────────────────────────────

    suspend fun saveTimerState(elapsed: Long, anchor: Long?, isRunning: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.TIMER_ELAPSED] = elapsed
            // Handle nullable anchor correctly:
            if (anchor != null) {
                prefs[Keys.TIMER_ANCHOR] = anchor  // Save timestamp
            } else {
                prefs.remove(Keys.TIMER_ANCHOR)    // Explicitly clear old value
            }
            prefs[Keys.TIMER_RUNNING] = isRunning
        }
    }
    // Save user's chosen duration (call when user selects a new time)
    suspend fun saveTimerTotalDuration(durationMillis: Long) {
        dataStore.edit { prefs ->
            prefs[Keys.TIMER_TOTAL_DURATION] = durationMillis
        }
    }

    suspend fun saveString(key: Preferences.Key<String>, value: String) {
        dataStore.edit { prefs -> prefs[key] = value }
    }

    suspend fun saveInt(key: Preferences.Key<Int>, value: Int) {
        dataStore.edit { prefs -> prefs[key] = value }
    }

    suspend fun saveBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { prefs -> prefs[key] = value }
    }

    suspend fun saveLong(key: Preferences.Key<Long>, value: Long) {
        dataStore.edit { prefs -> prefs[key] = value }
    }

    suspend fun saveFloat(key: Preferences.Key<Float>, value: Float) {
        dataStore.edit { prefs -> prefs[key] = value }
    }

    suspend fun removeKey(key: Preferences.Key<*>) {
        dataStore.edit { prefs -> prefs.remove(key) }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    suspend fun saveTimerThresholds(thresholds: List<Long>) {
        dataStore.edit { prefs ->
            prefs[Keys.TIMER_THRESHOLDS] = thresholds.joinToString(",")
        }
    }
    // ─────────────────────────────────────────────────────
    //  READ OPERATIONS (Flow for reactive UI)
    // ─────────────────────────────────────────────────────

    fun observeTimerElapsed(): Flow<Long> = dataStore.data
        .catch { emit(emptyPreferences()) } // Handle IO errors gracefully
        .map { prefs -> prefs[Keys.TIMER_ELAPSED] ?: 0L }

    fun observeTimerAnchor(): Flow<Long?> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[Keys.TIMER_ANCHOR] }

    fun observeTimerRunning(): Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[Keys.TIMER_RUNNING] ?: false }

    // Observe duration reactively (for Compose UI)
    fun observeTimerTotalDuration(): Flow<Long> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[Keys.TIMER_TOTAL_DURATION] ?: DEFAULT_TIMER_DURATION
        }

    fun <T> observe(key: Preferences.Key<T>, defaultValue: T): Flow<T> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[key] ?: defaultValue }

    fun observeTimerThresholds(): Flow<List<Long>> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[Keys.TIMER_THRESHOLDS]
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?.takeIf { it.isNotEmpty() }
                ?: listOf(100L, 1000L)  // ✅ Default fallback
        }

    // ─────────────────────────────────────────────────────
    //  ONE-TIME READ (for non-reactive use cases)
    // ─────────────────────────────────────────────────────

    suspend fun getTimerElapsed(): Long = dataStore.data
        .map { prefs -> prefs[Keys.TIMER_ELAPSED] ?: 0L }
        .first()

    //  One-time read (for ViewModel init)
    suspend fun getTimerTotalDuration(): Long = dataStore.data
        .map { prefs -> prefs[Keys.TIMER_TOTAL_DURATION] ?: DEFAULT_TIMER_DURATION }
        .first()

    suspend fun <T> getValue(key: Preferences.Key<T>, defaultValue: T): T = dataStore.data
        .map { prefs -> prefs[key] ?: defaultValue }
        .first()

    suspend fun loadTimerState(): Triple<Long, Long?, Boolean> {
        val prefs = dataStore.data.first() // Suspend until first value is available

        val elapsed = prefs[Keys.TIMER_ELAPSED] ?: 0L
        val anchor = prefs[Keys.TIMER_ANCHOR] // Nullable: null if removed
        val isRunning = prefs[Keys.TIMER_RUNNING] ?: false

        return Triple(elapsed, anchor, isRunning)
    }

    suspend fun getTimerThresholds(): List<Long> = dataStore.data
        .map { prefs ->
            prefs[Keys.TIMER_THRESHOLDS]
                ?.split(",")
                ?.mapNotNull { it.trim().toLongOrNull() }
                ?.takeIf { it.isNotEmpty() }
                ?: listOf(60L, 150L, 240L)
        }
        .first()
}

