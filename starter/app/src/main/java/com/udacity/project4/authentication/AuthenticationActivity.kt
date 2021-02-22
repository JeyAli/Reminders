package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import timber.log.Timber

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */

class AuthenticationActivity : AppCompatActivity() {
    companion object {
        const val SIGN_IN_CODE = 111
    }

    private lateinit var binding: ActivityAuthenticationBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel.authenticationState.observe(this, { authenticationState ->
            if (authenticationState == LoginViewModel.AuthenticationState.AUTHENTICATED) {
                binding.authButton.setOnClickListener {
                    startRemindersActivity()
                    finish()
                    Timber.i("User is Authenticated")
                }

            } else if (authenticationState == LoginViewModel.AuthenticationState.UNAUTHENTICATED) {
                Timber.e("User is not Authenticated")
                binding.authButton.setOnClickListener {
                    launchSignInFlow()
                }
            }
        })
    }

    private fun launchSignInFlow() {
        val providers =
            arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build()
            )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.GreenTheme)
                .setLogo(R.drawable.f7ucf36adhhcr3cse67f6q39fs)
                .setIsSmartLockEnabled(true)
                .build(),
            SIGN_IN_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                 Timber.i( "Successfully Signed in")
                startRemindersActivity()
                finish()
            } else {
                if (response == null) {
                    Timber.i("Back button pressed")
                    return
                }
                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    Timber.e("No Network")
                }
            }
        }
    }

    private fun startRemindersActivity() {
        val intent = Intent(this, RemindersActivity::class.java)
        startActivity(intent)
    }
}
