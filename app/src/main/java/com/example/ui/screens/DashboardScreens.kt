package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.MeskotAvatar
import com.example.ui.theme.*
import com.example.viewmodel.AppView
import com.example.viewmodel.MeskotViewModel

@Composable
fun UserDashboardScreen(
    viewModel: MeskotViewModel,
    currentUser: User?,
    posts: List<Post>,
    friendsSet: Set<String>,
    language: Language,
    onNavigate: (AppView) -> Unit
) {
    if (currentUser == null) return

    val myPosts = remember(posts, currentUser) { posts.filter { it.uid == currentUser.uid } }
    val totalTipsReceived = remember(myPosts) { myPosts.sumOf { it.tipTotal } }
    val totalReactionsReceived = remember(myPosts) { myPosts.sumOf { it.reactions.size } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text(
                text = if (language == Language.AM) "የተጠቃሚ ዳሽቦርድ" else "Personal Dashboard",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MeskotInk
            )
            Text(
                text = if (language == Language.AM) "የእንቅስቃሴዎ እና የማህበረሰብ ተሳትፎዎ አጠቃላይ እይታ" else "Overview of your activity and community reach",
                fontSize = 13.sp,
                color = MeskotMuted
            )
        }

        // Stats Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = if (language == Language.AM) "ልጥፎች" else "Posts",
                    value = myPosts.size.toString(),
                    icon = "📝",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (language == Language.AM) "ጓደኞች" else "Friends",
                    value = friendsSet.size.toString(),
                    icon = "👥",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = if (language == Language.AM) "ውዳሴዎች" else "Reactions",
                    value = totalReactionsReceived.toString(),
                    icon = "❤️",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (language == Language.AM) "የተገኘ ገቢ (ቻፓ)" else "Tips (Chapa)",
                    value = "${totalTipsReceived.toInt()} ETB",
                    icon = "💰",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Navigation to Admin if authorized
        if (currentUser.isAdmin) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                    border = BorderStroke(1.dp, MeskotGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(AppView.ADMIN_DASHBOARD) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("🛡️", fontSize = 24.sp)
                            Column {
                                Text(
                                    text = if (language == Language.AM) "የአስተዳዳሪ ፓነል" else "Admin Management Console",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MeskotGoldDeep
                                )
                                Text(
                                    text = if (language == Language.AM) "ተጠቃሚዎችን፣ ይዘቶችን እና ቡድኖችን ያስተዳድሩ" else "Manage users, content moderation & groups",
                                    fontSize = 12.sp,
                                    color = MeskotMuted
                                )
                            }
                        }
                        Text("›", fontSize = 22.sp, color = MeskotGoldDeep)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MeskotCard),
        border = BorderStroke(1.dp, MeskotLine),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 12.5.sp, color = MeskotMuted)
                Text(icon, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MeskotInk
            )
        }
    }
}

@Composable
fun AdminDashboardScreen(
    viewModel: MeskotViewModel,
    allUsers: List<User>,
    posts: List<Post>,
    groups: List<Group>,
    albums: List<Album>,
    language: Language,
    onBack: () -> Unit
) {
    var selectedSection by remember { mutableStateOf("users") } // users, posts, groups

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskotPaper)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Column {
                    Text(
                        text = if (language == Language.AM) "የአስተዳዳሪ ፓነል" else "Admin Console",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = MeskotInk
                    )
                    Text(
                        text = if (language == Language.AM) "የመድረክ አስተዳደር እና የይዘት ቁጥጥር" else "Platform Management & Moderation",
                        fontSize = 12.sp,
                        color = MeskotMuted
                    )
                }
            }
        }

        // Quick platform stats row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(title = "Users", value = allUsers.size.toString(), icon = "👥", modifier = Modifier.weight(1f))
                StatCard(title = "Posts", value = posts.size.toString(), icon = "📝", modifier = Modifier.weight(1f))
                StatCard(title = "Groups", value = groups.size.toString(), icon = "👪", modifier = Modifier.weight(1f))
            }
        }

        // Section Tabs
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
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
                        label = "Users (${allUsers.size})",
                        isSelected = selectedSection == "users",
                        onClick = { selectedSection = "users" }
                    )
                    TabPill(
                        label = "Posts (${posts.size})",
                        isSelected = selectedSection == "posts",
                        onClick = { selectedSection = "posts" }
                    )
                    TabPill(
                        label = "Groups (${groups.size})",
                        isSelected = selectedSection == "groups",
                        onClick = { selectedSection = "groups" }
                    )
                }
            }
        }

        when (selectedSection) {
            "users" -> {
                items(allUsers, key = { it.uid }) { user ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MeskotAvatar(photoUrl = user.photoURL, displayName = user.displayName, size = 40.dp)
                                Column {
                                    Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = "${user.email} ${if (user.isSuspended) "· 🔴 Suspended" else "· 🟢 Active"}",
                                        fontSize = 11.5.sp,
                                        color = if (user.isSuspended) MeskotCrimson else MeskotMuted
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.toggleUserSuspend(user.uid) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(if (user.isSuspended) "Unsuspend" else "Suspend", fontSize = 11.sp, color = if (user.isSuspended) MeskotOnlineGreen else MeskotCrimson)
                                }
                            }
                        }
                    }
                }
            }
            "posts" -> {
                items(posts, key = { it.id }) { post ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(post.text.ifBlank { "[Media content]" }, fontSize = 12.sp, color = MeskotMuted, maxLines = 2)
                            }
                            IconButton(onClick = { viewModel.deletePost(post.id) }) {
                                Text("🗑️", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
            "groups" -> {
                items(groups, key = { it.id }) { g ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(g.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${g.memberCount} members · ${g.description}", fontSize = 12.sp, color = MeskotMuted, maxLines = 1)
                            }
                            IconButton(onClick = { viewModel.adminDeleteGroup(g.id) }) {
                                Text("🗑️", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
