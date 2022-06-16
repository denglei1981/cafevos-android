package com.changanford.common.bean

data class HomePageBean(var topicList:ListMainBean<Topic>?=null,var  postList: ListMainBean<PostDataBean>?=null,var circleList:ListMainBean<NewCircleBean>?=null){

}