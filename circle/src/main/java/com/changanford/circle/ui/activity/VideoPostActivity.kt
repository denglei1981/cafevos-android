package com.changanford.circle.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.provider.Settings
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.baidu.mapapi.search.core.PoiInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.changanford.circle.R
import com.changanford.circle.adapter.ButtomTypeAdapter
import com.changanford.circle.adapter.ButtomlabelAdapter
import com.changanford.circle.adapter.PostVideoAdapter
import com.changanford.circle.bean.*
import com.changanford.circle.databinding.VideoPostBinding
import com.changanford.circle.viewmodel.PostViewModule
import com.changanford.circle.widget.dialog.CirclePostTagDialog
import com.changanford.circle.widget.pop.ShowSavePostPop
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
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
import com.changanford.common.ui.videoedit.ExtractVideoInfoUtil
import com.changanford.common.util.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.image.ImageCompress
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.common.widget.HomeBottomDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.ScreenUtils
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener
import com.yalantis.ucrop.UCrop
import com.changanford.circle.adapter.EmojiAdapter
import com.changanford.circle.databinding.HeaderEmojiBinding
import com.jakewharton.rxbinding4.view.systemUiVisibilityChanges
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig
import java.io.File
import java.util.*
import kotlin.concurrent.schedule


/**
 * 视频帖子
 */
@Route(path = ARouterCirclePath.VideoPostActivity)
class VideoPostActivity : BaseActivity<VideoPostBinding, PostViewModule>() {
    val postVideoAdapter by lazy {
        PostVideoAdapter()
    }
    private var platename: String = ""
    private var circlename: String = ""
    private var address: String = ""
    private var selectList = ArrayList<LocalMedia>()
    private var tempList = ArrayList<LocalMedia>() //临时的

    private var nomalwith = 500;
    private var nomalhight = 500;
    private lateinit var plateBean: PlateBean
    private var iskeybarOpen = false

    private var isTopPost = false
    private var isCirclePost: Boolean = false
    private var isH5Post: Boolean = false
    private var postType: Int = 0
    private var h5postbean: H5PostTypeBean? = null
    private lateinit var jsonStr: String
    private lateinit var SelectlocalMedia: LocalMedia
    private var isunSave: Boolean = false  // 不自动保存 默认FALSE

    private var isHasVideoPath = false;//  有网络的图片url 路径

    private val insertPostId by lazy {
        System.currentTimeMillis()
    }
    private val dialog by lazy {
        LoadDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setLoadingText("视频上传中..")
            show()
        }
    }
    private var params = hashMapOf<String, Any>()
    private val upedimgs = ArrayList<ImageUrlBean>()  //上传之后的图片集合地址-视频帖子只有封面

    private val buttomTypeAdapter by lazy {
        ButtomTypeAdapter()
    }
    private val buttomlabelAdapter by lazy {
        ButtomlabelAdapter()
    }
    private val emojiAdapter by lazy {
        EmojiAdapter()
    }
    private var locaPostEntity: PostEntity? = null
    override fun initView() {
        ImmersionBar.with(this).keyboardEnable(true)
            .setOnKeyboardListener { isPopup, keyboardHeight ->
                if (isPopup) {
                    binding.bottom.llContent.visibility = View.VISIBLE
                    binding.bottom.emojirec.visibility = View.GONE
                    binding.bottom.clEmojiHead.visibility=View.GONE
                } else {
                    binding.bottom.llContent.visibility = View.GONE
                }
            }.init()  //顶起页面底部
        binding.icAttribute.vLineOne.visibility = View.GONE
        title = "发帖页"
        binding.etBiaoti.requestFocus()
        ImmersionBar.with(this).keyboardEnable(true).init()  //顶起页面底部
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "发帖"
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.text = "发布"
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.white))
        binding.title.barTvOther.textSize = 12f
        binding.title.barTvOther.background = ContextCompat.getDrawable(this, R.drawable.post_btn_no_bg)
        "actionbarheight--${ImmersionBar.getActionBarHeight(this)}".logD()
        "NavigationBarHeight--${ImmersionBar.getNavigationBarHeight(this)}".logD()
        "ScreenHeight--${ScreenUtils.getScreenHeight(this)}".logD()
        val bthinttxt = "标题 (2-30字)"
        val spannableString = SpannableString(bthinttxt)
        val intstart = bthinttxt.indexOf('(')
        val intend = bthinttxt.length
        spannableString.setSpan(
            AbsoluteSizeSpan(60),
            0,
            intstart,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(40),
            intstart,
            intend,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        binding.etBiaoti.hint = spannableString
        locaPostEntity = intent.getSerializableExtra("postEntity") as PostEntity?

        isH5Post = intent.extras?.getBoolean("isH5Post") ?: false
        isCirclePost = intent.extras?.getBoolean("isCirclePost") ?: false
        isTopPost = intent.extras?.getBoolean("isTopPost") ?: false

        binding.bottom.tvMore.setOnClickListener {
            showMoreTag()
        }
        binding.bottom.llContent.visibility = View.GONE
        initListener()
    }

    private fun checkNext(){
        val hasTitle = binding.etBiaoti.text!!.length >= 2
        if (hasTitle ) {
            binding.title.barTvOther.isEnabled = true
            binding.title.barTvOther.background =
                ContextCompat.getDrawable(this, R.drawable.post_btn_bg)
        } else {
            binding.title.barTvOther.isEnabled = false
            binding.title.barTvOther.background =
                ContextCompat.getDrawable(this, R.drawable.post_btn_no_bg)
        }
    }

    private fun initListener(){
        binding.etBiaoti.addTextChangedListener {
            checkNext()
            it?.let { editable ->
                if (editable.length >= 2) {
                    binding.tvNoTips.visibility = View.GONE
                }
            }
        }
        binding.bottom.ivDown.setOnClickListener {
            binding.bottom.emojirec.visibility = View.GONE
            binding.bottom.clEmojiHead.visibility=View.GONE
        }
        binding.etContent.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.etBiaoti.text?.let { editable ->
                    if (editable.length < 2) {
                        binding.tvNoTips.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun observe() {
        super.observe()
//        ImmersionBar.with(this).setOnKeyboardListener { isPopup, keyboardHeight ->
//            Log.d("ImmersionBar", keyboardHeight.toString())
////            if (isPopup) {
////                iskeybarOpen = true
//            binding.bottom.emojirec.visibility = View.GONE
////            } else {
////                iskeybarOpen = false
////            }
//        }
//        LiveDataBus.get().with(LiveDataBusKey.CLEARVIDEOPOSTDATA).observe(this, Observer {
//            selectList.clear()
//            postVideoAdapter.notifyDataSetChanged()
//        })
        viewModel.isEnablePost.observe(this) {
            binding.title.barTvOther.isEnabled = it
        }
        viewModel.postsuccess.observe(this, Observer {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            if (locaPostEntity != null) {
                viewModel.deletePost(locaPostEntity!!.postsId)
            }
            isunSave = true
            "发布成功".toast()
            startARouter(ARouterMyPath.MineFollowUI, true)
            finish()
        })
        viewModel.postError.observe(this, Observer {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
//            showErrorWarn()
        })


        viewModel.stsBean.observe(this, Observer {
            it?.let {
                upedimgs.clear()
                uploadFM(it, dialog)
            }
        })
        viewModel.cityCode.observe(this, Observer {
            params["cityCode"] = it.cityCode ?: ""
            params["city"] = it.cityName
        })
        LiveDataBus.get().with(LiveDataBusKey.Conversation, HotPicItemBean::class.java)
            .observe(
                this
            ) {
                // 已改角标
                isunSave = false
                buttomTypeAdapter.setData(3, ButtomTypeBean(it.name, 1, 2))
                params["topicId"] = it.topicId.toString()
                showTopic(it.name)
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
            // 已改角标
            val showCity = it.city.plus("·").plus(it.name)
            buttomTypeAdapter.setData(0, ButtomTypeBean(showCity, 1, 4))
            showAddress(address)
        }

        viewModel.plateBean.observe(this, Observer {
            plateBean = it
            plateBean.plate.forEach {
                if (it.name == "社区") {
                    // 已改角标
                    buttomTypeAdapter?.setData(1, ButtomTypeBean("", 0, 0))
                    buttomTypeAdapter?.setData(2, ButtomTypeBean(it.name, 1, 1))
                    platename = it.name
                    params["plate"] = it.plate
                    params["actionCode"] = it.actionCode
                }
            }
        })
        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATIONNOTHING, String::class.java)
            .observe(this,
                {
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
                })

        LiveDataBus.get().with(LiveDataBusKey.CREATE_LOCATION, CreateLocation::class.java)
            .observe(this, Observer {
                isunSave = false
                address = it.address
                params["address"] = address
                params["lat"] = it.lat
                params["lon"] = it.lon
                viewModel.getCityDetailBylngAndlat(it.lat, it.lon)
                params["province"] = it.province
                val showCity = it.city.plus("·").plus(it.addrName)
                buttomTypeAdapter.setData(0, ButtomTypeBean(showCity, 1, 4))
            })

        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
            isunSave = false
            postVideoAdapter.fmPath = it.toString()
            postVideoAdapter.notifyDataSetChanged()
        })
        // 过时了。
//        viewModel.keywords.observe(this, Observer {
//            buttomlabelAdapter.addData(it)
//            handleEditPost()
//            if (locaPostEntity != null) {
//                if (locaPostEntity!!.keywords.isNotEmpty()) {
//                    buttomlabelAdapter.data.forEach {
//                        it.isselect = it.tagName == locaPostEntity!!.keywords
//                    }
//                    buttomlabelAdapter.notifyDataSetChanged()
//                }
//            }
//        })
        viewModel.tagsList.observe(this, Observer { ptList ->

            handleEditPost()
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
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        initTags()
                    }
                }
            } else {
                initTags()
            }
        })


        viewModel._downloadLocalMedias.observe(this, {
            //选择的图片重置
            selectList.clear()
            selectList.addAll(it)
            //添加封面
            if (it.size != 0) {
                postVideoAdapter.fmPath = it[0].realPath
                postVideoAdapter.notifyDataSetChanged()
            }
            //展示选择的图片
            postVideoAdapter.setList(it)
            postVideoAdapter.notifyDataSetChanged()
        })


        //赋值
        viewModel.postDetailsBean.observe(this) {
            it?.let { locaPostEntity ->
                if (locaPostEntity != null) {//同草稿逻辑
                    viewModel.downGlideImgs(listOf(ImageList(locaPostEntity.pics)))

                    binding.etBiaoti.setText(locaPostEntity!!.title)
                    binding.etContent.setText(locaPostEntity!!.content)
                    params["plate"] = locaPostEntity!!.plate
                    params["topicId"] = locaPostEntity!!.topicId
                    params["postsId"] = locaPostEntity!!.postsId
                    params["type"] = locaPostEntity.type
                    locaPostEntity.keywords?.let { k ->
                        params["keywords"] = k
                    }
                    params["circleId"] = locaPostEntity!!.circleId
                    params["content"] = locaPostEntity!!.content ?: ""
                    params["actionCode"] = locaPostEntity!!.actionCode
                    params["title"] = locaPostEntity!!.title
                    locaPostEntity.address?.let { ad ->
                        params["address"] = ad
                    }
                    if (TextUtils.isEmpty(locaPostEntity.address)) {
                        params["address"] = ""
                    }
                    params["lat"] = locaPostEntity.lat
                    params["lon"] = locaPostEntity.lon
                    params["province"] = locaPostEntity.province
                    params["cityCode"] = locaPostEntity.cityCode
                    params["city"] = locaPostEntity.city
                    isHasVideoPath = true
                    params["videoUrl"] = locaPostEntity.videoUrl

                    if (params["plate"] != 0) {
                        buttomTypeAdapter.setData(1, ButtomTypeBean("", 0, 0))
                        buttomTypeAdapter.setData(
                            2,
                            ButtomTypeBean(locaPostEntity.plateName, 1, 1)
                        )
                    }
                    if (locaPostEntity.topicName?.isNotEmpty() == true) buttomTypeAdapter.setData(
                        3,
                        ButtomTypeBean(locaPostEntity.topicName ?: "", 1, 2)
                    )
                    if (locaPostEntity.circleName?.isNotEmpty() == true) buttomTypeAdapter.setData(
                        4,
                        ButtomTypeBean(locaPostEntity.circleName ?: "", 1, 3)
                    )
                    showLocaPostCity()
//                        jsonStr2obj(locaPostEntity!!.localMeadle)
                    //选择的标签
                    if (TextUtils.isEmpty(locaPostEntity.keywords)) {
                        buttomlabelAdapter.data.forEach {
                            it.isselect = it.tagName == locaPostEntity!!.keywords
                        }
                        buttomlabelAdapter.notifyDataSetChanged()
                    }
                }
            }
        }


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

            )
            .show()
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

    override fun initData() {
        onclick()
        viewModel.getPlate()
        viewModel.getTags() //标签
        initbuttom()
        val manager = FullyGridLayoutManager(
            this,
            4, GridLayoutManager.VERTICAL, false
        )
        binding.picsrec.layoutManager = manager
//        postVideoAdapter.draggableModule.isDragEnabled = true
        binding.picsrec.adapter = postVideoAdapter
        postVideoAdapter.setList(selectList)
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
    }

    private fun initlocaData() {
        if (locaPostEntity != null) {

            binding.etBiaoti.setText(locaPostEntity!!.title)
            binding.etContent.setText(locaPostEntity!!.content)
            params["plate"] = locaPostEntity!!.plate
            params["topicId"] = locaPostEntity!!.topicId
            params["type"] = locaPostEntity!!.type
            params["keywords"] = locaPostEntity!!.keywords
            params["circleId"] = locaPostEntity!!.circleId
            params["content"] = locaPostEntity!!.content
            params["actionCode"] = locaPostEntity!!.actionCode
            params["title"] = locaPostEntity!!.title
            params["address"] = locaPostEntity!!.address
            params["lat"] = locaPostEntity!!.lat
            params["lon"] = locaPostEntity!!.lon
            params["province"] = locaPostEntity!!.province
            params["cityCode"] = locaPostEntity!!.cityCode
            params["city"] = locaPostEntity!!.city
            params["addrName"] = locaPostEntity!!.addrName
            platename = locaPostEntity!!.plateName
            circlename = locaPostEntity!!.circleName
            if (params["plate"] != 0) {
                // 已改角标
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
            showLocaPostCity()
            if (locaPostEntity!!.fmpath.isNotEmpty()) {
                postVideoAdapter.fmPath = locaPostEntity!!.fmpath
                postVideoAdapter.notifyDataSetChanged()
            }
            jsonStr2obj(locaPostEntity!!.localMeadle)
        } else {
            PictureUtil.ChoseVideo(
                this,
                selectList,
                object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        isHasVideoPath = false
                        if (result != null) {
                            SelectlocalMedia = result[0]
                            startARouterForResult(
                                this@VideoPostActivity,
                                ARouterCirclePath.PictureEditAudioActivity,
                                Bundle().apply {
                                    putString("path", result[0].realPath)
                                },
                                PictureEditAudioActivity.EDIT_VIDEOPATH
                            )
                        }
                    }

                    override fun onCancel() {
                        isunSave = false

                    }

                })
        }
    }

    private fun showTopic(name: String) {
        binding.icAttribute.run {
            tvTopic.visibility = View.GONE
            llTopic.visibility = View.VISIBLE
            tvTopicName.text = name
        }
    }

    private fun showAddress(address: String) {
        binding.icAttribute.run {
            tvAddress.visibility = View.GONE
            llAddress.visibility = View.VISIBLE
            tvAddressName.text = address
        }
    }

    private fun showCircle(circleName: String) {
        if (circleName == "发布到广场") {
            return
        }
        binding.icAttribute.run {
            tvCircle.visibility = View.GONE
            llCircle.visibility = View.VISIBLE
            tvCircleName.text = circleName
        }
    }

    private fun jsonStr2obj(jonson: String) {
        val media = JSON.parseArray(jonson, LocalMedia::class.java);
        selectList.addAll(media)
        postVideoAdapter.setList(media)
        postVideoAdapter.notifyDataSetChanged()
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
                ButtomTypeBean("", 0, 3)
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
        binding.icAttribute.apply {
            rlTopic.setOnClickListener { toHuati() }
            tvDeleteTopic.setOnClickListener {
                params.remove("topicId")
                binding.icAttribute.run {
                    tvTopic.visibility = View.VISIBLE
                    llTopic.visibility = View.GONE
                    tvTopicName.text = ""
                }
            }
            rlCircle.setOnClickListener { toQuanzi() }
            tvDeleteCircle.setOnClickListener {
                params.remove("circleId")

                binding.icAttribute.run {
                    tvCircle.visibility = View.VISIBLE
                    llCircle.visibility = View.GONE
                    tvCircleName.text = ""
                }
            }
            rlAddress.setOnClickListener {
                isunSave = true
                startARouter(ARouterCirclePath.ChooseLocationActivity)
            }
            tvDeleteAddress.setOnClickListener {
                binding.icAttribute.run {

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
        emojiAdapter.setList(emojiList)
        emojiAdapter.setOnItemClickListener { adapter, view, position ->
            val emoji = emojiAdapter.getItem(position)

            val index = if (binding.etBiaoti.hasFocus()) {
                binding.etBiaoti.selectionStart
            } else {
                binding.etContent.selectionStart
            }

            val editContent = if (binding.etBiaoti.hasFocus()) {
                binding.etBiaoti.text
            } else {
                binding.etContent.text
            }
            editContent?.insert(index, emoji)

        }
    }

    private fun getEmojiStringByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    private fun ispost() {
        var biaoti = binding.etBiaoti.text.toString()
        var content = binding.etContent.text.toString()
        when {
            selectList.size == 0 -> {
                "请选择视频".toast()
                return
            }

            biaoti.isEmpty() || biaoti.length > 30 || biaoti.length < 2 -> {
                "请输入2-30字的帖子标题".toast()
                return
            }

//            content.isNullOrEmpty() -> {
//                "请输入正文内容".toast()
//            }
//
//            platename.isEmpty() -> {
//                "请选择模块".toast()
//            }

            else -> {
                params["content"] = content
                params["title"] = biaoti

                viewModel.getOSS()
            }
        }
    }

    private fun onclick() {
        binding.bottom.ivEmoj.setOnClickListener {
//            if (binding.etContent.hasFocus() && iskeybarOpen) {

            HideKeyboardUtil.hideKeyboard(binding.bottom.emojirec.windowToken)

            Timer().schedule(80) {
                binding.bottom.emojirec.post {
                    if (binding.bottom.emojirec.isShown) {
                        binding.bottom.emojirec.visibility = View.GONE
                        binding.bottom.clEmojiHead.visibility=View.GONE
                    } else {
                        binding.bottom.emojirec.visibility = View.VISIBLE
                        binding.bottom.clEmojiHead.visibility=View.VISIBLE
                    }
                }
            }
//            } else if (!iskeybarOpen) {
//                Timer().schedule(80) {
//                    binding.bottom.emojirec.post {
//                        if (binding.bottom.emojirec.isShown) {
//                            binding.bottom.emojirec.visibility = View.GONE
//                        }
//                    }
//                }
//            }

        }

        binding.title.barImgBack.setOnClickListener {
            back()

        }
        postVideoAdapter.setOnItemChildClickListener { adapter, view, position ->
            isunSave = true
            if (view.id == R.id.iv_delete) {
                selectList.remove(postVideoAdapter.getItem(position))
                postVideoAdapter.remove(postVideoAdapter.getItem(position))
//                binding.mscr.smoothScrollTo(0, 0);
            }
        }

        postVideoAdapter.setOnItemClickListener { adapter, view, position ->
            isunSave = true
            val holder = binding.picsrec.findViewHolderForLayoutPosition(position)
            if (holder != null && holder.itemViewType == 0x9843) {//添加
                PictureUtil.ChoseVideo(
                    this,
                    selectList,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            isHasVideoPath = false
                            if (result != null) {
                                SelectlocalMedia = result[0]
                                startARouterForResult(
                                    this@VideoPostActivity,
                                    ARouterCirclePath.PictureEditAudioActivity,
                                    Bundle().apply {
                                        putString("path", result[0].realPath)
                                    },
                                    PictureEditAudioActivity.EDIT_VIDEOPATH
                                )
                            }
                        }

                        override fun onCancel() {
                            isunSave = false

                        }

                    })
            } else {
                val videoList = postVideoAdapter.data


                val array = ArrayList<String>()
                array.add("选择封面")
                if (videoList.size >= 1) {
                    array.add("重选视频")
                }
                if (canEditVideo) {
                    array.add("编辑视频")
                } else {
                    val contains = array.contains("重选视频")
                    if (!contains) {
                        array.add("重选视频")
                    }

                }
//                if (!postVideoAdapter.fmPath.isNullOrEmpty()) {
//                    array.add("删除封面")
//                }
                HomeBottomDialog(this, *array.toTypedArray())
                    .setOnClickItemListener(object :
                        HomeBottomDialog.OnClickItemListener {
                        override fun onClickItem(position: Int, str: String) {

                            when (str) {
                                "选择封面" -> {
                                    if (canEditVideo) {
                                        isunSave = true
                                        startARouterForResult(
                                            this@VideoPostActivity,
                                            ARouterCirclePath.VideoChoseFMActivity,
                                            Bundle().apply {
                                                putString(
                                                    "cutpath",
                                                    PictureUtil.getFinallyPath(selectList[0])
                                                )
                                            }, VideoChoseFMActivity.FM_CALLBACK
                                        )
                                    } else {
                                        isunSave = true
                                        PictureUtil.openGalleryOnePic(this@VideoPostActivity,
                                            object : OnResultCallbackListener<LocalMedia> {
                                                override fun onResult(result: MutableList<LocalMedia>?) {

                                                    val localMedia = result?.get(0)
                                                    val bundle = Bundle()
                                                    val selectList = arrayListOf(localMedia)
                                                    bundle.putParcelableArrayList(
                                                        "picList",
                                                        selectList
                                                    )
                                                    bundle.putInt("position", 0)
                                                    bundle.putInt("showEditType", -1)
                                                    bundle.putBoolean("isVideo", true)
                                                    startARouter(
                                                        ARouterCirclePath.PictureeditlActivity,
                                                        bundle
                                                    )

                                                }

                                                override fun onCancel() {

                                                }

                                            })
                                    }

                                }

                                "重选视频" -> {
                                    cxsp()
                                }

                                "编辑视频" -> {
                                    val permissions = Permissions.build(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    )
                                    SoulPermission.getInstance()
                                        .checkAndRequestPermissions(permissions, object :
                                            CheckRequestPermissionsListener {
                                            override fun onAllPermissionOk(allPermissions: Array<out Permission>?) {
                                                isunSave = true
                                                startARouterForResult(
                                                    this@VideoPostActivity,
                                                    ARouterCirclePath.PictureEditAudioActivity,
                                                    Bundle().apply {
                                                        putString(
                                                            "path",
                                                            selectList[0].realPath
                                                        )
                                                    },
                                                    PictureEditAudioActivity.EDIT_VIDEOPATH
                                                )
                                            }

                                            override fun onPermissionDenied(refusedPermissions: Array<out Permission>?) {
                                            }

                                        })
                                }
//                                "删除封面" -> {
//                                    postVideoAdapter.fmPath = ""
//                                    postVideoAdapter.notifyDataSetChanged()
//                                }
                            }
                        }
                    }).show()
            }
        }


        binding.title.barTvOther.setOnClickListener {
            ispost()
        }
        binding.bottom.ivHuati.setOnClickListener {
            toHuati()
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
//            if (buttomTypeAdapter.getItem(position).itemType == 0 || buttomTypeAdapter.getItem(position).itemType == 1
//            ) {
//                if (::plateBean.isInitialized && plateBean.plate.isNotEmpty()) {
//                    val sList = mutableListOf<String>()
//                    for (bean in plateBean.plate) {
//                        sList.add(bean.name)
//                    }
//                    HomeBottomDialog(this, *sList.toTypedArray()).setOnClickItemListener(object :
//                        HomeBottomDialog.OnClickItemListener {
//                        override fun onClickItem(position: Int, str: String) {
//                            buttomTypeAdapter.setData(1, ButtomTypeBean("", 0, 0))
//                            buttomTypeAdapter.setData(2, ButtomTypeBean(str, 1, 1))
//                            platename = str
//                            params["plate"] = plateBean.plate[position].plate
//                            params["actionCode"] = plateBean.plate[position].actionCode
//                        }
//                    }).show()
//                } else {
//                    viewModel.getPlate()
//                }
//            }
        }

        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.tvEtcount.text = "${binding.etContent.length()}/500"
            }

        })

        binding.bottom.ivQuanzi.setOnClickListener {
            toQuanzi()
        }

        binding.bottom.ivLoc.setOnClickListener {
            isunSave = true
            startARouter(ARouterCirclePath.ChooseLocationActivity)
        }

        binding.bottom.tvPic.text = "视频"
        binding.bottom.ivPic.visibility = View.GONE
        binding.bottom.ivPic.setOnClickListener {


            if (isHasVideoPath) {
                cxsp()
            } else {
                PictureUtil.ChoseVideo(
                    this,
                    selectList,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            if (result != null) {
                                SelectlocalMedia = result[0]
                                isHasVideoPath = false
                                startARouterForResult(
                                    this@VideoPostActivity,
                                    ARouterCirclePath.PictureEditAudioActivity,
                                    Bundle().apply {
                                        putString("path", result[0].realPath)
                                    },
                                    PictureEditAudioActivity.EDIT_VIDEOPATH
                                )
                            }
                        }

                        override fun onCancel() {

                        }

                    })
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

    private fun uploadFM(stsBean: STSBean, dialog: LoadDialog) {
        val fmpath = postVideoAdapter.fmPath

        if (!fmpath.isNullOrEmpty()) {
            upimgs(stsBean, fmpath, dialog)
        } else {
            //从视频中下载第一帧 返回path
            var videofirstpic = getFristVideoPic()
            upimgs(stsBean, videofirstpic, dialog)
        }
    }

    private fun upimgs(stsBean: STSBean, fmUrl: String, dialog: LoadDialog) {
        ImageCompress.compressImage(
            this,
            arrayListOf(fmUrl),
            object : ImageCompress.ImageCompressResult {
                override fun compressSuccess(list: List<File>) {
                    upCompressimgs(stsBean, list[0].absolutePath, dialog)
                }

                override fun compressFailed() {
                    upCompressimgs(stsBean, fmUrl, dialog)
                }

            })
    }

    private fun upCompressimgs(stsBean: STSBean, fmpath: String, dialog: LoadDialog) {

        var type = fmpath?.substring(fmpath.lastIndexOf(".") + 1, fmpath.length)
        Glide.with(this).asBitmap().load(fmpath)
            .skipMemoryCache(true)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    nomalwith = resource.width;
                    nomalhight = resource.height;
                    val path =
                        stsBean.tempFilePath + System.currentTimeMillis() + "androidios${
                            nomalwith
                        }_${
                            nomalhight
                        }." + type
                    AliYunOssUploadOrDownFileConfig.getInstance(this@VideoPostActivity).initOss(
                        stsBean.endpoint, stsBean.accessKeyId,
                        stsBean.accessKeySecret, stsBean.securityToken
                    )
                    AliYunOssUploadOrDownFileConfig.getInstance(this@VideoPostActivity)
                        .uploadFile(stsBean.bucketName, path, fmpath, "", 0)
                    AliYunOssUploadOrDownFileConfig.getInstance(this@VideoPostActivity)
                        .setOnUploadFile(object :
                            AliYunOssUploadOrDownFileConfig.OnUploadFile {
                            override fun onUploadFileSuccess(info: String) {
                                val upLoadPath = if (path.endsWith(".mp4")) {
                                    "$path${"?x-oss-process=video/snapshot,t_0,f_jpg,w_800,h_600,m_fast.jpg"}"
                                } else path
                                params["pics"] = upLoadPath
                                upvideo(stsBean, dialog)
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

            })
    }

    @SuppressLint("SimpleDateFormat")
    private fun upvideo(stsBean: STSBean, dialog: LoadDialog) {
        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )

        val media = selectList[0]
        val ytPath = PictureUtil.getFinallyPath(media)
        Log.d("=============", "${ytPath}")
        var type = ytPath.substring(ytPath.lastIndexOf(".") + 1, ytPath.length)

        val path =
            stsBean.tempFilePath + System.currentTimeMillis() + "androidios${
                if (media.width == 0) {
                    nomalwith
                } else {
                    media.width
                }
            }_${
                if (media.height == 0) {
                    nomalhight
                } else {
                    media.height
                }
            }." + type

        params["type"] = 3
        if (!isHasVideoPath) {
            // TODO 重选视频后， 把isHasVideoPath  置于 FALSE
            val mExtractVideoInfoUtil = ExtractVideoInfoUtil(ytPath)
            val videoTime = mExtractVideoInfoUtil.videoLength.toLong()
            params["videoTime"] = TimeUtils().getVideoTime(videoTime)
            params["videoUrl"] = path
        }
        AliYunOssUploadOrDownFileConfig.getInstance(this)
            .uploadFile(stsBean.bucketName, path, ytPath, "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {
                params["isPublish"] = 2
                JSON.toJSONString(params).logD()
                addPost(dialog)

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

    private fun getFristVideoPic(): String {

        val mmr = MediaMetadataRetriever()
        val file = File(PictureUtil.getFinallyPath(selectList[0]))
        if (file.exists()) {
            mmr.setDataSource(file.absolutePath);//设置数据源为该文件对象指定的绝对路径
            val bitmap = mmr.frameAtTime //获得视频第一帧的Bitmap对象
            var path = MConstant.saveIMGpath
            FileHelper.saveBitmapToFile(bitmap, path)
            return path
        } else {
            return ""
        }
    }


    fun addPost(dialog: LoadDialog) {
        var tagIds = ""
        var tagNames = ""
        buttomlabelAdapter.data.forEach {
            if (it.isselect) {
                tagIds += it.id + ","
                tagNames += it.tagName + ","
            }
        }
        params["tagIds"] = tagIds
        try {
            val biaoti = params["title"]
            val content = params["content"]
            BuriedUtil.instant?.post(biaoti.toString(), content.toString(), tagNames)
        } catch (e: Exception) {
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
        isunSave = true
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {

                    val resultUri = UCrop.getOutput(data!!)
//                    selectList[0].isCut = true
//                    selectList[0].cutPath = resultUri?.path
//                    postVideoAdapter.setList(selectList)
                    postVideoAdapter.fmPath = resultUri?.path
                    postVideoAdapter.notifyDataSetChanged()
                }

                UCrop.RESULT_ERROR -> {
                    val cropError = UCrop.getError(data!!)
                }

                PostActivity.REQUEST_CIRCLE -> {
                    if (data != null) {
                        params["circleId"] = data.getIntExtra("circleId", 0)
                        circlename = data.getStringExtra("name").toString()
                        buttomTypeAdapter.setData(4, ButtomTypeBean(circlename, 1, 3))
                        showCircle(circlename)
                    }
                }

                PictureEditAudioActivity.EDIT_VIDEOPATH -> {

                    canEditVideo = true
                    val videoeditpath = data?.getStringExtra("finalPath")
                    val time = data!!.getLongExtra("time", 0)
                    SelectlocalMedia.apply {
                        isCut = true
                        cutPath = videoeditpath
                        duration = time
                    }
                    selectList.clear()
                    selectList.add(SelectlocalMedia)
                    postVideoAdapter.setList(selectList)
                    postVideoAdapter.notifyDataSetChanged()

                }

                VideoChoseFMActivity.FM_CALLBACK -> {

                }
            }
        }
    }

    var canEditVideo = true
    fun handleEditPost() {
        val postsId = intent?.getStringExtra("postsId")
        postsId?.let {
            canEditVideo = false
            //监听下载的图片
            viewModel.getPostById(it)
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

    private fun cxsp() {
        isunSave = true
        tempList.clear()
        tempList.addAll(selectList)
        PictureUtil.ChoseVideo(
            this,
            selectList.apply { clear() },
            object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: MutableList<LocalMedia>?) {

                    if (result != null) {
                        selectList.clear()
                        selectList.addAll(result)
                        SelectlocalMedia = result[0]
                        isHasVideoPath = false
                    }
                    postVideoAdapter.setList(selectList)
                    startARouterForResult(
                        this@VideoPostActivity,
                        ARouterCirclePath.PictureEditAudioActivity,
                        Bundle().apply {
                            putString("path", selectList[0].realPath)
                        },
                        PictureEditAudioActivity.EDIT_VIDEOPATH
                    )
                }

                override fun onCancel() {
                    // selecltList  还原
                    selectList.clear()
                    selectList.addAll(tempList)
                }
            })
    }

    private fun back() {
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

    override fun onPause() {
        super.onPause()
        if (!isunSave) {
            ondesSave()
        }
    }

    fun isSave(): Boolean {
        if (binding.etBiaoti.text.toString().isNotEmpty()) {
            return true
        } else if (binding.etContent.text.toString().isNotEmpty()) {
            return true
        } else if (selectList.size > 0) {
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

    override fun onBackPressed() {
//        super.onBackPressed()
        back()
    }

    fun saveInsertPostent(isHandleSave: Boolean) {
        val postEntity = if (locaPostEntity != null) locaPostEntity!! else PostEntity()
        if (postEntity.postsId == 0L) {
            postEntity.postsId = insertPostId
        }
        postEntity.content = binding.etContent.text.toString() //内容
        postEntity.circleId =
            if (params["circleId"] == null) "" else params["circleId"].toString()  //选择圈子的id
        postEntity.circleName = circlename  //选择圈子的名称
        postEntity.plate =
            if (params["plate"] == null) 0 else params["plate"] as Int//模块ID
        postEntity.plateName = platename  //模块名称
        postEntity.topicId =
            if (params["topicId"] == null) "" else params["topicId"] as String  //话题ID
        postEntity.topicName = buttomTypeAdapter.getItem(3).content ?: ""  //话题名称
        postEntity.keywords =
            if (params["keywords"] != null) params["keywords"].toString() else ""  //关键字
//                    postEntity.keywordValues = binding.keywordTv.text.toString()
        postEntity.localMeadle = JSON.toJSONString(selectList)
        postEntity.actionCode =
            if (params["actionCode"] != null) params["actionCode"] as String else ""
//        postEntity.fmpath =
//            if (selectList.size > 0) PictureUtil.getFinallyPath(selectList[0]) else ""
        postEntity.type = "3"  //视频帖子类型
        postEntity.title = binding.etBiaoti.text.toString()
        postEntity.address = if (params["address"] != null) params["address"] as String else ""
        postEntity.lat = if (params["lat"] != null) params["lat"] as Double else 0.0
        postEntity.lon = if (params["lon"] != null) params["lon"] as Double else 0.0
        postEntity.city =
            if (params["city"] != null) params["city"] as String else ""
        postEntity.province =
            if (params["province"] != null) params["province"] as String else ""
        postEntity.cityCode =
            if (params["cityCode"] != null) params["cityCode"] as String else ""
        postEntity.creattime = System.currentTimeMillis().toString()
        postEntity.addrName =
            if (params["addrName"] != null) params["addrName"] as String else ""
        // 保存tags
        saveCgTags(postEntity)
        viewModel.insertPostentity(postEntity)
        if (isHandleSave) {
            finish()
        }
    }

    fun saveCgTags(postEntity: PostEntity) {
        // 保存tags
        val data = buttomlabelAdapter.data
        val gson = Gson()
        val toJsonTags = gson.toJson(data)
        postEntity.tags = toJsonTags
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

    fun showLoctionServicePermission() {
        isunSave = true
        // 没有打开定位服务。
        LocationServiceUtil.openCurrentAppSystemSettingUI(this)
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, PostActivity.REQUEST_LOCATION_SERVICE)
        return

    }
}