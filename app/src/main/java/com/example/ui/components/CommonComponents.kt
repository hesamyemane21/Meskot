package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Language
import com.example.model.ReactionType
import com.example.ui.theme.*
import com.example.viewmodel.AppView

@Composable
fun MeskotBrandMark(
    size: Dp = 34.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
            .background(
                Brush.linearGradient(
                    listOf(MeskotGold, MeskotGoldDeep)
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomStart = 4.dp, bottomEnd = 4.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Vertical lattice line
        Box(
            modifier = Modifier
                .width(1.8.dp)
                .fillMaxHeight(0.72f)
                .background(Color.White.copy(alpha = 0.75f))
        )
        // Horizontal lattice line
        Box(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(1.8.dp)
                .background(Color.White.copy(alpha = 0.75f))
        )
    }
}

@Composable
fun MeskotAvatar(
    photoUrl: String?,
    displayName: String,
    size: Dp = 38.dp,
    showOnlineDot: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val clickableMod = if (onClick != null) modifier.clickable { onClick() } else modifier

    Box(
        modifier = clickableMod.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(1.dp, MeskotLine, CircleShape)
            )
        } else {
            val initials = displayName.trim().split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")
                .ifBlank { "?" }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MeskotGoldDeep)
                    .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.42f).sp
                )
            }
        }

        if (showOnlineDot) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(MeskotOnlineGreen)
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }
    }
}

@Composable
fun MeskotTopBar(
    language: Language,
    onToggleLanguage: () -> Unit,
    onMenuClick: () -> Unit,
    currentUserPhoto: String?,
    currentUserName: String,
    onProfileClick: () -> Unit
) {
    Surface(
        color = MeskotPaper.copy(alpha = 0.95f),
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MeskotLine)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                MeskotBrandMark()
                Column {
                    Text(
                        text = "Meskot",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MeskotInk,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = if (language == Language.AM) "መስኮትህ ለማህበረሰብህ" else "መስኮት · Ethiopian Community",
                        fontSize = 10.5.sp,
                        color = MeskotMuted
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language Toggle Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MeskotPaper2,
                    border = BorderStroke(1.dp, MeskotLine),
                    modifier = Modifier.clickable { onToggleLanguage() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (language == Language.EN) MeskotInk else Color.Transparent
                        ) {
                            Text(
                                text = "EN",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (language == Language.EN) Color.White else MeskotMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (language == Language.AM) MeskotInk else Color.Transparent
                        ) {
                            Text(
                                text = "አማ",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (language == Language.AM) Color.White else MeskotMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Menu button
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MeskotCard)
                        .border(1.dp, MeskotLine, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MeskotInk
                    )
                }

                // Profile Avatar
                MeskotAvatar(
                    photoUrl = currentUserPhoto,
                    displayName = currentUserName,
                    size = 36.dp,
                    onClick = onProfileClick
                )
            }
        }
    }
}

@Composable
fun MeskotIconNav(
    currentView: AppView,
    friendReqCount: Int,
    unreadMsgCount: Int,
    unreadNotifCount: Int,
    isAdmin: Boolean,
    onNavigate: (AppView) -> Unit
) {
    Surface(
        color = MeskotPaper.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, MeskotLine)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = "🏠",
                isSelected = currentView == AppView.FEED,
                onClick = { onNavigate(AppView.FEED) },
                testTag = "nav_feed"
            )
            NavItem(
                icon = "👥",
                badgeCount = friendReqCount,
                isSelected = currentView == AppView.FRIENDS,
                onClick = { onNavigate(AppView.FRIENDS) },
                testTag = "nav_friends"
            )
            NavItem(
                icon = "💬",
                badgeCount = unreadMsgCount,
                isSelected = currentView == AppView.MESSAGES || currentView == AppView.CHAT,
                onClick = { onNavigate(AppView.MESSAGES) },
                testTag = "nav_messages"
            )
            NavItem(
                icon = "👪",
                isSelected = currentView == AppView.GROUPS || currentView == AppView.GROUP_PAGE,
                onClick = { onNavigate(AppView.GROUPS) },
                testTag = "nav_groups"
            )
            NavItem(
                icon = "🖼️",
                isSelected = currentView == AppView.PHOTOS || currentView == AppView.ALBUM_PAGE,
                onClick = { onNavigate(AppView.PHOTOS) },
                testTag = "nav_photos"
            )
            NavItem(
                icon = "🔔",
                badgeCount = unreadNotifCount,
                isSelected = currentView == AppView.NOTIFICATIONS,
                onClick = { onNavigate(AppView.NOTIFICATIONS) },
                testTag = "nav_notifications"
            )
            NavItem(
                icon = "📊",
                isSelected = currentView == AppView.USER_DASHBOARD,
                onClick = { onNavigate(AppView.USER_DASHBOARD) },
                testTag = "nav_dashboard"
            )
            if (isAdmin) {
                NavItem(
                    icon = "🛡️",
                    isSelected = currentView == AppView.ADMIN_DASHBOARD,
                    onClick = { onNavigate(AppView.ADMIN_DASHBOARD) },
                    testTag = "nav_admin"
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: String,
    badgeCount: Int = 0,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .testTag(testTag)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .height(2.5.dp)
                    .background(if (isSelected) MeskotGold else Color.Transparent, RoundedCornerShape(2.dp))
            )
        }

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-4).dp)
                    .clip(CircleShape)
                    .background(MeskotCrimson)
                    .padding(horizontal = 4.5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ReactionPickerBar(
    onSelectReaction: (ReactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, MeskotLine),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReactionType.values().forEach { reaction ->
                Text(
                    text = reaction.emoji,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .clickable { onSelectReaction(reaction) }
                        .padding(4.dp)
                )
            }
        }
    }
}

fun formatTimeAgo(millis: Long, language: Language): String {
    val diffSec = (System.currentTimeMillis() - millis) / 1000
    return if (language == Language.AM) {
        when {
            diffSec < 60 -> "አሁን"
            diffSec < 3600 -> "${diffSec / 60} ደቂቃ በፊት"
            diffSec < 86400 -> "${diffSec / 3600} ሰዓት በፊት"
            else -> "${diffSec / 86400} ቀን በፊት"
        }
    } else {
        when {
            diffSec < 60 -> "just now"
            diffSec < 3600 -> "${diffSec / 60}m ago"
            diffSec < 86400 -> "${diffSec / 3600}h ago"
            else -> "${diffSec / 86400}d ago"
        }
    }
}
