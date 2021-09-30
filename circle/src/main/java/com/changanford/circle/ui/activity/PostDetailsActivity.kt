package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityPostDetailsBinding
import com.changanford.circle.ui.fragment.PostImageDetailsFragment
import com.changanford.circle.ui.fragment.PostVideoDetailsFragment
import com.changanford.circle.viewmodel.PostGraphicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.gyf.immersionbar.ImmersionBar

/**
 * 帖子详情
 */
@Route(path = ARouterCirclePath.PostDetailsActivity)
class PostDetailsActivity : BaseActivity<ActivityPostDetailsBinding, PostGraphicViewModel>() {


    private var postsId = ""

    override fun initView() {
        postsId = intent.getStringExtra("postsId").toString()
    }

    override fun initData() {
        viewModel.getData(postsId)
    }

    override fun observe() {
        super.observe()
        viewModel.postDetailsBean.observe(this, {
            val trans = supportFragmentManager.beginTransaction()
            when (it.type) {
                3 -> {//视频
                    ImmersionBar.with(this).statusBarDarkFont(false).init()
                    trans.replace(
                        R.id.frame_layout,
                        PostVideoDetailsFragment(it)
                    )
                }
                else -> {//图文 图片
                    trans.replace(
                        R.id.frame_layout,
                        PostImageDetailsFragment(it)
                    )
                }
            }
            trans.commitAllowingStateLoss()
        })
    }

}