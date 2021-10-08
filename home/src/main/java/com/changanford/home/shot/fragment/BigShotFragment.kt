package com.changanford.home.shot.fragment
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.home.databinding.FragmentBigShotBinding
import com.changanford.home.news.data.SpecialData
import com.changanford.home.shot.adapter.BigShotPostListAdapter
import com.changanford.home.shot.adapter.BigShotUserListAdapter
import com.changanford.home.shot.request.BigShotListViewModel
/**
 *  大咖
 * */
class BigShotFragment : BaseLoadSirFragment<FragmentBigShotBinding, BigShotListViewModel>() {


    val bigShotUserListAdapter: BigShotUserListAdapter by lazy {
        BigShotUserListAdapter()
    }


    var bigShotPostListAdapter: BigShotPostListAdapter? = null


    companion object {
        fun newInstance(): BigShotFragment {
            val fg = BigShotFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {

        setLoadSir(binding.refreshLayout)
        binding.recyclerViewH.adapter = bigShotUserListAdapter
        binding.recyclerViewH.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        bigShotPostListAdapter = BigShotPostListAdapter().apply {
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
        }
        binding.recyclerViewV.adapter = bigShotPostListAdapter
        binding.recyclerViewV.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        viewModel.getRecommendList()
    }

    override fun observe() {
        super.observe()
        viewModel.bigShotsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                bigShotUserListAdapter.addData(it.data)


            } else {
                binding.refreshLayout.finishRefresh()
                showFailure(it.message)
            }
        })

    }

    override fun initData() {


    }

    override fun onRetryBtnClick() {

    }
}