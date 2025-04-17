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

class JuzDetailActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_JUZ = "extra_juz"
        private const val BISMILLAH_TEXT = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ"

        fun newIntent(context: Context, juz: Juz): Intent {
            return Intent(context, JuzDetailActivity::class.java).apply {
                putExtra(EXTRA_JUZ, juz as Serializable)
            }
        }
    }

    private val apiService by lazy { QuranApi.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val juz = intent.getSerializableExtra(EXTRA_JUZ) as? Juz ?: run {
            finish()
            return
        }

        setContent {
            var groupedAyahs by remember { mutableStateOf(listOf<Pair<String?, List<Ayah>>>()) }
            var surahInfo by remember { mutableStateOf<Triple<String, String, String>?>(null) }
            var selectedAyah by remember { mutableStateOf<Ayah?>(null) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            var ayahToSurahMap by remember { mutableStateOf<Map<Int, Pair<Int, String>>?>(null) }

            LaunchedEffect(Unit) {
                try {
                    // Ambil data ayat untuk Juz
                    val arabicResponse = apiService.getJuzArabic(juz.number)
                    val indonesianResponse = apiService.getJuzIndonesian(juz.number)

                    if (arabicResponse.isSuccessful && indonesianResponse.isSuccessful) {
                        val arabicJuz = arabicResponse.body()?.data
                        val indonesianJuz = indonesianResponse.body()?.data

                        if (arabicJuz != null && indonesianJuz != null) {
                            // Ambil daftar semua Surah untuk menentukan nomor Surah dari ayat
                            val quranResponse = apiService.getQuranUthmani()
                            if (quranResponse.isSuccessful) {
                                val quranData = quranResponse.body()?.data
                                if (quranData != null) {
                                    // Map ayat ke nomor Surah dan nama Surah
                                    val tempAyahToSurahMap = mutableMapOf<Int, Pair<Int, String>>()
                                    quranData.surahs.forEach { surah ->
                                        surah.ayahs.forEach { ayah ->
                                            tempAyahToSurahMap[ayah.number] = Pair(surah.number, surah.englishName)
                                        }
                                    }
                                    ayahToSurahMap = tempAyahToSurahMap

                                    // Proses ayat dalam Juz dan kelompokkan berdasarkan Surah
                                    val groupedAyahsList = mutableListOf<Pair<String?, List<Ayah>>>()
                                    var currentAyahs = mutableListOf<Ayah>()
                                    var currentBismillah: String? = null
                                    var currentSurahNumber: Int? = null

                                    arabicJuz.ayahs.forEachIndexed { index, arabicAyah ->
                                        val (surahNumber, surahName) = tempAyahToSurahMap[arabicAyah.number] ?: Pair(0, "Unknown")
                                        val isSurahExcluded = surahNumber == 9
                                        val isFirstAyahOfSurah = arabicAyah.numberInSurah == 1

                                        if (isFirstAyahOfSurah && currentAyahs.isNotEmpty()) {
                                            // Simpan ayat-ayat Surah sebelumnya ke dalam grup
                                            groupedAyahsList.add(Pair(currentBismillah, currentAyahs.toList()))
                                            currentAyahs.clear()
                                        }

                                        if (isFirstAyahOfSurah) {
                                            currentSurahNumber = surahNumber
                                            if (!isSurahExcluded) {
                                                // Deteksi dan pisahkan Bismillah
                                                if (arabicAyah.text.startsWith(BISMILLAH_TEXT)) {
                                                    currentBismillah = BISMILLAH_TEXT
                                                    val remainingText = arabicAyah.text.removePrefix(BISMILLAH_TEXT).trim()
                                                    if (remainingText.isNotEmpty()) {
                                                        // Jika ada teks setelah Bismillah, tambahkan sebagai ayat
                                                        currentAyahs.add(
                                                            Ayah(
                                                                number = arabicAyah.number,
                                                                text = remainingText,
                                                                numberInSurah = arabicAyah.numberInSurah,
                                                                translation = Translation(
                                                                    text = indonesianJuz.ayahs.getOrNull(index)?.text
                                                                        ?: "Terjemahan tidak tersedia"
                                                                )
                                                            )
                                                        )
                                                    }
                                                } else {
                                                    // Jika ayat pertama tidak mengandung Bismillah, tambahkan apa adanya
                                                    currentAyahs.add(
                                                        Ayah(
                                                            number = arabicAyah.number,
                                                            text = arabicAyah.text,
                                                            numberInSurah = arabicAyah.numberInSurah,
                                                            translation = Translation(
                                                                text = indonesianJuz.ayahs.getOrNull(index)?.text
                                                                    ?: "Terjemahan tidak tersedia"
                                                            )
                                                        )
                                                    )
                                                }
                                            } else {
                                                currentBismillah = null
                                                currentAyahs.add(
                                                    Ayah(
                                                        number = arabicAyah.number,
                                                        text = arabicAyah.text,
                                                        numberInSurah = arabicAyah.numberInSurah,
                                                        translation = Translation(
                                                            text = indonesianJuz.ayahs.getOrNull(index)?.text
                                                                ?: "Terjemahan tidak tersedia"
                                                        )
                                                    )
                                                )
                                            }
                                        } else {
                                            // Tambahkan ayat ke daftar, kecuali Bismillah
                                            val indonesianAyah = indonesianJuz.ayahs.getOrNull(index)
                                            currentAyahs.add(
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

                                    // Tambahkan grup terakhir jika ada
                                    if (currentAyahs.isNotEmpty()) {
                                        groupedAyahsList.add(Pair(currentBismillah, currentAyahs.toList()))
                                    }

                                    groupedAyahs = groupedAyahsList

                                    // Gunakan informasi dari JuzFragment untuk konsistensi
                                    surahInfo = Triple(
                                        "Juz ${juz.number}",
                                        juz.startingSurah,
                                        "Ayat ${juz.startingAyah}"
                                    )
                                } else {
                                    errorMessage = "Gagal memuat data quran-uthmani"
                                }
                            } else {
                                errorMessage = "Gagal memuat data quran-uthmani: ${quranResponse.code()}"
                            }
                        } else {
                            errorMessage = "Gagal memuat data Juz"
                        }
                    } else {
                        errorMessage = "Gagal memuat data Juz: ${arabicResponse.code()}"
                    }
                } catch (e: Exception) {
                    errorMessage = "Gagal memuat Juz: ${e.message}"
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
                groupedAyahs = groupedAyahs,
                surahInfo = surahInfo,
                onAyahClick = { ayah ->
                    selectedAyah = ayah
                },
                onBookmarkClick = { ayah ->
                    // Gunakan ayahToSurahMap untuk menentukan Surah yang benar
                    val (correctSurahNumber, correctSurahName) = ayahToSurahMap?.get(ayah.number) ?: Pair(0, "Unknown")
                    BookmarkManager.addBookmark(
                        this@JuzDetailActivity,
                        Bookmark(
                            surahNumber = correctSurahNumber,
                            ayahNumber = ayah.number,
                            ayahText = ayah.text,
                            surahName = correctSurahName
                        )
                    )
                    android.widget.Toast.makeText(this@JuzDetailActivity, "Ayat ${ayah.numberInSurah} dari $correctSurahName telah ditambahkan ke bookmark", android.widget.Toast.LENGTH_SHORT).show()
                }
            )

            selectedAyah?.let { ayah ->
                AlertDialog(
                    onDismissRequest = { selectedAyah = null },
                    title = { Text("Ayat ${ayah.numberInSurah}") },
                    text = { Text("Apakah Anda yakin ingin menambahkan ayat ini ke bookmark?") },
                    confirmButton = {
                        TextButton(onClick = {
                            // Gunakan ayahToSurahMap untuk menentukan Surah yang benar
                            val (correctSurahNumber, correctSurahName) = ayahToSurahMap?.get(ayah.number) ?: Pair(0, "Unknown")
                            BookmarkManager.addBookmark(
                                this@JuzDetailActivity,
                                Bookmark(
                                    surahNumber = correctSurahNumber,
                                    ayahNumber = ayah.number,
                                    ayahText = ayah.text,
                                    surahName = correctSurahName
                                )
                            )
                            android.widget.Toast.makeText(this@JuzDetailActivity, "Ayat ${ayah.numberInSurah} dari $correctSurahName telah ditambahkan ke bookmark", android.widget.Toast.LENGTH_SHORT).show()
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