package com.changanford.home.shot.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.home.R
import com.changanford.home.bean.BigShotRecommendBean
import com.changanford.home.databinding.ItemBigShotStateBinding
import com.changanford.home.news.data.SpecialData

class BigShotUserListAdapter  : BaseQuickAdapter<BigShotRecommendBean,BaseDataBindingHolder<ItemBigShotStateBinding>>(R.layout.item_big_shot_state){


    override fun convert(
        holder: BaseDataBindingHolder<ItemBigShotStateBinding>,
        item: BigShotRecommendBean
    ) {

    }
}