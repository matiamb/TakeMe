package com.gfreeman.takeme.login.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import contract.LoginContract
import kotlinx.coroutines.tasks.await

class LoginRepository(private val context: Context): LoginContract.ILoginModel {
    private lateinit var auth: FirebaseAuth
    override suspend fun loginWithUserAndPass(email: String, password: String): Boolean {
        auth = Firebase.auth
        var result = false
        if (email == "" || password == ""){
            Log.i("Mati", "signInWithEmail:failure")
            result = false
        } else {
            val job = auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        Log.d("Mati", "signInWithEmail:success, $user")
                        result = true
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Mati", "signInWithEmail:failure", task.exception)
                        result = false
                    }
                }
            job.await()
        }
        return result
    }

    override fun logOut(){
        auth.signOut()
    }

    override fun loginWithProvider(provider: String) {
        TODO("Not yet implemented")
    }
}