package com.example.quranapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import com.example.quranapp.ui.AyahScreen
import java.io.Serializable

class SurahDetailActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_SURAH = "extra_surah"
        private const val EXTRA_AYAH_NUMBER = "extra_ayah_number"
        private const val BISMILLAH_TEXT = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"

        fun newIntent(context: Context, surah: Surah, ayahNumber: Int? = null): Intent {
            return Intent(context, SurahDetailActivity::class.java).apply {
                putExtra(EXTRA_SURAH, surah as Serializable)
                ayahNumber?.let { putExtra(EXTRA_AYAH_NUMBER, it) }
            }
        }
    }

    private val apiService by lazy { QuranApi.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val surah = intent.getSerializableExtra(EXTRA_SURAH) as? Surah ?: run {
            finish()
            return
        }
        val targetAyahNumber = intent.getIntExtra(EXTRA_AYAH_NUMBER, -1).takeIf { it != -1 }

        setContent {
            var ayahs by remember { mutableStateOf(listOf<Ayah>()) }
            var surahInfo by remember { mutableStateOf<Triple<String, String, String>?>(null) }
            var bismillahText by remember { mutableStateOf<String?>(null) }
            var selectedAyah by remember { mutableStateOf<Ayah?>(null) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                try {
                    // Ambil data quran-uthmani untuk teks Arab
                    val quranResponse = apiService.getQuranUthmani()
                    // Ambil terjemahan Indonesia
                    val indonesianResponse = apiService.getSurahIndonesian(surah.number)

                    if (quranResponse.isSuccessful && indonesianResponse.isSuccessful) {
                        val quranData = quranResponse.body()?.data
                        val indonesianSurah = indonesianResponse.body()?.data

                        if (quranData != null && indonesianSurah != null) {
                            // Ambil ayat Arab dari quranData untuk Surah yang sesuai
                            val arabicSurah = quranData.surahs.find { it.number == surah.number }
                            if (arabicSurah != null) {
                                // Tentukan apakah Surah ini adalah Surah 9 (At-Taubah)
                                val isSurahExcluded = surah.number == 9
                                val ayahsToDisplay = mutableListOf<Ayah>()

                                if (isSurahExcluded) {
                                    // Untuk Surah 9, tampilkan semua ayat tanpa Bismillah
                                    ayahsToDisplay.addAll(arabicSurah.ayahs.mapIndexed { index, arabicAyah ->
                                        val indonesianAyah = indonesianSurah.ayahs.getOrNull(index)
                                        Ayah(
                                            number = arabicAyah.number,
                                            text = arabicAyah.text,
                                            numberInSurah = arabicAyah.numberInSurah,
                                            translation = Translation(
                                                text = indonesianAyah?.text ?: "Terjemahan tidak tersedia"
                                            )
                                        )
                                    })
                                } else {
                                    // Untuk Surah lainnya, pisahkan Bismillah
                                    arabicSurah.ayahs.forEachIndexed { index, arabicAyah ->
                                        if (index == 0) {
                                            // Ayat pertama: Deteksi dan pisahkan Bismillah
                                            if (arabicAyah.text.startsWith(BISMILLAH_TEXT)) {
                                                bismillahText = BISMILLAH_TEXT
                                                val remainingText = arabicAyah.text.removePrefix(BISMILLAH_TEXT).trim()
                                                if (remainingText.isNotEmpty()) {
                                                    // Jika ada teks setelah Bismillah, tambahkan sebagai ayat
                                                    ayahsToDisplay.add(
                                                        Ayah(
                                                            number = arabicAyah.number,
                                                            text = remainingText,
                                                            numberInSurah = arabicAyah.numberInSurah,
                                                            translation = Translation(
                                                                text = indonesianSurah.ayahs.getOrNull(index)?.text
                                                                    ?: "Terjemahan tidak tersedia"
                                                            )
                                                        )
                                                    )
                                                }
                                            } else {
                                                // Jika ayat pertama tidak mengandung Bismillah, tambahkan apa adanya
                                                ayahsToDisplay.add(
                                                    Ayah(
                                                        number = arabicAyah.number,
                                                        text = arabicAyah.text,
                                                        numberInSurah = arabicAyah.numberInSurah,
                                                        translation = Translation(
                                                            text = indonesianSurah.ayahs.getOrNull(index)?.text
                                                                ?: "Terjemahan tidak tersedia"
                                                        )
                                                    )
                                                )
                                            }
                                        } else {
                                            // Ayat lainnya, tambahkan apa adanya
                                            val indonesianAyah = indonesianSurah.ayahs.getOrNull(index)
                                            ayahsToDisplay.add(
                                                Ayah(
                                                    number = arabicAyah.number,
                                                    text = arabicAyah.text,
                                                    numberInSurah = arabicAyah.numberInSurah,
                                                    translation = Translation(
                                                        text = indonesianAyah?.text ?: "Terjemahan tidak tersedia"
                                                    )
                                                )
                                            )
                                        }
                                    }
                                }

                                ayahs = ayahsToDisplay
                                surahInfo = Triple(
                                    surah.revelationType,
                                    surah.indonesianNameTranslation,
                                    "${surah.numberOfAyahs} Ayat"
                                )
                            } else {
                                errorMessage = "Surah tidak ditemukan dalam data Arab"
                            }
                        } else {
                            errorMessage = "Gagal memuat data Surah"
                        }
                    } else {
                        errorMessage = "Gagal memuat data Surah: ${indonesianResponse.code()}"
                    }
                } catch (e: Exception) {
                    errorMessage = "Gagal memuat Surah: ${e.message}"
                }
            }

            errorMessage?.let { message ->
                AlertDialog(
                    onDismissRequest = { errorMessage = null },
                    title = { Text("Error") },
                    text = { Text(message) },
                    confirmButton = {
                        TextButton(onClick = { errorMessage = null }) {
                            Text("OK")
                        }
                    }
                )
            }

            AyahScreen(
                ayahs = ayahs,
                surahInfo = surahInfo,
                bismillahText = bismillahText,
                targetAyahNumber = targetAyahNumber,
                onAyahClick = { ayah ->
                    selectedAyah = ayah
                },
                onBookmarkClick = { ayah ->
                    BookmarkManager.addBookmark(
                        this@SurahDetailActivity,
                        Bookmark(
                            surahNumber = surah.number,
                            ayahNumber = ayah.number,
                            ayahText = ayah.text,
                            surahName = surah.indonesianName // Gunakan nama Surah dari konteks SurahDetailActivity
                        )
                    )
                    android.widget.Toast.makeText(this@SurahDetailActivity, "Ayat ${ayah.numberInSurah} dari ${surah.indonesianName} telah ditambahkan ke bookmark", android.widget.Toast.LENGTH_SHORT).show()
                }
            )

            selectedAyah?.let { ayah ->
                AlertDialog(
                    onDismissRequest = { selectedAyah = null },
                    title = { Text("Ayat ${ayah.numberInSurah}") },
                    text = { Text("Apakah Anda yakin ingin menambahkan ayat ini ke bookmark?") },
                    confirmButton = {
                        TextButton(onClick = {
                            BookmarkManager.addBookmark(
                                this@SurahDetailActivity,
                                Bookmark(
                                    surahNumber = surah.number,
                                    ayahNumber = ayah.number,
                                    ayahText = ayah.text,
                                    surahName = surah.indonesianName
                                )
                            )
                            android.widget.Toast.makeText(this@SurahDetailActivity, "Ayat ${ayah.numberInSurah} dari ${surah.indonesianName} telah ditambahkan ke bookmark", android.widget.Toast.LENGTH_SHORT).show()
                            selectedAyah = null
                        }) {
                            Text("Bookmark")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedAyah = null }) {
                            Text("Batal")
                        }
                    }
                )
            }
        }
    }
}