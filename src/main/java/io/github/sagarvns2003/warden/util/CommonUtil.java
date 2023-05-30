package io.github.sagarvns2003.warden.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.MessageHeaders;

public class CommonUtil {

	public static Map<String, Object> extractAllHeaders(MessageHeaders headers) {
		if (null != headers && !headers.isEmpty()) {
			return headers.entrySet().parallelStream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		}
		return Map.of();
	}

	public static List<String> convertToList(String commaSeperatedString) {
		if (StringUtils.isBlank(commaSeperatedString)) {
			return List.of();
		}
		return Stream.of(commaSeperatedString.split(",")).map(String::strip).collect(Collectors.toList());
	}
}