package com.DecupleProject.API;

import com.DecupleProject.Core.Util.LinkUtility;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Youtube {

    private YouTube youTube;

    public Youtube() {
        try {
            youTube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), null).setApplicationName("Quintuple").build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String searchYoutube(String search) {

        LinkUtility l = new LinkUtility();
        if (l.isURL(search)) return search;

        try {
            List<SearchResult> results = youTube.search()
                    .list("id,snippet")
                    .setQ(search)
                    .setMaxResults(1L)
                    .setType("video")
                   // .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
                    .setFields("items(id/videoId)")
                    .setKey("AIzaSyDM1sK9iC6EywCHgzBYtbgDxapWTXlzvIU")
                    .execute()
                    .getItems();

            if (!results.isEmpty()) {
                String videoId = results.get(0).getId().getVideoId();
                return "https://www.youtube.com/watch?v=" + videoId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getTitle(String url) {

        String title = "";

        try {
            if (url != null) {
                URL urlR = new URL("http://www.youtube.com/oembed?url=" + url + "&format=json");
                title = new JSONObject(IOUtils.toString(urlR, StandardCharsets.UTF_8)).getString("title");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return title;

    }

    public String getThumbnail(String url) {

        String thumbnailUrl = "";

        try {
            if (url != null) {
                URL urlR = new URL("http://www.youtube.com/oembed?url=" + url + "&format=json");
                thumbnailUrl = new JSONObject(IOUtils.toString(urlR, StandardCharsets.UTF_8)).getString("thumbnail_url");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return thumbnailUrl;

    }

}
