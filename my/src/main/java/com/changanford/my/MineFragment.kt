package com.changanford.my

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.basic.BaseFragment
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.utilext.logE
import com.changanford.my.bean.MINE_ITEM_MENU
import com.changanford.my.bean.MINE_ITEM_TITLE
import com.changanford.my.bean.MineMenuMultiEntity
import com.changanford.my.bean.getMenus
import com.changanford.my.databinding.MineFragmentBinding
import com.changanford.my.viewmodel.SignViewModel

class MineFragment : BaseFragment<MineFragmentBinding, SignViewModel>() {

    val menuAdapter: MenuAdapter by lazy {
        MenuAdapter()
    }

    override fun initView() {
        binding.imHeadIcon.setOnClickListener {
            RouterManger.param(RouterManger.KEY_TO_OBJ, "我是传过来的数据")
                .startARouter(ARouterMyPath.SignUI)
        }

        viewModel.userDatabase.getUniUserInfoDao().getUser().observe(this, Observer {
            it?.toString()?.logE()
        })

        var layoutManger: GridLayoutManager = GridLayoutManager(requireContext(), 4)
        binding.rcyMenu.layoutManager = layoutManger
        binding.rcyMenu.adapter = menuAdapter.apply {
            addData(getMenus)
            addData(getMenus)
            addData(getMenus)
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

        }
    }
}