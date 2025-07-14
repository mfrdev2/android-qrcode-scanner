package com.ba.qrc_scanner.utils

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visibleIfNotEmpty")
fun setVisibleIfNotEmpty(view: View, text: String?) {
    view.visibility = if (text.isNullOrBlank()) View.GONE else View.VISIBLE
}
