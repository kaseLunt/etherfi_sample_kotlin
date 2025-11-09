package com.babel.etherfiportfoliotracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babel.etherfiportfoliotracker.data.WalletAddress
import com.babel.etherfiportfoliotracker.data.WalletDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the Address List screen.
 * Manages the list of saved wallet addresses.
 *
 * @property walletDao Data access object for wallet operations
 */
@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val walletDao: WalletDao
) : ViewModel() {

    /**
     * StateFlow of all saved wallet addresses.
     * Automatically updates when the database changes.
     *
     * The UI can collect this to display the list of addresses.
     */
    val walletListState: StateFlow<List<WalletAddress>> = walletDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}