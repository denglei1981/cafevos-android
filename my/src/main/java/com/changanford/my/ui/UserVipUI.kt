package com.changanford.my.ui

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.UserIdCardBeanItem
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.databinding.ItemMedalBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBusKey.MINE_MEMBER_INFO_ID
import com.changanford.common.util.bus.LiveDataBusKey.MINE_MEMBER_INFO_TYPE
import com.changanford.common.util.room.SysUserInfoBean
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiAllVipBinding
import com.changanford.my.viewmodel.SignViewModel
import com.google.gson.Gson
import com.xiaomi.push.it

/**
 *  文件名：UserVipUI
 *  创建者: zcy
 *  创建日期：2021/10/11 9:47
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.UniUserIdcardUI)
class UserVipUI : BaseMineUI<UiAllVipBinding, SignViewModel>() {

    override fun initView() {
        binding.medalToolbar.toolbarTitle.text = "我的会员"

        binding.mineMedal.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.MyVipUI)
        }
    }

    override fun initData() {
        super.initData()
        viewModel.getUserIdCardList {
            it.onSuccess {
                initAdapter(it)
            }
            it.onFailure {
                initAdapter(it)
            }
        }
    }

    private fun initAdapter(datas: ArrayList<UserIdCardBeanItem>?) {
        var sysUserInfoBean: SysUserInfoBean? = UserManger.getSysUserInfo()
        var userInfoBean: UserInfoBean? = null
        sysUserInfoBean?.userJson?.let {
            userInfoBean = Gson().fromJson(it, UserInfoBean::class.java)
        }
        var num: Int = 0
        datas?.let {
            datas.forEach {
                if (it.memberId == userInfoBean?.ext?.memberId) {
                    binding.imMedalWithIcon.load(it.memberIcon, R.mipmap.ic_def_vip)
                    binding.imMedalWithName.text = "当前展示：${it.memberName}"
                }
                if (it.isAuth == "1") {
                    num++
                }
            }
        }
        if (num == 0) {
            binding.imWithVipNum.text = "还未获取到身份"
        } else {
            binding.imWithVipNum.text = "${num}个身份"
        }
        binding.rcyVip.layoutManager = GridLayoutManager(this, 3)
        binding.rcyVip.adapter = object :
            BaseQuickAdapter<UserIdCardBeanItem, BaseDataBindingHolder<ItemMedalBinding>>(R.layout.item_medal) {
            override fun convert(
                holder: BaseDataBindingHolder<ItemMedalBinding>,
                item: UserIdCardBeanItem
            ) {
                holder.dataBinding?.let {
                    it.imMedalIcon.load(item.memberIcon, R.mipmap.ic_medal_ex)
                    it.tvMedalName.text = item.memberName

                    when {
                        item.isAuth == "1" -> {//获得未领取
                            it.btnGetMedal.visibility = View.GONE
                            it.tvMedalDes.visibility = View.VISIBLE
                            it.tvMedalDes.text = item.memberDesc
                        }
                        item.isAuth.isNullOrEmpty() -> {//未获取
                            it.btnGetMedal.visibility = View.VISIBLE
                            it.tvMedalDes.visibility = View.GONE
                            it.btnGetMedal.text = "立即认证"
                        }
                    }
                    it.btnGetMedal.setOnClickListener {
                        RouterManger.param(MINE_MEMBER_INFO_ID, item.memberId)
                            .param(MINE_MEMBER_INFO_TYPE, item.memberKey)
                            .param("title", item.memberName)
                            .startARouter(ARouterMyPath.UniUserAuthUI)
                    }
                }
            }
        }.apply {
            if (datas.isNullOrEmpty()) {
                showEmpty()?.let {
                    setEmptyView(it)
                }
            } else {
                addData(datas)
            }
        }
    }
}