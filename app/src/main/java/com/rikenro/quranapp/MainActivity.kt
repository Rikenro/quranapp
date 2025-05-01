package com.rikenro.quranapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.rikenro.quranapp.ui.theme.QuranAppTheme

class MainActivity : FragmentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Konfigurasi Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Cek apakah pengguna sudah login
        if (auth.currentUser == null) {
            // Jika belum login, arahkan ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Tutup MainActivity agar pengguna tidak bisa kembali tanpa login
            return
        }

        // Jika sudah login, lanjutkan ke UI MainActivity
        setContent {
            QuranAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(supportFragmentManager, auth, googleSignInClient)
                }
            }
        }
    }
}

@Composable
fun MainScreen(fragmentManager: androidx.fragment.app.FragmentManager, auth: FirebaseAuth, googleSignInClient: GoogleSignInClient) {
    val tabs = listOf("SURAH", "JUZ", "BOOKMARK")
    var selectedTabIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    var showProfileMenu by remember { mutableStateOf(false) }

    // Dapatkan Activity dari LocalContext
    val context = LocalContext.current
    val activity = context as? FragmentActivity

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
                contentColor = Color.White, // Teks putih
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Teks "Al Quran" sebagai judul
                        Text(
                            text = "Al Quran",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            // Tombol Profil
                            IconButton(onClick = { showProfileMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile",
                                    tint = Color.White
                                )
                            }
                            // Dropdown Menu Profil
                            DropdownMenu(
                                expanded = showProfileMenu,
                                onDismissRequest = { showProfileMenu = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    // Nama pengguna
                                    Text(
                                        text = "Name: ${auth.currentUser?.displayName ?: "Unknown"}",
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    // Email pengguna
                                    Text(
                                        text = "Email: ${auth.currentUser?.email ?: "Unknown"}",
                                        color = Color.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            // Tombol Logout
                            IconButton(onClick = {
                                // Logout dari Firebase
                                auth.signOut()
                                // Logout dari Google Sign-In
                                googleSignInClient.signOut().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Setelah logout berhasil, arahkan ke LoginActivity
                                        val intent = Intent(activity, LoginActivity::class.java)
                                        activity?.startActivity(intent)
                                        activity?.finish()
                                    } else {
                                        // Jika gagal logout dari Google Sign-In, tetap arahkan ke LoginActivity
                                        val intent = Intent(activity, LoginActivity::class.java)
                                        activity?.startActivity(intent)
                                        activity?.finish()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = Color.White
                                )
                            }
                        }
                    }
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