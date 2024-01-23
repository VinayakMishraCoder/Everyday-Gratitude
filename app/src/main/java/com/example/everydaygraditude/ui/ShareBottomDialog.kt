package com.example.everydaygraditude.ui

import android.content.*
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.everydaygraditude.databinding.ShareBottomDialogBinding
import com.example.everydaygraditude.datamodels.GratitudeNote
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
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
            shareZenOnApps(WHATSAPP)
        }
        binding.facebookShareButton.setOnClickListener {
            shareZenOnApps(FACEBOOK)
        }
        binding.instagramShareButton.setOnClickListener {
            shareZenOnApps(INSTAGRAM)
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
     * Share text and downloaded image on all the available apps.
     * */
    private fun moreShareOptions() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"

        lifecycleScope.launch (Dispatchers.IO) {
            val imageFile = currentNoteData?.dzImageUrl?.let { downloadImageAsync(it) }
            val imageUri = imageFile?.let {
                FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    it
                )
            }

            shareIntent.putExtra(Intent.EXTRA_TEXT, currentNoteData?.text + " - " + currentNoteData?.author)
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val chooserIntent = Intent.createChooser(shareIntent, "Share with")
            if (chooserIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(chooserIntent)
            } else {
                Toast.makeText(this@ShareBottomDialog.requireContext(), "No apps available to share.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Share text and downloaded image on the apps: Whatsapp, Instagram, Facebook.
     * Parameter: appName from companion object.
     * */
    private fun shareZenOnApps(appName: String) {
        val appIntent = Intent(Intent.ACTION_SEND)
        appIntent.type = "image/*"

        appIntent.setPackage(when(appName) {
            WHATSAPP -> "com.whatsapp"
            INSTAGRAM -> "com.instagram.android"
            FACEBOOK -> "com.facebook.katana"
            else -> "com.whatsapp"
        })

        lifecycleScope.launch(Dispatchers.IO) {
            val imageFile = currentNoteData?.dzImageUrl?.let { downloadImageAsync(it) }
            val imageUri = imageFile?.let {
                FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    it
                )
            }

            appIntent.putExtra(Intent.EXTRA_TEXT, currentNoteData?.text + " - " + currentNoteData?.author)
            appIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            appIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            try {
                Objects.requireNonNull(activity)?.startActivity(appIntent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(this@ShareBottomDialog.requireContext(), "${appName} Not installed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Download image temporarily for sharing to other apps.
     * */
    private suspend fun downloadImageAsync(imageUrl: String): File = withContext(Dispatchers.IO) {
        val url = URL(imageUrl)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val inputStream: InputStream = connection.inputStream
        val imageFile = File(requireContext().cacheDir, "shared_image.jpg")

        try {
            val outputStream = FileOutputStream(imageFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream.close()
            connection.disconnect()
        }

        return@withContext imageFile
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