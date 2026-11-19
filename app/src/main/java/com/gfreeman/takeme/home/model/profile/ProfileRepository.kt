package com.gfreeman.takeme.home.model.profile

/**
 * Data model representing the user's profile information.
 */
data class UserProfile(
    val name: String,
    val email: String
)

/**
 * Repository interface for Profile data operations.
 */
interface ProfileRepository {
    fun getUserData(): UserProfile
    fun logOut()
}
