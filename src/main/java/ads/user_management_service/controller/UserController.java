package ads.user_management_service.controller;

import ads.user_management_service.constant.AuthPath;
import ads.user_management_service.dto.BaseResponse;
import ads.user_management_service.dto.CreateUserRequestDto;
import ads.user_management_service.dto.UpdatePasswordRequest;
import ads.user_management_service.service.UserService;
import ads.user_management_service.util.BaseResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

        userService.createUser(request, authHeader);
        return BaseResponseUtils.constructSuccessResponse(null);
    }

    @PutMapping("/update-password/{userId}")
    public BaseResponse<Void> updatePassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable UUID userId,
            @RequestBody UpdatePasswordRequest request) {

        userService.updatePassword(userId, request.getNewPassword(), authHeader);
        return BaseResponseUtils.constructSuccessResponse(null);
    }
}