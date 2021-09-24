package com.changanford.my

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.*
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.utilext.logE
import com.changanford.my.databinding.MineFragmentBinding
import com.changanford.my.viewmodel.SignViewModel
import com.google.gson.Gson

class MineFragment : BaseFragment<MineFragmentBinding, SignViewModel>() {

    val menuAdapter: MenuAdapter by lazy {
        MenuAdapter()
    }

    override fun initView() {
        binding.imHeadIcon.setOnClickListener {
            RouterManger.param(RouterManger.KEY_TO_OBJ, "我是传过来的数据")
                .startARouter(ARouterMyPath.SignUI)
        }
        binding.imMineSetting.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.MineSettingUI)
        }

        viewModel.userDatabase.getUniUserInfoDao().getUser().observe(this, Observer {
            it?.toString()?.logE()
            if (null == it || it.userJson.isNullOrEmpty()) {
                login(null)
            } else {
                var userInfoBean: UserInfoBean =
                    Gson().fromJson(it.userJson, UserInfoBean::class.java)
                login(userInfoBean)
            }
        })

        var layoutManger: GridLayoutManager = GridLayoutManager(requireContext(), 4)
        binding.rcyMenu.layoutManager = layoutManger
        binding.rcyMenu.adapter = menuAdapter.apply {
            addData(getMenus)
            setGridSpanSizeLookup { _, _, position ->
                data[position].spanSize
            }
        }
    }

    override fun initData() {
        lifecycleScope.launchWhenStarted {

        }
    }

    private fun login(loginBean: UserInfoBean?) {
        if (null == loginBean) {//无数据
            binding.tvName.text = "未登录"
        } else {
            //登录，有数据
            loginBean.let {
                binding.tvName.text = it.nickname
            }
        }
    }

    inner class MenuAdapter :
        BaseMultiItemQuickAdapter<MineMenuMultiEntity, BaseViewHolder>() {

        init {
            addItemType(MINE_ITEM_TITLE, R.layout.item_title)
            addItemType(MINE_ITEM_MENU, R.layout.item_menu)
        }

        override fun convert(
            holder: BaseViewHolder,
            item: MineMenuMultiEntity
        ) {
            when (getItemViewType(holder.layoutPosition)) {
                MINE_ITEM_TITLE -> {

                }
                MINE_ITEM_MENU -> {
                    holder.setText(R.id.tv_menu_text, item.menuName)
                    holder.itemView.setOnClickListener {
                        if (item.menuName == "粉丝" || item.menuName == "关注") {

                            RouterManger.param(
                                RouterManger.KEY_TO_ID, when (item.menuName) {
                                    "粉丝" -> {
                                        1
                                    }
                                    "关注" -> {
                                        2
                                    }
                                    else -> {
                                        1
                                    }
                                }
                            ).startARouter(item.routeUrl)
                        } else if (item.routeUrl.isNotEmpty()) {
                            RouterManger.startARouter(item.routeUrl)
                        }
                    }
                }
            }
        }
    }
}