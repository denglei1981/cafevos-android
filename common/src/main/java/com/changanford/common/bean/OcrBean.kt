package com.changanford.common.bean

data class OcrBean(
    val address: String,
    val birth: String,
    val card_region: List<CardRegion>,
    val face_rect: FaceRect,
    val face_rect_vertices: List<FaceRectVertice>,
    val name: String,
    val nationality: String,
    val num: String,
    val sex: String,
    val addr: String,
    val config_str: String,
    val engine_num: String,
    val issue_date: String,
    val model: String,
    val owner: String,
    val plate_num: String,
    val register_date: String,
    val request_id: String,
    val success: Boolean,
    val use_character: String,
    val vehicle_type: String,
    var picUrl:String,
    val vin: String
)

data class CardRegion(
    val x: String,
    val y: String
)

data class FaceRect(
    val angle: String,
    val center: Center,
    val size: Size
)

data class FaceRectVertice(
    val x: String,
    val y: String
)

data class Center(
    val x: String,
    val y: String
)

data class Size(
    val height: String,
    val width: String
)

/**
 * Ocr识别提交数据
 * imgExt:图片地址/base64
 * imgType:  HTTP_URL(1, "网络地址"),     BASE64(2, "BASE64");
 *  ocrSceneType: VIN(1, "车架号"),     ID_CARD(2, "身份证"),     DRIVER_LICENCE(3, "驾驶证"),     WALK_LICENCE(4, "行驶证");
 *  INVOICE(4, "发票");
 *  path : 上传图片地址（无前缀）
 */

//data class OcrRequestBean(
//    var imgExt: String,
//    var ocrSceneType: String,
//    var path: String,
//    var imgType: String = "HTTP_URL"
//)