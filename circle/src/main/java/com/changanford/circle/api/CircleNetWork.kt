package com.changanford.circle.api

import com.changanford.circle.bean.*
import com.changanford.circle.bean.AskListMainData
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.bean.MechanicData
import com.changanford.circle.bean.PostKeywordBean
import com.changanford.common.bean.*
import com.changanford.common.net.CommonResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

/**
 *Author lcw
 *Time on 2021/9/24
 *Purpose
 */
interface CircleNetWork {

    /**
     * 获取全部圈子
     */
    @POST("con/circle/getAllCircles")
    suspend fun getAllCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 社区：获取 最热、 最新、精华 推荐 帖子
     */
    @POST("con/posts/getPosts")
    suspend fun getPosts(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<PostBean>


    @POST("con/community/recommendPosts")
    suspend fun getRecommendPosts(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<PostBean>


    /**
     * 获取话题列表
     */
    @POST("con/topic/getSugesstionTopics")
    suspend fun getSugesstionTopics(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HotPicBean>

    /**
     * 获取话题详情
     */
    @POST("con/topic/getTopicInfo")
    suspend fun getSugesstionTopicDetail(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<SugesstionTopicDetailBean>

    /**
     * 获取圈子
     */
    @POST("con/community/circleAllTypeList")
    suspend fun getAllTypeCircles(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HomeDataListBean<ChoseCircleBean>>

    /**
     * 查询圈子详情
     */
    @POST("con/community/circleInfo")
    suspend fun queryCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleDetailBean>

    /**
     * 帖子详情
     */
    @POST("con/posts/postsDetail")
    suspend fun getPostsDetail(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<PostsDetailBean>

    /**
     * 社区 and 我的圈子 ：圈子成员
     */
    @POST("con/circle/getCircleUsers")
    suspend fun getCircleUsers(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HomeDataListBean<CircleMemberBean>>

    /**
     *  一级评论列表
     */
    @POST("con/comment/commentList")
    suspend fun getCommentList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HomeDataListBean<CommentListBean>>

    /**
     * 圈子搜索
     */
    @POST("con/circle/searchCircles")
    suspend fun searchCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HomeDataListBean<ChoseCircleBean>>

    /**
     * 话题搜索
     */
    @POST("con/topic/searchTopics")
    suspend fun searchTopics(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HomeDataListBean<HotPicItemBean>>

    /**
     * 发布图片帖子
     */
    @POST("con/posts/addPosts")
    suspend fun postEdit(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    @POST("con/posts/getPlate")
    suspend fun getPlate(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<PlateBean>

    /**
     * 标签
     */
    @POST("con/posts/keyWords")
    suspend fun getkeywords(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<PostKeywordBean>>





    @POST("con/community/keyWords")
    suspend fun getTags(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<PostTagData>>

    /**
     * 帖子点赞
     */
    @POST("con/posts/actionLike")
    suspend fun actionLike(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 帖子收藏
     */
    @POST("con/collection/post")
    suspend fun collectionApi(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 评论点赞
     */
    @POST("con/comment/actionLike")
    suspend fun commentLike(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 关注或者取消关注
     */
    @POST("userFans/userFollowOrCanaleFollow")
    suspend fun userFollowOrCancelFollow(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     *  帖子评论
     */
    @POST("con/posts/addComment")
    suspend fun addPostsComment(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>


    /**
     *   资讯评论
     * */
    @POST("/con/article/addComment")
    suspend fun addCommentNews(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 加入圈子
     */
    @POST("con/circle/joinCircle")
    suspend fun joinCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 参与的圈子
     */
    @POST("con/circle/getJoinCircles")
    suspend fun getjoinCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ChooseCircleBean>

    /**
     * 我创建的圈子
     */
    @POST("con/circle/getCreateCircles")
    suspend fun getCreateCircles(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ChooseCircleBean>

    /**
     *  一级评论回复列表
     */
    @POST("con/comment/childCommentList")
    suspend fun getChildCommentList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HomeDataListBean<ChildCommentListBean>>

    @POST("baseDealer/getCityDetailBylngAndlat")
    suspend fun getCityDetailBylngAndlat(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<LocationDataBean>

    /**
     * 社区v2: 获取管理员信息
     */
    @POST("con/circle/getCircleRoles")
    suspend fun getCircleRoles(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleRolesBean>

    /**
     * 社区v2: 获取申请管理员信息
     */
    @POST("con/circle/applyManagerInfo")
    suspend fun applyManagerInfo(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<GetApplyManageBean>

    /**
     * 社区v2: 提交管理员申请信息
     */
    @POST("con/circle/applyManager")
    suspend fun applyManager(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 社区v2: 取消 管理员申请信息
     */
    @POST("con/circle/cancelApplyManager")
    suspend fun cancelApplyManager(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 创建圈子
     */
    @POST("con/community/circleCreate")
    suspend fun createCircle(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<Any>

    /**
     * 编辑圈子
     */
    @POST("con/community/circleEdit")
    suspend fun editCircle(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<Any>

    /**
     * 退出圈子
     */
    @POST("con/circle/quitCircle")
    suspend fun quitCircle(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 社区首页顶部
     */
    @POST("con/circle/communityIndex")
    suspend fun communityIndex(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleMainBean>


    @POST("/con/community/recommend")
    suspend fun communityTopic(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<CircleMainBean>


    /**
     * 管理员列表
     */
    @POST("con/circle/getStarsRole")
    suspend fun getStarsRole(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<CircleDialogBeanItem>>

    /**
     * 圈子 ：设置管理员
     */
    @POST("con/circle/setStarsRole")
    suspend fun setStarsRole(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 我的圈子 ：删除圈子已有成员
     */
    @POST("con/circle/deleteCircleUsers")
    suspend fun deleteCircleUsers(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 分享
     */
    @POST("con/share/callback")
    suspend fun shareCallBack(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 申请加精
     */
    @POST("con/posts/setGood")
    suspend fun postSetGood(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 删除帖子
     */
    @POST("con/posts/delete")
    suspend fun postDelete(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 下架(设置仅自己可见)
     */
    @POST("con/posts/setPrivate")
    suspend fun postPrivate(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     *不喜欢原因
     */
    @POST("con/reason/dislikeReason")
    suspend fun getDislikeReason(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<String>>

    /**
     * 帖子不喜欢
     */
    @POST("con/posts/addDislike")
    suspend fun dislikePost(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     *举报原因
     */
    @POST("con/reason/tipOffReason")
    suspend fun getTipOffReason(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<String>>

    /**
     * 帖子举报
     */
    @POST("con/posts/addTipOffs")
    suspend fun reportPost(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

    /**
     * 圈子类型
     */
    @POST("con/community/circleAllType")
    suspend fun circleTypes(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<CircleTypesBean>>
    /**
     * 圈子首页
    * */
    @POST("con/community/circle")
    suspend fun circleHome(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<CirceHomeBean>
    /**
     * 猜你喜欢
     */
    @POST("con/community/circleLike")
    suspend fun youLike(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<NewCircleDataBean>
    /**
     * 热门榜单分类
     */
    @POST("con/community/circleTop")
    suspend fun circleHotTypes(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<ArrayList<CirCleHotList>>
    /**
     * 热门榜单列表
     */
    @POST("con/community/circleTopList")
    suspend fun circleHotList(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<NewCircleDataBean>
    /**
     * 创建圈子 获取tag标签
     */
    @POST("con/community/circleCreateInfo")
    suspend fun circleCreateInfo(@HeaderMap headMap: Map<String, String>, @Body requestBody: RequestBody): CommonResponse<TagInfoBean>

    @POST("con/ads/list")
    suspend fun getRecommendTopic(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<AdBean>>




    @POST("con/community/postsAddressList")
    suspend fun getUserPostAdress(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<List<SerachUserAddress>>


    @POST("base/dict/getType")
    suspend fun getQuestionType(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<ArrayList<QuestionData>>



    /**
     * 发布提问
     */
    @POST("qa/createQuestion")
    suspend fun createQuestion(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<String>

    /**
     * 提问首页
     * */
    @POST("qa/index")
    suspend fun getInitQuestion(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<MechanicData>
    @POST("qa/qustions")
    suspend fun getRecommendQuestionList(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<HomeDataListBean<AskListMainData>>
    /**
     * 我/TA的问答
    * */
    @POST("/qa/personalQA")
    suspend fun personalQA(@HeaderMap headMap: Map<String, String>,@Body requestBody: RequestBody): CommonResponse<QuestionInfoBean>
    /**
     * 我/TA 的 提问/回答/被采纳
     * */
    @POST("/qa/qustionOfpersonal")
    suspend fun questionOfPersonal(@HeaderMap headMap: Map<String, String>,@Body requestBody: RequestBody): CommonResponse<QuestionInfoBean>
    /**
     * 技师邀请回答列表
     * */
    @POST("/qa/qustionOfInvite")
    suspend fun questionOfInvite(@HeaderMap headMap: Map<String, String>,@Body requestBody: RequestBody): CommonResponse<QuestionInfoBean>


    /**
     * 编辑技术详情
     * */
    @POST("/qa/techniciaPersonalInfo")
    suspend fun techniciaPersonalInfo(@HeaderMap headMap: Map<String, String>,@Body requestBody: RequestBody): CommonResponse<TechnicianData>

   /**
    * 更换技师个人资料
    * */

   @POST("/qa/updateTechniciaPersonalInfo")
   suspend fun updateTechniciaPersonalInfo(@HeaderMap headMap: Map<String, String>,@Body requestBody: RequestBody): CommonResponse<String>

    //分享成功回调
    @POST("/con/share/callback")
    suspend fun ShareBack(
        @HeaderMap headMap: Map<String, String>,
        @Body requestBody: RequestBody
    ): CommonResponse<Any>

}