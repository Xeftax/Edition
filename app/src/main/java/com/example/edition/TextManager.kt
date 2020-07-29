package com.example.edition

import android.app.Activity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import kotlin.math.pow
import kotlin.math.sqrt


class TextManager(var fragment: PicturePreviewFragment, var layout: ConstraintLayout) : View.OnTouchListener{
    private var nbTextView: Int = 0
    private var editText: Boolean = false
    private lateinit var currentText: EditText

    private var timer: Long = 0
    private var hDelta: Float = 0f
    private var vDelta: Float = 0f
    private  var move: Boolean = false

    fun toggleText(){
        Log.d("editText", editText.toString())
        if (!editText) {
            fragment.hideTools()
            addText()
        } else {
            fragment.showTools()
            quitText(currentText)
        }
    }

    private fun addText(){
        val textView = EditText(layout.context, null, 0, R.style.TextTool)
        textView.id = nbTextView
        textView.background = ContextCompat.getDrawable(textView.context, R.drawable.text_border)
        textView.setOnTouchListener(this)
        textView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (textView.text.toString() != "") textView.setPadding(10,0,10,0)
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        layout.addView(textView)
        nbTextView++

        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        constraintSet.connect(textView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0)
        constraintSet.connect(textView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        constraintSet.connect(textView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0)
        constraintSet.connect(textView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        constraintSet.setVerticalBias(textView.id, 0.7f)
        constraintSet.applyTo(layout)

        editText(textView)
    }


    private fun editText(textView: EditText){
        textView.hint = ""
        textView.isCursorVisible = true
        textView.isFocusableInTouchMode = true
        textView.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        textView.requestFocus()
        currentText = textView
        editText = true
        showKeyboard()

        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)

        val vInitBias =(textView.layoutParams as ConstraintLayout.LayoutParams).verticalBias
        val hInitBias =(textView.layoutParams as ConstraintLayout.LayoutParams).horizontalBias

        HeightProvider(fragment.activity as MainActivity).init().setHeightListener(object :
            HeightProvider.HeightListener {
            override fun onHeightChanged(height: Int) {
                if (height == 0) {
                    constraintSet.setVerticalBias(textView.id, vInitBias)
                    constraintSet.setHorizontalBias(textView.id, hInitBias)
                    TransitionManager.beginDelayedTransition(layout)
                    constraintSet.applyTo(layout)
                    Log.d("ids", textView.id.toString())
                } else {
                    val hBias = 0.5f
                    val vBias = 1 - (height + textView.height/2)/ layout.height.toFloat()
                    constraintSet.setVerticalBias(textView.id, vBias)
                    constraintSet.setHorizontalBias(textView.id, hBias)
                    TransitionManager.beginDelayedTransition(layout)
                    constraintSet.applyTo(layout)

                }
            }
        })
    }

    private fun quitText(textView: EditText){
        val text: String = textView.text.toString()
        if (text.replace("\\s+","") == "") {
            textView.text = null
            textView.hint = "Text"
            textView.setPadding(10,0,10,0)
        } else textView.background = null
        textView.isCursorVisible = false
        textView.clearFocus()
        textView.requestFocus(EditText.FOCUS_DOWN)
        hideKeyboard()
        editText = false
    }

    private fun hideKeyboard() {
        val inputMethodManager = fragment.activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(fragment.activity?.currentFocus?.windowToken, 0)
    }

    private fun showKeyboard() {
        val inputMethodManager = fragment.activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(fragment.activity?.currentFocus?.windowToken, InputMethodManager.SHOW_FORCED, 0)
    }


    override fun onTouch(textView: View, event: MotionEvent): Boolean {
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)
        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                currentText = textView as EditText
                timer = System.currentTimeMillis()
                move = false
                textView.background = ContextCompat.getDrawable(textView.context, R.drawable.text_border)

                hDelta = event.rawX/(layout.width - textView.width) - (textView.layoutParams as ConstraintLayout.LayoutParams).horizontalBias
                vDelta =  event.rawY/(layout.height - textView.height) - (textView.layoutParams as ConstraintLayout.LayoutParams).verticalBias
            }
            MotionEvent.ACTION_MOVE -> {
                val hLayout = (textView.layoutParams as ConstraintLayout.LayoutParams).horizontalBias
                val vLayout = (textView.layoutParams as ConstraintLayout.LayoutParams).verticalBias
                val hFinger = event.rawX/(layout.width - textView.width)
                val vFinger = event.rawY/(layout.height - textView.height)

                if (move && !editText) {
                    var hBias = hFinger - hDelta
                    var vBias = vFinger - vDelta
                    when {
                        hBias < 0 -> hBias = 0f
                        hBias > 1 -> hBias = 1f
                        vBias < 0 -> vBias = 0f
                        vBias > 1 -> vBias = 1f
                    }
                    constraintSet.setHorizontalBias(textView.id,hBias)
                    constraintSet.setVerticalBias(textView.id,vBias)
                } else if (sqrt((hFinger - hLayout - hDelta).pow(2) + 4*(vFinger - vLayout - vDelta).pow(2)) >= 0.01f) move = true

            }
            MotionEvent.ACTION_UP -> {
                if (System.currentTimeMillis() - timer <= 500 && !move) {
                    editText(textView as EditText)

                } else if ((textView as EditText).hint != "Text") textView.background = null

            }
        }

        constraintSet.applyTo(layout)
        layout.invalidate()
        return true
    }

}
