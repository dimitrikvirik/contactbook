package git.dimitrikvirik.contactbook.util;


import git.dimitrikvirik.contactbook.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenUtil {


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.tokenValidity}")
    private String tokenValidity;



    public Claims getClaims(String token) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            return claimsJws.getBody();
        } catch (ExpiredJwtException exception) {
            throw new TokenExpiredException();
        }
    }

    public String doGenerateToken(Claims claims, String subject) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Long.parseLong(tokenValidity))))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }



}