package com.changanford.my.ui

import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MineUtils
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.toast
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMedalTabBinding
import com.changanford.my.databinding.UiCenterFeedbackBinding
import com.changanford.my.ui.fragment.HelpAndBackFragment
import com.changanford.my.viewmodel.SignViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

@Route(path = ARouterMyPath.MineCenterFeedbackUI)
class MineCenterFeedbackUI : BaseMineUI<UiCenterFeedbackBinding, SignViewModel>() {
    private val titles = arrayListOf("热门问题", "所有问题")
    private var oldPosition = 0
    var mobile: String = ""//节假日热线
    override fun initView() {
//        setLoadSir(binding.root)
        updateMainGio("帮助与反馈页", "帮助与反馈页")
        binding.mineToolbar.toolbarTitle.text = "帮助与反馈"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }
        binding.kefu.setOnClickListener {
            if (mobile.isEmpty()) {
                "获取配置电话适配".toast()
                return@setOnClickListener
            }
            MineUtils.callPhone(this, mobile)
        }
        var hasFeedbacks: Int = 0
        viewModel.userDatabase.getUniUserInfoDao().getUser().observe(this) {
            it?.let {
                var userInfoBean: UserInfoBean =
                    Gson().fromJson(it.userJson, UserInfoBean::class.java)
                hasFeedbacks = userInfoBean?.hasFeedbacks!!
            }
        }
        viewModel.queryCmcStatePhone()
        viewModel.cmcStatePhoneBean.observe(this) {
            mobile = it.LRPhone
        }
        binding.yijian.setOnClickListener {
            JumpUtils.instans?.jump(
                when (hasFeedbacks) {
                    1 -> {
                        42
                    }

                    else -> {
                        11
                    }
                }
            )
        }
//        binding.more.setOnClickListener {
//            JumpUtils.instans?.jump(39)
//        }
        initViewpager()
        showContent()
    }

    private fun initViewpager() {
        binding.viewpager.isUserInputEnabled = false
        binding.viewpager.run {
            adapter = object : FragmentStateAdapter(this@MineCenterFeedbackUI) {
                override fun getItemCount(): Int {
                    return titles.size
                }

                override fun createFragment(position: Int): Fragment {
                    return HelpAndBackFragment.newInstance(position)
                }
            }

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val oldTitle =
                        binding.tabLayout.getTabAt(oldPosition)?.view?.findViewById<TextView>(R.id.tv_tab)
                    val oldIn =
                        binding.tabLayout.getTabAt(oldPosition)?.view?.findViewById<TextView>(R.id.tab_in)
                    if (oldTitle != null) {
                        oldTitle.textSize = 16F
                        oldTitle.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_9916
                            )
                        )
                        oldTitle.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                        oldIn?.isSelected = false
                    }

                    val title =
                        binding.tabLayout.getTabAt(position)?.view?.findViewById<TextView>(R.id.tv_tab)
                    val newIn =
                        binding.tabLayout.getTabAt(position)?.view?.findViewById<TextView>(R.id.tab_in)
                    if (title != null) {
                        title.textSize = 18F
                        title.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.text_01025C
                            )
                        )
                        //加粗
//                        title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        newIn?.isSelected = true
                    }

                    oldPosition = position
                }
            })

            TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, tabPosition ->
                val itemHelpTabBinding = ItemMedalTabBinding.inflate(layoutInflater)
                itemHelpTabBinding.tvTab.text = titles[tabPosition]
                //解决第一次进来item显示不完的bug
                itemHelpTabBinding.tabIn.isSelected = tabPosition == 0
                if (tabPosition == 0) {
                    itemHelpTabBinding.tvTab.textSize = 18F
                    itemHelpTabBinding.tvTab.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.text_01025C
                        )
                    )
                    //加粗
//                    itemHelpTabBinding.tvTab.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                } else {
                    itemHelpTabBinding.tvTab.textSize = 16F
                    itemHelpTabBinding.tvTab.typeface =
                        Typeface.defaultFromStyle(Typeface.NORMAL)
                    itemHelpTabBinding.tvTab.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.color_9916
                        )
                    )
                }
                tab.customView = itemHelpTabBinding.root
            }.attach()
        }
    }

}