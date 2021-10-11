package com.changanford.my

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.databinding.ItemMedalBinding
import com.changanford.common.databinding.ItemPersonMedalBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.utilext.load
import com.changanford.my.databinding.ItemMedalTabBinding
import com.changanford.my.databinding.UiPersonCenterBinding
import com.changanford.my.viewmodel.SignViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 *  文件名：PresonCenterUI
 *  创建者: zcy
 *  创建日期：2021/9/29 17:26
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.TaCentreInfoUI)
class PersonCenterUI : BaseMineUI<UiPersonCenterBinding, SignViewModel>() {
    private val titles = arrayListOf("帖子", "活动", "资讯")
    private var oldPosition = 0
    var userId: String = "0"

    override fun initView() {
        intent.extras?.let { bundle ->
            bundle.getString("value")?.let {
                userId = it
            }
        }
        binding.centerToolbar.toolbarTitle.text = "个人主页"
        binding.centerToolbar.toolbar.setNavigationOnClickListener { back() }
        binding.personRcy.visibility = View.GONE
        initViewpager()
    }

    override fun initData() {
        viewModel.queryOtherInfo(userId) {
            it.onSuccess { user ->
                user?.let {
                    if (user.userId == userId) {
                        binding.btnFollow.visibility = View.GONE
                    }
                    binding.headIcon.load(user.avatar, R.mipmap.my_headdefault)
                    binding.nickName.text = user.nickname
                    binding.userGrade.text = user.ext?.growSeriesName
                    binding.followLayout.apply {
                        followNum.text = "${MineUtils.num(user.count?.follows.toLong())}"
                        fansNum.text = "${MineUtils.num(user.count?.fans.toLong())}"
                        goodNum.text = "${MineUtils.num(user.count?.likeds.toLong())}"
                    }
                    binding.userDesc.text = if (user.brief.isNullOrEmpty()) "这个人很赖~" else user.brief
                    binding.btnFollow.text = if (user.isFollow == 0) "关注" else "已关注"

                    binding.userVip.visibility =
                        if (user.ext?.medalName.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                    binding.userVip.text = user.ext?.memberName
                }
            }
        }

        viewModel.queryOtherUserMedal(userId) {
            it.onSuccess { medals ->
                binding.personRcy.layoutManager = LinearLayoutManager(this).apply {
                    orientation = RecyclerView.HORIZONTAL
                }
                binding.personRcy.adapter =
                    object :
                        BaseQuickAdapter<MedalListBeanItem, BaseDataBindingHolder<ItemPersonMedalBinding>>(
                            R.layout.item_person_medal
                        ) {
                        override fun convert(
                            holder: BaseDataBindingHolder<ItemPersonMedalBinding>,
                            item: MedalListBeanItem
                        ) {
                            holder.dataBinding?.let {
                                it.imMedalIcon.load(item.medalImage, R.mipmap.ic_medal_ex)
                                it.tvMedalName.text = item.medalName
                            }
                        }
                    }.apply {
                        medals?.apply {
                            if (size > 0) {
                                binding.personRcy.visibility = View.VISIBLE
                                addData(this)
                            }
                        }
                    }
            }
        }
    }

    private fun initViewpager() {
        binding.viewpager.run {
            adapter = object : FragmentStateAdapter(this@PersonCenterUI) {
                override fun getItemCount(): Int {
                    return titles.size
                }

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> {
                            PostFragment.newInstance("centerPost", userId)
                        }
                        1 -> {
                            ActFragment.newInstance("actMyCreate", userId)
                        }
                        2 -> {
                            InformationFragment.newInstance("centerInformation", userId)
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