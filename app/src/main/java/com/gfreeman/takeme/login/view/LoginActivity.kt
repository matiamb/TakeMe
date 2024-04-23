package com.gfreeman.takeme.login.view

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gfreeman.takeme.R
import com.gfreeman.takeme.login.model.LoginRepository
import com.gfreeman.takeme.login.presenter.LoginPresenter
import com.google.android.material.textfield.TextInputLayout
import contract.ILoginContract

class LoginActivity : AppCompatActivity(), ILoginContract.LoginView {

    lateinit var loginPresenter: ILoginContract.ILoginPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        initPresenter()
        initViews()

    }

    private fun initViews() {
        val inputUser = findViewById<TextInputLayout>(R.id.textfield_user)
        val inputPassword = findViewById<TextInputLayout>(R.id.textfield_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener {
            loginPresenter.loginWithUserAndPass(
                inputUser.editText?.text.toString(),
                inputPassword.editText?.text.toString())
        }
    }

    private fun initPresenter() {
        val loginModel = LoginRepository()
        loginPresenter = LoginPresenter(loginModel)
        loginPresenter.attachView(this)
    }


    override fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun openMapsScreen() {
        TODO("Not yet implemented")
    }
}