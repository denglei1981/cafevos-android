package com.changanford.circle.adapter

import android.annotation.SuppressLint
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CityEntity
import com.changanford.circle.databinding.CityListItemSearchBinding

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CitySearchListAdapter :
    BaseQuickAdapter<CityEntity, BaseViewHolder>(R.layout.city_list_item_search),LoadMoreModule {

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: CityEntity) {
        val binding = DataBindingUtil.bind<CityListItemSearchBinding>(holder.itemView)
        binding?.let {
            binding.cityNameTv.text=item.name
        }

    }
}