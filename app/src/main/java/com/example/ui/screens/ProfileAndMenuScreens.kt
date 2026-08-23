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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.MeskotAvatar
import com.example.ui.components.TipModal
import com.example.ui.theme.*
import com.example.viewmodel.AppView
import com.example.viewmodel.MeskotViewModel

@Composable
fun ProfileScreen(
    viewModel: MeskotViewModel,
    profileUser: User?,
    currentUser: User?,
    posts: List<Post>,
    friendsSet: Set<String>,
    friendRequests: List<FriendRequest>,
    savedPostIds: Set<String>,
    commentsMap: Map<String, List<Comment>>,
    language: Language,
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    if (profileUser == null) return

    val isMe = profileUser.uid == currentUser?.uid
    val isFriend = friendsSet.contains(profileUser.uid)
    val isOutgoing = friendRequests.any { it.fromUid == currentUser?.uid && it.toUid == profileUser.uid }
    val isIncoming = friendRequests.any { it.fromUid == profileUser.uid && it.toUid == currentUser?.uid }

    var selectedTab by remember { mutableStateOf("posts") } // posts, photos
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var tippingTargetPost by remember { mutableStateOf<Post?>(null) }

    val userPosts = remember(posts, profileUser) { posts.filter { it.uid == profileUser.uid } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskotPaper),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Banner & Back
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.linearGradient(listOf(MeskotGoldDeep, MeskotCrimson))
                    )
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

        // Profile Info Header Card
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp),
                colors = CardDefaults.cardColors(containerColor = MeskotCard),
                border = BorderStroke(1.dp, MeskotLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar overlapping banner
                    Box(modifier = Modifier.offset(y = (-45).dp)) {
                        MeskotAvatar(
                            photoUrl = profileUser.photoURL,
                            displayName = profileUser.displayName,
                            size = 86.dp,
                            showOnlineDot = true
                        )
                    }

                    Text(
                        text = profileUser.displayName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = MeskotInk,
                        modifier = Modifier.offset(y = (-30).dp)
                    )

                    if (profileUser.bio.isNotBlank()) {
                        Text(
                            text = profileUser.bio,
                            fontSize = 13.5.sp,
                            color = MeskotMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .offset(y = (-24).dp)
                                .padding(horizontal = 16.dp)
                        )
                    }

                    // Action buttons (Edit Profile or Friend Actions)
                    Row(
                        modifier = Modifier
                            .offset(y = (-12).dp)
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isMe) {
                            Button(
                                onClick = { showEditProfileDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MeskotInk),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (language == Language.AM) "መገለጫ አርትዕ" else "Edit profile", fontSize = 13.sp)
                            }
                        } else {
                            when {
                                isFriend -> {
                                    Button(
                                        onClick = { onNavigateToChat(profileUser.uid) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MeskotInk),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(if (language == Language.AM) "መልእክት ላክ" else "Send message", fontSize = 13.sp)
                                    }
                                    OutlinedButton(
                                        onClick = { viewModel.unfriend(profileUser.uid) },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(if (language == Language.AM) "አስወግድ" else "Unfriend", color = MeskotInk, fontSize = 13.sp)
                                    }
                                }
                                isOutgoing -> {
                                    OutlinedButton(
                                        onClick = { viewModel.cancelFriendRequest(profileUser.uid) },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(if (language == Language.AM) "ጥያቄ ተልኳል (ሰርዝ)" else "Requested (Cancel)", fontSize = 13.sp)
                                    }
                                }
                                isIncoming -> {
                                    Button(
                                        onClick = { viewModel.acceptFriendRequest(profileUser.uid) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(if (language == Language.AM) "ተቀበል" else "Accept request", fontSize = 13.sp)
                                    }
                                }
                                else -> {
                                    Button(
                                        onClick = { viewModel.sendFriendRequest(profileUser.uid) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(if (language == Language.AM) "ጓደኛ ጨምር" else "Add friend", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sub Tabs (Posts / Photos)
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MeskotCard,
                border = BorderStroke(1.dp, MeskotLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TabPill(
                        label = "${if (language == Language.AM) "ልጥፎች" else "Posts"} (${userPosts.size})",
                        isSelected = selectedTab == "posts",
                        onClick = { selectedTab = "posts" }
                    )
                    TabPill(
                        label = if (language == Language.AM) "ፎቶዎች" else "Photos",
                        isSelected = selectedTab == "photos",
                        onClick = { selectedTab = "photos" }
                    )
                }
            }
        }

        // Tab Content
        if (selectedTab == "posts") {
            if (userPosts.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(viewModel.t("noPosts"), color = MeskotMuted)
                    }
                }
            } else {
                items(userPosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        currentUser = currentUser,
                        isSaved = savedPostIds.contains(post.id),
                        comments = commentsMap[post.id].orEmpty(),
                        language = language,
                        onAuthorClick = {},
                        onReaction = { r -> viewModel.setReaction(post.id, r) },
                        onAddComment = { text, parentId -> viewModel.addComment(post.id, text, parentId) },
                        onToggleCommentLike = { cId -> viewModel.toggleCommentLike(post.id, cId) },
                        onEditComment = { cId, newText -> viewModel.editComment(post.id, cId, newText) },
                        onDeleteComment = { cId -> viewModel.deleteComment(post.id, cId) },
                        onShare = { viewModel.sharePost(post) },
                        onToggleSave = { viewModel.toggleSavePost(post.id) },
                        onTip = { tippingTargetPost = post },
                        onEditPost = { newText -> viewModel.editPost(post.id, newText) },
                        onDeletePost = { viewModel.deletePost(post.id) }
                    )
                }
            }
        } else {
            val allPhotos = userPosts.flatMap { it.media }
            if (allPhotos.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(viewModel.t("noMedia"), color = MeskotMuted)
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allPhotos.chunked(3).forEach { rowMedia ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowMedia.forEach { m ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                    ) {
                                        AsyncImage(model = m.url, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                    }
                                }
                                repeat(3 - rowMedia.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Modal
    if (showEditProfileDialog) {
        var newName by remember { mutableStateOf(currentUser?.displayName ?: "") }
        var newBio by remember { mutableStateOf(currentUser?.bio ?: "") }
        var newPhoto by remember { mutableStateOf(currentUser?.photoURL ?: "") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text(if (language == Language.AM) "መገለጫ አርትዕ" else "Edit profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text(if (language == Language.AM) "ሙሉ ስም" else "Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newBio,
                        onValueChange = { newBio = it },
                        label = { Text(if (language == Language.AM) "የህይወት ታሪክ (Bio)" else "Bio") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPhoto,
                        onValueChange = { newPhoto = it },
                        label = { Text(if (language == Language.AM) "የፎቶ ዩአርኤል" else "Photo URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(newName.trim(), newBio.trim(), newPhoto.trim())
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MeskotGold)
                ) {
                    Text(if (language == Language.AM) "አስቀምጥ" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text(if (language == Language.AM) "ይቅር" else "Cancel")
                }
            }
        )
    }

    if (tippingTargetPost != null) {
        TipModal(
            post = tippingTargetPost!!,
            language = language,
            onDismiss = { tippingTargetPost = null },
            onConfirmTip = { amount ->
                tippingTargetPost?.let { viewModel.sendTip(it.id, amount) }
            }
        )
    }
}

@Composable
fun SavedScreen(
    viewModel: MeskotViewModel,
    posts: List<Post>,
    savedPostIds: Set<String>,
    currentUser: User?,
    commentsMap: Map<String, List<Comment>>,
    language: Language,
    onNavigateToProfile: (String) -> Unit
) {
    val savedPosts = remember(posts, savedPostIds) {
        posts.filter { savedPostIds.contains(it.id) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Text(
            text = if (language == Language.AM) "የተቀመጡ ልጥፎች" else "Saved Posts",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = MeskotInk
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (savedPosts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (language == Language.AM) "ምንም የተቀመጡ ልጥፎች የሉም" else "No saved posts yet",
                    color = MeskotMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(savedPosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        currentUser = currentUser,
                        isSaved = true,
                        comments = commentsMap[post.id].orEmpty(),
                        language = language,
                        onAuthorClick = { onNavigateToProfile(post.uid) },
                        onReaction = { r -> viewModel.setReaction(post.id, r) },
                        onAddComment = { text, parentId -> viewModel.addComment(post.id, text, parentId) },
                        onToggleCommentLike = { cId -> viewModel.toggleCommentLike(post.id, cId) },
                        onEditComment = { cId, newText -> viewModel.editComment(post.id, cId, newText) },
                        onDeleteComment = { cId -> viewModel.deleteComment(post.id, cId) },
                        onShare = { viewModel.sharePost(post) },
                        onToggleSave = { viewModel.toggleSavePost(post.id) },
                        onTip = {},
                        onEditPost = { newText -> viewModel.editPost(post.id, newText) },
                        onDeletePost = { viewModel.deletePost(post.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun MenuScreen(
    viewModel: MeskotViewModel,
    currentUser: User?,
    language: Language,
    isAdmin: Boolean,
    onNavigate: (AppView) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MeskotCard),
                border = BorderStroke(1.dp, MeskotLine),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(AppView.PROFILE) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MeskotAvatar(
                        photoUrl = currentUser?.photoURL,
                        displayName = currentUser?.displayName ?: "User",
                        size = 52.dp,
                        showOnlineDot = true
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser?.displayName ?: "User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = if (language == Language.AM) "መገለጫዎን ይመልከቱ" else "View your profile",
                            fontSize = 12.sp,
                            color = MeskotMuted
                        )
                    }
                    Text("›", fontSize = 24.sp, color = MeskotMuted)
                }
            }
        }

        // Shortcuts Grid
        item {
            Text(
                text = if (language == Language.AM) "አቋራጮች" else "Shortcuts",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MeskotInk
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MenuShortcutCard(icon = "🏠", title = if (language == Language.AM) "የዜና ምግብ" else "Feed", onClick = { onNavigate(AppView.FEED) }, modifier = Modifier.weight(1f))
                MenuShortcutCard(icon = "👥", title = if (language == Language.AM) "ጓደኞች" else "Friends", onClick = { onNavigate(AppView.FRIENDS) }, modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MenuShortcutCard(icon = "💬", title = if (language == Language.AM) "መልዕክቶች" else "Messages", onClick = { onNavigate(AppView.MESSAGES) }, modifier = Modifier.weight(1f))
                MenuShortcutCard(icon = "👪", title = if (language == Language.AM) "ቡድኖች" else "Groups", onClick = { onNavigate(AppView.GROUPS) }, modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MenuShortcutCard(icon = "🖼️", title = if (language == Language.AM) "ፎቶዎች" else "Photos", onClick = { onNavigate(AppView.PHOTOS) }, modifier = Modifier.weight(1f))
                MenuShortcutCard(icon = "📊", title = if (language == Language.AM) "ዳሽቦርድ" else "Dashboard", onClick = { onNavigate(AppView.USER_DASHBOARD) }, modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MenuShortcutCard(icon = "🔖", title = if (language == Language.AM) "የተቀመጡ" else "Saved", onClick = { onNavigate(AppView.SAVED) }, modifier = Modifier.weight(1f))
                if (isAdmin) {
                    MenuShortcutCard(icon = "🛡️", title = if (language == Language.AM) "አስተዳዳሪ" else "Admin", onClick = { onNavigate(AppView.ADMIN_DASHBOARD) }, modifier = Modifier.weight(1f))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Log out button
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = MeskotPaper2),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("logout_btn")
            ) {
                Text(
                    text = if (language == Language.AM) "ውጣ (Log out)" else "Log out",
                    color = MeskotCrimson,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun MenuShortcutCard(
    icon: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MeskotCard),
        border = BorderStroke(1.dp, MeskotLine),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(icon, fontSize = 22.sp)
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MeskotInk
            )
        }
    }
}
