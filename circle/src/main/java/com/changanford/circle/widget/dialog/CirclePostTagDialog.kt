package com.changanford.circle.widget.dialog


import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.adapter.DialogPostTagAdapter
import com.changanford.circle.bean.PostKeywordBean
import com.changanford.circle.bean.PostTagData
import com.changanford.circle.databinding.DialogCirclePostTagBinding
import com.changanford.common.ui.dialog.BaseAppCompatDialog
import kotlin.math.sign

class CirclePostTagDialog(private val activity: AppCompatActivity) : BaseAppCompatDialog(activity) {

    var postTagDataList: List<PostTagData>? = null
    var selectTagList: List<PostKeywordBean>? = null
    var mDatabind: DialogCirclePostTagBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.dialog_circle_post_tag,
        null,
        false
    )
    lateinit var callback: ICallbackTag

    var totalTags:Int=0;
    private val dialogPostTagAdapter: DialogPostTagAdapter by lazy {
        DialogPostTagAdapter()
    }

    constructor(
        acts: AppCompatActivity,
        callback: ICallbackTag,
        postTagDataList: List<PostTagData>,
        selectTagList: List<PostKeywordBean>
    ) : this(acts) {
        setContentView(mDatabind.root)
        this.callback = callback
        this.postTagDataList = postTagDataList
        this.selectTagList = selectTagList
        initView()
        initData()
    }

    fun initView() {
        mDatabind.rvList.adapter = dialogPostTagAdapter
        mDatabind.tvCancel.setOnClickListener {
            dismiss()
        }
        mDatabind.tvSure.setOnClickListener {
            dialogPostTagAdapter.hobbyIds.forEach{
                it.isselect=true
            }
            if(dialogPostTagAdapter.hobbyIds.size>=6){
                callback.callbackTag(false, dialogPostTagAdapter.hobbyIds,totalTags)
            }else{// 补齐6个
                if (dialogPostTagAdapter.hobbyIds.size < totalTags) {
                    val last = mutableListOf<PostKeywordBean>()
                    selectTagList?.forEach {
                        if( !dialogPostTagAdapter.hobbyIds.contains(it)){
                            if(last.size>=totalTags-dialogPostTagAdapter.hobbyIds.size){
                                return@forEach
                            }
                            it.isselect=false
                            last.add(it)
                        }
                    }
                    dialogPostTagAdapter.hobbyIds.addAll(last)
                    callback.callbackTag(false, dialogPostTagAdapter.hobbyIds,totalTags)
                }

            }


            dismiss()
        }

    }

    fun initData() {
        selectTagList?.forEach {
            if (it.isselect) {
                dialogPostTagAdapter.hobbyIds.add(it)
            }
        }
        postTagDataList?.forEach {
            totalTags+=it.tagMaxCount
        }
        dialogPostTagAdapter.setList(postTagDataList)
    }


    override fun initAd() {

    }

    interface ICallbackTag {
        fun callbackTag(cancel: Boolean, tags: MutableList<PostKeywordBean>,totalTags:Int)
    }

}
