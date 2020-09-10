package com.DecupleProject.Core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyFile {

    public CopyFile() {
    }

    public boolean copyTo(File to, File... from) {

        if (!to.isDirectory()) return false;

        for (File file : from) {

            if (file.isDirectory()) {
                return false;
            }

            FileInputStream is = null;
            FileOutputStream os = null;

            try {
                is = new FileInputStream(file);
                os = new FileOutputStream(to);

                byte[] bytes = new byte[4096];

                while (is.read(bytes) != -1) {
                    os.write(bytes, 0, is.read(bytes));
                }

                return true;
            } catch (Exception e) {
                new ExceptionReport(e);
                e.printStackTrace();
            } finally {
                try {
                    assert is != null && os != null;
                    is.close();
                    os.close();
                } catch (IOException e) {
                    new ExceptionReport(e);
                    e.printStackTrace();
                }
            }

        }

        return false;

    }

    public boolean copyTo(String toPath, String... fromPath) {

        File to = new File(toPath);
        File[] from = new File[fromPath.length];

        for (int i = 0; i < fromPath.length; i++) {
            from[i] = new File(fromPath[i]);
        }

        if (!to.isDirectory()) return false;

        for (File file : from) {

            if (file.isDirectory()) {
                return false;
            }

            FileInputStream is = null;
            FileOutputStream os = null;

            try {
                is = new FileInputStream(file);
                os = new FileOutputStream(to);

                byte[] bytes = new byte[4096];

                while (is.read(bytes) != -1) {
                    os.write(bytes, 0, is.read(bytes));
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    assert is != null && os != null;
                    is.close();
                    os.close();
                } catch (IOException e) {
                    new ExceptionReport(e);
                    e.printStackTrace();
                }
            }

        }

        return false;

    }

}
