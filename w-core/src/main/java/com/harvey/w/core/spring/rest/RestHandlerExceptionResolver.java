package com.harvey.w.core.spring.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.harvey.w.core.exception.ValidationException;
import com.harvey.w.core.utils.HttpUtils;

public class RestHandlerExceptionResolver implements HandlerExceptionResolver, PriorityOrdered {

    private int order = 0;
    private Map<Integer, String> statusCodeMap = new HashMap<Integer, String>() {
        private static final long serialVersionUID = 1L;
        {
            this.put(HttpServletResponse.SC_UNAUTHORIZED, "NoneLoginException,org.springframework.security.core.AuthenticationException");
        }
    };
    private int statusCode = HttpServletResponse.SC_OK;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return new ModelAndView(new RestExceptionView(ex));
    }

    private class RestExceptionView implements View {

        /**
         * Log variable for all child classes.
         */
        protected final Log log = LogFactory.getLog(getClass());

        private Exception ex;

        public RestExceptionView(Exception ex) {
            this.ex = ex;
        }

        @Override
        public String getContentType() {
            return "application/json;charset=utf-8";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            Map<String, Object> map = new HashMap<String, Object>();
            if (ex instanceof ValidationException) {
                map.put("errors", ((ValidationException) ex).getErrors());

            } else {
                map.put("errors", ex.toString());
            }

            /*
             * if (ex instanceof NoneLoginException || ex instanceof
             * AuthenticationException) {
             * response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); }else{
             * response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
             * }
             */
            int sc = statusCode;
            String simpleName = ',' + ex.getClass().getSimpleName();
            String name = ',' + ex.getClass().getName();
            for (Entry<Integer, String> entry : statusCodeMap.entrySet()) {
                String source = ',' + entry.getValue();
                if (source.contains(simpleName) || source.contains(name)) {
                    sc = entry.getKey();
                    break;
                }
            }
            response.setStatus(sc);
            HttpUtils.outJson(map, request, response);

            log.error("Exception：", ex);
        }

    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setStatusCodeMap(Map<Integer, String> statusCodeMap) {
        if (statusCodeMap != null) {
            this.statusCodeMap.putAll(statusCodeMap);
        }
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
