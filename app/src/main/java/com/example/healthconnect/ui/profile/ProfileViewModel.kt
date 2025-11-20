package com.example.healthconnect.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.models.User
import com.example.healthconnect.data.admin.validateUsers.UserRepositoryImpl
import com.example.healthconnect.ui.profile.logout.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val userRepository: UserRepositoryImpl
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    private val _editedUser = MutableStateFlow<User?>(null)
    val editedUser: StateFlow<User?> = _editedUser

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            // Récupérer l'utilisateur du repository singleton
            var user = userRepository.getCurrentUser()
            if (user == null) {
                // Pour test local : forcer user_1 si aucun utilisateur n'est défini
                Log.d("ProfileViewModel", "Aucun utilisateur courant trouvé, forçage vers user_1 pour test")
                userRepository.setCurrentUser("user_2")
                user = userRepository.getCurrentUser()
            } else {
                Log.d("ProfileViewModel", "Utilisateur courant trouvé: ${user.uid}")
            }

            _currentUser.value = user
            _editedUser.value = user
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
                // TODO: Save to Firestore/backend
                _currentUser.value = updatedUser
                _isEditing.value = false
            }
        }
    }

    /**
     * Appelé lorsque l'utilisateur clique sur le bouton de déconnexion.
     */
    fun onLogoutClicked() {
        logoutUseCase.execute()
    }

    // Debug helper: force current user for testing
    fun forceSetCurrentUserForTest(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.setCurrentUser(userId)
                val user = userRepository.getCurrentUser()
                Log.d("ProfileViewModel", "forceSetCurrentUserForTest -> set to: ${user?.uid}")
                _currentUser.value = user
                _editedUser.value = user
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error forcing current user: ${e.message}")
            }
        }
    }
}