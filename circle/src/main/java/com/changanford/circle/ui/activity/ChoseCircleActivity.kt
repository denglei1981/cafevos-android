package com.changanford.circle.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.ChoseCircleAdapter
import com.changanford.circle.databinding.ChooseCircleBinding
import com.changanford.circle.viewmodel.ChooseCircleViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils

@Route(path = ARouterCirclePath.ChoseCircleActivity)
class ChoseCircleActivity : BaseActivity<ChooseCircleBinding, ChooseCircleViewModel>() {
    val adapter by lazy {
        ChoseCircleAdapter()
    }

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "选择圈子"
        binding.rec.layoutManager = LinearLayoutManager(this)
        binding.rec.adapter = adapter
    }

    override fun initData() {
        binding.title.barImgBack.setOnClickListener {
            finish()
        }
        binding.tvNocy.setOnClickListener {
            intent.putExtra("name", binding.tvNocy.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        adapter.setEmptyView(R.layout.view_empty)
        adapter.setOnItemClickListener { madapter, view, position ->
            if (adapter.getItem(position).itemType == 2) {
                val intent = Intent()
                val bean = adapter.getItem(position)
                intent.putExtra("circleId", bean.circleId)
                intent.putExtra("name", bean.name)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        viewModel.getCreateCircles()
    }

    override fun observe() {
        super.observe()
        viewModel.datas.observe(this, Observer {
            adapter.addData(it)
        })
    }
}