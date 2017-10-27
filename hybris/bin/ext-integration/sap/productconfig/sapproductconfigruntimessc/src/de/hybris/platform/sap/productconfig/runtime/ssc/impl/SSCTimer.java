/*
 * [y] hybris Platform
 *
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import org.apache.log4j.Logger;


/**
 * Helper class to enable to simple performance measurements of configuration engine calls. It will simple trace the
 * duration of every configuration engine call and log it with DEBUG severity.<br>
 * Set the LogLevel for this class to debug to enable the measurement.
 */
public class SSCTimer
{
	private static final Logger LOG = Logger.getLogger(SSCTimer.class);


	private long startTime;
	private long endTime;


	private String step;

	/**
	 * Default Constructor, will initiate the measurement.
	 */
	public SSCTimer()
	{
		super();
		startTime = System.nanoTime();
	}

	/**
	 * Stops the measurement and logs the result.
	 */
	public void stop()
	{
		endTime = System.nanoTime();
		logTime();
	}

	protected void logTime()
	{
		if (!LOG.isDebugEnabled())
		{
			return;
		}
		final long timeInNanos = endTime - startTime;
		final long timeInMs = timeInNanos / (1000 * 1000);

		LOG.debug("Call to SSC (" + step + ") took " + timeInMs + "ms");
	}

	/**
	 * Will re-initiate the measurement.
	 *
	 * @param step
	 *           name of the configuration engine call, will be logged together with the result.
	 */
	public void start(final String step)
	{
		this.step = step;
		startTime = System.nanoTime();
	}

}
