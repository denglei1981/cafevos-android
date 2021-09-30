package com.changanford.evos.demo

import android.graphics.Rect
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.util.paging.SingleAdapter
import com.changanford.common.utilext.load
import com.changanford.evos.R
import com.changanford.evos.databinding.DemoShopItemBinding
import com.changanford.evos.databinding.FragmentDemo4Binding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DemoFragment1.newInstance] factory method to
 * create an instance of this fragment.
 */
class DemoFragment4 : BaseFragment<FragmentDemo4Binding,EmptyViewModel>() {

    private var adapter = object : SingleAdapter<DemoShopItemBinding, Int>(bind = { binding, position ->
        binding.img.load(list[position])
        //...
    }, edgeEffect = {
        binding.recycle.edgeEffectFactory = it.holder.OverScrollEffect()
        binding.recycle.addOnScrollListener(it.holder.OnScrollListener())
    }){
        override fun getItemCount(): Int {
            return list.size
        }
    }
    override fun initView() {
        binding.recycle.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.recycle.adapter = adapter
        binding.recycle.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = 20
            }
        })
    }

    var list = ArrayList<Int>()

    override fun initData() {
        list.add(R.mipmap.demos1)
        list.add(R.mipmap.demos2)
        list.add(R.mipmap.demos3)
        list.add(R.mipmap.demos1)
        list.add(R.mipmap.demos2)
        list.add(R.mipmap.demos3)
        adapter.notifyDataSetChanged()
    }

}