package com.linecorp.highlightview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import android.support.annotation.AttrRes
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation

class HighlightView : ConstraintLayout {
    private lateinit var view: View

    private val highlightPaint: Paint
        get() = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    private val layoutInflater: LayoutInflater
        get() = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var currentTargets: Array<out HighlightTarget>? = null
    private var highlightCircleSizeRatio: Float = 0.0f

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        @AttrRes defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            drawBackgroundColor()
            maybeDrawHighlightCircles()
        }
    }

    private fun initView() {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_HARDWARE, null /* paint */)
        view = layoutInflater
            .inflate(
                R.layout.highlight_view,
                this /* root */,
                false /* attachToRoot */
            )
            .also(::addView)
    }

    private fun startHighlightCircleExpandAnimation(onAnimationEnd: () -> Unit) {
        HighlightCircleExpandAnimation(view = this)
            .apply {
                duration = HIGHLIGHT_CIRCLE_EXPAND_DURATION_MS
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) = onAnimationEnd()

                    override fun onAnimationRepeat(animation: Animation?) {
                        // Do nothing.
                    }

                    override fun onAnimationStart(animation: Animation?) {
                        // Do nothing.
                    }

                })
            }
            .also(::startAnimation)
    }

    private fun startHighlightCircleShrinkAnimation() {
        HighlightCircleShrinkAnimation(view = this)
            .apply { duration = HIGHLIGHT_CIRCLE_SHRINK_DURATION_MS }
            .also(::startAnimation)
    }

    fun highlight(vararg targets: HighlightTarget) {
        this.currentTargets = targets
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bringToFront()
        } else {
            translationZ = DEFAULT_TRANSLATION_Z
        }
        startHighlightCircleExpandAnimation(::startHighlightCircleShrinkAnimation)
    }

    private fun Canvas.drawBackgroundColor() =
        background?.draw(this /* canvas */)
            ?: drawColor(DEFAULT_BACKGROUND_COLOR)

    private fun Canvas.maybeDrawHighlightCircles() =
        currentTargets?.forEach { target: HighlightTarget ->
            target.viewReference.get()?.let { targetView: View ->
                val centerPosition = targetView.getCenterPosition()
                val radius = targetView.getHighlightRadius() + target.highlightPadding.toFloat()
                drawCircle(
                    centerPosition.x,
                    centerPosition.y,
                    radius * highlightCircleSizeRatio /* radius */,
                    highlightPaint
                )
            }
        }

    private fun View.getCenterPosition(): Position =
        Position(x = width / 2.0f + x, y = height / 2.0f + y)

    private fun View.getHighlightRadius(): Float =
        Math.sqrt(Math.pow(width / 2.0, 2.0) + Math.pow(height / 2.0, 2.0)).toFloat()

    fun setHighlightCircleSizeRatio(ratio: Float) {
        highlightCircleSizeRatio = ratio
    }

    companion object {
        private const val DEFAULT_TRANSLATION_Z = 1_000.0f

        const val DEFAULT_HIGHLIGHT_PADDING = 0
        private val DEFAULT_BACKGROUND_COLOR =
            Color.parseColor("#66000000" /* colorString */)

        private const val HIGHLIGHT_CIRCLE_EXPAND_DURATION_MS = 150L
        private const val HIGHLIGHT_CIRCLE_SHRINK_DURATION_MS = 150L
        const val HIGHLIGHT_CIRCLE_MAX_EXPAND_RATIO = 1.2f
    }
}
