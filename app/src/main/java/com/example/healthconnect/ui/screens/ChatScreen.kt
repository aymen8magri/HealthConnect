package com.example.healthconnect.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.healthconnect.ui.components.BottomNavBar

data class Chat(val id: Int, val name: String, val lastMessage: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController) {
    val chats = remember {
        mutableStateListOf(
            Chat(1, "Jean", "Salut, on court demain ?"),
            Chat(2, "Marie", "Merci pour les conseils !"),
            Chat(3, "Support", "Ton compte est activé.")
        )
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chats) { chat ->
                ChatItem(chat = chat, onClick = {
                    // TODO: Naviguer vers la conversation spécifique (ex. chat.id)
                    println("Ouvrir conversation avec ${chat.name}")
                })
            }
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder pour une icône ou image de profil
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Text(chat.name[0].toString(), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(chat.name, style = MaterialTheme.typography.titleMedium)
                Text(chat.lastMessage, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            }
        }
    }
}