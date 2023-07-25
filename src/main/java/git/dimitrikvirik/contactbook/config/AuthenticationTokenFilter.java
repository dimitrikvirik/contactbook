package git.dimitrikvirik.contactbook.config;

import git.dimitrikvirik.contactbook.model.security.UserPrincipal;
import git.dimitrikvirik.contactbook.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {


        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Claims claims = jwtTokenUtil.getClaims(token);
            List<GrantedAuthority> scopes = getGrantedAuthorities(claims);
            String username = claims.get("username", String.class);
            PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
                    new UserPrincipal(claims.getSubject(), username),
                    null,
                    scopes);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }


    @SuppressWarnings("unchecked")
    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        return ((List<String>) claims.get("scopes",
                //ParameterizedTypeReference.forType(ArrayList.class) string
                ArrayList.class
        )).stream().map(
                authority -> (GrantedAuthority) () -> authority
        ).toList();
    }
}
