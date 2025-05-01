package com.rikenro.quranapp

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApi {
    @GET("surah")
    suspend fun getSurahList(): Response<SurahListResponse>

    @GET("quran/quran-uthmani")
    suspend fun getQuranUthmani(): Response<QuranResponse>

    @GET("surah/{surahNumber}/id.indonesian")
    suspend fun getSurahIndonesian(@Path("surahNumber") surahNumber: Int): Response<SurahDetailResponse>

    @GET("juz/{juzNumber}/quran-uthmani")
    suspend fun getJuzArabic(@Path("juzNumber") juzNumber: Int): Response<JuzDetailResponse>

    @GET("juz/{juzNumber}/id.indonesian")
    suspend fun getJuzIndonesian(@Path("juzNumber") juzNumber: Int): Response<JuzDetailResponse>

    @GET("quran/ar.alafasy")
    suspend fun getQuranAudio(): Response<QuranAudioResponse>

    @GET("surah/{surahNumber}/ar.alafasy")
    suspend fun getSurahAudio(@Path("surahNumber") surahNumber: Int): Response<SurahAudioResponse>

    @GET("ayah/{ayahNumber}/ar.alafasy")
    suspend fun getAyahAudio(@Path("ayahNumber") ayahNumber: Int): Response<AyahAudioResponse>

    companion object {
        val instance: QuranApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://api.alquran.cloud/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuranApi::class.java)
        }
    }
}