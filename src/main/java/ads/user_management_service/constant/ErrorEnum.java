package ads.user_management_service.constant;


import ads.user_management_service.util.ConfigUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum ErrorEnum {
	/// Success Errors (0000)
	SUCCESS				("0000", "Success", ConfigUtil.getApplicationName()),

	/// Client Errors (01XX)
	INVALID_INPUT		("0101", "Invalid Input", ConfigUtil.getApplicationName()),
	MISSING_REQUIRED_PARAMETER		("0102", "Missing required parameters", ConfigUtil.getApplicationName()),
	INVALID_AUTHENTICATION_TOKEN		("0103", "Invalid authentication token", ConfigUtil.getApplicationName()),
	UNAUTHORIZED_ACCESS		("0104", "Unauthorized access", ConfigUtil.getApplicationName()),
	DATA_NOT_FOUND		("0105", "Data Not Found", ConfigUtil.getApplicationName()),
	METHOD_NOT_ALLOWED		("0106", "Method not allowed", ConfigUtil.getApplicationName()),
	USER_ALREADY_EXIST		("0107", "User Name Already Exist", ConfigUtil.getApplicationName()),
	EMAIL_ALREADY_EXIST		("0107", "Email Already Exist", ConfigUtil.getApplicationName()),

	/// Server Errors (02XX)
	INTERNAL_SERVER_ERROR		("0201", "Internal Server Error", ConfigUtil.getApplicationName()),
	SERVICE_UNAVAILABLE		("0202", "Service Unavailable", ConfigUtil.getApplicationName()),
	TIMEOUT		("0203", "Timeout occurred", ConfigUtil.getApplicationName()),
	DATA_PROCESS_ERROR		("0205", "Data Processing Error", ConfigUtil.getApplicationName()),
	CONFIGURATION_ERROR		("0206", "Configuration Error", ConfigUtil.getApplicationName()),

	/// Validation Errors (03XX)
	MISSING_REQUIRED ("0301", "Missing required fields", ConfigUtil.getApplicationName()),
	FIELD_VALUE_OUT_OF_RANGE ("0302", "Field value out of range", ConfigUtil.getApplicationName()),
	INVALID_FORMAT ("0303", "Invalid data format", ConfigUtil.getApplicationName()),
	DUPLICATE ("0304", "Duplicate data", ConfigUtil.getApplicationName()),

	///  Authentication and Authorization Errors (04XX)
	UNAUTHORIZED	("0400", "UNAUTHORIZED. Please contact Admin", ConfigUtil.getApplicationName()),
	INVALID_CREDENTIAL	("0401", "Please entry your valid username and password", ConfigUtil.getApplicationName()),
	INVALID_TOKEN		("0402", "Invalid Token", ConfigUtil.getApplicationName()),
	RESTRICTED_ACCESS	("0403", "Restricted Access or you don't have access", ConfigUtil.getApplicationName()),
	SESSION_EXPIRED		("0404", "Session Expired", ConfigUtil.getApplicationName()),
	INVALID_API_KEY		("0405", "Invalid API Key", ConfigUtil.getApplicationName()),
	USER_ALREADY_LOGIN	("0406", "Please sign out your account in other device", ConfigUtil.getApplicationName()),
	USER_ALREADY_LOGIN_ANOTHER_ACCOUNT	("0407", "Your account is detected as logged in on another device, access to this device will be logged out.", ConfigUtil.getApplicationName()),
	ACCOUNT_LOCKED	("0408", "Your account cannot be used temporarily because you entered your password incorrectly 3 times. Contact Admin to reactivate it.", ConfigUtil.getApplicationName()),
	PASSWORD_EXPIRED	("0409", "Your account has been disabled because you have not changed your password in the last 60 days. Contact Admin to reactivate it.", ConfigUtil.getApplicationName()),
	ACCOUNT_EXPIRED	("0410", "Your account has expired. Contact Admin to reactivate it.", ConfigUtil.getApplicationName()),
	INVALID_ACCOUNT	("0411", "Your account is invalid. Please contact Admin", ConfigUtil.getApplicationName()),
	INVALID_REQUEST	("0411", "Invalid Request. Please contact Admin", ConfigUtil.getApplicationName()),


	/// Password error num (043x)
	PASSWORD_DOESNOT_MATCH	("0431", "Password does not match.", ConfigUtil.getApplicationName()),
	PASSWORD_INVALID	("0432", "Password must be at least 12 characters long, contain an uppercase letter, a lowercase letter, and at least one number or special character", ConfigUtil.getApplicationName()),

	/// General error (99xx)
	RABBIT_SIMULATOR_ERROR	("9998", "KAFKA_SIMULATOR_ERROR", ConfigUtil.getApplicationName()),
	DEFAULT_ERROR		("9999", "General Error", ConfigUtil.getApplicationName())
	;

	@NonNull @Getter private String errorCode;
	@NonNull @Getter private String errorDesc;
	@NonNull @Getter private String errorSource;
	@Getter @Setter private String errorDetail;
	@Getter @Setter private String operationId;
	@Getter @Setter private Object additionalData;

	public static ErrorEnum getErrorEnumByCode(String code) {
		for (ErrorEnum errorEnum : ErrorEnum.values()) {
			if (errorEnum.getErrorCode().equals(code)) {
				return errorEnum;
			}
		}
		
		return DEFAULT_ERROR;
	}
	
}
