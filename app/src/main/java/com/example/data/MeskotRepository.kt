package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MeskotRepository {

    // Demo Users
    private val defaultUsers = listOf(
        User(
            uid = "user_hesam",
            displayName = "Hesam Yemane",
            email = "hesam@meskot.et",
            bio = "Building bridges for our Ethiopian diaspora and local tech community 🇪🇹✨",
            photoURL = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
            isAdmin = true
        ),
        User(
            uid = "user_selam",
            displayName = "Selamawit Tadesse",
            email = "selam@meskot.et",
            bio = "Visual artist & cultural preservationist in Addis Ababa. Exploring Ethiopian manuscript arts.",
            photoURL = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400",
            isAdmin = false
        ),
        User(
            uid = "user_dawit",
            displayName = "Dawit Bekele",
            email = "dawit@meskot.et",
            bio = "Coffee roaster & Yirgacheffe enthusiast. Sharing stories from Sidama highlands ☕",
            photoURL = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            isAdmin = false
        ),
        User(
            uid = "user_meron",
            displayName = "Meron Haile",
            email = "meron@meskot.et",
            bio = "Architecture researcher documenting rock-hewn churches and traditional meskot windows.",
            photoURL = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400",
            isAdmin = false
        ),
        User(
            uid = "user_amanuel",
            displayName = "Amanuel Girma",
            email = "amanuel@meskot.et",
            bio = "Software engineer & open source enthusiast in Bole, Addis Ababa. Let's collaborate!",
            photoURL = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400",
            isAdmin = false
        )
    )

    private val _users = MutableStateFlow<List<User>>(defaultUsers)
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(defaultUsers[0])
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Posts & Group Posts
    private val defaultPosts = listOf(
        Post(
            id = "post_1",
            uid = "user_selam",
            authorName = "Selamawit Tadesse",
            authorPhoto = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400",
            text = "Welcome to Meskot (መስኮት)! 🪟✨\n\nA digital window connecting Ethiopians across the globe — from Addis Ababa to Washington D.C. Share your stories, photos, and cultural heritage.",
            bgColor = "linear-gradient(135deg,#8C2F39,#B8863A)",
            reactions = mapOf("user_hesam" to ReactionType.LOVE, "user_dawit" to ReactionType.LIKE, "user_meron" to ReactionType.LIKE),
            commentCount = 3,
            tipTotal = 50.0,
            createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 15
        ),
        Post(
            id = "post_2",
            uid = "user_dawit",
            authorName = "Dawit Bekele",
            authorPhoto = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400",
            text = "Fresh harvest from the highlands of Yirgacheffe! Morning coffee ceremony with buna, rue (tena adam), and fresh popcorn. Have a blessed week everyone! ☕🇪🇹",
            media = listOf(
                MediaItem("https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600", MediaType.IMAGE),
                MediaItem("https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=600", MediaType.IMAGE)
            ),
            reactions = mapOf("user_hesam" to ReactionType.LIKE, "user_selam" to ReactionType.LOVE),
            commentCount = 2,
            tipTotal = 25.0,
            createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 60 * 2
        ),
        Post(
            id = "post_3",
            uid = "user_meron",
            authorName = "Meron Haile",
            authorPhoto = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=400",
            text = "Studying the intricate stone window carvings of Biete Medhane Alem in Lalibela. The geometry and symbolism developed centuries ago continues to inspire contemporary African architecture.",
            media = listOf(
                MediaItem("https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600", MediaType.IMAGE)
            ),
            reactions = mapOf("user_hesam" to ReactionType.WOW, "user_dawit" to ReactionType.LOVE),
            commentCount = 1,
            createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 60 * 5
        )
    )

    private val _posts = MutableStateFlow<List<Post>>(defaultPosts)
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(
        mapOf(
            "post_1" to listOf(
                Comment(
                    id = "c_1",
                    postId = "post_1",
                    uid = "user_hesam",
                    authorName = "Hesam Yemane",
                    authorPhoto = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                    text = "Betam des yilal! Proud to see our community platform come to life 👏",
                    likes = mapOf("user_selam" to true),
                    createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 10
                ),
                Comment(
                    id = "c_2",
                    postId = "post_1",
                    parentId = "c_1",
                    uid = "user_selam",
                    authorName = "Selamawit Tadesse",
                    authorPhoto = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400",
                    text = "Ameseginalehu Hesam! Looking forward to having everyone on board.",
                    createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 8
                ),
                Comment(
                    id = "c_3",
                    postId = "post_1",
                    uid = "user_amanuel",
                    authorName = "Amanuel Girma",
                    authorPhoto = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400",
                    text = "Great design and smooth experience! 🌟",
                    createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 5
                )
            ),
            "post_2" to listOf(
                Comment(
                    id = "c_4",
                    postId = "post_2",
                    uid = "user_selam",
                    authorName = "Selamawit Tadesse",
                    authorPhoto = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400",
                    text = "Buna tetu! The aroma must be incredible.",
                    createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 50
                ),
                Comment(
                    id = "c_5",
                    postId = "post_2",
                    uid = "user_hesam",
                    authorName = "Hesam Yemane",
                    authorPhoto = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                    text = "Save some for me next time I visit Sidama! 😄",
                    createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 40
                )
            )
        )
    )
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    // Friends & Requests
    private val _friends = MutableStateFlow<Map<String, Set<String>>>(
        mapOf(
            "user_hesam" to setOf("user_selam", "user_dawit"),
            "user_selam" to setOf("user_hesam", "user_meron"),
            "user_dawit" to setOf("user_hesam"),
            "user_meron" to setOf("user_selam")
        )
    )
    val friends: StateFlow<Map<String, Set<String>>> = _friends.asStateFlow()

    private val _friendRequests = MutableStateFlow<List<FriendRequest>>(
        listOf(
            FriendRequest("req_1", fromUid = "user_meron", toUid = "user_hesam")
        )
    )
    val friendRequests: StateFlow<List<FriendRequest>> = _friendRequests.asStateFlow()

    // Direct Messages & Conversations
    private val _conversations = MutableStateFlow<List<Conversation>>(
        listOf(
            Conversation(
                id = "user_hesam_user_selam",
                participants = listOf("user_hesam", "user_selam"),
                lastMessage = "Selam! Have you seen the new exhibition at the National Museum?",
                lastMessageAtMillis = System.currentTimeMillis() - 1000 * 60 * 20,
                unreadCounts = mapOf("user_hesam" to 0, "user_selam" to 0)
            ),
            Conversation(
                id = "user_dawit_user_hesam",
                participants = listOf("user_hesam", "user_dawit"),
                lastMessage = "Let's catch up over coffee this Saturday!",
                lastMessageAtMillis = System.currentTimeMillis() - 1000 * 60 * 60,
                unreadCounts = mapOf("user_hesam" to 1, "user_dawit" to 0)
            )
        )
    )
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(
        mapOf(
            "user_hesam_user_selam" to listOf(
                ChatMessage("m1", "user_hesam_user_selam", "user_hesam", "Selam Selam! How is the new artwork coming along?", createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 30),
                ChatMessage("m2", "user_hesam_user_selam", "user_selam", "Selam! It is progressing well. Finishing the gold leaf accents today.", createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 25),
                ChatMessage("m3", "user_hesam_user_selam", "user_selam", "Selam! Have you seen the new exhibition at the National Museum?", createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 20)
            ),
            "user_dawit_user_hesam" to listOf(
                ChatMessage("m4", "user_dawit_user_hesam", "user_dawit", "Let's catch up over coffee this Saturday!", createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 60)
            )
        )
    )
    val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()

    // Groups
    private val _groups = MutableStateFlow<List<Group>>(
        listOf(
            Group("g_1", "Ethiopian Tech & Startups", "Innovators, designers, and software engineers collaborating across Ethiopia.", "user_hesam", memberCount = 14, coverColorHex = "#2A4838"),
            Group("g_2", "Habesha Coffee Lovers", "Celebrating the history, roasting techniques, and rituals of Ethiopian coffee culture.", "user_dawit", memberCount = 28, coverColorHex = "#B8863A"),
            Group("g_3", "Ethiopian Arts & History", "Exploring traditional art, Geez manuscripts, music, and ancient architecture.", "user_selam", memberCount = 19, coverColorHex = "#8C2F39")
        )
    )
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _groupMemberships = MutableStateFlow<Map<String, Set<String>>>(
        mapOf(
            "user_hesam" to setOf("g_1", "g_2")
        )
    )
    val groupMemberships: StateFlow<Map<String, Set<String>>> = _groupMemberships.asStateFlow()

    private val _groupPosts = MutableStateFlow<Map<String, List<Post>>>(
        mapOf(
            "g_1" to listOf(
                Post(
                    id = "gp_1",
                    uid = "user_hesam",
                    authorName = "Hesam Yemane",
                    authorPhoto = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400",
                    text = "Welcome to the Ethiopian Tech group! Share what projects you are building this month.",
                    reactions = mapOf("user_amanuel" to ReactionType.LIKE),
                    commentCount = 1,
                    createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 60 * 3
                )
            )
        )
    )
    val groupPosts: StateFlow<Map<String, List<Post>>> = _groupPosts.asStateFlow()

    // Albums & Photos
    private val _albums = MutableStateFlow<List<Album>>(
        listOf(
            Album("a_1", "user_hesam", "Addis Vibes & Coffee", "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400", count = 3),
            Album("a_2", "user_selam", "Lalibela Architecture", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=400", count = 2)
        )
    )
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _albumMedia = MutableStateFlow<Map<String, List<AlbumMedia>>>(
        mapOf(
            "a_1" to listOf(
                AlbumMedia("m_1", "a_1", "user_hesam", "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=800", caption = "Traditional roasting pan"),
                AlbumMedia("m_2", "a_1", "user_hesam", "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800", caption = "Jebena pouring session"),
                AlbumMedia("m_3", "a_1", "user_hesam", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800", caption = "Profile portrait")
            ),
            "a_2" to listOf(
                AlbumMedia("m_4", "a_2", "user_selam", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800", caption = "Window motifs"),
                AlbumMedia("m_5", "a_2", "user_selam", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800", caption = "Art studio")
            )
        )
    )
    val albumMedia: StateFlow<Map<String, List<AlbumMedia>>> = _albumMedia.asStateFlow()

    // Saved Posts
    private val _savedPostIds = MutableStateFlow<Set<String>>(setOf("post_1"))
    val savedPostIds: StateFlow<Set<String>> = _savedPostIds.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(
        listOf(
            NotificationItem(
                id = "n_1",
                toUid = "user_hesam",
                fromUid = "user_selam",
                type = NotifType.REACTION,
                reactionType = ReactionType.LOVE,
                postId = "post_1",
                read = false,
                createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 12
            ),
            NotificationItem(
                id = "n_2",
                toUid = "user_hesam",
                fromUid = "user_meron",
                type = NotifType.FRIEND_REQ,
                read = false,
                createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 45
            ),
            NotificationItem(
                id = "n_3",
                toUid = "user_hesam",
                fromUid = "user_dawit",
                type = NotifType.TIP,
                amount = 25.0,
                postId = "post_1",
                read = true,
                createdAtMillis = System.currentTimeMillis() - 1000 * 60 * 120
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Active Call
    private val _activeCall = MutableStateFlow<ActiveCall?>(null)
    val activeCall: StateFlow<ActiveCall?> = _activeCall.asStateFlow()

    // ================= ACTIONS =================

    fun switchCurrentUser(uid: String) {
        val user = _users.value.find { it.uid == uid }
        if (user != null) {
            _currentUser.value = user
        }
    }

    fun login(email: String, pass: String): Result<User> {
        val found = _users.value.find { it.email.equals(email.trim(), ignoreCase = true) }
        return if (found != null) {
            if (found.isSuspended) {
                Result.failure(Exception("This account has been suspended."))
            } else {
                _currentUser.value = found
                Result.success(found)
            }
        } else {
            // Auto create account if testing
            val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            val newUser = User(
                uid = "user_" + UUID.randomUUID().toString().take(6),
                displayName = name,
                email = email.trim(),
                bio = "New member of the Meskot community"
            )
            _users.value = _users.value + newUser
            _currentUser.value = newUser
            Result.success(newUser)
        }
    }

    fun signup(name: String, email: String, pass: String): Result<User> {
        if (pass.length < 6) return Result.failure(Exception("Password must be longer than 6 characters."))
        val existing = _users.value.find { it.email.equals(email.trim(), ignoreCase = true) }
        if (existing != null) return Result.failure(Exception("Account with this email already exists."))
        val newUser = User(
            uid = "user_" + UUID.randomUUID().toString().take(6),
            displayName = name.trim(),
            email = email.trim(),
            bio = ""
        )
        _users.value = _users.value + newUser
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(displayName: String, bio: String, photoURL: String = "") {
        val curr = _currentUser.value ?: return
        val updated = curr.copy(
            displayName = displayName.ifBlank { curr.displayName },
            bio = bio,
            photoURL = if (photoURL.isNotBlank()) photoURL else curr.photoURL
        )
        _currentUser.value = updated
        _users.value = _users.value.map { if (it.uid == curr.uid) updated else it }
    }

    // Post creation
    fun createPost(
        text: String,
        media: List<MediaItem> = emptyList(),
        bgColor: String? = null,
        visibility: PostVisibility = PostVisibility.PUBLIC,
        groupId: String? = null
    ) {
        val curr = _currentUser.value ?: return
        val newPost = Post(
            id = "post_" + System.currentTimeMillis(),
            uid = curr.uid,
            authorName = curr.displayName,
            authorPhoto = curr.photoURL,
            text = text,
            media = media,
            bgColor = bgColor,
            visibility = visibility,
            createdAtMillis = System.currentTimeMillis()
        )
        if (groupId != null) {
            val currGroupPosts = _groupPosts.value[groupId].orEmpty()
            _groupPosts.value = _groupPosts.value + (groupId to (listOf(newPost) + currGroupPosts))
        } else {
            _posts.value = listOf(newPost) + _posts.value
        }
    }

    fun editPost(postId: String, newText: String, isGroup: Boolean = false, groupId: String? = null) {
        if (isGroup && groupId != null) {
            val list = _groupPosts.value[groupId].orEmpty().map {
                if (it.id == postId) it.copy(text = newText, editedAtMillis = System.currentTimeMillis()) else it
            }
            _groupPosts.value = _groupPosts.value + (groupId to list)
        } else {
            _posts.value = _posts.value.map {
                if (it.id == postId) it.copy(text = newText, editedAtMillis = System.currentTimeMillis()) else it
            }
        }
    }

    fun deletePost(postId: String, isGroup: Boolean = false, groupId: String? = null) {
        if (isGroup && groupId != null) {
            val list = _groupPosts.value[groupId].orEmpty().filter { it.id != postId }
            _groupPosts.value = _groupPosts.value + (groupId to list)
        } else {
            _posts.value = _posts.value.filter { it.id != postId }
            _savedPostIds.value = _savedPostIds.value - postId
        }
    }

    fun setReaction(postId: String, reactionType: ReactionType, isGroup: Boolean = false, groupId: String? = null) {
        val curr = _currentUser.value ?: return
        val updater: (Post) -> Post = { post ->
            val currReactions = post.reactions.toMutableMap()
            if (currReactions[curr.uid] == reactionType) {
                currReactions.remove(curr.uid)
            } else {
                currReactions[curr.uid] = reactionType
                if (post.uid != curr.uid) {
                    addNotification(post.uid, NotifType.REACTION, reactionType = reactionType, postId = postId, groupId = groupId)
                }
            }
            post.copy(reactions = currReactions)
        }

        if (isGroup && groupId != null) {
            val list = _groupPosts.value[groupId].orEmpty().map { if (it.id == postId) updater(it) else it }
            _groupPosts.value = _groupPosts.value + (groupId to list)
        } else {
            _posts.value = _posts.value.map { if (it.id == postId) updater(it) else it }
        }
    }

    fun addComment(postId: String, text: String, parentId: String? = null, isGroup: Boolean = false, groupId: String? = null) {
        val curr = _currentUser.value ?: return
        val newComment = Comment(
            id = "c_" + System.currentTimeMillis(),
            postId = postId,
            parentId = parentId,
            uid = curr.uid,
            authorName = curr.displayName,
            authorPhoto = curr.photoURL,
            text = text,
            createdAtMillis = System.currentTimeMillis()
        )
        val postComments = _comments.value[postId].orEmpty() + newComment
        _comments.value = _comments.value + (postId to postComments)

        // Increment comment count on post
        if (isGroup && groupId != null) {
            val list = _groupPosts.value[groupId].orEmpty().map {
                if (it.id == postId) it.copy(commentCount = it.commentCount + 1) else it
            }
            _groupPosts.value = _groupPosts.value + (groupId to list)
        } else {
            _posts.value = _posts.value.map {
                if (it.id == postId) it.copy(commentCount = it.commentCount + 1) else it
            }
            val targetPost = _posts.value.find { it.id == postId }
            if (targetPost != null && targetPost.uid != curr.uid) {
                addNotification(targetPost.uid, NotifType.COMMENT, postId = postId, groupId = groupId)
            }
        }
    }

    fun toggleCommentLike(postId: String, commentId: String) {
        val curr = _currentUser.value ?: return
        val postComments = _comments.value[postId].orEmpty().map { comment ->
            if (comment.id == commentId) {
                val currLikes = comment.likes.toMutableMap()
                if (currLikes[curr.uid] == true) {
                    currLikes.remove(curr.uid)
                } else {
                    currLikes[curr.uid] = true
                }
                comment.copy(likes = currLikes)
            } else comment
        }
        _comments.value = _comments.value + (postId to postComments)
    }

    fun editComment(postId: String, commentId: String, newText: String) {
        val postComments = _comments.value[postId].orEmpty().map {
            if (it.id == commentId) it.copy(text = newText, editedAtMillis = System.currentTimeMillis()) else it
        }
        _comments.value = _comments.value + (postId to postComments)
    }

    fun deleteComment(postId: String, commentId: String, isGroup: Boolean = false, groupId: String? = null) {
        val postComments = _comments.value[postId].orEmpty().filter { it.id != commentId && it.parentId != commentId }
        _comments.value = _comments.value + (postId to postComments)
        if (isGroup && groupId != null) {
            val list = _groupPosts.value[groupId].orEmpty().map {
                if (it.id == postId) it.copy(commentCount = maxOf(0, it.commentCount - 1)) else it
            }
            _groupPosts.value = _groupPosts.value + (groupId to list)
        } else {
            _posts.value = _posts.value.map {
                if (it.id == postId) it.copy(commentCount = maxOf(0, it.commentCount - 1)) else it
            }
        }
    }

    fun sharePost(post: Post) {
        val curr = _currentUser.value ?: return
        val sharedInfo = SharedPostInfo(
            postId = post.sharedFrom?.postId ?: post.id,
            uid = post.sharedFrom?.uid ?: post.uid,
            authorName = post.sharedFrom?.authorName ?: post.authorName,
            authorPhoto = post.sharedFrom?.authorPhoto ?: post.authorPhoto,
            text = post.sharedFrom?.text ?: post.text,
            media = post.sharedFrom?.media ?: post.media,
            createdAtMillis = post.sharedFrom?.createdAtMillis ?: post.createdAtMillis
        )
        val newPost = Post(
            id = "post_shared_" + System.currentTimeMillis(),
            uid = curr.uid,
            authorName = curr.displayName,
            authorPhoto = curr.photoURL,
            text = "",
            sharedFrom = sharedInfo,
            createdAtMillis = System.currentTimeMillis()
        )
        _posts.value = listOf(newPost) + _posts.value
        if (post.uid != curr.uid) {
            addNotification(post.uid, NotifType.SHARE, postId = post.id)
        }
    }

    fun toggleSavePost(postId: String) {
        val currSet = _savedPostIds.value.toMutableSet()
        if (currSet.contains(postId)) {
            currSet.remove(postId)
        } else {
            currSet.add(postId)
        }
        _savedPostIds.value = currSet
    }

    fun sendTip(postId: String, amount: Double, isGroup: Boolean = false, groupId: String? = null) {
        val curr = _currentUser.value ?: return
        if (isGroup && groupId != null) {
            val list = _groupPosts.value[groupId].orEmpty().map {
                if (it.id == postId) it.copy(tipTotal = it.tipTotal + amount) else it
            }
            _groupPosts.value = _groupPosts.value + (groupId to list)
        } else {
            _posts.value = _posts.value.map {
                if (it.id == postId) it.copy(tipTotal = it.tipTotal + amount) else it
            }
            val targetPost = _posts.value.find { it.id == postId }
            if (targetPost != null && targetPost.uid != curr.uid) {
                addNotification(targetPost.uid, NotifType.TIP, amount = amount, postId = postId)
            }
        }
    }

    // Friends
    fun sendFriendRequest(toUid: String) {
        val curr = _currentUser.value ?: return
        if (curr.uid == toUid) return
        val newReq = FriendRequest(id = "req_${curr.uid}_$toUid", fromUid = curr.uid, toUid = toUid)
        _friendRequests.value = _friendRequests.value + newReq
        addNotification(toUid, NotifType.FRIEND_REQ)
    }

    fun cancelFriendRequest(toUid: String) {
        val curr = _currentUser.value ?: return
        _friendRequests.value = _friendRequests.value.filterNot { it.fromUid == curr.uid && it.toUid == toUid }
    }

    fun acceptFriendRequest(fromUid: String) {
        val curr = _currentUser.value ?: return
        _friendRequests.value = _friendRequests.value.filterNot { it.fromUid == fromUid && it.toUid == curr.uid }

        val myFriends = _friends.value[curr.uid].orEmpty().toMutableSet()
        myFriends.add(fromUid)
        val theirFriends = _friends.value[fromUid].orEmpty().toMutableSet()
        theirFriends.add(curr.uid)

        _friends.value = _friends.value + (curr.uid to myFriends) + (fromUid to theirFriends)
        addNotification(fromUid, NotifType.FRIEND_ACCEPT)
    }

    fun declineFriendRequest(fromUid: String) {
        val curr = _currentUser.value ?: return
        _friendRequests.value = _friendRequests.value.filterNot { it.fromUid == fromUid && it.toUid == curr.uid }
    }

    fun unfriend(uid: String) {
        val curr = _currentUser.value ?: return
        val myFriends = _friends.value[curr.uid].orEmpty().toMutableSet()
        myFriends.remove(uid)
        val theirFriends = _friends.value[uid].orEmpty().toMutableSet()
        theirFriends.remove(curr.uid)

        _friends.value = _friends.value + (curr.uid to myFriends) + (uid to theirFriends)
    }

    // Direct Messages
    fun getOrCreateConversationId(otherUid: String): String {
        val curr = _currentUser.value ?: return ""
        val existing = _conversations.value.find {
            it.participants.contains(curr.uid) && it.participants.contains(otherUid)
        }
        return if (existing != null) {
            existing.id
        } else {
            val newId = listOf(curr.uid, otherUid).sorted().joinToString("_")
            val newConvo = Conversation(
                id = newId,
                participants = listOf(curr.uid, otherUid),
                lastMessage = "",
                lastMessageAtMillis = System.currentTimeMillis()
            )
            _conversations.value = listOf(newConvo) + _conversations.value
            newId
        }
    }

    fun sendChatMessage(convoId: String, otherUid: String, text: String) {
        val curr = _currentUser.value ?: return
        val newMsg = ChatMessage(
            id = "msg_" + System.currentTimeMillis(),
            convoId = convoId,
            fromUid = curr.uid,
            text = text,
            createdAtMillis = System.currentTimeMillis()
        )
        val list = _chatMessages.value[convoId].orEmpty() + newMsg
        _chatMessages.value = _chatMessages.value + (convoId to list)

        _conversations.value = _conversations.value.map {
            if (it.id == convoId) {
                it.copy(
                    lastMessage = text,
                    lastMessageAtMillis = System.currentTimeMillis(),
                    unreadCounts = it.unreadCounts + (otherUid to (it.unreadCounts[otherUid] ?: 0) + 1)
                )
            } else it
        }
        addNotification(otherUid, NotifType.MESSAGE, convoId = convoId)
    }

    fun editChatMessage(convoId: String, msgId: String, newText: String) {
        val list = _chatMessages.value[convoId].orEmpty().map {
            if (it.id == msgId) it.copy(text = newText, editedAtMillis = System.currentTimeMillis()) else it
        }
        _chatMessages.value = _chatMessages.value + (convoId to list)
    }

    fun deleteChatMessage(convoId: String, msgId: String) {
        val list = _chatMessages.value[convoId].orEmpty().filter { it.id != msgId }
        _chatMessages.value = _chatMessages.value + (convoId to list)
    }

    fun markConvoAsRead(convoId: String) {
        val curr = _currentUser.value ?: return
        _conversations.value = _conversations.value.map {
            if (it.id == convoId) {
                it.copy(unreadCounts = it.unreadCounts + (curr.uid to 0))
            } else it
        }
    }

    // Call Actions
    fun startCall(toUid: String, type: String) {
        val curr = _currentUser.value ?: return
        val call = ActiveCall(
            callId = "call_" + System.currentTimeMillis(),
            fromUid = curr.uid,
            toUid = toUid,
            type = type,
            isIncoming = false,
            status = "connected",
            startTimeMillis = System.currentTimeMillis()
        )
        _activeCall.value = call
    }

    fun toggleMute() {
        val curr = _activeCall.value ?: return
        _activeCall.value = curr.copy(isMuted = !curr.isMuted)
    }

    fun toggleCamera() {
        val curr = _activeCall.value ?: return
        _activeCall.value = curr.copy(isCameraOff = !curr.isCameraOff)
    }

    fun endCall() {
        val call = _activeCall.value ?: return
        val durationSec = ((System.currentTimeMillis() - call.startTimeMillis) / 1000).toInt()
        val otherUid = if (call.fromUid == (_currentUser.value?.uid ?: "")) call.toUid else call.fromUid
        val convoId = getOrCreateConversationId(otherUid)

        // Add call log message to chat
        val callLogMsg = ChatMessage(
            id = "call_log_" + System.currentTimeMillis(),
            convoId = convoId,
            fromUid = call.fromUid,
            text = if (call.type == "video") "Video call" else "Audio call",
            callLog = CallLogInfo(callType = call.type, callStatus = "completed", callDurationSec = durationSec),
            createdAtMillis = System.currentTimeMillis()
        )
        val list = _chatMessages.value[convoId].orEmpty() + callLogMsg
        _chatMessages.value = _chatMessages.value + (convoId to list)

        _activeCall.value = null
    }

    // Groups
    fun createGroup(name: String, description: String): Group {
        val curr = _currentUser.value ?: throw Exception("Not logged in")
        val newGroup = Group(
            id = "g_" + System.currentTimeMillis(),
            name = name,
            description = description,
            createdBy = curr.uid,
            memberCount = 1,
            coverColorHex = listOf("#B8863A", "#8C2F39", "#2A4838", "#4A3B5C", "#335577").random()
        )
        _groups.value = listOf(newGroup) + _groups.value
        val memberships = _groupMemberships.value[curr.uid].orEmpty().toMutableSet()
        memberships.add(newGroup.id)
        _groupMemberships.value = _groupMemberships.value + (curr.uid to memberships)
        return newGroup
    }

    fun toggleGroupJoin(groupId: String) {
        val curr = _currentUser.value ?: return
        val memberships = _groupMemberships.value[curr.uid].orEmpty().toMutableSet()
        val isMember = memberships.contains(groupId)
        if (isMember) {
            memberships.remove(groupId)
            _groups.value = _groups.value.map { if (it.id == groupId) it.copy(memberCount = maxOf(1, it.memberCount - 1)) else it }
        } else {
            memberships.add(groupId)
            _groups.value = _groups.value.map { if (it.id == groupId) it.copy(memberCount = it.memberCount + 1) else it }
        }
        _groupMemberships.value = _groupMemberships.value + (curr.uid to memberships)
    }

    // Albums
    fun createAlbum(title: String): Album {
        val curr = _currentUser.value ?: throw Exception("Not logged in")
        val newAlbum = Album(
            id = "album_" + System.currentTimeMillis(),
            uid = curr.uid,
            title = title,
            count = 0
        )
        _albums.value = listOf(newAlbum) + _albums.value
        return newAlbum
    }

    fun addAlbumMedia(albumId: String, url: String, type: MediaType = MediaType.IMAGE, caption: String = "") {
        val curr = _currentUser.value ?: return
        val newMedia = AlbumMedia(
            id = "media_" + System.currentTimeMillis(),
            albumId = albumId,
            uid = curr.uid,
            url = url,
            type = type,
            caption = caption
        )
        val list = listOf(newMedia) + _albumMedia.value[albumId].orEmpty()
        _albumMedia.value = _albumMedia.value + (albumId to list)

        _albums.value = _albums.value.map {
            if (it.id == albumId) {
                it.copy(count = it.count + 1, coverURL = url, coverType = type)
            } else it
        }
    }

    // Notifications
    private fun addNotification(
        toUid: String,
        type: NotifType,
        reactionType: ReactionType? = null,
        postId: String? = null,
        convoId: String? = null,
        groupId: String? = null,
        amount: Double? = null
    ) {
        val curr = _currentUser.value ?: return
        if (toUid == curr.uid) return
        val newNotif = NotificationItem(
            id = "notif_" + System.currentTimeMillis(),
            toUid = toUid,
            fromUid = curr.uid,
            type = type,
            reactionType = reactionType,
            postId = postId,
            convoId = convoId,
            groupId = groupId,
            amount = amount,
            read = false,
            createdAtMillis = System.currentTimeMillis()
        )
        _notifications.value = listOf(newNotif) + _notifications.value
    }

    fun markNotificationsAsRead() {
        val curr = _currentUser.value ?: return
        _notifications.value = _notifications.value.map {
            if (it.toUid == curr.uid) it.copy(read = true) else it
        }
    }

    // Admin Actions
    fun toggleUserSuspend(uid: String) {
        _users.value = _users.value.map {
            if (it.uid == uid) it.copy(isSuspended = !it.isSuspended) else it
        }
    }

    fun toggleUserAdmin(uid: String) {
        _users.value = _users.value.map {
            if (it.uid == uid) it.copy(isAdmin = !it.isAdmin) else it
        }
        if (_currentUser.value?.uid == uid) {
            _currentUser.value = _currentUser.value?.copy(isAdmin = !(_currentUser.value?.isAdmin ?: false))
        }
    }

    fun adminDeleteGroup(groupId: String) {
        _groups.value = _groups.value.filterNot { it.id == groupId }
        _groupPosts.value = _groupPosts.value - groupId
    }
}
