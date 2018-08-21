package com.hahafather007.voicetotext.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class WaveformView : View {
    private val mPrimaryWidth = 1.0f
    private val mSecondaryWidth = 0.5f
    private var mAmplitude = MIN_AMPLITUDE
    private val mWaveColor = Color.rgb(0x87, 0xce, 0xfa)
    private val mDensity = 2
    private val mWaveCount = 5
    private val mFrequency = 0.1875f
    private val mPhaseShift = -0.1875f
    private val mPath = Path()
    private val mPrimaryPaint = Paint()
    private val mSecondaryPaint = Paint()
    private var mPhase = mPhaseShift

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        initialize()
    }

    private fun initialize() {
        mPrimaryPaint.strokeWidth = mPrimaryWidth
        mPrimaryPaint.isAntiAlias = true
        mPrimaryPaint.style = Paint.Style.STROKE
        mPrimaryPaint.color = mWaveColor

        mSecondaryPaint.strokeWidth = mSecondaryWidth
        mSecondaryPaint.isAntiAlias = true
        mSecondaryPaint.style = Paint.Style.STROKE
        mSecondaryPaint.color = mWaveColor
    }

    fun updateAmplitude(amplitude: Float) {
        mAmplitude = Math.max(amplitude, MIN_AMPLITUDE)
    }

    override fun onDraw(canvas: Canvas) {
        val width = width
        val height = height

        for (l in 0 until mWaveCount) {
            val midH = height / 2.0f
            val midW = width / 2.0f

            val maxAmplitude = midH / 2f - 4.0f
            val progress = 1.0f - l * 1.0f / mWaveCount
            val normalAmplitude = (1.5f * progress - 0.5f) * mAmplitude

            val multiplier = Math.min(1.0, (progress / 3.0f * 2.0f + 1.0f / 3.0f).toDouble()).toFloat()

            if (l != 0) {
                mSecondaryPaint.alpha = (multiplier * 255).toInt()
            }

            mPath.reset()
            var x = 0f
            while (x < width + mDensity) {
                val scaling = 1f - Math.pow((1 / midW * (x - midW)).toDouble(), 2.0).toFloat()
                val y = scaling * maxAmplitude * normalAmplitude * Math.sin(
                        180f * x * mFrequency / (width * Math.PI) + mPhase).toFloat() + midH
                if (x == 0f) {
                    mPath.moveTo(x, y)
                } else {
                    mPath.lineTo(x, y)
                }
                x += mDensity
            }

            if (l == 0) {
                canvas.drawPath(mPath, mPrimaryPaint)
            } else {
                canvas.drawPath(mPath, mSecondaryPaint)
            }
        }

        mPhase += mPhaseShift

        invalidate()
    }

    companion object {
        private const val MIN_AMPLITUDE = 0.066f
    }
}