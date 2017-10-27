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
/**
 *
 */
package de.hybris.platform.permissionswebservices.controllers;

import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.security.permissions.PermissionAssignment;
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService;

import javax.annotation.Resource;


public class AbstractPermissionsWebServicesTest extends ServicelayerTest
{
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private PermissionManagementService permissionManagementService;

	protected void insertGlobalPermission(final String principalId, final String permission)
	{
		final PrincipalModel example = new PrincipalModel();
		example.setUid(principalId);
		final PrincipalModel principal = flexibleSearchService.getModelByExample(example);
		permissionManagementService.addGlobalPermission(new PermissionAssignment(permission, principal));
	}


}
