package com.medals.medalsbackend.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;

@Component
@WebFilter("/*")
public class HttpStatusCaptureFilter implements Filter {

    private static final ThreadLocal<Integer> statusThreadLocal = new ThreadLocal<>();

    public static Integer getHttpStatus() {
        return statusThreadLocal.get();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws java.io.IOException, jakarta.servlet.ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        chain.doFilter(request, new HttpServletResponseWrapper(httpServletResponse) {
            @Override
            public void setStatus(int sc) {
                statusThreadLocal.set(sc);
                super.setStatus(sc);
            }

            @Override
            public void sendError(int sc) throws java.io.IOException {
                statusThreadLocal.set(sc);
                super.sendError(sc);
            }

            @Override
            public void sendError(int sc, String msg) throws java.io.IOException {
                statusThreadLocal.set(sc);
                super.sendError(sc, msg);
            }
        });

        statusThreadLocal.remove(); // Clean up
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
