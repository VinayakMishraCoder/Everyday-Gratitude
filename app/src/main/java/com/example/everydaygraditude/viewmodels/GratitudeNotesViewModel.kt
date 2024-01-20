package com.example.everydaygraditude.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.everydaygraditude.datamodels.GratitudeNote
import com.example.everydaygraditude.repositories.repository.GratitudeNotesRepository
import com.example.everydaygraditude.utils.DateTimeUtil
import com.example.everydaygraditude.utils.DateTimeUtil.DATE_FORMAT_1
import com.example.everydaygraditude.utils.InternetConnectionUtil
import com.example.everydaygraditude.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class GratitudeNotesViewModel @Inject constructor(
    private val internetConnectionUtil: InternetConnectionUtil,
    private val repository: GratitudeNotesRepository
) : ViewModel() {

    private val _responseData = MutableLiveData<ResultWrapper<ArrayList<GratitudeNote>>>()
    val responseData: LiveData<ResultWrapper<ArrayList<GratitudeNote>>> get() = _responseData

    val MAX_PREV_DAYS_LIMIT = 8 // 7 previous dates and today's date

    val currDateIndex = MutableLiveData<Int>(0) // currently set date

    var allPreviousDates = ArrayList<String>() // all the dates including the current date

    var currJob: Job? = null

    /**
     * Store the previous 7 dates along with today's date in a list.
     * Traversing it makes it easy to navigate to different dates on the [GratitudeNotesActivity] Screen.
     * */
    fun storeAllPreviousDates() {
        for(i in 0 until MAX_PREV_DAYS_LIMIT) {
            if(i == 0) allPreviousDates.add(
                DateTimeUtil.getCurrentDate()
            )
            else allPreviousDates.add(
                DateTimeUtil.getPreviousDate(allPreviousDates.get(allPreviousDates.size-1), DATE_FORMAT_1)
            )
        }
    }

    fun getNotesData() {

        /**
         * Check if internet facilities are turned on or not.
         * If not no need to do the API call.
         * */
        if(!internetConnectionUtil.isConnectedToInternet()) {
            _responseData.postValue(ResultWrapper.Error("Error : No Internet Connection detected."))
            return
        }

        /**
         * Debounced jobs, Cancel the current ongoing job, then start the requested job.
         * This is done in case the user very fastly changes the dates and a lot requests can accumulate leading
         * to lot of API calls.
         * */
        currJob?.cancel()

        currJob = viewModelScope.launch {

            _responseData.postValue(ResultWrapper.Loading)

            val result = withContext(Dispatchers.IO) {
                try {
                    var currResponse: ArrayList<GratitudeNote> = ArrayList()

                    currDateIndex.value?.let {
                        currResponse = repository.getGratitudeNotes(getAPIFormattedDate(allPreviousDates.get(it)))
                    }

                    if(!currResponse.isNullOrEmpty()) {
                        ResultWrapper.Success(currResponse)
                    } else {
                        ResultWrapper.Error("Empty Response.\nRetry again please.")
                    }

                } catch (e: Exception) {
                    Log.e("error-msg", "Error : ${e.message}")
                    ResultWrapper.Error("Problem fetching notes.\nRetry again please.")
                }
            }

            _responseData.postValue(result)
        }
    }

    private fun getAPIFormattedDate(date: String): String {
        // Format needed for the API call.
        return "" + date[6]+date[7]+date[8]+date[9]+date[3]+date[4]+date[0]+date[1]
    }
}