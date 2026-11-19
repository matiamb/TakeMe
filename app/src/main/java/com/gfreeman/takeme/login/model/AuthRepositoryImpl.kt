package com.gfreeman.takeme.login.model

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repository implementation for Auth operations.
 *
 * MAIN-SAFETY:
 * Uses withContext(ioDispatcher) so ViewModel can call auth operations safely from any thread.
 */
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean = withContext(ioDispatcher) {
        // Accepts any non-empty credentials for mock login
        email.isNotEmpty() && password.isNotEmpty()
    }

    override suspend fun signup(email: String, password: String): Boolean = withContext(ioDispatcher) {
        email.isNotEmpty() && password.isNotEmpty()
    }

    override suspend fun sendPasswordResetEmail(email: String): Boolean = withContext(ioDispatcher) {
        email.isNotEmpty()
    }

    override fun saveSession() {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putBoolean(FAKE_LOGIN_PREFERENCES_KEY, true).apply()
    }
}
