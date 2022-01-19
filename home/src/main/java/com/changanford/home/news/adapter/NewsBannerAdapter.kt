package com.changanford.home.news.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.load
import com.changanford.home.R
import com.changanford.home.bean.SpecialListBean
import com.changanford.home.databinding.ItemNewsBarBannerBinding
import com.changanford.home.databinding.ItemNewsPicDetailsBannerBinding
import com.changanford.home.news.data.ImageTexts
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class NewsPicDetailsBannerAdapter : BaseBannerAdapter<ImageTexts?>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_recommend_bar_banner
    }
    override fun bindData(holder: BaseViewHolder<ImageTexts?>?, data: ImageTexts?, position: Int, pageSize: Int) {
        holder?.let {
            DataBindingUtil.bind<ItemNewsPicDetailsBannerBinding>(it.itemView)?.apply {
                data?.apply {
                   ivBanner.load(data.img)
                }
            }
        }
    }
}

//class NewsPicDetailsBannerAdapter : BaseBannerAdapter<ImageTexts, NewsPicDetailsBannerViewHolder>() {
//
//    override fun getLayoutId(viewType: Int): Int {
//        return R.layout.item_news_pic_details_banner
//    }
//
//
//    override fun createViewHolder(itemView: View?, viewType: Int): NewsPicDetailsBannerViewHolder {
//        return NewsPicDetailsBannerViewHolder(itemView!!)
//    }
//
//    override fun onBind(
//        holder: NewsPicDetailsBannerViewHolder?,
//        data: ImageTexts,
//        position: Int,
//        pageSize: Int
//    ) {
//        holder!!.bindData(data, position, pageSize)
//    }
//}


class NewsBannerAdapter : BaseBannerAdapter<SpecialListBean?>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_recommend_bar_banner
    }
    override fun bindData(holder: BaseViewHolder<SpecialListBean?>?, data: SpecialListBean?, position: Int, pageSize: Int) {
        holder?.let {
            DataBindingUtil.bind<ItemNewsBarBannerBinding>(it.itemView)?.apply {
                data?.apply {
                    ivBanner.load(data.pics)
                    ivBanner.setOnClickListener {
                        JumpUtils.instans?.jump(8,data.artId)
                    }
                }
            }
        }
    }
}
//class NewsBannerAdapter : BaseBannerAdapter<SpecialListBean, PostBarBannerViewHolder>() {
//
//    override fun getLayoutId(viewType: Int): Int {
//        return R.layout.item_news_bar_banner
//    }
//
//
//    override fun createViewHolder(itemView: View?, viewType: Int): PostBarBannerViewHolder {
//        return PostBarBannerViewHolder(itemView!!)
//    }
//
//    override fun onBind(
//        holder: PostBarBannerViewHolder?,
//        data: SpecialListBean,
//        position: Int,
//        pageSize: Int
//    ) {
//        holder!!.bindData(data, position, pageSize)
//    }
//}

class NewsPicDetailsBannerViewHolder(itemView: View) : BaseViewHolder<ImageTexts>(itemView) {
    override fun bindData(data: ImageTexts, position: Int, pageSize: Int) {
        val binding = DataBindingUtil.bind<ItemNewsPicDetailsBannerBinding>(itemView)
        binding?.ivBanner?.load(data.img)
//        binding?.ivBanner?.setOnClickListener {
//            JumpUtils.instans?.jump(8,data.artId)
//        }
    }


}



class PostBarBannerViewHolder(itemView: View) : BaseViewHolder<SpecialListBean>(itemView) {
    override fun bindData(data: SpecialListBean, position: Int, pageSize: Int) {
        val binding = DataBindingUtil.bind<ItemNewsBarBannerBinding>(itemView)
        binding?.ivBanner?.load(data.pics)
        binding?.ivBanner?.setOnClickListener {
            JumpUtils.instans?.jump(8,data.artId)
        }
    }


}