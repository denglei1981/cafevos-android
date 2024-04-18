package com.changanford.common.util.image

import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.databinding.ItemCommonFourPicsBinding
import com.changanford.common.databinding.ItemCommonOnePicsBinding
import com.changanford.common.databinding.ItemCommonPicsBinding
import com.changanford.common.databinding.ItemCommonThreePicsBinding
import com.changanford.common.databinding.ItemCommonTwoPicsBinding
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
//                visibleWho(binding, 1)
//                DataBindingUtil.bind<ItemCommonOnePicsBinding>(binding.layoutOne.root)?.apply {
//                    ivPic.loadCompress(pics[0])
//                    ivPic.setCircular(12)
//                }
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
//                visibleWho(binding, 2)
//                DataBindingUtil.bind<ItemCommonTwoPicsBinding>(binding.layoutTwo.root)?.apply {
//                    ivOnePic.loadCompress(pics[0])
//                    ivTwoPic.loadCompress(pics[1])
//                    ivOnePic.setCircular(12)
//                    ivTwoPic.setCircular(12)
//                }
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
//                visibleWho(binding, 3)
//                DataBindingUtil.bind<ItemCommonThreePicsBinding>(binding.layoutThree.root)?.apply {
//                    ivOnePic.loadCompress(pics[0])
//                    ivTwoPic.loadCompress(pics[1])
//                    ivThreePic.loadCompress(pics[2])
//                    ivOnePic.setCircular(12)
//                    ivTwoPic.setCircular(12)
//                    ivThreePic.setCircular(12)
//                }
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
//                visibleWho(binding, 4)
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

    private fun visibleWho(binding: ItemCommonPicsBinding, index: Int) {
//        binding.layoutOne.viewStub?.isVisible = index == 1
//        binding.layoutTwo.viewStub?.isVisible = index == 2
//        binding.layoutThree.viewStub?.isVisible = index == 3
//        binding.layoutFour.viewStub?.isVisible = index == 4
    }
}