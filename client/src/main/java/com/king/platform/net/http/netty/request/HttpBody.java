// Copyright (C) king.com Ltd 2015
// https://github.com/king/king-http-client
// Author: Magnus Gustafsson
// License: Apache 2.0, https://raw.github.com/king/king-http-client/LICENSE-APACHE

package com.king.platform.net.http.netty.request;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.nio.charset.Charset;

public interface HttpBody {
	long getContentLength();

	String getContentType();

	Charset getCharacterEncoding();

	ChannelFuture writeContent(ChannelHandlerContext ctx, boolean isSecure) throws IOException;
}
