package com.supnet.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EntryViewModelFactory(
   private val navigator: EntryFlowNavigator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EntryViewModel(navigator) as T
    }

}