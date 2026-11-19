package com.gfreeman.takeme.login.model

const val FAKE_LOGIN_PREFERENCES_KEY = "FAKE_LOGIN_PREFERENCES_KEY"

/**
 * Clean Architecture Repository interface for Authentication.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
    suspend fun signup(email: String, password: String): Boolean
    suspend fun sendPasswordResetEmail(email: String): Boolean
    fun saveSession()
}
