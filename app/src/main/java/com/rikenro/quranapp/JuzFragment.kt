package com.rikenro.quranapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import com.rikenro.quranapp.ui.JuzScreen

class JuzFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            setContent {
                val juzList = remember { mutableStateListOf<Juz>() }
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    // Gunakan data statis untuk mengisi juzList
                    juzList.addAll(juzData)
                }

                JuzScreen(
                    juzList = juzList,
                    onJuzClick = { juz ->
                        context.startActivity(JuzDetailActivity.newIntent(context, juz))
                    }
                )
            }
        }
    }

    companion object {
        val juzData = listOf(
            Juz(1, "Al-Fatihah", 1, 1),
            Juz(2, "Al-Baqarah", 142, 2),
            Juz(3, "Al-Baqarah", 253, 2),
            Juz(4, "Ali 'Imran", 93, 3),
            Juz(5, "An-Nisa", 24, 4),
            Juz(6, "An-Nisa", 148, 4),
            Juz(7, "Al-Ma'idah", 83, 5),
            Juz(8, "Al-An'am", 111, 6),
            Juz(9, "Al-A'raf", 88, 7),
            Juz(10, "Al-A'raf", 189, 7),
            Juz(11, "At-Taubah", 1, 9),
            Juz(12, "Hud", 6, 11),
            Juz(13, "Yusuf", 53, 12),
            Juz(14, "Al-Hijr", 1, 15),
            Juz(15, "Al-Isra", 1, 17),
            Juz(16, "Al-Kahf", 75, 18),
            Juz(17, "Al-Anbiya", 1, 21),
            Juz(18, "Al-Mu'minun", 1, 23),
            Juz(19, "Al-Furqan", 21, 25),
            Juz(20, "Ash-Shu'ara", 1, 26),
            Juz(21, "An-Naml", 56, 27),
            Juz(22, "Al-Qasas", 51, 28),
            Juz(23, "Al-Ankabut", 46, 29),
            Juz(24, "Ar-Rum", 1, 30),
            Juz(25, "As-Sajdah", 1, 32),
            Juz(26, "Ya-Sin", 28, 36),
            Juz(27, "As-Saffat", 145, 37),
            Juz(28, "Sad", 1, 38),
            Juz(29, "Az-Zumar", 32, 39),
            Juz(30, "An-Naba", 1, 78)
        )
    }
}