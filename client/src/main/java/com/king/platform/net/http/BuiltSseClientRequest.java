// Copyright (C) king.com Ltd 2016
// https://github.com/king/king-http-client
// Author: Magnus Gustafsson
// License: Apache 2.0, https://raw.github.com/king/king-http-client/LICENSE-APACHE

package com.king.platform.net.http;


public interface BuiltSseClientRequest {
	/**
	 * Build the SseClient and execute it against the server
	 *
	 * @param sseExecutionCallback the callback object
	 * @return the built SseClient
	 */
	SseClient execute(SseClientCallback sseExecutionCallback);

	/**
	 * Build the SseClient and execute it against the server
	 *
	 * @return the built SseClient
	 */
	SseClient execute();


	/**
	 * Build the SseClient but don't connect it agaoinst the server.
	 * The client can later be connected using the {@link SseClient#connect()} method.
	 *
	 * @return the built SseClient
	 */
	SseClient build();

}
