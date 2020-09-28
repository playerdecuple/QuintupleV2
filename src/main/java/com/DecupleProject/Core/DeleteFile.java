package com.DecupleProject.Core;


import java.io.File;

public class DeleteFile {

    public DeleteFile() {
    }

    public void deleteFile(File f) {

        System.gc();
        System.runFinalization();

        try {
            if (f.exists()) {

                if (f.isDirectory()) {
                    File[] folder_list = f.listFiles(); // Get file lists

                    for (File file : folder_list) {
                        if (file.isFile()) {
                            file.delete();
                        } else {
                            deleteFile(file); // Restart this method
                        }
                        file.delete();
                    }

                    boolean deleted = f.delete(); // Delete 'path' directory
                    if (!deleted) {
                        System.out.println("Bot can't deleted file(s). Path : " + f.getPath());
                    }
                } else {
                    f.delete();
                }
            }
        } catch (Exception e) {
            new ExceptionReport(e);
            e.getStackTrace();
        }

    }

    public void deleteFile(String p) {

        System.gc();
        System.runFinalization();

        File f = new File(p);

        try {
            if (f.exists()) {
                File[] folder_list = f.listFiles(); // Get file lists

                for (File file : folder_list) {
                    if (file.isFile()) {
                        boolean deleted = file.delete();
                    } else {
                        deleteFile(file); // Restart this method
                    }
                    boolean deleted = file.delete();
                }

                boolean deleted = f.delete(); // Delete 'path' directory
                if (!deleted) {
                    System.out.println("Bot can't deleted file(s). Path : " + f.getPath());
                }
            }
        } catch (Exception e) {
            new ExceptionReport(e);
            e.getStackTrace();
        }

    }

    /* Never used codes yet.

    public void deleteFile(File ... fs) {

        for (File f : fs) {
            System.gc();
            System.runFinalization();

            try {
                if (f.exists()) {
                    File[] folder_list = f.listFiles(); // Get file lists

                    for (File file : folder_list) {
                        if (file.isFile()) {
                            boolean deleted = file.delete();
                            if (!deleted) return;
                        } else {
                            deleteFile(file); // Restart this method
                        }
                        boolean deleted = file.delete();
                        if (!deleted) return;
                    }

                    boolean deleted = f.delete(); // Delete 'path' directory
                    if (!deleted) {
                        System.out.println("Bot can't deleted file(s). Path : " + f.getPath());
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

    }

    public void deleteFile(String ... ps) {

        for (String p : ps) {

            File f = new File(p);

            System.gc();
            System.runFinalization();

            try {
                if (f.exists()){
                    File[] folder_list = f.listFiles(); // Get file lists

                    for (File file : folder_list) {
                        if (file.isFile()) {
                            boolean deleted = file.delete();
                            if (!deleted) return;
                        } else {
                            deleteFile(file); // Restart this method
                        }
                        boolean deleted = file.delete();
                        if (!deleted) return;
                    }

                    boolean deleted = f.delete(); // Delete 'path' directory
                    if (!deleted) {
                        System.out.println("Bot can't deleted file(s). Path : " + f.getPath());
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }

        }

    }

     */

}