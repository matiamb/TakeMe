package com.gfreeman.takeme.home.model.profile

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.gfreeman.takeme.login.model.FAKE_LOGIN_PREFERENCES_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Implementation of ProfileRepository handling logout & SharedPreferences operations.
 */
class ProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ProfileRepository {

    override fun getUserData(): UserProfile {
        return UserProfile(
            name = "User Name",
            email = "user@example.com"
        )
    }

    override fun logOut() {
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit { putBoolean(FAKE_LOGIN_PREFERENCES_KEY, false) }
    }
}
