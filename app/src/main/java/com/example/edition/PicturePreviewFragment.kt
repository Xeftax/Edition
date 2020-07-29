package com.example.edition

import android.os.Bundle
import android.os.Handler
import android.transition.TransitionManager
import android.view.*
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.edition.databinding.ChatPicturePreviewBinding
import kotlinx.android.synthetic.main.chat_picture_preview.*
import org.linphone.activities.main.preview.PicturePreviewViewModel


class PicturePreviewFragment : Fragment() {
    private lateinit var bd: ChatPicturePreviewBinding
    private lateinit var viewModel: PicturePreviewViewModel
    private var masterTools: List<ImageView> = ArrayList()
    private var 
    private var editMode: CharSequence = "preview"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bd = ChatPicturePreviewBinding.inflate(inflater, container, false)
        masterTools = listOf(bd.cropPicturePreview, bd.pencilPicturePreview, bd.linePicturePreview, bd.textPicturePreview)
        return bd.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val textManager = TextManager(this, bd.editionLayout)

        (activity as MainActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        (activity as MainActivity).window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        bd.setEditClickListener {
            bd.editPicturePreview.visibility = View.INVISIBLE
            val constraintSet = ConstraintSet()
            constraintSet.clone(bd.relativeLayout)
            constraintSet.clear(bd.cropPicturePreview.id, ConstraintSet.START)
            constraintSet.connect(bd.cropPicturePreview.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 10)
            constraintSet.clear(bd.sendPicturePreview.id, ConstraintSet.START)
            constraintSet.connect(bd.sendPicturePreview.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 10)
            TransitionManager.beginDelayedTransition(bd.relativeLayout)
            constraintSet.applyTo(bd.relativeLayout)
            //bd.cropPicturePreview.translation(0, 10, 10, 0, 300, 0)
            editMode = "edition"
        }

        bd.setMainClickListener {
            if (editMode == "text") textManager.toggleText()
        }

        bd.setCropClickListener {
            val constraintSet = ConstraintSet()
            constraintSet.clone(bd.relativeLayout)
            constraintSet.constrainHeight(bd.pencilPicturePreview.id, 1)
            TransitionManager.beginDelayedTransition(bd.relativeLayout)
            constraintSet.applyTo(bd.relativeLayout)
            //masterToolsDisplay(bd.cropPicturePreview, "top", "all", 200)
        }

        bd.setPencilClickListener {
            masterToolsDisplay(bd.pencilPicturePreview, "null", "right", 45)
            bd.highlighterPicturePreview.otherToolDisplay(200)
            colorBarDisplay()
        }

        bd.setLineClickListener {
            masterToolsDisplay(bd.linePicturePreview, "null", "right", 100)
            bd.squarePicturePreview.otherToolDisplay(200)
            bd.circlePicturePreview.otherToolDisplay(250)
            colorBarDisplay()
        }

        bd.setTextClickListener {
            masterToolsDisplay(bd.textPicturePreview, "bottom", "top", 150)
            colorBarDisplay()
            editMode = "text"
        }

        bd.setHighlighterClickListener {
            replaceTool(bd.pencilPicturePreview, bd.highlighterPicturePreview, "left")
        }

        bd.setSquareClickListener {
            replaceTool(bd.linePicturePreview, bd.squarePicturePreview, "null")
            if (bd.linePicturePreview.width != 0) bd.circlePicturePreview.otherToolDisplay(0)
        }

        bd.setCircleClickListener {
            replaceTool(bd.linePicturePreview, bd.circlePicturePreview, "left")
            if (bd.linePicturePreview.width != 0) bd.squarePicturePreview.otherToolDisplay(50)
        }

        bd.setSendClickListener {
            (activity as MainActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            (activity as MainActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            findNavController().popBackStack()
        }

        bd.setBackClickListener {
            (activity as MainActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            (activity as MainActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            findNavController().popBackStack()
        }
    }











    private fun masterToolsDisplay(
        toolClicked: View,
        background1: String,
        background2: String,
        delayChange: Long
    ) {
        val updateTools: ArrayList<View> = ArrayList()
        val height: Int
        val width: Int = 40
        if (bd.textPicturePreview.layoutParams.height == 0 || bd.linePicturePreview.layoutParams.height == 0) {
            toolClicked.changeBackground(background1, 0)
            height = 40
        } else {
            toolClicked.changeBackground(background2, delayChange)
            height = 0
        }
        for (tool in masterTools) {
            if (tool != toolClicked) {
                updateTools.add(tool)
            }
        }
        updateTools[0].homothety(width, height, 100, 0)
        updateTools[1].homothety(width, height, 100, 50)
        updateTools[2].homothety(width, height, 100, 100)
    }


    private fun View.otherToolDisplay(offset: Long) {
        val height: Int = 40
        if (layoutParams.width == 0) homothety(40, height, 100, offset)
        else homothety(0, height, 100, 0)
    }


    private fun colorBarDisplay() {
        val height: Int
        val width: Int = 40
        if (bd.barPicturePreview.layoutParams.height == 0) height = 40
        else {
            bd.textPicturePreview.changeBackground("null", 0)
            bd.textPicturePreview.changeBackground("bottom", 465)
            height = 0
        }

        bd.barPicturePreview.homothety(width, (height * 4.5).toInt(), 200, 200)
        bd.pipettePicturePreview.homothety(width, height, 100, 350)
    }


    private fun replaceTool(old: View, new: View, initBackground: String) {
        if (old.width == 0) {
            masterToolsDisplay(old, "null", "right", 45)
            old.otherToolDisplay(0)
            new.otherToolDisplay(0)
            new.changeBackground(initBackground, 100)
            new.changeBackgroundTint("dark", 100)
            colorBarDisplay()
        } else {
            old.otherToolDisplay(0)
            new.changeBackground("top", 150)
            new.changeBackgroundTint("light", 0)
        }
    }


    fun showTools() {
        bd.cropPicturePreview.translation(0, 10, 10, 0, 300, 0)
        bd.backPicturePreview.translation(10, 10, 0, 0, 300, 0)
    }


    fun hideTools() {
        bd.cropPicturePreview.translation(0, 10, -55, 0, 300, 0)
        bd.backPicturePreview.translation(-55, 10, 0, 0, 300, 0)
    }





    private fun View.translation(
        leftFinal: Int,
        topFinal: Int,
        rightFinal: Int,
        bottomFinal: Int,
        duration: Long,
        offset: Long
    ) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        val leftInit = params.leftMargin
        val topInit = params.topMargin
        val rightInit = params.rightMargin
        val bottomInit = params.bottomMargin
        val anim: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val leftIncr =
                    (leftInit + (convertDpToPixel(leftFinal) - leftInit) * interpolatedTime).toInt()
                val topIncr =
                    (topInit + (convertDpToPixel(topFinal) - topInit) * interpolatedTime).toInt()
                val rightIncr =
                    (rightInit + (convertDpToPixel(rightFinal) - rightInit) * interpolatedTime).toInt()
                val bottomIncr =
                    (bottomInit + (convertDpToPixel(bottomFinal) - bottomInit) * interpolatedTime).toInt()
                params.setMargins(leftIncr, topIncr, rightIncr, bottomIncr)
                layoutParams = params
            }
        }
        anim.duration = duration; // in ms
        anim.startOffset = offset
        startAnimation(anim)
    }

    private fun View.homothety(widthFinal: Int, heightFinal: Int, duration: Long, offset: Long) {
        val params = layoutParams
        val widthInit = params.width
        val heightInit = params.height
        val anim: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                params.width =
                    (widthInit + (convertDpToPixel(widthFinal) - widthInit) * interpolatedTime).toInt()
                params.height =
                    (heightInit + (convertDpToPixel(heightFinal) - heightInit) * interpolatedTime).toInt()
                layoutParams = params
            }
        }
        anim.duration = duration; // in ms
        anim.startOffset = offset
        startAnimation(anim)
    }


    fun View.changeBackground(type: String, delay: Long) {
        val handler = Handler()
        handler.postDelayed(Runnable {
            when (type) {
                "null" -> background = ContextCompat.getDrawable(
                    context,
                    R.drawable.button_background_picture_preview_color
                )
                "all" -> background =
                    ContextCompat.getDrawable(context, R.drawable.button_background_picture_preview)
                "left" -> background = ContextCompat.getDrawable(
                    context,
                    R.drawable.button_background_picture_preview_left
                )
                "top" -> background = ContextCompat.getDrawable(
                    context,
                    R.drawable.button_background_picture_preview_top
                )
                "right" -> background = ContextCompat.getDrawable(
                    context,
                    R.drawable.button_background_picture_preview_right
                )
                "bottom" -> background = ContextCompat.getDrawable(
                    context,
                    R.drawable.button_background_picture_preview_bottom
                )
            }
        }, delay)
    }


    fun View.changeBackgroundTint(type: String, delay: Long) {
        val handler = Handler()
        handler.postDelayed(Runnable {
            when (type) {
                "light" -> backgroundTintList = ContextCompat.getColorStateList(
                    this.context,
                    R.color.button_pressed_background_picture_preview
                )
                "dark" -> backgroundTintList =
                    ContextCompat.getColorStateList(this.context, R.color.black_color)
            }
        }, delay)
    }


    fun convertDpToPixel(dp: Int): Int {
        val resources = (activity as MainActivity).getResources()
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi / 160f).toInt()
    }


    fun convertPixelToDp(px: Int): Int {
        val resources = (activity as MainActivity).resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi / 160f).toInt()
    }

    fun getBinding(): ChatPicturePreviewBinding {
        return bd
    }
}