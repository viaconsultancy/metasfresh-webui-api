package de.metas.ui.web.process.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.metas.ui.web.process.ProcessInstanceResult;

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

@SuppressWarnings("serial")
public class JSONProcessInstanceResult implements Serializable
{
	public static final JSONProcessInstanceResult of(final ProcessInstanceResult result)
	{
		return new JSONProcessInstanceResult(result);
	}

	@JsonProperty("summary")
	private final String summary;
	@JsonProperty("error")
	private final boolean error;
	@JsonProperty("reportContentType")
	private final String reportContentType;
	@JsonProperty("reportData")
	private final byte[] reportData;

	private JSONProcessInstanceResult(final ProcessInstanceResult result)
	{
		super();
		summary = result.getSummary();
		error = result.isError();
		reportContentType = result.getReportContentType();

		final byte[] reportData = result.getReportData();
		this.reportData = reportData == null || reportData.length == 0 ? null : reportData;
	}

	public String getSummary()
	{
		return summary;
	}

	public boolean isError()
	{
		return error;
	}

	public String getReportContentType()
	{
		return reportContentType;
	}

	public byte[] getReportData()
	{
		return reportData;
	}
}