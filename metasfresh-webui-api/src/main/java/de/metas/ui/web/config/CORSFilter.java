package de.metas.ui.web.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/*
 * #%L
 * metasfresh-webui-api
 * %%
 * Copyright (C) 2016 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@Component
public class CORSFilter implements Filter
{
	public CORSFilter()
	{
		super();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		if (response instanceof HttpServletResponse)
		{
			// FIXME: allow CORS for the whole application !!!

			HttpServletResponse httpResponse = (HttpServletResponse)response;
			httpResponse.setHeader("Access-Control-Allow-Origin", "*");
			httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
			httpResponse.setHeader("Access-Control-Max-Age", "3600");

			// adding one more allowed header as requested by @damianprzygodzki to fix the error
			// "Content-Type is not allowed by Access-Control-Allow-Headers"
			// httpResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with");
			httpResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with, Content-Type");
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy()
	{
	}

}