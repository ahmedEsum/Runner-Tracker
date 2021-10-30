package com.example.android.runnertraker.utils

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("setImage")
fun setImage (imageView: ImageView , bitmap: Bitmap?){
    Glide.with(imageView.context).load(bitmap).into(imageView)
}

@BindingAdapter("setDate")
fun setDate(textView: TextView,timeStamp: Long){
    val calender = Calendar.getInstance().apply {
        timeInMillis= timeStamp
    }
    val format = SimpleDateFormat("dd.MM.yy",Locale.getDefault())
    textView.text=format.format(calender.time)
}

@BindingAdapter("setTime")
fun setTime (textView: TextView , long: Long){
    textView.text=TrackingUtility.getTimeFormatted(long)
}

@BindingAdapter("setDistance")
fun setDistance (textView: TextView , distance : Int){
    textView.text= "${(distance / 1000f)} KM"
}


@BindingAdapter("setSpeed")
fun setSpeed (textView: TextView , speed : Float){
    textView.text="${(speed/1000f)} KMH"
}


@BindingAdapter("setCalories")
fun setCalories(textView: TextView , calories : Int){
    textView.text= "$calories kcal"
}
