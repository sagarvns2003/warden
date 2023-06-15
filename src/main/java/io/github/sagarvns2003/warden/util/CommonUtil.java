package io.github.sagarvns2003.warden.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
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

	public static Map<String, String> convertToMap(String commaSeperatedString) {
		Map<String, String> labelMap = Map.of();
		if (StringUtils.isBlank(commaSeperatedString)) {
			return labelMap;
		}

		List<String> labels = convertToList(commaSeperatedString);
		if (null != labels && !labels.isEmpty()) {
			// This conversion will also handle the duplicate element from the list
			labelMap = new ConcurrentHashMap<>(
					labels.stream().collect(Collectors.toMap(s -> s, s -> Constant.BLANK, (e1, e2) -> e1)));
		}
		return labelMap;
	}

	public static List<String> convertToList(String commaSeperatedString) {
		if (StringUtils.isBlank(commaSeperatedString)) {
			return List.of();
		}
		return Stream.of(commaSeperatedString.split(",")).map(String::strip).collect(Collectors.toList());
	}

}