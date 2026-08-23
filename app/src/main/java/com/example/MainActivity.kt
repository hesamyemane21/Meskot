package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.AlbumMedia
import com.example.model.Language
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MeskotPaper
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppView
import com.example.viewmodel.MeskotViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MeskotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MeskotApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MeskotApp(viewModel: MeskotViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Lightbox State
    var lightboxMediaList by remember { mutableStateOf<List<AlbumMedia>>(emptyList()) }
    var lightboxIndex by remember { mutableStateOf(-1) }

    val myFriendsSet = remember(uiState.friendsMap, uiState.currentUser) {
        uiState.friendsMap[uiState.currentUser?.uid].orEmpty()
    }

    // Handle System Back Button
    BackHandler(enabled = uiState.currentView != AppView.FEED && uiState.currentUser != null) {
        when (uiState.currentView) {
            AppView.CHAT -> viewModel.navigateTo(AppView.MESSAGES)
            AppView.GROUP_PAGE -> viewModel.navigateTo(AppView.GROUPS)
            AppView.ALBUM_PAGE -> viewModel.navigateTo(AppView.PHOTOS)
            AppView.PROFILE -> viewModel.navigateTo(AppView.FEED)
            AppView.SAVED -> viewModel.navigateTo(AppView.FEED)
            AppView.MENU -> viewModel.navigateTo(AppView.FEED)
            AppView.ADMIN_DASHBOARD -> viewModel.navigateTo(AppView.USER_DASHBOARD)
            AppView.USER_DASHBOARD -> viewModel.navigateTo(AppView.FEED)
            else -> viewModel.navigateTo(AppView.FEED)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MeskotPaper
    ) {
        if (uiState.currentUser == null) {
            // Authentication Screen
            AuthScreen(
                viewModel = viewModel,
                language = uiState.language,
                allUsers = uiState.allUsers
            )
        } else {
            // Logged-in App Layout
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    Column {
                        MeskotTopBar(
                            language = uiState.language,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onMenuClick = {
                                if (uiState.currentView == AppView.MENU) {
                                    viewModel.navigateTo(AppView.FEED)
                                } else {
                                    viewModel.navigateTo(AppView.MENU)
                                }
                            },
                            currentUserPhoto = uiState.currentUser?.photoURL,
                            currentUserName = uiState.currentUser?.displayName ?: "User",
                            onProfileClick = {
                                uiState.currentUser?.let { viewModel.navigateToProfile(it.uid) }
                            }
                        )

                        // Secondary Sticky Icon Navigation Bar
                        MeskotIconNav(
                            currentView = uiState.currentView,
                            friendReqCount = uiState.friendRequests.count { it.toUid == uiState.currentUser?.uid },
                            unreadMsgCount = uiState.conversations.sumOf { it.unreadCounts[uiState.currentUser?.uid] ?: 0 },
                            unreadNotifCount = uiState.notifications.count { !it.read },
                            isAdmin = uiState.currentUser?.isAdmin == true,
                            onNavigate = { view -> viewModel.navigateTo(view) }
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = uiState.currentView,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "view_transition"
                    ) { view ->
                        when (view) {
                            AppView.AUTH -> {
                                AuthScreen(
                                    viewModel = viewModel,
                                    language = uiState.language,
                                    allUsers = uiState.allUsers
                                )
                            }
                            AppView.FEED -> {
                                FeedScreen(
                                    viewModel = viewModel,
                                    posts = uiState.posts,
                                    currentUser = uiState.currentUser,
                                    allUsers = uiState.allUsers,
                                    friendsSet = myFriendsSet,
                                    savedPostIds = uiState.savedPostIds,
                                    commentsMap = uiState.comments,
                                    language = uiState.language,
                                    onNavigateToProfile = { uid -> viewModel.navigateToProfile(uid) },
                                    onNavigateToChat = { uid -> viewModel.navigateToChat(uid) }
                                )
                            }
                            AppView.FRIENDS -> {
                                FriendsScreen(
                                    viewModel = viewModel,
                                    currentUser = uiState.currentUser,
                                    allUsers = uiState.allUsers,
                                    friendsSet = myFriendsSet,
                                    friendRequests = uiState.friendRequests,
                                    language = uiState.language,
                                    onNavigateToProfile = { uid -> viewModel.navigateToProfile(uid) },
                                    onNavigateToChat = { uid -> viewModel.navigateToChat(uid) }
                                )
                            }
                            AppView.MESSAGES -> {
                                MessagesScreen(
                                    viewModel = viewModel,
                                    currentUser = uiState.currentUser,
                                    conversations = uiState.conversations,
                                    allUsers = uiState.allUsers,
                                    language = uiState.language,
                                    onNavigateToChat = { uid -> viewModel.navigateToChat(uid) },
                                    onNavigateToProfile = { uid -> viewModel.navigateToProfile(uid) }
                                )
                            }
                            AppView.CHAT -> {
                                val otherUser = uiState.allUsers.find { it.uid == uiState.selectedChatUid }
                                val convoId = listOf(uiState.currentUser?.uid ?: "", uiState.selectedChatUid ?: "").sorted().joinToString("_")
                                val msgs = uiState.chatMessages[convoId].orEmpty()

                                ChatScreen(
                                    viewModel = viewModel,
                                    currentUser = uiState.currentUser,
                                    otherUser = otherUser,
                                    messages = msgs,
                                    language = uiState.language,
                                    onBack = { viewModel.navigateTo(AppView.MESSAGES) }
                                )
                            }
                            AppView.GROUPS -> {
                                val myGrpIds = uiState.groupMemberships[uiState.currentUser?.uid].orEmpty()
                                GroupsScreen(
                                    viewModel = viewModel,
                                    currentUser = uiState.currentUser,
                                    groups = uiState.groups,
                                    myGroupIds = myGrpIds,
                                    language = uiState.language,
                                    onNavigateToGroup = { gId -> viewModel.navigateToGroup(gId) }
                                )
                            }
                            AppView.GROUP_PAGE -> {
                                val group = uiState.groups.find { it.id == uiState.selectedGroupId }
                                val grpPosts = uiState.groupPosts[uiState.selectedGroupId].orEmpty()
                                val myGrpIds = uiState.groupMemberships[uiState.currentUser?.uid].orEmpty()
                                val isMember = group?.let { myGrpIds.contains(it.id) } == true

                                GroupPageScreen(
                                    viewModel = viewModel,
                                    group = group,
                                    posts = grpPosts,
                                    currentUser = uiState.currentUser,
                                    isMember = isMember,
                                    language = uiState.language,
                                    onBack = { viewModel.navigateTo(AppView.GROUPS) },
                                    onNavigateToProfile = { uid -> viewModel.navigateToProfile(uid) }
                                )
                            }
                            AppView.PHOTOS -> {
                                PhotosScreen(
                                    viewModel = viewModel,
                                    currentUser = uiState.currentUser,
                                    albums = uiState.albums,
                                    language = uiState.language,
                                    onNavigateToAlbum = { albumId -> viewModel.navigateToAlbum(albumId) }
                                )
                            }
                            AppView.ALBUM_PAGE -> {
                                val album = uiState.albums.find { it.id == uiState.selectedAlbumId }
                                val mediaList = uiState.albumMedia[uiState.selectedAlbumId].orEmpty()

                                AlbumPageScreen(
                                    viewModel = viewModel,
                                    album = album,
                                    mediaList = mediaList,
                                    language = uiState.language,
                                    onBack = { viewModel.navigateTo(AppView.PHOTOS) },
                                    onMediaClick = { idx ->
                                        lightboxMediaList = mediaList
                                        lightboxIndex = idx
                                    }
                                )
                            }
                            AppView.NOTIFICATIONS -> {
                                NotificationsScreen(
                                    viewModel = viewModel,
                                    notifications = uiState.notifications,
                                    language = uiState.language,
                                    onNavigate = { v -> viewModel.navigateTo(v) },
                                    onNavigateToChat = { uid -> viewModel.navigateToChat(uid) }
                                )
                            }
                            AppView.USER_DASHBOARD -> {
                                UserDashboardScreen(
                                    viewModel = viewModel,
                                    currentUser = uiState.currentUser,
                                    posts = uiState.posts,
                                    friendsSet = myFriendsSet,
                                    language = uiState.language,
                                    onNavigate = { v -> viewModel.navigateTo(v) }
                                )
                            }
                            AppView.ADMIN_DASHBOARD -> {
                                AdminDashboardScreen(
                                    viewModel = viewModel,
                                    allUsers = uiState.allUsers,
                                    posts = uiState.posts,
                                    groups = uiState.groups,
                                    albums = uiState.albums,
                                    language = uiState.language,
                                    onBack = { viewModel.navigateTo(AppView.USER_DASHBOARD) }
                                )
                            }
                            AppView.PROFILE -> {
                                val profileUser = uiState.allUsers.find { it.uid == uiState.selectedProfileUid } ?: uiState.currentUser
                                ProfileScreen(
                                    viewModel = viewModel,
                                    profileUser = profileUser,
                                    currentUser = uiState.currentUser,
                                    posts = uiState.posts,
                                    friendsSet = myFriendsSet,
                                    friendRequests = uiState.friendRequests,
                                    savedPostIds = uiState.savedPostIds,
                                    commentsMap = uiState.comments,
                                    language = uiState.language,
                                    onBack = { viewModel.navigateTo(AppView.FEED) },
                                    onNavigateToChat = { uid -> viewModel.navigateToChat(uid) }
                                )
                            }
                            AppView.SAVED -> {
                                SavedScreen(
                                    viewModel = viewModel,
                                    posts = uiState.posts,
                                    savedPostIds = uiState.savedPostIds,
                                    currentUser = uiState.currentUser,
                                    commentsMap = uiState.comments,
                                    language = uiState.language,
                                    onNavigateToProfile = { uid -> viewModel.navigateToProfile(uid) }
                                )
                            }
                            AppView.MENU -> {
                                MenuScreen(
                                    viewModel = viewModel,
                                    currentUser = uiState.currentUser,
                                    language = uiState.language,
                                    isAdmin = uiState.currentUser?.isAdmin == true,
                                    onNavigate = { v -> viewModel.navigateTo(v) }
                                )
                            }
                        }
                    }

                    // Active Video/Audio Call Overlay
                    if (uiState.activeCall != null) {
                        val call = uiState.activeCall!!
                        val otherUid = if (call.fromUid == uiState.currentUser?.uid) call.toUid else call.fromUid
                        val otherUser = uiState.allUsers.find { it.uid == otherUid }
                        CallOverlayScreen(
                            activeCall = call,
                            otherUser = otherUser,
                            language = uiState.language,
                            onToggleMute = { viewModel.toggleMute() },
                            onToggleCamera = { viewModel.toggleCamera() },
                            onEndCall = { viewModel.endCall() }
                        )
                    }

                    // Fullscreen Lightbox Media Viewer
                    if (lightboxIndex >= 0 && lightboxIndex < lightboxMediaList.size) {
                        LightboxViewer(
                            mediaList = lightboxMediaList,
                            currentIndex = lightboxIndex,
                            onClose = { lightboxIndex = -1 },
                            onNext = {
                                if (lightboxIndex < lightboxMediaList.size - 1) lightboxIndex++
                            },
                            onPrev = {
                                if (lightboxIndex > 0) lightboxIndex--
                            }
                        )
                    }
                }
            }
        }
    }
}
