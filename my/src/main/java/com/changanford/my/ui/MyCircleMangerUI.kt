package com.changanford.my.ui

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.CircleUserBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.bean.MangerCircleCheck
import com.changanford.my.databinding.ItemMedalTabBinding
import com.changanford.my.databinding.UiCollectBinding
import com.changanford.my.ui.fragment.AllMangerCircleFragment
import com.changanford.my.viewmodel.CircleViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 *  文件名：MyCollectUI
 *  创建者: zcy
 *  创建日期：2021/9/26 16:54
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.CircleMemberUI)
class MyCircleMangerUI : BaseMineUI<UiCollectBinding, CircleViewModel>() {

    private var titles = arrayListOf("全部", "待审核")
    private var oldPosition = 0

    var circleId: String = ""
    var title: String = "我的圈子"

    var isCheck: Boolean = true //点击管理按钮
    var position: Int = 0 // 全部

    override fun initView() {
        binding.tabLayout.visibility = View.GONE
        intent.extras?.getString(RouterManger.KEY_TO_ITEM)?.let {
            title = it
        }
        intent?.extras?.getString(RouterManger.KEY_TO_ID)?.let {
            circleId = it
        }
        binding.collectToolbar.toolbarTitle.text = title
        binding.collectToolbar.toolbarSave.text = "管理"
        binding.collectToolbar.toolbar.setNavigationOnClickListener { back() }
        binding.collectToolbar.toolbarSave.setOnClickListener {
            click()
        }
        LiveDataBus.get()
            .with(LiveDataBusKey.MINE_REFRESH_CIRCLE_STATUS_NO_PEOPLE, Boolean::class.java)
            .observe(this) {
                binding.collectToolbar.toolbarSave.isVisible = !it
            }
        viewModel.circleNum.observe(this, Observer {
            initViewpager(it)
        })

        LiveDataBus.get()
            .with(LiveDataBusKey.MINE_REFRESH_CIRCLE_STATUS, Boolean::class.java)
            .observe(this, Observer {
                if (it) {
                    //重置
                    isCheck = false
                    click()
                }
            })
    }

    override fun initData() {
        viewModel.queryCircleCount(circleId)
    }

    private fun initViewpager(circle: CircleUserBean) {
        circle?.let {
            titles.clear()
            titles = arrayListOf("全部(${it.userCount})", "待审核(${it.userApplyCount})")
        }

        binding.viewpager.isUserInputEnabled = false
        binding.viewpager.run {
            adapter = object : FragmentStateAdapter(this@MyCircleMangerUI) {
                override fun getItemCount(): Int {
                    return titles.size
                }

                override fun createFragment(position: Int): Fragment {
                    return AllMangerCircleFragment.newInstance(1, circleId)
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

                    //重置
                    isCheck = false
                    this@MyCircleMangerUI.position = position
                    click()
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

    /**
     * 管理
     */
    private fun click() {
        if (isCheck) { //管理
            isCheck = false
            binding.collectToolbar.toolbarSave.text = "取消"
        } else {//取消
            isCheck = true
            binding.collectToolbar.toolbarSave.text = "管理"
        }
        LiveDataBus.get()
            .with(LiveDataBusKey.MINE_DELETE_CIRCLE_USER, MangerCircleCheck::class.java)
            .postValue(MangerCircleCheck(1, !isCheck))
    }
}