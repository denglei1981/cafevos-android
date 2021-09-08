package com.changanford.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.changanford.common.bean.RecommendData
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.util.paging.DataRepository
import kotlinx.coroutines.flow.Flow

class ShopViewModel : ViewModel() {
    /**
     * 使用paging获取接口数据，默认每页10条
     */
    fun getRecommendList(): Flow<PagingData<RecommendData>> {
        return DataRepository.getDataInfo(query = { pageNum, pageSize ->
            fetchRequest(pageNum == 1) {
                var map = HashMap<String, Any>()
                map["pageNo"] = pageNum
                map["pageSize"] = pageSize
                val hashMap = HashMap<String, Any>()
                hashMap["pageNo"] = pageNum.toString()
                hashMap["pageSize"] = pageSize.toString()
                hashMap["queryParams"] = HashMap<String, Any>().also {
                    it["isHotSelected"] = "1"
                }
                var rkey = getRandomKey()
                apiService.getRecommendList(map.header(rkey), map.body(rkey))
            }
        }, list = {
            it?.dataList
        }).cachedIn(viewModelScope)
    }
}