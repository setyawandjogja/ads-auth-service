package ads.user_management_service.controller;

import ads.user_management_service.constant.AuthPath;
import ads.user_management_service.dto.BaseResponse;
import ads.user_management_service.dto.LoginRequestDto;
import ads.user_management_service.dto.LoginResponseDto;
import ads.user_management_service.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthPath.AUTH)
public class AuthController  {
	
	@Autowired
	private LoginService loginService;

	@PostMapping(value = AuthPath.LOGIN, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {MediaType.APPLICATION_JSON_VALUE })
	public BaseResponse<LoginResponseDto> mainLogin(@RequestBody LoginRequestDto request){
		return loginService.login(request);
	}



}
