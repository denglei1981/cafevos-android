package com.changanford.circle.ui.activity

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
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
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.router.startARouterForResult
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
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
import com.yw.li_model.adapter.EmojiAdapter
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
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
    private var isunSave: Boolean = false
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
        EmojiAdapter(this)
    }
    private var locaPostEntity: PostEntity? = null
    override fun initView() {
        binding.etBiaoti.requestFocus()
        ImmersionBar.with(this).keyboardEnable(true).init()  //顶起页面底部
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "发帖"
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.text = "发布"
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.white))
        binding.title.barTvOther.textSize = 12f
        binding.title.barTvOther.background = resources.getDrawable(R.drawable.post_btn_bg)
        "actionbarheight--${ImmersionBar.getActionBarHeight(this)}".logD()
        "NavigationBarHeight--${ImmersionBar.getNavigationBarHeight(this)}".logD()
        "ScreenHeight--${ScreenUtils.getScreenHeight(this)}".logD()
        var bthinttxt = "标题 (6-20字之间)"
        var spannableString = SpannableString(bthinttxt)
        var intstart = bthinttxt.indexOf('(')
        var intend = bthinttxt.length
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
    }

    override fun observe() {
        super.observe()
        ImmersionBar.with(this).setOnKeyboardListener { isPopup, keyboardHeight ->
            Log.d("ImmersionBar", keyboardHeight.toString())
//            if (isPopup) {
//                iskeybarOpen = true
            binding.bottom.emojirec.visibility = View.GONE
//            } else {
//                iskeybarOpen = false
//            }
        }
//        LiveDataBus.get().with(LiveDataBusKey.CLEARVIDEOPOSTDATA).observe(this, Observer {
//            selectList.clear()
//            postVideoAdapter.notifyDataSetChanged()
//        })
        viewModel.postsuccess.observe(this, Observer {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            if (locaPostEntity != null) {
                viewModel.deletePost(locaPostEntity!!.postsId)
            }
            "发布成功".toast()
            startARouter(ARouterMyPath.MineFollowUI, true)
            finish()
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
            .observe(this,
                Observer {
                    // 已改角标
                    buttomTypeAdapter.setData(3, ButtomTypeBean(it.name, 1, 2))
                    params["topicId"] = it.topicId.toString()
                })


        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION, PoiInfo::class.java).observe(this,
            {
                address = it.address ?: it.name ?: ""
                params["address"] = address
                it.location?.let { mit ->
                    params["lat"] = mit.latitude
                    params["lon"] = mit.longitude
                    viewModel.getCityDetailBylngAndlat(it.location.latitude, it.location.longitude)
                }
                params["province"] = it.province ?: address
                // 已改角标
                buttomTypeAdapter.setData(0, ButtomTypeBean(it.name, 1, 4))

            })

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
                    params.remove("lat")
                    params.remove("lon")
                    params.remove("city")
                    params.remove("province")
                    params.remove("cityCode")
                    params.remove("address")
                    address = ""
                    buttomTypeAdapter.setData(0, ButtomTypeBean("不显示位置", 1, 4))
                })

        LiveDataBus.get().with(LiveDataBusKey.CREATE_LOCATION, CreateLocation::class.java)
            .observe(this, Observer {

                address = it.address
                params["address"] = address
                params["lat"] = it.lat
                params["lon"] = it.lon
                viewModel.getCityDetailBylngAndlat(it.lat, it.lon)
                params["province"] = it.province
                buttomTypeAdapter.setData(0, ButtomTypeBean(address, 1, 4))
            })

        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
            postVideoAdapter.fmPath = it.toString()
            postVideoAdapter.notifyDataSetChanged()
        })
        // 过时了。
        viewModel.keywords.observe(this, Observer {
            buttomlabelAdapter.addData(it)
            handleEditPost()
            if (locaPostEntity != null) {
                if (locaPostEntity!!.keywords.isNotEmpty()) {
                    buttomlabelAdapter.data.forEach {
                        it.isselect = it.tagName == locaPostEntity!!.keywords
                    }
                    buttomlabelAdapter.notifyDataSetChanged()
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
        }

        if (isTopPost) {
            params["topicId"] = intent.extras?.getString("topId") ?: "0"
            params["topicName"] = intent.extras?.getString("topName") ?: ""
            (params["topicName"] as String).isNotEmpty().let {
                buttomTypeAdapter.setData(3, ButtomTypeBean(params["topicName"] as String, 1, 2))
            }
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
            }
            if (locaPostEntity!!.circleName.isNotEmpty()) {
                buttomTypeAdapter.setData(
                    4,
                    ButtomTypeBean(locaPostEntity!!.circleName, 1, 3)
                )
            }
            if (locaPostEntity!!.address.isNotEmpty()) {
                buttomTypeAdapter.setData(
                    0,
                    ButtomTypeBean(locaPostEntity!!.address, 1, 4)
                )
            }
            if (locaPostEntity!!.fmpath.isNotEmpty()) {
                postVideoAdapter.fmPath = locaPostEntity!!.fmpath
                postVideoAdapter.notifyDataSetChanged()
            }
            jsonStr2obj(locaPostEntity!!.localMeadle)
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
            buttomlabelAdapter.getItem(position).isselect = !buttomlabelAdapter.getItem(position).isselect
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
        emojiAdapter.setItems(emojiList)
        emojiAdapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
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

        })
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
            biaoti.isNullOrEmpty() || biaoti.length < 6 || biaoti.length > 20 -> {
                "请输入6-20字的帖子标题".toast()
                return
            }
            content.isNullOrEmpty() -> {
                "请输入正文内容".toast()
            }
            platename.isEmpty() -> {
                "请选择模块".toast()
            }
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
                    } else {
                        binding.bottom.emojirec.visibility = View.VISIBLE
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
            if (view.id == R.id.iv_delete) {
                selectList.remove(postVideoAdapter.getItem(position))
                postVideoAdapter.remove(postVideoAdapter.getItem(position))
                binding.mscr.smoothScrollTo(0, 0);
            }
        }

        postVideoAdapter.setOnItemClickListener { adapter, view, position ->
            val holder = binding.picsrec.findViewHolderForLayoutPosition(position)
            if (holder != null && holder.itemViewType == 0x9843) {//添加
                PictureUtil.ChoseVideo(
                    this,
                    selectList,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
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

                        }

                    })
            } else {
                val array = ArrayList<String>()
                array.add("选择封面")
                if (canEditVideo) {
                    array.add("编辑视频")
                } else {
                    array.add("重选视频")
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
                                        PictureUtil.openGalleryOnePic(this@VideoPostActivity,
                                            object : OnResultCallbackListener<LocalMedia> {
                                                override fun onResult(result: MutableList<LocalMedia>?) {
                                                    val localMedia = result?.get(0)
                                                    var bundle = Bundle()
                                                    var selectList = arrayListOf(localMedia)
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
            startARouter(ARouterCirclePath.ChooseConversationActivity)
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
            val buttomType= buttomTypeAdapter.getItem(position).itemType
            when(buttomType){
                0,1->{ // 选择板块
                    showPlate()
                }
                4->{ // 选择地址。
                    startARouter(ARouterCirclePath.ChooseLocationActivity)
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
            startARouterForResult(
                this,
                ARouterCirclePath.ChoseCircleActivity,
                PostActivity.REQUEST_CIRCLE
            )
        }

        binding.bottom.ivLoc.setOnClickListener {
            startARouter(ARouterCirclePath.ChooseLocationActivity)
        }

        binding.bottom.tvPic.text = "视频"
        binding.bottom.ivPic.setOnClickListener {
            PictureUtil.ChoseVideo(
                this,
                selectList,
                object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
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

                    }

                })
        }
    }

    private fun uploadFM(stsBean: STSBean, dialog: LoadDialog) {
        var fmpath = postVideoAdapter.fmPath

        if (!fmpath.isNullOrEmpty()) {
            upimgs(stsBean, fmpath, dialog)
        } else {
            //从视频中下载第一帧 返回path
            var videofirstpic = getFristVideoPic()
            upimgs(stsBean, videofirstpic, dialog)
        }
    }

    private fun upimgs(stsBean: STSBean, fmpath: String, dialog: LoadDialog) {
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
                                params["pics"] = path
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
        params["videoUrl"] = path


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
        var file = File(PictureUtil.getFinallyPath(selectList[0]))
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
        buttomlabelAdapter.data.forEach {
            if (it.isselect) {
                tagIds += it.id + ","
            }
        }
        params["tagIds"] = tagIds
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
            override fun callbackTag(cancel: Boolean, tags: MutableList<PostKeywordBean>,totalTags:Int) {
                if (!cancel) {
                    buttomlabelAdapter.setNewInstance(tags)
                }
//                if (!cancel) {
//                    tags.forEach {
//                        it.isselect = true
//                    }
//                    if (tags.size >= totalTags) {//全换了
//                        buttomlabelAdapter.setNewInstance(tags)
//                    }
//                    if (tags.size < totalTags) {
//                        val last = mutableListOf<PostKeywordBean>()
//                        buttomlabelAdapter.data.forEach {
//                            if (!tags.contains(it)) {
//                                it.isselect=false
//                                last.add(it)
//                                if (last.size == totalTags) {
//                                    return
//                                }
//                            }
//                        }
//                        tags.addAll(last)
//                        buttomlabelAdapter.setNewInstance(tags)
//                    }
//                }
            }
        }, postTagDataList!!, buttomlabelAdapter.data)
        circlePostTagDialog?.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
        var postsId = intent?.getStringExtra("postsId")
        postsId?.let {
            canEditVideo = false
            //监听下载的图片
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
            viewModel.postDetailsBean.observe(this, {
                it?.let { locaPostEntity ->
                    if (locaPostEntity != null) {//同草稿逻辑
                        viewModel.downGlideImgs(listOf(ImageList(locaPostEntity.pics)))

                        binding.etBiaoti.setText(locaPostEntity!!.title)
                        binding.etContent.setText(locaPostEntity!!.content)
                        params["plate"] = locaPostEntity!!.plate
                        params["topicId"] = locaPostEntity!!.topicId
                        params["postsId"] = locaPostEntity!!.postsId
                        params["type"] = locaPostEntity!!.type
                        params["keywords"] = locaPostEntity!!.keywords
                        params["circleId"] = locaPostEntity!!.circleId
                        params["content"] = locaPostEntity!!.content ?: ""
                        params["actionCode"] = locaPostEntity!!.actionCode
                        params["title"] = locaPostEntity!!.title
                        params["address"] = locaPostEntity!!.address
                        params["lat"] = locaPostEntity!!.lat
                        params["lon"] = locaPostEntity!!.lon
                        params["province"] = locaPostEntity!!.province
                        params["cityCode"] = locaPostEntity!!.cityCode
                        params["city"] = locaPostEntity!!.city

                        if (params["plate"] != 0) {
                            buttomTypeAdapter.setData(1, ButtomTypeBean("", 0, 0))
                            buttomTypeAdapter.setData(
                                2,
                                ButtomTypeBean(locaPostEntity!!.plateName, 1, 1)
                            )
                        }
                        if (locaPostEntity!!.topicName?.isNotEmpty() == true) buttomTypeAdapter.setData(
                            3,
                            ButtomTypeBean(locaPostEntity!!.topicName ?: "", 1, 2)
                        )
                        if (locaPostEntity!!.circleName?.isNotEmpty() == true) buttomTypeAdapter.setData(
                            4,
                            ButtomTypeBean(locaPostEntity!!.circleName ?: "", 1, 3)
                        )
                        if (locaPostEntity!!.address?.isNotEmpty() == true) buttomTypeAdapter.setData(
                            0,
                            ButtomTypeBean(locaPostEntity!!.address, 1, 4)
                        )
//                        jsonStr2obj(locaPostEntity!!.localMeadle)
                        //选择的标签
                        if (locaPostEntity!!.keywords?.isNotEmpty() == true) {
                            buttomlabelAdapter.data.forEach {
                                it.isselect = it.tagName == locaPostEntity!!.keywords
                            }
                            buttomlabelAdapter.notifyDataSetChanged()
                        }
                    }
                }
            })
            viewModel.getPostById(it)
        }
    }

    private fun cxsp() {
        PictureUtil.ChoseVideo(
            this,
            selectList.apply { clear() },
            object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: MutableList<LocalMedia>?) {
                    if (result != null) {
                        selectList.clear()
                        selectList.addAll(result)
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

                }

            })
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun back() {

        var postsId = intent?.getStringExtra("postsId")
        if (!isSave()) {
            finish()
        } else {
            if (!postsId.isNullOrEmpty()) {
                finish()
                return
            }
            var postEntity =
                if (locaPostEntity != null) locaPostEntity!! else PostEntity()
            if (postEntity.postsId == 0L) {
                postEntity.postsId = insertPostId
            }
            ShowSavePostPop(this, object : ShowSavePostPop.PostBackListener {

                override fun save() {

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
                    postEntity.fmpath =
                        if (selectList.size > 0) PictureUtil.getFinallyPath(selectList[0]) else ""
                    postEntity.type = "3"  //视频帖子类型
                    postEntity.title = binding.etBiaoti.text.toString()
                    postEntity.address =
                        if (params["address"] != null) params["address"] as String else ""
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
                    finish()
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

    fun isSave():Boolean{
        if (binding.etBiaoti.text.toString().isNotEmpty()){
            return true
        }else if(binding.etContent.text.toString().isNotEmpty()){
            return true
        }else if (selectList.size>0){
            return true
        }else if(buttomTypeAdapter.getItem(0).content.isNotEmpty()
            ||buttomTypeAdapter.getItem(2).content.isNotEmpty()
            ||buttomTypeAdapter.getItem(3).content.isNotEmpty()
            ||buttomTypeAdapter.getItem(4).content.isNotEmpty()){
            return true
        }
        buttomlabelAdapter.data.forEach {
            if (it.isselect){
                return true
            }
        }
        return false
    }

    fun ondesSave() {

        var postsId = intent?.getStringExtra("postsId")
        if (!postsId.isNullOrEmpty()) {
            return
        }
        if (isSave()) {
            var postEntity =
                if (locaPostEntity != null) locaPostEntity!! else PostEntity()
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
            postEntity.fmpath =
                if (selectList.size > 0) PictureUtil.getFinallyPath(selectList[0]) else ""
            postEntity.type = "3"  //视频帖子类型
            postEntity.title = binding.etBiaoti.text.toString()
            postEntity.address =
                if (params["address"] != null) params["address"] as String else ""
            postEntity.lat = if (params["lat"] != null) params["lat"] as Double else 0.0
            postEntity.lon = if (params["lon"] != null) params["lon"] as Double else 0.0
            postEntity.city =
                if (params["city"] != null) params["city"] as String else ""
            postEntity.province =
                if (params["province"] != null) params["province"] as String else ""
            postEntity.cityCode =
                if (params["cityCode"] != null) params["cityCode"] as String else ""
            postEntity.creattime = System.currentTimeMillis().toString()

            // 保存tags
            saveCgTags(postEntity)
            viewModel.insertPostentity(postEntity)

        }

    }

    fun saveCgTags(postEntity:PostEntity){
        // 保存tags
        val data = buttomlabelAdapter.data
        val gson = Gson()
        val toJsonTags = gson.toJson(data)
        postEntity.tags = toJsonTags
    }

    fun showPlate(){
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
}