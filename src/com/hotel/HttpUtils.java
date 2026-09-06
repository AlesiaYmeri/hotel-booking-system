package com.hotel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HttpUtils {

    private HttpUtils() {
    }

    public static Map<String, String> parseParams(String raw) {
        Map<String, String> map = new LinkedHashMap<>();
        if (raw == null || raw.isEmpty()) {
            return map;
        }
        String[] pairs = raw.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            int idx = pair.indexOf('=');
            String key;
            String value;
            if (idx >= 0) {
                key = decode(pair.substring(0, idx));
                value = decode(pair.substring(idx + 1));
            } else {
                key = decode(pair);
                value = "";
            }
            map.put(key, value);
        }
        return map;
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    public static String readBody(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int read;
        while ((read = is.read(chunk)) != -1) {
            buffer.write(chunk, 0, read);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    public static String escapeHtml(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
