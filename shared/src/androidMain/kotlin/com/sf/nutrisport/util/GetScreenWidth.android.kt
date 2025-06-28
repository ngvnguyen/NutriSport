package com.sf.nutrisport.util

import android.content.res.Resources

actual fun getScreenWidth(): Float {
    return Resources.getSystem().displayMetrics.widthPixels/
            Resources.getSystem().displayMetrics.density
}