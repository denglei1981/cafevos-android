package com.changanford.base.view

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.changanford.base.R

class MyTextView : TextView {

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()

    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun init() {
//        var manager = AssetManager()
//        val tf = Typeface.createFromAsset(manager, "font/MHeiPRC-Bold.OTF")
        this.typeface = resources.getFont(R.font.mheiprcbold)
    }
}