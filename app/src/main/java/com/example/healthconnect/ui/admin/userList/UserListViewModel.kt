package com.example.healthconnect.ui.admin.userList

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.admin.validateUsers.UserRepository
import com.example.healthconnect.data.models.Participation
import com.example.healthconnect.data.models.Roles
import com.example.healthconnect.data.models.Status
import com.example.healthconnect.data.models.User
import com.example.healthconnect.data.participation.ParticipationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val participationRepo: ParticipationRepository,
    private val  savedStateHandle: SavedStateHandle

) : ViewModel() {
    val missionId: String? = savedStateHandle["missionId"]

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _roleFilter = MutableStateFlow<Roles?>(null)
    val roleFilter: StateFlow<Roles?> = _roleFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow<Status?>(null)
    val statusFilter: StateFlow<Status?> = _statusFilter.asStateFlow()
    var users: List<User> =emptyList()


    var isCoordinator: Boolean = false


    var isAdmin : Boolean=false


    val filteredUsers: StateFlow<List<User>> = combine(
        searchQuery,
        roleFilter,
        statusFilter,
        allUsers
    ) { query, role, status, users ->
        val byQuery = if (query.isBlank()) users else users.filter {
            it.fullName.contains(query, ignoreCase = true) || it.email.contains(
                query,
                ignoreCase = true
            )
        }
        val byRole = role?.let { r -> byQuery.filter { it.role == r } } ?: byQuery
        val byStatus = status?.let { s -> byRole.filter { it.statusRole == s } } ?: byRole
        byStatus
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadData(missionId)
    }

    fun loadData(missionId: String?) {
        viewModelScope.launch {
            try {
                isCoordinator = userRepo.isCurrentUserCoordinatorVerified()
                isAdmin = userRepo.isCurrentUserAdmin()

                Log.d("UserListViewModel", "Role check → isAdmin=$isAdmin, isCoordinator=$isCoordinator")
                Log.d("UserListViewModel", "Received missionId = $missionId")

                val userList = when {
                    isAdmin -> {
                        Log.d("UserListViewModel", "Loading all users (ADMIN)")
                        userRepo.getAllUsers()
                    }

                    isCoordinator && !missionId.isNullOrEmpty() -> {
                        Log.d("UserListViewModel", "Coordinator → loading participations for mission $missionId")

                        val participations = participationRepo.getParticipationByMissionId(missionId)
                        Log.d("UserListViewModel", "Participations found: ${participations.size}")
                        participations.forEach {
                            Log.d("UserListViewModel", "Participation → userId=${it.userId}")
                        }

                        val usersFromParticipations = participations.mapNotNull { participation ->
                            val user = userRepo.getUserById(participation.userId)
                            Log.d("UserListViewModel", "Fetching user for ${participation.userId} → $user")
                            user
                        }

                        Log.d("UserListViewModel", "Users fetched from participations: ${usersFromParticipations.size}")
                        usersFromParticipations
                    }

                    else -> {
                        Log.d("UserListViewModel", "Not admin and not coordinator or missionId null/empty")
                        emptyList()
                    }
                }

                _allUsers.value = userList
                users = userList

            } catch (e: Exception) {
                Log.e("UserListViewModel", "Error in loadData: ${e.message}")
                _allUsers.value = emptyList()
            }
        }
    }



    fun validateParticipation(participationId: String) {
        viewModelScope.launch {
            participationRepo.validateParticipation(participationId)
        }
    }

    fun rejectParticipation(participationId: String) {
        viewModelScope.launch {
            participationRepo.rejectParticipation(participationId)
        }
    }
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onRoleFilterChange(newRole: Roles?) {
        _roleFilter.value = newRole
    }

    fun onStatusFilterChange(newStatus: Status?) {
        _statusFilter.value = newStatus
    }

    // refresh list after potential updates
    fun refresh() {
        loadData(missionId)
    }
}