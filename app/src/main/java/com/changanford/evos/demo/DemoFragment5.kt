package com.changanford.evos.demo

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.util.paging.SingleAdapter
import com.changanford.common.utilext.load
import com.changanford.evos.R
import com.changanford.evos.databinding.DemoMyItemBinding
import com.changanford.evos.databinding.FragmentDemo5Binding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DemoFragment1.newInstance] factory method to
 * create an instance of this fragment.
 */
class DemoFragment5 : BaseFragment<FragmentDemo5Binding,EmptyViewModel>() {
    private var adapter = object : SingleAdapter<DemoMyItemBinding, Int>(bind = { binding, position ->
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
    }

    var list = ArrayList<Int>()

    override fun initData() {
        list.add(R.mipmap.demom1)
        list.add(R.mipmap.demom2)
        list.add(R.mipmap.demom3)
        list.add(R.mipmap.demom4)
        adapter.notifyDataSetChanged()
    }

}