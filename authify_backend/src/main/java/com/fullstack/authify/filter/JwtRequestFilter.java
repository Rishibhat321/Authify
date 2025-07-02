package com.fullstack.authify.filter;

import com.fullstack.authify.service.AppUserDetailsService;
import com.fullstack.authify.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private final AppUserDetailsService appUserDetailsService;

    @Autowired
    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_URLS = List.of("/api/login", "/api/register", "/api/send-reset-otp", "/api/reset-password", "/api/logout");


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // get the servlet path
       String path =  request.getServletPath();

       // don't validate the jwt tokens for the public urls
        if(PUBLIC_URLS.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;
        String email = null;

        //1. Check the authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
           jwt = authorizationHeader.substring(7);
        }

        //2. If authorization header is not found, then check the cookies
        if(jwt==null) {
            Cookie[] cookies = request.getCookies();
            if(cookies != null) {
                for(Cookie cookie : cookies) {
                    if("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }

        //3. Validate the token and set the security context
        if(jwt != null) {
            email = jwtUtil.extractEmail(jwt);
            if(email != null  && SecurityContextHolder.getContext().getAuthentication() == null) {
               UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);

               if(jwtUtil.validateToken(jwt, userDetails)) {
                   UsernamePasswordAuthenticationToken authenticationToken = new
                           UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                   authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                   SecurityContextHolder.getContext().setAuthentication(authenticationToken);
               }
            }
        }

        filterChain.doFilter(request, response);

    }
}
