package com.gfreeman.takeme.login.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gfreeman.takeme.R
import com.gfreeman.takeme.R.*
import com.gfreeman.takeme.login.model.SignupModel
import com.gfreeman.takeme.login.presenter.SignupPresenter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import contract.SignupContract

class SignupActivity : AppCompatActivity(), SignupContract.SignupView {
    private lateinit var txtEmail: TextInputLayout
    private lateinit var txtPassword: TextInputLayout
    private lateinit var btnSignup: MaterialButton
    private lateinit var signupPresenter: SignupContract.SignupPresenter<SignupContract.SignupView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txtEmail = findViewById(R.id.textfield_signup_email)
        txtPassword = findViewById(R.id.textfield_signup_password)
        btnSignup = findViewById(R.id.btn_signup_fragment)

        initPresenter()
        btnSignup.setOnClickListener {
            val email = txtEmail.editText?.text.toString()
            val password = txtPassword.editText?.text.toString()
            if (email != null || password != null){
                Log.i("Mati", "Email: $email, Password: $password")
                signupPresenter.signUpUser(email, password)
                Toast.makeText(this, getString(string.welcome_signup_message), Toast.LENGTH_SHORT).show()
                this.finish()
            }
            else {
                Toast.makeText(this, "Email or password cannot be null", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun initPresenter() {
        val signupModel = SignupModel()
        signupPresenter = SignupPresenter(signupModel)
        signupPresenter.attachView(this)
    }

    override fun showErrorMessage(message: String) {
        TODO("Not yet implemented")
    }

    override fun getViewContext(): Context {
        return this
    }
}