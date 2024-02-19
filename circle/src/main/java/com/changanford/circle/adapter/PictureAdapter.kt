package com.changanford.circle.adapter

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.PictureItemBinding
import com.changanford.common.util.PictureUtil
import com.changanford.common.utilext.GlideUtils
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.tools.DoubleUtils
import com.yalantis.ucrop.UCrop
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder


class PictureAdapter(val activity: Activity, private val showedittype: Int, var content:String) :
    BaseBannerAdapter<LocalMedia>() {
//    override fun createViewHolder(itemView: View, viewType: Int): PictureAdapterViewHolder {
//
//        return PictureAdapterViewHolder(itemView, activity = activity, type =type,content)
//    }
//
//    override fun onBind(
//        holder: PictureAdapterViewHolder?,
//        data: LocalMedia,
//        position: Int,
//        pageSize: Int
//    ) {
//        holder?.bindData(data,position,pageSize)
//    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.picture_item
    }

    override fun bindData(holder: BaseViewHolder<LocalMedia>?, data: LocalMedia?, position: Int, pageSize: Int) {
        holder?.let {
            DataBindingUtil.bind<PictureItemBinding>(it.itemView)?.apply {
                data?.apply {
//                    if (showedittype==-1||showedittype == 312)etPicmiaoshu.visibility = View.GONE else etPicmiaoshu.visibility = View.VISIBLE
                    GlideUtils.loadFilePath(PictureUtil.getFinallyPath(data),pic)
                    etPicmiaoshu.setText(content)
                    if (showedittype == 1){
                        etPicmiaoshu.setText(data.contentDesc?:"")
                    }
                    data.contentDesc =etPicmiaoshu.text.toString()
                    tvTiaozhen?.setOnClickListener {
                        if (!DoubleUtils.isFastDoubleClick()) {
                            data.let { it1 -> PictureUtil.getFinallyPath(it1) }?.let { it2 ->
                                PictureUtil.startUCrop(
                                    activity,
                                    it2,
                                    UCrop.REQUEST_CROP,
                                    16f,
                                    9f
                                )
                            }
                        }
                    }

                    etPicmiaoshu.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable) {
                            data.contentDesc = s.toString()

                        }
                    })
                }
            }
        }
    }
}