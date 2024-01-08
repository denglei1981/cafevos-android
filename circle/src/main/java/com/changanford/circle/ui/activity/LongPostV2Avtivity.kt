package com.changanford.circle.ui.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.media.ExifInterface
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.baidu.mapapi.search.core.PoiInfo
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.adapter.ButtomTypeAdapter
import com.changanford.circle.adapter.ButtomlabelAdapter
import com.changanford.circle.adapter.LongPostV2Adapter
import com.changanford.circle.bean.*
import com.changanford.circle.databinding.HeaderEmojiBinding
import com.changanford.circle.databinding.HeaderLongPostBinding
import com.changanford.circle.databinding.LongpostactivityBinding
import com.changanford.circle.viewmodel.PostViewModule
import com.changanford.circle.widget.dialog.CirclePostTagDialog
import com.changanford.circle.widget.pop.ShowSavePostPop
import com.changanford.common.bean.CreateLocation
import com.changanford.common.bean.ImageUrlBean
import com.changanford.common.bean.STSBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.router.startARouterForResult
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.image.ImageCompress
import com.changanford.common.util.image.ImageCompress.compressImage
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.common.widget.HomeBottomDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.changanford.circle.adapter.EmojiAdapter
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.SpecialCarListBean
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.load
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import com.xiaomi.push.it
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig
import java.io.File
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


@Route(path = ARouterCirclePath.LongPostAvtivity)
class LongPostV2Avtivity : BaseActivity<LongpostactivityBinding, PostViewModule>() {

    private lateinit var headBinding: HeaderLongPostBinding

    private val headview by lazy {
        layoutInflater.inflate(R.layout.header_long_post, null)
    }
    private val longpostadapter by lazy {
        LongPostV2Adapter(binding.longpostrec.layoutManager as LinearLayoutManager)
    }

    private var postViewType = MutableLiveData(0)
    private var specialCarListBean: SpecialCarListBean? = null

    private lateinit var plateBean: PlateBean
    private var platename: String = ""
    private var circlename: String = ""
    private var address: String = ""
    private val upedimgs = ArrayList<ImageUrlBean>()  //上传之后的图片集合地址
    private var nomalwith = 500
    private var nomalhight = 500
    private var selectList = ArrayList<LongPostBean>()

    private var type = 0
    private var params = hashMapOf<String, Any>()
    private var FMMeadia: LocalMedia? = null


    private var locaPostEntity: PostEntity? = null
    private var editText: EditText? = null
    private var iskeybarOpen = false

    private var isTopPost = false
    private var isCirclePost: Boolean = false
    private var isH5Post: Boolean = false
    private var postType: Int = 0
    private var h5postbean: H5PostTypeBean? = null
    private lateinit var jsonStr: String

    private var isunSave: Boolean = false  // 要不要保存的标志。
    private val insertPostId by lazy {
        System.currentTimeMillis()
    }
    private val dialog by lazy {
        LoadDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setLoadingText("图片上传中..")
            show()
        }
    }

    private val buttomTypeAdapter by lazy {
        ButtomTypeAdapter()
    }
    private val buttomlabelAdapter by lazy {
        ButtomlabelAdapter()
    }
    private val emojiAdapter by lazy {
        EmojiAdapter()
    }

    companion object {
        const val ITEM_SELECTPIC = 0x5564
    }

    override fun initView() {
        title = "发帖页"
        ImmersionBar.with(this)
            .keyboardEnable(true)
            .init()  //顶起页面底部
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "发文章"
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.text = "下一步"
//        binding.title.barTvOther.background =
//            ContextCompat.getDrawable(this, R.drawable.post_btn_no_bg)
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.color_a680))
        binding.title.barTvOther.textSize = 14f
        headBinding = DataBindingUtil.bind(headview)!!
        locaPostEntity = intent.getSerializableExtra("postEntity") as PostEntity?
        bus()
        isH5Post = intent.extras?.getBoolean("isH5Post") ?: false
        isCirclePost = intent.extras?.getBoolean("isCirclePost") ?: false
        isTopPost = intent.extras?.getBoolean("isTopPost") ?: false

        binding.bottom.tvMore.setOnClickListener {
            showMoreTag()
        }
    }

    private fun showTopic(name: String) {
        headBinding.icAttribute.run {
            tvTopic.visibility = View.GONE
            llTopic.visibility = View.VISIBLE
            tvTopicName.text = name
        }
    }

    private fun showAddress(address: String) {
        headBinding.icAttribute.run {
            tvAddress.visibility = View.GONE
            llAddress.visibility = View.VISIBLE
            tvAddressName.text = address
        }
    }

    private fun showCircle(circleName: String) {
        if (circleName == "发布到广场") {
            return
        }
        headBinding.icAttribute.run {
            tvCircle.visibility = View.GONE
            llCircle.visibility = View.VISIBLE
            tvCircleName.text = circleName
        }
    }

    private fun showCar(carName: String) {
        headBinding.icAttribute.run {
            tvCar.visibility = View.GONE
            llCar.visibility = View.VISIBLE
            tvCarName.text = carName
            val layoutParams = rlCar.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.setMargins(0, -4, 0, 0)
        }
    }

    private fun isCarHistory(isCar: Boolean) {
        headBinding.icAttribute.run {
            clCar.isVisible = isCar
            if (!isCar) {
                params.remove("carModelIds")
                tvCarName.text = ""
                tvCar.visibility = View.VISIBLE
                llCar.visibility = View.GONE
            }
        }
    }

    private fun noTopic() {
        params.remove("topicId")
        headBinding.icAttribute.run {
            tvTopic.visibility = View.VISIBLE
            llTopic.visibility = View.GONE
            tvTopicName.text = ""
        }
    }

    private fun noCircle() {
        params.remove("circleId")
        headBinding.icAttribute.run {
            tvCircle.visibility = View.VISIBLE
            llCircle.visibility = View.GONE
            tvCircleName.text = ""
        }
    }

    private fun noLocation() {
        headBinding.icAttribute.run {
            isunSave = false
            params.remove("lat")
            params.remove("lon")
            params.remove("city")
            params.remove("province")
            params.remove("cityCode")
            params.remove("address")
            params.remove("addrName")
            address = ""
            tvAddress.visibility = View.VISIBLE
            llAddress.visibility = View.GONE
            tvAddressName.text = ""
        }
    }

    override fun observe() {
        super.observe()
        headBinding.ivFm.setCircular(12)
        ImmersionBar.with(this).setOnKeyboardListener { isPopup, keyboardHeight ->
            Log.d("ImmersionBar", keyboardHeight.toString())
            binding.bottom.emojirec.visibility = View.GONE
            binding.bottom.clEmojiHead.visibility = View.GONE

        }
        viewModel.isEnablePost.observe(this) {
            binding.title.barTvOther.isEnabled = it
        }
        LiveDataBus.get().with(LiveDataBusKey.ConversationNO).observe(this) {
            noTopic()
            isCarHistory(false)
        }
        LiveDataBus.get().with(LiveDataBusKey.LONGPOSTFM).observe(this, Observer {
            isunSave = false
            FMMeadia = it as LocalMedia
            headBinding.ivFm.visibility = View.VISIBLE
            headBinding.tvFmTips.visibility = View.VISIBLE
            GlideUtils.loadFilePath(PictureUtil.getFinallyPath(FMMeadia!!), headBinding.ivFm)
            headBinding.ivAddfm.visibility = View.GONE
            headBinding.tvFm.visibility = View.GONE
            checkViewTwoTypeContent()
        })
        LiveDataBus.get().withs<SpecialCarListBean>(LiveDataBusKey.CHOOSE_CAR_POST).observe(this) {
            specialCarListBean = it
            params["carModelIds"] = it.carModelId
            showCar(it.carModelName)
        }
        LiveDataBus.get().with(LiveDataBusKey.LONG_POST_CONTENT).observe(this) {
            checkViewOneTypeContent()
        }
        LiveDataBus.get().with(LiveDataBusKey.LONG_POST_JIAO).observe(this) {
            headBinding.etBiaoti.text?.let { editable ->
                if (editable.length < 2) {
                    headBinding.tvNoTips.visibility = View.VISIBLE
                }
            }
        }
        viewModel.postsuccess.observe(this, Observer {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            if (locaPostEntity != null) {
                viewModel.deletePost(locaPostEntity!!.postsId)
            } else {
                viewModel.deleteLastPost()
            }
            isunSave = true
            "发布成功".toast()
            startARouter(ARouterMyPath.MineFollowUI, true)
            finish()
        })
        viewModel.stsBean.observe(this) {
            it?.let {
                upedimgs.clear()
                selectList.clear()
                selectList.add(LongPostBean("", FMMeadia))
                selectList.addAll(longpostadapter.data)
                var mediacount = 0
                selectList.forEach {
                    if (it.localMedias != null) {
                        mediacount++
                    }
                }
                val needCompressImg = ArrayList<String?>()
                selectList.forEach { bean ->
                    bean.localMedias?.let {
                        needCompressImg.add(
                            PictureUtil.getFinallyPath(
                                bean.localMedias!!
                            )
                        )
                    }
                }

                compressImage(
                    this,
                    needCompressImg,
                    object : ImageCompress.ImageCompressResult {
                        override fun compressSuccess(list: List<File>) {
                            var index = 0
                            selectList.forEach {
                                it.localMedias?.let {
                                    it.myCompressPath = list[index].absolutePath
                                    index++
                                }
                            }
                            uploadImgs(it, 0, dialog, mediacount, 0)
                        }

                        override fun compressFailed() {
                            uploadImgs(it, 0, dialog, mediacount, 0)
                        }

                    })

            }
        }
        viewModel.cityCode.observe(this, Observer {
            params["cityCode"] = it.cityCode ?: ""
            params["city"] = it.cityName
        })
        LiveDataBus.get().with(LiveDataBusKey.Conversation, HotPicItemBean::class.java)
            .observe(
                this
            ) {
                isunSave = false
                buttomTypeAdapter.setData(3, ButtomTypeBean(it.name, 1, 2))
                params["topicId"] = it.topicId.toString()
                showTopic(it.name)
                isCarHistory(it.isBuyCarDiary == 1)
            }


        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION, PoiInfo::class.java).observe(
            this
        ) {
            isunSave = false
            address = it.address ?: it.name ?: ""
            params["address"] = address
            params["addrName"] = it.name
            it.location?.let { mit ->
                params["lat"] = mit.latitude
                params["lon"] = mit.longitude
                viewModel.getCityDetailBylngAndlat(it.location.latitude, it.location.longitude)
            }
            params["province"] = it.province ?: address
            val showCity = it.city.plus("·").plus(it.name)
            buttomTypeAdapter.setData(0, ButtomTypeBean(showCity, 1, 4))
            showAddress(address)
        }

        LiveDataBus.get().with(LiveDataBusKey.CREATE_LOCATION, CreateLocation::class.java)
            .observe(this, Observer {
                isunSave = false
                address = it.address
                params["address"] = address
                params["addrName"] = it.addrName
                params["lat"] = it.lat
                params["lon"] = it.lon
                viewModel.getCityDetailBylngAndlat(it.lat, it.lon)
                params["province"] = it.province
                val showCity = it.city.plus("·").plus(it.addrName)
                buttomTypeAdapter.setData(0, ButtomTypeBean(showCity, 1, 4))
            })
        postViewType.observe(this) {
            hideKeyboard(binding.longpostrec.windowToken)
            lifecycleScope.launch {
                delay(200)
                setViewType()
            }
        }
        viewModel.plateBean.observe(this, Observer {
            plateBean = it
            plateBean.plate.forEach {
                if (it.name == "社区") {
                    buttomTypeAdapter.setData(1, ButtomTypeBean("", 0, 0))
                    buttomTypeAdapter.setData(2, ButtomTypeBean(it.name, 1, 1))
                    platename = it.name
                    params["plate"] = it.plate
                    params["actionCode"] = it.actionCode
                }
            }
        })
        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATIONNOTHING, String::class.java)
            .observe(
                this
            ) {
                isunSave = false
                params.remove("lat")
                params.remove("lon")
                params.remove("city")
                params.remove("province")
                params.remove("cityCode")
                params.remove("address")
                params.remove("addrName")
                address = ""
                buttomTypeAdapter.setData(0, ButtomTypeBean("不显示位置", 1, 4))
                noLocation()
//                    binding.tvLocation.text = "不显示位置"
            }

        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
            // todo  加图片，加一片那种。
            isunSave = false
            val localMedias = it as List<LocalMedia>
            if (longpostadapter.selectionIndex > 0) {// 光标位置后追加图片大于0
                val selectionStr =
                    longpostadapter.getItem(longpostadapter.selectionPosition).content
                val gindex = longpostadapter.currentTxtView?.selectionStart // 光标位置
                val gItem = longpostadapter.selectionPosition
//                toastShow("光标位置===" + gindex)
//                LogUtil.e("post", "光标位置===" + gindex)
//                LogUtil.e("post", "光标item===" + gItem)
                if (gindex != null && selectionStr != null) {
                    if (gindex > selectionStr.length) {
                        toastShow("请更换文本点击位置")
                        return@Observer
                    }
                }

                selectionStr?.let { s ->
                    val startStr = gindex?.let { it1 -> s.substring(0, it1) }
                    val endStr = gindex?.let { it1 -> s.substring(it1, s.length) }
                    val starStrBean = LongPostBean(startStr)
                    val picPostBean: ArrayList<LongPostBean> = arrayListOf()
                    localMedias.forEach { m ->
                        picPostBean.add(LongPostBean("", m))
                        if (localMedias.indexOf(m) < localMedias.size - 1) {
                            picPostBean.add(LongPostBean(""))
                        }
                    }
                    longpostadapter.remove(longpostadapter.getItem(longpostadapter.selectionPosition)) //移除之前的
                    longpostadapter.addData(longpostadapter.selectionPosition, starStrBean)
                    longpostadapter.addData(longpostadapter.selectionPosition + 1, picPostBean)
                    val endStrBean = LongPostBean(endStr)
                    longpostadapter.addData(
                        longpostadapter.selectionPosition + picPostBean.size + 1,
                        endStrBean
                    )
                }
                longpostadapter.selectionIndex = -1
                longpostadapter.selectionPosition = -1
                longpostadapter.currentTxtView?.clearFocus()//清除光标
            } else {// 默认在最后加图片
                localMedias.forEach { m ->
                    longpostadapter.addData(LongPostBean("", m))
                    longpostadapter.addData(LongPostBean(""))
                }
            }
        })
        viewModel.tagsList.observe(this, Observer { ptList ->
            postTagDataList = ptList
            if (locaPostEntity != null) {
                locaPostEntity?.let { lp ->
                    val tagsStr = lp.tags
                    if (!TextUtils.isEmpty(tagsStr)) {
                        try {
                            val gson = Gson()
                            val postTagList = gson.fromJson<List<PostKeywordBean>>(
                                tagsStr,
                                object : TypeToken<List<PostKeywordBean>>() {}.type
                            )
                            buttomlabelAdapter.addData(postTagList)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        initTags()
                    }
                }
            } else {
                handleEditPost()
                initTags()
            }

        })

        viewModel.postError.observe(this, Observer {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
//            showErrorWarn()
        })

    }

    fun showErrorWarn() {
        QuickPopupBuilder.with(this)
            .contentView(R.layout.dialog_post_error)
            .config(
                QuickPopupConfig()
                    .gravity(Gravity.CENTER)
                    .withClick(R.id.btn_comfir, View.OnClickListener {
                        saveInsertPostent(true)
                    }, true)

            ).show()
    }


    fun initTags() {
        val buttomTagList = arrayListOf<PostKeywordBean>()
        postTagDataList?.forEach { td ->
            run {
                for ((index, tag) in td.tags.withIndex()) {
                    if (index >= td.tagMaxCount) {
                        break
                    }
                    buttomTagList.add(tag)
                }
            }
        }
        buttomlabelAdapter.addData(buttomTagList)
    }

    fun saveCgTags(postEntity: PostEntity) {
        // 保存tags
        val data = buttomlabelAdapter.data
        val gson = Gson()
        val toJsonTags = gson.toJson(data)
        postEntity.tags = toJsonTags
    }

    fun initEtContent() {
        val default = LongPostBean(hintStr = "请输入正文，字数小于1000")
        longpostadapter.addData(default)
    }

    override fun initData() {
        initListener()
        initandonclickhead()
        viewModel.getPlate()
        viewModel.getTags() //标签
        val layoutManager = LinearLayoutManager(this)
        binding.longpostrec.layoutManager = layoutManager
        longpostadapter.draggableModule.isDragEnabled = true
        binding.longpostrec.adapter = longpostadapter
        longpostadapter.addHeaderView(headview)
        params["type"] = 4
        initbuttom()
        onclick()
        initlocaData()
        if (isH5Post) {
            postType = intent.extras?.getInt("postType") ?: 0
            jsonStr = intent.extras?.getString("jsonStr") ?: ""
            if (jsonStr.isNotEmpty()) {
                h5postbean = JSON.parseObject(jsonStr, H5PostTypeBean::class.java)
                params["activityId"] = h5postbean?.ext ?: ""
            }
        }
        if (isCirclePost) {
            params["circleId"] = intent.extras?.getString("circleId") ?: "0"
            circlename = intent.extras?.getString("circleName") ?: ""
            circlename.isNotEmpty().let {
                buttomTypeAdapter.setData(4, ButtomTypeBean(circlename, 1, 3))
            }
            showCircle(circlename)
        }
        if (isTopPost) {
            params["topicId"] = intent.extras?.getString("topId") ?: "0"
            params["topicName"] = intent.extras?.getString("topName") ?: ""
            (params["topicName"] as String).isNotEmpty().let {
                buttomTypeAdapter.setData(3, ButtomTypeBean(params["topicName"] as String, 1, 2))
            }
            showTopic(intent.extras?.getString("topName") ?: "")
        }
        if (!intent.getStringExtra("carModelsId").isNullOrEmpty()) {
            params["carModelIds"] = intent.getStringExtra("carModelsId").toString()
            val carModelName = intent.getStringExtra("carModelsName").toString()
            isCarHistory(true)
            showCar(carModelName)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        headBinding.etBiaoti.addTextChangedListener {
            checkViewOneTypeContent()
            it?.let { editable ->
                if (editable.length >= 2) {
                    headBinding.tvNoTips.visibility = View.GONE
                }
            }
        }
        binding.bottom.ivDown.setOnClickListener {
            binding.bottom.emojirec.visibility = View.GONE
            binding.bottom.clEmojiHead.visibility = View.GONE
        }
        binding.longpostrec.setOnTouchListener { v, event ->
            if (event.y > headBinding.etBiaoti.y) {
                val mSelectPics = longpostadapter.data.filter { it.localMedias != null }
                if (!mSelectPics.isNullOrEmpty() && mSelectPics.size > 2) {

                } else {
                    if (postViewType.value != 1) {
                        longpostadapter.currentTxtView?.let {
                            it.requestFocus()
                            HideKeyboardUtil.showSoftInput(it)
                        }
                    }
                }
            }
            false
        }
    }

    private fun initlocaData() {
        if (locaPostEntity != null) {
            headBinding.etBiaoti.setText(locaPostEntity!!.title)
            params["plate"] = locaPostEntity!!.plate
            platename = locaPostEntity!!.plateName
            params["topicId"] = locaPostEntity!!.topicId
            params["type"] = locaPostEntity!!.type
            params["keywords"] = locaPostEntity!!.keywords
            params["circleId"] = locaPostEntity!!.circleId
            circlename = locaPostEntity!!.circleName
            params["content"] = locaPostEntity!!.content
            params["actionCode"] = locaPostEntity!!.actionCode
            params["title"] = locaPostEntity!!.title
            params["address"] = locaPostEntity!!.address
            address = locaPostEntity!!.address
            params["lat"] = locaPostEntity!!.lat
            params["lon"] = locaPostEntity!!.lon
            params["province"] = locaPostEntity!!.province
            params["cityCode"] = locaPostEntity!!.cityCode
            params["city"] = locaPostEntity!!.city
            params["addrName"] = locaPostEntity!!.addrName
            platename = locaPostEntity!!.plateName
            circlename = locaPostEntity!!.circleName
            if (params["plate"] != 0) {
                buttomTypeAdapter.setData(1, ButtomTypeBean("", 0, 0))
                buttomTypeAdapter.setData(2, ButtomTypeBean(locaPostEntity!!.plateName, 1, 1))
            }
            if (locaPostEntity!!.topicName.isNotEmpty()) {
                buttomTypeAdapter.setData(
                    3,
                    ButtomTypeBean(locaPostEntity!!.topicName, 1, 2)
                )
                showTopic(locaPostEntity!!.topicName)
            }
            if (locaPostEntity!!.circleName.isNotEmpty()) {
                buttomTypeAdapter.setData(
                    4,
                    ButtomTypeBean(locaPostEntity!!.circleName, 1, 3)
                )
                showCircle(locaPostEntity!!.circleName)
            }
            if (locaPostEntity?.carModelId?.isNotEmpty() == true) {
                params["carModelIds"] = locaPostEntity!!.carModelId
                showCar(locaPostEntity!!.carModelName)
                isCarHistory(true)
            }
            showLocaPostCity()
            if (locaPostEntity!!.longpostFmLocalMeadle.isNotEmpty()) {
                try {
                    FMMeadia =
                        JSON.parseObject(
                            locaPostEntity!!.longpostFmLocalMeadle,
                            LocalMedia::class.java
                        )
                    headBinding.ivFm.visibility = View.VISIBLE
                    GlideUtils.loadRoundFilePath(
                        PictureUtil.getFinallyPath(FMMeadia!!),
                        headBinding.ivFm
                    )
                    headBinding.ivAddfm.visibility = View.GONE
                    headBinding.tvFm.visibility = View.GONE
                } catch (e: Exception) {

                }

            }
            jsonStr2obj(locaPostEntity!!.longPostDatas)
        } else {
            initEtContent()
        }
    }

    private fun showLocaPostCity() {
        locaPostEntity?.let { lp ->
            var showCity = ""
            if (lp.city.isNotEmpty() && lp.addrName.isNotEmpty()) {
                showCity = locaPostEntity!!.city.plus("·").plus(locaPostEntity!!.addrName)
            }
            if (showCity.isNotEmpty()) {
                showAddress(showCity)
            }
            if (lp.city.isEmpty()) {
                showCity = "定位"
            }
            buttomTypeAdapter.setData(
                0,
                ButtomTypeBean(showCity, 1, 4)
            )
        }
    }

    private fun jsonStr2obj(jonson: String) {
        try {
            val longPostBean: ArrayList<LongPostBean> =
                Gson().fromJson(jonson, object : TypeToken<ArrayList<LongPostBean>>() {}.type)
            longpostadapter.addData(longPostBean)
            longpostadapter.notifyDataSetChanged()
        } catch (e: Exception) {

        }
    }

    private fun initbuttom() {
        binding.typerec.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        binding.typerec.adapter = buttomTypeAdapter
        buttomTypeAdapter.addData(
            arrayListOf(
                ButtomTypeBean("定位", 1, 4),
                ButtomTypeBean("选择模块", 1, 0),
                ButtomTypeBean("", 0, 1),
                ButtomTypeBean("", 0, 2),
                ButtomTypeBean("", 0, 3),
            )
        )
        binding.bottom.labelrec.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        binding.bottom.labelrec.adapter = buttomlabelAdapter

        buttomlabelAdapter.setOnItemClickListener { adapter, view, position ->
            buttomlabelAdapter.getItem(position).isselect =
                !buttomlabelAdapter.getItem(position).isselect
            buttomlabelAdapter.notifyDataSetChanged()
        }

        binding.bottom.emojirec.adapter = emojiAdapter
        val emojiList = ArrayList<String>()
        for (i in EmojiBean.emojiint) {
            getEmojiStringByUnicode(
                i
            ).let {
                emojiList.add(
                    it
                )
            }
        }
        emojiAdapter.setList(emojiList)
        headBinding.etBiaoti.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                editText = headBinding.etBiaoti
            }
        }
        emojiAdapter.setOnItemClickListener { adapter, view, position ->
            val emoji = emojiAdapter.getItem(position)
            setEditContent(emoji)
        }
    }

    private fun setEditContent(emoJi: String?) {
        val rootView = window.decorView
        val focusView = rootView.findFocus()
        if (focusView is EditText) {
            val index = focusView.selectionStart
            val editContent = focusView.text
            index.let { editContent?.insert(it, emoJi) }
        }
    }

    private fun getEmojiStringByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            binding.title.barImgBack.callOnClick()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }


    fun savePostDialog() {
        val postsId = intent?.getStringExtra("postsId")
        if (!isSave()) {
            finish()
        } else {
            if (!postsId.isNullOrEmpty()) {
                finish()
                return
            }
            ShowSavePostPop(this, object : ShowSavePostPop.PostBackListener {
                override fun save() {
                    saveInsertPostent(true)
                }

                override fun unsave() {
                    isunSave = true
                    finish()
                }
            }).showPopupWindow()
        }

    }

    private fun onclick() {

        binding.title.barImgBack.setOnClickListener {
            if (postViewType.value == 1) {
                postViewType.value = 0
            } else {
                savePostDialog()
            }
        }

        binding.bottom.ivEmoj.setOnClickListener {

            HideKeyboardUtil.hideKeyboard(binding.bottom.emojirec.windowToken)

            Timer().schedule(80) {
                binding.bottom.emojirec.post {
                    if (binding.bottom.emojirec.isShown) {
                        binding.bottom.emojirec.visibility = View.GONE
                        binding.bottom.clEmojiHead.visibility = View.GONE
                    } else {
                        binding.bottom.emojirec.visibility = View.VISIBLE
                        binding.bottom.clEmojiHead.visibility = View.VISIBLE
                    }
                }
            }
        }
        binding.title.barTvOther.setOnClickListener {
            if (postViewType.value == 0) {
                postViewType.value = 1
            } else {
                ispost()
            }
        }

        buttomTypeAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.buttom_iv_close) {
                buttomTypeAdapter.setData(
                    position,
                    ButtomTypeBean("", 0, buttomTypeAdapter.getItem(position).itemType)
                )
                when (buttomTypeAdapter.getItem(position).itemType) {
                    2 -> {
                        params.remove("topicId")
                        params.remove("topicName")
                    }

                    3 -> {
                        params.remove("circleId")
                        params.remove("circleName")
                        circlename = ""
                    }

                    4 -> {
                        params.remove("lat")
                        params.remove("lon")
                        params.remove("city")
                        params.remove("province")
                        params.remove("cityCode")
                        params.remove("address")
                        address = ""
                    }
                }
            }

        }

        buttomTypeAdapter.setOnItemClickListener { adapter, view, position ->

            val buttomType = buttomTypeAdapter.getItem(position).itemType
            when (buttomType) {
                0, 1 -> { // 选择板块
                    showPlate()
                }

                2 -> { // 话题
                    toHuati()
                }

                3 -> {// 圈子
                    toQuanzi()
                }

                4 -> { // 选择地址。
                    if (!LocationServiceUtil.isLocServiceEnable(this)) {//没有打开定位服务
                        openLocationService()
                    } else {
                        isunSave = true
                        startARouter(ARouterCirclePath.ChooseLocationActivity)
                    }
                }

            }
        }


        binding.bottom.ivQuanzi.setOnClickListener {
            toQuanzi()
        }

        binding.bottom.ivLoc.setOnClickListener {
            isunSave = true
            startARouter(ARouterCirclePath.ChooseLocationActivity)
        }
        binding.bottom.ivHuati.setOnClickListener {
            toHuati()
        }
        headBinding.icAttribute.apply {
            rlTopic.setOnClickListener { toHuati() }
            tvDeleteTopic.setOnClickListener {
                params.remove("topicId")
                headBinding.icAttribute.run {
                    tvTopic.visibility = View.VISIBLE
                    llTopic.visibility = View.GONE
                    tvTopicName.text = ""
                }
                isCarHistory(false)
            }
            rlCircle.setOnClickListener { toQuanzi() }
            rlCar.setOnClickListener { chooseCar() }
            tvDeleteCircle.setOnClickListener {
                params.remove("circleId")

                headBinding.icAttribute.run {
                    tvCircle.visibility = View.VISIBLE
                    llCircle.visibility = View.GONE
                    tvCircleName.text = ""
                }
            }
            tvDeleteCar.setOnClickListener {
                params.remove("carModelIds")

                headBinding.icAttribute.run {
                    tvCar.visibility = View.VISIBLE
                    llCar.visibility = View.GONE
                    tvCarName.text = ""
                    val layoutParams = rlCar.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.setMargins(0, 0, 0, 0)
                }
            }
            rlAddress.setOnClickListener {
                isunSave = true
                startARouter(ARouterCirclePath.ChooseLocationActivity)
            }
            tvDeleteAddress.setOnClickListener {
                headBinding.icAttribute.run {

                    isunSave = false
                    params.remove("lat")
                    params.remove("lon")
                    params.remove("city")
                    params.remove("province")
                    params.remove("cityCode")
                    params.remove("address")
                    params.remove("addrName")
                    address = ""

                    tvAddress.visibility = View.VISIBLE
                    llAddress.visibility = View.GONE
                    tvAddressName.text = ""
                }
            }

        }
        binding.bottom.ivPic.setOnClickListener {
            isunSave = true
            val meadiaList: ArrayList<LocalMedia> = arrayListOf()
            PictureUtil.openGallery(
                this, meadiaList,
                onResultCallbackListener = object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        if (result != null) {
                            meadiaList.clear()
                            meadiaList.addAll(result)
                        }
                        if (meadiaList.size > 0) {
                            val bundle = Bundle()
                            bundle.putParcelableArrayList("picList", meadiaList)
                            bundle.putInt("position", 0)
                            bundle.putInt("showEditType", -1)
                            startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
                        }
                    }

                    override fun onCancel() {
                        isunSave = false
                    }

                }, isCompress = false
            )
        }



        longpostadapter.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.iv_delete -> {
                    longpostadapter.removeAt(position)
                    resetAdapter()
                    checkViewOneTypeContent()
                }

                R.id.iv_pic -> {
                    isunSave = true
                    PictureUtil.openGalleryOnePic(this@LongPostV2Avtivity,
                        object : OnResultCallbackListener<LocalMedia> {
                            override fun onResult(result: MutableList<LocalMedia>?) {

                                val localMedia = result?.get(0)
                                localMedia?.let {
                                    val bundle = Bundle()
                                    bundle.putParcelableArrayList(
                                        "picList",
                                        arrayListOf(localMedia)
                                    )
                                    bundle.putInt("position", 0)
                                    bundle.putInt("showEditType", 0)
                                    bundle.putString(
                                        "itemcontent",
                                        longpostadapter.getItem(position).content
                                    )
                                    bundle.putInt("itemPosition", position)
                                    bundle.putBoolean("longpostItemSelect", true)
                                    startARouterForResult(
                                        this@LongPostV2Avtivity,
                                        ARouterCirclePath.PictureeditlActivity,
                                        bundle,
                                        ITEM_SELECTPIC
                                    )
                                }

                            }

                            override fun onCancel() {
                                isunSave = false
                            }

                        })
                }

                R.id.ivFm -> {
                    val array = ArrayList<String>()
                    array.add("编辑图片")
                    array.add("删除图片")
                    HomeBottomDialog(this@LongPostV2Avtivity, *array.toTypedArray())
                        .setOnClickItemListener(object :
                            HomeBottomDialog.OnClickItemListener {
                            override fun onClickItem(mposition: Int, str: String) {
                                when (str) {
                                    "编辑图片" -> {
                                        isunSave = true
                                        var bundle = Bundle()
                                        bundle.putParcelableArrayList(
                                            "picList",
                                            arrayListOf(longpostadapter.getItem(position).localMedias)
                                        )
                                        bundle.putInt("position", position)
                                        bundle.putInt("showEditType", 0)
                                        bundle.putString(
                                            "itemcontent",
                                            longpostadapter.getItem(position).content
                                        )
                                        bundle.putInt("itemPosition", position)
                                        bundle.putBoolean("longpostItemSelect", true)
                                        startARouterForResult(
                                            this@LongPostV2Avtivity,
                                            ARouterCirclePath.PictureeditlActivity,
                                            bundle,
                                            ITEM_SELECTPIC
                                        )
                                    }

                                    "删除图片" -> {
                                        longpostadapter.getItem(position).localMedias = null
                                        longpostadapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }).show()
                }
            }
        }

        longpostadapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

                "drag start".logD()
                val holder = viewHolder as BaseViewHolder
                // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.WHITE
                val endColor = Color.rgb(245, 245, 245)
                val v = ValueAnimator.ofArgb(startColor, endColor)
                v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                v.duration = 300
                v.start()
                holder.itemView.alpha = 0.7f
//                "postionStar====${pos}".logE()
            }

            override fun onItemDragMoving(
                source: RecyclerView.ViewHolder?,
                from: Int,
                target: RecyclerView.ViewHolder?,
                to: Int
            ) {
                """"move from: " + source.getAdapterPosition() + " to: " + target.getAdapterPosition() """.logD()
            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                "drag end".logD()
                val holder = viewHolder as BaseViewHolder
                // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.rgb(245, 245, 245)
                val endColor = Color.WHITE
                val v = ValueAnimator.ofArgb(startColor, endColor)
                v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                v.duration = 300
                v.start()
                holder.itemView.alpha = 1f
                resetAdapter()
//                "postionEnd====${pos}".logE()
//                val longBean = longpostadapter.getItem(pos)
//                val prePosition = pos - 1
//                if (prePosition > 0) {
//                    if (longBean.localMedias != null) {
//                        // 拖动的是图
//                        val item = longpostadapter.getItem(prePosition)
//                        if (item.localMedias != null) {
//                            longpostadapter.addData(pos, LongPostBean(""))
//                        }
//                        if (longpostadapter.data.size == pos+1) {//把图拖动到最后一个了,加一个文本
//                            longpostadapter.addData(LongPostBean(""))
//                        }
//                        // 图之间,要加个文本。
//                    } else {// 拖动的是文本
//                        val item = longpostadapter.getItem(prePosition)
//                        if (item.localMedias == null) { // 是文本，合二唯一
//                            val currentBean = longpostadapter.getItem(pos)
//                            currentBean.content = item.content.plus("\n"+currentBean.content)
//                            longpostadapter.notifyItemChanged(pos)
//                            longpostadapter.remove(item)
//                        }
//                    }
//                }
            }
        })
    }

    private fun resetEditeAdapter() {
        val data = longpostadapter.data
        val copyOnWriteArrayList = CopyOnWriteArrayList<LongPostBean>()
        copyOnWriteArrayList.addAll(data)
        lifecycleScope.launch {
            addEdite(copyOnWriteArrayList)
            longpostadapter.data = copyOnWriteArrayList
            longpostadapter.notifyDataSetChanged()
        }
    }

    private fun resetAdapter() {
        val data = longpostadapter.data
        val copyOnWriteArrayList = CopyOnWriteArrayList<LongPostBean>()
        copyOnWriteArrayList.addAll(data)
        lifecycleScope.launch {
            addEdite(copyOnWriteArrayList)
            delay(300)
            removeEdite(copyOnWriteArrayList)
            addEdite(copyOnWriteArrayList)
            longpostadapter.data = copyOnWriteArrayList
            longpostadapter.notifyDataSetChanged()
        }
    }

    private fun addEdite(data: CopyOnWriteArrayList<LongPostBean>) {
        if (data.isNotEmpty()) {
            if (data[0].localMedias != null) {
                val newTxt = LongPostBean("")
                data.add(0, newTxt)
            }
        }
        for (i in 0 until data.size - 1) {
            val item = data[i]
            if (item.localMedias != null) {
                val nextItem = data[i + 1]
                if (nextItem.localMedias != null) {
                    val newTxt = LongPostBean("")
                    data.add(i + 1, newTxt)
                }
            }
        }
        if (data.size - 1 < 0) {
            return
        }
        if (data[data.size - 1].localMedias != null) {
            val newTxt = LongPostBean("")
            data.add(data.size, newTxt)
        }
    }

    private fun removeEdite(copyOnWriteArrayList: CopyOnWriteArrayList<LongPostBean>) {
        for (i in copyOnWriteArrayList.size - 1 downTo 0) {
            val item = copyOnWriteArrayList[i]
            if (item.localMedias == null) {
                val preIndex = i - 1
                if (preIndex >= 0) {
                    val preItem = copyOnWriteArrayList[preIndex]
                    if (preItem.localMedias == null) { // 是文本
                        val content =
                            if (preItem.content?.trim()
                                    ?.isNotEmpty() == true && item.content?.trim()
                                    ?.isNotEmpty() == true
                            ) preItem.content + "\n" else preItem.content
                        val newContent =
                            content.plus(item.content)
                        item.content = newContent// 新文本内容
                        copyOnWriteArrayList.remove(preItem) // 移除前一个文本
                    }
                }

            }
        }
    }

    private fun toQuanzi() {
        isunSave = true
        startARouterForResult(
            this,
            ARouterCirclePath.ChoseCircleActivity,
            PostActivity.REQUEST_CIRCLE
        )
    }

    private fun toHuati() {
        isunSave = true
        startARouter(ARouterCirclePath.ChooseConversationActivity)
    }

    private fun ispost() {
        var biaoti = headBinding.etBiaoti.text.toString()
        when {
            FMMeadia == null -> {
                "请选择封面".toast()
                return
            }

            biaoti.isEmpty() || biaoti.length > 30 || biaoti.length < 2 -> {
                "请输入2-30字的帖子标题".toast()
                return
            }

            headBinding.icAttribute.clCar.isVisible && params["carModelIds"] == null -> {
                "请选择车型".toast()
                return
            }

            platename.isEmpty() -> {
                "请选择模块".toast()
            }

            else -> {
                params["title"] = biaoti
                //埋点
                viewModel.getOSS()
            }
        }
    }

    private fun chooseCar() {
        startARouter(ARouterCirclePath.ChooseCarActivity, Bundle().apply {
            if (params["carModelIds"] != null) {
                putString("carModelIds", params["carModelIds"].toString())
            }
        })
    }

    private fun initandonclickhead() {
        val bthinttxt = "请输入标题 (2-30个字)"
//        val spannableString = SpannableString(bthinttxt)
//        val intstart = bthinttxt.indexOf('(')
//        val intend = bthinttxt.length
//        spannableString.setSpan(
//            AbsoluteSizeSpan(60),
//            0,
//            intstart,
//            Spannable.SPAN_INCLUSIVE_INCLUSIVE
//        )
//        spannableString.setSpan(
//            AbsoluteSizeSpan(40),
//            intstart,
//            intend,
//            Spannable.SPAN_INCLUSIVE_INCLUSIVE
//        )
        headBinding.etBiaoti.hint = bthinttxt
        headBinding.etBiaoti.requestFocus()
        editText = headBinding.etBiaoti

        headBinding.ivAddfm.setOnClickListener {
            isunSave = true
            PictureUtil.openGalleryOnePic(this, object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: MutableList<LocalMedia>?) {
                    val localMedia = result?.get(0)
                    localMedia?.let {
                        val bundle = Bundle()
                        bundle.putParcelableArrayList("picList", arrayListOf(localMedia))
                        bundle.putInt("position", 0)
                        bundle.putInt("showEditType", -1)
                        bundle.putBoolean("longPostFM", true)
                        startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
                    }
                }

                override fun onCancel() {
                    isunSave = false
                }

            })
        }
        headBinding.tFl.setOnClickListener {
            if (headBinding.ivAddfm.isVisible) {
                isunSave = true
                PictureUtil.openGalleryOnePic(this, object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        val localMedia = result?.get(0)
                        localMedia?.let {
                            val bundle = Bundle()
                            bundle.putParcelableArrayList("picList", arrayListOf(localMedia))
                            bundle.putInt("position", 0)
                            bundle.putInt("showEditType", -1)
                            bundle.putBoolean("longPostFM", true)
                            startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
                        }
                    }

                    override fun onCancel() {
                        isunSave = false
                    }

                })
            }
        }
        headBinding.ivFm.setOnClickListener {
            val array = ArrayList<String>()
            array.add("编辑封面")
            array.add("重选封面")
//            array.add("删除封面")
            HomeBottomDialog(this, *array.toTypedArray())
                .setOnClickItemListener(object :
                    HomeBottomDialog.OnClickItemListener {
                    override fun onClickItem(position: Int, str: String) {
                        isunSave = true
                        when (str) {
                            "重选封面" -> {
                                PictureUtil.openGalleryOnePic(this@LongPostV2Avtivity,
                                    object : OnResultCallbackListener<LocalMedia> {
                                        override fun onResult(result: MutableList<LocalMedia>?) {
                                            val localMedia = result?.get(0)
                                            localMedia?.let {
                                                val bundle = Bundle()
                                                bundle.putParcelableArrayList(
                                                    "picList",
                                                    arrayListOf(localMedia)
                                                )
                                                bundle.putInt("position", 0)
                                                bundle.putInt("showEditType", -1)
                                                bundle.putBoolean("longPostFM", true)
                                                startARouter(
                                                    ARouterCirclePath.PictureeditlActivity,
                                                    bundle
                                                )
                                            }
                                        }

                                        override fun onCancel() {
                                            isunSave = false
                                        }

                                    })

                            }

                            "编辑封面" -> {
                                val bundle = Bundle()
                                bundle.putParcelableArrayList("picList", arrayListOf(FMMeadia))
                                bundle.putInt("position", 0)
                                bundle.putInt("showEditType", -1)
                                bundle.putBoolean("longPostFM", true)
                                startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
                            }

                            "删除封面" -> {
                                FMMeadia = null
                                headBinding.ivAddfm.visibility = View.VISIBLE
                                headBinding.tvFm.visibility = View.VISIBLE
                                headBinding.ivFm.visibility = View.GONE
                                headBinding.tvFmTips.visibility = View.GONE

                            }
                        }
                    }
                }).show()
        }
    }

    private fun uploadImgs(
        stsBean: STSBean,
        index: Int,
        dialog: LoadDialog,
        mediacount: Int,
        indexcount: Int
    ) {
        var path = ""
        var ytPath = ""
        val scount = index + 1

        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )

        if (selectList[index].localMedias != null) {  //封面必不为空 index 0必有值
            val media = selectList[index].localMedias!!
            ytPath = if (media.myCompressPath.isNullOrEmpty()) {
                PictureUtil.getFinallyPath(media)
            } else {
                media.myCompressPath
            }

            Log.d("=============", "${ytPath}")
            val type = ytPath.substring(ytPath.lastIndexOf(".") + 1, ytPath.length)
            val exifInterface = ExifInterface(ytPath);
            val rotation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            path = stsBean.tempFilePath + System.currentTimeMillis() + "androidios${
                if (media.isCut) {
                    if (rotation == ExifInterface.ORIENTATION_ROTATE_90 || rotation == ExifInterface.ORIENTATION_ROTATE_270) {
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 500)
                    } else {
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 500);
                    }
                } else {
                    if (media.width == 0) {
                        nomalwith
                    } else {
                        media.width
                    }
                }
            }_${
                if (media.isCut) {
                    if (rotation == ExifInterface.ORIENTATION_ROTATE_90 || rotation == ExifInterface.ORIENTATION_ROTATE_270) {
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 500);
                    } else {
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 500)
                    }
                } else {
                    if (media.height == 0) {
                        nomalwith
                    } else {
                        media.height
                    }
                }
            }." + type
            if (index == 0) {
                params["pics"] = path
            }

        } else {
            if (index == 0) {
                params["pics"] = path
            } else if (index + 1 == selectList.size) {  //最后一个图片为空 开始post
                upedimgs.add(ImageUrlBean("", longpostadapter.getItem(index - 1).content!!))
                addPost()
                return
            } else {
                if (longpostadapter.getItem(index - 1).content?.isNotEmpty() == true) {
                    upedimgs.add(ImageUrlBean("", longpostadapter.getItem(index - 1).content!!))
                }
                uploadImgs(stsBean, scount, dialog, mediacount, indexcount)
                return
            }
        }

        AliYunOssUploadOrDownFileConfig.getInstance(this)
            .uploadFile(stsBean.bucketName, path, ytPath, "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {
                if (index != 0) {
                    upedimgs.add(ImageUrlBean(path, longpostadapter.getItem(index - 1).content!!))
                }
                val mindexpic = indexcount + 1
                runOnUiThread {
                    Log.d("mcount--", "${mindexpic}")
                    dialog.setTvprogress("${mindexpic}/${mediacount}")
                }
                if (scount == selectList.size) {
                    addPost()
                    return
                }
                uploadImgs(stsBean, scount, dialog, mediacount, mindexpic)
            }

            override fun onUploadFileFailed(errCode: String) {
                errCode.toast()
                dialog.dismiss()
            }

            override fun onuploadFileprogress(
                request: PutObjectRequest,
                currentSize: Long,
                totalSize: Long
            ) {
            }
        })
    }

    fun addPost() {
        var tagIds = ""
        var tagNames = ""
        // 移除都是空的情况。
        val last =
            upedimgs.filter { !TextUtils.isEmpty(it.imgDesc) || !TextUtils.isEmpty(it.imgUrl) }
        last?.let {
            params["imgUrl"] = last
        }
        params["isPublish"] = 2
        buttomlabelAdapter.data.forEach {
            if (it.isselect) {
                tagIds += it.id + ","
                tagNames += it.tagName + ","
            }
        }
        params["tagIds"] = tagIds
        params["content"] = ""
        JSON.toJSONString(params).logD()

        try {
            val biaoti = params["title"]
            val content = "" //content 没有用了
            BuriedUtil.instant?.post(biaoti.toString(), content, tagNames)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        viewModel.postEdit(params)
    }

    private var circlePostTagDialog: CirclePostTagDialog? = null
    var postTagDataList: List<PostTagData>? = null
    fun showMoreTag() {
        if (postTagDataList == null) {
            toastShow("没有可选的标签")
            return
        }
        circlePostTagDialog = CirclePostTagDialog(this, object : CirclePostTagDialog.ICallbackTag {
            override fun callbackTag(
                cancel: Boolean,
                tags: MutableList<PostKeywordBean>,
                totalTags: Int
            ) {
                if (!cancel) {
                    buttomlabelAdapter.setNewInstance(tags)
                }
            }
        }, postTagDataList!!, buttomlabelAdapter.data)
        circlePostTagDialog?.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isunSave = false
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PostActivity.REQUEST_CIRCLE -> {
                    if (data != null) {
                        val mCircleId = data.getIntExtra("circleId", 0)
                        params["circleId"] = mCircleId

                        circlename = data.getStringExtra("name").toString()
                        buttomTypeAdapter.setData(4, ButtomTypeBean(circlename, 1, 3))
                        showCircle(circlename)
                        if (mCircleId == 0) {
                            noCircle()
                        }
                    }
                }

                ITEM_SELECTPIC -> {
                    val media = data!!.getParcelableArrayListExtra<LocalMedia>("itemMedia")
                    val itemposition = data!!.getIntExtra("position", 0)
                    longpostadapter.getItem(itemposition).apply {
                        localMedias = media!![0]
                        content = media!![0].contentDesc
                    }
                    longpostadapter.notifyDataSetChanged()
                    checkViewOneTypeContent()
                }
            }
        }
    }

    private fun bus() {
//        LiveDataBus.get().withs<EditText>(CircleLiveBusKey.POST_EDIT).observe(this, {
//            editText = it
//        })
    }

    fun handleEditPost() {
        val postsId = intent?.getStringExtra("postsId")
        postsId?.let {
            //赋值
            viewModel.postDetailsBean.observe(this) {
                it?.let { locaPostEntity ->
                    if (locaPostEntity != null) {//同草稿逻辑
                        headBinding.etBiaoti.setText(locaPostEntity.title)
                        params["plate"] = locaPostEntity.plate
                        platename = locaPostEntity.plateName
                        params["topicId"] = locaPostEntity.topicId
                        params["postsId"] = locaPostEntity.postsId
                        params["type"] = locaPostEntity.type
                        locaPostEntity.keywords?.let { k ->
                            params["keywords"] = k
                        }
                        params["circleId"] = locaPostEntity.circleId
                        circlename = locaPostEntity.circleName ?: ""
                        params["content"] = locaPostEntity.content ?: ""
                        params["actionCode"] = locaPostEntity.actionCode
                        params["title"] = locaPostEntity.title
                        params["address"] = locaPostEntity.address ?: ""
                        if (locaPostEntity.address?.isNotEmpty() == true) {
                            address = locaPostEntity.address.toString()
                        }
                        params["lat"] = locaPostEntity.lat
                        params["lon"] = locaPostEntity.lon
                        params["addrName"] = locaPostEntity.addrName ?: ""
                        params["province"] = locaPostEntity.province
                        params["cityCode"] = locaPostEntity.cityCode
                        params["city"] = locaPostEntity.city
                        if (params["plate"] != 0) {
                            buttomTypeAdapter.setData(1, ButtomTypeBean("", 0, 0))
                            buttomTypeAdapter.setData(
                                2,
                                ButtomTypeBean(locaPostEntity.plateName, 1, 1)
                            )
                        }
                        if (locaPostEntity.carModelIds != null) {
                            params["carModelIds"] = locaPostEntity.carModelIds
                            isCarHistory(true)
                            locaPostEntity.carModelName?.let { it1 -> showCar(it1) }
                        }
                        if (locaPostEntity.topicName?.isNotEmpty() == true) {
                            buttomTypeAdapter.setData(
                                3,
                                ButtomTypeBean(locaPostEntity.topicName ?: "", 1, 2)
                            )
                            showTopic(locaPostEntity.topicName)
                        }
                        if (locaPostEntity.circleName?.isNotEmpty() == true) {
                            buttomTypeAdapter.setData(
                                4,
                                ButtomTypeBean(locaPostEntity.circleName ?: "", 1, 3)
                            )
                            showCircle(locaPostEntity.circleName)
                        }
                        showLocaPostCity()
                        locaPostEntity.let { lp ->
                            var showCity = ""
                            if (lp.city.isNotEmpty() && lp.addrName?.isNotEmpty() == true) {
                                showCity =
                                    locaPostEntity.city.plus("·").plus(locaPostEntity.addrName)
                            }
                            if (showCity.isNotEmpty()) {
                                showAddress(showCity)
                            }
                            if (lp.city.isEmpty()) {
                                showCity = "定位"
                            }
                            buttomTypeAdapter.setData(
                                0,
                                ButtomTypeBean(showCity, 1, 4)
                            )
                        }
                        //选择的标签
                        if (TextUtils.isEmpty(locaPostEntity.keywords)) {
                            buttomlabelAdapter.data.forEach {
                                it.isselect = it.tagName == locaPostEntity.keywords
                            }
                            buttomlabelAdapter.notifyDataSetChanged()
                        }
                        //图片下载,第一张图为封面图
                        locaPostEntity.imageList?.let {
                            val templist = ArrayList<ImageList>()
                            templist.add(ImageList(locaPostEntity.pics))
                            templist.addAll(it)
                            viewModel.downGlideImgs(templist)
                        }
                        //监听下载的图片
                        viewModel._downloadLocalMedias.observe(this) {
                            //选择的图片重置
                            //封面逻辑
                            FMMeadia = it[0]
                            headBinding.ivFm.visibility = View.VISIBLE
                            headBinding.tvFmTips.visibility = View.VISIBLE
                            GlideUtils.loadRoundFilePath(
                                PictureUtil.getFinallyPath(FMMeadia!!),
                                headBinding.ivFm
                            )
                            headBinding.ivAddfm.visibility = View.GONE
                            headBinding.tvFm.visibility = View.GONE
                            //长图部分
//                            selectList.clear()
                            longpostadapter.data.clear()
                            it.forEachIndexed { index, localMedia ->
                                if (index != 0) {
                                    val longPostBean = LongPostBean(
                                        locaPostEntity.imageList?.get(index - 1)?.imgDesc ?: "",
                                        if (localMedia.realPath?.isEmpty() == true) null else localMedia
                                    )
                                    //展示选择的图片
                                    longpostadapter.addData(longPostBean)
                                }
                            }
                            lifecycleScope.launch {
                                delay(500)
                                resetEditeAdapter()
                            }
                        }
                    }
                }
            }
            viewModel.getPostById(it)
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isunSave) {
            ondesSave()
        }
    }

    fun isSave(): Boolean {
        if (headBinding.etBiaoti.text.toString().isNotEmpty()) {
            return true
        } else if (selectList.size > 0) {
            return true
        } else if (
            buttomTypeAdapter.getItem(3).content.isNotEmpty()
            || buttomTypeAdapter.getItem(4).content.isNotEmpty()
        ) {
            return true
        } else if (buttomTypeAdapter.getItem(0).content.isNotEmpty()) {
            val bottomStr = buttomTypeAdapter.getItem(0).content
            if ("定位" == bottomStr) {
                return false
            }
            return true
        } else if (buttomTypeAdapter.getItem(2).content.isNotEmpty()) {
            val bottomStr = buttomTypeAdapter.getItem(2).content
            if ("社区" == bottomStr) {
                return false
            }
            return true
        }
        buttomlabelAdapter.data.forEach {
            if (it.isselect) {
                return true
            }
        }
        return false
    }

    fun ondesSave() {
        val postsId = intent?.getStringExtra("postsId")
        if (!postsId.isNullOrEmpty()) {
            return
        }
        if (isSave()) {
            saveInsertPostent(false)
        }
    }

    fun saveInsertPostent(isHandleSave: Boolean) {
        val postEntity = if (locaPostEntity != null) locaPostEntity!! else PostEntity()
        if (postEntity.postsId == 0L) {
            postEntity.postsId = insertPostId
        }
//        postEntity.content = headBinding.etContent.text.toString() //内容
        postEntity.circleId =
            if (params["circleId"] == null) "" else params["circleId"].toString()  //选择圈子的id
        postEntity.circleName = circlename  //选择圈子的名称
        postEntity.plate =
            if (params["plate"] == null) 0 else params["plate"] as Int//模块ID
//        postEntity.plateName = platename  //模块名称
        postEntity.topicId =
            if (params["topicId"] == null) "" else params["topicId"] as String  //话题ID
        postEntity.topicName = buttomTypeAdapter.getItem(3).content ?: ""  //话题名称
        postEntity.carModelId =
            if (params["carModelIds"] == null) "" else params["carModelIds"] as String  //车型ID
        postEntity.carModelName =
            if (params["carModelIds"] == null) "" else headBinding.icAttribute.tvCarName.text.toString()   //车型名称
        postEntity.keywords =
            if (params["keywords"] != null) params["keywords"].toString() else ""  //关键字
//                    postEntity.keywordValues = binding.keywordTv.text.toString()
        postEntity.localMeadle = JSON.toJSONString(selectList)
        postEntity.actionCode =
            if (params["actionCode"] != null) params["actionCode"] as String else ""
        postEntity.longpostFmLocalMeadle =
            if (FMMeadia != null) JSON.toJSONString(FMMeadia) else ""
        postEntity.longPostDatas = JSON.toJSONString(longpostadapter.data)
        postEntity.type = "4"  //长图帖子类型
        postEntity.title = headBinding.etBiaoti.text.toString()
        postEntity.address =
            if (params["address"] != null) params["address"] as String else ""

        postEntity.addrName =
            if (params["addrName"] != null) params["addrName"] as String else ""

        postEntity.lat = if (params["lat"] != null) params["lat"] as Double else 0.0
        postEntity.lon = if (params["lon"] != null) params["lon"] as Double else 0.0
        postEntity.city =
            if (params["city"] != null) params["city"] as String else ""
        postEntity.province =
            if (params["province"] != null) params["province"] as String else ""
        postEntity.cityCode =
            if (params["cityCode"] != null) params["cityCode"] as String else ""
        postEntity.creattime = System.currentTimeMillis().toString()
        saveCgTags(postEntity)
        viewModel.insertPostentity(postEntity)
        if (isHandleSave) {
            finish()
        }

    }

    fun showPlate() {
        if (::plateBean.isInitialized && plateBean.plate.isNotEmpty()) {
            val sList = mutableListOf<String>()
            for (bean in plateBean.plate) {
                sList.add(bean.name)
            }
            HomeBottomDialog(this, *sList.toTypedArray()).setOnClickItemListener(object :
                HomeBottomDialog.OnClickItemListener {
                override fun onClickItem(position: Int, str: String) {
                    buttomTypeAdapter.setData(1, ButtomTypeBean("", 0, 0))
                    buttomTypeAdapter.setData(2, ButtomTypeBean(str, 1, 1))
                    platename = str
                    params["plate"] = plateBean.plate[position].plate
                    params["actionCode"] = plateBean.plate[position].actionCode
                }
            }).show()
        } else {
            viewModel.getPlate()
        }
    }

    fun openLocationService() {
        QuickPopupBuilder.with(this)
            .contentView(R.layout.pop_open_location_service)
            .config(
                QuickPopupConfig()
                    .gravity(Gravity.CENTER)
                    .withClick(R.id.btn_comfir, View.OnClickListener {
                        showLoctionServicePermission()
                    }, true)
                    .withClick(R.id.btn_cancel, View.OnClickListener {
                        finish()
                    }, true)
            )
            .show()
    }

    private fun checkViewOneTypeContent() {
        if (postViewType.value == 1) {
            return
        }
        val titleContent = headBinding.etBiaoti.text
        val titleHasContent = titleContent?.isNotEmpty() == true && titleContent.length > 1
        val content =
            longpostadapter.data.filter { it.content?.isNotEmpty() == true || it.localMedias != null }
        if (titleHasContent && content.isNotEmpty()) {
            binding.title.barTvOther.isEnabled = true
            binding.title.barTvOther.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.color_1700F4
                )
            )
//            binding.title.barTvOther.background =
//                ContextCompat.getDrawable(this, R.drawable.post_btn_bg)
        } else {
            binding.title.barTvOther.isEnabled = false
            binding.title.barTvOther.setTextColor(ContextCompat.getColor(this, R.color.color_a680))
//            binding.title.barTvOther.background =
//                ContextCompat.getDrawable(this, R.drawable.post_btn_no_bg)
        }
    }

    private fun checkViewTwoTypeContent() {
        if (FMMeadia == null) {
            binding.title.barTvOther.isEnabled = false
            binding.title.barTvOther.setTextColor(ContextCompat.getColor(this, R.color.color_a680))
//            binding.title.barTvOther.background =
//                ContextCompat.getDrawable(this, R.drawable.post_btn_no_bg)
        } else {
            binding.title.barTvOther.isEnabled = true
            binding.title.barTvOther.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.color_1700F4
                )
            )
//            binding.title.barTvOther.background =
//                ContextCompat.getDrawable(this, R.drawable.post_btn_bg)
        }
    }

    private fun setViewType() {
        when (postViewType.value) {
            0 -> {
                binding.title.barTvOther.text = "下一步"
                headBinding.tFl.visibility = View.GONE
                headBinding.tvFmTips2.visibility = View.GONE
                headBinding.icAttribute.root.visibility = View.GONE
                binding.bottom.root.visibility = View.VISIBLE
                headBinding.etBiaoti.visibility = View.VISIBLE
                headBinding.tvline.visibility = View.VISIBLE
                binding.longpostrec.isNestedScrollingEnabled = true
                binding.longpostrec.isVerticalScrollBarEnabled = true
                val linearLayoutManager: LinearLayoutManager =
                    object : LinearLayoutManager(this) {
                        override fun canScrollVertically(): Boolean {
                            return true
                        }
                    }
                binding.longpostrec.layoutManager = linearLayoutManager
                longpostadapter.needGone = false
                longpostadapter.notifyDataSetChanged()
                checkViewOneTypeContent()
            }

            1 -> {
                binding.title.barTvOther.text = "发布"
                longpostadapter.needGone = true
                longpostadapter.notifyDataSetChanged()
                binding.longpostrec.scrollToPosition(0)
                binding.bottom.emojirec.visibility = View.GONE
                binding.bottom.clEmojiHead.visibility = View.GONE
                headBinding.tFl.visibility = View.VISIBLE
                headBinding.tvFmTips2.visibility = View.VISIBLE
                headBinding.icAttribute.root.visibility = View.VISIBLE
                binding.bottom.root.visibility = View.GONE
                headBinding.etBiaoti.visibility = View.GONE
                headBinding.tvline.visibility = View.GONE
                binding.longpostrec.isNestedScrollingEnabled = false
                binding.longpostrec.isVerticalScrollBarEnabled = false
                val linearLayoutManager: LinearLayoutManager =
                    object : LinearLayoutManager(this) {
                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                binding.longpostrec.layoutManager = linearLayoutManager
                checkViewTwoTypeContent()
            }

            else -> {}
        }
    }

    fun showLoctionServicePermission() {
        isunSave = true
        // 没有打开定位服务。
        LocationServiceUtil.openCurrentAppSystemSettingUI(this)
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, PostActivity.REQUEST_LOCATION_SERVICE)
        return

    }
}