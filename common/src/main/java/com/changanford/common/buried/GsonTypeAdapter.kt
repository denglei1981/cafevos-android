package com.changanford.common.buried

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * @Author: hpb
 * @Date: 2020/5/19
 * @Des:
 */
//String
class StringTypeAdapter : TypeAdapter<String>() {

    override fun write(writer: JsonWriter, value: String?) {
        try {
            if (value == null) {
                writer.nullValue()
                return
            }
            writer.value(value)
        } catch (e: Exception) {
        }
    }

    override fun read(reader: JsonReader): String {
        try {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return ""
            }
            return reader.nextString()
        } catch (e: Exception) {
        }
        return ""
    }

}

class DoubleTypeAdapter : TypeAdapter<Double>() {

    override fun write(writer: JsonWriter, value: Double?) {
        try {
            if (value == null) {
                writer.nullValue()
                return
            }
            writer.value(value)
        } catch (e: Exception) {
        }
    }

    override fun read(reader: JsonReader): Double {
        try {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return 0.0
            }
            return reader.nextDouble()
        } catch (e: Exception) {
        }
        return 0.0
    }

}


class IntTypeAdapter : TypeAdapter<Int>() {

    override fun write(writer: JsonWriter, value: Int?) {
        try {
            if (value == null) {
                writer.nullValue()
                return
            }
            writer.value(value)
        } catch (e: Exception) {
        }
    }

    override fun read(reader: JsonReader): Int {
        try {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return 0
            }
            return reader.nextInt()
        } catch (e: Exception) {
        }
        return 0
    }

}

class LongTypeAdapter : TypeAdapter<Long>() {

    override fun write(writer: JsonWriter, value: Long?) {
        try {
            if (value == null) {
                writer.nullValue()
                return
            }
            writer.value(value)
        } catch (e: Exception) {
        }
    }

    override fun read(reader: JsonReader): Long {
        try {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return 0
            }
            return reader.nextLong()
        } catch (e: Exception) {
        }
        return 0
    }

}

class NumberTypeAdapter : TypeAdapter<Number>() {

    override fun write(writer: JsonWriter, value: Number?) {
        try {
            if (value == null) {
                writer.nullValue()
                return
            }
            writer.value(value)
        } catch (e: Exception) {
        }
    }

    override fun read(reader: JsonReader): Number {
        try {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return 0
            }
            return reader.nextDouble().toInt()
        } catch (e: Exception) {
        }
        return 0
    }

}


class BooleanTypeAdapter : TypeAdapter<Boolean>() {

    override fun write(writer: JsonWriter, value: Boolean?) {
        try {
            if (value == null) {
                writer.nullValue()
                return
            }
            writer.value(value)
        } catch (e: Exception) {
        }
    }

    override fun read(reader: JsonReader): Boolean {
        try {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return false
            }
            return reader.nextBoolean()
        } catch (e: Exception) {
        }
        return false
    }

}

//
//class BaseBeanTypeAdapter : TypeAdapter<BaseBean<*>>() {
//    override fun write(writer: JsonWriter, value: BaseBean<*>?) {
//        try {
//            if (value == null) {
//                writer.nullValue()
//                return
//            }
//            writer.beginObject()
//            writer.name("code").value(value.code)
//            writer.name("data").value(value.data)
//            writer.name("encr").value(value.encr)
//            writer.name("msg").value(value.msg)
//            writer.name("msgId").value(value.msgId)
//            writer.name("timestamp").value(value.timestamp)
//            writer.endObject()
//        } catch (e: Exception) {
//        }
//    }
//
//    override fun read(reader: JsonReader): BaseBean<*> {
//
//    }
//}