/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.personalizationyprofile.yaas.client;

import de.hybris.platform.personalizationyprofile.Neighbour;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;


/**
 *
 */
public class MockCxSecuredGraphClient implements CxSecuredGraphClient
{

	@Override
	public List<Neighbour> getNeighbour(final String nodeType, final String id)
	{
		return new ArrayList<>();
	}

	@Override
	public List<Neighbour> getNeighbour(final String nodeType, final String id, final String params)
	{
		return new ArrayList<>();
	}

	@Override
	public Observable<List<Neighbour>> getNeighbourAsync(final String nodeType, final String id)
	{
		return Observable.from(new ArrayList());
	}

	@Override
	public Observable<List<Neighbour>> getNeighbourAsync(final String nodeType, final String id, final String params)
	{
		return Observable.from(new ArrayList());
	}

}
