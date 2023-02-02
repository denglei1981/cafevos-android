package com.changanford.common.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.changanford.common.R
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.databinding.ItemPagingHeadBinding
import com.changanford.common.databinding.ItemPagingLoadmoreBinding
import java.lang.Exception

/**
 * @Author: lcw
 * @Des: 分页adapter
 */
class PagingAdapter(private val context: Context) :
    PagingDataAdapter<PagingItemView<Any>, RecyclerView.ViewHolder>(provideDiffer()) {

    private var mOnItemClickListener: OnRecyclerViewItemClickListener? = null

    private val mLayoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)!!.layoutRes
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            getItem(position)?.onBindView(
                position = position,
                binding = DataBindingUtil.bind(holder.itemView)!!
            )
            holder.itemView.setOnClickListener {
                mOnItemClickListener?.onItemClick(it, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PagingVHolder(mLayoutInflater.inflate(viewType, parent, false))
    }

    fun setOnItemClickListener(listener: OnRecyclerViewItemClickListener?) {
        mOnItemClickListener = listener
    }
}

class LoadMoreAdapter(private val retryCallBack: () -> Unit) : LoadStateAdapter<LoadMoreView>() {
    override fun onBindViewHolder(holder: LoadMoreView, loadState: LoadState) {
        holder.bindState(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadMoreView {
        return LoadMoreView(parent, loadState, retryCallBack)
    }

}

class LoadMoreView(
    parent: ViewGroup,
    loadState: LoadState,
    val retryCallBack: () -> Unit
) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)!!.inflate(R.layout.item_paging_loadmore, parent, false)
    ) {

    val loading = ObservableBoolean()

    init {
        DataBindingUtil.bind<ItemPagingLoadmoreBinding>(itemView)?.itemview = this
        bindState(loadState)
    }

    fun bindState(loadState: LoadState) {
        loading.set(loadState is LoadState.Loading)
    }
}

class HeadAdapter : LoadStateAdapter<HeadView>() {
    override fun onBindViewHolder(holder: HeadView, loadState: LoadState) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): HeadView {
        return HeadView(parent)
    }

}

class HeadView(
    parent: ViewGroup,
) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)!!.inflate(R.layout.item_paging_head, parent, false)
    ) {


    init {
        DataBindingUtil.bind<ItemPagingHeadBinding>(itemView)?.itemview = this
    }

}

class PagingVHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

private fun provideDiffer() = object :
    DiffUtil.ItemCallback<PagingItemView<Any>>() {
    override fun areItemsTheSame(
        oldItem: PagingItemView<Any>,
        newItem: PagingItemView<Any>
    ): Boolean {
        return try {
            oldItem.areItemsTheSame(newItem)
        } catch (e: Exception) {
            false
        }

    }

    override fun areContentsTheSame(
        oldItem: PagingItemView<Any>,
        newItem: PagingItemView<Any>
    ): Boolean {
        return try {
            oldItem.areContentsTheSame(newItem)
        } catch (e: Exception) {
            false
        }
    }

}