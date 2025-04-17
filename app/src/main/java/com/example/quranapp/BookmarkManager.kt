package com.example.quranapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Bookmark(
    val surahNumber: Int,
    val ayahNumber: Int,
    val ayahText: String,
    val surahName: String? = null,
    val isJuz: Boolean = false
)

object BookmarkManager {
    private const val PREFS_NAME = "QuranAppPrefs"
    private const val BOOKMARKS_KEY = "bookmarks"
    private val gson = Gson()

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun addBookmark(context: Context, bookmark: Bookmark) {
        val bookmarks = getBookmarks(context).toMutableList()
        bookmarks.add(bookmark)
        val editor = getPreferences(context).edit()
        editor.putString(BOOKMARKS_KEY, gson.toJson(bookmarks))
        editor.apply()
    }

    fun getBookmarks(context: Context): List<Bookmark> {
        val json = getPreferences(context).getString(BOOKMARKS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Bookmark>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun removeBookmark(context: Context, bookmark: Bookmark) {
        val bookmarks = getBookmarks(context).toMutableList()
        bookmarks.remove(bookmark)
        val editor = getPreferences(context).edit()
        editor.putString(BOOKMARKS_KEY, gson.toJson(bookmarks))
        editor.apply()
    }
}