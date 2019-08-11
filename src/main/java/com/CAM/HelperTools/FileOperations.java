package com.CAM.HelperTools;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileOperations {

    public static List<FileHeader> unzip(String source, String destination){
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
            return (List<FileHeader>) zipFile.getFileHeaders();
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static boolean deleteDirectory(String path){
        File dir = new File(path);
        Log.verbose(path + " is valid directory: " + dir.exists());
        if(!dir.exists()){
            return false;
        }
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean deleteFile(String path){
        File file = new File(path);
        Log.verbose(path + " is valid file: " + file.exists());
        return file.delete();
    }

    public static String getFileVersion(String path) {
        //TODO: Clean this mess up
        String version = null;
        try {
            Runtime rt = Runtime.getRuntime();
            String commandPrefix = "wmic datafile where name=";
            String commandSuffix = " get Version /value";
            String fullCommand = commandPrefix + convertPath(path) + commandSuffix;
            String[] commands = {"cmd", "/c",  fullCommand};

            Process pr = rt.exec(commands);

            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line = null;
            while ((line = input.readLine()) != null) {
                if(!line.contains("Version")){
                    continue;
                }
                version = line.split("=")[1];
            }

            int exitVal = pr.waitFor();
            Log.verbose("Exited with error code " + exitVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    private static String convertPath(String path){

        String[] parts = path.split("\\\\");

        String frankenstein = "\"" + parts[0];

        for(int i=1; i<parts.length; i++){
            frankenstein = frankenstein + "\\\\" + parts[i];
        }
        frankenstein = frankenstein + "\"";

        return frankenstein;
    }


}