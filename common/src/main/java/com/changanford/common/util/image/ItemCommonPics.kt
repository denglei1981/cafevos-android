package com.changanford.common.util.image

import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.bean.AdBean
import com.changanford.common.databinding.ItemCommonFourPicsBinding
import com.changanford.common.databinding.ItemCommonOnePicsBinding
import com.changanford.common.databinding.ItemCommonPicsBinding
import com.changanford.common.databinding.ItemCommonThreePicsBinding
import com.changanford.common.databinding.ItemCommonTwoPicsBinding
import com.changanford.common.databinding.ItemShopKgOnePicsBinding
import com.changanford.common.databinding.ItemShopKgThreePicsBinding
import com.changanford.common.databinding.ItemShopKgTwoPicsBinding
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.GlideUtils.loadCompress

/**
 * @author: niubobo
 * @date: 2024/2/19
 * @description：
 */
object ItemCommonPics {

    fun setItemCommonPics(binding: ItemCommonPicsBinding, pics: List<String>?) {
        if (pics.isNullOrEmpty()) {
            return
        }
        when (pics.size) {
            1 -> {
                binding.clPics.removeAllViews()
                val fourView = DataBindingUtil.inflate<ItemCommonOnePicsBinding>(
                    LayoutInflater.from(binding.root.context),
                    R.layout.item_common_one_pics,
                    null,
                    false
                ).apply {
                    ivPic.loadCompress(pics[0])
                    ivPic.setCircular(12)
                }
                binding.clPics.addView(fourView.root)
            }

            2 -> {
                binding.clPics.removeAllViews()
                val fourView = DataBindingUtil.inflate<ItemCommonTwoPicsBinding>(
                    LayoutInflater.from(binding.root.context),
                    R.layout.item_common_two_pics,
                    null,
                    false
                ).apply {
                    ivOnePic.loadCompress(pics[0])
                    ivTwoPic.loadCompress(pics[1])
                    ivOnePic.setCircular(12)
                    ivTwoPic.setCircular(12)
                }
                binding.clPics.addView(fourView.root)
            }

            3 -> {
                binding.clPics.removeAllViews()
                val fourView = DataBindingUtil.inflate<ItemCommonThreePicsBinding>(
                    LayoutInflater.from(binding.root.context),
                    R.layout.item_common_three_pics,
                    null,
                    false
                ).apply {
                    ivOnePic.loadCompress(pics[0])
                    ivTwoPic.loadCompress(pics[1])
                    ivThreePic.loadCompress(pics[2])
                    ivOnePic.setCircular(12)
                    ivTwoPic.setCircular(12)
                    ivThreePic.setCircular(12)
                }
                binding.clPics.addView(fourView.root)
            }

            else -> {//大于等于4张
                binding.clPics.removeAllViews()
                val fourView = DataBindingUtil.inflate<ItemCommonFourPicsBinding>(
                    LayoutInflater.from(binding.root.context),
                    R.layout.item_common_four_pics,
                    null,
                    false
                ).apply {
                    ivOnePic.loadCompress(pics[0])
                    ivTwoPic.loadCompress(pics[1])
                    ivThreePic.loadCompress(pics[2])
                    ivFourPic.loadCompress(pics[3])
                    ivOnePic.setCircular(12)
                    ivTwoPic.setCircular(12)
                    ivThreePic.setCircular(12)
                    ivFourPic.setCircular(12)
                }
                binding.clPics.addView(fourView.root)
            }
        }
    }


    fun setItemShopPics(binding: ItemCommonPicsBinding, pics: List<AdBean>?) {
        if (pics.isNullOrEmpty()) {
            return
        }
        when (pics.size) {
            1 -> {
                binding.clPics.removeAllViews()
                val fourView = DataBindingUtil.inflate<ItemShopKgOnePicsBinding>(
                    LayoutInflater.from(binding.root.context),
                    R.layout.item_shop_kg_one_pics,
                    null,
                    false
                ).apply {
                    ivPic.loadCompress(pics[0].getAdImgUrl())
                    ivPic.setCircular(12)
                    tvTitle.text = pics[0].adName
                }
                binding.clPics.addView(fourView.root)
            }

            2 -> {
                binding.clPics.removeAllViews()
                val fourView = DataBindingUtil.inflate<ItemShopKgTwoPicsBinding>(
                    LayoutInflater.from(binding.root.context),
                    R.layout.item_shop_kg_two_pics,
                    null,
                    false
                ).apply {
                    ivOnePic.loadCompress(pics[0].getAdImgUrl())
                    ivTwoPic.loadCompress(pics[1].getAdImgUrl())
                    ivOnePic.setCircular(12)
                    ivTwoPic.setCircular(12)
                    tvTitleOne.text = pics[0].adName
                    tvTitleTwo.text = pics[1].adName
                }
                binding.clPics.addView(fourView.root)
            }

            3 -> {
                binding.clPics.removeAllViews()
                val fourView = DataBindingUtil.inflate<ItemShopKgThreePicsBinding>(
                    LayoutInflater.from(binding.root.context),
                    R.layout.item_shop_kg_three_pics,
                    null,
                    false
                ).apply {
                    ivOnePic.loadCompress(pics[0].getAdImgUrl())
                    ivTwoPic.loadCompress(pics[1].getAdImgUrl())
                    ivThreePic.loadCompress(pics[2].getAdImgUrl())
                    ivOnePic.setCircular(12)
                    ivTwoPic.setCircular(12)
                    ivThreePic.setCircular(12)
                    tvTitleOne.text = pics[0].adName
                    tvTitleTwo.text = pics[1].adName
                    tvTitleThree.text = pics[2].adName
                }
                binding.clPics.addView(fourView.root)
            }

            else -> {//大于等于4张
                binding.clPics.removeAllViews()
                val fourView = DataBindingUtil.inflate<ItemShopKgThreePicsBinding>(
                    LayoutInflater.from(binding.root.context),
                    R.layout.item_shop_kg_three_pics,
                    null,
                    false
                ).apply {
                    ivOnePic.loadCompress(pics[0].getAdImgUrl())
                    ivTwoPic.loadCompress(pics[1].getAdImgUrl())
                    ivThreePic.loadCompress(pics[2].getAdImgUrl())
                    ivOnePic.setCircular(12)
                    ivTwoPic.setCircular(12)
                    ivThreePic.setCircular(12)
                    tvTitleOne.text = pics[0].adName
                    tvTitleTwo.text = pics[1].adName
                    tvTitleThree.text = pics[2].adName
                }
                binding.clPics.addView(fourView.root)
            }
        }
    }
}