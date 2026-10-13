package com.rubecoder.rubeaurachecker.data.local

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetKey
import androidx.datastore.preferences.core.stringKey
import androidx.datastore.preferences.preferencesDataStore
import com.rubecoder.rubeaurachecker.domain.model.ScanHistoryItem
import com.rubecoder.rubeaurachecker.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryDataStore @Inject constructor(
    private val context: Context
) : HistoryRepository {

    private val Context.datastore by preferencesDataStore(name = "history_prefs")

    private val HISTORY_ITEMS_KEY = stringSetKey("history_items")

    override suspend fun getHistoryItems(): Flow<List<ScanHistoryItem>> {
        return context.datastore.data
            .map { preferences ->
                val itemsJsonSet = preferences[HISTORY_ITEMS_KEY] ?: emptySet()
                itemsJsonSet.mapNotNull { json ->
                    try {
                        Json { ignoreUnknownKeys = true }.decodeFromString(ScanHistoryItem.serializer(), json)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
    }

    override suspend fun deleteHistoryItem(id: String): Boolean {
        return try {
            context.datastore.edit { preferences ->
                val itemsJsonSet = preferences[HISTORY_ITEMS_KEY] ?: emptySet()
                val filteredItems = itemsJsonSet.filterNot { json ->
                    try {
                        Json { ignoreUnknownKeys = true }.decodeFromString(ScanHistoryItem.serializer(), json).id == id
                    } catch (e: Exception) {
                        false
                    }
                }
                preferences[HISTORY_ITEMS_KEY] = filteredItems
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun clearHistory(): Boolean {
        return try {
            context.datastore.edit { preferences ->
                preferences[HISTORY_ITEMS_KEY] = emptySet()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun addScan(score: Int, timestamp: Long = System.currentTimeMillis()): Boolean {
        return try {
            val newItem = ScanHistoryItem(
                id = UUID.randomUUID().toString(),
                score = score,
                timestamp = timestamp
            )
            val json = Json { ignoreUnknownKeys = true }.encodeToString(ScanHistoryItem.serializer(), newItem)
            context.datastore.edit { preferences ->
                val itemsJsonSet = preferences[HISTORY_ITEMS_KEY] ?: emptySet()
                val updatedSet = itemsJsonSet + json
                // Limit to 100 most recent items
                val sortedItems = updatedSet.toList()
                    .mapNotNull { json ->
                        try {
                            Json { ignoreUnknownKeys = true }.decodeFromString(ScanHistoryItem.serializer(), json)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    .sortedByDescending { it.timestamp }
                    .take(100)
                    .map { Json { ignoreUnknownKeys = true }.encodeToString(ScanHistoryItem.serializer(), it) }
                    .toSet()
                preferences[HISTORY_ITEMS_KEY] = sortedItems
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}