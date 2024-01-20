package com.example.everydaygraditude.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.everydaygraditude.databinding.ItemGraditudeNoteBinding
import com.example.everydaygraditude.datamodels.GratitudeNote
import javax.inject.Inject

class GratitudeNotesAdapter @Inject constructor(): RecyclerView.Adapter<GratitudeNotesAdapter.NotesItemViewHolder>() {

    private var dataList = ArrayList<GratitudeNote>()

    private var onShareClickListener: ((GratitudeNote) -> Unit)? = null

    fun setOnShareClickListener(action: (GratitudeNote) -> Unit) {
        this.onShareClickListener = action
    }

    fun setDataList(listings: ArrayList<GratitudeNote>?) {
        if(listings != null) {
            this.dataList = listings
        }
        notifyDataSetChanged()
    }

    inner class NotesItemViewHolder(var binding: ItemGraditudeNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position:Int, currNote: GratitudeNote) {
            binding.currNote = currNote
            binding.shareButton.setOnClickListener { onShareClickListener?.invoke(currNote) }

            /**
             * Show Gratitude Ending Icons and text at the very end of the list.
             * */
            binding.endIcon.visibility = if(position == dataList.size-1) View.VISIBLE else View.GONE
            binding.endText.visibility = if(position == dataList.size-1) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesItemViewHolder {
        return NotesItemViewHolder(ItemGraditudeNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NotesItemViewHolder, position: Int) {
        holder.bind(position, dataList[position])
    }

    override fun getItemCount(): Int  = dataList.size
}