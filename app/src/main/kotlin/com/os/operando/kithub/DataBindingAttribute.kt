package com.os.operando.kithub

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun ImageView.loadUrl(url: String?) {
    url?.let {
        Picasso.with(context).load(it).into(this)
    }
}