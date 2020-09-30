package com.DecupleProject.Core;

import com.DecupleProject.Core.Util.SendSource;
import com.DecupleProject.Listener.DefaultListener;
import net.dv8tion.jda.api.entities.User;

import java.text.SimpleDateFormat;

public class ExceptionReport {

    public ExceptionReport(Exception e) {

        try {
            User owner = DefaultListener.owner;

            StringBuilder stackTrace = new StringBuilder();
            int more = 0;
            int traceCount = 0;

            for (StackTraceElement trace : e.getStackTrace()) {

                traceCount++;
                StringBuilder traceMsg = new StringBuilder();

                SendSource s = new SendSource(owner);

                traceMsg.append("\n").append("- at ").append(trace.getClassName())
                        .append(".")
                        .append(trace.getMethodName())
                        .append("(FILE : ").append(trace.getFileName())
                        .append(", LN : ").append(trace.getLineNumber()).append(")")
                        .append(s.returnSourceExp(trace.getClassName().replace(".", "/") + ".java",
                                trace.getLineNumber(), trace.getLineNumber()));

                if ((stackTrace.toString().length() + traceMsg.toString().length()) >= 1800) {
                    more++;
                } else {
                    stackTrace.append(traceMsg.toString());
                }

                if (traceCount == e.getStackTrace().length) {
                    if (more != 0) {
                        stackTrace.append("\n").append("...").append(" ").append(more).append(" more..");
                    }
                }
            }

            String msg = e.getClass().toString() + ": " + e.getMessage();

            if (msg.length() > 100) {
                msg = msg.substring(0, 97) + "...";
            }

            owner.openPrivateChannel().complete().sendMessage("```diff\n" + getTimeStamp(System.currentTimeMillis())
                    + " - Exception thrown : \n" + msg + "\n" + stackTrace + "\n\nCaused By: \n" + e.getCause() + "```").queue();
        } catch (IllegalStateException ex) {
            // ignore
        }
    }

    public String getTimeStamp(long time) {
        SimpleDateFormat dp = new SimpleDateFormat("[yyyy/MM/dd hh:mm:ss]");
        return dp.format(time);
    }

}
