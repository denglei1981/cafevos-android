package com.changanford.common.bean

data class HomePageBean(var topicList:ListMainBean<Topic>?=null,var  postList: ListMainBean<PostDataBean>?=null,var circleList:ListMainBean<NewCircleBean>?=null,var type:Int=-1,var total:Int=0){

}


data class MyCollectBean(var infoList:InfoBean?=null,var  postList: PostBean?=null,var actDataBean:AccBean?=null,var shopList:ShopBean?=null,var type:Int=-1,var total:Int=0){

}