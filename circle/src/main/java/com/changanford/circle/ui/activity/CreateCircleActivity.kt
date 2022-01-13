package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.circle.CircleTagAdapter
import com.changanford.circle.databinding.ActivityCreateCircleBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.circle.viewmodel.CreateCircleViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.CircleItemBean
import com.changanford.common.helper.OSSHelper
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.toast
import com.changanford.common.wutil.FlowLayoutManager
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

/**
 *Author lcw
 *Time on 2021/10/11
 *Purpose 创建圈子
 */
@Route(path = ARouterCirclePath.CreateCircleActivity)
class CreateCircleActivity : BaseActivity<ActivityCreateCircleBinding, CreateCircleViewModel>() {

    private var picUrl = ""
    private val mAdapter by lazy { CircleTagAdapter() }
    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.title.apply {
            AppUtils.setStatusBarMarginTop(rlTitle, this@CreateCircleActivity)
            ivBack.setOnClickListener { finish() }
            tvTitle.text = "创建圈子"
            wtvCreate.visibility= View.VISIBLE
            wtvCreate.setOnClickListener { submit() }
        }

        binding.run {
            ivFengmian.setCircular(5)
            etBiaoti.addTextChangedListener {
                binding.tvNum.text = it?.length.toString() + "/8"
                btnIsClick()
            }
            etContent.addTextChangedListener {
                binding.tvNum1.text = it?.length.toString() + "/50"
                btnIsClick()
            }
        }
        val flowLayoutManager1 =FlowLayoutManager(this@CreateCircleActivity,true)
        val flowLayoutManager0 =FlowLayoutManager(this@CreateCircleActivity,2) {
            binding.cbMore.visibility=if(it>2)View.VISIBLE else View.GONE
        }
        binding.recyclerView.apply {
            this.layoutManager=flowLayoutManager0
            adapter=mAdapter
        }
        binding.cbMore.apply {
            setOnClickListener {
                binding.recyclerView.layoutManager=if(isChecked)flowLayoutManager1 else flowLayoutManager0
            }
        }

        initListener()
    }

    private fun initListener() {
        binding.run {
            ivFengmian.setOnClickListener {
                PictureUtil.openGalleryOnePic(this@CreateCircleActivity, object :
                    OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        val bean = result?.get(0)
                        val path = bean?.let { it1 -> PictureUtil.getFinallyPath(it1) }
                        path?.let { it1 ->
                            OSSHelper.init(this@CreateCircleActivity)
                                .getOSSToImage(this@CreateCircleActivity,
                                    it1, object : OSSHelper.OSSImageListener {
                                        override fun getPicUrl(url: String) {
                                            picUrl = url
                                            ivFengmian.post {
                                                ivFengmian.loadImage(picUrl, ImageOptions().apply {
                                                    placeholder = R.mipmap.add_image
                                                })
                                            }
                                        }

                                    })
                        }
                    }

                    override fun onCancel() {

                    }

                })
            }

        }
    }


    override fun initData() {
        //获取圈子标签信息
        viewModel.getTagInfo()
        val data = intent.getSerializableExtra(RouterManger.KEY_TO_ITEM) as CircleItemBean?
        if (data != null) {
            binding.run {
                picUrl = data.pic
                ivFengmian.loadImage(data.pic)
                etBiaoti.setText(data.name)
                etContent.setText(data.description)
                binding.commit.text = "立即编辑"

                commit.setOnClickListener {
                    val title = binding.etBiaoti.text.toString()
                    val content = binding.etContent.text.toString()

                    if (picUrl.isEmpty()) {
                        "请上传封面".toast()
                        return@setOnClickListener
                    }
                    if (title.isEmpty()) {
                        "请输入标题".toast()
                        return@setOnClickListener
                    }
                    if (content.isEmpty()) {
                        "请输入详情".toast()
                        return@setOnClickListener
                    }
                    viewModel.editCircle(content, data.circleId.toString(), title, picUrl)
                }
            }
        } else {
            binding.commit.setOnClickListener {
                val title = binding.etBiaoti.text.toString()
                val content = binding.etContent.text.toString()

                if (picUrl.isEmpty()) {
                    "请上传封面".toast()
                    return@setOnClickListener
                }
                if (title.isEmpty()) {
                    "请输入标题".toast()
                    return@setOnClickListener
                }
                if (content.isEmpty()) {
                    "请输入详情".toast()
                    return@setOnClickListener
                }
                viewModel.upLoadCircle(content, title, picUrl)
            }
        }
        btnIsClick()
    }
    /**
     * 创建按钮是否可以点击
     * */
    private fun btnIsClick(){
        binding.title.wtvCreate.apply {
            val titleLength=binding.etBiaoti.text.length
            if(picUrl.isEmpty()||titleLength<4||binding.etContent.text.isEmpty()){
                isEnabled=false
                setBackgroundResource(R.drawable.shadow_dd_12dp)
            }else{
                isEnabled=true
                setBackgroundResource(R.drawable.shadow_00095b_12dp)
            }
        }
    }
    private fun submit(){
        val title = binding.etBiaoti.text.toString()
        val content = binding.etContent.text.toString()
        val tagIds= arrayListOf<Int>()
        //被选中的标签集合
        mAdapter.data.filter { it.isCheck==true }.apply {
            forEach { it.tagId?.apply { tagIds.add(this) }}
        }
        viewModel.upLoadCircle(content, title, picUrl,tagIds)
    }
    override fun observe() {
        super.observe()
        viewModel.upLoadBean.observe(this, {
            it.msg.toast()
            if (it.code == 0) {
                LiveDataBus.get().with(CircleLiveBusKey.REFRESH_MANAGEMENT_CIRCLE).postValue(false)
                finish()
            }
        })
        viewModel.tagInfoData.observe(this,{
            it?.apply {
                mAdapter.tagMaxCount=tagMaxCount?:0
                mAdapter.setList(tags)
            }
        })
    }
}