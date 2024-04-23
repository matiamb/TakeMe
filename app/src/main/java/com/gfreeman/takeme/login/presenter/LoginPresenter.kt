package com.gfreeman.takeme.login.presenter

import android.util.Patterns
import contract.ILoginContract

class LoginPresenter(private val loginModel: ILoginContract.ILoginModel): ILoginContract.ILoginPresenter {
    private lateinit var loginView: ILoginContract.LoginView
    override fun loginWithUserAndPass(user: String, password: String) {
        /*if (!isValidUser(user)){
            loginView.showErrorMessage("Incorrect User or password")
            return
        }*/
        val loginResult = loginModel.loginWithUserAndPass(user, password)
        if (!loginResult){
            loginView.showErrorMessage("Incorrect user or pass")
        }
        else{ loginView.openMapsScreen() }
    }

    private fun isValidUser(email: String): Boolean {
        return (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    override fun loginWithProvider(provider: String) {
        TODO("Not yet implemented")
    }

    override fun attachView(loginView: ILoginContract.LoginView) {
        this.loginView = loginView
    }
}