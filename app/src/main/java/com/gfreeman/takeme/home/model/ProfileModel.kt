package com.gfreeman.takeme.home.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import contract.ProfileContract

class ProfileModel: ProfileContract.ProfileModel {
    private var auth = Firebase.auth
    override fun logOut() {
        try {
            auth.signOut()
            val currentUser = auth.currentUser
            Log.i("Mati", "Current user logged in: $currentUser")
        } catch (e: Exception){
            Log.i("Mati", "Not logged in to Firebase")
        }
    }
    override fun getUserData(): List<String?> {
        val user = auth.currentUser
        if (user != null){
            val list = arrayListOf(user.displayName, user.email)
            return list
        } else {
            return emptyList()
        }
    }
}