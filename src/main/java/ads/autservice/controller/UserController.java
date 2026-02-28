package ads.autservice.controller;

import ads.autservice.constant.AuthPath;
import ads.autservice.constant.ErrorEnum;
import ads.autservice.dto.BaseResponse;
import ads.autservice.dto.CreateUserRequestDto;
import ads.autservice.dto.UpdatePasswordRequest;
import ads.autservice.service.UserService;
import ads.autservice.util.BaseResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
@RestController
@RequestMapping(AuthPath.USER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(AuthPath.CREATE_USER)
    public BaseResponse<Void> createUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateUserRequestDto request) {

        try {
            userService.createUser(request, authHeader);
            return BaseResponseUtils.constructResponse(ErrorEnum.SUCCESS, null);
        } catch (Throwable e) {
            return BaseResponseUtils.constructResponse(
                    BaseResponseUtils.getErrorCode(e),
                    null
            );
        }
    }

    @PutMapping("/update-password/{userId}")
    public BaseResponse<Void> updatePassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("userId") UUID userId,
            @RequestBody UpdatePasswordRequest request) {

        try {
            userService.updatePassword(userId, request.getNewPassword(), authHeader);
            return BaseResponseUtils.constructResponse(ErrorEnum.SUCCESS, null);
        } catch (Throwable e) {
            return BaseResponseUtils.constructResponse(
                    BaseResponseUtils.getErrorCode(e),
                    null
            );
        }
    }
}
