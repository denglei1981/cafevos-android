package com.changanford.common.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.R
import com.changanford.common.basic.BaseActivity
import com.changanford.common.databinding.ActivitySignatureBinding
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.util.ext.setOnFastClickListener
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar
import com.changanford.common.viewmodel.SignatureViewModel
import com.changanford.common.widget.SignatureView

/**
 * @author: niubobo
 * @date: 2024/12/6
 * @description：手写签名
 */
@Route(path = ARouterCommonPath.SignatureActivity)
class SignatureActivity : BaseActivity<ActivitySignatureBinding, SignatureViewModel>() {

    override fun initView() {
        binding.run {
            title.toolbar.initTitleBar(
                this@SignatureActivity,
                Builder().apply { title = "手写签名" })

            signature.setOnSignatureCompleteListener(object :
                SignatureView.OnSignatureCompleteListener {
                override fun onSignatureComplete(isSigned: Boolean) {
                    ivSure.isEnabled = isSigned
                    ivSure.setImageResource(if (isSigned) R.mipmap.ic_qm_sure else R.mipmap.ic_qm_sure_no)
                }

            })
            ivReset.setOnFastClickListener {
                signature.clear()
            }
        }
    }

    override fun initData() {

    }
}