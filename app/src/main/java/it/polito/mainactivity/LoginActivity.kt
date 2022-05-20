package it.polito.mainactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
        .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
        .build()
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        res -> this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){
        val response = result.idpResponse
        if(result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
        } else {
            Log.d("LoginFragment", "sign in failed")
            if(response == null)
                Log.d("LoginFragment", "canceled by user")
            else
                Log.d("LoginFragment", "error:" + response.error?.errorCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val bLogin = findViewById<SignInButton>(R.id.bLogin)
        bLogin.setOnClickListener {
            signInLauncher.launch(signInIntent)
        }
    }

}