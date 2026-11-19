package com.gfreeman.takeme.login.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.gfreeman.takeme.home.view.HomeActivity
import com.gfreeman.takeme.login.viewmodel.LoginViewModel
import com.gfreeman.takeme.ui.theme.TakeMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TakeMeTheme {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    },
                    onSkipClick = {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    },
                    onSignUpClick = {
                        startActivity(Intent(this, SignupActivity::class.java))
                    }
                )
            }
        }
    }
}
