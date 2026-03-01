package ads.user_management_service.service;

import ads.user_management_service.constant.ErrorEnum;
import ads.user_management_service.constant.RedisKey;
import ads.user_management_service.exception.GenericException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

	private static final Duration EXPIRY_DURATION = Duration.ofHours(24);

	@Value("${application.security.jwt.secret-key}")
	private String secretKey;

	@Autowired
	private RedisService redisService;

	/*
	 * =========================
	 * BUILD TOKEN
	 * =========================
	 */
	public String buildToken(String subject, String issuer, String id, Map<String, Object> claims)
			throws GenericException {

		log.info("Generate token for subject={}, id={}", subject, id);

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + EXPIRY_DURATION.toMillis());

		String token = Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuer(issuer)     // role
				.setId(id)             // userId
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();

		// Simpan ke Redis (single session)
		redisService.setData(
				RedisKey.TOKEN + ":" + token,
				subject,
				EXPIRY_DURATION
		);

		redisService.setData(
				RedisKey.LOGIN_SESSION + ":" + subject,
				token,
				EXPIRY_DURATION
		);

		return token;
	}

	/*
	 * =========================
	 * VALIDATE TOKEN
	 * =========================
	 */
	public Claims validateToken(String token) throws GenericException {

		try {

			// 1️⃣ Validate signature & expiration dulu
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(getSignInKey())
					.build()
					.parseClaimsJws(token)
					.getBody();

			String subject = claims.getSubject();

			// 2️⃣ Check Redis TOKEN
			String redisSubject = redisService.getData(
					RedisKey.TOKEN + ":" + token,
					String.class
			);

			if (StringUtils.isBlank(redisSubject)) {
				throw new GenericException(ErrorEnum.INVALID_TOKEN);
			}

			// 3️⃣ Check LOGIN_SESSION (single session control)
			String loginSession = redisService.getData(
					RedisKey.LOGIN_SESSION + ":" + subject,
					String.class
			);

			if (StringUtils.isBlank(loginSession) || !loginSession.equals(token)) {
				throw new GenericException(ErrorEnum.INVALID_TOKEN);
			}

			return claims;

		} catch (ExpiredJwtException e) {
			log.warn("Token expired: {}", e.getMessage());
			throw new GenericException(ErrorEnum.TOKEN_EXPIRED);
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("Invalid token: {}", e.getMessage());
			throw new GenericException(ErrorEnum.INVALID_TOKEN);
		}
	}

	/*
	 * =========================
	 * EXTRACT METHODS
	 * =========================
	 */
	public String extractUsername(String token) throws GenericException {
		return validateToken(token).getSubject();
	}

	public String extractRole(String token) throws GenericException {
		return validateToken(token).getIssuer();
	}

	public String extractUserId(String token) throws GenericException {
		return validateToken(token).getId();
	}

	/*
	 * =========================
	 * REVOKE TOKEN
	 * =========================
	 */
	public Boolean revokeToken(String token) {

		String subject = redisService.getData(
				RedisKey.TOKEN + ":" + token,
				String.class
		);

		if (subject != null) {
			redisService.deleteKey(RedisKey.LOGIN_SESSION + ":" + subject);
		}

		return redisService.deleteKey(RedisKey.TOKEN + ":" + token);
	}

	/*
	 * =========================
	 * SIGNING KEY
	 * =========================
	 */
	private Key getSignInKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}

	// ===============================
	// JWT VALIDATION
	// ===============================
	public String validateAndExtractToken(String authHeader) {

		if (authHeader == null || authHeader.isBlank()) {
			throw new GenericException(
					ErrorEnum.UNAUTHORIZED,
					"Authorization required"
			);
		}

		if (!authHeader.startsWith("Bearer ")) {
			throw new GenericException(
					ErrorEnum.UNAUTHORIZED,
					"Invalid Authorization format"
			);
		}

		return authHeader.substring(7);
	}
}