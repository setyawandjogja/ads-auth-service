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

@RestController
@RequestMapping(AuthPath.USER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping(AuthPath.CREATE_USER)
    public BaseResponse<Void> createUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateUserRequestDto request) {

        try {

            // Ambil token
            String token = authHeader.replace("Bearer ", "");

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
