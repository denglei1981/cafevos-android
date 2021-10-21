package com.changanford.home.news.data

import com.changanford.common.bean.AdBean
import com.changanford.common.bean.InfoDataBean

data class NewsExpandData (val recommendArticles:MutableList<InfoDataBean>, val ads:MutableList<AdBean>


)