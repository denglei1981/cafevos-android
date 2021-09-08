package com.changanford.common.util.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.changanford.common.net.CommonResponse

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.paging.DataSource
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/25 17:13
 * @Description: 网络数据源封装，用于分页的网络请求
 * @param query,请求接口
 * *********************************************************************************
 */
class DataSource<T, V : Any>(
    val query: suspend (Int, Int) -> CommonResponse<T>,//传入页数和每页数据，请求接口
    private val list: (T?) -> List<V>?//获取展示的列表，接口中可能有其他数据，需要这个方法获取列表数据
) :
    PagingSource<Int, V>() {
    override fun getRefreshKey(state: PagingState<Int, V>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, V> {
        var pageNum: Int = params.key ?: 1
        return try {
            var data: CommonResponse<T> = query(pageNum, params.loadSize)
            var items: List<V>? = list(data.data)
            LoadResult.Page(
                items ?: arrayListOf(),
                prevKey = if (pageNum == 1) null else (pageNum - 1),
                nextKey = if (items == null ||items?.size == 0) null else (pageNum + 1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}