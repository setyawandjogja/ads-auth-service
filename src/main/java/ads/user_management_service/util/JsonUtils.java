package ads.user_management_service.util;

import ads.user_management_service.constant.ErrorEnum;
import ads.user_management_service.exception.GenericException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@SuppressWarnings("all")
public class JsonUtils {

	private JsonUtils() {
	}
	
	private static final ObjectMapper objectMapper = JsonMapper.builder()
			.addModule(new ParameterNamesModule())
			.addModule(new Jdk8Module())
			.addModule(new JavaTimeModule())
			.build();
	
	static {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static String objectAsStringJson(Object data) throws GenericException {
		try {
			return objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			log.error("Error when parsing object to string json ", e);
			throw new GenericException(ErrorEnum.DEFAULT_ERROR, e.toString());
		}
	}

	public static <T> T stringJsonAsObject(String json, Class<T> clazz) throws GenericException {
		try {
			if (json != null && json.trim().equals("[]")) {
				log.error("JSON is an empty array, returning null for object type {}", clazz.getName());
				return null;
			}
			return objectMapper.readValue(json, clazz);
		} catch (IOException e) {
			log.error("Error when parsing string json to object ", e);
			throw new GenericException(ErrorEnum.DEFAULT_ERROR, e.toString());
		}
	}


	public static <T> T stringJsonAsObject(String json, TypeReference<T> clazz) throws GenericException {
		try {
			if (json != null && json.trim().equals("[]")) {
				log.error("JSON is an empty array, returning null for object type {}", clazz.getType().getTypeName());
				return null;
			}
			return objectMapper.readValue(json, clazz);
		} catch (IOException e) {
			log.error("Error when parsing string json to object ", e);
			throw new GenericException(ErrorEnum.DEFAULT_ERROR, e.toString());
		}
	}
	
	public static <T> T convertObject(Object from, Class<T> destClazz) {
		return objectMapper.convertValue(from, destClazz);
	}

	public static <T> T convertObject(Object from, TypeReference<T> type) {
		return objectMapper.convertValue(from, type);
	}
	
}
