package com.changanford.common.util.crash

import java.lang.reflect.Field
import java.lang.reflect.Method

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.util.crash.ReflexUtil
 * @Author:　 　
 * @Version : V1.0
 * @Date: 3/9/21 5:40 PM
 * @Description: 　
 * *********************************************************************************
 */
object ReflexUtil {
    internal fun getField(clazz: Class<*>, filedName: String): Field? {
        val getDeclaredField =
            Class::class.java.getDeclaredMethod("[getDeclaredField]", String::class.java)
        getDeclaredField.isAccessible = true
        return getDeclaredField.invoke(clazz, filedName) as Field?
    }

    internal fun getMethod(clazz: Class<*>, methodName: String, vararg params: Class<*>): Method? {
        val getDeclaredField = Class::class.java.getDeclaredMethod(
            "getDeclaredMethod",
            String::class.java,
            params.javaClass
        )
        getDeclaredField.isAccessible = true
        return getDeclaredField.invoke(clazz, methodName, params) as Method?
    }

    internal fun getFields(clazz: Class<*>): Array<Field> {
        val getDeclaredField = Class::class.java.getDeclaredMethod("getDeclaredFields")
        getDeclaredField.isAccessible = true
        return getDeclaredField.invoke(clazz) as Array<Field>
    }

    fun <T : Any> copyTo(fromObj: T, toObj: T) {
        val fields = getFields(fromObj.javaClass)
        fields.forEach {
            it.isAccessible = true
            it.set(toObj, it.get(fromObj))
        }

    }

}