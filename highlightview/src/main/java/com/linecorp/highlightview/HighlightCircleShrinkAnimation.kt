package com.linecorp.highlightview

import android.support.annotation.FloatRange
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation

class HighlightCircleShrinkAnimation(private val view: HighlightView) : Animation() {
    override fun applyTransformation(
        @FloatRange(from = 0.0, to = 1.0) interpolatedTime: Float,
        t: Transformation?
    ) = view
        .apply {
            interpolator = DecelerateInterpolator()
            setHighlightCircleSizeRatio(
                START_EXPAND_RATIO + (1 - START_EXPAND_RATIO) * interpolatedTime
            )
        }
        .invalidate()

    companion object {
        private const val START_EXPAND_RATIO = HighlightView.HIGHLIGHT_CIRCLE_MAX_EXPAND_RATIO
    }
}
