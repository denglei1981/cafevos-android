package com.changanford.home

import android.content.Context
import androidx.core.content.ContextCompat

import com.changanford.home.news.data.Authors
import com.google.android.material.button.MaterialButton

class SetFollowState(var context: Context) {

    fun setFollowState(btnFollow: MaterialButton, authors: Authors) {
        when (authors.isFollow) {
            0 -> { // 未关注
                btnFollow.text = "关注"
                btnFollow.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_gray_f2f4f9
                    )
                )
                btnFollow.setTextColor(ContextCompat.getColor(context, R.color.blue_tab))
            }
            1 -> {//  已经关注
                btnFollow.text = "已关注"
                btnFollow.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_tab))
                btnFollow.setTextColor(ContextCompat.getColor(context , R.color.white))
            }
        }
    }

}