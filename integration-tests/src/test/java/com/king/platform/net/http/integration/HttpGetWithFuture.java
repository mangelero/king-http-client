// Copyright (C) king.com Ltd 2015
// https://github.com/king/king-http-client
// Author: Magnus Gustafsson
// License: Apache 2.0, https://raw.github.com/king/king-http-client/LICENSE-APACHE

package com.king.platform.net.http.integration;


import com.king.platform.net.http.HttpClient;
import com.king.platform.net.http.HttpResponse;
import com.king.platform.net.http.netty.ConnectionClosedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class HttpGetWithFuture {
	IntegrationServer integrationServer;
	private HttpClient httpClient;
	private int port;

	private String okBody = "EVERYTHING IS OKAY!";

	@BeforeEach
	public void setUp() throws Exception {
		integrationServer = new JettyIntegrationServer(5000);
		integrationServer.start();
		port = integrationServer.getPort();

		httpClient = new TestingHttpClientFactory().create();
		httpClient.start();

	}


	@Test
	public void get200WithClose() throws Exception {

		integrationServer.addServlet(new HttpServlet() {
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				resp.addHeader("Connection", "close");
				resp.getWriter().write(okBody);
				resp.getWriter().flush();
			}
		}, "/testOk");


		HttpResponse<String> response = httpClient.createGet("http://localhost:" + port + "/testOk").build().execute().get(200, TimeUnit.MILLISECONDS);

		assertEquals(okBody, response.getBody());
		assertEquals(200, response.getStatusCode());

	}

	@Test
	public void get200() throws Exception {

		integrationServer.addServlet(new HttpServlet() {
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				resp.getWriter().write(okBody);
				resp.getWriter().flush();
			}
		}, "/testOk");

		HttpResponse<String> response = httpClient.createGet("http://localhost:" + port + "/testOk").build().execute().get(200, TimeUnit.MILLISECONDS);

		assertEquals(okBody, response.getBody());
		assertEquals(200, response.getStatusCode());

	}

	@Test
	public void get200WithQuotedContentType() throws Exception {
		integrationServer.addServlet(new HttpServlet() {
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				resp.setHeader("Content-Type", "text/html; charset=\"utf-8\"");
				resp.getWriter().write(okBody);
				resp.getWriter().flush();
			}
		}, "/testOk");

		HttpResponse<String> response = httpClient.createGet("http://localhost:" + port + "/testOk").build().execute().join();

		assertEquals(okBody, response.getBody());
		assertEquals(200, response.getStatusCode());
	}

	@Test
	public void get200WithContentLength() throws Exception {
		integrationServer.addServlet(new HttpServlet() {
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				resp.addHeader("Connection", "close");

				ServletOutputStream outputStream = resp.getOutputStream();
				byte[] bytes = okBody.getBytes(StandardCharsets.UTF_8);

				resp.setContentLength(bytes.length);

				outputStream.write(bytes);
				outputStream.flush();
			}
		}, "/testOk");

		HttpResponse<String> response = httpClient.createGet("http://localhost:" + port + "/testOk").build().execute().get(200, TimeUnit.MILLISECONDS);

		assertEquals(okBody, response.getBody());
		assertEquals(200, response.getStatusCode());

	}


	@Test
	public void get200WithIncorrectContentLength() throws Exception {
		integrationServer.addServlet(new HttpServlet() {
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				resp.addHeader("Connection", "close");

				ServletOutputStream outputStream = resp.getOutputStream();
				byte[] bytes = okBody.getBytes(StandardCharsets.UTF_8);

				resp.setContentLength(2000);

				outputStream.write(bytes);
				outputStream.flush();
			}
		}, "/testOk");


		try {
			httpClient.createGet("http://localhost:" + port + "/testOk").build().execute().get(200, TimeUnit.MILLISECONDS);
			fail("Should have thrown exception");
		} catch (ExecutionException ee) {
			Throwable cause = ee.getCause();
			assertTrue(cause instanceof ConnectionClosedException);
		}

	}


	@Test
	public void get404() throws Exception {

		integrationServer.addServlet(new HttpServlet() {
			@Override
			protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				resp.setStatus(404);
			}
		}, "/test404");


		HttpResponse<String> response = httpClient.createGet("http://localhost:" + port + "/test404").build().execute().join();
		assertEquals("", response.getBody());
		assertEquals(404, response.getStatusCode());
	}


	@AfterEach
	public void tearDown() throws Exception {
		integrationServer.shutdown();
		httpClient.shutdown();

	}


}
