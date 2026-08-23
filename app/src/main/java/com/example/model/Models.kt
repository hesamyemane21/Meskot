package com.example.model

enum class Language {
    EN, AM
}

enum class ReactionType(val emoji: String, val labelEn: String, val labelAm: String) {
    LIKE("👍", "Like", "ውደድ"),
    LOVE("❤️", "Love", "ውደድ በጣም"),
    HAHA("😆", "Haha", "ሳቅ"),
    WOW("😮", "Wow", "ዋው"),
    SAD("😢", "Sad", "አዝኗል"),
    ANGRY("😡", "Angry", "ተናደደ")
}

enum class PostVisibility(val icon: String, val labelEn: String, val labelAm: String, val subEn: String, val subAm: String) {
    PUBLIC("🌍", "Public", "ሁሉም", "Anyone on Meskot", "በመስኮት ላይ ያለ ማንኛውም ሰው"),
    FRIENDS("👥", "Friends", "ጓደኞች", "Your friends on Meskot", "በመስኮት ላይ ያሉ ጓደኞችዎ"),
    ONLY_ME("🔒", "Only me", "እኔ ብቻ", "Only visible to you", "ለእርስዎ ብቻ የሚታይ")
}

enum class MediaType {
    IMAGE, VIDEO
}

data class MediaItem(
    val url: String,
    val type: MediaType = MediaType.IMAGE
)

data class SharedPostInfo(
    val postId: String,
    val col: String = "posts",
    val uid: String,
    val authorName: String,
    val authorPhoto: String = "",
    val text: String = "",
    val media: List<MediaItem> = emptyList(),
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class User(
    val uid: String,
    val displayName: String,
    val email: String,
    val bio: String = "",
    val photoURL: String = "",
    val isAdmin: Boolean = false,
    val isSuspended: Boolean = false,
    val emailVerified: Boolean = true,
    val lastSeenMillis: Long = System.currentTimeMillis(),
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class Post(
    val id: String,
    val uid: String,
    val authorName: String,
    val authorPhoto: String = "",
    val text: String = "",
    val media: List<MediaItem> = emptyList(),
    val bgColor: String? = null,
    val visibility: PostVisibility = PostVisibility.PUBLIC,
    val reactions: Map<String, ReactionType> = emptyMap(), // uid -> ReactionType
    val commentCount: Int = 0,
    val sharedFrom: SharedPostInfo? = null,
    val tipTotal: Double = 0.0,
    val editedAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class Comment(
    val id: String,
    val postId: String,
    val parentId: String? = null,
    val uid: String,
    val authorName: String,
    val authorPhoto: String = "",
    val text: String = "",
    val likes: Map<String, Boolean> = emptyMap(), // uid -> true
    val editedAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class FriendRequest(
    val id: String,
    val fromUid: String,
    val toUid: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class Conversation(
    val id: String,
    val participants: List<String>,
    val lastMessage: String = "",
    val lastMessageAtMillis: Long = System.currentTimeMillis(),
    val unreadCounts: Map<String, Int> = emptyMap()
)

data class CallLogInfo(
    val callType: String = "audio", // audio or video
    val callStatus: String = "completed", // completed, missed
    val callDurationSec: Int = 0
)

data class ChatMessage(
    val id: String,
    val convoId: String,
    val fromUid: String,
    val text: String,
    val callLog: CallLogInfo? = null,
    val editedAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class Group(
    val id: String,
    val name: String,
    val description: String = "",
    val createdBy: String,
    val memberCount: Int = 1,
    val coverColorHex: String = "#B8863A",
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class Album(
    val id: String,
    val uid: String,
    val title: String,
    val coverURL: String = "",
    val coverType: MediaType = MediaType.IMAGE,
    val count: Int = 0,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class AlbumMedia(
    val id: String,
    val albumId: String,
    val uid: String,
    val url: String,
    val type: MediaType = MediaType.IMAGE,
    val caption: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)

enum class NotifType {
    LIKE, COMMENT, FRIEND_REQ, FRIEND_ACCEPT, MESSAGE, SHARE, TIP, REACTION
}

data class NotificationItem(
    val id: String,
    val toUid: String,
    val fromUid: String,
    val type: NotifType,
    val reactionType: ReactionType? = null,
    val postId: String? = null,
    val convoId: String? = null,
    val groupId: String? = null,
    val amount: Double? = null,
    val read: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class ActiveCall(
    val callId: String,
    val fromUid: String,
    val toUid: String,
    val type: String, // audio, video
    val isIncoming: Boolean,
    val status: String, // ringing, connected, ended
    val startTimeMillis: Long = 0L,
    val isMuted: Boolean = false,
    val isCameraOff: Boolean = false
)
