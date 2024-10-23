package com.gfreeman.takeme.login.model

import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import contract.SignupContract

class SignupModel : SignupContract.SignupModel {
    private lateinit var auth: FirebaseAuth
    override fun signUpUser(email: String, password: String) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Mati", "createUserWithEmail:success")
                    val user = auth.currentUser
                    Log.i("Mati", "Current user: $user")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Mati", "createUserWithEmail:failure", task.exception)
                }
            }
    }
}