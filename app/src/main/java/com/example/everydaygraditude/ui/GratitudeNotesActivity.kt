package com.example.everydaygraditude.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.everydaygraditude.R
import com.example.everydaygraditude.adapters.GratitudeNotesAdapter
import com.example.everydaygraditude.databinding.ActivityGratitudeNotesBinding
import com.example.everydaygraditude.datamodels.GratitudeNote
import com.example.everydaygraditude.utils.DateTimeUtil
import com.example.everydaygraditude.utils.ResultWrapper
import com.example.everydaygraditude.utils.ShareActions.READ_ACTION
import com.example.everydaygraditude.utils.ShareActions.SEND_ACTION
import com.example.everydaygraditude.utils.ShareActions.SHARE_ACTION
import com.example.everydaygraditude.viewmodels.GratitudeNotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GratitudeNotesActivity : AppCompatActivity() {

    lateinit var binding: ActivityGratitudeNotesBinding

    private val vm: GratitudeNotesViewModel by viewModels()

    @Inject lateinit var adapter : GratitudeNotesAdapter

    var bottomShareDialogFragment: ShareBottomDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gratitude_notes)

        supportActionBar?.hide()

        /**
         * Dismiss the previously opened dialog in case of any change(configuration such as theme, internet, etc.)
         * */
        bottomShareDialogFragment?.dismiss()

        /**
         * Actions to be taken on clicking share button on any of the Note Cards.
         * */
        adapter.setOnShareClickListener {
            when(it.dzType) {
                SEND_ACTION -> {
                    openShareBottomDialog(it)
                }
                SHARE_ACTION -> {
                    openShareBottomDialog(it)
                }
                READ_ACTION -> {
                    readNoteOnBrowser(it)
                }
                else -> {
                    openShareBottomDialog(it)
                }
            }
        }

        binding.notesRecyclerView.adapter = adapter

        binding.refreshLayout.setOnRefreshListener {
            vm.getNotesData()
        }

        binding.previousButton.setOnClickListener {
            movePreviousDay()
        }

        binding.nextButton.setOnClickListener {
            moveNextDay()
        }

        vm.storeAllPreviousDates()

        observeData()

        vm.getNotesData()
    }

    private fun observeData() {
        /**
         * Observe response and refresh.
         * */
        vm.responseData.observe(this) { response ->
            when (response) {
                is ResultWrapper.Loading -> {
                    binding.refreshLayout.isRefreshing = true
                }
                is ResultWrapper.Success -> {
                    binding.refreshLayout.isRefreshing = false
                    adapter.setDataList(response.data)
                }
                is ResultWrapper.Error -> {
                    binding.refreshLayout.isRefreshing = false
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * 1. Set the current date to the top of the screen.
         * 2. This date can be obtained through the current-date-index selected which is stored in the
         * viewModel in variable currDateIndex.
         * 3. Handled visibility of next and previous buttons, changing date format according to figma.
         * 4. Then refresh
         * */
        vm.currDateIndex.observe(this) { currDateIndex ->
            binding.currDay = when(currDateIndex) {
                0-> {
                    binding.nextButton.visibility = View.GONE
                    "Today"
                }
                1 -> {
                    binding.previousButton.visibility = View.VISIBLE
                    binding.nextButton.visibility = View.VISIBLE
                    "Yesterday"
                }
                vm.allPreviousDates.size-1 -> {
                    binding.previousButton.visibility = View.GONE
                    DateTimeUtil.convertDatePattern(vm.allPreviousDates.get(currDateIndex))
                }
                else -> {
                    binding.previousButton.visibility = View.VISIBLE
                    binding.nextButton.visibility = View.VISIBLE
                    DateTimeUtil.convertDatePattern(vm.allPreviousDates.get(currDateIndex))
                }
            }
            vm.getNotesData()
        }
    }

    /**
     * Change the currDateIndex to move next day, decrement it as the days are stored in that order.
     * This change is being observed in the [observeData] method.
     * */
    private fun moveNextDay() {
        vm.currDateIndex.value?.let { currDateIndex ->
            if(currDateIndex == 0) {
                Toast.makeText(this, "Already reached the most recent notes.", Toast.LENGTH_SHORT).show()
            } else {
                vm.currDateIndex.value = currDateIndex-1
            }
        }
    }

    /**
     * Change the currDateIndex to move previous day, increment it as the days are stored in that order.
     * This change is being observed in the [observeData] method.
     * */
    private fun movePreviousDay() {
        vm.currDateIndex.value?.let { currDateIndex ->
            if(currDateIndex == vm.MAX_PREV_DAYS_LIMIT-1) {
                Toast.makeText(this, "Can view only up to 7 days in past. Sorry.", Toast.LENGTH_SHORT).show()
            } else {
                vm.currDateIndex.value = currDateIndex+1
            }
        }
    }

    /**
     * Open share note dialog.
     * */
    private fun openShareBottomDialog(note: GratitudeNote) {
        bottomShareDialogFragment = ShareBottomDialog().apply {
            setCurrentNoteData(note)
        }
        bottomShareDialogFragment?.let { shareFragment ->
            shareFragment.show(supportFragmentManager, shareFragment.tag)
        }
    }

    /**
     * Open note via browser using [GratitudeNote.articleUrl].
     * */
    private fun readNoteOnBrowser(note: GratitudeNote) {
        if(note.articleUrl == null) {
            Toast.makeText(this, "Sorry Website not available at the moment.", Toast.LENGTH_SHORT).show()
            return
        }
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(note.articleUrl)
        startActivity(openURL)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        bottomShareDialogFragment?.dismiss()
    }
}