package com.DecupleProject.Core;

import java.io.File;
import java.io.FileWriter;

public class WriteFile {

    public WriteFile() {

    }

    public void writeString(File f, String value) {
        try {
            FileWriter fW = new FileWriter(f.getPath());
            fW.write(value);
            fW.flush();
            fW.close();
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }
    }

    public void writeStringToFiles(String value, File... f) {
        try {
            for (File file : f) {
                FileWriter fW = new FileWriter(file.getPath());
                fW.write(value);
                fW.flush();
                fW.close();
            }
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }
    }

    public void writeLong(File f, Long value) {
        try {
            FileWriter fW = new FileWriter(f.getPath());
            fW.write(String.valueOf(value));
            fW.flush();
            fW.close();
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }
    }

    public void writeInt(File f, int value) {
        try {
            FileWriter fW = new FileWriter(f.getPath());
            fW.write(String.valueOf(value));
            fW.flush();
            fW.close();
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }
    }

    public void writeString(String path, String value) {
        try {
            FileWriter fW = new FileWriter(path);
            fW.write(value);
            fW.flush();
            fW.close();
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }
    }

    public void writeLong(String path, Long value) {
        try {
            FileWriter fW = new FileWriter(path);
            fW.write(String.valueOf(value));
            fW.flush();
            fW.close();
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }
    }

    public void writeInt(String path, int value) {
        try {
            FileWriter fW = new FileWriter(path);
            fW.write(String.valueOf(value));
            fW.flush();
            fW.close();
        } catch (Exception e) {
            new ExceptionReport(e);
            e.printStackTrace();
        }
    }

}
