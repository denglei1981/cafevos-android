package com.changanford.common.util.image

import androidx.core.view.isVisible
import com.changanford.common.databinding.ItemCommonPicsBinding
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
                visibleWho(binding, 1)
                binding.layoutOne.ivPic.loadCompress(pics[0])
                binding.layoutOne.ivPic.setCircular(12)
            }

            2 -> {
                visibleWho(binding, 2)
                binding.layoutTwo.ivOnePic.loadCompress(pics[0])
                binding.layoutTwo.ivTwoPic.loadCompress(pics[1])
                binding.layoutTwo.ivOnePic.setCircular(12)
                binding.layoutTwo.ivTwoPic.setCircular(12)
            }

            3 -> {
                visibleWho(binding, 3)
                binding.layoutThree.ivOnePic.loadCompress(pics[0])
                binding.layoutThree.ivTwoPic.loadCompress(pics[1])
                binding.layoutThree.ivThreePic.loadCompress(pics[2])
                binding.layoutThree.ivOnePic.setCircular(12)
                binding.layoutThree.ivTwoPic.setCircular(12)
                binding.layoutThree.ivThreePic.setCircular(12)
            }

            else -> {//大于等于4张
                visibleWho(binding, 4)
                binding.layoutFour.ivOnePic.loadCompress(pics[0])
                binding.layoutFour.ivTwoPic.loadCompress(pics[1])
                binding.layoutFour.ivThreePic.loadCompress(pics[2])
                binding.layoutFour.ivFourPic.loadCompress(pics[3])
                binding.layoutFour.ivOnePic.setCircular(12)
                binding.layoutFour.ivTwoPic.setCircular(12)
                binding.layoutFour.ivThreePic.setCircular(12)
                binding.layoutFour.ivFourPic.setCircular(12)
            }
        }
    }

    private fun visibleWho(binding: ItemCommonPicsBinding, index: Int) {
        binding.layoutOne.root.isVisible = index == 1
        binding.layoutTwo.root.isVisible = index == 2
        binding.layoutThree.root.isVisible = index == 3
        binding.layoutFour.root.isVisible = index == 4
    }
}