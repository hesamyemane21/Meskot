package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.NotifType
import com.example.model.NotificationItem
import com.example.model.User
import com.example.ui.components.MeskotAvatar
import com.example.ui.components.formatTimeAgo
import com.example.ui.theme.*
import com.example.viewmodel.AppView
import com.example.viewmodel.MeskotViewModel

@Composable
fun NotificationsScreen(
    viewModel: MeskotViewModel,
    notifications: List<NotificationItem>,
    language: Language,
    onNavigate: (AppView) -> Unit,
    onNavigateToChat: (String) -> Unit
) {
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
                text = if (language == Language.AM) "ማሳወቂያዎች" else "Notifications",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MeskotInk
            )
            if (notifications.isNotEmpty()) {
                TextButton(onClick = { viewModel.navigateTo(AppView.NOTIFICATIONS) }) {
                    Text(
                        text = if (language == Language.AM) "ሁሉንም እንደተነበበ ምልክት አድርግ" else "Mark all read",
                        fontSize = 12.sp,
                        color = MeskotGoldDeep
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (language == Language.AM) "ምንም ማሳወቂያዎች የሉም" else "No notifications yet",
                    color = MeskotMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    NotificationItemCard(
                        notification = notif,
                        language = language,
                        onClick = {
                            when (notif.type) {
                                NotifType.MESSAGE -> onNavigateToChat(notif.fromUid)
                                NotifType.FRIEND_REQ, NotifType.FRIEND_ACCEPT -> onNavigate(AppView.FRIENDS)
                                NotifType.LIKE, NotifType.COMMENT, NotifType.SHARE, NotifType.REACTION, NotifType.TIP -> onNavigate(AppView.FEED)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItemCard(
    notification: NotificationItem,
    language: Language,
    onClick: () -> Unit
) {
    val icon = when (notification.type) {
        NotifType.LIKE, NotifType.REACTION -> notification.reactionType?.emoji ?: "❤️"
        NotifType.COMMENT -> "💬"
        NotifType.FRIEND_REQ -> "👥"
        NotifType.FRIEND_ACCEPT -> "🤝"
        NotifType.MESSAGE -> "✉️"
        NotifType.SHARE -> "🔁"
        NotifType.TIP -> "💰"
    }

    val title = when (notification.type) {
        NotifType.LIKE -> if (language == Language.AM) "ልጥፍዎን ወደዱ" else "liked your post"
        NotifType.REACTION -> if (language == Language.AM) "ለልጥፍዎ ምላሽ ሰጡ" else "reacted to your post"
        NotifType.COMMENT -> if (language == Language.AM) "በልጥፍዎ ላይ አስተያየት ሰጡ" else "commented on your post"
        NotifType.FRIEND_REQ -> if (language == Language.AM) "የጓደኝነት ጥያቄ ላኩልዎት" else "sent you a friend request"
        NotifType.FRIEND_ACCEPT -> if (language == Language.AM) "የጓደኝነት ጥያቄዎን ተቀበሉ" else "accepted your friend request"
        NotifType.MESSAGE -> if (language == Language.AM) "መልእክት ላኩልዎት" else "sent you a direct message"
        NotifType.SHARE -> if (language == Language.AM) "ልጥፍዎን አጋሩ" else "shared your post"
        NotifType.TIP -> if (language == Language.AM) "${notification.amount?.toInt() ?: 10} ብር በቻፓ ላኩልዎት" else "sent you ${notification.amount?.toInt() ?: 10} ETB tip via Chapa"
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.read) MeskotCard else MeskotPaper2
        ),
        border = BorderStroke(1.dp, if (notification.read) MeskotLine else MeskotGold.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MeskotCard),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 20.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = if (notification.read) FontWeight.Normal else FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = MeskotInk
                )
                Text(
                    text = formatTimeAgo(notification.createdAtMillis, language),
                    fontSize = 11.sp,
                    color = MeskotMuted
                )
            }

            if (!notification.read) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MeskotGold)
                )
            }
        }
    }
}
