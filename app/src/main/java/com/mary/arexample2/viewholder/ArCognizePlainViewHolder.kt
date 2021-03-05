package com.mary.arexample2.viewholder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.mary.arexample2.R

class ArCognizePlainViewHolder(context : Context) {

    companion object {
        private const val TAG = "ArCognizePlainViewHolde"
    }

    var view : View = LayoutInflater.from(context).inflate(R.layout.view_holder_cognize_plane,null)

}