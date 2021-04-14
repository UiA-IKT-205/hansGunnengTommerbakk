package com.example.huskeliste.lists.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tasks(val task:String, var statCheckBox:Boolean = false):Parcelable