package com.changanford.circle.adapter

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.bean.ImageList
import com.changanford.circle.databinding.ItemPostBarBannerBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.common.bean.MediaListBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class PostBarBannerAdapter(val isGood: Int ) : BaseBannerAdapter<ImageList?>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_post_bar_banner
    }
    override fun bindData(holder: BaseViewHolder<ImageList?>?, data: ImageList?, position: Int, pageSize: Int) {
        holder!!.bindData(data, position, pageSize)
        val binding = DataBindingUtil.bind<ItemPostBarBannerBinding>(holder.itemView)

        binding?.ivBanner?.load(GlideUtils.defaultHandleImageUrl(data?.imgUrl))
        if(isGood==1){
            binding?.ivVeryPost?.visibility=View.VISIBLE
        }else{
            binding?.ivVeryPost?.visibility=View.GONE
        }

        binding?.ivBanner?.setOnClickListener {
            val pics = arrayListOf<MediaListBean>()
            mList.forEach {
                pics.add(MediaListBean("${it?.imgUrl}"))
            }
            val bundle = Bundle()
            bundle.putSerializable("imgList", pics)
            bundle.putInt("count", position)
            startARouter(ARouterCirclePath.PhotoViewActivity, bundle)
        }
    }
}

class PostBarBannerViewHolder(itemView: View) : BaseViewHolder<ImageList>(itemView) {
    override fun bindData(data: ImageList?, position: Int, pageSize: Int) {
    }

}