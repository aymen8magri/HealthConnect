package com.example.healthconnect.ui.admin.userDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.admin.validateUsers.UserRepository
import com.example.healthconnect.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _user.value = userRepository.getUserById(userId)
        }
    }

    fun verifyUser(userId: String) {
        viewModelScope.launch {
            userRepository.verifyUser(userId)
            _user.value = userRepository.getUserById(userId)
        }
    }

    fun rejectUser(userId: String) {
        viewModelScope.launch {
            userRepository.rejectUser(userId)
            _user.value = userRepository.getUserById(userId)
        }
    }
}