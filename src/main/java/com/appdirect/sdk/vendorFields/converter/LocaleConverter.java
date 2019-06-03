package com.appdirect.sdk.vendorFields.converter;

import java.beans.PropertyEditorSupport;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

import com.appdirect.sdk.exception.PropertyEditorSupportException;

public class LocaleConverter extends PropertyEditorSupport {
	public void setAsText(final String text) throws IllegalArgumentException {
		try {
			setValue(Locale.LanguageRange.parse(text)
					.stream()
					.sorted(Comparator.comparing(Locale.LanguageRange::getWeight).reversed())
					.map(localeRange -> Locale.forLanguageTag(localeRange.getRange()))
					.collect(Collectors.toList()));
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new PropertyEditorSupportException("Failed to serialize Locale from Accept-Language header with value=%s", text);
		}
	}
}
