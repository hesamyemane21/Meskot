package com.example

import com.example.data.MeskotRepository
import com.example.model.Language
import com.example.model.PostVisibility
import com.example.model.ReactionType
import com.example.viewmodel.AppView
import com.example.viewmodel.MeskotViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MeskotViewModelTest {

    private lateinit var repository: MeskotRepository
    private lateinit var viewModel: MeskotViewModel

    @Before
    fun setUp() {
        repository = MeskotRepository()
        viewModel = MeskotViewModel(repository)
    }

    @Test
    fun testInitialStateHasSampleData() {
        val posts = repository.posts.value
        assertTrue("Posts should be pre-populated", posts.isNotEmpty())

        val users = repository.users.value
        assertTrue("Users should be pre-populated", users.isNotEmpty())

        val currentUser = repository.currentUser.value
        assertNotNull("Current user should be active", currentUser)
        assertEquals("Almaz Tesfaye", currentUser?.displayName)
    }

    @Test
    fun testLanguageSwitching() {
        assertEquals("Feed", viewModel.t("feed"))
        viewModel.setLanguage(Language.AM)
        assertEquals("የዜና ምግብ", viewModel.t("feed"))
        viewModel.toggleLanguage()
        assertEquals("Feed", viewModel.t("feed"))
    }

    @Test
    fun testPostCreationAndReactions() {
        val initialCount = repository.posts.value.size
        viewModel.createPost(
            text = "Selam all! Excited to share this test post.",
            visibility = PostVisibility.PUBLIC
        )

        val newCount = repository.posts.value.size
        assertEquals(initialCount + 1, newCount)

        val createdPost = repository.posts.value.first()
        assertEquals("Selam all! Excited to share this test post.", createdPost.text)

        // Add reaction
        val currentUid = repository.currentUser.value!!.uid
        viewModel.setReaction(createdPost.id, ReactionType.LOVE)
        val updatedPost = repository.posts.value.first { it.id == createdPost.id }
        assertEquals(ReactionType.LOVE, updatedPost.reactions[currentUid])
    }

    @Test
    fun testChapaTipping() {
        val targetPost = repository.posts.value.first()
        val initialTips = targetPost.tipTotal

        viewModel.sendTip(targetPost.id, 50.0)

        val updatedPost = repository.posts.value.first { it.id == targetPost.id }
        assertEquals(initialTips + 50.0, updatedPost.tipTotal, 0.001)
    }

    @Test
    fun testFriendRequestLifecycle() {
        val allUsers = repository.users.value
        val otherUser = allUsers.first { it.uid != repository.currentUser.value?.uid }

        // Send request
        viewModel.sendFriendRequest(otherUser.uid)
        val outgoingReqs = repository.friendRequests.value.filter { it.fromUid == repository.currentUser.value?.uid && it.toUid == otherUser.uid }
        assertTrue("Request should be logged", outgoingReqs.isNotEmpty())

        // Switch to the other user and accept
        viewModel.switchUser(otherUser.uid)
        viewModel.acceptFriendRequest(outgoingReqs.first().fromUid)

        val friends = repository.friends.value[otherUser.uid].orEmpty()
        assertTrue("Friend should be registered", friends.contains(outgoingReqs.first().fromUid))
    }

    @Test
    fun testGroupCreation() {
        val initialGroupCount = repository.groups.value.size
        viewModel.createGroup("Addis Tech Innovators", "A hub for developers and creators in Ethiopia")

        val newGroupCount = repository.groups.value.size
        assertEquals(initialGroupCount + 1, newGroupCount)

        val createdGroup = repository.groups.value.first { it.name == "Addis Tech Innovators" }
        assertEquals("A hub for developers and creators in Ethiopia", createdGroup.description)
    }

    @Test
    fun testDirectMessaging() {
        val otherUid = "u_bereket"
        val currentUid = repository.currentUser.value!!.uid
        val convoId = listOf(currentUid, otherUid).sorted().joinToString("_")

        viewModel.sendChatMessage(convoId, otherUid, "Tena yistillign!")

        val msgs = repository.chatMessages.value[convoId].orEmpty()
        assertTrue("Message should be recorded", msgs.any { it.text == "Tena yistillign!" })
    }
}
