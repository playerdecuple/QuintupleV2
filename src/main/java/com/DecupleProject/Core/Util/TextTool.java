package com.DecupleProject.Core.Util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TextTool {

    // Hangeul Method
    public String qwertyToHangeul(String input) throws ScriptException {
        ScriptEngineManager mgr = new ScriptEngineManager();

        ScriptEngine engine = mgr.getEngineByName("JavaScript");

        return String.valueOf(engine.eval("var e2k = (function() {\n" +
                "var en_h = \"rRseEfaqQtTdwWczxvg\";\n" +
                "var reg_h = \"[\" + en_h + \"]\";\n" +
                "\n" +
                "var en_b = {k:0,o:1,i:2,O:3,j:4,p:5,u:6,P:7,h:8,hk:9,ho:10,hl:11,y:12,n:13,nj:14,np:15,nl:16,b:17,m:18,ml:19,l:20};\n" +
                "var reg_b = \"hk|ho|hl|nj|np|nl|ml|k|o|i|O|j|p|u|P|h|y|n|b|m|l\";\n" +
                "\n" +
                "var en_f = {\"\":0,r:1,R:2,rt:3,s:4,sw:5,sg:6,e:7,f:8,fr:9,fa:10,fq:11,ft:12,fx:13,fv:14,fg:15,a:16,q:17,qt:18,t:19,T:20,d:21,w:22,c:23,z:24,x:25,v:26,g:27};\n" +
                "var reg_f = \"rt|sw|sg|fr|fa|fq|ft|fx|fv|fg|qt|r|R|s|e|f|a|q|t|T|d|w|c|z|x|v|g|\";\n" +
                "\n" +
                "var reg_exp = new RegExp(\"(\"+reg_h+\")(\"+reg_b+\")((\"+reg_f+\")(?=(\"+reg_h+\")(\"+reg_b+\"))|(\"+reg_f+\"))\",\"g\");\n" +
                "\n" +
                "var replace = function(str,h,b,f) {\n" +
                "return String.fromCharCode(en_h.indexOf(h) * 588 + en_b[b] * 28 + en_f[f] + 44032);\n" +
                "};\n" +
                "\n" +
                "return (function(str) {\n" +
                "return str.replace(reg_exp,replace);\n" +
                "});\n" +
                "})();" +
                "" +
                "e2k(\"" + input + "\")"));
    }

    // Number Method
    public long nextLong(long bound) {
        Random r = new Random();
        long bits, val;

        do {
            bits = (r.nextLong() << 1) >>> 1;
            val = bits & bound;
        } while (bits - val + (bound - 1) < 0L);

        return val;
    }

    public long nextLong(long boundFrom, long boundTo) {
        return ThreadLocalRandom.current().nextLong(boundFrom, boundTo);
    }

    public String addKoreanUnitsToNumber(long number) {
        String nStr = String.valueOf(number);
        StringBuilder result = new StringBuilder();

        char[] c = new char[nStr.length()];
        for (int i = nStr.length() - 1; i >= 0; i--) {
            c[i] = nStr.charAt(i);
            result.insert(0, c[i]);

            if (i == nStr.length() - 4 && i != 0) {
                result.insert(0, "만 ");
            } else if (i == nStr.length() - 8 && i != 0) {
                result.insert(0, "억 ");
            } else if (i == nStr.length() - 12 && i != 0) {
                result.insert(0, "조 ");
            } else if (i == nStr.length() - 16 && i != 0) {
                result.insert(0, "경 ");
            }
        }

        return result.toString()
                .replace("0000경 ", "")
                .replace("0000조 ", "")
                .replace("0000억 ", "")
                .replace("0000만 ", "")
                .replace(" 0000", "");
    }

}
