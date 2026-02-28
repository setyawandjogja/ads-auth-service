package ads.user_management_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	private ValueOperations<String, Object> valueOps() {
		return redisTemplate.opsForValue();
	}

	public <T> T getData(String key, Class<T> clazz) {

		Object value = valueOps().get(key);

		if (value == null) {
			return null;
		}

		// ✅ Jika sudah sesuai tipe
		if (clazz.isInstance(value)) {
			return clazz.cast(value);
		}

		// ✅ Convert LinkedHashMap → DTO
		return objectMapper.convertValue(value, clazz);
	}

	// 🔥 FIX TANPA GETEX
	public <T> T getData(String key, Class<T> clazz, Duration expired) {

		Object value = valueOps().get(key);   // ⬅ hanya GET biasa

		if (value == null) {
			return null;
		}

		// ⬅ manual extend TTL (pengganti GETEX)
		redisTemplate.expire(key, expired);

		if (clazz.isInstance(value)) {
			return clazz.cast(value);
		}

		return objectMapper.convertValue(value, clazz);
	}

	public void setData(String key, Object value) {
		valueOps().set(key, value);
	}

	public void setData(String key, Object value, Duration expired) {
		valueOps().set(key, value, expired);
	}

	public Boolean deleteKey(String key) {
		return redisTemplate.delete(key);
	}
}
