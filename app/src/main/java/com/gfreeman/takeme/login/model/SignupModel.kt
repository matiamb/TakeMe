package com.gfreeman.takeme.login.model

import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth
import contract.SignupContract
import kotlinx.coroutines.tasks.await

class SignupModel : SignupContract.SignupModel {
    private lateinit var auth: FirebaseAuth
    override suspend fun signUpUser(email: String, password: String): Boolean {
        auth = Firebase.auth
        var result = false
        try {
           val job = auth.createUserWithEmailAndPassword(email, password)
            job.await()
            result = job.isSuccessful
        } catch (e : FirebaseAuthException){
            print(e.message)
            result = false
        }
        return result

//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("Mati", "createUserWithEmail:success")
//                    val user = auth.currentUser
//                    Log.i("Mati", "Current user: $user")
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("Mati", "createUserWithEmail:failure", task.exception)
//                }
//            }
    }
}