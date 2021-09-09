package com.changanford.shop.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapterOneLayout<T>(context: Context, layoutRes: Int) :
    RecyclerView.Adapter<BaseAdapterOneLayout.BaseViewHolder>(),
    BaseAdapterDelegate {

    private var mContext: Context? = null

    private val mLayoutRes = layoutRes

    private var mOnItemClickListener: OnRecyclerViewItemClickListener? = null

    private var mItems: ArrayList<T>? = null

    private var mInflater: LayoutInflater? = null

    init {
        this.mContext = context
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(mInflater?.inflate(mLayoutRes, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding: ViewDataBinding? = holder.binding
        fillData(binding, mItems!![position], position)
        binding?.let { it ->
            it.root.setOnClickListener {
                mOnItemClickListener?.onItemClick(it, position)
            }
        }
    }


    /**
     * @param vdBinding item布局
     * @param position  下标
     */
    abstract fun fillData(vdBinding: ViewDataBinding?, item: T, position: Int)

    open fun setItems(items: ArrayList<T>?) {
        mItems = items
    }

    open fun getItems(): ArrayList<T>? {
        if (mItems == null) {
            mItems = ArrayList()
        }
        return mItems
    }

    class BaseViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(itemView!!)

    }

    override val isEmpty: Boolean
        get() = mItems!!.isEmpty()

    override fun destroy() {
        mItems?.clear()
        mContext = null
        mInflater = null
    }

    override fun getItemCount(): Int {
        return if(mItems.isNullOrEmpty()){
            0
        }else{
            mItems?.size!!
        }
    }

    open fun setOnItemClickListener(listener: OnRecyclerViewItemClickListener?) {
        mOnItemClickListener = listener
    }
}

