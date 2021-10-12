package com.changanford.home.acts.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.changanford.home.R
import com.changanford.home.acts.adapter.HomeActsScreenItemAdapter
import com.changanford.home.acts.adapter.HomeActsTypeItemAdapter
import com.changanford.home.base.BaseAppCompatDialog
import com.changanford.home.callback.ICallback
import com.changanford.home.data.EnumBean
import com.changanford.home.databinding.DialogHomeActsScreenBinding


class HomeActsScreenDialog(var acts: Context,private val lifecycleOwner: LifecycleOwner) : BaseAppCompatDialog(acts) {
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

    constructor(acts: Context,lifecycleOwner: LifecycleOwner, callback: ICallback) : this(acts,lifecycleOwner) {
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

    fun  setOfficalData(officalList:List<EnumBean>){
        homeActsScreenItemAdapter.setNewInstance(officalList as? MutableList<EnumBean>)

    }
    fun setActsTypeDatta(actsList:List<EnumBean>){
        homeActsTypeItemAdapter.setNewInstance(actsList as? MutableList<EnumBean>)
    }

    override fun initAd() {

    }

    fun initView() {

    }
    fun initData() {
        mDatabind.homeRvPublish.layoutManager = GridLayoutManager(acts, 3)
        mDatabind.homeRvPublish.adapter = homeActsScreenItemAdapter
        mDatabind.homeRvActsType.layoutManager=GridLayoutManager(acts,3)
        mDatabind.homeRvActsType.adapter=homeActsTypeItemAdapter
    }


    fun getData(){

    }


}