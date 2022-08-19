package com.changanford.common.ui.activity

import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.adapter.FordAlbumDetailsAdapter
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.CategoryOfPhoto
import com.changanford.common.constant.IntentKey
import com.changanford.common.databinding.ActivityFordAlbumDetailsBinding
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar

/**
 *Author lcw
 *Time on 2022/8/19
 *Purpose
 */
@Route(path = ARouterCommonPath.FordAlbumDetailsActivity)
class FordAlbumDetailsActivity : BaseActivity<ActivityFordAlbumDetailsBinding, BaseViewModel>() {

    private val adapter by lazy {
        FordAlbumDetailsAdapter()
    }

    private var position = 0
    private var data: CategoryOfPhoto? = null
    private var selectPic = ""

    override fun initView() {
        position = intent.getIntExtra(IntentKey.FORD_ALBUM_POSITION, 0)
        data = intent.getParcelableExtra(IntentKey.FORD_ALBUM_ITEM)
        binding.run {
            title.toolbar.initTitleBar(
                this@FordAlbumDetailsActivity,
                Builder()
            )
            vpPic.adapter = adapter
            data?.let {
                adapter.setList(it.imgUrls)
                setTitleNum("${position + 1}/${it.imgUrls.size}")
                vpPic.currentItem = position
                selectPic = it.imgUrls[position]
                vpPic.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        selectPic = it.imgUrls[position]
                        setTitleNum("${position + 1}/${it.imgUrls.size}")
                    }
                })
            }
            tvSet.setOnClickListener {
                LiveDataBus.get().with(LiveDataBusKey.FORD_ALBUM_RESULT).postValue(selectPic)
                finish()
            }
        }
    }

    override fun initData() {

    }

    private fun setTitleNum(titleContent: String) {
        binding.run {
            title.toolbar.initTitleBar(
                this@FordAlbumDetailsActivity,
                Builder().apply { title = titleContent })

        }
    }
}