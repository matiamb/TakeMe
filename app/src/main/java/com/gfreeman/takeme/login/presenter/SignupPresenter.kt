package com.gfreeman.takeme.login.presenter

import android.widget.Toast
import contract.BaseContract
import contract.SignupContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupPresenter(private val signupModel: SignupContract.SignupModel) : SignupContract.SignupPresenter<SignupContract.SignupView> {
    private lateinit var signupView: BaseContract.IBaseView

    override fun signUpUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try{
                signupModel.signUpUser(email, password)
            }
            catch (e: Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    Toast.makeText(signupView.getViewContext(), "Could not register user", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun attachView(view: SignupContract.SignupView) {
        signupView = view
    }
}