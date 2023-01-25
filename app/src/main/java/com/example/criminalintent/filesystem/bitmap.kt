package com.example.criminalintent.filesystem

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.app.Activity
import android.graphics.Point

fun getFileBitmap(filePath: String, destWidth: Int, destHeight: Int) : Bitmap {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(filePath, options)

    val srcWidth = options.outWidth
    val srcHeight = options.outHeight
    var inSampleSize = 1
    if (srcWidth > destWidth || srcHeight > destHeight) {
        val widthScale = srcWidth / destWidth
        val heightScale = srcHeight / destHeight
        inSampleSize = widthScale.coerceAtLeast(heightScale)
    }

    val newOptions = BitmapFactory.Options()
    newOptions.inSampleSize = inSampleSize
    return BitmapFactory.decodeFile(filePath, newOptions)
}

fun getFileBitmap(filePath: String, activity: Activity) : Bitmap {
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size)

    return getFileBitmap(filePath, size.x, size.y)
}