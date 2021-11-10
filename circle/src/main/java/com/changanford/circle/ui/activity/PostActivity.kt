package com.changanford.circle.ui.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
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
import com.changanford.circle.adapter.PostPicAdapter
import com.changanford.circle.bean.*
import com.changanford.circle.databinding.PostActivityBinding
import com.changanford.circle.viewmodel.PostViewModule
import com.changanford.circle.widget.pop.ShowSavePostPop
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
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
import com.changanford.common.widget.HomeBottomDialog
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.ScreenUtils
import com.yalantis.ucrop.UCrop
import com.yw.li_model.adapter.EmojiAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * 发图片帖子
 */
@Route(path = ARouterCirclePath.PostActivity)
class PostActivity : BaseActivity<PostActivityBinding, PostViewModule>() {
    private var platename: String = ""
    private var circlename: String = ""
    private var address: String = ""
    lateinit var postPicAdapter: PostPicAdapter
    private var selectList = ArrayList<LocalMedia>()
    private var type = 0

    private var params = hashMapOf<String, Any>()
    private lateinit var plateBean: PlateBean
    private var nomalwith = 500;
    private var nomalhight = 500;
    private var isshowemoji = true
    private var iskeybarOpen = false
    private val upedimgs = ArrayList<ImageUrlBean>()  //上传之后的图片集合地址
    private var isTopPost = false
    private var isCirclePost: Boolean = false
    private var isH5Post: Boolean = false
    private var postType: Int = 0
    private var h5postbean: H5PostTypeBean? = null
    private lateinit var jsonStr: String
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
        EmojiAdapter(this)
    }
    private var locaPostEntity: PostEntity? = null

    companion object {
        const val REQUEST_CIRCLE = 0x435
    }

    override fun initView() {
        ImmersionBar.with(this).keyboardEnable(true).init()  //顶起页面底部
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "发帖"
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.text = "发布"
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.white))
        binding.title.barTvOther.textSize = 12f
        binding.title.barTvOther.background = resources.getDrawable(R.drawable.post_btn_bg)
        postPicAdapter = PostPicAdapter(type)
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
        binding.etBiaoti.requestFocus()


    }

    override fun observe() {
        super.observe()
        ImmersionBar.with(this).setOnKeyboardListener { isPopup, keyboardHeight ->
            Log.d("ImmersionBar", keyboardHeight.toString())
//            if (isPopup){
//                iskeybarOpen = true
                binding.bottom.emojirec.visibility = View.GONE
//            } else{
//                iskeybarOpen= false
//            }
        }
        viewModel.postsuccess.observe(this, Observer {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            if (locaPostEntity!=null){
                viewModel.deletePost(locaPostEntity!!.postsId)
            }
            "发布成功".toast()
            startARouter(ARouterMyPath.MineFollowUI, true)
            finish()
        })
        viewModel.stsBean.observe(this, Observer {
            it?.let {
                upedimgs.clear()
                uploadImgs(it, 0, dialog)
            }
        })
        viewModel.cityCode.observe(this, Observer {
            params["cityCode"] = it.cityCode ?: ""
            params["city"] = it.cityName
        })
        LiveDataBus.get().with(LiveDataBusKey.Conversation, HotPicItemBean::class.java)
            .observe(this,
                Observer {
                    buttomTypeAdapter.setData(2, ButtomTypeBean(it.name, 1, 2))
                    params["topicId"] = it.topicId.toString()
                })


        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION, PoiInfo::class.java).observe(this,
            {
                address = it.address?:it.name?:""
                params["address"] = address
                it.location?.let { mit->
                    params["lat"] = mit.latitude
                    params["lon"] = mit.longitude
                    viewModel.getCityDetailBylngAndlat(it.location.latitude, it.location.longitude)
                }
                params["province"] = it.province ?: address
                buttomTypeAdapter.setData(4, ButtomTypeBean(it.name, 1, 4))

            })

        viewModel.plateBean.observe(this, Observer {
            plateBean = it
           plateBean?.plate?.forEach {
               if (it.name == "社区"){
                   buttomTypeAdapter?.setData(0,ButtomTypeBean("",0,0))
                   buttomTypeAdapter?.setData(1,ButtomTypeBean(it.name,1,1))
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
                    address= ""
//                    buttomTypeAdapter.setData(4, ButtomTypeBean(it, 1, 4))
                })

        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
            selectList.clear()
            selectList.addAll(it as Collection<LocalMedia>)
            postPicAdapter.setList(selectList)
        })

        viewModel.keywords.observe(this, Observer {
            buttomlabelAdapter.addData(it)
            if (locaPostEntity != null) {
                if (locaPostEntity!!.keywords.isNotEmpty()) {
                    buttomlabelAdapter.data.forEach {
                        it.isselect = it.tagName == locaPostEntity!!.keywords
                    }
                    buttomlabelAdapter.notifyDataSetChanged()
                }
            }else{
                handleEditPost()
            }
        })

    }


    override fun initData() {
        onclick()
        viewModel.getPlate() //获取发帖类型
        viewModel.getKeyWords() //标签
        initbuttom()
        params["type"] = 2
        val manager = FullyGridLayoutManager(
            this,
            4, GridLayoutManager.VERTICAL, false
        )
        binding.picsrec.layoutManager = manager
        postPicAdapter.draggableModule.isDragEnabled = true
        binding.picsrec.adapter = postPicAdapter
        postPicAdapter.setList(selectList)

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
                buttomTypeAdapter.setData(3, ButtomTypeBean(circlename, 1, 3))
            }
        }

        if (isTopPost) {
            params["topicId"] = intent.extras?.getString("topId") ?: "0"
            params["topicName"] = intent.extras?.getString("topName") ?: ""
            (params["topicName"] as String).isNotEmpty().let {
                buttomTypeAdapter.setData(2, ButtomTypeBean(params["topicName"] as String, 1, 2))
            }
        }
    }

    private fun initlocaData() {
        if (locaPostEntity != null) {
            jsonStr2obj(locaPostEntity!!.localMeadle)
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
                buttomTypeAdapter.setData(0, ButtomTypeBean("", 0, 0))
                buttomTypeAdapter.setData(1, ButtomTypeBean(locaPostEntity!!.plateName, 1, 1))
            }

            if (locaPostEntity!!.topicName.isNotEmpty()) {
                buttomTypeAdapter.setData(
                    2,
                    ButtomTypeBean(locaPostEntity!!.topicName, 1, 2)
                )
            }


            if (locaPostEntity!!.circleName.isNotEmpty()) {
                buttomTypeAdapter.setData(
                    3,
                    ButtomTypeBean(locaPostEntity!!.circleName, 1, 3)
                )
            }
            if (locaPostEntity!!.address.isNotEmpty()) {
                buttomTypeAdapter.setData(
                    4,
                    ButtomTypeBean(locaPostEntity!!.address, 1, 4)
                )
            }
        }
    }

    private fun jsonStr2obj(jonson: String) {
        val media = JSON.parseArray(jonson, LocalMedia::class.java);
        postPicAdapter.setList(media)
        postPicAdapter.notifyDataSetChanged()
    }

    private fun onclick() {

        binding.etBiaoti.setOnFocusChangeListener { view, b ->
            if (b){
                isshowemoji = false
            }
        }


        binding.title.barImgBack.setOnClickListener {
            var postsId = intent?.getStringExtra("postsId")
            if (binding.etBiaoti.text.toString().isEmpty()) {
                finish()
            } else {
                if (!postsId.isNullOrEmpty()){
                    finish()
                    return@setOnClickListener
                }
                ShowSavePostPop(this, object : ShowSavePostPop.PostBackListener {
                    override fun con() {

                    }

                    override fun save() {
                        var postEntity =
                            if (locaPostEntity != null) locaPostEntity!! else PostEntity()
                        postEntity.content = binding.etContent.text.toString() //内容
                        postEntity.circleId =
                            if (params["circleId"] == null) "" else params["circleId"].toString()  //选择圈子的id
                        postEntity.circleName = circlename  //选择圈子的名称
                        postEntity.plate =
                            if (params["plate"] == null) 0 else params["plate"] as Int//模块ID
                        postEntity.plateName = platename  //模块名称
                        postEntity.topicId =
                            if (params["topicId"] == null) "" else params["topicId"] as String  //话题ID
                        postEntity.topicName = buttomTypeAdapter.getItem(2).content ?: ""  //话题名称
                        postEntity.keywords =
                            if (params["keywords"] != null) params["keywords"].toString() else ""  //关键字
//                    postEntity.keywordValues = binding.keywordTv.text.toString()
                        postEntity.localMeadle = JSON.toJSONString(selectList)
                        postEntity.actionCode =
                            if (params["actionCode"] != null) params["actionCode"] as String else ""
                        postEntity.fmpath =
                            if (selectList.size > 0) PictureUtil.getFinallyPath(selectList[0]) else ""
                        postEntity.type = "2"  //图片帖子类型
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
                        if (locaPostEntity == null) {
                            viewModel.insertPostentity(postEntity)
                        } else {
                            viewModel.update(postEntity)
                        }
                        finish()
                    }

                    override fun unsave() {
//                    viewModel.clearPost()
                        finish()
                    }

                }).showPopupWindow()
            }
        }
        binding.bottom.ivEmoj.setOnClickListener {
//            if (binding.etContent.hasFocus()&&iskeybarOpen){

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
//            }else if(!iskeybarOpen) {
//                Timer().schedule(80) {
//                    binding.bottom.emojirec.post {
//                        if (binding.bottom.emojirec.isShown) {
//                            binding.bottom.emojirec.visibility = View.GONE
//                        }
//                    }
//                }
//            }

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
                when(buttomTypeAdapter.getItem(position).itemType){
                    2 ->{
                        params.remove("topicId")
                        params.remove("topicName")
                    }
                    3->{
                        params.remove("circleId")
                        params.remove("circleName")
                        circlename=""
                    }
                    4->{
                        params.remove("lat")
                        params.remove("lon")
                        params.remove("city")
                        params.remove("province")
                        params.remove("cityCode")
                        params.remove("address")
                        address= ""
                    }
                }
            }

        }

        buttomTypeAdapter.setOnItemClickListener { adapter, view, position ->
            if (buttomTypeAdapter.getItem(position).itemType == 0 || buttomTypeAdapter.getItem(
                    position
                ).itemType == 1
            ) {
                if (::plateBean.isInitialized && plateBean.plate.isNotEmpty()) {
                    val sList = mutableListOf<String>()
                    for (bean in plateBean.plate) {
                        sList.add(bean.name)
                    }
                    HomeBottomDialog(this, *sList.toTypedArray()).setOnClickItemListener(object :
                        HomeBottomDialog.OnClickItemListener {
                        override fun onClickItem(position: Int, str: String) {
                            buttomTypeAdapter.setData(0, ButtomTypeBean("", 0, 0))
                            buttomTypeAdapter.setData(1, ButtomTypeBean(str, 1, 1))
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
                REQUEST_CIRCLE
            )
        }

        binding.bottom.ivLoc.setOnClickListener {
            startARouter(ARouterCirclePath.ChooseLocationActivity)
        }

        binding.bottom.ivPic.setOnClickListener {
            PictureUtil.openGallery(
                this,
                selectList,
                object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        if (result != null) {
                            selectList.clear()
                            selectList.addAll(result)
                        }
                        var bundle = Bundle()
                        bundle.putParcelableArrayList("picList", selectList)
                        bundle.putInt("position", 0)
                        bundle.putInt("showEditType", -1)
                        startARouter(ARouterCirclePath.PictureeditlActivity, bundle)

                    }

                    override fun onCancel() {

                    }

                })
        }
        postPicAdapter.setOnItemClickListener { adapter, view, position ->
            val holder = binding.picsrec.findViewHolderForLayoutPosition(position)
            if (holder != null && holder.itemViewType == 0x9843) {//添加
                "actionbarheight--${ImmersionBar.getActionBarHeight(this)}".logD()
                "NavigationBarHeight--${ImmersionBar.getNavigationBarHeight(this)}".logD()
                "ScreenHeight--${ScreenUtils.getScreenHeight(this)}".logD()
                PictureUtil.openGallery(
                    this,
                    selectList,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            if (result != null) {
                                selectList.clear()
                                selectList.addAll(result)
                            }
                            var bundle = Bundle()
                            bundle.putParcelableArrayList("picList", selectList)
                            bundle.putInt("position", 0)
                            bundle.putInt("showEditType", -1)
                            startARouter(ARouterCirclePath.PictureeditlActivity, bundle)

                        }

                        override fun onCancel() {

                        }

                    })
            } else {
                var bundle = Bundle()
                bundle.putParcelableArrayList("picList", selectList)
                bundle.putInt("position", position)
                bundle.putInt("showEditType", -1)
                startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
            }
        }
        postPicAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_delete) {
                selectList.remove(postPicAdapter.getItem(position))
                postPicAdapter.remove(postPicAdapter.getItem(position))
                postPicAdapter.notifyDataSetChanged()
                binding.mscr.smoothScrollTo(0, 0);
            }
        }


        postPicAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
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
                postPicAdapter.notifyDataSetChanged()
            }

        })
    }

    private fun initbuttom() {
        binding.typerec.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        binding.typerec.adapter = buttomTypeAdapter
        buttomTypeAdapter.addData(
            arrayListOf(
                ButtomTypeBean("选择模块", 1, 0),
                ButtomTypeBean("", 0, 1),
                ButtomTypeBean("", 0, 2),
                ButtomTypeBean("", 0, 3),
                ButtomTypeBean("", 0, 4)
            )
        )
        binding.bottom.labelrec.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        binding.bottom.labelrec.adapter = buttomlabelAdapter

        buttomlabelAdapter.setOnItemClickListener { adapter, view, position ->
            if (buttomlabelAdapter.getItem(position).isselect){
                buttomlabelAdapter.getItem(position).isselect = false
                params.remove("keywords")
            }else{
                buttomlabelAdapter.getItem(position).isselect = true
                buttomlabelAdapter.data.forEachIndexed { index, buttomlabelBean ->
                    if (index != position) {
                        buttomlabelBean.isselect = false
                    }
                }
                params["keywords"] = buttomlabelAdapter.getItem(position).tagName
            }
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

                var index = if (binding.etBiaoti.hasFocus()) {
                    binding.etBiaoti.selectionStart
                } else {
                    binding.etContent.selectionStart
                }

                var editContent = if (binding.etBiaoti.hasFocus()) {
                    binding.etBiaoti.text
                } else {
                    binding.etContent.text
                }
                editContent?.insert(index, emoji)
            }

        })
    }

    private fun ispost() {
        var biaoti = binding.etBiaoti.text.toString()
        var content = binding.etContent.text.toString()
        when {
            selectList.size == 0 -> {
                "请选择图片".toast()
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

    private fun uploadImgs(stsBean: STSBean, index: Int, dialog: LoadDialog) {
        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )

        val media = selectList[index]
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


        AliYunOssUploadOrDownFileConfig.getInstance(this)
            .uploadFile(stsBean.bucketName, path, ytPath, "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {

                upedimgs.add(ImageUrlBean(path, ""))
                val scount = index + 1
                runOnUiThread {
                    dialog.setTvprogress("${scount}/${selectList.size}")
                }
                if (scount == selectList.size) {
                    params["imgUrl"] = upedimgs
                    params["pics"] = upedimgs[0]?.imgUrl
                    params["isPublish"] = 2
                    JSON.toJSONString(params).logD()
                    addPost(dialog)
                    return
                }

                uploadImgs(stsBean, scount, dialog)
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

    fun addPost(dialog: LoadDialog) {
        viewModel.postEdit(params)
    }

    private fun getEmojiStringByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    selectList[0].isCut = true
                    selectList[0].cutPath = resultUri?.path
                    postPicAdapter.setList(selectList)
                    postPicAdapter.notifyDataSetChanged()
                }
                UCrop.RESULT_ERROR -> {
                    val cropError = UCrop.getError(data!!)
                }
                REQUEST_CIRCLE -> {
                    if (data != null) {
                        params["circleId"] = data.getIntExtra("circleId", 0)
                        circlename = data.getStringExtra("name").toString()
                        buttomTypeAdapter.setData(3, ButtomTypeBean(circlename, 1, 3))
                    }
                }
            }
        }
    }

    fun handleEditPost() {
        var postsId = intent?.getStringExtra("postsId")
        postsId?.let {
            //监听下载的图片
            viewModel._downloadLocalMedias.observe(this, {
                //选择的图片重置
                selectList.clear()
                selectList.addAll(it)
                //展示选择的图片
                postPicAdapter.setList(it)
                postPicAdapter.notifyDataSetChanged()
            })

            //赋值
            viewModel.postDetailsBean.observe(this, {
                it?.let { locaPostEntity ->
                    if (locaPostEntity != null) {//同草稿逻辑
                        locaPostEntity.imageList?.let { it1 -> viewModel.downGlideImgs(it1) }
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
                            buttomTypeAdapter.setData(0, ButtomTypeBean("", 0, 0))
                            buttomTypeAdapter.setData(
                                1,
                                ButtomTypeBean(locaPostEntity!!.plateName, 1, 1)
                            )
                        }
                        if (locaPostEntity!!.topicName?.isNotEmpty() == true)
                            buttomTypeAdapter.setData(
                                2,
                                ButtomTypeBean(locaPostEntity!!.topicName ?: "", 1, 2)
                            )
                        if (locaPostEntity!!.circleName?.isNotEmpty() == true) buttomTypeAdapter.setData(
                            3,
                            ButtomTypeBean(locaPostEntity!!.circleName ?: "", 1, 3)
                        )
                        if (locaPostEntity!!.address?.isNotEmpty() == true) buttomTypeAdapter.setData(
                            4,
                            ButtomTypeBean(locaPostEntity!!.address, 1, 4)
                        )
                        //选择的标签
                        if (locaPostEntity!!.keywords?.isNotEmpty()==true) {
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


}

