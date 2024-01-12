package com.changanford.common.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.luck.picture.lib.entity.LocalMedia
import java.io.Serializable

/**
 * 帖子表
 */
@Entity(tableName = "post_table")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "postsId") var postsId: Long = 0,//帖子id
    @ColumnInfo(name = "circleId") var circleId: String = "",  //圈子id
    @ColumnInfo(name = "circleName") var circleName: String = "",  //圈子名称
    @ColumnInfo(name = "content") var content: String = "",//内容
    @ColumnInfo(name = "imgUrl") var imgUrl: String = "",//图片帖子中,图片地址 jsonstring
    @ColumnInfo(name = "isPublish") var isPublish: String = "",//发布状态 1 草稿 2 发布
    @ColumnInfo(name = "keywords") var keywords: String = "",//关键字
    @ColumnInfo(name = "keywordValues") var keywordValues: String = "",//关键字
    @ColumnInfo(name = "pics") var pics: String = "",//封面
    @ColumnInfo(name = "plate") var plate: Int = 0,//所属板块id 1 大咖 2 社区
    @ColumnInfo(name = "plateName") var plateName: String = "",//所属板块名称 1 大咖 2 社区
    @ColumnInfo(name = "title") var title: String = "",//标题
    @ColumnInfo(name = "topicId") var topicId: String = "",//话题id
    @ColumnInfo(name = "carModelId") var carModelId: String = "",//车型id
    @ColumnInfo(name = "carModelName") var carModelName: String = "",//车型昵称
    @ColumnInfo(name = "topicName") var topicName: String = "",//话题id
    @ColumnInfo(name = "type") var type: String = "",//帖子类型 1 图文 2 图片 3 视频 4 图文长帖，5 报名活动 ，6 投票活动
    @ColumnInfo(name = "userId") var userId: String = "",//用户id
    @ColumnInfo(name = "videoTime") var videoTime: String = "",//若为视频帖子,视频时长
    @ColumnInfo(name = "videoUrl") var videoUrl: String = "",//视频帖子中视频地址
    @ColumnInfo(name = "actionCode") var actionCode: String = "", //板块对应的行为权限
    @ColumnInfo(name = "localMeadle") var localMeadle: String = "",//图片视频对象tojsonstring
    @ColumnInfo(name = "fmpath") var fmpath: String = "", //图片视频对象tojsonstring
    @ColumnInfo(name = "lat") var lat: Double = 0.0,
    @ColumnInfo(name = "lon") var lon: Double = 0.0,
    @ColumnInfo(name = "address") var address: String = "",
    @ColumnInfo(name = "city") var city: String = "",
    @ColumnInfo(name = "province") var province: String = "",
    @ColumnInfo(name = "cityCode") var cityCode:String ="",
    @ColumnInfo(name="creattime") var creattime:String="",
    @ColumnInfo(name="longpostFmLocalMeadle") var longpostFmLocalMeadle:String="",//发长图的封面
    @ColumnInfo(name="longPostDatas") var longPostDatas:String="", //发长图的列表集合 分开存储是因为可能没有选择封面
    @ColumnInfo(name="tags") var tags:String="",   // 标签 存储为json
    @ColumnInfo(name="addrName") var addrName:String="",
    @ColumnInfo(name = "baoming") var baoming:String = "",//报名活动
    @ColumnInfo(name = "toupiao") var toupiao:String = "",//投票活动
    @ColumnInfo(name = "isShowCar") var isShowCar:Boolean = false,//投票活动


) : Serializable

@Dao
interface PostDao {
    @Query("SELECT * FROM post_table")
    fun findAll(): LiveData<List<PostEntity>>

    @Query("DELETE  from post_table")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PostEntity)

    @Query("Delete from post_table Where postsId = :postid")
    suspend fun delete(postid: Long)

    @Update
    suspend fun update(postEntity: PostEntity)

    // 查询最后一调数据，删掉。
    @Query("SELECT * FROM post_table ORDER BY postsId DESC LIMIT 1 ")
    fun findLastPostEntity(): LiveData<PostEntity>




}