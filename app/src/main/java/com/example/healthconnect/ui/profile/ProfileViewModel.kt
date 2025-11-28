package com.example.healthconnect.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.admin.validateUsers.UserRepository
import com.example.healthconnect.data.models.User
import com.example.healthconnect.ui.profile.logout.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    private val _editedUser = MutableStateFlow<User?>(null)
    val editedUser: StateFlow<User?> = _editedUser

    val isMed = mutableStateOf<Boolean?>(null)
    val isCoord = mutableStateOf<Boolean?>(null)
    val isVol = mutableStateOf<Boolean?>(null)
    val isAdmin = mutableStateOf<Boolean?>(null)

    init {
        viewModelScope.launch {
            // Load the current user from Firestore
            _currentUser.value = userRepository.getCurrentUser()

            // Load verifications
            isMed.value = userRepository.isCurrentUserDoctorVerified()
            isCoord.value = userRepository.isCurrentUserCoordinatorVerified()
            isAdmin.value = userRepository.isCurrentUserAdmin()
        }
    }

    fun startEditing() {
        _isEditing.value = true
        _editedUser.value = _currentUser.value?.copy()
    }

    fun cancelEditing() {
        _isEditing.value = false
        _editedUser.value = _currentUser.value?.copy()
    }

    fun updateUserField(block: (User) -> User) {
        _editedUser.value = _editedUser.value?.let(block)
    }

    fun saveChanges() {
        viewModelScope.launch {
            _editedUser.value?.let { updatedUser ->
                // save to Firestore
                userRepository.updateUser(updatedUser)
                _currentUser.value = updatedUser
                _isEditing.value = false
            }
        }
    }

    fun onLogoutClicked() {
        logoutUseCase.execute()
    }
}
