package com.example.loginsignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider

private const val RC_GOOGLE_SIGN_UP = 1001


class SignupActivity : AppCompatActivity() {

    private lateinit var signupEmailEditText: EditText
    private lateinit var signupPasswordEditText: EditText
    private val TAG = "MainActivity"
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signupEmailEditText = findViewById(R.id.signupEmailEditText)
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText)

        auth = FirebaseAuth.getInstance()

        val googleSignUpButton = findViewById<SignInButton>(R.id.google_sign_up_button)
        googleSignUpButton.setOnClickListener {
            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
            startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGN_UP)
        }

    }

    fun register(view: View) {
        val email = signupEmailEditText.text.toString()
        val password = signupPasswordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Signup successful
                    Toast.makeText(this, "Signup successful.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // Signup failed
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the request code is for Google sign up
        if (requestCode == RC_GOOGLE_SIGN_UP) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Sign up with Google credential
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign up successful, go to the main activity
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            // Sign up failed, display an error message
                            Log.w(TAG, "Google sign up failed", task.exception)
                            Toast.makeText(this, "Google sign up failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                // Google sign up failed, display an error message
                Log.w(TAG, "Google sign up failed!!!!! " + e.message, e)
                Toast.makeText(this, "Google sign up failed!!!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
