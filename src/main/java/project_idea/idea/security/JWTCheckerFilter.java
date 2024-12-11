package project_idea.idea.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import project_idea.idea.entities.User;
import project_idea.idea.exceptions.UnauthorizedException;
import project_idea.idea.services.UsersService;
import project_idea.idea.tools.JWT;

import java.io.IOException;
import java.util.UUID;

@Component
public class JWTCheckerFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTCheckerFilter.class);

    private final JWT jwt;
    private final UsersService usersService;
    
    public JWTCheckerFilter(JWT jwt, UsersService usersService) {
        this.jwt = jwt;
        this.usersService = usersService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        logger.debug("Processing request to: {}", request.getRequestURI());
        
        // Allow requests without authentication header to proceed
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No valid Authorization header found");
            filterChain.doFilter(request, response);
            return;
        }
        
        String accessToken = authHeader.substring(7);
        logger.debug("Token received and being verified");

        try {
            jwt.verifyToken(accessToken);
            String userId = jwt.getIdFromToken(accessToken);
            User currentUser = this.usersService.findById(UUID.fromString(userId));
            logger.debug("User authenticated successfully: {}", currentUser.getEmail());

            Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            throw new UnauthorizedException("Invalid or expired token");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return new AntPathMatcher().match("/auth/**", request.getServletPath()) ||
                new AntPathMatcher().match("/categories", request.getServletPath()) ||
                (request.getMethod().equals("GET") && 
                 new AntPathMatcher().match("/posts", request.getServletPath()) &&
                 new AntPathMatcher().match("/posts/**", request.getServletPath()) &&
                 !request.getServletPath().contains("/me")) ||
                new AntPathMatcher().match("/social-profiles", request.getServletPath()) ||
                new AntPathMatcher().match("/social-profiles/*/public", request.getServletPath()) ||
                new AntPathMatcher().match("/v3/api-docs/**", request.getServletPath()) ||
                new AntPathMatcher().match("/swagger-ui/**", request.getServletPath());
    }
}
