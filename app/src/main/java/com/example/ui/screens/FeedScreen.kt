package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MeskotViewModel

@Composable
fun FeedScreen(
    viewModel: MeskotViewModel,
    posts: List<Post>,
    currentUser: User?,
    allUsers: List<User>,
    friendsSet: Set<String>,
    savedPostIds: Set<String>,
    commentsMap: Map<String, List<Comment>>,
    language: Language,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    var showComposerDialog by remember { mutableStateOf(false) }
    var tippingTargetPost by remember { mutableStateOf<Post?>(null) }

    // Filter posts according to privacy: Public, or if Author is friend, or own post
    val visiblePosts = remember(posts, currentUser, friendsSet) {
        posts.filter { post ->
            when (post.visibility) {
                PostVisibility.PUBLIC -> true
                PostVisibility.FRIENDS -> post.uid == currentUser?.uid || friendsSet.contains(post.uid)
                PostVisibility.ONLY_ME -> post.uid == currentUser?.uid
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Active Friends Stories Strip
            item {
                ActiveFriendsStrip(
                    allUsers = allUsers.filter { it.uid != currentUser?.uid },
                    onUserClick = { onNavigateToProfile(it.uid) }
                )
            }

            // Pill Composer Trigger
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MeskotCard),
                    border = BorderStroke(1.dp, MeskotLine),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clickable { showComposerDialog = true }
                        .testTag("feed_composer_trigger")
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
                            size = 38.dp
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(22.dp))
                                .background(MeskotPaper2)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = if (language == Language.AM) "ምን እያሰቡ ነው?" else "What's on your mind?",
                                color = MeskotMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            if (visiblePosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🪟", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = viewModel.t("emptyFeed"),
                                color = MeskotMuted,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(visiblePosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        currentUser = currentUser,
                        isSaved = savedPostIds.contains(post.id),
                        comments = commentsMap[post.id].orEmpty(),
                        language = language,
                        onAuthorClick = { onNavigateToProfile(post.uid) },
                        onReaction = { reaction -> viewModel.setReaction(post.id, reaction) },
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
        }

        // Full Composer Dialog
        if (showComposerDialog) {
            ComposerDialog(
                currentUser = currentUser,
                language = language,
                onDismiss = { showComposerDialog = false },
                onSubmitPost = { text, media, bg, visibility, grpId ->
                    viewModel.createPost(text, media, bg, visibility, grpId)
                }
            )
        }

        // Tipping Modal
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
}

@Composable
fun ActiveFriendsStrip(
    allUsers: List<User>,
    onUserClick: (User) -> Unit
) {
    Surface(
        color = MeskotCard,
        border = BorderStroke(1.dp, MeskotLine),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(allUsers, key = { it.uid }) { user ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(58.dp)
                        .clickable { onUserClick(user) }
                ) {
                    MeskotAvatar(
                        photoUrl = user.photoURL,
                        displayName = user.displayName,
                        size = 52.dp,
                        showOnlineDot = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.displayName.split(" ").firstOrNull() ?: user.displayName,
                        fontSize = 11.sp,
                        color = MeskotInk,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    currentUser: User?,
    isSaved: Boolean,
    comments: List<Comment>,
    language: Language,
    onAuthorClick: () -> Unit,
    onReaction: (ReactionType) -> Unit,
    onAddComment: (text: String, parentId: String?) -> Unit,
    onToggleCommentLike: (String) -> Unit,
    onEditComment: (String, String) -> Unit,
    onDeleteComment: (String) -> Unit,
    onShare: () -> Unit,
    onToggleSave: () -> Unit,
    onTip: () -> Unit,
    onEditPost: (String) -> Unit,
    onDeletePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCommentsExpanded by remember { mutableStateOf(false) }
    var showReactionPicker by remember { mutableStateOf(false) }
    var showPostMenu by remember { mutableStateOf(false) }
    var isEditingPost by remember { mutableStateOf(false) }
    var editedPostText by remember { mutableStateOf(post.text) }
    var isBodyClamped by remember { mutableStateOf(true) }

    val isAuthor = currentUser?.uid == post.uid
    val myReaction = currentUser?.uid?.let { post.reactions[it] }

    val reactionCounts = remember(post.reactions) {
        val map = mutableMapOf<ReactionType, Int>()
        post.reactions.values.forEach { r -> map[r] = (map[r] ?: 0) + 1 }
        map
    }
    val totalReactions = post.reactions.size
    val topReactionTypes = reactionCounts.entries.sortedByDescending { it.value }.take(3).map { it.key }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MeskotCard),
        border = BorderStroke(1.dp, MeskotLine),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("post_card_${post.id}")
    ) {
        Column {
            // Ethiopian Cross Architectural Accent line at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.linearGradient(listOf(MeskotGold, MeskotCrimson, MeskotGoldDeep))
                    )
            )

            // Post Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onAuthorClick() }
                ) {
                    MeskotAvatar(
                        photoUrl = post.authorPhoto,
                        displayName = post.authorName,
                        size = 40.dp
                    )
                    Column {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = MeskotInk
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = formatTimeAgo(post.createdAtMillis, language),
                                fontSize = 11.5.sp,
                                color = MeskotMuted
                            )
                            if (post.editedAtMillis != null) {
                                Text("· edited", fontSize = 11.sp, color = MeskotMuted)
                            }
                        }
                    }
                }

                // Author Menu (Edit / Delete)
                if (isAuthor) {
                    Box {
                        IconButton(onClick = { showPostMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = MeskotMuted)
                        }
                        DropdownMenu(
                            expanded = showPostMenu,
                            onDismissRequest = { showPostMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (language == Language.AM) "አርትዕ" else "Edit") },
                                onClick = {
                                    showPostMenu = false
                                    editedPostText = post.text
                                    isEditingPost = true
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (language == Language.AM) "ሰርዝ" else "Delete", color = MeskotCrimson) },
                                onClick = {
                                    showPostMenu = false
                                    onDeletePost()
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MeskotCrimson) }
                            )
                        }
                    }
                }
            }

            // Shared Post Banner Tag
            if (post.sharedFrom != null) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🔁", fontSize = 12.sp)
                    Text(
                        text = if (language == Language.AM) "ልጥፍ አጋርቷል" else "shared a post",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MeskotMuted
                    )
                }
            }

            // Edit Post Inline Form
            if (isEditingPost) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = editedPostText,
                        onValueChange = { editedPostText = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { isEditingPost = false }) {
                            Text(if (language == Language.AM) "ይቅር" else "Cancel")
                        }
                        Button(
                            onClick = {
                                onEditPost(editedPostText)
                                isEditingPost = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MeskotGold)
                        ) {
                            Text(if (language == Language.AM) "አስቀምጥ" else "Save")
                        }
                    }
                }
            } else {
                // Post Body / Text
                if (post.bgColor != null) {
                    val brush = when (post.bgColor) {
                        "linear-gradient(135deg,#8C2F39,#B8863A)" -> Brush.linearGradient(GradientSunset)
                        "linear-gradient(135deg,#1B2A22,#2A4838)" -> Brush.linearGradient(GradientForest)
                        "linear-gradient(135deg,#335577,#5A8FBE)" -> Brush.linearGradient(GradientNavy)
                        "linear-gradient(135deg,#4A3B5C,#8C5CA8)" -> Brush.linearGradient(GradientAmethyst)
                        "linear-gradient(135deg,#B8863A,#E8B94F)" -> Brush.linearGradient(GradientAmber)
                        else -> Brush.linearGradient(GradientSunset)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.text,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                } else if (post.text.isNotBlank()) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                        Text(
                            text = post.text,
                            fontSize = 14.5.sp,
                            lineHeight = 22.sp,
                            color = MeskotInk,
                            maxLines = if (isBodyClamped && post.text.length > 140) 3 else Int.MAX_VALUE,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (post.text.length > 140) {
                            Text(
                                text = if (isBodyClamped) (if (language == Language.AM) "… ተጨማሪ ይመልከቱ" else "… See more") else (if (language == Language.AM) "አሳንስ" else "See less"),
                                fontWeight = FontWeight.Bold,
                                color = MeskotMuted,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .clickable { isBodyClamped = !isBodyClamped }
                                    .padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Shared Post Box
                if (post.sharedFrom != null) {
                    val sf = post.sharedFrom
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MeskotPaper),
                        border = BorderStroke(1.dp, MeskotLine),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MeskotAvatar(photoUrl = sf.authorPhoto, displayName = sf.authorName, size = 28.dp)
                                Column {
                                    Text(sf.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(formatTimeAgo(sf.createdAtMillis, language), fontSize = 10.5.sp, color = MeskotMuted)
                                }
                            }
                            if (sf.text.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(sf.text, fontSize = 13.5.sp)
                            }
                            if (sf.media.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                PostMediaGrid(media = sf.media)
                            }
                        }
                    }
                }

                // Media Gallery Grid
                if (post.media.isNotEmpty()) {
                    PostMediaGrid(media = post.media)
                }
            }

            // Reactions & Tips Summary Line
            if (totalReactions > 0 || post.tipTotal > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (totalReactions > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                                topReactionTypes.forEach { r ->
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(1.dp, MeskotLine, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(r.emoji, fontSize = 11.sp)
                                    }
                                }
                            }
                            Text(
                                text = "$totalReactions ${if (language == Language.AM) "ውዳሴዎች" else "likes"}",
                                fontSize = 12.sp,
                                color = MeskotMuted
                            )
                        }
                    }
                    if (post.tipTotal > 0) {
                        Text(
                            text = "💰 ${post.tipTotal.toInt()} ${if (language == Language.AM) "ብር ተገኝቷል" else "ETB received"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MeskotGoldDeep
                        )
                    }
                }
            }

            HorizontalDivider(color = MeskotLine)

            // Action Buttons Bar
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like / React Button
                    TextButton(
                        onClick = {
                            if (myReaction != null) {
                                onReaction(myReaction) // Toggle off
                            } else {
                                onReaction(ReactionType.LIKE)
                            }
                        },
                        modifier = Modifier.testTag("post_like_btn_${post.id}")
                    ) {
                        Text(myReaction?.emoji ?: "👍", fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (myReaction != null) (if (language == Language.AM) myReaction.labelAm else myReaction.labelEn) else (if (language == Language.AM) "ውደድ" else "Like"),
                            fontWeight = FontWeight.SemiBold,
                            color = if (myReaction != null) MeskotCrimson else MeskotMuted,
                            fontSize = 13.sp
                        )
                    }

                    // Comment Button
                    TextButton(
                        onClick = { isCommentsExpanded = !isCommentsExpanded },
                        modifier = Modifier.testTag("post_comment_btn_${post.id}")
                    ) {
                        Text("💬", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${if (language == Language.AM) "አስተያየት" else "Comment"}${if (post.commentCount > 0) " · ${post.commentCount}" else ""}",
                            fontWeight = FontWeight.SemiBold,
                            color = MeskotMuted,
                            fontSize = 13.sp
                        )
                    }

                    // Share Button
                    TextButton(onClick = onShare) {
                        Text("🔁", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == Language.AM) "አጋራ" else "Share",
                            fontWeight = FontWeight.SemiBold,
                            color = MeskotMuted,
                            fontSize = 13.sp
                        )
                    }

                    // Save Button
                    IconButton(onClick = onToggleSave) {
                        Text(
                            text = if (isSaved) "🔖" else "🏷️",
                            fontSize = 16.sp
                        )
                    }

                    // Tip / Support Button
                    if (!isAuthor) {
                        IconButton(onClick = onTip) {
                            Text("💰", fontSize = 16.sp)
                        }
                    }
                }

                // Reaction Picker floating bar
                if (showReactionPicker) {
                    ReactionPickerBar(
                        onSelectReaction = {
                            onReaction(it)
                            showReactionPicker = false
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = 10.dp, y = (-42).dp)
                    )
                }
            }

            // Expandable Comments Section
            AnimatedVisibility(visible = isCommentsExpanded) {
                CommentsSection(
                    comments = comments,
                    language = language,
                    currentUser = currentUser,
                    onAddComment = onAddComment,
                    onToggleLike = onToggleCommentLike,
                    onEditComment = onEditComment,
                    onDeleteComment = onDeleteComment
                )
            }
        }
    }
}

@Composable
fun PostMediaGrid(media: List<MediaItem>) {
    when (media.size) {
        1 -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 340.dp)
            ) {
                AsyncImage(
                    model = media[0].url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(260.dp)
                )
                if (media[0].type == MediaType.VIDEO) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Text("▶ Video", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }
        2 -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                media.forEach { m ->
                    AsyncImage(
                        model = m.url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
        else -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    media.take(2).forEach { m ->
                        AsyncImage(
                            model = m.url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    media.drop(2).take(2).forEach { m ->
                        AsyncImage(
                            model = m.url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentsSection(
    comments: List<Comment>,
    language: Language,
    currentUser: User?,
    onAddComment: (text: String, parentId: String?) -> Unit,
    onToggleLike: (String) -> Unit,
    onEditComment: (String, String) -> Unit,
    onDeleteComment: (String) -> Unit
) {
    var commentInputText by remember { mutableStateOf("") }
    var replyingToCommentId by remember { mutableStateOf<String?>(null) }
    var replyInputText by remember { mutableStateOf("") }

    val topLevelComments = remember(comments) { comments.filter { it.parentId == null } }
    val repliesByParent = remember(comments) {
        comments.filter { it.parentId != null }.groupBy { it.parentId!! }
    }

    Surface(
        color = MeskotPaper,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Comment input bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = commentInputText,
                    onValueChange = { commentInputText = it },
                    placeholder = { Text(if (language == Language.AM) "አስተያየት ይጻፉ…" else "Write a comment…", fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = MeskotGold,
                        unfocusedBorderColor = MeskotLine
                    ),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (commentInputText.isNotBlank()) {
                            onAddComment(commentInputText.trim(), null)
                            commentInputText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MeskotInk),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(if (language == Language.AM) "ላክ" else "Send", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comments List
            topLevelComments.forEach { comment ->
                CommentRow(
                    comment = comment,
                    currentUser = currentUser,
                    language = language,
                    onToggleLike = { onToggleLike(comment.id) },
                    onReplyClick = {
                        replyingToCommentId = if (replyingToCommentId == comment.id) null else comment.id
                    },
                    onEdit = { onEditComment(comment.id, it) },
                    onDelete = { onDeleteComment(comment.id) }
                )

                // Reply composer
                if (replyingToCommentId == comment.id) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp, top = 4.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = replyInputText,
                            onValueChange = { replyInputText = it },
                            placeholder = { Text(if (language == Language.AM) "መልስ ይጻፉ…" else "Write a reply…", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (replyInputText.isNotBlank()) {
                                    onAddComment(replyInputText.trim(), comment.id)
                                    replyInputText = ""
                                    replyingToCommentId = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (language == Language.AM) "መልስ" else "Reply", fontSize = 11.sp)
                        }
                    }
                }

                // Replies List
                val replies = repliesByParent[comment.id].orEmpty()
                replies.forEach { reply ->
                    Box(modifier = Modifier.padding(start = 28.dp)) {
                        CommentRow(
                            comment = reply,
                            currentUser = currentUser,
                            language = language,
                            isReply = true,
                            onToggleLike = { onToggleLike(reply.id) },
                            onReplyClick = {},
                            onEdit = { onEditComment(reply.id, it) },
                            onDelete = { onDeleteComment(reply.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentRow(
    comment: Comment,
    currentUser: User?,
    language: Language,
    isReply: Boolean = false,
    onToggleLike: () -> Unit,
    onReplyClick: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedText by remember { mutableStateOf(comment.text) }

    val isMine = currentUser?.uid == comment.uid
    val isLiked = currentUser?.uid?.let { comment.likes[it] } == true
    val likeCount = comment.likes.size

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        MeskotAvatar(
            photoUrl = comment.authorPhoto,
            displayName = comment.authorName,
            size = if (isReply) 26.dp else 32.dp
        )

        Column(modifier = Modifier.weight(1f)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, MeskotLine)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = comment.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = MeskotInk
                        )
                        if (isMine) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "✏️",
                                    fontSize = 11.sp,
                                    modifier = Modifier.clickable { isEditing = !isEditing }
                                )
                                Text(
                                    text = "🗑️",
                                    fontSize = 11.sp,
                                    modifier = Modifier.clickable { onDelete() }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    if (isEditing) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            OutlinedTextField(
                                value = editedText,
                                onValueChange = { editedText = it },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    onEdit(editedText)
                                    isEditing = false
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("OK")
                            }
                        }
                    } else {
                        Text(
                            text = comment.text,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MeskotInk
                        )
                    }
                }
            }

            // Comment Actions row
            Row(
                modifier = Modifier.padding(start = 6.dp, top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isLiked) "❤️ " else ""}${if (language == Language.AM) "ውደድ" else "Like"}${if (likeCount > 0) " · $likeCount" else ""}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLiked) MeskotCrimson else MeskotMuted,
                    modifier = Modifier.clickable { onToggleLike() }
                )
                if (!isReply) {
                    Text(
                        text = if (language == Language.AM) "መልስ" else "Reply",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MeskotMuted,
                        modifier = Modifier.clickable { onReplyClick() }
                    )
                }
                Text(
                    text = formatTimeAgo(comment.createdAtMillis, language),
                    fontSize = 10.5.sp,
                    color = MeskotMuted
                )
            }
        }
    }
}
