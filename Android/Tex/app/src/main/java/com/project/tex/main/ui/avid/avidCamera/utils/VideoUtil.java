package com.project.tex.main.ui.avid.avidCamera.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class VideoUtil {

    private static final String TAG = VideoUtil.class.getSimpleName();
    public static final int VIDEO_MAX_DURATION = 60;
    public static final String POSTFIX = ".jpeg";
    private static final String TRIM_PATH = "tex_video";
    private static final String THUMB_PATH = "thumb";

    /**
     * Mix AAC and MP4 [replaces video's audio track]
     *
     * @param aacPath .aac
     * @param mp4Path .mp4
     * @param outPath .mp4
     */
    public static boolean muxAacMp4(String aacPath, String mp4Path, String outPath) {
        boolean flag = false;
        try {
            AACTrackImpl aacTrack = new AACTrackImpl(new FileDataSourceImpl(aacPath));
            Movie videoMovie = MovieCreator.build(mp4Path);
            Track videoTracks = null;// Get the video-only portion of a video
            for (Track videoMovieTrack : videoMovie.getTracks()) {
                if ("vide".equals(videoMovieTrack.getHandler())) {
                    videoTracks = videoMovieTrack;
                }
            }

            Movie resultMovie = new Movie();
            resultMovie.addTrack(videoTracks);// video section
            resultMovie.addTrack(aacTrack);// audio part

            Container out = new DefaultMp4Builder().build(resultMovie);
            FileOutputStream fos = new FileOutputStream(new File(outPath));
            out.writeContainer(fos.getChannel());
            fos.close();
            flag = true;
            Log.e("update_tag", "merge finish");
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * Mix M4A and MP4 [replaces video's audio track]
     *
     * @param m4aPath .m4a[can also be used.mp4]
     * @param mp4Path .mp4
     * @param outPath .mp4
     */
    public static boolean muxM4AMp4(String m4aPath, String mp4Path, String outPath)
            throws IOException {
        Movie audioMovie = MovieCreator.build(m4aPath);
        Track audioTracks = null;// Get the audio-only portion of a video
        for (Track audioMovieTrack : audioMovie.getTracks()) {
            if ("soun".equals(audioMovieTrack.getHandler())) {
                audioTracks = audioMovieTrack;
            }
        }

        Movie videoMovie = MovieCreator.build(mp4Path);
        Track videoTracks = null;// Get the video-only portion of a video
        for (Track videoMovieTrack : videoMovie.getTracks()) {
            if ("vide".equals(videoMovieTrack.getHandler())) {
                videoTracks = videoMovieTrack;
            }
        }

        Movie resultMovie = new Movie();
        resultMovie.addTrack(videoTracks);// video section
        resultMovie.addTrack(audioTracks);// audio part

        Container out = new DefaultMp4Builder().build(resultMovie);
        FileOutputStream fos = new FileOutputStream(new File(outPath));
        out.writeContainer(fos.getChannel());
        fos.close();
        return true;
    }

    public static String getVideoFilePath(String url) {

        if (TextUtils.isEmpty(url) || url.length() < 5)
            return "";

        if (url.substring(0, 4).equalsIgnoreCase("http")) {
        } else
            url = "file://" + url;

        return url;
    }

    public static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0)
            return "00:00";
        else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = (int) (seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * Crop video local path
     *
     * @param context
     * @param dirName
     * @param fileNamePrefix
     * @return
     */
    public static String getTrimmedVideoPath(Context context, String dirName, String
            fileNamePrefix) {
        String finalPath = "";
        String dirPath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dirPath = context.getExternalCacheDir() + File.separator + dirName; // /mnt/sdcard/Android/data/<package name>/files/...
        } else {
            dirPath = context.getCacheDir() + File.separator + dirName; // /data/data/<package name>/files/...
        }
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        finalPath = file.getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String outputName = fileNamePrefix + timeStamp + ".mp4";
        finalPath = finalPath + "/" + outputName;
        return finalPath;
    }

    /**
     * Crop video local directory path
     */
    public static String getTrimmedVideoDir(Context context, String dirName) {
        String dirPath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dirPath = context.getExternalCacheDir() + File.separator
                    + dirName; // /mnt/sdcard/Android/data/<package name>/files/...
        } else {
            dirPath = context.getCacheDir() + File.separator
                    + dirName; // /data/data/<package name>/files/...
        }
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dirPath;
    }

    public static String saveImageToSDForEdit(Bitmap bmp, String dirPath, String fileName) {
        if (bmp == null) {
            return "";
        }
        File appDir = new File(dirPath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                bmp.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 80, fos);
            } else {
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static boolean deleteFile(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; ++i) {
                    deleteFile(files[i]);
                }
            }
        }
        return f.delete();
    }

    public static String getSaveEditThumbnailDir(Context context) {
        String state = Environment.getExternalStorageState();
        File rootDir =
                state.equals(Environment.MEDIA_MOUNTED) ? context.getExternalCacheDir()
                        : context.getCacheDir();
        File folderDir = new File(rootDir.getAbsolutePath() + File.separator + TRIM_PATH + File.separator + THUMB_PATH);
        if (folderDir == null) {
            folderDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "videoeditor" + File.separator + "picture");
        }
        if (!folderDir.exists() && folderDir.mkdirs()) {

        }
        return folderDir.getAbsolutePath();
    }

}
