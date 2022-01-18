package com.changanford.circle.ui.activity

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.poi.*
import com.changanford.circle.R
import com.changanford.circle.adapter.LocaAdapter
import com.changanford.circle.databinding.LayoutSearchLocationFooterBinding
import com.changanford.circle.databinding.SearchlocBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toast


// 搜索位置信息。
@Route(path = ARouterCirclePath.SearchLocActivity)
class SearchLocActivity : BaseActivity<SearchlocBinding, EmptyViewModel>(),
    OnGetPoiSearchResultListener {

    lateinit var locaAdapter: LocaAdapter
    var lat = 0.0
    var lon = 0.0
    var city = ""
    lateinit var mPoiSearch: PoiSearch
    var ml: ArrayList<PoiInfo> = ArrayList()
    lateinit var poiInfo: PoiInfo


    override fun initData() {
        binding.etsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    binding.ivClose.visibility = View.VISIBLE
                    shearch(s.toString())
                } else {
                    binding.ivClose.visibility = View.GONE
                    ml.clear()
                    locaAdapter.setList(ml)
                    locaAdapter.notifyDataSetChanged()
                }
            }

        })

        binding.tvConsle.setOnClickListener {
            finish()
        }

        binding.ivClose.setOnClickListener {
            binding.etsearch.setText("")
            ml.clear()
            locaAdapter.setList(ml)
            locaAdapter.notifyDataSetChanged()
        }
        binding.tvCommit.setOnClickListener {
            if (locaAdapter?.id == -1) {
                "请选择地址".toast()
            } else {
                LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION).postValue(poiInfo)
                LiveDataBus.get().with(LiveDataBusKey.ColseCHOOSELOCATION, Boolean::class.java)
                    .postValue(true)
                finish()


            }
        }
        LiveDataBus.get().with(LiveDataBusKey.CREATE_COLSE_LOCATION, Boolean::class.java)
            .observe(this,
                {
                    if (it) {
                        finish()
                    }
                })
    }

    var footerBinding : LayoutSearchLocationFooterBinding?=null
    private fun addFooter() {
        if (footerBinding == null) {
            footerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.layout_search_location_footer,
                binding.locrec,
                false
            )
            footerBinding?.let {
                locaAdapter.addFooterView(it.root, 0)
                it.tvCreateLocation.setOnClickListener {
                    val intent = Intent()
                    intent.setClass(this,CreateLocationActivity::class.java)
                    startActivity(intent)
                }
            }
//            val recommendBannerAdapter = RecommendBannerAdapter()
//            headNewBinding?.let {
//                recommendAdapter.addHeaderView(it.root, 0)
//                it.bViewpager.setAdapter(recommendBannerAdapter)
//                it.bViewpager.setCanLoop(true)
//                it.bViewpager.setIndicatorView(it.drIndicator)
//                it.bViewpager.setAutoPlay(true)
//                it.bViewpager.setScrollDuration(500)
//                it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE_SCALE)
//                it.bViewpager.create()
//            }
//            setIndicator()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0);
    }

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.commTitleBar, this)
        mPoiSearch = PoiSearch.newInstance()
        mPoiSearch.setOnGetPoiSearchResultListener(this)
        locaAdapter = LocaAdapter()
        binding.locrec.layoutManager = LinearLayoutManager(this)
        binding.locrec.adapter = locaAdapter
        city = intent?.extras?.getString("city").toString()
        lat = intent.extras?.getDouble("Lat") ?: 0.0
        lon = intent.extras?.getDouble("Lon") ?: 0.0
        locaAdapter.setOnItemClickListener { adapter, view, position ->
//            LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION).postValue(ml[position])
            locaAdapter.setSelectID(position)
            locaAdapter.notifyDataSetChanged()
            poiInfo = ml[position]
        }
        showSoftInputFromWindow(binding.etsearch)
    }

    fun shearch(shearch: String) {
        val poiCitySearchOption = PoiCitySearchOption()
        poiCitySearchOption.mIsCityLimit = false
        poiCitySearchOption.city(city)
        poiCitySearchOption.keyword(shearch)
        poiCitySearchOption.pageNum(0)
        poiCitySearchOption.pageCapacity(99)
        mPoiSearch.searchInCity(poiCitySearchOption)
        var strlist = shearch.toCharArray()
        var list = ArrayList<String>()
        for (e in strlist) {
            list.add(e.toString())
        }
        list.add(shearch)
        locaAdapter.setTagName(list)
        addFooter()
    }

    override fun onGetPoiResult(poiResult: PoiResult?) {

        if (poiResult?.error == SearchResult.ERRORNO.NO_ERROR) {
            ml.clear()
            ml.addAll(poiResult?.allPoi)
            locaAdapter.setList(ml)
            locaAdapter.setSelectID(-1)
            locaAdapter.notifyDataSetChanged()
        }
    }

    override fun onGetPoiDetailResult(p0: PoiDetailResult?) {
    }

    override fun onGetPoiDetailResult(p0: PoiDetailSearchResult?) {
    }

    override fun onGetPoiIndoorResult(p0: PoiIndoorResult?) {
    }


    fun showSoftInputFromWindow(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}