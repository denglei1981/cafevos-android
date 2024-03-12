package com.changanford.home.search.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.adapter.HomeSearchAcAdapter
import com.changanford.common.adapter.PolySearchTopicAdapter
import com.changanford.common.basic.BaseActivity
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.constant.JumpConstant.SEARCH_CONTENT
import com.changanford.common.constant.JumpConstant.SEARCH_TYPE
import com.changanford.common.constant.SearchTypeConstant.SEARCH_ASK
import com.changanford.common.constant.SearchTypeConstant.SEARCH_POST
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.HideKeyboardUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.util.room.SearchRecordDatabase
import com.changanford.common.util.room.SearchRecordEntity
import com.changanford.common.utilext.toastShow
import com.changanford.common.wutil.FlowLayoutManager
import com.changanford.home.R
import com.changanford.home.databinding.ActivityPolySearchBinding
import com.changanford.home.search.adapter.SearchHistoryAdapter
import com.changanford.home.search.adapter.SearchHotAdapter
import com.changanford.home.search.request.PolySearchViewModel
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections


/**
 *  聚合搜索页面
 *
 *  author:ny
 *  * */
@Route(path = ARouterHomePath.PolySearchActivity)
class PolySearchActivity : BaseActivity<ActivityPolySearchBinding, PolySearchViewModel>() {

    private val searchHotAdapter: SearchHotAdapter by lazy {
        SearchHotAdapter()
    }
    private val historyAdapter by lazy {
        SearchHistoryAdapter()
    }
    private val topicAdapter by lazy {
        PolySearchTopicAdapter()
    }
    private val mViewList = ArrayList<View>()

    //搜索列表
    private val sAdapter by lazy {
        HomeSearchAcAdapter()
    }

    var searchType = -1

    var mDatas: MutableList<SearchRecordEntity>? = null

    override fun onResume() {
        super.onResume()
        updateMainGio("搜索页", "搜索页")
        binding.layoutSearch.searchContent.setText("")
        HideKeyboardUtil.showSoftInput(binding.layoutSearch.searchContent)
    }

    override fun initView() {
        title = "搜索页"
        val searchTypStr = intent.getStringExtra(SEARCH_TYPE)
        searchType = if (!TextUtils.isEmpty(searchTypStr)) {
            searchTypStr?.toIntOrNull()!!
        } else {
            -1
        }
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.white)

        val flowLayoutManager = FlowLayoutManager(this, 2) {

        }
        val flowLayoutManager1 = FlowLayoutManager(this, true)
        val flowLayoutManager0 = FlowLayoutManager(this, 2) {
            binding.ivExpand.visibility = if (it > 2) View.VISIBLE else View.GONE
        }
        historyAdapter.isExpand.observe(this) {
            binding.recyclerViewHistory.layoutManager =
                if (it) flowLayoutManager1 else flowLayoutManager0
        }
        binding.ivExpand.setOnClickListener {
            historyAdapter.isExpand.value = historyAdapter.isExpand.value == false
            if (historyAdapter.isExpand.value == true) {
                binding.ivExpand.setImageResource(R.mipmap.ic_his_up_end)
            } else {
                binding.ivExpand.setImageResource(R.mipmap.ic_his_down_end)
            }
        }
        binding.recyclerViewHistory.layoutManager = flowLayoutManager0

        binding.recyclerViewHistory.adapter = historyAdapter


        binding.recyclerViewFind.layoutManager = flowLayoutManager
        binding.ryTopic.adapter = topicAdapter

        binding.smartLayout.setEnableLoadMore(false)
        binding.smartLayout.setEnableOverScrollDrag(true)
        binding.rvAuto.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvAuto.adapter = sAdapter

        binding.recyclerViewFind.adapter = searchHotAdapter


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
//        val divider: Int = 30.toIntPx()
//        val gridItemDecoration = object : RecyclerView.ItemDecoration() {
//            override fun getItemOffsets(
//                outRect: Rect,
//                view: View,
//                parent: RecyclerView,
//                state: RecyclerView.State
//            ) {
//                val layoutManager = parent.layoutManager as GridLayoutManager?
//                val lp = view.layoutParams as GridLayoutManager.LayoutParams
//                val spanCount = layoutManager!!.spanCount
//                val layoutPosition =
//                    (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
//                if (lp.spanSize != spanCount) {
//                    //左边间距
//                    if (layoutPosition % 2 == 1) {
//                        outRect.left = divider / 2
//                        outRect.right = 0
//                    } else {
//                        outRect.left = 0
//                        outRect.right = (divider / 2) + 15.toIntPx()
//                    }
//                }
//            }
//
//        }
//        binding.ryTopic.addItemDecoration(gridItemDecoration)

        binding.tvClear.setOnClickListener {
            // 清空历史记录。
            viewModel.clearRecord(this)
        }
        binding.ivTopicRight.setOnClickListener {
            JumpUtils.instans?.jump(113)
        }
        historyAdapter.setOnItemClickListener { adapter, view, position ->
            val bean = historyAdapter.getItem(position)
            search(bean.keyword, true)
//            if (position == 7 && !historyAdapter.isExpand) {
//                historyList?.let {
//                    historyAdapter.setExpand(true)
//                    historyAdapter.setList(it)
//                }
//            } else {
//
//            }
        }
        sAdapter.setOnItemClickListener { _, _, position ->
            val bean = sAdapter.getItem(position)
            if (bean.jumpDataType != 0 && bean.jumpDataType != null) {//jump跳转
                JumpUtils.instans?.jump(bean.jumpDataType, bean.jumpDataValue)
            } else {
                search(bean.keyword, true)
            }
        }
        topicAdapter.setOnItemClickListener { _, _, position ->
            val bean = topicAdapter.data[position]
            JumpUtils.instans?.jump(9, bean.topicId.toString())
        }
        searchHotAdapter.setOnItemClickListener { _, _, position ->
            val bean = searchHotAdapter.getItem(position)
            if (bean.jumpDataType != null) {//jump跳转
                JumpUtils.instans?.jump(bean.jumpDataType, bean.jumpDataValue)
            } else {
//                search(bean.keyword, true)
                val bundle = Bundle()
                bundle.putInt(SEARCH_TYPE, searchType)
                bundle.putString(SEARCH_CONTENT, bean.keyword)
                startARouter(ARouterHomePath.PloySearchResultActivity, bundle)
            }
        }
        binding.layoutSearch.searchContent.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == 0) {
                HideKeyboardUtil.showSoftInput(binding.layoutSearch.searchContent)
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.keyCode == KeyEvent.ACTION_UP)) {
                    search(binding.layoutSearch.searchContent.text.toString(), false)
                }
            }
            false
        }

        binding.layoutSearch.searchContent.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        binding.rvAuto.visibility = View.GONE
                    } else {
                        sAdapter.searchContent = s.toString()
                        binding.rvAuto.visibility = View.VISIBLE
                        viewModel.getSearchAc(s.toString())
                    }
                }

            })


        //获取本地搜索历史
        SearchRecordDatabase.getInstance(this).getSearchRecordDao().findAll()
            .observe(this) {
                if (!it.isNullOrEmpty()) {//有数据
                    Collections.reverse(it)//倒叙
                    binding.gHis.visibility = View.VISIBLE
                } else {
                    binding.gHis.visibility = View.GONE
                }
//                mDatas = it as? MutableList<SearchRecordEntity>
//                mDatas?.let {
//                    initZFlowLayout()
//                }
                historyAdapter.setList(it)
            }
        binding.layoutSearch.cancel.setOnClickListener {
            onBackPressed()
        }

        lifecycleScope.launch {
            delay(600)
            HideKeyboardUtil.showSoftInput(binding.layoutSearch.searchContent)
        }
        LiveDataBus.get().withs<String>(LiveDataBusKey.CLOSE_POLY).observe(this){
            finish()
        }
    }

    var searchContent = ""
    fun search(searchContent: String, needHide: Boolean) {
        this.searchContent = searchContent
        if (TextUtils.isEmpty(searchContent)) {
            toastShow("请输入你喜欢的内容")
            return
        }
        if (needHide) {
            HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)
        }
//        binding.layoutSearch.searchContent.setText(searchContent)
        isPs()

    }

    private fun jumpNomarl() {
        val bundle = Bundle()
        bundle.putInt(SEARCH_TYPE, searchType)
        bundle.putString(SEARCH_CONTENT, searchContent)
        startARouter(ARouterHomePath.PloySearchResultActivity, bundle)
        when (searchType) {
            SEARCH_POST -> { //搜索帖子。 埋点。
                BuriedUtil.instant?.communityMainTopSearsh(searchContent)
            }

            SEARCH_ASK -> {
                BuriedUtil.instant?.communityQuestionSerach(searchContent)
            }
        }

    }

//    private fun initZFlowLayout() {
//        if (mDatas == null) return
//        mViewList.clear()
//        for (i in 0 until mDatas!!.size) {
//            val textView = LayoutInflater.from(this)
//                .inflate(
//                    R.layout.item_history_search_new,
//                    binding.recyclerViewHistory,
//                    false
//                ) as TextView
//            textView.text = (mDatas!![i].keyword)
//            mViewList.add(textView)
//        }
//        binding.recyclerViewHistory.setChildren(mViewList)
//        binding.recyclerViewHistory.viewTreeObserver
//            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    binding.recyclerViewHistory.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                    val lineCount: Int = binding.recyclerViewHistory.lineCount //行数
//                    val twoLineViewCount: Int =
//                        binding.recyclerViewHistory.twoLineViewCount //前两行里面view的个数
//                    val expandLineViewCount: Int =
//                        binding.recyclerViewHistory.expandLineViewCount ///展开时显示view的个数
//                    if (lineCount > 2) {  //默认展示2行，其余折叠收起，最多展示5行
//                        initIvClose(twoLineViewCount, expandLineViewCount)
//                    }
//                }
//            })
//        binding.recyclerViewHistory.setOnTagClickListener { _, position ->
//            val bean = mDatas?.get(position)
////            bean?.keyword?.let { search(it, true) }
//            val bundle = Bundle()
//            bundle.putInt(SEARCH_TYPE, searchType)
//            bundle.putString(SEARCH_CONTENT, bean?.keyword)
//            startARouter(ARouterHomePath.PloySearchResultActivity, bundle)
//        }
//    }

//    private fun initIvClose(twoLineViewCount: Int, expandLineViewCount: Int) {
//        mViewList.clear()
//        for (i in 0 until twoLineViewCount) {
//            val textView = LayoutInflater.from(this)
//                .inflate(
//                    R.layout.item_history_search_new,
//                    binding.recyclerViewHistory,
//                    false
//                ) as TextView
//            textView.text = mDatas?.get(i)?.keyword
//            mViewList.add(textView)
//        }
//
//        //展开按钮
//        val imageView = LayoutInflater.from(this)
//            .inflate(
//                R.layout.item_search_history_img,
//                binding.recyclerViewHistory,
//                false
//            ) as ImageView
//        imageView.setImageResource(R.mipmap.ic_his_down_end)
//        imageView.setOnClickListener { v: View? ->
//            initIvOpen(
//                twoLineViewCount,
//                expandLineViewCount
//            )
//        }
//        mViewList.add(imageView)
//        binding.recyclerViewHistory.setChildren(mViewList)
//        binding.recyclerViewHistory.viewTreeObserver
//            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    binding.recyclerViewHistory.viewTreeObserver
//                        .removeOnGlobalLayoutListener(this)
//                    val lineCount: Int = binding.recyclerViewHistory.lineCount
//                    val twoLineViewCount = binding.recyclerViewHistory.twoLineViewCount
//                    if (lineCount > 2) {
//                        initIvClose(
//                            twoLineViewCount - 1,
//                            binding.recyclerViewHistory.expandLineViewCount
//                        )
//                    }
//                }
//            })
//    }

//    private fun initIvOpen(twoLineViewCount: Int, expandLineViewCount: Int) {
//        mViewList.clear()
//        for (i in 0 until expandLineViewCount) {
//            val textView = LayoutInflater.from(this)
//                .inflate(
//                    R.layout.item_history_search_new,
//                    binding.recyclerViewHistory,
//                    false
//                ) as TextView
//            textView.text = mDatas?.get(i)?.keyword
//            mViewList.add(textView)
//        }
//
//        //收起按钮
//        val imageView = LayoutInflater.from(this)
//            .inflate(
//                R.layout.item_search_history_img,
//                binding.recyclerViewHistory,
//                false
//            ) as ImageView
//        imageView.setImageResource(R.mipmap.ic_his_up_end)
//        imageView.setOnClickListener { v: View? ->
//            initIvClose(
//                twoLineViewCount,
//                expandLineViewCount
//            )
//        }
//        mViewList.add(imageView) //不需要的话可以不添加
//        binding.recyclerViewHistory.setChildren(mViewList)
//    }

    fun isPs() {
//        viewModel.getSearchContent(searchContent)
        viewModel.insertRecord(this, searchContent) // 异步写入本地数据库。
        jumpNomarl()
    }

    override fun initData() {
//        viewModel.getSearchHistoryList()
        viewModel.getSearchKeyList()
        viewModel.getTopic()
    }

    override fun observe() {
        super.observe()
        viewModel.searchKeyLiveData.observe(this) {
            if (it.isSuccess) {
//                searchHotAdapter.setNewInstance(it.data as? MutableList<SearchKeyBean>)
                searchHotAdapter.setList(it.data)
                binding.gHotSearch.isVisible = !it.data.isNullOrEmpty()
            }
        }
        viewModel.searchAutoLiveData.observe(this, Observer {
            if (it.isSuccess) {
                sAdapter.setList(it.data)
            }
        })
        viewModel.searchKolingLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.data.extend != null && it.data.extend.jumpDataType > 0) {
                    val jumpDataType = it.data.extend.jumpDataType
                    val jumpDataValue = it.data.extend.jumpDataValue
                    JumpUtils.instans!!.jump(jumpDataType, jumpDataValue)
                } else {
                    jumpNomarl()
                }
            } else {
                jumpNomarl()
            }
        })

        viewModel.hotTopicBean.observe(this) {
            binding.gTopic.isVisible = it.topics.isNotEmpty()
            topicAdapter.setList(it.topics)
        }
    }


}