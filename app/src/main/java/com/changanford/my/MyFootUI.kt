package com.changanford.my

import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.my.databinding.ItemMedalTabBinding
import com.changanford.my.databinding.UiCollectBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
 *  文件名：MyCollectUI
 *  创建者: zcy
 *  创建日期：2021/9/26 16:54
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineFootprintUI)
class MyFootUI : BaseMineUI<UiCollectBinding, EmptyViewModel>() {

    private val titles = arrayListOf("资讯", "帖子", "活动", "商品")
    private var oldPosition = 0

    override fun initView() {
        GioPageConstant.infoEntrance = "我的足迹-资讯"
        GioPageConstant.postEntrance = "我的足迹-帖子"
        binding.collectToolbar.toolbarTitle.text = "我的足迹"
        setLoadSir(binding.root)
        LiveDataBus.get().with(LiveDataBusKey.BUS_SHOW_LOAD_CONTENT).observe(this){
            showContent()
        }
        initViewpager()
    }

    private fun initViewpager() {
        binding.viewpager.isUserInputEnabled = false
        binding.viewpager.offscreenPageLimit = 4
        binding.viewpager.run {
            adapter = object : FragmentStateAdapter(this@MyFootUI) {
                override fun getItemCount(): Int {
                    return titles.size
                }

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> {
                            InformationFragment.newInstance("footInformation")
                        }
                        1 -> {
                            PostFragment.newInstance("footPost")
                        }
                        2 -> {
                            ActFragment.newInstance("footAct")
                        }
                        3 -> {
                            MyShopFragment.newInstance("footShop")
                        }
                        else -> {
                            PostFragment.newInstance("$position")
                        }
                    }
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
                        oldTitle.textSize = 14F
                        oldTitle.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_0817
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
                        title.textSize = 15F
                        title.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.text_01025C
                            )
                        )
                        //加粗
                        title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
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
                    itemHelpTabBinding.tvTab.textSize = 15F
                    itemHelpTabBinding.tvTab.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.text_01025C
                        )
                    )
                    //加粗
                    itemHelpTabBinding.tvTab.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                } else {
                    itemHelpTabBinding.tvTab.textSize = 14F
                    itemHelpTabBinding.tvTab.typeface =
                        Typeface.defaultFromStyle(Typeface.NORMAL)
                    itemHelpTabBinding.tvTab.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.color_0817
                        )
                    )
                }
                tab.customView = itemHelpTabBinding.root
            }.attach()
        }
    }
}