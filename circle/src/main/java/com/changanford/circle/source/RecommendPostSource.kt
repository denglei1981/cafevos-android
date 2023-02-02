package com.changanford.circle.source

import androidx.paging.PagingSource
import com.changanford.circle.api.CircleNetWork
import com.changanford.common.MyApp
import com.changanford.common.bean.PostBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.buried.RetrofitClient
import com.changanford.common.net.*
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.createHashMap

/**
 *Author lcw
 *Time on 2023/2/2
 *Purpose
 */
class RecommendPostSource(private val viewType: Int) : PagingSource<Int, PostDataBean>() {

    private val recommendRepository by lazy { RecommendRepository() }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostDataBean> {
        // 如果key是null，那就加载第1页的数据
        val page = params.key ?: 1
        // 每一页的数据长度
        val pageSize = params.loadSize
        return try {
            val body = MyApp.mContext.createHashMap()
            body["pageNo"] = page
            body["pageSize"] = 20
            body["queryParams"] = HashMap<String, Any>().also {
                it["viewType"] = viewType
                it["type"] = viewType
            }
            val rKey = getRandomKey()
            val response = recommendRepository.getRecommendPosts(body, rKey)
            LiveDataBus.get().with(CircleLiveBusKey.REFRESH_CIRCLE_MAIN).postValue(false)
            LoadResult.Page(
                data = response.data!!.dataList,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.data!!.dataList.size != 20) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}

class RecommendRepository {
    suspend fun getRecommendPosts(body: HashMap<String, Any>, rKey: String) =
        ApiClient.createApi<CircleNetWork>()
            .getRecommendPosts(body.header(rKey), body.body(rKey)).onSuccess {  }
}