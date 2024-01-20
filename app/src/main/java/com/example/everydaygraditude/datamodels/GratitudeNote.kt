package com.example.everydaygraditude.datamodels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GratitudeNote(
    @SerializedName("articleUrl")
    val articleUrl: String? = null,

    @SerializedName("author")
    val author: String? = null,

    @SerializedName("bgImageUrl")
    val bgImageUrl: String? = null,

    @SerializedName("dzImageUrl")
    val dzImageUrl: String? = null,

    @SerializedName("dzType")
    val dzType: String? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("primaryCTAText")
    val primaryCTAText: String? = null,

    @SerializedName("sharePrefix")
    val sharePrefix: String? = null,

    @SerializedName("text")
    val text: String? = null,

    @SerializedName("theme")
    val theme: String? = null,

    @SerializedName("themeTitle")
    val themeTitle: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("uniqueId")
    val uniqueId: String? = null
) : Parcelable

