package com.CAM.DataCollection;

import com.CAM.HelperTools.DownloadListener;
import com.CAM.HelperTools.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class FileDownloader {

    private String downloadLocation;
    private static ArrayList<DownloadListener> listeners = new ArrayList<>();
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public FileDownloader(String downloadLocation){
        this.downloadLocation = downloadLocation;
    }

    public static void listen(DownloadListener listener){
        listeners.add(listener);
    }

    public void notifyAllListeners(double progress){
        for(DownloadListener listener : listeners){
            listener.notify(progress);
        }
    }

    public void notifyAllListenersMultiDownload(double progress, double totalProgress){
        for(DownloadListener listener : listeners){
            listener.notifyMultipleDownload(progress, totalProgress);
        }
    }

    public void downloadFile(String stringUrl, String fileName) {
        File file = new File(downloadLocation + "/" + fileName);

        try {
            URL url = new URL(stringUrl);
            FileUtils.copyURLToFile(url, file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void downloadFileMonitored(String stringUrl, String fileName) {

        File file = new File(downloadLocation + "/" + fileName);
        URL url = null;
        URLConnection urlConnection = null;
        InputStream source = null;
        try {
            url = new URL(stringUrl);
            urlConnection = url.openConnection();
            urlConnection.connect();
            source = url.openStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int fileSize = urlConnection.getContentLength();
        try {


            final FileOutputStream output = FileUtils.openOutputStream(file);
            try {

                final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                long count = 0;
                int n = 0;
                while (EOF != (n = source.read(buffer))) {
                    output.write(buffer, 0, n);
                    count += n;
                    double progress = (count*1.0/fileSize);
                    notifyAllListeners(progress);
                }

                output.close(); // don't swallow close Exception if copy completes normally
            } finally {
                IOUtils.closeQuietly(output);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    public void downloadMultipleFilesMonitored(HashMap<String, String> files){
        HashMap<String, FileDownload> fileSizes = establishFileSize(files);

        long sumFileSize = 0;
        for(String key : fileSizes.keySet()){
            sumFileSize += fileSizes.get(key).fileSize;
        }
        System.out.println("sumFileSize: " + sumFileSize);
        long totalCount = 0;

        for(String stringUrl : files.keySet()){
            String fileName = files.get(stringUrl);
            URL url = fileSizes.get(stringUrl).url;
            int fileSize = fileSizes.get(stringUrl).fileSize;

            File file = new File(downloadLocation + "/" + fileName);
            InputStream source = null;
            try {
                source = url.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                final FileOutputStream output = FileUtils.openOutputStream(file);
                try {

                    final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    long count = 0;
                    int n = 0;
                    while (EOF != (n = source.read(buffer))) {
                        output.write(buffer, 0, n);
                        count += n;
                        totalCount += n;
                        double progress = (count*1.0/fileSize);
                        double totalProgress = (totalCount*1.0/sumFileSize);
                        notifyAllListenersMultiDownload(progress, totalProgress);
                    }

                    output.close(); // don't swallow close Exception if copy completes normally
                } finally {
                    IOUtils.closeQuietly(output);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(source);
            }

            System.out.println("Finished download");
        }

    }

    private HashMap<String, FileDownload> establishFileSize(HashMap<String, String> files){
        HashMap<String, FileDownload> fileSizes = new HashMap<>();
        for(String stringUrl : files.keySet()){

            URL url = null;
            URLConnection urlConnection = null;
            try {
                url = new URL(stringUrl);
                urlConnection = url.openConnection();
                urlConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            fileSizes.put(stringUrl, new FileDownload(urlConnection.getContentLength(), url));
        }
        return fileSizes;
    }



    private class FileDownload {
        int fileSize;
        URL url;

        public FileDownload(int fileSize, URL url){
            this.fileSize = fileSize;
            this.url = url;
        }
    }



}


