package com.example.everydaygraditude.ui

import android.content.*
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.everydaygraditude.databinding.ShareBottomDialogBinding
import com.example.everydaygraditude.datamodels.GratitudeNote
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

/**
 * Share the currentNoteData.text and currentNoteData.author to various platforms.
 * */
class ShareBottomDialog : BottomSheetDialogFragment() {

    private lateinit var binding: ShareBottomDialogBinding

    /**
     * Set the current note clicked to open this dialog via setCurrentNoteData method.
     * The current note will be stored to currentNoteData.
     * */

    private var currentNoteData: GratitudeNote? = null

    fun setCurrentNoteData(currentNoteData: GratitudeNote) {
        this.currentNoteData = currentNoteData
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ShareBottomDialogBinding.inflate(inflater, container, false)
        binding.currNote = currentNoteData

        binding.copyButton.setOnClickListener {
            copyToClipBoard()
        }
        binding.whatsappShareButton.setOnClickListener {
            shareTextOnApp(WHATSAPP)
        }
        binding.facebookShareButton.setOnClickListener {
            shareTextOnApp(FACEBOOK)
        }
        binding.instagramShareButton.setOnClickListener {
            shareTextOnApp(INSTAGRAM)
        }
        binding.moreButton.setOnClickListener {
            moreShareOptions()
        }
        binding.closeButton.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    /**
     * Clip the text to clipboard.
     * */
    private fun copyToClipBoard() {
        val clipboard: ClipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Note", currentNoteData?.text + " - " + currentNoteData?.author)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * Share text on all the available apps.
     * */
    private fun moreShareOptions() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentNoteData?.text + " - " + currentNoteData?.author)

        val chooserIntent = Intent.createChooser(shareIntent, "Share with")
        if (chooserIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(chooserIntent)
        } else {
            Toast.makeText(this.requireContext(), "No apps available to share.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Share text on the apps: Whatsapp, Instagram, Facebook.
     * Parameter: appName from companion object.
     * */
    private fun shareTextOnApp(appName: String) {

        val appIntent = Intent(Intent.ACTION_SEND)
        appIntent.type = "text/plain"
        appIntent.setPackage(when(appName) {
            WHATSAPP -> "com.whatsapp"
            INSTAGRAM -> "com.instagram.android"
            FACEBOOK -> "com.facebook.katana"
            else -> "com.whatsapp"
        })

        appIntent.putExtra(Intent.EXTRA_TEXT, currentNoteData?.text + " - " + currentNoteData?.author)

        try {
            Objects.requireNonNull(activity)?.startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this.requireContext(), "${appName} Not installed.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * On configuration changes cancel the dialog, as data will not persist at the time.
     * */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dismiss()
    }

    companion object {
        const val WHATSAPP = "Whatsapp"
        const val INSTAGRAM = "Instagram"
        const val FACEBOOK = "Facebook"
    }
}