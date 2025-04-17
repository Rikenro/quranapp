package com.example.quranapp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuranResponse(val data: QuranData)
data class QuranData(val surahs: List<SurahDetail>)
data class JuzDetailResponse(val data: JuzDetail)
data class SurahDetailResponse(val data: SurahDetail)
data class SurahListResponse(val data: List<SurahInfo>)

data class Surah(
    val number: Int,
    val name: String,
    val indonesianName: String,
    val indonesianNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int,
    val ayahs: List<Ayah> = emptyList()
) : Serializable

data class Juz(
    val number: Int,
    val startingSurah: String,
    val startingAyah: Int,
    val surahNumber: Int
) : Serializable
data class SurahInfo(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val revelationType: String,
    val numberOfAyahs: Int
)

data class JuzDetail(
    val number: Int,
    val ayahs: List<Ayah>
)
data class Ayah(
    val number: Int,
    val numberInSurah: Int,
    val text: String,
    val translation: Translation? = null
) : Serializable

data class Translation(
    @SerializedName("text") val text: String
)

data class SurahDetail(
    val number: Int,
    val name: String,
    @SerializedName("englishName")
    val englishName: String,
    @SerializedName("englishNameTranslation")
    val englishNameTranslation: String,
    @SerializedName("revelationType")
    val revelationType: String,
    @SerializedName("numberOfAyahs")
    val numberOfAyahs: Int,
    val ayahs: List<AyahDetail>
)

data class AyahDetail(
    val number: Int,
    @SerializedName("numberInSurah")
    val numberInSurah: Int,
    val text: String,
)
