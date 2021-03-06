// Copyright (C) king.com Ltd 2015
// https://github.com/king/king-http-client
// Author: Magnus Gustafsson
// License: Apache 2.0, https://raw.github.com/king/king-http-client/LICENSE-APACHE

package com.king.platform.net.http.netty.requestbuilder;

import com.king.platform.net.http.netty.request.ByteArrayHttpBody;
import com.king.platform.net.http.netty.request.HttpBody;
import com.king.platform.net.http.netty.util.ParameterEncoder;
import com.king.platform.net.http.util.Param;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class FormParameterBodyBuilder implements RequestBodyBuilder {
	private static final ParameterEncoder parameterEncoder = new ParameterEncoder();

	private List<Param> parameters = new ArrayList<>();

	public FormParameterBodyBuilder() {
	}

	public FormParameterBodyBuilder(FormParameterBodyBuilder bodyBuilder) {
		parameters.addAll(bodyBuilder.parameters);
	}

	@Override
	public HttpBody createHttpBody(String contentType, Charset characterEncoding) {
		if (contentType == null) {
			contentType = "application/x-www-form-urlencoded";
		}

		StringBuilder content = new StringBuilder();
		for (Param parameter : parameters) {
			parameterEncoder.addParameter(content, parameter.getName(), parameter.getValue());
		}
		content.setLength(content.length() - 1);


		return new ByteArrayHttpBody(content.toString().getBytes(characterEncoding), contentType, characterEncoding);
	}

	public void addParameter(String name, String value) {
		parameters.add(new Param(name, value));
	}

	public void addParameters(Map<String, String> parameters) {
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			this.parameters.add(new Param(entry.getKey(), entry.getValue()));
		}
	}
}
