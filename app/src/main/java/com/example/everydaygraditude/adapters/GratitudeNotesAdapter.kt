package com.example.everydaygraditude.adapters

import android.view.LayoutInflater
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
        fun bind(currNote: GratitudeNote) {
            binding.currNote = currNote
            binding.shareButton.setOnClickListener { onShareClickListener?.invoke(currNote) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesItemViewHolder {
        return NotesItemViewHolder(ItemGraditudeNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NotesItemViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int  = dataList.size
}