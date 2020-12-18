package org.tc.financial.state.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenService implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3034134477424772776L;

	@Value("${jwt.validityTime}")
	private long validity;
	
	@Value("${jwt.secret}")
	private String secret;
	
	// Retrieve data From Token
	private String getDataFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}
	
	// Retrieve expriration Date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}
	
	// Check if the token is expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	// Generate token for Object
	public String generateToken(Object object) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, object.toString());
	}

	private String doGenerateToken(Map<String, Object> claims, String data) {
		
		return Jwts.builder().setClaims(claims).setSubject(data).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + validity * 1000))
				.signWith(SignatureAlgorithm.HS256, secret).compact();
	}
	
	// Check if token in valid
	public Boolean validateToken(String token, Object data) {
		return (isTokenExpired(token))? false : getDataFromToken(token).equals(data.toString());
	}
}
