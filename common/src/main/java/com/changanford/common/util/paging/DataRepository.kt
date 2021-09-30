package com.changanford.common.util.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.changanford.common.net.CommonResponse
import kotlinx.coroutines.flow.Flow

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.paging.DataRepository
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/25 17:43
 * @Description: 　
 * *********************************************************************************
 */
object DataRepository {

    fun <T, V : Any> getDataInfo(
        pageSize: Int = 10,
        query: suspend (Int, Int) -> CommonResponse<T>,
        list: (T?) -> List<V>?
    ): Flow<PagingData<V>> {
        return Pager(config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        ), pagingSourceFactory =
        {
            DataSource(query, list)
        }).flow
    }

}