package com.mary.arexample2.viewholder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.mary.arexample2.R
import com.mary.arexample2.util.DlogUtil
import com.mary.arexample2.util.event.ESSArrow
import com.mary.arexample2.util.event.EventCenter

class ArMeasureHeightViewHolder(context: Context) {

    companion object {
        private const val TAG = "ArMeasureHeightViewHolder"
    }

    interface ArMeasureHeightViewHolderDelegate {
        fun confirm()
    }

    private lateinit var editTextMeter: EditText
    private lateinit var textViewNext: TextView

    var view: View = LayoutInflater.from(context).inflate(R.layout.view_holder_measure_height, null)
    lateinit var arMeasureHeightViewHolderDelegate : ArMeasureHeightViewHolderDelegate

    init {
        findView()
        setListener()
    }

    private fun findView() {
        editTextMeter = view.findViewById(R.id.editTextMeter)
        textViewNext = view.findViewById(R.id.textViewNext)
    }

    private fun setListener() {
        view.setOnClickListener {
            DlogUtil.d(TAG, "으응?")
        }

        editTextMeter.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            var handle : Boolean = false

            if(actionId == EditorInfo.IME_ACTION_NEXT) {
                DlogUtil.d(TAG, "next")
                handle = true
                sendMeter()
            }

            return@OnEditorActionListener handle
        })


        textViewNext.setOnClickListener {
            sendMeter()
        }
    }

    private fun sendMeter() {
        if (editTextMeter.text.toString() == "" || editTextMeter.text.isNullOrEmpty()) {
            DlogUtil.d(TAG, "비엇다 비엇어")
        } else {
            var hashMap : HashMap<String?, Any?> = hashMapOf()
            hashMap["height"] = editTextMeter.text
            EventCenter.sendEvent(ESSArrow.ENTER_HEIGHT_METER, this, hashMap )
            arMeasureHeightViewHolderDelegate.confirm()
        }
    }
}