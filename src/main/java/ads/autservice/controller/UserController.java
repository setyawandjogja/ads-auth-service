package ads.autservice.controller;

import ads.autservice.constant.AuthPath;
import ads.autservice.constant.ErrorEnum;
import ads.autservice.dto.BaseResponse;
import ads.autservice.dto.CreateUserRequestDto;
import ads.autservice.service.JwtService;
import ads.autservice.service.UserService;
import ads.autservice.util.BaseResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ads.autservice.exception.GenericException;
@RestController
@RequestMapping(AuthPath.USER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping(AuthPath.CREATE_USER)
    public BaseResponse<Void> createUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateUserRequestDto request) {

        try {

            // 🔐 Cek header ada atau tidak
            if (authHeader == null || authHeader.isBlank()) {
                throw new GenericException(ErrorEnum.UNAUTHORIZED, "Authorization required");
            }

            if (!authHeader.startsWith("Bearer ")) {
                throw new GenericException(ErrorEnum.UNAUTHORIZED, "Invalid Authorization format");
            }

            // Ambil token
            String token = authHeader.substring(7);

            // Extract role dari JWT
            String role = jwtService.extractRole(token);

            userService.createUser(request, role);

            return BaseResponseUtils.constructResponse(ErrorEnum.SUCCESS, null);

        } catch (Throwable e) {
            return BaseResponseUtils.constructResponse(
                    BaseResponseUtils.getErrorCode(e),
                    null
            );
        }
    }
}
