package com.changanford.common.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.R
import com.changanford.common.adapter.FordAlbumAdapter
import com.changanford.common.basic.BaseActivity
import com.changanford.common.databinding.ActivityFordAlbumBinding
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar
import com.changanford.common.viewmodel.FordAlbumViewModel

/**
 *Author lcw
 *Time on 2022/8/19
 *Purpose 福域相册
 */
@Route(path = ARouterCommonPath.FordAlbumActivity)
class FordAlbumActivity : BaseActivity<ActivityFordAlbumBinding, FordAlbumViewModel>() {

    private val albumAdapter by lazy {
        FordAlbumAdapter()
    }

    override fun initView() {
        binding.run {
            title.toolbar.initTitleBar(
                this@FordAlbumActivity,
                Builder().apply { title = "福域相册" })

            ryPic.adapter = albumAdapter
        }

        LiveDataBus.get().withs<String>(LiveDataBusKey.FORD_ALBUM_RESULT).observe(this) {
            finish()
        }
    }

    override fun initData() {
        viewModel.getFordPhotos()
    }

    override fun observe() {
        super.observe()
        viewModel.photosBean.observe(this) {
            it?.let {
                albumAdapter.setList(it.categoryOfPhotos)
            }
        }
    }
}