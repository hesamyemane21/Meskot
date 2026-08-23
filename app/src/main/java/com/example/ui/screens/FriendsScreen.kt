package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FriendRequest
import com.example.model.Language
import com.example.model.User
import com.example.ui.components.MeskotAvatar
import com.example.ui.theme.*
import com.example.viewmodel.MeskotViewModel

@Composable
fun FriendsScreen(
    viewModel: MeskotViewModel,
    currentUser: User?,
    allUsers: List<User>,
    friendsSet: Set<String>,
    friendRequests: List<FriendRequest>,
    language: Language,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("friends") } // friends, requests, suggestions
    var searchQuery by remember { mutableStateOf("") }

    val incomingRequests = remember(friendRequests, currentUser) {
        friendRequests.filter { it.toUid == currentUser?.uid }
    }
    val outgoingRequests = remember(friendRequests, currentUser) {
        friendRequests.filter { it.fromUid == currentUser?.uid }
    }

    val friendsList = remember(allUsers, friendsSet, searchQuery) {
        allUsers.filter {
            friendsSet.contains(it.uid) && (searchQuery.isBlank() || it.displayName.contains(searchQuery, ignoreCase = true))
        }
    }

    val suggestionsList = remember(allUsers, friendsSet, incomingRequests, outgoingRequests, currentUser, searchQuery) {
        val incomingUids = incomingRequests.map { it.fromUid }.toSet()
        val outgoingUids = outgoingRequests.map { it.toUid }.toSet()
        allUsers.filter {
            it.uid != currentUser?.uid &&
                    !friendsSet.contains(it.uid) &&
                    !incomingUids.contains(it.uid) &&
                    !outgoingUids.contains(it.uid) &&
                    (searchQuery.isBlank() || it.displayName.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Text(
            text = if (language == Language.AM) "ጓደኞች" else "Friends",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = MeskotInk
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Tabs
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MeskotCard,
            border = BorderStroke(1.dp, MeskotLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TabPill(
                    label = "${if (language == Language.AM) "ጓደኞች" else "All Friends"} (${friendsSet.size})",
                    isSelected = selectedTab == "friends",
                    onClick = { selectedTab = "friends" }
                )
                TabPill(
                    label = "${if (language == Language.AM) "ጥያቄዎች" else "Requests"} (${incomingRequests.size})",
                    isSelected = selectedTab == "requests",
                    onClick = { selectedTab = "requests" }
                )
                TabPill(
                    label = if (language == Language.AM) "ጥቆማዎች" else "Suggestions",
                    isSelected = selectedTab == "suggestions",
                    onClick = { selectedTab = "suggestions" }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (language == Language.AM) "ጓደኞችን ፈልግ…" else "Search people…", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MeskotMuted) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = MeskotGold,
                unfocusedBorderColor = MeskotLine
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            when (selectedTab) {
                "friends" -> {
                    if (friendsList.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (language == Language.AM) "ምንም ጓደኞች አልተገኙም" else "No friends found",
                                    color = MeskotMuted
                                )
                            }
                        }
                    } else {
                        items(friendsList, key = { it.uid }) { friend ->
                            FriendItemCard(
                                user = friend,
                                language = language,
                                onProfileClick = { onNavigateToProfile(friend.uid) },
                                onChatClick = { onNavigateToChat(friend.uid) },
                                onUnfriend = { viewModel.unfriend(friend.uid) }
                            )
                        }
                    }
                }
                "requests" -> {
                    if (incomingRequests.isEmpty() && outgoingRequests.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (language == Language.AM) "ምንም የጓደኝነት ጥያቄ የለም" else "No pending friend requests",
                                    color = MeskotMuted
                                )
                            }
                        }
                    } else {
                        if (incomingRequests.isNotEmpty()) {
                            item {
                                Text(
                                    text = if (language == Language.AM) "የመጡ ጥያቄዎች" else "Received Requests",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MeskotInk
                                )
                            }
                            items(incomingRequests, key = { it.id }) { req ->
                                val requester = allUsers.find { it.uid == req.fromUid }
                                if (requester != null) {
                                    IncomingRequestCard(
                                        user = requester,
                                        language = language,
                                        onAccept = { viewModel.acceptFriendRequest(requester.uid) },
                                        onDecline = { viewModel.declineFriendRequest(requester.uid) },
                                        onProfileClick = { onNavigateToProfile(requester.uid) }
                                    )
                                }
                            }
                        }

                        if (outgoingRequests.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (language == Language.AM) "የተላኩ ጥያቄዎች" else "Sent Requests",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MeskotMuted
                                )
                            }
                            items(outgoingRequests, key = { it.id }) { req ->
                                val target = allUsers.find { it.uid == req.toUid }
                                if (target != null) {
                                    OutgoingRequestCard(
                                        user = target,
                                        language = language,
                                        onCancel = { viewModel.cancelFriendRequest(target.uid) }
                                    )
                                }
                            }
                        }
                    }
                }
                "suggestions" -> {
                    if (suggestionsList.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (language == Language.AM) "ምንም አዲስ ጥቆማ የለም" else "No new suggestions",
                                    color = MeskotMuted
                                )
                            }
                        }
                    } else {
                        items(suggestionsList, key = { it.uid }) { user ->
                            SuggestionCard(
                                user = user,
                                language = language,
                                onAddFriend = { viewModel.sendFriendRequest(user.uid) },
                                onProfileClick = { onNavigateToProfile(user.uid) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MeskotInk else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MeskotMuted,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun FriendItemCard(
    user: User,
    language: Language,
    onProfileClick: () -> Unit,
    onChatClick: () -> Unit,
    onUnfriend: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MeskotCard),
        border = BorderStroke(1.dp, MeskotLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onProfileClick() }
            ) {
                MeskotAvatar(
                    photoUrl = user.photoURL,
                    displayName = user.displayName,
                    size = 44.dp,
                    showOnlineDot = true
                )
                Column {
                    Text(
                        text = user.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = MeskotInk
                    )
                    Text(
                        text = if (user.bio.isNotBlank()) user.bio else user.email,
                        fontSize = 11.5.sp,
                        color = MeskotMuted,
                        maxLines = 1
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onChatClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MeskotPaper2)
                ) {
                    Text("💬", fontSize = 16.sp)
                }

                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = MeskotMuted)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text(if (language == Language.AM) "አስወግድ" else "Unfriend", color = MeskotCrimson) },
                            onClick = {
                                showMenu = false
                                onUnfriend()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IncomingRequestCard(
    user: User,
    language: Language,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onProfileClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MeskotCard),
        border = BorderStroke(1.dp, MeskotLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onProfileClick() }
            ) {
                MeskotAvatar(photoUrl = user.photoURL, displayName = user.displayName, size = 44.dp)
                Column {
                    Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(user.email, fontSize = 11.5.sp, color = MeskotMuted)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(if (language == Language.AM) "ተቀበል" else "Accept", fontSize = 12.sp, color = Color.White)
                }
                OutlinedButton(
                    onClick = onDecline,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(if (language == Language.AM) "አትቀበል" else "Decline", fontSize = 12.sp, color = MeskotInk)
                }
            }
        }
    }
}

@Composable
fun OutgoingRequestCard(
    user: User,
    language: Language,
    onCancel: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MeskotCard),
        border = BorderStroke(1.dp, MeskotLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                MeskotAvatar(photoUrl = user.photoURL, displayName = user.displayName, size = 40.dp)
                Column {
                    Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(if (language == Language.AM) "ጥያቄ ተልኳል" else "Request sent", fontSize = 11.5.sp, color = MeskotMuted)
                }
            }

            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(if (language == Language.AM) "ሰርዝ" else "Cancel", fontSize = 12.sp, color = MeskotCrimson)
            }
        }
    }
}

@Composable
fun SuggestionCard(
    user: User,
    language: Language,
    onAddFriend: () -> Unit,
    onProfileClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MeskotCard),
        border = BorderStroke(1.dp, MeskotLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onProfileClick() }
            ) {
                MeskotAvatar(photoUrl = user.photoURL, displayName = user.displayName, size = 44.dp)
                Column {
                    Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(if (user.bio.isNotBlank()) user.bio else user.email, fontSize = 11.5.sp, color = MeskotMuted, maxLines = 1)
                }
            }

            Button(
                onClick = onAddFriend,
                colors = ButtonDefaults.buttonColors(containerColor = MeskotInk),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(if (language == Language.AM) "ጓደኛ ጨምር" else "Add Friend", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}
