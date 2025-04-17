package com.example.quranapp.ui

import android.app.Activity
import android.media.MediaPlayer
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quranapp.*
import com.example.quranapp.R
import kotlinx.coroutines.launch

@Composable
fun AyahScreen(
    ayahs: List<Ayah> = emptyList(),
    groupedAyahs: List<Pair<String?, List<Ayah>>> = emptyList(),
    surahInfo: Triple<String, String, String>?,
    surahNumber: Int? = null,
    juzNumber: Int? = null,
    bismillahText: String? = null,
    targetAyahNumber: Int? = null,
    onAyahClick: (Ayah) -> Unit,
    onBookmarkClick: (Ayah) -> Unit
) {
    val listState = rememberLazyListState()
    val activity = LocalActivity.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val quranApi = QuranApi.instance

    // State untuk data audio, menggunakan Map untuk menyimpan audio berdasarkan nomor ayat global
    val audioAyahsMap = remember { mutableStateMapOf<Int, AudioAyahDetail>() }

    // State untuk MediaPlayer
    val mediaPlayerState = remember { mutableStateOf<MediaPlayer?>(null) }
    val currentPlayingAyah = remember { mutableStateOf<Int?>(null) }

    // Tentukan apakah kita menampilkan Surah atau Juz
    val isSurah = ayahs.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                backgroundColor = Color(0xFF2A4F4D),
                contentColor = Color.Black,
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { activity?.finish() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Al Quran",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    )
                    surahInfo?.let { (revelationType, translationName, ayahCount) ->
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = translationName,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ayahCount,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = revelationType,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            bismillahText?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = Color.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            LazyColumn(state = listState) {
                if (ayahs.isNotEmpty()) {
                    items(ayahs) { ayah ->
                        // Ambil audio untuk ayat ini jika belum ada di map
                        LaunchedEffect(ayah.number) {
                            if (ayah.number !in audioAyahsMap) {
                                try {
                                    Log.d("AyahScreen", "Mengambil audio untuk ayat ${ayah.number}")
                                    val audioResponse = quranApi.getAyahAudio(ayah.number)
                                    if (audioResponse.isSuccessful) {
                                        audioResponse.body()?.data?.let { audioAyah ->
                                            audioAyahsMap[ayah.number] = audioAyah
                                            Log.d("AyahScreen", "Audio untuk ayat ${ayah.number}: ${audioAyah.audio}")
                                        }
                                    } else {
                                        Log.e("AyahScreen", "Gagal mengambil audio ayat ${ayah.number}: ${audioResponse.code()} - ${audioResponse.message()}")
                                    }
                                } catch (e: Exception) {
                                    Log.e("AyahScreen", "Error saat mengambil audio ayat ${ayah.number}: ${e.message}", e)
                                }
                            }
                        }

                        val audioAyah = audioAyahsMap[ayah.number]
                        AyahItem(
                            ayah = ayah,
                            audioUrl = audioAyah?.audio,
                            isPlaying = currentPlayingAyah.value == ayah.number,
                            onClick = { onAyahClick(ayah) },
                            onBookmarkClick = { onBookmarkClick(ayah) },
                            onPlayClick = { url ->
                                if (url != null) {
                                    Log.d("AyahScreen", "Memutar audio untuk ayat ${ayah.number}: $url")
                                    try {
                                        if (currentPlayingAyah.value == ayah.number) {
                                            Log.d("AyahScreen", "Pause audio untuk ayat ${ayah.number}")
                                            mediaPlayerState.value?.pause()
                                            currentPlayingAyah.value = null
                                        } else {
                                            mediaPlayerState.value?.stop()
                                            mediaPlayerState.value?.release()

                                            val mediaPlayer = MediaPlayer().apply {
                                                setDataSource(url)
                                                setOnPreparedListener {
                                                    Log.d("AyahScreen", "MediaPlayer siap, mulai memutar")
                                                    start()
                                                }
                                                setOnErrorListener { mp, what, extra ->
                                                    Log.e("AyahScreen", "MediaPlayer error: what=$what, extra=$extra")
                                                    true
                                                }
                                                setOnCompletionListener {
                                                    Log.d("AyahScreen", "Pemutaran selesai untuk ayat ${ayah.number}")
                                                    currentPlayingAyah.value = null
                                                    release()
                                                    mediaPlayerState.value = null
                                                }
                                                prepareAsync()
                                            }
                                            mediaPlayerState.value = mediaPlayer
                                            currentPlayingAyah.value = ayah.number
                                        }
                                    } catch (e: Exception) {
                                        Log.e("AyahScreen", "Error saat memutar audio: ${e.message}", e)
                                    }
                                } else {
                                    Log.w("AyahScreen", "URL audio tidak tersedia untuk ayat ${ayah.number} (numberInSurah: ${ayah.numberInSurah})")
                                }
                            },
                            onStopClick = {
                                Log.d("AyahScreen", "Menghentikan audio untuk ayat ${ayah.number}")
                                mediaPlayerState.value?.stop()
                                mediaPlayerState.value?.release()
                                mediaPlayerState.value = null
                                currentPlayingAyah.value = null
                            }
                        )
                    }
                }

                groupedAyahs.forEach { (bismillah, ayahList) ->
                    bismillah?.let {
                        item {
                            Text(
                                text = it,
                                color = Color.Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                    items(ayahList) { ayah ->
                        // Ambil audio untuk ayat ini jika belum ada di map
                        LaunchedEffect(ayah.number) {
                            if (ayah.number !in audioAyahsMap) {
                                try {
                                    Log.d("AyahScreen", "Mengambil audio untuk ayat ${ayah.number}")
                                    val audioResponse = quranApi.getAyahAudio(ayah.number)
                                    if (audioResponse.isSuccessful) {
                                        audioResponse.body()?.data?.let { audioAyah ->
                                            audioAyahsMap[ayah.number] = audioAyah
                                            Log.d("AyahScreen", "Audio untuk ayat ${ayah.number}: ${audioAyah.audio}")
                                        }
                                    } else {
                                        Log.e("AyahScreen", "Gagal mengambil audio ayat ${ayah.number}: ${audioResponse.code()} - ${audioResponse.message()}")
                                    }
                                } catch (e: Exception) {
                                    Log.e("AyahScreen", "Error saat mengambil audio ayat ${ayah.number}: ${e.message}", e)
                                }
                            }
                        }

                        val audioAyah = audioAyahsMap[ayah.number]
                        AyahItem(
                            ayah = ayah,
                            audioUrl = audioAyah?.audio,
                            isPlaying = currentPlayingAyah.value == ayah.number,
                            onClick = { onAyahClick(ayah) },
                            onBookmarkClick = { onBookmarkClick(ayah) },
                            onPlayClick = { url ->
                                if (url != null) {
                                    Log.d("AyahScreen", "Memutar audio untuk ayat ${ayah.number}: $url")
                                    try {
                                        if (currentPlayingAyah.value == ayah.number) {
                                            Log.d("AyahScreen", "Pause audio untuk ayat ${ayah.number}")
                                            mediaPlayerState.value?.pause()
                                            currentPlayingAyah.value = null
                                        } else {
                                            mediaPlayerState.value?.stop()
                                            mediaPlayerState.value?.release()

                                            val mediaPlayer = MediaPlayer().apply {
                                                setDataSource(url)
                                                setOnPreparedListener {
                                                    Log.d("AyahScreen", "MediaPlayer siap, mulai memutar")
                                                    start()
                                                }
                                                setOnErrorListener { mp, what, extra ->
                                                    Log.e("AyahScreen", "MediaPlayer error: what=$what, extra=$extra")
                                                    true
                                                }
                                                setOnCompletionListener {
                                                    Log.d("AyahScreen", "Pemutaran selesai untuk ayat ${ayah.number}")
                                                    currentPlayingAyah.value = null
                                                    release()
                                                    mediaPlayerState.value = null
                                                }
                                                prepareAsync()
                                            }
                                            mediaPlayerState.value = mediaPlayer
                                            currentPlayingAyah.value = ayah.number
                                        }
                                    } catch (e: Exception) {
                                        Log.e("AyahScreen", "Error saat memutar audio: ${e.message}", e)
                                    }
                                } else {
                                    Log.w("AyahScreen", "URL audio tidak tersedia untuk ayat ${ayah.number} (numberInSurah: ${ayah.numberInSurah})")
                                }
                            },
                            onStopClick = {
                                Log.d("AyahScreen", "Menghentikan audio untuk ayat ${ayah.number}")
                                mediaPlayerState.value?.stop()
                                mediaPlayerState.value?.release()
                                mediaPlayerState.value = null
                                currentPlayingAyah.value = null
                            }
                        )
                    }
                }
            }

            LaunchedEffect(targetAyahNumber, ayahs) {
                targetAyahNumber?.let { target ->
                    val index = ayahs.indexOfFirst { it.number == target }
                    if (index != -1) {
                        listState.animateScrollToItem(index)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("AyahScreen", "Membersihkan MediaPlayer saat Composable dihancurkan")
            mediaPlayerState.value?.stop()
            mediaPlayerState.value?.release()
            mediaPlayerState.value = null
            currentPlayingAyah.value = null
        }
    }
}

@Composable
fun AyahItem(
    ayah: Ayah,
    audioUrl: String?,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onPlayClick: (String?) -> Unit,
    onStopClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFFF5F5F5),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${ayah.numberInSurah}. ${ayah.text}",
                    color = Color.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Row {
                    IconButton(onClick = { onPlayClick(audioUrl) }) {
                        Icon(
                            painter = painterResource(
                                id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                            ),
                            contentDescription = if (isPlaying) "Pause Audio" else "Play Audio",
                            tint = Color.Black
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ayah.translation?.text ?: "Terjemahan tidak tersedia",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}