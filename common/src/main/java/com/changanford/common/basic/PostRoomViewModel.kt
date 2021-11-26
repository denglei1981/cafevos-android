package com.changanford.common.basic

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.net.ApiClient
import com.changanford.common.net.getRandomKey
import com.changanford.common.room.PostDatabase
import com.changanford.common.room.PostEntity
import com.changanford.common.utilext.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * 帖子数据库操作 可继承
 */
open class PostRoomViewModel() : BaseViewModel() {

    var posts = MutableLiveData<List<PostEntity>>()

    /**
     * 插入帖子
     */
    fun insertPostentity(postEntity: PostEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            PostDatabase.getInstance(MyApp.mContext).getPostDao()
                .insert(postEntity)
        }
    }

    /**
     * 删除所有帖子
     */
    fun clearPost() {
        viewModelScope.launch(Dispatchers.IO) {
            PostDatabase.getInstance(MyApp.mContext).getPostDao()
                .clearAll()
        }
    }

    /**
     * 删除该帖子
     */
    fun deletePost(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            PostDatabase.getInstance(MyApp.mContext).getPostDao()
                .delete(id)
        }
    }

    /**
     * 查询所有帖子
     */
    fun findAll(){
        viewModelScope.launch(Dispatchers.IO) {
          posts.value =  PostDatabase.getInstance(MyApp.mContext).getPostDao().findAll().value
        }
    }

    /**
     * 查询所有帖子
     */
    fun update(postEntity: PostEntity){
        viewModelScope.launch(Dispatchers.IO) {
            PostDatabase.getInstance(MyApp.mContext).getPostDao().update(postEntity)
        }
    }

}