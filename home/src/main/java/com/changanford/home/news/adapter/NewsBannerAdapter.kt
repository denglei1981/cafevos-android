package com.changanford.home.news.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.changanford.common.bean.SpecialListBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.ext.loadImage85
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.databinding.ItemNewsBarBannerBinding
import com.changanford.home.databinding.ItemNewsDetailAdvsBinding
import com.changanford.home.databinding.ItemNewsPicDetailsBannerBinding
import com.changanford.home.databinding.ItemRecommendBarBannerBinding
import com.changanford.home.news.data.ImageTexts
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class NewsPicDetailsBannerAdapter : BaseBannerAdapter<ImageTexts?>() {

    override fun getLayoutId(viewType: Int): Int {
        return if (viewType == 0) R.layout.item_news_pic_details_banner
        else R.layout.item_news_detail_advs
    }

    fun getMList(): MutableList<ImageTexts?>? {
        return mList
    }

    override fun bindData(
        holder: BaseViewHolder<ImageTexts?>?,
        data: ImageTexts?,
        position: Int,
        pageSize: Int
    ) {
        if (getViewType(position) == 0) {
            holder?.let {
                DataBindingUtil.bind<ItemNewsPicDetailsBannerBinding>(it.itemView)?.apply {
                    data?.apply {
                        ivBanner.loadImage85(data.img)
                    }
                }
            }
        } else {
            holder?.let {
                //热门推荐adapter
                val newsRecommendListAdapter: NewsRecommendListAdapter by lazy {
                    NewsRecommendListAdapter()
                }
                val newsAdsListAdapter: NewsAdsListAdapter by lazy {
                    NewsAdsListAdapter()
                }
                newsRecommendListAdapter.setOnItemClickListener { adapter, view, position ->
                    val item = newsRecommendListAdapter.getItem(position)
                    if (item.authors != null) {
                        JumpUtils.instans?.jump(2, item.artId)
                    } else {
                        toastShow("没有作者")
                    }
                }
                DataBindingUtil.bind<ItemNewsDetailAdvsBinding>(it.itemView)?.apply {
                    data?.apply {
                        rvRelate.adapter = newsRecommendListAdapter
                        rvAds.adapter = newsAdsListAdapter
                        if (data.infoData?.recommendArticles != null && data.infoData.recommendArticles?.size!! > 0) {
                            flRecommend.visibility = View.VISIBLE
                            newsRecommendListAdapter.setNewInstance(data.infoData.recommendArticles)
                        } else {
                            flRecommend.visibility = View.GONE
                            homeGrayLine.isVisible = false
                        }
                        if (data.infoData?.ads != null && data.infoData.ads?.size!! > 0) {
                            rvAds.visibility = View.VISIBLE
                            newsAdsListAdapter.setNewInstance(data.infoData.ads)
                        } else {
                            rvAds.visibility = View.GONE
                        }

                    }
                }
            }
        }

    }

    override fun getViewType(position: Int): Int {
        return if (mList[position]?.infoData == null) {
            0
        } else {
            1
        }
//        return super.getViewType(position)
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

    override fun bindData(
        holder: BaseViewHolder<SpecialListBean?>?,
        data: SpecialListBean?,
        position: Int,
        pageSize: Int
    ) {
        holder?.let {
            DataBindingUtil.bind<ItemRecommendBarBannerBinding>(it.itemView)?.apply {
                data?.apply {
                    flBg.isVisible = true
                    tvSubTitle.text = if (data.title.isNullOrEmpty()) "长安福特专题" else data.title
                    ivBanner.load(data.pics)
                    ivBanner.setOnClickListener {
                        JumpUtils.instans?.jump(8, data.artId)
                        GIOUtils.homePageClick("专题区", (position + 1).toString(), data.title)
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
            JumpUtils.instans?.jump(8, data.artId)
        }
    }


}