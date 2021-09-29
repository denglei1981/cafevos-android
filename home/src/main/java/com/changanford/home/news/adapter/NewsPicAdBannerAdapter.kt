package com.changanford.home.news.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.load
import com.changanford.home.R
import com.changanford.home.databinding.ItemNewsAdBarBannerBinding
import com.changanford.home.databinding.ItemNewsBarBannerBinding
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class NewsPicAdBannerAdapter : BaseBannerAdapter<String, PicAdBarBannerViewHolder>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_news_ad_bar_banner
    }

    override fun createViewHolder(itemView: View?, viewType: Int): PicAdBarBannerViewHolder {
        return PicAdBarBannerViewHolder(itemView!!)
    }

    override fun onBind(
        holder: PicAdBarBannerViewHolder?,
        data: String?,
        position: Int,
        pageSize: Int
    ) {
        holder!!.bindData(data, position, pageSize)
    }
}


class PicAdBarBannerViewHolder(itemView: View) : BaseViewHolder<String>(itemView) {
    override fun bindData(data: String?, position: Int, pageSize: Int) {
        val binding = DataBindingUtil.bind<ItemNewsAdBarBannerBinding>(itemView)
        binding?.ivBanner?.load(data)
        binding?.ivBanner?.setOnClickListener {
//            var pics = arrayListOf<MediaListBean>(MediaListBean().apply {
//                img_url = data
//            }, MediaListBean().apply {
//                img_url = data
//            })
//            var bundle = Bundle()
//            bundle.putSerializable("imgList", pics)
//            bundle.putInt("count", 0)
//            startARouter(ARouterCirclePath.PhotoViewActivity, bundle)
              startARouter(ARouterHomePath.SpecialListActivity)
        }
    }

}