package com.DecupleProject.Core;

import java.io.File;

public class DeleteFile {

    public DeleteFile() {}

    public void deleteFile(File f) {

        System.gc();
        System.runFinalization();

        try {
            if (f.exists()){
                File[] folder_list = f.listFiles(); // Get file lists

                for (int i = 0; i < folder_list.length; i++) {
                    if (folder_list[i].isFile()) {
                        folder_list[i].delete();
                    } else {
                        deleteFile(folder_list[i]); // Restart this method
                    }
                    folder_list[i].delete();
                }

                f.delete(); // Delete 'path' directory
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    public void deleteFile(String p) {

        System.gc();
        System.runFinalization();

        File f = new File(p);

        try {
            if (f.exists()){
                File[] folder_list = f.listFiles(); // Get file lists

                for (int i = 0; i < folder_list.length; i++) {
                    if (folder_list[i].isFile()) {
                        folder_list[i].delete();
                    } else {
                        deleteFile(folder_list[i]); // Restart this method
                    }
                    folder_list[i].delete();
                }

                f.delete(); // Delete 'path' directory
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    public void deleteFile(File ... fs) {

        for (File f : fs) {

            System.gc();
            System.runFinalization();

            try {
                if (f.exists()){
                    File[] folder_list = f.listFiles(); // Get file lists

                    for (int i = 0; i < folder_list.length; i++) {
                        if (folder_list[i].isFile()) {
                            folder_list[i].delete();
                        } else {
                            deleteFile(folder_list[i]); // Restart this method
                        }
                        folder_list[i].delete();
                    }

                    f.delete(); // Delete 'path' directory
                }
            } catch (Exception e) {
                e.getStackTrace();
            }

        }

    }

    public void deleteFile(String ... ps) {

        for (String p : ps) {

            File f = new File(p);

            try {
                if (f.exists()){
                    File[] folder_list = f.listFiles(); // Get file lists

                    for (int i = 0; i < folder_list.length; i++) {
                        if (folder_list[i].isFile()) {
                            folder_list[i].delete();
                        } else {
                            deleteFile(folder_list[i]); // Restart this method
                        }
                        folder_list[i].delete();
                    }

                    f.delete(); // Delete 'path' directory
                }
            } catch (Exception e) {
                e.getStackTrace();
            }

        }

    }

}