package com.changanford.circle.ui.activity.baoming

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.baidu.mapapi.search.core.PoiInfo
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.adapter.ButtomlabelAdapter
import com.changanford.circle.adapter.LongPostV2Adapter
import com.changanford.circle.bean.*
import com.changanford.circle.bean.H5PostTypeBean
import com.changanford.circle.bean.PostKeywordBean
import com.changanford.circle.databinding.ActivityFabudeitalBinding
import com.changanford.circle.ui.activity.PostActivity
import com.changanford.circle.ui.release.MMapActivity
import com.changanford.circle.ui.release.ReleaseActivity
import com.changanford.circle.ui.release.widget.ActivityTypeDialog
import com.changanford.circle.viewmodel.PostViewModule
import com.changanford.circle.widget.dialog.CirclePostTagDialog
import com.changanford.circle.widget.pop.ShowSavePostPop
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.bean.*
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.router.startARouterForResult
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.*
import com.changanford.common.widget.HomeBottomDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.DoubleUtils
import com.luck.picture.lib.tools.ToastUtils
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import com.scwang.smart.refresh.layout.util.SmartUtil
import com.yw.li_model.adapter.EmojiAdapter
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig
import java.util.*


/**
 * 发布报名活动第二步
 */
@Route(path = ARouterCirclePath.ActivityFabuStep2)
class AvtivityFabuStep2 : BaseActivity<ActivityFabudeitalBinding, PostViewModule>() {

    private val longpostadapter by lazy {
        LongPostV2Adapter(binding.longpostrec.layoutManager as LinearLayoutManager)
    }

    private var baoMingViewModel: BaoMingViewModel? = null

    private lateinit var plateBean: PlateBean
    private var platename: String = ""
    private var circlename: String = ""
    private var address: String = ""
    private val upedimgs = ArrayList<DtoBeanNew.ContentImg>()  //上传之后的图片集合地址
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

    private val buttomlabelAdapter by lazy {
        ButtomlabelAdapter()
    }
    private val emojiAdapter by lazy {
        EmojiAdapter(this)
    }

    companion object {
        const val ITEM_SELECTPIC = 0x5564
    }

    override fun initView() {
        ImmersionBar.with(this)
            .keyboardEnable(true)
            .init()  //顶起页面底部
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "发布报名活动"
        locaPostEntity = intent.getSerializableExtra("postEntity") as PostEntity?
        bus()
        isH5Post = intent.extras?.getBoolean("isH5Post") ?: false
        isCirclePost = intent.extras?.getBoolean("isCirclePost") ?: false
        isTopPost = intent.extras?.getBoolean("isTopPost") ?: false
        baoMingViewModel = createViewModel(BaoMingViewModel::class.java)
        ActivityFabuBaoming.dto.coverImgUrl?.toast()
    }

    override fun observe() {
        super.observe()
        ImmersionBar.with(this).setOnKeyboardListener { isPopup, keyboardHeight ->
            Log.d("ImmersionBar", keyboardHeight.toString())
            binding.ivPic.isVisible = isPopup

        }
        LiveDataBus.get().with(LiveDataBusKey.LONGPOSTFM).observe(this, Observer {
            isunSave = false
            FMMeadia = it as LocalMedia
        })
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
        viewModel.stsBean.observe(this, Observer {
            it?.let {
                upedimgs.clear()
                selectList.addAll(longpostadapter.data)
                var mediacount = 0
                selectList.forEach {
                    if (it.localMedias != null) {
                        mediacount++
                    }
                }
                uploadImgs(it, 0, dialog, mediacount, 0)
            }
        })
        viewModel.cityCode.observe(this, Observer {
            params["cityCode"] = it.cityCode ?: ""
            params["city"] = it.cityName
        })
        LiveDataBus.get().with(LiveDataBusKey.Conversation, HotPicItemBean::class.java)
            .observe(this,
                Observer {
                    isunSave = false
                    params["topicId"] = it.topicId.toString()
                })


        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION, PoiInfo::class.java).observe(this,
            {
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
            })

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
            })
        viewModel.plateBean.observe(this, Observer {
            plateBean = it
            plateBean.plate.forEach {
                if (it.name == "社区") {
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
//                    binding.tvLocation.text = "不显示位置"
                })

        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
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
            showErrorWarn()
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
        val default = LongPostBean(hintStr = "请输入正文内容，字数小于1000")
        longpostadapter.addData(default)
    }

    override fun initData() {
        initandonclickhead()
        viewModel.getPlate()
        viewModel.getTags() //标签
        val layoutManager = LinearLayoutManager(this)
        binding.longpostrec.layoutManager = layoutManager
        longpostadapter.draggableModule.isDragEnabled = true
        binding.longpostrec.adapter = longpostadapter
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
            }
        }
        if (isTopPost) {
            params["topicId"] = intent.extras?.getString("topId") ?: "0"
            params["topicName"] = intent.extras?.getString("topName") ?: ""
            (params["topicName"] as String).isNotEmpty().let {
            }
        }
    }

    private fun initlocaData() {
        if (locaPostEntity != null) {
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
            }
            if (locaPostEntity!!.topicName.isNotEmpty()) {
            }
            if (locaPostEntity!!.circleName.isNotEmpty()) {
            }
            showLocaPostCity()
            if (locaPostEntity!!.longpostFmLocalMeadle.isNotEmpty()) {
                try {
                    FMMeadia =
                        JSON.parseObject(
                            locaPostEntity!!.longpostFmLocalMeadle,
                            LocalMedia::class.java
                        )
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
            if (lp.city.isEmpty()) {
                showCity = "定位"
            }
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

        buttomlabelAdapter.setOnItemClickListener { adapter, view, position ->
            buttomlabelAdapter.getItem(position).isselect =
                !buttomlabelAdapter.getItem(position).isselect
            buttomlabelAdapter.notifyDataSetChanged()
        }

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
                setEditContent(emoji)
            }
        })
    }

    private fun setEditContent(emoJi: String?) {
        val index = editText?.selectionStart
        val editContent = editText?.text
        index?.let { editContent?.insert(it, emoJi) }
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
            savePostDialog()

        }
        binding.bottom.timelayout.setOnClickListener {
            setTimePicker()
        }
        binding.bottom.leixinglayout.setOnClickListener {
            if (!DoubleUtils.isFastDoubleClick()) {
                ActivityTypeDialog(this) { integer: Int ->
                    actType = integer.toString() + ""
                    ActivityFabuBaoming.dto.wonderfulType = actType
                    if (integer == 0) {
                        binding.bottom.leixing.text = "线下活动"
                    } else {
                        binding.bottom.leixing.text = "线上活动"
                    }
                    null
                }.setDefault(Integer.valueOf(actType)).show()
            }
        }
        binding.bottom.placelayout.setOnClickListener {
            StartBaduMap()
        }
        binding.bottom.nickSave.setOnClickListener {
            viewModel.getOSS()

        }
        binding.ivPic.setOnClickListener {
            isunSave = true
            val meadiaList: ArrayList<LocalMedia> = arrayListOf()
            PictureUtil.openGallery(this, meadiaList,
                object : OnResultCallbackListener<LocalMedia> {
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

                })
        }



        longpostadapter.setOnItemChildClickListener(object : OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ) {
                if (view.id == R.id.iv_delete) {
                    longpostadapter.remove(position)
                } else if (view.id == R.id.iv_pic) {
                    isunSave = true
                    PictureUtil.openGalleryOnePic(this@AvtivityFabuStep2,
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
                                        this@AvtivityFabuStep2,
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
                } else if (view.id == R.id.ivFm) {
                    val array = ArrayList<String>()
                    array.add("编辑图片")
                    array.add("删除图片")
                    HomeBottomDialog(this@AvtivityFabuStep2, *array.toTypedArray())
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
                                            this@AvtivityFabuStep2,
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
        })

        longpostadapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

                "drag start".logD()
                val holder = viewHolder as BaseViewHolder
                // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.WHITE
                val endColor = Color.rgb(245, 245, 245)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
                holder.itemView.alpha = 0.7f
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
                holder.itemView.alpha = 1f
                longpostadapter.notifyDataSetChanged()
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
        when {
            FMMeadia == null -> {
                "请选择封面".toast()
                return
            }
            platename.isEmpty() -> {
                "请选择模块".toast()
            }
            else -> {
                //埋点
                viewModel.getOSS()
            }
        }
    }

    private fun initandonclickhead() {
        val bthinttxt = "标题 (1-20字之间)"
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
            val media = selectList[index].localMedias
            ytPath = PictureUtil.getFinallyPath(media!!)
            Log.d("=============", "${ytPath}")
            val type = ytPath.substring(ytPath.lastIndexOf(".") + 1, ytPath.length)
            val exifInterface = ExifInterface(ytPath);
            val rotation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            );
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
                upedimgs.add(
                    DtoBeanNew.ContentImg(
                        "",
                        longpostadapter.getItem(index - 1).content!!
                    )
                )
                addPost()
                return
            } else {
                if (longpostadapter.getItem(index - 1).content?.isNotEmpty() == true) {
                    upedimgs.add(
                        DtoBeanNew.ContentImg(
                            "",
                            longpostadapter.getItem(index - 1).content!!
                        )
                    )
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
                    upedimgs.add(
                        DtoBeanNew.ContentImg(
                            path,
                            longpostadapter.getItem(index - 1).content!!
                        )
                    )
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
            upedimgs.filter {
                !TextUtils.isEmpty(
                    it.contentDesc
                ) || !TextUtils.isEmpty(it.contentImgUrl)
            }
        params["imgUrl"] = last
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isunSave = false
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PostActivity.REQUEST_CIRCLE -> {
                    if (data != null) {
                        params["circleId"] = data.getIntExtra("circleId", 0)
                        circlename = data.getStringExtra("name").toString()
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
                }
                ReleaseActivity.ADDRESSBACK -> {
                    val poiInfo =
                        data!!.getBundleExtra("mbundaddress")!!.getParcelable<MapReturnBean>("poi")
                    if (poiInfo != null && poiInfo.poiInfo != null) {
                        binding.bottom.place.text = poiInfo.poiInfo.getAddress()
                        ActivityFabuBaoming.dto?.apply {
                            latitude = poiInfo.poiInfo.getLocation().latitude.toString() + ""
                            longitude = poiInfo.poiInfo.getLocation().longitude.toString() + ""
                            townName = poiInfo.poiInfo.area
                            provinceName = poiInfo.poiInfo.province
                            cityName = poiInfo.cityName
                            townId = poiInfo.qid
                            provinceId = poiInfo.sid
                            cityId = poiInfo.cid
                            activityAddr = poiInfo.poiInfo.getAddress()
                        }
//                        dtoBean.setActivityAddr(poiInfo.poiInfo.getAddress())
//                        dtoBean.setCityId(poiInfo.cid)
//                        dtoBean.setProvinceId(poiInfo.sid)
//                        dtoBean.setTownId(poiInfo.qid)
//                        dtoBean.setCityName(poiInfo.cityName)
//                        dtoBean.setProvinceName(poiInfo.poiInfo.province)
//                        dtoBean.setTownName(poiInfo.poiInfo.area)
//                        dtoBean.setLongitude(poiInfo.poiInfo.getLocation().longitude.toString() + "")
//                        dtoBean.setLatitude(poiInfo.poiInfo.getLocation().latitude.toString() + "")
                    }
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
            viewModel.postDetailsBean.observe(this, {
                it?.let { locaPostEntity ->
                    if (locaPostEntity != null) {//同草稿逻辑
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
                        params["province"] = locaPostEntity.province
                        params["cityCode"] = locaPostEntity.cityCode
                        params["city"] = locaPostEntity.city
                        if (params["plate"] != 0) {
                        }
                        if (locaPostEntity.topicName?.isNotEmpty() == true) {
                        }
                        if (locaPostEntity.circleName?.isNotEmpty() == true) {
                        }
                        showLocaPostCity()
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
                        viewModel._downloadLocalMedias.observe(this, {
                            //选择的图片重置
                            //封面逻辑
                            FMMeadia = it[0]
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
                                    longpostadapter.notifyDataSetChanged()
                                }
                            }
                        })
                    }
                }
            })
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
//        if (headBinding.etBiaoti.text.toString().isNotEmpty()) {
//            return true
//        } else
//
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
        postEntity.plateName = platename  //模块名称
        postEntity.topicId =
            if (params["topicId"] == null) "" else params["topicId"] as String  //话题ID
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

    /****************************/
    private var pvActTime: TimePickerView? = null
    private var pvActEndTime: TimePickerView? = null
    var timebegin: Date = Date(System.currentTimeMillis())
    var datebegin = ""
    var dateend = ""
    var actType = "0" //活动类型


    fun setTimePicker() {
        initTimePick1()
        initTimePickEND()
        pvActTime?.show()
    }

    /**
     * 选择活动时间
     */
    private fun initTimePick1() {
        //时间选择器
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate[2099, 11] = 31
        //正确设置方式 原因：注意事项有说明
        if (
            pvActTime == null
        ) {
            pvActTime = TimePickerBuilder(
                this
            ) { date, v ->
                datebegin = TimeUtils.MillisToStr1(date.time)
                ActivityFabuBaoming.dto.beginTime = datebegin
                timebegin = date
                pvActEndTime?.show()
            }
                .setCancelText("取消") //取消按钮文字
                .setSubmitText("确定") //确认按钮文字
                .setTitleText("开始时间")
                .setTitleSize(SmartUtil.dp2px(6f)) //标题文字大小
                .setOutSideCancelable(true) //点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true) //是否循环滚动
                .setSubmitColor(resources.getColor(R.color.black)) //确定按钮文字颜色
                .setCancelColor(resources.getColor(R.color.textgray)) //取消按钮文字颜色
                .setTitleBgColor(resources.getColor(R.color.color_withe)) //标题背景颜色 Night mode
                .setBgColor(Color.WHITE) //滚轮背景颜色 Night mode
                .setType(booleanArrayOf(true, true, true, true, true, false))
                .setRangDate(startDate, endDate) //起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "") //默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)
                .build()
        }
    }

    /**
     * 选择活动时间
     */
    private fun initTimePickEND() {
        //时间选择器
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate[2099, 11] = 31
        //正确设置方式 原因：注意事项有说明
        if (pvActEndTime == null) {
            pvActEndTime = TimePickerBuilder(
                this
            ) { date, v ->
                dateend = TimeUtils.MillisToStr1(date.time)
                if (timebegin.time > date.time) {
                    ToastUtils.s(
                        BaseApplication.INSTANT.applicationContext,
                        "结束时间不能小于开始时间"
                    )
                    pvActTime!!.show()
                } else {
                    ActivityFabuBaoming.dto.endTime = dateend
                    binding.bottom.time.text = "$datebegin - $dateend"
                }
            }
                .setCancelText("取消") //取消按钮文字
                .setSubmitText("确定") //确认按钮文字
                .setTitleText("结束时间")
                .setTitleSize(SmartUtil.dp2px(6f)) //标题文字大小
                .setOutSideCancelable(true) //点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true) //是否循环滚动
                .setSubmitColor(resources.getColor(R.color.black)) //确定按钮文字颜色
                .setCancelColor(resources.getColor(R.color.textgray)) //取消按钮文字颜色
                .setTitleBgColor(resources.getColor(R.color.color_withe)) //标题背景颜色 Night mode
                .setBgColor(Color.WHITE) //滚轮背景颜色 Night mode
                .setType(booleanArrayOf(true, true, true, true, true, false))
                .setRangDate(startDate, endDate) //起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "") //默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)
                .build()
        }
    }


    /**
     * 选择地址
     */
    private fun StartBaduMap() {
        SoulPermission.getInstance()
            .checkAndRequestPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
                object : CheckRequestPermissionListener {
                    override fun onPermissionOk(permission: Permission) {
                        startActivityForResult(
                            Intent(
                                this@AvtivityFabuStep2,
                                MMapActivity::class.java
                            ), ReleaseActivity.ADDRESSBACK
                        )
                    }

                    override fun onPermissionDenied(permission: Permission) {
                        AlertDialog(this@AvtivityFabuStep2).builder()
                            .setTitle("提示")
                            .setMsg("您已禁止了定位权限，请到设置中心去打开")
                            .setNegativeButton("取消") { }.setPositiveButton(
                                "确定"
                            ) { SoulPermission.getInstance().goPermissionSettings() }.show()
                    }
                })
    }
}