package com.gfreeman.takeme.login.presenter

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Patterns
import contract.LoginContract

class LoginPresenter(private val loginModel: LoginContract.ILoginModel): LoginContract.ILoginPresenter<LoginContract.LoginView> {
    private lateinit var loginView: LoginContract.LoginView
    override fun loginWithUserAndPass(user: String, password: String) {
        /*if (!isValidUser(user)){
            loginView.showErrorMessage("Incorrect User or password")
            return
        }*/
        val loginResult = loginModel.loginWithUserAndPass(user, password)
        if (!loginResult){
            loginView.showErrorMessage("Incorrect user or pass")
        }
        saveSession()
        loginView.openMapsScreen()
    }

    private fun isValidUser(email: String): Boolean {
        return (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    override fun loginWithProvider(provider: String) {
        //TODO("Not yet implemented")
    }

    override fun attachView(loginView: LoginContract.LoginView) {
        this.loginView = loginView
        if (checkSession(loginView.getViewContext())) {
            this.loginView.openMapsScreen()
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