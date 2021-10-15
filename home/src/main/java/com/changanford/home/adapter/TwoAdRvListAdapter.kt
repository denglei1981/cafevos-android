package com.changanford.home.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.SetFollowState
import com.changanford.home.bean.BigShotRecommendBean
import com.changanford.home.data.AdBean
import com.changanford.home.data.TwoAdData
import com.changanford.home.databinding.ItemBigShotStateBinding
import com.changanford.home.databinding.ItemTwoRvAdBinding
import com.google.android.material.button.MaterialButton

class TwoAdRvListAdapter :
    BaseQuickAdapter<AdBean, BaseDataBindingHolder<ItemTwoRvAdBinding>>(R.layout.item_two_rv_ad) {


    override fun convert(
        holder: BaseDataBindingHolder<ItemTwoRvAdBinding>,
        item: AdBean
    ) {
        holder.dataBinding?.let {
            GlideUtils.loadBD(item.adImg, it.ivThumbs)
            it.tvTips.text = item.adName
            it.tvTitle.text = item.adSubName
            if (item.isVideo == 1) {
                it.ivPlay.visibility = View.VISIBLE
            } else {
                it.ivPlay.visibility = View.GONE
            }
        }
    }


}