package com.changanford.my

import androidx.lifecycle.MutableLiveData
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.MenuBeanItem
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.setDrawableLeft
import com.changanford.my.adapter.MedalAdapter
import com.changanford.my.adapter.MenuAdapter
import com.changanford.my.databinding.FragmentMyBinding
import com.changanford.my.viewmodel.SignViewModel

class MyFragment : BaseFragment<FragmentMyBinding, SignViewModel>() {
    lateinit var menuBean: ArrayList<MenuBeanItem>
    private var menuAdapter = MenuAdapter()
    private var medalAdapter = MedalAdapter()
    private var loginState: MutableLiveData<Boolean> = MutableLiveData()
    override fun initView() {
        binding.daySign.setDrawableLeft(R.mipmap.my_daysign, R.dimen.dp_20)
        binding.memberEnter.setDrawableLeft(R.mipmap.my_member, R.dimen.dp_20)
        binding.medalRec.isSaveEnabled = false
        binding.menuRec.isSaveEnabled = false
        binding.menuRec.adapter = menuAdapter
        binding.medalRec.adapter = medalAdapter
        loginState.observe(this, {
            when (it) {
                false -> {//未登录

                }
                true -> {//已登录

                }
            }
        })

        binding.daySign.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.MineAddressListUI)
        }

    }

    override fun initData() {
        viewModel.getMenuList()
        viewModel.menuBean.observe(this, {
            menuBean = it
            menuAdapter.data = menuBean
            medalAdapter.data = menuBean
            menuAdapter.notifyDataSetChanged()
            medalAdapter.notifyDataSetChanged()
        })
    }

    fun initClick() {
        binding.daySign.setOnClickListener { JumpUtils.instans?.jump(37) }
        binding.mySet.setOnClickListener { JumpUtils.instans?.jump(21) }
        binding.myMsg.setOnClickListener { JumpUtils.instans?.jump(24) }
        binding.memberEnter.setOnClickListener { JumpUtils.instans?.jump(29) }
        binding.myHead.setOnClickListener { JumpUtils.instans?.jump(34) }
        binding.myStateLayout.apply {
            myStateFabu.setOnClickListener { JumpUtils.instans?.jump(23) }
            myStateFensi.setOnClickListener { JumpUtils.instans?.jump(40) }
            myStateGuanzhu.setOnClickListener { JumpUtils.instans?.jump(25) }
            myStateShoucang.setOnClickListener { JumpUtils.instans?.jump(27) }
        }
    }

    fun setData() {
        binding.myName.text = "我的名字是。"
        binding.myContent.text = "这里是说明"
        binding.myScore.text = "33333"
        binding.myScoreAcc.text = "1.7倍加速"
        binding.myStateLayout.apply {
            myStateFabu.text = "----"
            myStateFensi.text = "----"
            myStateGuanzhu.text = "----"
            myStateShoucang.text = "----"
        }
        binding.myScoreLayout
    }

}

