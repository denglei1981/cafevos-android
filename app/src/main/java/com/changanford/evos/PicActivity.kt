package com.changanford.evos

import android.content.Intent
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.util.PictureUtil
import com.changanford.common.utilext.logD
import com.changanford.evos.databinding.PictestBinding
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.yalantis.ucrop.UCrop

class PicActivity :BaseActivity<PictestBinding,EmptyViewModel>() {

    private var selectList = ArrayList<LocalMedia>()
    override fun initView() {
        binding.test.setOnClickListener {
            PictureUtil.openGallery(this,selectList,object :OnResultCallbackListener<LocalMedia>{
                override fun onResult(result: MutableList<LocalMedia>?) {
                    if (result != null) {
                        selectList.clear()
                        selectList.addAll(result)
                    }
                   result?.forEach {
                       it.realPath.logD()
                   }
                    result?.get(0)?.let { it ->
                        PictureUtil.startUCrop(this@PicActivity,
                            PictureUtil.getFinallyPath(it),UCrop.REQUEST_CROP,16f,9f)
                    }
                }

                override fun onCancel() {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    override fun initData() {
        ImmersionBar.with(this).init()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            selectList[0].isCut=true
            selectList[0].cutPath = resultUri?.path
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
        }
    }
}