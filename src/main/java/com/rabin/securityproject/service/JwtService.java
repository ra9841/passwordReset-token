package com.rabin.securityproject.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    //Random Encryption key generator
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";


    public String extractUsername(String token) {  //from JwtAuthenticationFilter class & method explain here
        return extractClaim(token, Claims::getSubject);
    } //method of extractClaim is down

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractALLClaims(token);
        return claimsResolver.apply(claims);
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }//method of generateToken is down


    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private Claims extractALLClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())  //method of getSignInKey is down
                .build()
                .parseClaimsJws(token) //try to see this parseClaimsJws some have parseClaimsJwt(it may give error: Signed Claims JWSs are not supported)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] KeyBytes = Decoders.BASE64.decode(SECRET_KEY); //we define this SECRET_KEY at top
        return Keys.hmacShaKeyFor(KeyBytes);
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);//method of isTokenExpired is down
    }

    public  boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }//method of extractExpiration is down

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}


