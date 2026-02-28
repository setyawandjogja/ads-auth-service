package ads.autservice.controller;

import ads.autservice.constant.AuthPath;
import ads.autservice.dto.BaseResponse;
import ads.autservice.dto.CreateUserRequestDto;
import ads.autservice.dto.UpdatePasswordRequest;
import ads.autservice.service.UserService;
import ads.autservice.util.BaseResponseUtils;
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