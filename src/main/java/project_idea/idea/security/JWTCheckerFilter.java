package project_idea.idea.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

	@Autowired
	private JWT jwt;
	@Autowired
	private UsersService usersService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer "))
			throw new UnauthorizedException("Insert the token in the Authorization Header with the correct format!");
		String accessToken = authHeader.substring(7);

		jwt.verifyToken(accessToken);

		String userId = jwt.getIdFromToken(accessToken);
		User currentUser = this.usersService.findById(UUID.fromString(userId));

		Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);

	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return new AntPathMatcher().match("/auth/**", request.getServletPath()) ||
				new AntPathMatcher().match("/v3/api-docs/**", request.getServletPath()) ||
				new AntPathMatcher().match("/swagger-ui/**", request.getServletPath());
	}
}