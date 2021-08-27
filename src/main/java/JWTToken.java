import io.jsonwebtoken.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Objects;

@Component
public class JWTToken extends OncePerRequestFilter {
    final String secretKey="NeverGiveUp";
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            validateRequest(jwt,secretKey);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    public static void validateRequest(String token,String sKey) throws Exception {
        if (token == null) {
            throw new Exception("Token is required");
        }
        if (!Objects.equals(Jwts.parser().setSigningKey(sKey).parseClaimsJws(token).getHeader().getAlgorithm(), String.valueOf(SignatureAlgorithm.HS256)))
            throw new Exception("Invalid Signature Algorithm");
        try {
            Jwts.parser().setSigningKey(sKey).parseClaimsJws(token);
        } catch (MalformedJwtException ex) {
            throw new Exception("Token invalid");
        } catch (ExpiredJwtException ex) {
            throw new Exception("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new Exception("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new Exception("JWT claims string is empty.");
        }
    }
}
