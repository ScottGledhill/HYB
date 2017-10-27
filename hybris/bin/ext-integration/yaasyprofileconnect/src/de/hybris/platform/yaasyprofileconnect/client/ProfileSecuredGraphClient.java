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
package de.hybris.platform.yaasyprofileconnect.client;

import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;
import rx.Observable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;


@Http("profileSecuredGraph")
@OAuth
public interface ProfileSecuredGraphClient
{
	/**
	 * Returns all nodes which are NO FURTHER AWAY THAN JUST 1 EDGE from the uniquely defined node (e.g. session).
	 * The first returned node is a source node.
	 *
	 * The tenant string is the value of the project's Identifier from the Builder
	 * @param namespace source node namespace for relation
	 * @param type source node type for relation
	 * @param id (pattern: ^([a-zA-Z0-9_%~.-]*)$), native node id of relation start (special characters shall be URL encoded)
     * @return
     */
	@GET
	@Path("/${tenant}/neighbours/{fromNamespace}/{fromType}/{fromId}")
	@OAuth(scope = "hybris.profile_graph_view")
	Observable<List<NeighbourData>> getNeighbours(@PathParam("fromNamespace") String namespace,
			@PathParam("fromType") String type,
			@PathParam("fromId") String id);
}

