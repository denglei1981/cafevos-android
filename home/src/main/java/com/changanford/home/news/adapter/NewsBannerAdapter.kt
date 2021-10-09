package com.changanford.home.news.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.home.R
import com.changanford.home.bean.SpecialListBean
import com.changanford.home.databinding.ItemNewsBarBannerBinding
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class NewsBannerAdapter : BaseBannerAdapter<SpecialListBean, PostBarBannerViewHolder>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_news_bar_banner
    }


    override fun createViewHolder(itemView: View?, viewType: Int): PostBarBannerViewHolder {
        return PostBarBannerViewHolder(itemView!!)
    }

    override fun onBind(
        holder: PostBarBannerViewHolder?,
        data: SpecialListBean,
        position: Int,
        pageSize: Int
    ) {
        holder!!.bindData(data, position, pageSize)
    }
}


class PostBarBannerViewHolder(itemView: View) : BaseViewHolder<SpecialListBean>(itemView) {
    override fun bindData(data: SpecialListBean, position: Int, pageSize: Int) {
        val binding = DataBindingUtil.bind<ItemNewsBarBannerBinding>(itemView)
        binding?.ivBanner?.load(data.pics)
        binding?.ivBanner?.setOnClickListener {
//              startARouter(ARouterHomePath.SpecialDetailActivity)
            JumpUtils.instans?.jump(8,data.artId)
        }
    }


}