package com.toyproject.board.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;


import java.io.IOException;

public class CorsFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

//		Access-Control-Allow-Orgin : 요청을 보내는 페이지의 출처 [ *, 도메인 ]
//		Access-Control-Allow-Methods : 요청을 허용하는 메소드. Default : GET, POST, HEAD
//		Access-Control-Max-Age : 클라이언트에서 preflight의 요청 결과를 저장할 시간 지정. 해당 시간 동안은 pre-flight를 다시 요청하지 않는다.
//		Access-Control-Allow-Headers : 요청을 허용하는 헤더

        String origin = req.getHeader("Origin");
        res.setHeader("Access-Control-Allow-Origin", "*");
        // res.setHeader("Access-Control-Allow-Origin", StringUtils.isBlank(origin) ? "" : origin);
        res.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        res.setHeader("Access-Control-Max-Age", "3600");
        res.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Accept, Origin, X-Requested-With, Authorization4SS");
        res.setHeader("Access-Control-Expose-Headers", "X-Auth-Token");
        res.setHeader("Access-Control-Allow-Credentials", "true");

        if (req.getMethod().equals("OPTIONS")) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        chain.doFilter(request, response);
    }

}
