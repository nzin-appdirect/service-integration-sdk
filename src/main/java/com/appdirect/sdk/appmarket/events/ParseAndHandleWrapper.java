package com.appdirect.sdk.appmarket.events;

import java.util.Map;

import com.appdirect.sdk.appmarket.AppmarketEventHandler;

/**
 * SDK internal - Convenience class that parses an event into a rich event and sends it to its event handler
 *
 * @param <T> the type of the rich event the parser and handler support
 */
class ParseAndHandleWrapper<T> implements SDKEventHandler {
	private final EventParser<T> parser;
	private final AppmarketEventHandler<T> eventHandler;

	ParseAndHandleWrapper(EventParser<T> parser, AppmarketEventHandler<T> eventHandler) {
		this.parser = parser;
		this.eventHandler = eventHandler;
	}

	@Override
	public APIResult handle(String consumerKeyUsedByTheRequest, EventInfo event, Map<String, String[]> queryParams) {
		T parsedEvent = parser.parse(consumerKeyUsedByTheRequest, event, queryParams);
		return eventHandler.handle(parsedEvent);
	}
}