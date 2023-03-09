package com.anjiplus.template.gaea.business.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {
    final static Base64.Encoder encoder = Base64.getEncoder();
    final static Base64.Decoder decoder = Base64.getDecoder();

    public static String encode(String text) {
        return encoder.encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String encodedText) {
        return new String(decoder.decode(encodedText), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        String str = "test1234";

        System.out.println("编码后的字符串为：");
        System.out.println(Base64Util.encode(str));

        System.out.println("解码后的字符串为：");
        System.out.println(Base64Util.decode(Base64Util.encode(str)));
    }
}
