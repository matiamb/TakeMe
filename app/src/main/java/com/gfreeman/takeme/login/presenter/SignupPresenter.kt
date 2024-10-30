package com.gfreeman.takeme.login.presenter

import android.util.Log
import contract.BaseContract
import contract.LoginContract
import contract.SignupContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupPresenter(private val signupModel: SignupContract.SignupModel) : SignupContract.SignupPresenter<SignupContract.SignupView> {
    private lateinit var signupView: BaseContract.IBaseView
    private lateinit var loginView: LoginContract.LoginView

    override fun signUpUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val signupResult = signupModel.signUpUser(email, password)
            Log.i("Mati", "Signup Result: $signupResult")
            withContext(Dispatchers.Main){
                if (!signupResult){
                    signupView.showErrorMessage("Password must contain upper and lower case letters, at least a number and be at least 6 characters long")
                } else {
                    signupView.showErrorMessage("Welcome!")
                    loginView.openMapsScreen()
                }
            }
        }
    }

    override fun attachView(view: SignupContract.SignupView) {
        signupView = view
    }
}