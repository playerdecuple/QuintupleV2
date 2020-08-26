package com.DecupleProject.Core.Util;

import java.net.MalformedURLException;
import java.net.URL;

public class LinkUtility {

    public LinkUtility() {}

    public boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

}
