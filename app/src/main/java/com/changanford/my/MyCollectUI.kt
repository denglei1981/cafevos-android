package com.changanford.my

import android.graphics.Typeface
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.databinding.ItemMedalTabBinding
import com.changanford.my.databinding.UiCollectBinding
import com.google.android.material.tabs.TabLayoutMediator
import android.view.inputmethod.EditorInfo
import com.changanford.common.constant.JumpConstant
import com.changanford.common.util.HideKeyboardUtil
import com.changanford.common.util.gio.GioPageConstant


/**
 *  文件名：MyCollectUI
 *  创建者: zcy
 *  创建日期：2021/9/26 16:54
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineCollectUI)
class MyCollectUI : BaseMineUI<UiCollectBinding, EmptyViewModel>() {

    private val titles = arrayListOf("资讯", "帖子", "活动", "商品")
    private var oldPosition = 0

    val informationFragment: InformationFragment by lazy {
        InformationFragment.newInstance("collectInformation")
    }

    val postFragment: PostFragment by lazy {
        PostFragment.newInstance("collectPost")
    }

    val actFragment: ActFragment by lazy {
        ActFragment.newInstance("collectAct")
    }

    val myShopFragment: MyShopFragment by lazy {
        MyShopFragment.newInstance("collectShop")
    }

    var index = 0
    override fun initView() {
        GioPageConstant.infoEntrance = "我的收藏-资讯"
        GioPageConstant.postEntrance = "我的收藏-帖子"
        binding.collectToolbar.toolbarTitle.text = "我的收藏"
        val currentItem = intent.getStringExtra("value")

        if (!TextUtils.isEmpty(currentItem)) {
            try {
                if (currentItem != null) {
                    index = currentItem.toInt()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        initViewpager()
        binding.layoutSearch.cancel.visibility = View.GONE
        binding.layoutSearch.searchContent.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.keyCode == KeyEvent.ACTION_UP)) {
                search()
//                    hideKeyboard(binding.editSearch.windowToken)
            }
            false
        }
        binding.layoutSearch.cancel.setOnClickListener {
            binding.layoutSearch.searchContent.setText("")
            informationFragment.searchKeys = ""
            postFragment.searchKeys = ""
            myShopFragment.searchKeys = ""
            actFragment.searchKeys = ""
            search()

        }
        binding.viewpager.currentItem = index
    }

    // 搜搜索
    private fun search() {


        informationFragment.searchKeys =
            binding.layoutSearch.searchContent.text.toString().trim()
        informationFragment.myCollectInfo(1)
        HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)


        postFragment.searchKeys = binding.layoutSearch.searchContent.text.toString().trim()
        postFragment.mySerachInfo()
        HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)


        actFragment.searchKeys = binding.layoutSearch.searchContent.text.toString().trim()
        actFragment.mySerachInfo()
        HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)

        myShopFragment.searchKeys = binding.layoutSearch.searchContent.text.toString().trim()
        myShopFragment.mySerachInfo()
        HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)


//        when (binding.viewpager.currentItem) {
//            0 -> {
//                informationFragment.searchKeys =
//                    binding.layoutSearch.searchContent.text.toString().trim()
//                informationFragment.myCollectInfo(1)
//                HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)
//            }
//            1 -> {
//                postFragment.searchKeys = binding.layoutSearch.searchContent.text.toString().trim()
//                postFragment.mySerachInfo()
//                HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)
//            }
//            2 -> {
//                actFragment.searchKeys = binding.layoutSearch.searchContent.text.toString().trim()
//                actFragment.mySerachInfo()
//                HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)
//            }
//            3 -> {
//                myShopFragment.searchKeys = binding.layoutSearch.searchContent.text.toString().trim()
//                myShopFragment.mySerachInfo()
//                HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)
//            }
//        }
    }

    private fun initViewpager() {
        binding.viewpager.isUserInputEnabled = false
        binding.viewpager.offscreenPageLimit = 4
        binding.viewpager.run {
            adapter = object : FragmentStateAdapter(this@MyCollectUI) {
                override fun getItemCount(): Int {
                    return titles.size
                }

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> {
                            informationFragment
                        }
                        1 -> {
                            postFragment
                        }
                        2 -> {
                            actFragment
                        }
                        3 -> {
                            myShopFragment
                        }
                        else -> {
                            ActFragment.newInstance("$position")
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