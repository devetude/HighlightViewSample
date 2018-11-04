package com.linecorp.highlightview

import android.view.View
import java.lang.ref.WeakReference

data class HighlightTarget(
    private val view: View,
    val highlightPadding: Int = HighlightView.DEFAULT_HIGHLIGHT_PADDING
) {
    val viewReference: WeakReference<View> = WeakReference(view)
}
