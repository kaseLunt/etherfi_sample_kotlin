package com.babel.etherfiportfoliotracker.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.babel.etherfiportfoliotracker.ui.viewmodels.AddAddressViewModel

/**
 * Screen for adding a new wallet address.
 * Validates the address via Etherscan API before saving.
 *
 * @param navController Navigation controller for navigating between screens
 * @param viewModel ViewModel for managing address addition logic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    navController: NavController,
    viewModel: AddAddressViewModel = hiltViewModel()
) {
    // Collect state from ViewModel
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaveSuccess by viewModel.isSaveSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Local state for text fields
    var nickname by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Handle successful save - navigate back
    LaunchedEffect(isSaveSuccess) {
        if (isSaveSuccess) {
            viewModel.resetSaveSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Wallet Address") },
                // ▼▼▼ ADD THIS BLOCK ▼▼▼
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nickname TextField
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Nickname") },
                placeholder = { Text("e.g., My Main Wallet") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )

            // Address TextField
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Ethereum Address") },
                placeholder = { Text("0x...") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.saveAddress(
                        nickname = nickname.trim(),
                        address = address.trim()
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && nickname.isNotBlank() && address.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Address")
                }
            }

            // Loading Indicator (additional visual feedback)
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Validating address with Etherscan...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Error Message Display
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}