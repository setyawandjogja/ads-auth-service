package ads.user_management_service.constant;

public class AuthPath {
	
	private AuthPath() {
	}
	
	// Main Path
	public static final String AUTH= "auth";

	// Parameter Controller Sub-Path
	public static final String LOGIN = "/login";
	public static final String LOGOUT = "/logout";
	public static final String VALIDATE = "/validate";

	public static final String USER= "user";
	public static final String CREATE_USER = "/create";

}
