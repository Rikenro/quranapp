package com.rikenro.quranapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import com.rikenro.quranapp.ui.BookmarkScreen

class BookmarkFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            setContent {
                val context = LocalContext.current
                var bookmarks by remember { mutableStateOf(BookmarkManager.getBookmarks(context)) }

                BookmarkScreen(
                    bookmarks = bookmarks,
                    onBookmarkClick = { bookmark ->
                        val surah = Surah(
                            number = bookmark.surahNumber,
                            name = bookmark.surahName ?: "",
                            indonesianName = bookmark.surahName ?: "",
                            indonesianNameTranslation = "",
                            revelationType = "",
                            numberOfAyahs = 0
                        )
                        context.startActivity(SurahDetailActivity.newIntent(context, surah, bookmark.ayahNumber))
                    },
                    onDeleteClick = { bookmark ->
                        // Hapus bookmark menggunakan BookmarkManager
                        BookmarkManager.removeBookmark(context, bookmark)
                        // Perbarui state untuk UI
                        bookmarks = BookmarkManager.getBookmarks(context)
                    }
                )
            }
        }
    }
}