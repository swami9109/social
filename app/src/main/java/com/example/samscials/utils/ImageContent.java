package com.example.samscials.utils;

import android.net.Uri;

import com.example.samscials.model.GalleryImages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageContent {

    static final List<GalleryImages> list = new ArrayList<>();

    public  static void loadImages(File file){
        GalleryImages images = new GalleryImages();
        images.picUri = Uri.fromFile(file);
        addImages(images);
    }
    private static void addImages(GalleryImages images){
        list.add(0, images);
    }
    public static void loadSavedImages(File directory){
        File[] files = directory.listFiles();

        for (File file : files){
            String absolutePath = file.getAbsolutePath();
            String extension = absolutePath.substring(absolutePath.lastIndexOf("."));

            if (extension.equals(".jpg")|| extension.equals(".png")){
                loadImages(file);
            }
        }
    }
}
