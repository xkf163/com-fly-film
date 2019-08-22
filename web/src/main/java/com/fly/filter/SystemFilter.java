package com.fly.filter;



import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * filter过滤器，获取项目路径，设置ajax超时标识
 *
 * @author billJiang QQ:475572229
 */

@WebFilter(filterName = "test", urlPatterns = "/*")
public class SystemFilter implements Filter {



    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException,
            ServletException {


        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin","*");
        System.out.println(request.getRequestURL());
        String basePath = request.getContextPath();
        request.setAttribute("basePath", basePath);
        filterChain.doFilter(request, servletResponse);

    }

    @Override
    public void destroy() {

        // TODO Auto-generated method stub

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

        // TODO Auto-generated method stub

    }


}
