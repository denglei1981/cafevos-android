package com.changanford.common.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.changanford.common.R
import com.changanford.common.bean.AuthorBaseVo


import com.google.android.material.button.MaterialButton

class SetFollowState(var context: Context) {


    fun setFollowState(btnFollow: MaterialButton, authors: AuthorBaseVo, isGray: Boolean = false) {
        when (authors.isFollow) {
            1 -> {//  已经关注
                btnFollow.text = "已关注"
                if (isGray) {
                    btnFollow.setBackgroundColor(ContextCompat.getColor(context, R.color.color_DD))
                } else {
                    btnFollow.setBackgroundColor(ContextCompat.getColor(context, R.color.color_DD))
                }
                btnFollow.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            else -> { // 未关注
                btnFollow.text = "关注"
                btnFollow.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_gray_f2f4f9
                    )
                )
                btnFollow.setTextColor(ContextCompat.getColor(context, R.color.color_01025C))
            }
        }
    }





}