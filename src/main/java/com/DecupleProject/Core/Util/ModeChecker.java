package com.DecupleProject.Core.Util;

import com.DecupleProject.Core.ReadFile;

public class ModeChecker {

    ReadFile r = new ReadFile();

    public ModeChecker() {

    }

    public boolean isTestMode() {
        String mode = r.readString("D:/Database/NowMode.txt");
        return mode != null && mode.equalsIgnoreCase("test");
    }

}
