package com.changanford.my.ui

import android.graphics.Typeface
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.HideKeyboardUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.updateMainGio
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMedalTabBinding
import com.changanford.my.databinding.UiCollectBinding
import com.changanford.my.ui.fragment.CircleFragment
import com.changanford.my.viewmodel.CircleViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 *  文件名：MyCollectUI
 *  创建者: zcy
 *  创建日期：2021/9/26 16:54
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineCircleUI)
class MyCircleUI : BaseMineUI<UiCollectBinding, CircleViewModel>() {

    private val titles = arrayListOf("我参与的", "我管理的")
    private var oldPosition = 0
    private val fragments = arrayListOf<CircleFragment>()
    override fun initView() {
        setLoadSir(binding.root)
        updateMainGio("我的圈子页", "我的圈子页")
        binding.collectToolbar.toolbarTitle.text = "我的圈子"
        binding.collectToolbar.toolbarSave.setOnClickListener {
            RouterManger.startARouter(ARouterCirclePath.CreateCircleActivity)
        }
        binding.layoutSearch.apply {
            searchContent.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)
                    val content = v.text.toString()
                    val currentItem = binding.viewpager.currentItem
                    if (currentItem < fragments.size) fragments[currentItem].startSearch(content)
                }
                false
            }
            cancel.visibility = View.GONE
        }
        initViewpager()
    }

    override fun initData() {
        super.initData()
        LiveDataBus.get().with(LiveDataBusKey.BUS_SHOW_LOAD_CONTENT).observe(this) {
            showContent()
            intent.getStringExtra("value")?.let {
                if (it == "1") {
                    binding.viewpager.currentItem = 1
                }
            }
        }
        viewModel.createCircle {
            it.onSuccess {
                binding.collectToolbar.toolbarSave.text = "创建圈子"
                binding.collectToolbar.toolbarSave.visibility = View.VISIBLE
            }
        }
    }

    private fun initViewpager() {
        fragments.clear()
        for (position in 0 until titles.size) {
            fragments.add(CircleFragment.newInstance(position))
        }
        binding.viewpager.run {
            adapter = object : FragmentStateAdapter(this@MyCircleUI) {
                override fun getItemCount(): Int {
                    return titles.size
                }

                override fun createFragment(position: Int): Fragment {
                    return fragments[position]
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