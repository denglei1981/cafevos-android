package com.changanford.my.ui

import android.graphics.Typeface
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
import com.changanford.common.utilext.logE
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

    private var medalMap: MutableMap<Int, ArrayList<MedalListBeanItem>> = TreeMap { o1, o2 ->
        o1.compareTo(o2)
    }

    private val titles: ArrayList<MedalListBeanItem> = ArrayList()

    private var oldPosition = 0

    private var medalItem: MedalListBeanItem? = null

    var num: Int = 0

    override fun initView() {
        var sysUserInfoBean: SysUserInfoBean? = UserManger.getSysUserInfo()
        var userInfoBean: UserInfoBean? = null
        sysUserInfoBean?.userJson?.let {
            userInfoBean = Gson().fromJson(it, UserInfoBean::class.java)
        }
        binding.medalToolbar.toolbarTitle.text = "会员勋章"
        binding.medalToolbar.toolbarSave.apply {
            text = "我的勋章"
            textSize = 14f
            setOnClickListener {
                RouterManger.startARouter(ARouterMyPath.MineMedalUI)
            }
        }
        viewModel.allMedal.observe(this, Observer {
            it?.let { l ->
                l.forEach { item ->
                    var list: ArrayList<MedalListBeanItem>? = medalMap[item.medalType]
                    if (null == list) {
                        list = ArrayList()
                        list.add(item)
                        medalMap[item.medalType] = list
                        titles.add(item)
                    } else {
                        list.add(item)
                    }
                    if (item.isGet == "1") {//已领取
                        num++
                    }
                    if (item.isGet == "0" && null == medalItem) {
                        medalItem = item
                    }
                    userInfoBean?.ext?.medalId?.logE()
                    if (item.medalId.equals("${userInfoBean?.ext?.medalId}")) {
                        binding.imMedalWithIcon.load(item.medalImage, R.mipmap.ic_medal_ex)
                        binding.imMedalWithName.text = "当前佩戴：${item.medalName}"
                    }
                }
                binding.imWithVipNum.MedalNum(num)
                if (titles.size > 0) {
                    var medalItem = MedalListBeanItem(medalTypeName = "全部", medalType = 0)
                    titles.add(0, medalItem)
                    medalMap[0] = l
                }
                initViewpager()
            }
        })

        binding.mineMedal.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.MineMedalUI)
        }

        //弹框领取勋章
        viewModel.wearMedal.observe(this, Observer {
            if ("true" == it) {
                showToast("已点亮")
                LiveDataBus.get().with("refreshMedal", String::class.java)
                    .postValue("${medalItem?.medalId},")
            } else {
                showToast(it)
            }
        })

        LiveDataBus.get().with("refreshMedalNum", Int::class.java).observe(this, Observer {
            it?.let {
                binding.imWithVipNum.MedalNum(num + it)
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
                    return MedalFragment.newInstance(medalMap[titles[position].medalType])
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
                itemHelpTabBinding.tvTab.text = titles[tabPosition].medalTypeName
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

        medalItem?.let { item ->
            PopSuccessMedal().apply {
                binding.icon.load(item?.medalImage, R.mipmap.ic_medal_ex)
                binding.medalName.text = item?.medalName
                binding.getTitle1.text = item?.fillCondition
                binding.btnGetTake.visibility = View.VISIBLE
                binding.btnGetTake.setOnClickListener {
                    dismiss()
                    viewModel.wearMedal(item.medalId, "2")
                }
            }.showPopupWindow()
        }
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
        text = if (num == 0) "未获取勋章" else "${num}枚勋章"
    }
}