package com.linecorp.highlightview

import android.support.annotation.FloatRange

data class Position(
    @FloatRange(from = 0.0) val x: Float,
    @FloatRange(from = 0.0) val y: Float
)
