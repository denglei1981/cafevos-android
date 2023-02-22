package com.changanford.my

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.updateMainGio
import com.changanford.home.data.PublishData
import com.changanford.my.databinding.ItemMedalTabBinding
import com.changanford.my.databinding.UiMyActBinding
import com.changanford.my.viewmodel.MyActUiViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.lang.Exception

/**
 *  文件名：MyActUI
 *  创建者: zcy
 *  创建日期：2021/9/29 16:45
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineJoinAcUI)
class MyActUI : BaseMineUI<UiMyActBinding, MyActUiViewModel>() {

    private val titles = arrayListOf("我参与的","我发布的")
    private var oldPosition = 0
    val acts: String = "activity_add_wonderful"
    var selectPosition = "0"
    override fun initView() {
        updateMainGio("我的活动页", "我的活动页")
        binding.collectToolbar.toolbarTitle.text = "我的活动"
        binding.collectToolbar.toolbarSave.text = "草稿箱"
        binding.collectToolbar.toolbarSave.setTextColor(Color.parseColor("#1B3B89"))
        binding.collectToolbar.toolbarSave.visibility = View.VISIBLE
        binding.collectToolbar.toolbarSave.setOnClickListener {
            RouterManger.param("act",true).startARouter(ARouterMyPath.MyPostDraftUI)
//            JumpUtils.instans?.jump(13)
//            finish()
        }
        selectPosition = intent.getStringExtra("value")?:"0"
        initViewpager()
        if (selectPosition == "1"){
            binding.viewpager.currentItem = 1
        }
        setLoadSir(binding.root)
    }

    override fun observe() {
        super.observe()
        viewModel.myActPublishState.observe(this, Observer {
           changPerm(it)
        })
        LiveDataBus.get().with(LiveDataBusKey.BUS_SHOW_LOAD_CONTENT).observe(this){
            showContent()
        }

    }

    fun changPerm(data: List<String>) {
        try {
            if(data.isNotEmpty()){
                if(data.contains(acts)){
                    binding.collectToolbar.toolbarSave.text = "发布"
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun initViewpager() {
        binding.viewpager.isUserInputEnabled = false
        binding.viewpager.run {
            adapter = object : FragmentStateAdapter(this@MyActUI) {
                override fun getItemCount(): Int {
                    return titles.size
                }

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> {
                            ActFragment.newInstance("actMyJoin")
                        }
                        1 -> {
                            ActFragment.newInstance("actMyCreate")
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