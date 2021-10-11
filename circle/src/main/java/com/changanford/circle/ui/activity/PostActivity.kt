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
import com.changanford.circle.bean.ButtomTypeBean
import com.changanford.circle.bean.ButtomlabelBean
import com.changanford.circle.bean.HotPicItemBean
import com.changanford.circle.bean.PlateBean
import com.changanford.circle.databinding.PostActivityBinding
import com.changanford.circle.viewmodel.PostViewModule
import com.changanford.circle.widget.pop.ShowSavePostPop
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.ImageUrlBean
import com.changanford.common.bean.STSBean
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
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

/**
 * 发图片帖子
 */
@Route(path = ARouterCirclePath.PostActivity)
class PostActivity : BaseActivity<PostActivityBinding, PostViewModule>() {
    private var platename: String=""
    private var circlename: String=""
    private var address: String = ""
    lateinit var postPicAdapter: PostPicAdapter
    private var selectList = ArrayList<LocalMedia>()
    private var type = 0

    private var params = hashMapOf<String, Any>()
    private lateinit var plateBean: PlateBean
    private var nomalwith = 500;
    private var nomalhight = 500;

    private val upedimgs = ArrayList<ImageUrlBean>()  //上传之后的图片集合地址


    private val dialog by lazy{
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

    companion object{
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

    }

    override fun observe() {
        super.observe()

        viewModel.postsuccess.observe(this, Observer {
            if (dialog.isShowing){
                dialog.dismiss()
            }
            "发布成功".toast()
            finish()
        })
        viewModel.stsBean.observe(this, Observer {
            it?.let {
                upedimgs.clear()
                uploadImgs(it,0,dialog)
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
                    params["topicId"] = it.topicId
                })


        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION, PoiInfo::class.java).observe(this,
            {
                it.location.latitude.toString().toast()
                address = it.address
                params["address"] = address
                params["lat"] = it.location.latitude
                params["lon"] = it.location.longitude
                params["province"] = it.province ?: address
                viewModel.getCityDetailBylngAndlat(it.location.latitude, it.location.longitude)
                buttomTypeAdapter.setData(4, ButtomTypeBean(it.name, 1, 4))

            })

        viewModel.plateBean.observe(this, Observer {
            plateBean = it

        })
        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATIONNOTHING, String::class.java)
            .observe(this,
                {
                    it.toString().toast()
                })

        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
            selectList.clear()
            selectList.addAll(it as Collection<LocalMedia>)
            postPicAdapter.setList(selectList)
        })


    }


    override fun initData() {
        onclick()
        viewModel.getPlate() //获取发帖类型
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


    }

    private fun onclick() {
        binding.title.barImgBack.setOnClickListener {
            if (binding.etBiaoti.text.toString().isEmpty()){
                finish()
            }else{
                ShowSavePostPop(this, object : ShowSavePostPop.PostBackListener {
                    override fun con() {

                    }

                    override fun save() {
                        var postEntity = PostEntity()
                        postEntity.content = binding.etContent.text.toString()
                        postEntity.circleId = params["circleId"] as? String ?: "0"
                        postEntity.circleName = circlename
                        postEntity.plate = params["plate"] as? Int ?: 0
                        postEntity.plateName = platename
                        postEntity.topicId = params["topicId"] as? String ?: "0"
//                    postEntity.topicName = binding.choseTopicTv.text.toString()  //关键词
//                    postEntity.keywords = hashMap["keywords"].toString()  //关键字
//                    postEntity.keywordValues = binding.keywordTv.text.toString()
                        postEntity.localMeadle = JSON.toJSONString(selectList)
                        postEntity.actionCode = params["actionCode"].toString()
//                    postEntity.fmpath = mAdapter.fmPath.toString()
                        postEntity.type = params["type"].toString()
                        postEntity.title =
                            if (binding.etBiaoti.text == null) "" else "${binding.etBiaoti.text}"
                        postEntity.address = params["address"] as? String ?: ""
                        postEntity.lat = params["lat"] as? Double ?: 0.0
                        postEntity.lon = params["lon"] as? Double ?: 0.0
                        postEntity.city = params["city"] as? String ?: ""
                        postEntity.province = params["province"] as? String ?: ""
                        postEntity.cityCode = params["cityCode"] as? String ?: ""
                        viewModel.insertPostentity(postEntity)
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
            "表情未开发".toast()
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
        binding.bottom.typerec.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        binding.bottom.typerec.adapter = buttomTypeAdapter
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
        buttomlabelAdapter.addData(
            arrayListOf(
                ButtomlabelBean("最美重庆1"),
                ButtomlabelBean("最美重庆111"),
                ButtomlabelBean("最美重庆12222"),
                ButtomlabelBean("最美重庆13333"),
                ButtomlabelBean("最美重庆144444")
            )
        )
        buttomlabelAdapter.setOnItemClickListener { adapter, view, position ->
            buttomlabelAdapter.getItem(position).isselect = true
            buttomlabelAdapter.data.forEachIndexed { index, buttomlabelBean ->
                if (index != position) {
                    buttomlabelBean.isselect = false
                }
            }
            buttomlabelAdapter.notifyDataSetChanged()
            buttomlabelAdapter.getItem(position).content.toast()
        }
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
            !params.containsKey("plate") -> {
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


        params["pics"] = path

        AliYunOssUploadOrDownFileConfig.getInstance(this)
            .uploadFile(stsBean.bucketName, path, ytPath, "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {

                upedimgs.add(ImageUrlBean(path,""))
                val scount = index + 1
                runOnUiThread {
                    dialog.setTvprogress("${scount}/${selectList.size}")
                }
                if (scount == selectList.size) {
                    params["imgUrl"] = upedimgs
                    params["isPublish"]= 2
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
                        params["circleId"] = data.getStringExtra("circleId") ?: "0"
                        circlename = data.getStringExtra("name").toString()
                        buttomTypeAdapter.setData(3, ButtomTypeBean(circlename, 1, 3))
                    }
                }
            }
        }
    }


}

