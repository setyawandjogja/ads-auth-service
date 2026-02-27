package ads.autservice.controller;

import ads.autservice.constant.AuthPath;
import ads.autservice.dto.BaseResponse;
import ads.autservice.dto.LoginRequestDto;
import ads.autservice.dto.LoginResponseDto;
import ads.autservice.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthPath.AUTH_V1)
public class AuthController  {
	
	@Autowired
	private LoginService loginService;

	@PostMapping(value = AuthPath.LOGIN, produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {MediaType.APPLICATION_JSON_VALUE })
	public BaseResponse<LoginResponseDto> mainLogin(@RequestBody LoginRequestDto request){
		return loginService.login(request);
	}



}
