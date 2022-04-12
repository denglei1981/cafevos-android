package com.changanford.shop.ui.shoppingcart.request

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.shop.databinding.ActivityMultiplePackageBinding
import com.changanford.shop.databinding.ActivityNoLogisticsInfoBinding
import com.changanford.shop.view.TopBar

@Route(path = ARouterShopPath.NoLogisticsInfoActivity)
class NoLogisticsInfoActivity:
    BaseActivity<ActivityNoLogisticsInfoBinding, MultiplePackageViewModel>()  {
    companion object{
       fun start(activity: Activity){
           JumpUtils.instans?.jump(128)
        }
    }
    override fun initView() {
        binding.topbar.setOnBackClickListener(object :TopBar.OnBackClickListener{
            override fun onBackClick() {
                finish()
            }
        })
    }
    override fun initData() {

    }
}