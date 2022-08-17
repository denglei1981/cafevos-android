package com.changanford.common.util.toolbar

import android.view.MenuItem
import android.view.View

class Builder {
    var title: String? = null
    var leftTitle: String? = null
    var isShowLeftButton = true
    var leftButtonRes = 0
    var rightMenuRes = 0
    var barDarkFont = true
    var backgroundColor = 0
    var leftButtonClickListener: LeftButtonClickListener? = null
    var rightMenuClickListener: RightMenuClickListener? = null

    fun title(`var`: String?): Builder {
        title = `var`
        return this
    }

    fun leftTitle(`var`: String?): Builder {
        leftTitle = `var`
        return this
    }

    fun backgroundColor(`var`: Int): Builder {
        backgroundColor = `var`
        return this
    }

    fun isShowLeftButton(`val`: Boolean): Builder {
        isShowLeftButton = `val`
        return this
    }

    fun leftButtonRes(`val`: Int): Builder {
        leftButtonRes = `val`
        return this
    }

    fun rightMenuRes(`val`: Int): Builder {
        rightMenuRes = `val`
        return this
    }

    fun leftButtonClickListener(`val`: LeftButtonClickListener?): Builder {
        leftButtonClickListener = `val`
        return this
    }

    fun rightMenuClickListener(`val`: RightMenuClickListener?): Builder {
        rightMenuClickListener = `val`
        return this
    }

    fun build(): Builder {
        return this
    }

    interface LeftButtonClickListener {
        fun onClick(view: View?)
    }

    interface RightMenuClickListener {
        fun onClick(item: MenuItem?)
    }
}