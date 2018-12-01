package com.supnet.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.supnet.UserState
import com.supnet.data.supnet.SupnetRepository
import io.reactivex.Observable

class RootViewModelFactory(
    private val supnetRepository: SupnetRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RootViewModel(
            Observable.just(UserState.SIGNED_OUT)
        ) as T
    }

}