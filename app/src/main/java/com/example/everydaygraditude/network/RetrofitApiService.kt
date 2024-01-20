package com.example.everydaygraditude.network

import com.example.everydaygraditude.datamodels.GratitudeNote
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApiService {

    @GET("/prod/dailyzen/?version=2")
    suspend fun getGratitudeNotes(
        @Query("date") date: String
    ): ArrayList<GratitudeNote>

}