package com.example.everydaygraditude.repositories.repositoryImplementation

import com.example.everydaygraditude.datamodels.GratitudeNote
import com.example.everydaygraditude.network.RetrofitApiService
import com.example.everydaygraditude.repositories.repository.GratitudeNotesRepository
import javax.inject.Inject

class GratitudeNotesRepositoryImpl @Inject constructor(
    private val apiService: RetrofitApiService
): GratitudeNotesRepository {

    override suspend fun getGratitudeNotes(date: String): ArrayList<GratitudeNote> {
        return apiService.getGratitudeNotes(date)
    }

}