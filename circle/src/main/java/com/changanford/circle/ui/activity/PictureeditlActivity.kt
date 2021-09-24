package com.changanford.circle.ui.activity

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.PictureAdapter
import com.changanford.circle.databinding.PicturesEditBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.luck.picture.lib.entity.LocalMedia
import com.yalantis.ucrop.UCrop

/**
 * 图片预览
 */
@Route(path = ARouterCirclePath.PictureeditlActivity)
class PictureeditlActivity : BaseActivity<PicturesEditBinding, EmptyViewModel>() {
    var mediaList: List<LocalMedia>? = null //预览的所有图片集合:
    var selectorPosition = 0
    var showEditType = -1
    override fun initView() {

        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        intent.extras?.getParcelableArrayList<LocalMedia>("picList").also {
            mediaList = it
        }

        selectorPosition = intent.getIntExtra("position", 0)
        showEditType = intent.getIntExtra("showEditType", -1)
        binding.title.barTvOther.text = "下一步"
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.white))
        binding.title.barTvOther.textSize = 12f
        binding.title.barTvOther.background = resources.getDrawable(R.drawable.post_btn_bg)

    }

    override fun initData() {
        binding.title.barImgBack.setOnClickListener { finish() }

        binding.title.barTvOther.setOnClickListener {
            LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).postValue(mediaList)
            finish()
        }


        binding.picturePager.setAutoPlay(false)
            .setScrollDuration(1800)
            .setCanLoop(false)
            .setIndicatorVisibility(View.GONE)
            .setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
            .setInterval(3500)
            .setAdapter(PictureAdapter(this, showEditType))
            .registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.title.barTvTitle.text = "${position + 1}/${mediaList?.size}"
                    selectorPosition = position
                }
            })
            .create()

        binding.picturePager.refreshData(mediaList)
        binding.picturePager.setCurrentItem(selectorPosition, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP -> {
                val resultUri = UCrop.getOutput(data!!)
                mediaList!![selectorPosition].isCut = true
                mediaList!![selectorPosition].cutPath = resultUri!!.path
                binding.picturePager.refreshData(mediaList)
            }
            resultCode == UCrop.RESULT_ERROR -> {
                val cropError = UCrop.getError(data!!)
            }
        }
    }
}