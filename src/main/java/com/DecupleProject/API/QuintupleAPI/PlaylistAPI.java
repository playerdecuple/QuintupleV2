package com.DecupleProject.API.QuintupleAPI;

import com.DecupleProject.Core.UserDatabaseService;
import com.DecupleProject.Listener.DefaultListener;
import org.json.JSONObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PlaylistAPI extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("D");

        String requestUrl = request.getRequestURI();
        String id = requestUrl.substring("/playlists/".length());

        UserDatabaseService us = new UserDatabaseService(DefaultListener.jda.retrieveUserById(id).complete());

        if (us.getUserPlayLists() != null) {
            StringBuilder sb = new StringBuilder("{\n");

            for (int p = 0; p < us.getUserPlayLists().length; p++) {
                sb.append("\"playlist").append(p).append("\": ").append(JSONObject.quote(us.getUserId())).append("\n");
            }

            response.getOutputStream().println(sb.toString());
        } else {
            response.getOutputStream().println("{}");
        }

    }

}
