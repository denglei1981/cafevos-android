package com.changanford.my

import android.graphics.Typeface
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.databinding.ItemFootTabBinding
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.setOnFastClickListener
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.toIntPx
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
    private var nowPosition = 0
    private val manageType = MutableLiveData<Int>()

    override fun onResume() {
        super.onResume()
        updateMainGio("我的足迹页", "我的足迹页")
    }

    override fun initView() {
        title = "我的足迹页"
        GioPageConstant.infoEntrance = "我的足迹-资讯"
        GioPageConstant.postEntrance = "我的足迹-帖子"
        binding.collectToolbar.toolbarTitle.text = "我的足迹"
        binding.layoutSearch.root.visibility = View.GONE
        setLoadSir(binding.root)
        LiveDataBus.get().with(LiveDataBusKey.BUS_SHOW_LOAD_CONTENT).observe(this) {
            showContent()
        }
        initViewpager()
        manageType.observe(this) {
            when (it) {
                0 -> {
                    binding.viewpager.setPadding(0, 0, 0, 0)
                    binding.rlDelete.isVisible = false
                    binding.collectToolbar.toolbarSave.text = "管理"
                }

                else -> {
                    binding.viewpager.setPadding(0, 0, 0, 74.toIntPx())
                    binding.rlDelete.isVisible = true
                    binding.collectToolbar.toolbarSave.text = "完成"
                }
            }
            refreshBottomFragment(nowPosition)
        }
        binding.collectToolbar.toolbarSave.setOnClickListener {
            if (manageType.value == 0) {
                manageType.value = 1
            } else {
                manageType.value = 0
            }
        }
//        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
//            deleteCanCheck(isChecked)
//        }
        binding.checkbox.setOnClickListener {
            refreshBottomDataFragment(nowPosition, binding.checkbox.isChecked)
            deleteCanCheck(binding.checkbox.isChecked)
        }
        //底部是否全选
        LiveDataBus.get().withs<Boolean>(LiveDataBusKey.REFRESH_FOOT_CHECK).observe(this) {
            binding.checkbox.isChecked = it
        }
        LiveDataBus.get().withs<Boolean>(LiveDataBusKey.FOOT_UI_CAN_DELETE).observe(this) {
            deleteCanCheck(it)
        }
        binding.tvDelete.setOnFastClickListener {
            deleteSelectFragmentData()
        }
    }

    private fun deleteCanCheck(canCheck: Boolean) {
        if (canCheck) {
            binding.tvDelete.isEnabled = true
            binding.tvDelete.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvDelete.background =
                ContextCompat.getDrawable(this, R.drawable.bg_shape_1700f4_23)
        } else {
            binding.tvDelete.isEnabled = false
            binding.tvDelete.setTextColor(ContextCompat.getColor(this, R.color.color_4d16))
            binding.tvDelete.background =
                ContextCompat.getDrawable(this, R.drawable.bg_shape_80a6_23)
        }
    }

    private fun deleteSelectFragmentData() {
        when (nowPosition) {
            0 -> {
                LiveDataBus.get().with(LiveDataBusKey.DELETE_INFORMATION_DATA).postValue("")
            }

            1 -> {
                LiveDataBus.get().with(LiveDataBusKey.DELETE_POST_DATA).postValue("")
            }

            2 -> {
                LiveDataBus.get().with(LiveDataBusKey.DELETE_ACT_DATA).postValue("")
            }

            3 -> {
                LiveDataBus.get().with(LiveDataBusKey.DELETE_SHOP_DATA).postValue("")
            }
        }
    }

    private fun refreshBottomDataFragment(position: Int, isAll: Boolean) {
        when (position) {
            0 -> {//资讯是否全部选中
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_INFORMATION_DATA).postValue(isAll)
            }

            1 -> {//帖子
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_POST_DATA).postValue(isAll)
            }

            2 -> {//活动
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_ACTS_DATA).postValue(isAll)
            }

            3 -> {//商品
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_SHOP_DATA).postValue(isAll)
            }
        }
    }

    private fun refreshBottomFragment(position: Int) {
        when (position) {
            0 -> {//资讯是否显示删除
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_INFORMATION_FRAGMENT)
                    .postValue(manageType.value == 1)
            }

            1 -> {//帖子
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_POST_FRAGMENT)
                    .postValue(manageType.value == 1)
            }

            2 -> {//活动
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_ACTS_FRAGMENT)
                    .postValue(manageType.value == 1)
            }

            3 -> {//商品
                LiveDataBus.get().with(LiveDataBusKey.REFRESH_SHOP_FRAGMENT)
                    .postValue(manageType.value == 1)
            }
        }
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
                    nowPosition = position
                    manageType.value = 0
                    refreshBottomFragment(oldPosition)
                    refreshBottomDataFragment(oldPosition, false)
                    binding.checkbox.isChecked = false
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
                val itemHelpTabBinding = ItemFootTabBinding.inflate(layoutInflater)
                itemHelpTabBinding.tvTab.text = titles[tabPosition]
                //解决第一次进来item显示不完的bug
                itemHelpTabBinding.tabIn.isSelected = tabPosition == 0
                //tabLayout左右对齐
                when (tabPosition) {
                    0 -> {
                        val params =
                            itemHelpTabBinding.tvTab.layoutParams as RelativeLayout.LayoutParams
                        params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
                        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                        itemHelpTabBinding.tvTab.setLayoutParams(params)
                    }

                    1 -> {
                        val params =
                            itemHelpTabBinding.tvTab.layoutParams as RelativeLayout.LayoutParams
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                        itemHelpTabBinding.tvTab.setLayoutParams(params)
                        itemHelpTabBinding.tvTab.setPadding(0, 0, 15.toIntPx(), 0)
                    }

                    2 -> {
                        val params =
                            itemHelpTabBinding.tvTab.layoutParams as RelativeLayout.LayoutParams
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                        itemHelpTabBinding.tvTab.setLayoutParams(params)
                        itemHelpTabBinding.tvTab.setPadding(15.toIntPx(), 0, 0, 0)

                        val tabInLayoutParam =
                            itemHelpTabBinding.tabIn.layoutParams as RelativeLayout.LayoutParams
                        tabInLayoutParam.leftMargin = 18.toIntPx()
                        itemHelpTabBinding.tabIn.layoutParams = tabInLayoutParam

                    }

                    3 -> {
                        val params =
                            itemHelpTabBinding.tvTab.layoutParams as RelativeLayout.LayoutParams
                        params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                        itemHelpTabBinding.tvTab.setLayoutParams(params)
                    }
                }
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
            offscreenPageLimit = 4
        }
    }
}