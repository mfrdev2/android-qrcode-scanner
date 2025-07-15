package com.ba.qrc_scanner.utils

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.ba.qrc_scanner.R
import com.ba.qrc_scanner.data.remote.RetrofitClient
import com.bumptech.glide.Glide

@BindingAdapter("visibleIfNotEmpty")
fun setVisibleIfNotEmpty(view: View, text: String?) {
    view.visibility = if (text.isNullOrBlank()) View.GONE else View.VISIBLE
}

@BindingAdapter("imageUrl")
fun loadImageUrl(view: ImageView, imageId: String?) {
    if (imageId.isNullOrBlank()) {
        view.setImageResource(R.drawable.default_profile)
        return
    }
    val imageUrl = if (imageId.startsWith("http://") || imageId.startsWith("https://")) {
        imageId
    } else {
        "${RetrofitClient.BASE_URL}public/attachment/$imageId"
    }

    Log.d("imageUrl", imageUrl)
    loadImage(view, imageUrl)
}

fun loadImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
        .load(url)
        .placeholder(R.drawable.loading_spinner)
        .error(R.drawable.default_profile)
        .fallback(R.drawable.default_profile)
        .into(imageView)

}




