package com.changanford.common.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.R
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.MediaListBean
import com.changanford.common.databinding.ActivityDocumentBinding
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.ext.setOnFastClickListener
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar

/**
 * @author: niubobo
 * @date: 2024/7/17
 * @description：证件信息
 */
@Route(path = ARouterCommonPath.DocumentActivity)
class DocumentActivity : BaseActivity<ActivityDocumentBinding, BaseViewModel>() {
    override fun initView() {
        binding.run {
            title.toolbar.initTitleBar(
                this@DocumentActivity,
                Builder().apply { title = "证照信息" })
        }
        binding.ivDocument.setOnFastClickListener {
            val pics = arrayListOf<MediaListBean>()
            pics.add(MediaListBean(R.mipmap.ic_document))
            val bundle = Bundle()
            bundle.putSerializable("imgList", pics)
            bundle.putInt("count", 1)
            bundle.putInt("canSave", 1)
            startARouter(ARouterCirclePath.PhotoViewActivity, bundle)
        }
    }

    override fun initData() {

    }
}