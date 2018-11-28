package com.marknkamau.justjava.ui.checkout

import com.marknjunge.core.auth.AuthService
import com.marknjunge.core.data.firebase.ClientDatabaseService
import com.marknkamau.justjava.data.local.CartDao
import com.marknkamau.justjava.data.local.PreferencesRepository
import com.marknjunge.core.model.OrderItem
import com.marknjunge.core.model.Order
import com.marknkamau.justjava.data.models.CartItem
import com.marknkamau.justjava.data.models.toOrderItem
import com.marknkamau.justjava.ui.BasePresenter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

internal class CheckoutPresenter(private val activityView: CheckoutView,
                                 private val auth: AuthService,
                                 private val preferences: PreferencesRepository,
                                 private val database: ClientDatabaseService,
                                 private val cart: CartDao) : BasePresenter() {
    fun getSignInStatus() {
        if (auth.isSignedIn()) {
            activityView.setDisplayToLoggedIn(preferences.getUserDetails())
        } else {
            activityView.setDisplayToLoggedOut()
        }
    }

    fun placeOrder(orderId: String, address: String, comments: String, payCash: Boolean) {
        val paymentMethod = if (payCash) "cash" else "mpesa"
        val order = Order(orderId, auth.getCurrentUser().userId, 0, 0, address, comments, paymentMethod = paymentMethod)
        activityView.showUploadBar()

        disposables.add(cart.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { items: MutableList<CartItem> ->
                            placeOrderInternal(items, order)
                        },
                        onError = { t: Throwable? ->
                            Timber.e(t)
                        }
                ))

    }

    private fun placeOrderInternal(items: MutableList<CartItem>, order: Order) {
        val itemsCount = items.size
        var total = 0
        items.forEach { item -> total += item.itemPrice }

        order.itemsCount = itemsCount
        order.totalPrice = total

        val orderItems = mutableListOf<OrderItem>()
        items.forEach {
            orderItems.add(it.toOrderItem())
        }

        database.placeNewOrder(order, orderItems, object : ClientDatabaseService.WriteListener {
            override fun onSuccess() {
                disposables.add(Completable.fromCallable { cart.deleteAll() }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                                onComplete = {
                                    activityView.hideUploadBar()
                                    activityView.displayMessage("Order placed")
                                    activityView.finishActivity(order)
                                },
                                onError = { t: Throwable? ->
                                    Timber.e(t)
                                    activityView.hideUploadBar()
                                    activityView.displayMessage(t?.message)
                                }
                        ))

            }

            override fun onError(reason: String) {
                activityView.hideUploadBar()
                activityView.displayMessage(reason)
            }
        })

    }
}
