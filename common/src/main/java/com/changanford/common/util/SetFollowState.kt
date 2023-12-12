package com.changanford.common.util

import android.content.Context
import android.widget.TextView
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


    fun setFollowState(btnFollow: TextView, authors: AuthorBaseVo, isGray: Boolean = false) {
        when (authors.isFollow) {
            1 -> {//  已经关注
                btnFollow.text = "已关注"
                btnFollow.setTextColor(
                    ContextCompat.getColor(
                        btnFollow.context,
                        R.color.color_4d16
                    )
                )
                btnFollow.background =
                    ContextCompat.getDrawable(btnFollow.context, R.drawable.cm_followed_new_bg)
//                if (isGray) {
//                    btnFollow.setBackgroundColor(ContextCompat.getColor(context, R.color.color_DD))
//                } else {
//                    btnFollow.setBackgroundColor(ContextCompat.getColor(context, R.color.color_DD))
//                }
//                btnFollow.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            else -> { // 未关注
                btnFollow.text = "关注"
                btnFollow.setTextColor(
                    ContextCompat.getColor(
                        btnFollow.context,
                        R.color.color_1700F4
                    )
                )
                btnFollow.background =
                    ContextCompat.getDrawable(btnFollow.context, R.drawable.cm_follow_new_bg)
//                btnFollow.setBackgroundColor(
//                    ContextCompat.getColor(
//                        context,
//                        R.color.color_gray_f2f4f9
//                    )
//                )
//                btnFollow.setTextColor(ContextCompat.getColor(context, R.color.color_01025C))
            }
        }
    }


}