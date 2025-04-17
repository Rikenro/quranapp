package com.example.quranapp.ui

import android.app.Activity
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quranapp.Ayah
import com.example.quranapp.R

@Composable
fun AyahScreen(
    ayahs: List<Ayah> = emptyList(),
    groupedAyahs: List<Pair<String?, List<Ayah>>> = emptyList(),
    surahInfo: Triple<String, String, String>?,
    bismillahText: String? = null,
    targetAyahNumber: Int? = null,
    onAyahClick: (Ayah) -> Unit,
    onBookmarkClick: (Ayah) -> Unit
) {
    val listState = rememberLazyListState()
    val activity = LocalActivity.current

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                backgroundColor = Color(0xFF2A4F4D), // Warna hijau tua (#2A4F4D)
                contentColor = Color.Black, // Teks hitam
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Back
                    IconButton(
                        onClick = { activity?.finish() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White // Ikon hitam
                        )
                    }

                    // Teks "Al Quran" sebagai judul
                    Text(
                        text = "Al Quran",
                        color = Color.White, // Teks hitam
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    )

                    // Informasi Surah di sebelah kanan
                    surahInfo?.let { (revelationType, translationName, ayahCount) ->
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = translationName,
                                color = Color.White, // Teks hitam
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ayahCount,
                                color = Color.White, // Teks hitam
                                fontSize = 14.sp
                            )
                            Text(
                                text = revelationType,
                                color = Color.White, // Teks sekunder abu-abu
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
                .background(Color.White) // Latar belakang putih
                .padding(paddingValues)
        ) {
            // Tampilkan Bismillah di bawah AppBar untuk SurahDetailActivity
            bismillahText?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = Color.Black, // Teks hitam
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            LazyColumn(state = listState) {
                // Untuk SurahDetailActivity
                if (ayahs.isNotEmpty()) {
                    items(ayahs) { ayah ->
                        AyahItem(
                            ayah = ayah,
                            onClick = { onAyahClick(ayah) },
                            onBookmarkClick = { onBookmarkClick(ayah) }
                        )
                    }
                }

                // Untuk JuzDetailActivity
                groupedAyahs.forEach { (bismillah, ayahList) ->
                    // Tampilkan Bismillah sebagai header jika ada
                    bismillah?.let {
                        item {
                            Text(
                                text = it,
                                color = Color.Black, // Teks hitam
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                    items(ayahList) { ayah ->
                        AyahItem(
                            ayah = ayah,
                            onClick = { onAyahClick(ayah) },
                            onBookmarkClick = { onBookmarkClick(ayah) }
                        )
                    }
                }
            }

            // Scroll otomatis ke ayat yang di-bookmark
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
}

@Composable
fun AyahItem(
    ayah: Ayah,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFFF5F5F5), // Latar belakang card abu-abu sangat terang
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "${ayah.numberInSurah}. ${ayah.text}",
                    color = Color.Black, // Teks hitam
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ayah.translation?.text ?: "Terjemahan tidak tersedia",
                    color = Color.Gray, // Teks sekunder abu-abu
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}