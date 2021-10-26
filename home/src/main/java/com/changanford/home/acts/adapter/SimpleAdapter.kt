package com.changanford.home.acts.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.bean.CircleHeadBean
import com.changanford.home.databinding.ItemHomeBarBannerBinding
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class SimpleAdapter : BaseBannerAdapter<CircleHeadBean, PostBarBannerViewHolder>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_home_bar_banner
    }

    override fun createViewHolder(itemView: View?, viewType: Int): PostBarBannerViewHolder {
        return PostBarBannerViewHolder(itemView!!)
    }

    override fun onBind(
        holder: PostBarBannerViewHolder?,
        data: CircleHeadBean?,
        position: Int,
        pageSize: Int
    ) {
        holder!!.bindData(data, position, pageSize)
    }
}

class PostBarBannerViewHolder(itemView: View) : BaseViewHolder<CircleHeadBean>(itemView) {
    override fun bindData(data: CircleHeadBean?, position: Int, pageSize: Int) {
        val binding = DataBindingUtil.bind<ItemHomeBarBannerBinding>(itemView)
        binding?.ivBanner?.load(data?.adImg)
        binding?.ivBanner?.setOnClickListener {
            try{
                JumpUtils.instans?.jump(data?.jumpDataType,data?.jumpDataValue)
            }catch (e:Exception){
                e.printStackTrace()
                toastShow(e.message.toString())
            }
        }
    }

}