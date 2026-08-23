package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.model.ChatMessage
import com.example.model.Conversation
import com.example.model.Language
import com.example.model.User
import com.example.ui.components.MeskotAvatar
import com.example.ui.components.formatTimeAgo
import com.example.ui.theme.*
import com.example.viewmodel.MeskotViewModel

@Composable
fun MessagesScreen(
    viewModel: MeskotViewModel,
    currentUser: User?,
    conversations: List<Conversation>,
    allUsers: List<User>,
    language: Language,
    onNavigateToChat: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val userMap = remember(allUsers) { allUsers.associateBy { it.uid } }

    val filteredConversations = remember(conversations, allUsers, searchQuery, currentUser) {
        conversations.filter { convo ->
            val otherUid = convo.participants.firstOrNull { it != currentUser?.uid }
            val otherUser = otherUid?.let { userMap[it] }
            searchQuery.isBlank() || (otherUser?.displayName?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Text(
            text = if (language == Language.AM) "መልእክቶች" else "Messages",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = MeskotInk
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Active Contacts Strip
        ActiveFriendsStrip(
            allUsers = allUsers.filter { it.uid != currentUser?.uid },
            onUserClick = { onNavigateToChat(it.uid) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Search conversations
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (language == Language.AM) "ውይይቶችን ፈልግ…" else "Search conversations…", fontSize = 13.sp) },
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

        if (filteredConversations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (language == Language.AM) "ምንም ውይይት የለም" else "No messages yet. Start a chat with a friend!",
                    color = MeskotMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredConversations, key = { it.id }) { convo ->
                    val otherUid = convo.participants.firstOrNull { it != currentUser?.uid } ?: ""
                    val otherUser = userMap[otherUid]
                    val unread = convo.unreadCounts[currentUser?.uid] ?: 0

                    if (otherUser != null) {
                        ConversationItemCard(
                            otherUser = otherUser,
                            lastMessage = convo.lastMessage,
                            lastMessageTime = convo.lastMessageAtMillis,
                            unreadCount = unread,
                            language = language,
                            onClick = { onNavigateToChat(otherUser.uid) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItemCard(
    otherUser: User,
    lastMessage: String,
    lastMessageTime: Long,
    unreadCount: Int,
    language: Language,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MeskotCard),
        border = BorderStroke(1.dp, MeskotLine),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                MeskotAvatar(
                    photoUrl = otherUser.photoURL,
                    displayName = otherUser.displayName,
                    size = 46.dp,
                    showOnlineDot = true
                )
                Column {
                    Text(
                        text = otherUser.displayName,
                        fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 14.5.sp,
                        color = MeskotInk
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = lastMessage.ifBlank { "..." },
                        fontSize = 12.5.sp,
                        fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (unreadCount > 0) MeskotInk else MeskotMuted,
                        maxLines = 1
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formatTimeAgo(lastMessageTime, language),
                    fontSize = 10.5.sp,
                    color = MeskotMuted
                )
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MeskotCrimson)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    viewModel: MeskotViewModel,
    currentUser: User?,
    otherUser: User?,
    messages: List<ChatMessage>,
    language: Language,
    onBack: () -> Unit
) {
    if (otherUser == null) return

    val convoId = listOf(currentUser?.uid ?: "", otherUser.uid).sorted().joinToString("_")
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskotPaper)
    ) {
        // Chat Header
        Surface(
            color = MeskotCard,
            border = BorderStroke(1.dp, MeskotLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MeskotInk)
                    }
                    MeskotAvatar(
                        photoUrl = otherUser.photoURL,
                        displayName = otherUser.displayName,
                        size = 36.dp,
                        showOnlineDot = true
                    )
                    Column {
                        Text(
                            text = otherUser.displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = MeskotInk
                        )
                        Text(
                            text = if (language == Language.AM) "በመስመር ላይ" else "Online",
                            fontSize = 10.5.sp,
                            color = MeskotOnlineGreen
                        )
                    }
                }

                // Audio & Video Call Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { viewModel.startCall(otherUser.uid, "audio") }) {
                        Text("📞", fontSize = 18.sp)
                    }
                    IconButton(onClick = { viewModel.startCall(otherUser.uid, "video") }) {
                        Text("📹", fontSize = 18.sp)
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.fromUid == currentUser?.uid
                ChatMessageBubble(
                    message = msg,
                    isMe = isMe,
                    language = language,
                    onEdit = { newText -> viewModel.editChatMessage(convoId, msg.id, newText) },
                    onDelete = { viewModel.deleteChatMessage(convoId, msg.id) }
                )
            }
        }

        // Message Input Bar
        Surface(
            color = MeskotCard,
            border = BorderStroke(1.dp, MeskotLine),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(if (language == Language.AM) "መልእክት ይጻፉ…" else "Type a message…", fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = MeskotGold,
                        unfocusedBorderColor = MeskotLine
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendChatMessage(convoId, otherUser.uid, inputText.trim())
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MeskotInk)
                        .testTag("send_msg_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isMe: Boolean,
    language: Language,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(message.text) }

    // Call log banner
    if (message.callLog != null) {
        val cl = message.callLog
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MeskotPaper2,
                border = BorderStroke(1.dp, MeskotLine)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(if (cl.callType == "video") "📹" else "📞", fontSize = 13.sp)
                    Text(
                        text = "${if (cl.callType == "video") "Video Call" else "Audio Call"} · ${cl.callDurationSec}s",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MeskotMuted
                    )
                }
            }
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 16.dp
                ),
                color = if (isMe) MeskotInk else Color.White,
                border = if (isMe) null else BorderStroke(1.dp, MeskotLine),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editText,
                            onValueChange = { editText = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { isEditing = false }) { Text("Cancel", fontSize = 11.sp) }
                            Button(
                                onClick = {
                                    onEdit(editText)
                                    isEditing = false
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) { Text("Save", fontSize = 11.sp) }
                        }
                    } else {
                        Text(
                            text = message.text,
                            color = if (isMe) Color.White else MeskotInk,
                            fontSize = 14.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            // Message subline
            Row(
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimeAgo(message.createdAtMillis, language),
                    fontSize = 9.5.sp,
                    color = MeskotMuted
                )
                if (isMe && !isEditing) {
                    Text(
                        text = "✏️",
                        fontSize = 9.sp,
                        modifier = Modifier.clickable { isEditing = true }
                    )
                    Text(
                        text = "🗑️",
                        fontSize = 9.sp,
                        modifier = Modifier.clickable { onDelete() }
                    )
                }
            }
        }
    }
}
