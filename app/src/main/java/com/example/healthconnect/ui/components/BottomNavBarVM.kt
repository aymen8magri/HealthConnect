package com.example.healthconnect.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.admin.validateUsers.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomNavViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val isAdmin = mutableStateOf<Boolean?>(null)

    init {
        viewModelScope.launch {
            isAdmin.value = userRepository.isCurrentUserAdmin()
        }
    }
}
