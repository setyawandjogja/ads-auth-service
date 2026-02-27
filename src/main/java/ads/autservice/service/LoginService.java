package ads.autservice.service;


import ads.autservice.constant.ErrorEnum;
import ads.autservice.constant.RedisKey;
import ads.autservice.dto.BaseResponse;
import ads.autservice.dto.LoginRequestDto;
import ads.autservice.dto.LoginResponseDto;
import ads.autservice.dto.UserCacheDto;
import ads.autservice.entity.User;
import ads.autservice.exception.GenericException;
import ads.autservice.repository.UserRepository;
import ads.autservice.util.BaseResponseUtils;
import ads.autservice.util.Md5Util;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final RedisService redisService;

	private static final Duration CACHE_TTL = Duration.ofHours(24);

	public BaseResponse<LoginResponseDto> login(LoginRequestDto req) {

		log.info("Login start for username: {}", req.getUsername());

		ErrorEnum rc = ErrorEnum.SUCCESS;
		LoginResponseDto result = null;

		try {

			String username = req.getUsername();
			String rawPassword = req.getPassword();

			if (StringUtils.isBlank(username) || StringUtils.isBlank(rawPassword)) {
				throw new GenericException(ErrorEnum.INVALID_REQUEST);
			}

			String md5Password = Md5Util.md5(rawPassword);
			String redisKey = RedisKey.USER_CREDENTIAL + ":" + username;

			// ✅ Ambil dari Redis sebagai DTO
			UserCacheDto user = redisService.getData(redisKey, UserCacheDto.class);

			if (user != null) {
				log.info("User found in Redis");
			} else {

				log.info("User not in Redis, checking DB");

				User entity = userRepository.findByUserNameWithRole(username)
						.orElseThrow(() ->
								new GenericException(ErrorEnum.DATA_NOT_FOUND, "User not found"));

				// ✅ Mapping entity → DTO
				user = UserCacheDto.builder()
						.id(entity.getId())
						.fullName(entity.getFullName())
						.userName(entity.getUserName())
						.password(entity.getPassword())
						.roleName(entity.getRole().getRoleName())
						.build();

				redisService.setData(redisKey, user, CACHE_TTL);
			}

			if (!user.getPassword().equals(md5Password)) {
				throw new GenericException(ErrorEnum.INVALID_CREDENTIAL);
			}

			String token = jwtService.buildToken(
					user.getUserName(),
					user.getRoleName(),
					user.getId().toString(),
					new HashMap<>()
			);

			result = LoginResponseDto.builder()
					.token(token)
					.fullName(user.getFullName())
					.build();

			log.info("Login success for {}", username);

		} catch (Throwable e) {
			log.error("Login error", e);
			rc = BaseResponseUtils.getErrorCode(e);
		}

		return BaseResponseUtils.constructResponse(rc, result);
	}
}