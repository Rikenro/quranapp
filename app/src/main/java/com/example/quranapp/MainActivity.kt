package com.example.quranapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import com.example.quranapp.ui.theme.QuranAppTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuranAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(supportFragmentManager)
                }
            }
        }
    }
}

@Composable
fun MainScreen(fragmentManager: androidx.fragment.app.FragmentManager) {
    val tabs = listOf("SURAH", "JUZ", "BOOKMARK")
    var selectedTabIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                backgroundColor = Color(0xFF2A4F4D), // Warna hijau tua (#2A4F4DFF)
                contentColor = Color.White, // Teks hitam
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    // Teks "Al Quran" sebagai judul
                    Text(
                        text = "Al Quran",
                        color = Color.White, // Teks hitam
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    // TabRow di bawah teks "Al Quran"
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        backgroundColor = Color(0xFF2A4F4D), // Warna hijau tua (#2A4F4DFF)
                        contentColor = Color.Gray, // Aksen abu-abu untuk tab yang tidak dipilih
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                text = {
                                    Text(
                                        title,
                                        color = if (selectedTabIndex == index) Color.White else Color.Gray // Hitam untuk tab yang dipilih
                                    )
                                },
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            AndroidView(
                factory = { context ->
                    FragmentContainerView(context).apply {
                        id = page + 1 // Berikan ID unik untuk setiap container
                        fragmentManager.commit {
                            replace(id, when (page) {
                                0 -> SurahFragment()
                                1 -> JuzFragment()
                                2 -> BookmarkFragment()
                                else -> SurahFragment()
                            })
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}