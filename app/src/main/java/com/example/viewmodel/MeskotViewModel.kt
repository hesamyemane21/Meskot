package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MeskotRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class AppView {
    FEED,
    FRIENDS,
    MESSAGES,
    CHAT,
    GROUPS,
    GROUP_PAGE,
    PHOTOS,
    ALBUM_PAGE,
    NOTIFICATIONS,
    USER_DASHBOARD,
    ADMIN_DASHBOARD,
    PROFILE,
    SAVED,
    MENU,
    AUTH
}

data class MeskotUiState(
    val language: Language = Language.EN,
    val currentView: AppView = AppView.FEED,
    val currentUser: User? = null,
    val allUsers: List<User> = emptyList(),
    val posts: List<Post> = emptyList(),
    val friendsMap: Map<String, Set<String>> = emptyMap(),
    val friendRequests: List<FriendRequest> = emptyList(),
    val conversations: List<Conversation> = emptyList(),
    val chatMessages: Map<String, List<ChatMessage>> = emptyMap(),
    val groups: List<Group> = emptyList(),
    val groupMemberships: Map<String, Set<String>> = emptyMap(),
    val groupPosts: Map<String, List<Post>> = emptyMap(),
    val albums: List<Album> = emptyList(),
    val albumMedia: Map<String, List<AlbumMedia>> = emptyMap(),
    val savedPostIds: Set<String> = emptySet(),
    val notifications: List<NotificationItem> = emptyList(),
    val comments: Map<String, List<Comment>> = emptyMap(),
    val activeCall: ActiveCall? = null,
    // Active navigation parameters
    val selectedProfileUid: String? = null,
    val selectedGroupId: String? = null,
    val selectedAlbumId: String? = null,
    val selectedChatUid: String? = null,
    val lightboxMediaIndex: Int = -1,
    val lightboxMediaList: List<AlbumMedia> = emptyList(),
    val toastMessage: String? = null
)

class MeskotViewModel(
    private val repository: MeskotRepository = MeskotRepository()
) : ViewModel() {

    private val _language = MutableStateFlow(Language.EN)
    private val _currentView = MutableStateFlow(AppView.FEED)
    private val _selectedProfileUid = MutableStateFlow<String?>(null)
    private val _selectedGroupId = MutableStateFlow<String?>(null)
    private val _selectedAlbumId = MutableStateFlow<String?>(null)
    private val _selectedChatUid = MutableStateFlow<String?>(null)
    private val _lightboxMediaIndex = MutableStateFlow(-1)
    private val _lightboxMediaList = MutableStateFlow<List<AlbumMedia>>(emptyList())
    private val _toastMessage = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow(MeskotUiState())
    val uiState: StateFlow<MeskotUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _language,
                _currentView,
                repository.currentUser,
                repository.users,
                repository.posts,
                repository.friends,
                repository.friendRequests,
                repository.conversations,
                repository.chatMessages,
                repository.groups,
                repository.groupMemberships,
                repository.groupPosts,
                repository.albums,
                repository.albumMedia,
                repository.savedPostIds,
                repository.notifications,
                repository.comments,
                repository.activeCall,
                _selectedProfileUid,
                _selectedGroupId,
                _selectedAlbumId,
                _selectedChatUid,
                _lightboxMediaIndex,
                _lightboxMediaList,
                _toastMessage
            ) { args ->
                @Suppress("UNCHECKED_CAST")
                val lang = args[0] as Language
                val view = args[1] as AppView
                val curr = args[2] as? User
                val uList = args[3] as List<User>
                val postList = args[4] as List<Post>
                val frMap = args[5] as Map<String, Set<String>>
                val frReq = args[6] as List<FriendRequest>
                val convList = args[7] as List<Conversation>
                val cmMap = args[8] as Map<String, List<ChatMessage>>
                val grpList = args[9] as List<Group>
                val grpMem = args[10] as Map<String, Set<String>>
                val grpPosts = args[11] as Map<String, List<Post>>
                val albList = args[12] as List<Album>
                val albMedia = args[13] as Map<String, List<AlbumMedia>>
                val savedIds = args[14] as Set<String>
                val notifs = args[15] as List<NotificationItem>
                val comms = args[16] as Map<String, List<Comment>>
                val call = args[17] as? ActiveCall
                val profUid = args[18] as? String
                val grpId = args[19] as? String
                val albId = args[20] as? String
                val chatUid = args[21] as? String
                val lbIdx = args[22] as Int
                val lbList = args[23] as List<AlbumMedia>
                val toast = args[24] as? String

                val effectiveView = if (curr == null) AppView.AUTH else view

                MeskotUiState(
                    language = lang,
                    currentView = effectiveView,
                    currentUser = curr,
                    allUsers = uList,
                    posts = postList,
                    friendsMap = frMap,
                    friendRequests = frReq,
                    conversations = convList,
                    chatMessages = cmMap,
                    groups = grpList,
                    groupMemberships = grpMem,
                    groupPosts = grpPosts,
                    albums = albList,
                    albumMedia = albMedia,
                    savedPostIds = savedIds,
                    notifications = notifs,
                    comments = comms,
                    activeCall = call,
                    selectedProfileUid = profUid,
                    selectedGroupId = grpId,
                    selectedAlbumId = albId,
                    selectedChatUid = chatUid,
                    lightboxMediaIndex = lbIdx,
                    lightboxMediaList = lbList,
                    toastMessage = toast
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setLanguage(lang: Language) {
        _language.value = lang
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == Language.EN) Language.AM else Language.EN
    }

    fun t(key: String): String = MeskotStrings.get(key, _language.value)

    fun navigateTo(view: AppView) {
        _currentView.value = view
        if (view == AppView.NOTIFICATIONS) {
            repository.markNotificationsAsRead()
        }
    }

    fun navigateToProfile(uid: String) {
        _selectedProfileUid.value = uid
        _currentView.value = AppView.PROFILE
    }

    fun navigateToGroup(groupId: String) {
        _selectedGroupId.value = groupId
        _currentView.value = AppView.GROUP_PAGE
    }

    fun navigateToAlbum(albumId: String) {
        _selectedAlbumId.value = albumId
        _currentView.value = AppView.ALBUM_PAGE
    }

    fun navigateToChat(otherUid: String) {
        _selectedChatUid.value = otherUid
        val convoId = repository.getOrCreateConversationId(otherUid)
        repository.markConvoAsRead(convoId)
        _currentView.value = AppView.CHAT
    }

    fun openLightbox(list: List<AlbumMedia>, startIndex: Int) {
        _lightboxMediaList.value = list
        _lightboxMediaIndex.value = startIndex
    }

    fun closeLightbox() {
        _lightboxMediaIndex.value = -1
        _lightboxMediaList.value = emptyList()
    }

    fun nextLightbox() {
        val list = _lightboxMediaList.value
        if (list.isNotEmpty()) {
            _lightboxMediaIndex.value = (_lightboxMediaIndex.value + 1) % list.size
        }
    }

    fun prevLightbox() {
        val list = _lightboxMediaList.value
        if (list.isNotEmpty()) {
            _lightboxMediaIndex.value = (_lightboxMediaIndex.value - 1 + list.size) % list.size
        }
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Auth
    fun login(email: String, pass: String) {
        val res = repository.login(email, pass)
        res.onSuccess {
            showToast(t("welcomeBack") + ", ${it.displayName}")
            _currentView.value = AppView.FEED
        }.onFailure {
            showToast(it.message ?: "Login failed")
        }
    }

    fun signup(name: String, email: String, pass: String) {
        val res = repository.signup(name, email, pass)
        res.onSuccess {
            showToast(t("posted"))
            _currentView.value = AppView.FEED
        }.onFailure {
            showToast(it.message ?: "Sign up failed")
        }
    }

    fun logout() {
        repository.logout()
        _currentView.value = AppView.AUTH
    }

    fun switchUser(uid: String) {
        repository.switchCurrentUser(uid)
        showToast("Switched user")
    }

    fun updateProfile(displayName: String, bio: String, photoURL: String = "") {
        repository.updateProfile(displayName, bio, photoURL)
        showToast(t("profileSaved"))
    }

    // Post Operations
    fun createPost(
        text: String,
        media: List<MediaItem> = emptyList(),
        bgColor: String? = null,
        visibility: PostVisibility = PostVisibility.PUBLIC,
        groupId: String? = null
    ) {
        repository.createPost(text, media, bgColor, visibility, groupId)
        showToast(t("posted"))
    }

    fun editPost(postId: String, newText: String, isGroup: Boolean = false, groupId: String? = null) {
        repository.editPost(postId, newText, isGroup, groupId)
        showToast(t("profileSaved"))
    }

    fun deletePost(postId: String, isGroup: Boolean = false, groupId: String? = null) {
        repository.deletePost(postId, isGroup, groupId)
        showToast("Deleted")
    }

    fun setReaction(postId: String, reactionType: ReactionType, isGroup: Boolean = false, groupId: String? = null) {
        repository.setReaction(postId, reactionType, isGroup, groupId)
    }

    fun addComment(postId: String, text: String, parentId: String? = null, isGroup: Boolean = false, groupId: String? = null) {
        repository.addComment(postId, text, parentId, isGroup, groupId)
    }

    fun toggleCommentLike(postId: String, commentId: String) {
        repository.toggleCommentLike(postId, commentId)
    }

    fun editComment(postId: String, commentId: String, newText: String) {
        repository.editComment(postId, commentId, newText)
    }

    fun deleteComment(postId: String, commentId: String, isGroup: Boolean = false, groupId: String? = null) {
        repository.deleteComment(postId, commentId, isGroup, groupId)
    }

    fun sharePost(post: Post) {
        repository.sharePost(post)
        showToast(t("postShared"))
    }

    fun toggleSavePost(postId: String) {
        repository.toggleSavePost(postId)
    }

    fun sendTip(postId: String, amount: Double, isGroup: Boolean = false, groupId: String? = null) {
        repository.sendTip(postId, amount, isGroup, groupId)
        showToast("Thank you for your support! ($amount ETB sent via Chapa)")
    }

    // Friends
    fun sendFriendRequest(toUid: String) {
        repository.sendFriendRequest(toUid)
        showToast(t("requested"))
    }

    fun cancelFriendRequest(toUid: String) {
        repository.cancelFriendRequest(toUid)
    }

    fun acceptFriendRequest(fromUid: String) {
        repository.acceptFriendRequest(fromUid)
        showToast("Friend request accepted")
    }

    fun declineFriendRequest(fromUid: String) {
        repository.declineFriendRequest(fromUid)
    }

    fun unfriend(uid: String) {
        repository.unfriend(uid)
        showToast(t("unfriend"))
    }

    // Direct Messages & Call
    fun sendChatMessage(convoId: String, otherUid: String, text: String) {
        repository.sendChatMessage(convoId, otherUid, text)
    }

    fun editChatMessage(convoId: String, msgId: String, newText: String) {
        repository.editChatMessage(convoId, msgId, newText)
    }

    fun deleteChatMessage(convoId: String, msgId: String) {
        repository.deleteChatMessage(convoId, msgId)
    }

    fun startCall(toUid: String, type: String) {
        repository.startCall(toUid, type)
    }

    fun toggleMute() {
        repository.toggleMute()
    }

    fun toggleCamera() {
        repository.toggleCamera()
    }

    fun endCall() {
        repository.endCall()
    }

    // Groups
    fun createGroup(name: String, description: String) {
        val group = repository.createGroup(name, description)
        navigateToGroup(group.id)
        showToast("Group created")
    }

    fun toggleGroupJoin(groupId: String) {
        repository.toggleGroupJoin(groupId)
    }

    // Albums
    fun createAlbum(title: String) {
        val album = repository.createAlbum(title)
        navigateToAlbum(album.id)
        showToast("Album created")
    }

    fun addAlbumMedia(albumId: String, url: String, type: MediaType = MediaType.IMAGE, caption: String = "") {
        repository.addAlbumMedia(albumId, url, type, caption)
        showToast("Media added")
    }

    // Admin
    fun toggleUserSuspend(uid: String) {
        repository.toggleUserSuspend(uid)
        showToast("User status updated")
    }

    fun toggleUserAdmin(uid: String) {
        repository.toggleUserAdmin(uid)
        showToast("Admin privileges updated")
    }

    fun adminDeleteGroup(groupId: String) {
        repository.adminDeleteGroup(groupId)
        showToast("Group deleted")
    }
}
