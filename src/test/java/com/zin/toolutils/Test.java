package com.zin.toolutils;


/**
 * Created by zhujinming on 2017/6/23.
 */
public class Test {

    @org.junit.Test
    public void addition_isCorrect() throws Exception {
        addSpace("13718762478");
    }

    public void addSpace(String str) {
//        (str.length() / 3 == 0)

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("a").append("b").append("c").append("");
        System.out.println(stringBuffer.toString());
    }
}
