package com.harmonicplayer.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.security.GeneralSecurityException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorageManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SecureStorageManager {

    private companion object {
        private const val PREF_FILE_NAME = "harmonic_player_secure_prefs"
    }

    private var sharedPreferences: SharedPreferences = createEncryptedSharedPreferences()

    private fun createEncryptedSharedPreferences(): SharedPreferences {
        return try {
            initEncryptedSharedPreferences()
        } catch (e: Exception) {
            when (e) {
                is GeneralSecurityException, is IOException -> {
                    // Circuit breaker auto-recovery strategy: wipe corrupted preference file and re-initialize
                    context.deleteSharedPreferences(PREF_FILE_NAME)
                    initEncryptedSharedPreferences()
                }
                else -> throw e
            }
        }
    }

    private fun initEncryptedSharedPreferences(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREF_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    override fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    override fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
