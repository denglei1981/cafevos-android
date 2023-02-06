package com.changanford.circle.ui.activity

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AbsListView
import com.changanford.circle.adapter.CityListAdapter
import com.changanford.circle.adapter.CitySearchListAdapter
import com.changanford.circle.bean.CityEntity
import com.changanford.circle.databinding.ActivityChoiceAllCityBinding
import com.changanford.circle.widget.LetterListView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.util.AppUtils
import com.changanford.common.util.JsonReadUtil
import com.changanford.common.utilext.logE
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class ChoiceAllCityActivity :BaseActivity<ActivityChoiceAllCityBinding,EmptyViewModel>() ,
    AbsListView.OnScrollListener{

    //文件名称
    private val CityFileName = "allcity.json"
    protected var totalCityList: ArrayList<CityEntity> = arrayListOf()
    private var  cityListAdapter:CityListAdapter?=null

    private var  isScroll = false
    override fun initView() {

    }

    override fun initData() {

        AppUtils.setStatusBarPaddingTop(binding.llSearch, this)
        initTotalCityList()
        cityListAdapter = CityListAdapter(this, totalCityList)
        binding.totalCityLv.adapter = cityListAdapter
        cityListAdapter?.setClickItem{ postion,cityEntity->
            val intent = Intent()
            intent.putExtra("city",cityEntity)
            setResult(111,intent )
            this.finish()

        }
        binding.totalCityLv.setOnScrollListener(this)
        binding.lettersLv.setOnTouchingLetterChangedListener(object :LetterListView.OnTouchingLetterChangedListener{
            override fun onTouchingLetterChanged(s: String) {
                if(cityListAdapter!=null){
                    if (cityListAdapter!!.alphaIndexer[s] != null) {
                        isScroll=false
                        val position: Int = cityListAdapter!!.alphaIndexer[s]!!
                        binding.totalCityLv.setSelection(position)
//                    letterScorll.LetterScrolls(position, false)
//                totalCityLv.setSelection(position);
//                overlay.setText(s);
//                overlay.setVisibility(View.VISIBLE);
//                handler.removeCallbacks(overlayThread);
//                // 延迟让overlay为不可见
//                handler.postDelayed(overlayThread, 700);
                    }
                }
            }
        })
        binding.etsearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val content: String = binding.etsearch.text.toString().trim()
                setSearchCityList(content)
            }

        })
        binding.rvSearch.adapter=citySearchListAdapter
        binding.tvCancel.setOnClickListener {
            onBackPressed()
        }
    }
//    protected var searchCityList: ArrayList<CityEntity> = ArrayList()

    private  val citySearchListAdapter: CitySearchListAdapter by lazy{
        CitySearchListAdapter()
    }
    /**
     * 设置搜索数据展示
     */
    private fun setSearchCityList(content: String) {
        val searchCityList : ArrayList<CityEntity> = ArrayList()
        if (TextUtils.isEmpty(content)) {
            binding.rvSearch.visibility=View.GONE
        } else {
            binding.rvSearch.visibility=View.VISIBLE

            for (i in totalCityList.indices) {
                val cityEntity: CityEntity = totalCityList[i]
                if (cityEntity.name.contains(content) || cityEntity.pinyin.contains(content)
                    || cityEntity.first.contains(content)
                ) {
                    cityEntity.name.logE()
                    searchCityList.add(cityEntity)
                }
            }
            if (searchCityList.size != 0) {
                binding.rvSearch.visibility=View.VISIBLE
            } else {
                binding.rvSearch.visibility=View.GONE
            }

            citySearchListAdapter.setNewInstance(searchCityList)

        }
    }

    /**
     * 初始化全部城市列表
     */
    fun initTotalCityList() {
//        hotCityList.clear()
        totalCityList.clear()
//        curCityList.clear()
        val cityListJson: String = JsonReadUtil.getJsonStr(this,CityFileName)
        val jsonObject: JSONObject
        try {
            jsonObject = JSONObject(cityListJson)
            val array = jsonObject.getJSONArray("City")
            for (i in 0 until array.length()) {
                val `object` = array.getJSONObject(i)
                val name = `object`.getString("name")
                val key = `object`.getString("key")
                val pinyin = `object`.getString("full")
                val first = `object`.getString("first")
                val cityCode = `object`.getString("code")
                val cityEntity = CityEntity()
                cityEntity.name = name
                cityEntity.key = key
                cityEntity.pinyin = pinyin
                cityEntity.first = first
                cityEntity.cityCode = cityCode
                if (key == "热门") {
//                    hotCityList.add(cityEntity)
                } else {
                    if (!cityEntity.key.equals("0") && !cityEntity.key.equals("1")) {
//                        curCityList.add(cityEntity)
                    }
                    totalCityList.add(cityEntity)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
        isScroll = (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
                || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING)
    }

    override fun onScroll(
        view: AbsListView?,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {

    }
}