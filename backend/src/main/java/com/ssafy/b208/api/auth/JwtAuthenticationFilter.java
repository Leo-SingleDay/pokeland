package com.ssafy.b208.api.auth;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.ssafy.b208.api.db.entity.User;
import com.ssafy.b208.api.db.repository.UserRepository;
import com.ssafy.b208.api.exception.LackMoneyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Read the Authorization header, where th e JWT Token should be
        String header = request.getHeader(JwtTokenUtil.HEADER_STRING);
        // If header does not contain BEARER or is null delegate to Spring impl and exit
        if (header == null || !header.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // If header is present, try grab user principal from database and perform authorization
            Authentication authentication = getAuthentication(request);
            // jwt ???????????? ?????? ????????? ?????? ??????(authentication) ??????.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (TokenExpiredException ex){
            log.info(ex.getMessage());
            response.sendError(400,"?????? ?????? ????????? ?????? ???????????????.");
            return;
        }
        catch (Exception ex) {

            log.info(ex.getMessage());
            response.sendError(400,"???????????? ?????? ???????????????.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Transactional(readOnly = true)
    public Authentication getAuthentication(HttpServletRequest request)  {
        String token = request.getHeader(JwtTokenUtil.HEADER_STRING);
        // ?????? ????????? Authorization ????????? jwt ????????? ????????? ????????????, ?????? ?????? ??? ?????? ?????? ?????? ??????.
        if (token != null) {
            // parse the token and validate it (decode)
            JWTVerifier verifier = JwtTokenUtil.getVerifier();
            JwtTokenUtil.handleError(token);
            DecodedJWT decodedJWT = verifier.verify(token.replace(JwtTokenUtil.TOKEN_PREFIX, ""));
            String userId = decodedJWT.getSubject();

            // Search in the DB if we find the user by token subject (username)
            // If so, then grab user details and create spring auth token using username, pass, authorities/roles
            if (userId != null) {
                // jwt ????????? ????????? ?????? ??????(userId) ?????? ?????? ????????? ?????? ????????? ????????? ????????? ??????.
                Optional<User> user = userRepository.findOptionalByEmail(userId);
                if(user.isPresent()) {
                    // ????????? ?????? ????????? ??????, ?????? context ????????? ?????? ????????? ?????? ??????(jwtAuthentication) ??????.
                    NftUserDetail userDetails = new NftUserDetail(user.get());
                    UsernamePasswordAuthenticationToken jwtAuthentication = new UsernamePasswordAuthenticationToken(userId,
                            null, userDetails.getAuthorities());
                    jwtAuthentication.setDetails(userDetails);
                    return jwtAuthentication;
                }else{
                    return null;
                }
            }
            return null;
        }
        return null;
    }
}
