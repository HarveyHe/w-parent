package com.harvey.w.core.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harvey.w.core.bean.FileToDownload;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class HttpUtils {

    /**
     * 把map参数转为url query参数
     * @param queryParam 参数
     * @param skipNullValue 是否跳过空值
     * @param isEncodeParam 是否encode参数值
     * @return query string
     */
    public static String toQueryString(Map<?, ?> queryParam, boolean skipNullValue, boolean isEncodeParam) {
        if (MapUtils.isEmpty(queryParam)) {
            return "";
        }
        StrBuilder builder = new StrBuilder();
        for (Iterator<?> it = queryParam.entrySet().iterator(); it.hasNext();) {
            Entry<?, ?> entry = (Entry<?, ?>) it.next();
            boolean isEmpty = StringUtils.isEmpty(entry.getValue());
            if (skipNullValue && isEmpty) {
                continue;
            }
            builder.append(entry.getKey()).append('=');
            if (!isEmpty) {
                if (isEncodeParam) {
                    builder.append(encodeUrl(String.valueOf(entry.getValue())));
                } else {
                    builder.append(entry.getValue());
                }
            } else {
                builder.append("");
            }
            if (it.hasNext()) {
                builder.append('&');
            }
        }
        return builder.build();
    }    
    
    public static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }    
    
    /**
     * 获取URL请求参数,忽略重复值
     * 
     * @param queryString 查询参数串
     * @return 参数对象
     */
    public static Map<String, String> parseQueryString(String queryString) {
        if (StringUtils.isEmpty(queryString)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<String, String>();
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(queryString, "&");
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            int pos = pair.indexOf(61);
            if (pos == -1) {
                throw new IllegalArgumentException();
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1, pair.length()), sb);
            map.put(key, val);
        }
        return map;
    }

    private static String parseName(String s, StringBuilder sb) {
        sb.setLength(0);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
            case '+':
                sb.append(' ');
                break;
            case '%':
                try {
                    sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));

                    i += 2;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException();
                } catch (StringIndexOutOfBoundsException e) {
                    String rest = s.substring(i);
                    sb.append(rest);
                    if (rest.length() == 2) {
                        ++i;
                    }
                }
                break;
            default:
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String getJsonp(HttpServletRequest request) {
        String jsonp = request.getParameter("callback");
        if (StringUtils.isEmpty(jsonp)) {
            jsonp = request.getParameter("jsonp");
        }
        return jsonp;
    }

    public static void outJson(Object obj, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(StringUtils.isEmpty(response.getContentType())){
            response.setHeader("Content-Type", "application/json;charset=utf-8");
        }
        String jsonp = getJsonp(request);
        boolean isJsonp = !StringUtils.isEmpty(jsonp);
        Writer writer = response.getWriter();
        if (isJsonp) {
            writer.write(jsonp);
            writer.write('(');
        }
        JSON.serialize(writer, obj);
        if (isJsonp) {
            writer.write(')');
        }
    }

    public static void toDownload(FileToDownload fileToDownload, HttpServletRequest request, HttpServletResponse response) throws IOException {
        OutputStream responseOutputStream = response.getOutputStream();
        response.setHeader("Pragma", "public");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Content-Type", fileToDownload.getContentType());
        if (fileToDownload.isForceDownload()) {
            response.setHeader("Content-Type", "application/force-download");
            String fileName = fileToDownload.getFileName();
            if (fileName != null) {
                if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
                    fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
                } else {
                    fileName = URLEncoder.encode(fileName, "UTF-8");
                }
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            }
        }

        IOUtils.copy(fileToDownload.getContent(), responseOutputStream);
    }

    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix, boolean isTrimNullString) {
        Assert.notNull(request, "Request must not be null");
        Enumeration<String> paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        if (prefix == null) {
            prefix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if (values == null || values.length == 0) {
                    // Do nothing, no values found at all.
                } else if (values.length > 1) {
                    params.put(unprefixed, values);
                } else if ((isTrimNullString && "null".equalsIgnoreCase(values[0]))) {
                    params.put(unprefixed, null);
                } else {
                    params.put(unprefixed, values[0]);
                }
            }
        }
        return params;
    }
}
