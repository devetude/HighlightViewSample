package com.linecorp.highlightview

import android.support.annotation.FloatRange
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation

class HighlightCircleExpandAnimation(private val view: HighlightView) : Animation() {
    override fun applyTransformation(
        @FloatRange(from = 0.0, to = 1.0) interpolatedTime: Float,
        t: Transformation?
    ) = view
        .apply {
            interpolator = AccelerateInterpolator()
            setHighlightCircleSizeRatio(
                HighlightView.HIGHLIGHT_CIRCLE_MAX_EXPAND_RATIO * interpolatedTime
            )
        }
        .invalidate()
}
