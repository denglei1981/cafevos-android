package com.changanford.common.basic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

/**
 * Pair<Int, Int>
 * 第一个int mLayoutResId
 * 第二个int viewType
 */
abstract class BaseAdapter<T>(context: Context, vararg extras: Pair<Int, Int>) :
    RecyclerView.Adapter<BaseAdapter.BaseViewHolder>(),
    BaseAdapterDelegate {

    private var mContext: Context? = null

    private val mExtras = extras

    private var mOnItemClickListener: OnRecyclerViewItemClickListener? = null

    private var mItems: ArrayList<T>? = null

    private var mInflater: LayoutInflater? = null

    init {
        this.mContext = context
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (mExtras.size > 1) {
            mExtras.forEach { (key, value) ->
                if (viewType == value) {
                    return BaseViewHolder(mInflater?.inflate(key, parent, false))
                }
            }
        }
        return BaseViewHolder(mInflater?.inflate(mExtras[0].first, parent, false))
    }

    override fun getItemCount(): Int {
        return if (mItems.isNullOrEmpty()) return 0 else mItems?.size!!
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding: ViewDataBinding? = holder.binding
        fillData(binding, mItems!![position], position, getItemViewType(position))
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
    abstract fun fillData(vdBinding: ViewDataBinding?, item: T, position: Int, viewType: Int)

    open fun setItems(items: ArrayList<T>?) {
        if (items == null) {
            return
        }
        mItems = items
    }

    open fun getItems(): ArrayList<T>? {
        if (mItems == null) {
            mItems = ArrayList()
        }
        return mItems
    }

    open fun getItem(position: Int): T? {
        return mItems?.get(position)
    }

    class BaseViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(itemView!!)
    }

    override val isEmpty: Boolean
        get() = mItems.isNullOrEmpty()

    override fun destroy() {
        mItems?.clear()
        mContext = null
        mInflater = null
    }

    open fun setOnItemClickListener(listener: OnRecyclerViewItemClickListener?) {
        mOnItemClickListener = listener
    }

}

