package com.example.everydaygraditude.repositories.repository

import com.example.everydaygraditude.datamodels.GratitudeNote

interface GratitudeNotesRepository {
    suspend fun getGratitudeNotes(date: String) : ArrayList<GratitudeNote>
}