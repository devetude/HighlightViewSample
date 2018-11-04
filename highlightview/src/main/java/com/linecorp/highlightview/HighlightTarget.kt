package com.linecorp.highlightview

import android.support.annotation.IntRange
import android.view.View
import java.lang.ref.WeakReference

data class HighlightTarget(
    private val view: View,
    @IntRange(from = 0) val highlightPaddingPx: Int = HighlightView.DEFAULT_HIGHLIGHT_PADDING_PX
) {
    val viewReference: WeakReference<View> = WeakReference(view)
}
