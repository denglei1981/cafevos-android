package com.changanford.home.acts.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.changanford.home.R
import com.changanford.home.acts.adapter.HomeActsScreenItemAdapter
import com.changanford.home.acts.adapter.HomeActsTypeItemAdapter
import com.changanford.home.base.BaseAppCompatDialog
import com.changanford.home.callback.ICallback
import com.changanford.home.databinding.DialogHomeActsScreenBinding
import com.changanford.home.search.data.SearchData


class HomeActsScreenDialog(var acts: Context) : BaseAppCompatDialog(acts) {
    lateinit var mDatabind: DialogHomeActsScreenBinding
    lateinit var callback: ICallback

    private val homeActsScreenItemAdapter: HomeActsScreenItemAdapter by lazy {
        HomeActsScreenItemAdapter(
            arrayListOf()
        )
    }

    private  val homeActsTypeItemAdapter: HomeActsTypeItemAdapter by lazy {
        HomeActsTypeItemAdapter(
            arrayListOf()
        )
    }

    constructor(acts: Context, callback: ICallback) : this(acts) {
        this.callback = callback
        mDatabind = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_home_acts_screen,
            null,
            false
        )
        setContentView(mDatabind.root)
        initView()
        initData()
    }

    override fun initAd() {

    }

    fun initView() {

    }
    fun initData() {
        mDatabind.homeRvPublish.layoutManager = GridLayoutManager(acts, 3)
        mDatabind.homeRvPublish.adapter = homeActsScreenItemAdapter.apply {
            addData(SearchData())
            addData(SearchData())
            addData(SearchData())
        }
        mDatabind.homeRvActsType.layoutManager=GridLayoutManager(acts,3)
        mDatabind.homeRvActsType.adapter=homeActsTypeItemAdapter.apply {
            addData(SearchData())
            addData(SearchData())
            addData(SearchData())
            addData(SearchData())
            addData(SearchData())
            addData(SearchData())
        }
    }


}