package com.swyp.futsal.security.filter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.swyp.futsal.security.util.RequestUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

  private UserDetailsService userDetailsService;
  private FirebaseAuth firebaseAuth;

  public JwtFilter(UserDetailsService userDetailsService, FirebaseAuth firebaseAuth) {
    this.userDetailsService = userDetailsService;
    this.firebaseAuth = firebaseAuth;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return true;
    // return request.getRequestURI().equals("/users") && request.getMethod().equals("POST");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // get the token from the request
    FirebaseToken decodedToken;
    try{
      String header = RequestUtil.getAuthorizationToken(request.getHeader("Authorization"));
      decodedToken = firebaseAuth.verifyIdToken(header);
    } catch (FirebaseAuthException | IllegalArgumentException e) {
      // ErrorMessage 응답 전송
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"code\":\"INVALID_TOKEN\", \"message\":\"" + e.getMessage() + "\"}");
      return;
    }

    // User를 가져와 SecurityContext에 저장한다.
    try{
      UserDetails user = userDetailsService.loadUserByUsername(decodedToken.getUid());
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          user, null, user.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch(Exception e){
      // ErrorMessage 응답 전송
      e.printStackTrace();
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write("{\"code\":\"USER_NOT_FOUND\"}");
      return;
    }
    filterChain.doFilter(request, response);
  }
}
