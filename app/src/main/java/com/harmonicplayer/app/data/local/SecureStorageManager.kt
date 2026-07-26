package com.harmonicplayer.app.data.local

/**
 * Interface for hardware-backed encrypted key-value storage.
 */
interface SecureStorageManager {
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String? = null): String?
    fun remove(key: String)
    fun clear()
}
