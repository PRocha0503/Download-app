package com.example.download.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.withStyledAttributes
import com.example.download.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var width = 0.0f
    private var height = 0.0f
    private var progress = 0.0f
    private val circleDiameter = 60f
    private var downloadLabel = context.resources.getString(R.string.download)
    private var mainColor = 0
    private var textColor = 0

    //Keep tack of button status
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                downloadLabel = context.resources.getString(R.string.download)
                updateButtonState(ButtonState.Loading)
            }

            ButtonState.Loading -> {
                buttonAnimate()
            }

            ButtonState.Completed -> {
                invalidate()
            }

        }
    }
    //Go to next state
    fun updateButtonState(state: ButtonState) {
        buttonState = state
    }

    init{
        isClickable = true
//        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
//            mainColor = getColor(
//                R.styleable.LoadingButton_mainColor,
//                0
//            )
//        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton).apply {
            mainColor= getColor(R.styleable.LoadingButton_mainColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor,0)
        }
        typedArray.recycle()
    }
   //Rectangle
    private val rect by lazy { RectF(0f, 0f, width, height) }


    //Paint Rectangle
    private val buttonPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = 40F
    }
    //Draw on the screen
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            buttonPaint.color = mainColor
            drawRect(rect, buttonPaint)
            if (buttonState == ButtonState.Loading) {
                drawRectLoading(canvas)
                drawArcLoading(canvas)
            }
            buttonPaint.color = textColor
            drawText(downloadLabel, width.toFloat() / 2, height.toFloat() / 2 + 0, buttonPaint)

        }
    }
    //Set ssize on the screen
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val minh: Int = paddingTop + paddingBottom + suggestedMinimumHeight
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(minh),
            heightMeasureSpec,
            0
        )
        width = w.toFloat()
        height = h.toFloat()
        setMeasuredDimension(w, h)
    }

    //Animate Loading rect
    private fun drawRectLoading(canvas: Canvas) {
        buttonPaint.color = getColor(context,R.color.black)
        canvas.drawRect(0f, 0f, (width * progress), height, buttonPaint)
    }

    //Animate the loading circle
    private fun drawArcLoading(canvas: Canvas) {
        buttonPaint.color = getColor(context,R.color.purple_700)
        canvas.drawArc(0.75f * width, height * 0.3f, 0.75f * width + circleDiameter,
            height * 0.3f + circleDiameter, 0f, 360f * progress, true, buttonPaint)
    }


    private val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private fun buttonAnimate() {
        valueAnimator.apply {
            duration = 3000
            addUpdateListener {
                progress = animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                    downloadLabel = context.resources.getString(R.string.downloading)
                    isEnabled = false
                }

                override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                    updateButtonState(ButtonState.Completed)
                    downloadLabel = context.resources.getString(R.string.download)
                    isEnabled = true

                }
            })
            start()

        }
    }
}