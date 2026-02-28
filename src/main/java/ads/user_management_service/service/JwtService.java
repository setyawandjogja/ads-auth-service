package ads.autservice.service;

import ads.autservice.constant.ErrorEnum;
import ads.autservice.constant.RedisKey;
import ads.autservice.exception.GenericException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

	public String buildToken(String subject, String issuer, String id, Map<String, Object> claims)
			throws GenericException {

		log.info("Subject : {}, Issuer : {}, Id : {}", subject, issuer, id);

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + EXPIRY_DURATION.toMillis());

		String token = Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuer(issuer)
				.setId(id)
				.setIssuedAt(now)
				.setExpiration(expiryDate) // JWT expire 24 jam
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();

		// Save token ke Redis dengan TTL 24 jam
		redisService.setData(RedisKey.TOKEN + ":" + token, subject, EXPIRY_DURATION);
		redisService.setData(RedisKey.LOGIN_SESSION + ":" + subject, token, EXPIRY_DURATION);

		return token;
	}

	public Boolean revokeToken(String token) {
		String subject = redisService.getData(RedisKey.TOKEN + ":" + token, String.class);
		if (subject != null) {
			redisService.deleteKey(RedisKey.LOGIN_SESSION + ":" + subject);
		}
		return redisService.deleteKey(RedisKey.TOKEN + ":" + token);
	}

	public Claims validateToken(String token) throws GenericException {

		// Check Redis TOKEN
		String subject = redisService.getData(
				RedisKey.TOKEN + ":" + token,
				String.class,
				EXPIRY_DURATION
		);

		if (StringUtils.isBlank(subject)) {
			throw new GenericException(ErrorEnum.INVALID_TOKEN);
		}

		// Check Redis LOGIN_SESSION
		String checkToken = redisService.getData(
				RedisKey.LOGIN_SESSION + ":" + subject,
				String.class,
				EXPIRY_DURATION
		);

		if (StringUtils.isBlank(checkToken)) {
			throw new GenericException(ErrorEnum.INVALID_TOKEN);
		}

		// Validate signature & expiration
		return Jwts.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public String extractUsername(String token) throws GenericException {
		return validateToken(token).getSubject();
	}

	public String extractRole(String token) throws GenericException {
		return validateToken(token).getIssuer();
	}

	public String extractUserId(String token) throws GenericException {
		return validateToken(token).getId();
	}

	private Key getSignInKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}
}
