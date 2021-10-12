package com.changanford.circle.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import com.changanford.circle.R
import com.luck.picture.lib.entity.LocalMedia
import com.zhpan.bannerview.BaseBannerAdapter


class PictureAdapter(activity: Activity, type: Int) :
    BaseBannerAdapter<LocalMedia, PictureAdapterViewHolder>() {
    var activity: Activity = activity
    var type: Int = type
    override fun createViewHolder(itemView: View, viewType: Int): PictureAdapterViewHolder {

        return PictureAdapterViewHolder(itemView, activity = activity, type =type)
    }

    override fun onBind(
        holder: PictureAdapterViewHolder?,
        data: LocalMedia,
        position: Int,
        pageSize: Int
    ) {
        holder?.bindData(data,position,pageSize)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.picture_item
    }
}