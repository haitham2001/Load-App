package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var text = context.getString(R.string.download_btn)
    private var normalButtonColor = 0
    private var loadingButtonColor = 0
    private var textColor = 0
    private var progressCircleColor = 0
    private var statusLoader = 0f
    private val valueAnimator = ValueAnimator.ofFloat(0f,360f)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if(buttonState == ButtonState.Loading) {
            text = context.getString(R.string.loading_btn)
            valueAnimator.start()
        }
        else{
            text = context.getString(R.string.download_btn)
            valueAnimator.cancel()
        }
        invalidate()
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    init {
        textColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
        normalButtonColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
        loadingButtonColor = ResourcesCompat.getColor(resources, R.color.colorAccent, null)
        progressCircleColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)

        valueAnimator.apply {
            duration = 2000
            addUpdateListener {
                statusLoader = it.animatedValue as Float
                invalidate()
            }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //Draw the normal button
        paint.color = normalButtonColor
        canvas?.drawRect(0f,0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        //Draw the loading button
        paint.color = loadingButtonColor
        canvas?.drawRect(0.0f,0.0f,widthSize.toFloat()*statusLoader/360, heightSize.toFloat(), paint)

        //Draw the circle indicating the progress
        paint.color = progressCircleColor
        canvas?.drawArc(width-200f,30f,width - 100f,130f,0f, statusLoader, true, paint)
        
        //Draw the text and its color
        paint.color = textColor
        canvas?.drawText(text,widthSize/2.0f,heightSize/2.0f + 20.0f,paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}