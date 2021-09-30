package com.changanford.car

import androidx.core.view.ViewCompat
import com.changanford.car.databinding.ActivityDetailBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.utilext.load

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.evos.car.DetailActivity
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/8/5 17:07
 * @Description: 　
 * *********************************************************************************
 */
class DetailActivity : BaseActivity<ActivityDetailBinding, CarViewModel>() {
    override fun initView() {
        val img = intent.getStringExtra("img")
        val txt = intent.getStringExtra("txt")
        ViewCompat.setTransitionName(binding.detailImg, "i")
        ViewCompat.setTransitionName(binding.detailTxt, "c")
        binding.detailImg.load(img)
        binding.detailTxt.text = txt
        binding.title.barTvTitle.text = "详情"
    }

    override fun initData() {
    }
}