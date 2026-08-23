package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Album
import com.example.model.AlbumMedia
import com.example.model.Language
import com.example.model.MediaType
import com.example.model.User
import com.example.ui.theme.*
import com.example.viewmodel.MeskotViewModel

@Composable
fun PhotosScreen(
    viewModel: MeskotViewModel,
    currentUser: User?,
    albums: List<Album>,
    language: Language,
    onNavigateToAlbum: (String) -> Unit
) {
    var showCreateAlbumDialog by remember { mutableStateOf(false) }

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
                text = if (language == Language.AM) "የፎቶ አልበሞች" else "Photo Albums",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = MeskotInk
            )
            Button(
                onClick = { showCreateAlbumDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(if (language == Language.AM) "+ አዲስ አልበም" else "+ New Album", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (albums.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (language == Language.AM) "ምንም አልበሞች የሉም" else "No albums created yet",
                    color = MeskotMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(albums, key = { it.id }) { album ->
                    AlbumItemCard(
                        album = album,
                        language = language,
                        onClick = { onNavigateToAlbum(album.id) }
                    )
                }
            }
        }
    }

    // Create Album Modal Dialog
    if (showCreateAlbumDialog) {
        var albumTitle by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateAlbumDialog = false },
            title = {
                Text(
                    text = if (language == Language.AM) "አዲስ አልበም ይፍጠሩ" else "Create a new album",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = albumTitle,
                    onValueChange = { albumTitle = it },
                    label = { Text(if (language == Language.AM) "የአልበም ስም" else "Album Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (albumTitle.isNotBlank()) {
                            viewModel.createAlbum(albumTitle.trim())
                            showCreateAlbumDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (language == Language.AM) "ፍጠር" else "Create", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAlbumDialog = false }) {
                    Text(if (language == Language.AM) "ይቅር" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun AlbumItemCard(
    album: Album,
    language: Language,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MeskotCard),
        border = BorderStroke(1.dp, MeskotLine),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (album.coverURL.isNotBlank()) {
                    AsyncImage(
                        model = album.coverURL,
                        contentDescription = album.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(listOf(MeskotGold, MeskotCrimson))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🖼️", fontSize = 36.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "${album.count} ${if (language == Language.AM) "ፎቶዎች" else "photos"}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = album.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                color = MeskotInk,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}

@Composable
fun AlbumPageScreen(
    viewModel: MeskotViewModel,
    album: Album?,
    mediaList: List<AlbumMedia>,
    language: Language,
    onBack: () -> Unit,
    onMediaClick: (Int) -> Unit
) {
    if (album == null) return

    var showAddMediaDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MeskotPaper)
    ) {
        // Header
        Surface(
            color = MeskotCard,
            border = BorderStroke(1.dp, MeskotLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Column {
                        Text(
                            text = album.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Serif,
                            color = MeskotInk
                        )
                        Text(
                            text = "${mediaList.size} ${if (language == Language.AM) "ፎቶዎች" else "photos"}",
                            fontSize = 11.5.sp,
                            color = MeskotMuted
                        )
                    }
                }

                Button(
                    onClick = { showAddMediaDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MeskotGold),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(if (language == Language.AM) "+ ፎቶ ጨምር" else "+ Add photo", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        // Media Grid
        if (mediaList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (language == Language.AM) "በዚህ አልበም ውስጥ ምንም ፎቶ የለም" else "No photos in this album yet",
                    color = MeskotMuted
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(mediaList) { index, item ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onMediaClick(index) }
                    ) {
                        AsyncImage(
                            model = item.url,
                            contentDescription = item.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    // Add Media Dialog
    if (showAddMediaDialog) {
        var mediaUrl by remember { mutableStateOf("") }
        var caption by remember { mutableStateOf("") }

        val sampleUrls = listOf(
            "https://images.unsplash.com/photo-1547471080-7cc2caa01a7e?w=800",
            "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=800",
            "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800"
        )

        AlertDialog(
            onDismissRequest = { showAddMediaDialog = false },
            title = { Text(if (language == Language.AM) "ፎቶ ወደ አልበም ጨምር" else "Add photo to album") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = mediaUrl,
                        onValueChange = { mediaUrl = it },
                        label = { Text("Photo URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = caption,
                        onValueChange = { caption = it },
                        label = { Text(if (language == Language.AM) "መግለጫ (አማራጭ)" else "Caption (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Or select sample:", fontSize = 12.sp, color = MeskotMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        sampleUrls.forEach { sample ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { mediaUrl = sample }
                            ) {
                                AsyncImage(model = sample, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (mediaUrl.isNotBlank()) {
                            viewModel.addAlbumMedia(album.id, mediaUrl.trim(), MediaType.IMAGE, caption.trim())
                            showAddMediaDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MeskotGold)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMediaDialog = false }) { Text("Cancel") }
            }
        )
    }
}
