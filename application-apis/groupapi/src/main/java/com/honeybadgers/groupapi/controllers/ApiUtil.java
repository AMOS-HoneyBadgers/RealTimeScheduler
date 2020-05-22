package com.honeybadgers.groupapi.controllers;

import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiUtil {
    private ApiUtil(){

    }
    public static void setExampleResponse(NativeWebRequest req, String contentType, String example) throws IOException {
            HttpServletResponse res = req.getNativeResponse(HttpServletResponse.class);
            res.setCharacterEncoding("UTF-8");
            res.addHeader("Content-Type", contentType);
            res.getWriter().print(example);

    }

}
