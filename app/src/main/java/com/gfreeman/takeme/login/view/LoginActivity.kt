package com.gfreeman.takeme.login.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.gfreeman.takeme.R
import com.gfreeman.takeme.login.model.LoginRepository
import com.gfreeman.takeme.login.presenter.LoginPresenter
import com.google.android.material.textfield.TextInputLayout
import contract.LoginContract
import com.gfreeman.takeme.home.view.HomeActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), LoginContract.LoginView {

    lateinit var loginPresenter: LoginContract.ILoginPresenter<LoginContract.LoginView>
    lateinit var btnGoogle: MaterialButton
    val credentialManager = CredentialManager.create(getViewContext())
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
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
        val btnSkip = findViewById<Button>(R.id.btn_skip_login)
        btnGoogle = findViewById(R.id.btn_google)
        btnSkip.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            loginPresenter.loginWithUserAndPass(
                inputUser.editText?.text.toString(),
                inputPassword.editText?.text.toString())
            }
        //TODO Access with google mock code
//        btnGoogle.setOnClickListener {
//            val googleIdOption = GetGoogleIdOption.Builder()
//                .setFilterByAuthorizedAccounts(false)
//                .setServerClientId(getViewContext().getString(R.string.default_web_client_id))
//                .build()
//            val request = GetCredentialRequest.Builder()
//                .addCredentialOption(googleIdOption)
//                .build()
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val result = credentialManager.getCredential(
//                        request = request,
//                        context = getViewContext()
//                    )
//                    handleSignIn(result)
//                } catch (e : Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
    }

//    fun handleSignIn(result: GetCredentialResponse) {
//        // Handle the successfully returned credential.
//        val credential = result.credential
//
//        when (credential) {
//            is CustomCredential -> {
//                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//                    try {
//                        // Use googleIdTokenCredential and extract the ID to validate and
//                        // authenticate on your server.
//                        val idToken = GoogleIdTokenCredential
//                            .createFrom(credential.data)
//                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken.toString(), null)
//                        auth.signInWithCredential(firebaseCredential)
//                            .addOnCompleteListener(this) { task ->
//                                if (task.isSuccessful) {
//                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d("Mati", "signInWithCredential:success")
//                                    val user = auth.currentUser
//                                    val intent = Intent(this, HomeActivity::class.java)
//                                    startActivity(intent)
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Log.w("Mati", "signInWithCredential:failure", task.exception)
////                                    updateUI(null)
//                                }
//                            }
//                    } catch (e: Exception) {
//                        Log.e("Mati", "Received an invalid google id token response", e)
//                    }
//                } else {
//                    // Catch any unrecognized custom credential type here.
//                    Log.e("Mati", "Unexpected type of credential")
//                }
//
//            }
//        }
//    }

    private fun initPresenter() {
        val loginModel = LoginRepository(getViewContext())
        loginPresenter = LoginPresenter(loginModel)
        loginPresenter.attachView(this)
    }


    override fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    override fun openMapsScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun getViewContext(): Context {
        return this
    }
}