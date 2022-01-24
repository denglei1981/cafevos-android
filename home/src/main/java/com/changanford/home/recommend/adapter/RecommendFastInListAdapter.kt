package com.changanford.home.recommend.adapter

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.wutil.ScreenUtils
import com.changanford.home.R
import com.changanford.home.data.AdBean
import com.changanford.home.databinding.ItemHomeRecommendFastInBinding

// 不确定什么布局
class RecommendFastInListAdapter :
    BaseQuickAdapter<AdBean, BaseDataBindingHolder<ItemHomeRecommendFastInBinding>>(R.layout.item_home_recommend_fast_in) {


    var isWith=false
    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeRecommendFastInBinding>,
        item: AdBean
    ) {
//        val imgSize=(ScreenUtils.getScreenWidth(context)-ScreenUtils.dp2px(context,100f))/3

        holder.dataBinding?.let {
            GlideUtils.loadBD(item.adImg, it.ivOne)
            it.ivOne.setOnClickListener {
                JumpUtils.instans?.jump(item.jumpDataType,item.jumpDataValue)
            }
            try{
//                if(isWith){
//                    val layoutParams = ConstraintLayout.LayoutParams(imgSize, imgSize)
//                    layoutParams.marginEnd=10
//                    layoutParams.marginStart=10
//
//                    it.ivOne.layoutParams= layoutParams
//                }

                it.tvAdName.text=item.adSubName
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }


}