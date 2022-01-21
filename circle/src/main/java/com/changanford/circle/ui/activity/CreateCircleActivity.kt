package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.util.Log
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
import com.changanford.common.listener.OnPerformListener
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
import com.xiaomi.push.it

/**
 *Author lcw
 *Time on 2021/10/11
 *Purpose 创建圈子
 */
@Route(path = ARouterCirclePath.CreateCircleActivity)
class CreateCircleActivity : BaseActivity<ActivityCreateCircleBinding, CreateCircleViewModel>() {
    private var picUrl = ""
    private val mAdapter by lazy { CircleTagAdapter(listener=listener) }
    private var circleItemBean:CircleItemBean?=null
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
        circleItemBean = intent.getSerializableExtra(RouterManger.KEY_TO_ITEM) as CircleItemBean?
        circleItemBean?.let {
            picUrl = it.pic
            binding.apply {
                ivFengmian.loadImage(it.pic)
                etBiaoti.setText(it.name)
                etContent.setText(it.description)
            }
        }
        //获取圈子标签信息
        viewModel.getTagInfo()
        btnIsClick()
    }
    /**
     * 创建按钮是否可以点击
     * */
    private fun btnIsClick(){
        binding.title.wtvCreate.apply {
            val titleLength=binding.etBiaoti.text.length
            if(picUrl.isEmpty()||titleLength<4||binding.etContent.text.isEmpty()|| mAdapter.data.none { it.isCheck == true }){
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
        if(null==circleItemBean) viewModel.createCircle(title,content, picUrl,tagIds)
        else viewModel.editCircle(circleItemBean?.circleId,title,content,picUrl,tagIds)

    }
    private val listener=object : OnPerformListener {
        override fun onFinish(code: Int) {
            btnIsClick()
        }
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
        viewModel.tagInfoData.observe(this,{tagInfo->
            tagInfo?.apply {
                mAdapter.tagMaxCount=tagMaxCount?:0
                circleItemBean?.tagIds?.forEach {tagId->
                    tags?.let {tagItem->
                        val index=tagItem.indexOfFirst {item->tagId==item.tagId}
                        if(index>=0)tagItem[index].isCheck=true
                    }
                }
                mAdapter.setList(tags)
                btnIsClick()
            }
        })
    }
}