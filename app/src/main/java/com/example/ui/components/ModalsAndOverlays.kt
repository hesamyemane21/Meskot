package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TipModal(
    post: Post,
    language: Language,
    onDismiss: () -> Unit,
    onConfirmTip: (Double) -> Unit
) {
    var selectedPreset by remember { mutableStateOf<Double?>(null) }
    var customAmountText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val presetAmounts = listOf(10.0, 25.0, 50.0, 100.0)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MeskotCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            border = BorderStroke(1.dp, MeskotLine)
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Text(
                    text = if (language == Language.AM) "ይህን ፈጣሪ ይደግፉ" else "Support this creator",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = MeskotInk
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == Language.AM)
                        "100% ገንዘቡ በቻፓ (Chapa) በኩል ወደ እነሱ ይሄዳል — መስኮት ምንም አይወስድም።"
                    else
                        "100% goes to ${post.authorName} via Chapa — Meskot takes no platform cut.",
                    fontSize = 12.5.sp,
                    color = MeskotMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetAmounts.take(2).forEach { amount ->
                        val isSelected = selectedPreset == amount && customAmountText.isEmpty()
                        OutlinedButton(
                            onClick = {
                                selectedPreset = amount
                                customAmountText = ""
                                errorMessage = null
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MeskotGold else Color.White,
                                contentColor = if (isSelected) Color.White else MeskotInk
                            ),
                            border = BorderStroke(1.dp, if (isSelected) MeskotGold else MeskotLine),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "${amount.toInt()} ETB", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetAmounts.drop(2).forEach { amount ->
                        val isSelected = selectedPreset == amount && customAmountText.isEmpty()
                        OutlinedButton(
                            onClick = {
                                selectedPreset = amount
                                customAmountText = ""
                                errorMessage = null
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MeskotGold else Color.White,
                                contentColor = if (isSelected) Color.White else MeskotInk
                            ),
                            border = BorderStroke(1.dp, if (isSelected) MeskotGold else MeskotLine),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "${amount.toInt()} ETB", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = customAmountText,
                    onValueChange = {
                        customAmountText = it
                        selectedPreset = null
                        errorMessage = null
                    },
                    label = { Text(if (language == Language.AM) "ሌላ መጠን (ብር)" else "Custom amount (ETB)") },
                    placeholder = { Text("e.g. 30") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MeskotGold,
                        unfocusedBorderColor = MeskotLine
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MeskotCrimson,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (language == Language.AM) "ይቅር" else "Cancel", color = MeskotInk)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val customVal = customAmountText.toDoubleOrNull()
                            val amount = customVal ?: selectedPreset
                            if (amount == null || amount < 5.0) {
                                errorMessage = if (language == Language.AM) "እባክዎ ቢያንስ 5 ብር ይምረጡ" else "Please enter or select at least 5 ETB"
                            } else {
                                onConfirmTip(amount)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("confirm_tip_btn")
                    ) {
                        Text(if (language == Language.AM) "ወደ ክፍያ ይቀጥሉ" else "Continue to pay", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposerDialog(
    currentUser: User?,
    language: Language,
    initialGroupId: String? = null,
    onDismiss: () -> Unit,
    onSubmitPost: (text: String, media: List<MediaItem>, bgColor: String?, visibility: PostVisibility, groupId: String?) -> Unit
) {
    var postText by remember { mutableStateOf("") }
    var selectedBg by remember { mutableStateOf<String?>(null) }
    var selectedVisibility by remember { mutableStateOf(PostVisibility.PUBLIC) }
    var showVisibilitySheet by remember { mutableStateOf(false) }
    var pendingMedia by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var showMediaSamplePicker by remember { mutableStateOf(false) }

    val bgPresets = listOf(
        "" to "Aa",
        "linear-gradient(135deg,#8C2F39,#B8863A)" to "Sunset",
        "linear-gradient(135deg,#1B2A22,#2A4838)" to "Forest",
        "linear-gradient(135deg,#335577,#5A8FBE)" to "Navy",
        "linear-gradient(135deg,#4A3B5C,#8C5CA8)" to "Amethyst",
        "linear-gradient(135deg,#B8863A,#E8B94F)" to "Amber"
    )

    val sampleImages = listOf(
        "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600",
        "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=600",
        "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MeskotPaper
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    Text(
                        text = if (language == Language.AM) "ልጥፍ ይፍጠሩ" else "Create post",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Button(
                        onClick = {
                            if (postText.isNotBlank() || pendingMedia.isNotEmpty()) {
                                onSubmitPost(postText.trim(), pendingMedia, selectedBg, selectedVisibility, initialGroupId)
                                onDismiss()
                            }
                        },
                        enabled = postText.isNotBlank() || pendingMedia.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("submit_post_btn")
                    ) {
                        Text(if (language == Language.AM) "ለጥፍ" else "Post", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = MeskotLine)

                // Author Row with Privacy Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MeskotAvatar(
                        photoUrl = currentUser?.photoURL,
                        displayName = currentUser?.displayName ?: "User",
                        size = 44.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = currentUser?.displayName ?: "User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MeskotPaper2,
                            border = BorderStroke(1.dp, MeskotLine),
                            modifier = Modifier.clickable { showVisibilitySheet = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(selectedVisibility.icon, fontSize = 12.sp)
                                Text(
                                    text = if (language == Language.AM) selectedVisibility.labelAm else selectedVisibility.labelEn,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text("▾", fontSize = 10.sp)
                            }
                        }
                    }
                }

                // Text Body
                val bgBrush = when (selectedBg) {
                    "linear-gradient(135deg,#8C2F39,#B8863A)" -> Brush.linearGradient(GradientSunset)
                    "linear-gradient(135deg,#1B2A22,#2A4838)" -> Brush.linearGradient(GradientForest)
                    "linear-gradient(135deg,#335577,#5A8FBE)" -> Brush.linearGradient(GradientNavy)
                    "linear-gradient(135deg,#4A3B5C,#8C5CA8)" -> Brush.linearGradient(GradientAmethyst)
                    "linear-gradient(135deg,#B8863A,#E8B94F)" -> Brush.linearGradient(GradientAmber)
                    else -> null
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (bgBrush != null) Modifier.background(bgBrush)
                            else Modifier.background(Color.White)
                        )
                        .border(1.dp, MeskotLine, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = if (bgBrush != null) Alignment.Center else Alignment.TopStart
                ) {
                    TextField(
                        value = postText,
                        onValueChange = { postText = it },
                        placeholder = {
                            Text(
                                text = if (language == Language.AM) "ምን እያሰቡ ነው?" else "What's on your mind?",
                                color = if (bgBrush != null) Color.White.copy(alpha = 0.8f) else MeskotMuted,
                                fontSize = if (bgBrush != null) 22.sp else 16.sp,
                                textAlign = if (bgBrush != null) TextAlign.Center else TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = if (bgBrush != null) Color.White else MeskotInk,
                            unfocusedTextColor = if (bgBrush != null) Color.White else MeskotInk,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = if (bgBrush != null) 22.sp else 16.sp,
                            fontWeight = if (bgBrush != null) FontWeight.Bold else FontWeight.Normal,
                            textAlign = if (bgBrush != null) TextAlign.Center else TextAlign.Start
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("post_input_field")
                    )
                }

                // Media Previews
                if (pendingMedia.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(pendingMedia) { index, item ->
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, MeskotLine, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = item.url,
                                    contentDescription = "Media",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = {
                                        pendingMedia = pendingMedia.toMutableList().also { it.removeAt(index) }
                                    },
                                    modifier = Modifier
                                        .size(22.dp)
                                        .align(Alignment.TopEnd)
                                        .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Background Color Swatches
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    bgPresets.forEach { (bgGradient, label) ->
                        val isSelected = selectedBg == (if (bgGradient.isEmpty()) null else bgGradient)
                        val brush = when (bgGradient) {
                            "linear-gradient(135deg,#8C2F39,#B8863A)" -> Brush.linearGradient(GradientSunset)
                            "linear-gradient(135deg,#1B2A22,#2A4838)" -> Brush.linearGradient(GradientForest)
                            "linear-gradient(135deg,#335577,#5A8FBE)" -> Brush.linearGradient(GradientNavy)
                            "linear-gradient(135deg,#4A3B5C,#8C5CA8)" -> Brush.linearGradient(GradientAmethyst)
                            "linear-gradient(135deg,#B8863A,#E8B94F)" -> Brush.linearGradient(GradientAmber)
                            else -> null
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .then(
                                    if (brush != null) Modifier.background(brush)
                                    else Modifier.background(MeskotPaper2)
                                )
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) MeskotGold else MeskotLine,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedBg = if (bgGradient.isEmpty()) null else bgGradient
                                    if (selectedBg != null) pendingMedia = emptyList()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (bgGradient.isEmpty()) {
                                Text("Aa", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MeskotInk)
                            }
                        }
                    }
                }

                HorizontalDivider(color = MeskotLine)

                // Add Media Button Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { showMediaSamplePicker = true }
                    ) {
                        Text("🖼️  ", fontSize = 18.sp)
                        Text(
                            text = if (language == Language.AM) "ፎቶ/ቪዲዮ ጨምር" else "Photos/videos",
                            color = MeskotInk,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // Media sample picker dialog
    if (showMediaSamplePicker) {
        AlertDialog(
            onDismissRequest = { showMediaSamplePicker = false },
            title = { Text(if (language == Language.AM) "ፎቶ ይምረጡ" else "Select sample photos") },
            text = {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sampleImages) { url ->
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MeskotLine, RoundedCornerShape(8.dp))
                                .clickable {
                                    pendingMedia = pendingMedia + MediaItem(url, MediaType.IMAGE)
                                    selectedBg = null
                                    showMediaSamplePicker = false
                                }
                        ) {
                            AsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMediaSamplePicker = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Privacy Selector Dialog
    if (showVisibilitySheet) {
        AlertDialog(
            onDismissRequest = { showVisibilitySheet = false },
            title = {
                Text(
                    text = if (language == Language.AM) "ልጥፍዎን ማን ማየት ይችላል?" else "Who can see your post?",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PostVisibility.values().forEach { visibility ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedVisibility = visibility
                                    showVisibilitySheet = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(visibility.icon, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == Language.AM) visibility.labelAm else visibility.labelEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (language == Language.AM) visibility.subAm else visibility.subEn,
                                    fontSize = 12.sp,
                                    color = MeskotMuted
                                )
                            }
                            RadioButton(
                                selected = selectedVisibility == visibility,
                                onClick = {
                                    selectedVisibility = visibility
                                    showVisibilitySheet = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun CallOverlayScreen(
    activeCall: ActiveCall,
    otherUser: User?,
    language: Language,
    onToggleMute: () -> Unit,
    onToggleCamera: () -> Unit,
    onEndCall: () -> Unit
) {
    var callSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds++
        }
    }

    val formattedDuration = String.format("%d:%02d", callSeconds / 60, callSeconds % 60)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F1611)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Video preview background placeholder if video call
            if (activeCall.type == "video" && !activeCall.isCameraOff) {
                AsyncImage(
                    model = otherUser?.photoURL ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 40.dp)
                ) {
                    MeskotAvatar(
                        photoUrl = otherUser?.photoURL,
                        displayName = otherUser?.displayName ?: "User",
                        size = 110.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = otherUser?.displayName ?: "User",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${if (activeCall.type == "video") "📹 Video Call" else "📞 Audio Call"} · $formattedDuration",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 15.sp
                    )
                }

                // Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (activeCall.isMuted) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.16f))
                    ) {
                        Text(if (activeCall.isMuted) "🔇" else "🎤", fontSize = 22.sp)
                    }

                    // Hang Up Button
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MeskotCrimson)
                            .testTag("hang_up_btn")
                    ) {
                        Text("📞", fontSize = 24.sp)
                    }

                    // Camera Button if video
                    if (activeCall.type == "video") {
                        IconButton(
                            onClick = onToggleCamera,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (activeCall.isCameraOff) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.16f))
                        ) {
                            Text("📹", fontSize = 22.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LightboxViewer(
    mediaList: List<AlbumMedia>,
    currentIndex: Int,
    onClose: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit
) {
    if (currentIndex < 0 || currentIndex >= mediaList.size) return
    val currentItem = mediaList[currentIndex]

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xF00F1411)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Media
                AsyncImage(
                    model = currentItem.url,
                    contentDescription = currentItem.caption,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp)
                )

                // Top Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentIndex + 1} / ${mediaList.size}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Prev / Next Buttons
                IconButton(
                    onClick = onPrev,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Text("‹", fontSize = 28.sp, color = Color.White)
                }

                IconButton(
                    onClick = onNext,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Text("›", fontSize = 28.sp, color = Color.White)
                }

                // Caption
                if (currentItem.caption.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = currentItem.caption,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
