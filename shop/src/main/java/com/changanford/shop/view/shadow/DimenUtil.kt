package com.changanford.shop.view.shadow

import android.content.res.Resources

object DimenUtil {

    fun dp2px(dpValue:Float ):Float{
        return  (0.5f + dpValue * Resources.getSystem().displayMetrics.density)
    }

}