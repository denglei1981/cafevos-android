package com.changanford.circle.ui.compose

/**
 * @Author : wenke
 * @Time : 2022/6/9 0009
 * @Description : PostCompose
 */
/**
 * 视频帖子详情
* */
//@Composable
//fun PostDetailsCompose(dataBean: PostsDetailBean?){
//    dataBean?.apply {
//        Column(modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(horizontal = 20.dp)) {
//            authorBaseVo?.apply {
//                Row(modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
//                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
//                        .weight(1f)
//                        .padding(top = 10.dp)) {
//                        WImage(imgUrl = avatar, modifier = Modifier
//                            .size(40.dp)
//                            .clip(CircleShape))
//                        Spacer(modifier = Modifier.width(12.dp))
//                        //昵称、是否车主
//                        Text(buildAnnotatedString {
//                            withStyle(style = SpanStyle(color = colorResource(R.color.color_01), fontSize = 15.sp)) {
//                                append(nickname)
//                            }
//                            getMemberNames()?.let {
//                                append("/n")
//                                withStyle(style = SpanStyle(color = colorResource(R.color.color_00095B), fontSize = 11.sp)) {
//                                    append(it)
//                                }
//                            }
//                        })
//                        Spacer(modifier = Modifier.width(5.dp))
//                        //勋章 暂时只能佩戴一个
//                        Row {
//                            imags.forEach {
//                                Image(painter = rememberImagePainter(data = GlideUtils.handleNullableUrl(
//                                    it.img) ?: R.mipmap.head_default,
//                                    builder = {placeholder(R.mipmap.head_default)}),
//                                    contentScale = ContentScale.Crop,
//                                    contentDescription =null,modifier = Modifier
//                                        .size(20.dp)
//                                        .clip(CircleShape)
//                                        .clickable {
//                                            JumpUtils.instans?.jump(it.jumpDataType,
//                                                it.jumpDataValue)
//                                        })
//                            }
//                        }
//                        Spacer(modifier = Modifier.width(13.dp))
//                        //是否关注
//                        Box(modifier = Modifier
//                            .width(60.dp)
//                            .height(25.dp)
//                            .background(color = colorResource(R.color.color_E5),
//                                shape = RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
//                            Text(text = if(isFollow==1)"已关注" else "关注", fontSize = 12.sp, color = colorResource(R.color.color_00095B))
//                        }
//                    }
//                    //是否是精华帖
//                    if(dataBean.isGood==1) Image(painter = painterResource(R.mipmap.ic_essence), contentDescription =null )
//                }
//            }
//            Spacer(modifier = Modifier.height(15.dp))
//            //标题
//            Text(text = title, fontSize = 18.sp, color = colorResource(R.color.color_33))
//            Spacer(modifier = Modifier.height(21.dp))
//            Row {
//                //来自那个圈子
//                Text(text = if(!TextUtils.isEmpty(circleName))"来自" else "", fontSize = 14.sp, color = colorResource(R.color.color_33))
//                Text(text = circleName?:"",fontSize = 14.sp, color = colorResource(R.color.color_00095B), modifier = Modifier
//                    .weight(1f)
//                    .clickable {
//                        val bundle = Bundle()
//                        bundle.putString("circleId", "$circleId")
//                        startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
//                    })
//                //位置
//                if (!TextUtils.isEmpty(city)) {
//                    Image(painter = painterResource(com.changanford.circle.R.mipmap.circle_location_details), contentDescription ="" )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(text = showCity(), fontSize = 13.sp, color = colorResource(R.color.color_cc))
//                }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            //内容
//            Text(text = content?:"", fontSize = 15.sp, color = colorResource(R.color.color_01), lineHeight = 20.sp)
//            Spacer(modifier = Modifier.height(19.dp))
//            //topicName
//            topicName?.let {
//                Row(modifier = Modifier
//                    .height(40.dp)
//                    .padding(10.dp)
//                    .background(color = colorResource(R.color.color_66F2F4F9),
//                        shape = RoundedCornerShape(20.dp)), verticalAlignment = Alignment.CenterVertically) {
//                    Image(painter = painterResource(R.mipmap.ic_jh), contentDescription =null )
//                    Spacer(modifier = Modifier.width(9.dp))
//                    Text(text = it, fontSize = 13.sp, color = colorResource(R.color.color_8195C8))
//                }
//            }
//            //标签
//            if(tags!=null&&tags.size>0){
//                Spacer(modifier = Modifier.height(10.dp))
//                Row {
//                    tags.forEach {
//                        Box(contentAlignment = Alignment.Center, modifier = Modifier
//                            .width(68.dp)
//                            .height(23.dp)
//                            .background(colorResource(R.color.color_FAFBFD),
//                                shape = RoundedCornerShape(12.dp))) {
//                            Text(text = it.tagName, fontSize = 12.sp, color= colorResource(R.color.color_8195C8), overflow = TextOverflow.Ellipsis, maxLines = 1)
//                        }
//                        Spacer(modifier = Modifier.width(5.dp))
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(20.dp))
//        }
//    }
//
//}