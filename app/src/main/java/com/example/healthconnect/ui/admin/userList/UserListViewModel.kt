package com.example.healthconnect.ui.admin.userList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthconnect.data.admin.validateUsers.UserRepository
import com.example.healthconnect.data.models.Roles
import com.example.healthconnect.data.models.Status
import com.example.healthconnect.data.models.User
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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _roleFilter = MutableStateFlow<Roles?>(null)
    val roleFilter: StateFlow<Roles?> = _roleFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow<Status?>(null)
    val statusFilter: StateFlow<Status?> = _statusFilter.asStateFlow()

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
        val byStatus = status?.let { s -> byRole.filter { it.status == s } } ?: byRole
        byStatus
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _allUsers.value = userRepository.getAllUsers()
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
        loadUsers()
    }
}