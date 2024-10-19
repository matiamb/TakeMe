package com.gfreeman.takeme.login.presenter

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.util.Patterns
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import contract.LoginContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPresenter(private val loginModel: LoginContract.ILoginModel): LoginContract.ILoginPresenter<LoginContract.LoginView> {
    private lateinit var loginView: LoginContract.LoginView
    private lateinit var auth: FirebaseAuth
    override fun loginWithUserAndPass(user: String, password: String) {
        /*if (!isValidUser(user)){
            loginView.showErrorMessage("Incorrect User or password")
            return
        }*/
        CoroutineScope(Dispatchers.IO).launch {
            val loginResult = loginModel.loginWithUserAndPass(user, password)
            Log.i("Mati", "LoginResult= $loginResult")
            withContext(Dispatchers.Main){
                if (loginResult == false){
                    loginView.showErrorMessage("Incorrect user or pass")
                } else {
                    saveSession()
                    loginView.openMapsScreen()
                }
            }
        }
    }

    override fun logOut(){
        loginModel.logOut()
    }

    private fun isValidUser(email: String): Boolean {
        return (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    override fun loginWithProvider(provider: String) {
        //TODO("Not yet implemented")
    }

    override fun attachView(loginView: LoginContract.LoginView) {
        this.loginView = loginView
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (checkSession(loginView.getViewContext())) {
                this.loginView.openMapsScreen()
            } else {
                auth.signOut()
                Log.i("Mati", "User logged in but no remember me was pressed")
            }
        } else {
            Log.i("Mati", "No user logged in")
        }
    }
    private fun saveSession(){
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(loginView.getViewContext())
        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean(FAKE_LOGIN_PREFERENCES_KEY, true)
        editor.apply()
    }
    private fun checkSession(context: Context?) = context?.let {
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(FAKE_LOGIN_PREFERENCES_KEY, false)
    } ?: false
    companion object {
        const val FAKE_LOGIN_PREFERENCES_KEY = "FAKE_LOGIN_PREFERENCES_KEY"
    }
}