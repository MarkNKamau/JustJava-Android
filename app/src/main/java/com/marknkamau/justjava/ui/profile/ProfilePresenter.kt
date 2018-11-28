package com.marknkamau.justjava.ui.profile

import com.marknjunge.core.auth.AuthService
import com.marknjunge.core.data.firebase.ClientDatabaseService
import com.marknkamau.justjava.data.local.PreferencesRepository
import com.marknjunge.core.model.Order
import com.marknjunge.core.model.UserDetails
import timber.log.Timber

internal class ProfilePresenter(private val view: ProfileView,
                                private val preferencesRepository: PreferencesRepository,
                                private val authenticationService: AuthService,
                                private val databaseService: ClientDatabaseService) {

    init {
        getUserDetails()
        getPreviousOrders()
    }

    lateinit var userDetails: UserDetails

    private fun getUserDetails() {
        userDetails = preferencesRepository.getUserDetails()
        view.displayUserDetails(userDetails)
    }

    private fun getPreviousOrders() {
        view.showOrdersProgressBar()
        databaseService.getPreviousOrders(authenticationService.getCurrentUser().userId, object : ClientDatabaseService.PreviousOrdersListener {
            override fun onSuccess(previousOrders: MutableList<Order>) {
                view.hideOrdersProgressBar()
                if (previousOrders.isEmpty()) {
                    view.displayNoPreviousOrders()
                } else {
                    val sorted = previousOrders.sortedBy { it.date }.reversed().toMutableList()
                    view.displayPreviousOrders(sorted)
                }
            }

            override fun onError(reason: String) {
                view.hideOrdersProgressBar()
                view.displayMessage(reason)
            }
        })
    }

    fun updateUserDetails(name: String, phone: String, address: String) {
        view.showProfileProgressBar()
        authenticationService.setUserDisplayName(name, object : AuthService.AuthActionListener {
            override fun actionSuccessful(response: String) {
                databaseService.updateUserDetails(userDetails.id, name, phone, address, object : ClientDatabaseService.WriteListener {
                    override fun onSuccess() {
                        val newUserDetails = UserDetails(userDetails.id, userDetails.email, name, phone, address)

                        preferencesRepository.saveUserDetails(newUserDetails)
                        view.hideProfileProgressBar()
                        view.displayMessage("Profile updated")
                    }

                    override fun onError(reason: String) {
                        Timber.e(reason)
                        view.hideProfileProgressBar()
                        view.displayMessage(reason)
                    }
                })
            }

            override fun actionFailed(response: String) {
                view.displayMessage(response)
            }
        })
    }

}
