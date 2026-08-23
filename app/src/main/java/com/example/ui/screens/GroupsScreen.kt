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
import com.example.model.*
import com.example.ui.components.ComposerDialog
import com.example.ui.components.MeskotAvatar
import com.example.ui.components.TipModal
import com.example.ui.theme.*
import com.example.viewmodel.MeskotViewModel

@Composable
fun GroupsScreen(
    viewModel: MeskotViewModel,
    currentUser: User?,
    groups: List<Group>,
    myGroupIds: Set<String>,
    language: Language,
    onNavigateToGroup: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf("discover") } // discover, my_groups
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val myGroups = remember(groups, myGroupIds, searchQuery) {
        groups.filter {
            myGroupIds.contains(it.id) && (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true))
        }
    }

    val discoverGroups = remember(groups, searchQuery) {
        groups.filter {
            searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (language == Language.AM) "ቡድኖች" else "Groups",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MeskotInk
            )
            Button(
                onClick = { showCreateGroupDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(if (language == Language.AM) "+ አዲስ ቡድን" else "+ Create Group", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
            }
        }

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
                    label = if (language == Language.AM) "ያስሱ" else "Discover",
                    isSelected = selectedTab == "discover",
                    onClick = { selectedTab = "discover" }
                )
                TabPill(
                    label = "${if (language == Language.AM) "የእኔ ቡድኖች" else "My Groups"} (${myGroupIds.size})",
                    isSelected = selectedTab == "my_groups",
                    onClick = { selectedTab = "my_groups" }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (language == Language.AM) "ቡድኖችን ፈልግ…" else "Search groups…", fontSize = 13.sp) },
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

        // Content
        val targetList = if (selectedTab == "my_groups") myGroups else discoverGroups

        if (targetList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (language == Language.AM) "ምንም ቡድን አልተገኘም" else "No groups found",
                    color = MeskotMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(targetList, key = { it.id }) { group ->
                    val isMember = myGroupIds.contains(group.id)
                    GroupItemCard(
                        group = group,
                        isMember = isMember,
                        language = language,
                        onClick = { onNavigateToGroup(group.id) },
                        onToggleJoin = { viewModel.toggleGroupJoin(group.id) }
                    )
                }
            }
        }
    }

    // Create Group Modal Dialog
    if (showCreateGroupDialog) {
        var groupName by remember { mutableStateOf("") }
        var groupDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            title = {
                Text(
                    text = if (language == Language.AM) "አዲስ ቡድን ይፍጠሩ" else "Create a new group",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text(if (language == Language.AM) "የቡድን ስም" else "Group Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = groupDesc,
                        onValueChange = { groupDesc = it },
                        label = { Text(if (language == Language.AM) "መግለጫ" else "Description") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (groupName.isNotBlank()) {
                            viewModel.createGroup(groupName.trim(), groupDesc.trim())
                            showCreateGroupDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (language == Language.AM) "ፍጠር" else "Create", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateGroupDialog = false }) {
                    Text(if (language == Language.AM) "ይቅር" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun GroupItemCard(
    group: Group,
    isMember: Boolean,
    language: Language,
    onClick: () -> Unit,
    onToggleJoin: () -> Unit
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
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Group Cover Badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MeskotGoldDeep),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👪", fontSize = 22.sp)
                }
                Column {
                    Text(
                        text = group.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MeskotInk
                    )
                    Text(
                        text = "${group.memberCount} ${if (language == Language.AM) "አባላት" else "members"} · ${group.description}",
                        fontSize = 12.sp,
                        color = MeskotMuted,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isMember) {
                OutlinedButton(
                    onClick = onToggleJoin,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(if (language == Language.AM) "ተቀላቅለዋል" else "Joined", fontSize = 12.sp, color = MeskotInk)
                }
            } else {
                Button(
                    onClick = onToggleJoin,
                    colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(if (language == Language.AM) "ተቀላቀል" else "Join", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun GroupPageScreen(
    viewModel: MeskotViewModel,
    group: Group?,
    posts: List<Post>,
    currentUser: User?,
    isMember: Boolean,
    language: Language,
    onBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    if (group == null) return

    var showComposer by remember { mutableStateOf(false) }
    var tippingTargetPost by remember { mutableStateOf<Post?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskotPaper),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Group Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(MeskotGoldDeep)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
        }

        // Group Details Card
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MeskotCard),
                border = BorderStroke(1.dp, MeskotLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = group.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = MeskotInk
                            )
                            Text(
                                text = "${group.memberCount} ${if (language == Language.AM) "አባላት" else "members"}",
                                fontSize = 12.5.sp,
                                color = MeskotMuted
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleGroupJoin(group.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMember) MeskotPaper2 else MeskotGold
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (isMember) (if (language == Language.AM) "ተቀላቅለዋል" else "Joined") else (if (language == Language.AM) "ተቀላቀል" else "Join group"),
                                color = if (isMember) MeskotInk else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    if (group.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = group.description,
                            fontSize = 13.5.sp,
                            color = MeskotInk
                        )
                    }
                }
            }
        }

        // Post Composer trigger if member
        if (isMember) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MeskotCard),
                    border = BorderStroke(1.dp, MeskotLine),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .clickable { showComposer = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MeskotAvatar(
                            photoUrl = currentUser?.photoURL,
                            displayName = currentUser?.displayName ?: "User",
                            size = 36.dp
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MeskotPaper2)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (language == Language.AM) "ለዚህ ቡድን ያካፍሉ…" else "Share with the group…",
                                color = MeskotMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Group Posts
        if (posts.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (language == Language.AM) "ምንም የቡድን ልጥፎች የሉም" else "No posts in this group yet",
                        color = MeskotMuted
                    )
                }
            }
        } else {
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    currentUser = currentUser,
                    isSaved = false,
                    comments = emptyList(),
                    language = language,
                    onAuthorClick = { onNavigateToProfile(post.uid) },
                    onReaction = { r -> viewModel.setReaction(post.id, r, isGroup = true, groupId = group.id) },
                    onAddComment = { text, parentId -> viewModel.addComment(post.id, text, parentId, isGroup = true, groupId = group.id) },
                    onToggleCommentLike = { cId -> viewModel.toggleCommentLike(post.id, cId) },
                    onEditComment = { cId, newText -> viewModel.editComment(post.id, cId, newText) },
                    onDeleteComment = { cId -> viewModel.deleteComment(post.id, cId, isGroup = true, groupId = group.id) },
                    onShare = { viewModel.sharePost(post) },
                    onToggleSave = { viewModel.toggleSavePost(post.id) },
                    onTip = { tippingTargetPost = post },
                    onEditPost = { newText -> viewModel.editPost(post.id, newText, isGroup = true, groupId = group.id) },
                    onDeletePost = { viewModel.deletePost(post.id, isGroup = true, groupId = group.id) }
                )
            }
        }
    }

    if (showComposer) {
        ComposerDialog(
            currentUser = currentUser,
            language = language,
            initialGroupId = group.id,
            onDismiss = { showComposer = false },
            onSubmitPost = { text, media, bg, visibility, grpId ->
                viewModel.createPost(text, media, bg, visibility, grpId)
            }
        )
    }

    if (tippingTargetPost != null) {
        TipModal(
            post = tippingTargetPost!!,
            language = language,
            onDismiss = { tippingTargetPost = null },
            onConfirmTip = { amount ->
                tippingTargetPost?.let { viewModel.sendTip(it.id, amount, isGroup = true, groupId = group.id) }
            }
        )
    }
}
