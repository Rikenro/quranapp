package com.rikenro.quranapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import com.rikenro.quranapp.ui.SurahScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SurahFragment : Fragment() {
    private val apiService by lazy { QuranApi.instance }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            setContent {
                val surahList = remember { mutableStateListOf<Surah>() }
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    try {
                        // Gunakan getSurahList untuk hanya mengambil informasi dasar Surah
                        val response = apiService.getSurahList()
                        if (response.isSuccessful) {
                            val surahInfos = response.body()?.data ?: emptyList()
                            val surahs = surahInfos.map { surahInfo ->
                                Surah(
                                    number = surahInfo.number,
                                    name = surahInfo.name,
                                    indonesianName = surahInfo.englishName,
                                    indonesianNameTranslation = surahInfo.englishNameTranslation,
                                    revelationType = surahInfo.revelationType,
                                    numberOfAyahs = surahInfo.numberOfAyahs,
                                    ayahs = emptyList() // Tidak mengambil ayat di sini
                                )
                            }
                            surahList.addAll(surahs)
                        } else {
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(context, "Gagal memuat data Surah: ${response.code()}", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            android.widget.Toast.makeText(context, "Gagal memuat data Surah: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                SurahScreen(
                    surahList = surahList,
                    onSurahClick = { surah ->
                        context.startActivity(SurahDetailActivity.newIntent(context, surah))
                    }
                )
            }
        }
    }
}