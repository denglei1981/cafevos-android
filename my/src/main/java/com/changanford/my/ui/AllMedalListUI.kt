package com.changanford.my.ui

import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMedalTabBinding
import com.changanford.my.databinding.UiAllMedalBinding
import com.changanford.my.ui.fragment.MedalFragment
import com.changanford.my.viewmodel.SignViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

/**
 *  文件名：AllMedalListUI
 *  创建者: zcy
 *  创建日期：2021/9/14 14:32
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.AllMedalUI)
class AllMedalListUI : BaseMineUI<UiAllMedalBinding, SignViewModel>() {

    private var medalMap: HashMap<String, ArrayList<MedalListBeanItem>> = HashMap()

    private val titles: ArrayList<String> = ArrayList()

    private var oldPosition = 0

    override fun initView() {


        viewModel.allMedal.observe(this, Observer {
            it?.let { l ->
                l.forEach { item ->
                    var list: ArrayList<MedalListBeanItem>? = medalMap[item.medalTypeName]
                    if (null == list) {
                        list = ArrayList()
                        list.add(item)
                        medalMap[item.medalTypeName] = list
                    } else {
                        list.add(item)
                    }
                }
                medalMap.filterKeys { key ->
                    titles.add(key)
                }
                initViewpager()
            }
        })
    }

    private fun initViewpager() {
        binding.viewpager.run {
            adapter = object : FragmentStateAdapter(this@AllMedalListUI) {
                override fun getItemCount(): Int {
                    return titles.size
                }

                override fun createFragment(position: Int): Fragment {
                    return MedalFragment.newInstance(medalMap[titles[position]])
                }
            }

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val oldTitle =
                        binding.tabLayout.getTabAt(oldPosition)?.view?.findViewById<TextView>(R.id.tv_tab)
                    if (oldTitle != null) {
                        oldTitle.textSize = 14F
                        oldTitle.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.text_161E37
                            )
                        )
                        oldTitle.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    }

                    val title =
                        binding.tabLayout.getTabAt(position)?.view?.findViewById<TextView>(R.id.tv_tab)
                    if (title != null) {
                        title.textSize = 15F
                        title.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.text_161E37
                            )
                        )
                        //加粗
                        title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    }

                    oldPosition = position
                }
            })

            TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, tabPosition ->
                val itemHelpTabBinding = ItemMedalTabBinding.inflate(layoutInflater)
                itemHelpTabBinding.tvTab.text = titles[tabPosition]
                //解决第一次进来item显示不完的bug
                if (tabPosition == 0) {
                    itemHelpTabBinding.tvTab.textSize = 15F
                    itemHelpTabBinding.tvTab.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.text_161E37
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
                            R.color.text_161E37
                        )
                    )
                }
                tab.customView = itemHelpTabBinding.root
            }.attach()
        }
    }

    override fun initData() {
        super.initData()
        lifecycleScope.launch {
            viewModel.mineMedal()
        }
    }
}