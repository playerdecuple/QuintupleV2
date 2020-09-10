package com.DecupleProject.Core;

import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.entities.User;

import java.text.SimpleDateFormat;

public class ExceptionReport {

    public ExceptionReport(Exception e) {
        try {
            User owner = DefaultListener.owner;
            owner.openPrivateChannel().complete().sendMessage("```" + getTimeStamp(System.currentTimeMillis())
                    + " - Exception thrown : \n" + e.getMessage() + "\n\nCaused By: \n" + e.getCause() + "```").queue();
        } catch (IllegalStateException ex) {
            // ignore
        }
    }

    public String getTimeStamp(long time) {
        SimpleDateFormat dp = new SimpleDateFormat("[yyyy/MM/dd hh:mm:ss]");
        return dp.format(time);
    }

}
