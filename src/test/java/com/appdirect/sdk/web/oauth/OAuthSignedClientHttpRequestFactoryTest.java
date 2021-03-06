/*
 * Copyright 2017 AppDirect, Inc. and/or its affiliates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appdirect.sdk.web.oauth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthMessageSignerException;

public class OAuthSignedClientHttpRequestFactoryTest {
	private OAuthSignedClientHttpRequestFactory requestFactory;

	@Before
	public void setup() throws Exception {
		requestFactory = new OAuthSignedClientHttpRequestFactory("some-key", "some-secret");
	}

	@Test
	public void prepareConnection_signsRequest() throws Exception {
		HttpURLConnection connection = connectionTo("http://some-domain.com/?p1=v1");

		requestFactory.prepareConnection(connection, "GET");

		verify(connection).setRequestMethod("GET");
		verify(connection).setRequestProperty(eq("Authorization"), startsWith("OAuth oauth_consumer_key=\"some-key\""));
	}

	@Test
	public void prepareConnection_setsTimeoutsToDefaults() throws Exception {
		HttpURLConnection connection = connectionTo("http://some-other.com");

		requestFactory.prepareConnection(connection, "GET");

		verify(connection).setConnectTimeout(10_000);
		verify(connection).setReadTimeout(60_000);
	}

	@Test
	public void prepareConnection_throwsExceptionWhenSigningFailed() throws Exception {
		OAuthConsumer someCrashingConsumer = mock(OAuthConsumer.class);
		when(someCrashingConsumer.sign(any(Object.class))).thenThrow(new OAuthMessageSignerException("could not sign :("));

		requestFactory = new OAuthSignedClientHttpRequestFactory(someCrashingConsumer);

		assertThatThrownBy(() -> requestFactory.prepareConnection(connectionTo("http://some-domain.com"), "GET"))
				.hasMessage("Could not sign request to http://some-domain.com")
				.hasCauseExactlyInstanceOf(OAuthMessageSignerException.class);
	}

	@Test
	public void prepareConnection_acceptsRedirects() throws Exception {
		HttpURLConnection connection = connectionTo("http://some-domain.com/?p1=v1");

		requestFactory.prepareConnection(connection, "POST");

		verify(connection).setInstanceFollowRedirects(true);
	}

	private HttpURLConnection connectionTo(String url) throws IOException {
		HttpURLConnection connection = mock(HttpURLConnection.class);
		when(connection.getURL()).thenReturn(URI.create(url).toURL());

		return connection;
	}
}
