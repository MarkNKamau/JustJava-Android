package com.marknkamau.justjava.ui.login

import com.marknkamau.justjava.data.local.PreferencesRepository
import com.marknjunge.core.model.UserDetails
import com.marknjunge.core.auth.AuthService
import com.marknjunge.core.data.firebase.ClientDatabaseService

internal class LogInPresenter(private val activityView: LogInView,
                              private val preferences: PreferencesRepository,
                              private val auth: AuthService,
                              private val database: ClientDatabaseService) {

    fun checkSignInStatus() {
        if (auth.isSignedIn()) {
            activityView.closeActivity()
        }
    }

    fun signIn(email: String, password: String) {
        activityView.showDialog()
        auth.signIn(email, password, object : AuthService.AuthActionListener {
            override fun actionSuccessful(response: String) {
                getUserDefaults(response)
            }

            override fun actionFailed(response: String) {
                activityView.displayMessage(response)
            }
        })
    }

    private fun getUserDefaults(id: String) {
        database.getUserDefaults(id, object : ClientDatabaseService.UserDetailsListener {
            override fun onSuccess(userDetails: UserDetails) {
                preferences.saveUserDetails(userDetails)
                activityView.dismissDialog()
                activityView.displayMessage("Sign in successful")
                activityView.finishSignIn()
            }

            override fun onError(reason: String) {
                activityView.displayMessage(reason)
            }
        })
    }

    fun resetUserPassword(email: String) {
        auth.sendPasswordResetEmail(email, object : AuthService.AuthActionListener {
            override fun actionSuccessful(response: String) {
                activityView.displayMessage(response)
            }

            override fun actionFailed(response: String) {
                activityView.displayMessage(response)
            }
        })
    }
}
