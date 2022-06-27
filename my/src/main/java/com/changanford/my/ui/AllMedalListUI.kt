package com.changanford.my.ui

import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.room.SysUserInfoBean
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.ItemMedalTabBinding
import com.changanford.my.databinding.PopMedalBinding
import com.changanford.my.databinding.UiAllMedalBinding
import com.changanford.my.ui.fragment.MedalFragment
import com.changanford.my.viewmodel.SignViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import razerdp.basepopup.BasePopupWindow
import java.util.*
import kotlin.collections.ArrayList

/**
 *  文件名：AllMedalListUI
 *  创建者: zcy
 *  创建日期：2021/9/14 14:32
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.AllMedalUI)
class AllMedalListUI : BaseMineUI<UiAllMedalBinding, SignViewModel>() {


    private var oldPosition = 0

    private var medalItem: MedalListBeanItem? = null

    private var totalNum: Int = 0

    private var isRefresh: Boolean = false

    var userInfoBean: UserInfoBean? = null
    var medalDatas: ArrayList<MedalListBeanItem> = ArrayList()

    override fun initView() {

        binding.medalToolbar.toolbarTitle.text = "会员勋章"
        binding.medalToolbar.toolbarSave.apply {
            text = "我的勋章"
            textSize = 14f
            setOnClickListener {
                RouterManger.startARouter(ARouterMyPath.MineMedalUI)
            }
        }
        viewModel.medalTotalNum.observe(this, Observer {
            it?.let {
                totalNum = it
                binding.imWithVipNum.MedalNum(it)
            }
        })
        viewModel.allMedal.observe(this, Observer {
            it?.let { l ->
                medalDatas.clear()
                medalDatas.addAll(l)
                l.forEach { item ->
                    if (item.isGet == "0" && null == medalItem) {
                        medalItem = item
                    }
                    if (!TextUtils.isEmpty(item.isShow)&&item.isShow=="1") {
                        nowMedal(item)
                    }
                }
                if (viewModel.titles.size > 0) {
                    val medalItem = MedalListBeanItem(medalTypeName = "全部", medalType = 0)
                    viewModel.titles.add(0, medalItem)
                    viewModel.medalMap[0] = l
                }
                initViewpager()
            }
        })

        binding.mineMedal.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.MineMedalUI)
        }


        viewModel.wearMedal.observe(this, Observer {
            if ("true" == it) {
                showToast("已点亮")
                LiveDataBus.get().with("refreshMedal", String::class.java).postValue("${medalItem?.medalId},")
            } else {
                showToast(it)
            }
        })

        LiveDataBus.get().with("refreshMedalNum", Int::class.java).observe(this, Observer {
            it?.let {
                binding.imWithVipNum.MedalNum(totalNum)
                if (isRefresh) {
                    isRefresh = false
                    viewModel.mineMedal()
                }
            }
        })

        LiveDataBus.get().with("refreshNowMedal", String::class.java).observe(this, Observer {
            viewModel.mineMedal()
        })
    }

    private fun nowMedal(item: MedalListBeanItem) {
        binding.imMedalWithIcon.load(item.medalImage, R.mipmap.icon_mine_all_medal)
        binding.imMedalWithName.text = "当前佩戴：${item.medalName}"
    }

    override fun onPause() {
        super.onPause()
        isRefresh = true
    }

    private fun initViewpager() {
        binding.viewpager.run {
            adapter = object : FragmentStateAdapter(this@AllMedalListUI) {
                override fun getItemCount(): Int {
                    return viewModel.titles.size
                }

                override fun createFragment(position: Int): Fragment {
                    var medalType: Int = viewModel.titles[position].medalType
                    return MedalFragment.newInstance(viewModel.medalMap[medalType], medalType)
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
                itemHelpTabBinding.tvTab.text = viewModel.titles[tabPosition].medalTypeName
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

//        medalItem?.let { item ->
//            PopSuccessMedal().apply {
//                binding.icon.load(item?.medalImage, R.mipmap.ic_medal_ex)
//                binding.medalName.text = item.medalName
//                binding.getTitle1.text = item.remark
//                binding.btnGetTake.visibility = View.VISIBLE
//                binding.btnGetTake.setOnClickListener {
//                    dismiss()
////                    viewModel.wearMedal(item.medalId,item.medalKey)
//                }
//            }.showPopupWindow()
//        }
    }

    override fun initData() {
        super.initData()
        viewModel.mineMedal()
    }

    inner class PopSuccessMedal : BasePopupWindow(this) {
        var binding = PopMedalBinding.inflate(layoutInflater)

        init {
            contentView = binding.root
            popupGravity = Gravity.CENTER
        }

        override fun onViewCreated(contentView: View) {
            super.onViewCreated(contentView)

            binding.close.setOnClickListener { dismiss() }
        }
    }

    private fun AppCompatTextView.MedalNum(num: Int) {
        text = "${num}枚勋章"
    }
}