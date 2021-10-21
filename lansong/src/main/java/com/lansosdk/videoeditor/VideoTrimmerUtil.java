package com.lansosdk.videoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoTrimmerUtil {

//  public static void trim(Context context, String inputFile, String outputFile, long startMs, long endMs, final VideoTrimListener callback) {
//    final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//    final String outputName = "trimmedVideo_" + timeStamp + ".mp4";
//    outputFile = outputFile + "/" + outputName;
////    outputFile = outputFile
//    String start = convertSecondsToTime(startMs / 1000);
//    String duration = convertSecondsToTime((endMs - startMs) / 1000);
//    //String start = String.valueOf(startMs);
//    //String duration = String.valueOf(endMs - startMs);
//
//    /** 裁剪视频ffmpeg指令说明：
//     * ffmpeg -ss START -t DURATION -i INPUT -codec copy -avoid_negative_ts 1 OUTPUT
//     -ss 开始时间，如： 00:00:20，表示从20秒开始；
//     -t 时长，如： 00:00:10，表示截取10秒长的视频；
//     -i 输入，后面是空格，紧跟着就是输入视频文件；
//     -codec copy -avoid_negative_ts 1 表示所要使用的视频和音频的编码格式，这里指定为copy表示原样拷贝；
//     INPUT，输入视频文件；
//     OUTPUT，输出视频文件
//     */
//    //TODO: Here are some instructions
//    //https://trac.ffmpeg.org/wiki/Seeking
//    //https://superuser.com/questions/138331/using-ffmpeg-to-cut-up-video
//
//    String cmd =  "-ss " + start + " -t " + duration + " -accurate_seek" + " -i " + inputFile + " -codec copy -avoid_negative_ts 1 " + outputFile;
//    //String cmd = "-ss " + start + " -i " + inputFile + " -ss " + start + " -t " + duration + " -vcodec copy " + outputFile;
//    //{"ffmpeg", "-ss", "" + startTime, "-y", "-i", inputFile, "-t", "" + induration, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", outputFile}
//    //String cmd = "-ss " + start + " -y " + "-i " + inputFile + " -t " + duration + " -vcodec " + "mpeg4 " + "-b:v " + "2097152 " + "-b:a " + "48000 " + "-ac " + "2 " + "-ar " + "22050 "+ outputFile;
//    String[] command = cmd.split(" ");
//    try {
//      final String tempOutFile = outputFile;
//      FFmpeg.getInstance(context).execute(command, new ExecuteBinaryResponseHandler() {
//
//        @Override public void onSuccess(String s) {
//          callback.onFinishTrim(tempOutFile);
//        }
//
//        @Override public void onStart() {
//          callback.onStartTrim();
//        }
//      });
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }


  public static String convertSecondsToTime(long seconds) {
    String timeStr = null;
    int hour = 0;
    int minute = 0;
    int second = 0;
    if (seconds <= 0) {
      return "00:00";
    } else {
      minute = (int) seconds / 60;
      if (minute < 60) {
        second = (int) seconds % 60;
        timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
      } else {
        hour = minute / 60;
        if (hour > 99) return "99:59:59";
        minute = minute % 60;
        second = (int) (seconds - hour * 3600 - minute * 60);
        timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
      }
    }
    return timeStr;
  }

  private static String unitFormat(int i) {
    String retStr = null;
    if (i >= 0 && i < 10) {
      retStr = "0" + Integer.toString(i);
    } else {
      retStr = "" + i;
    }
    return retStr;
  }
}
