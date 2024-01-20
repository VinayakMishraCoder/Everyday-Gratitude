package com.example.everydaygraditude.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.example.everydaygraditude.R
import com.squareup.picasso.Picasso

object BindingAdapter {
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(view: AppCompatImageView, url:String?){
        if(url.isNullOrEmpty() == true) {
            view.setImageResource(R.drawable.gratitudeface)
        } else {
            Picasso.get().load(url)
                .placeholder(R.drawable.gratitudeface)
                .error(R.drawable.gratitudeface)
                .into(view)
        }
    }

    @BindingAdapter("setVisibility")
    @JvmStatic
    fun visibility(view: View, visibilityStatus: Boolean){
        if(visibilityStatus) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}