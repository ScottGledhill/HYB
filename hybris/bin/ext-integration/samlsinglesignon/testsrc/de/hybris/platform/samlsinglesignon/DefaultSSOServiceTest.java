/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 *
 */
package de.hybris.platform.samlsinglesignon;

import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;


@UnitTest
public class DefaultSSOServiceTest extends ServicelayerTest
{
	@InjectMocks
	private DefaultSSOService defaultSSOService;
	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;

	private final String employeeRole = "Employee";
	private final String asagentSalesManagerGroup = "asagentsalesmanagergroup";

	@Before
	public void setup()
	{
		Config.setParameter("sso.mapping.asagentgroup.usertype", employeeRole); // NOSONAR
		Config.setParameter("sso.mapping.asagentgroup.groups", asagentSalesManagerGroup); // NOSONAR
		defaultSSOService = new DefaultSSOService();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCreateSSOUser()
	{
		final String id = "id12345";
		final String userName = "testUserName";
		final String asagentGroup = "asagentgroup";

		EmployeeModel employeeModel = Mockito.mock(EmployeeModel.class);
		UserGroupModel asagentSalesManagerGroupModel = Mockito.mock(UserGroupModel.class);
		Mockito.when(userService.getUserForUID(id)).thenThrow(UnknownIdentifierException.class);
		Mockito.when(modelService.create(employeeRole)).thenReturn(employeeModel);
		Mockito.when(userService.getUserGroupForUID(asagentSalesManagerGroup)).thenReturn(asagentSalesManagerGroupModel);

		UserModel user = defaultSSOService.getOrCreateSSOUser(id, userName, Arrays.asList(asagentGroup));

		Mockito.verify(user).setUid(id);
		Mockito.verify(user).setName(userName);
		Mockito.verify(user).setGroups(Sets.newHashSet(asagentSalesManagerGroupModel));
		Mockito.verify(userService).setPassword(Mockito.eq(user), Mockito.anyString(), Mockito.eq("md5"));
		Mockito.verify(modelService).save(user);
	}

	@Test
	public void shouldGetSSOUser()
	{
		final String id = "id12345";
		final String userName = "testUserName";
		final String asagentGroup = "asagentgroup";

		EmployeeModel employeeModel = Mockito.mock(EmployeeModel.class);
		UserGroupModel asagentSalesManagerGroupModel = Mockito.mock(UserGroupModel.class);
		Mockito.when(userService.getUserForUID(id)).thenReturn(employeeModel);
		Mockito.when(userService.getUserGroupForUID(asagentSalesManagerGroup)).thenReturn(asagentSalesManagerGroupModel);

		UserModel user = defaultSSOService.getOrCreateSSOUser(id, userName, Arrays.asList(asagentGroup));

		Mockito.verify(user).setGroups(Sets.newHashSet(asagentSalesManagerGroupModel));
		Assert.assertEquals(employeeModel, user);
		Mockito.verify(modelService).save(user);

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenNoRoleFound()
	{

		final String id = "id12345";
		final String userName = "testUserName";
		final String asagentGroup = "otherGroup";

		defaultSSOService.getOrCreateSSOUser(id, userName, Arrays.asList(asagentGroup));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWheAmbigousRoleFound()
	{

		Config.setParameter("sso.mapping.asagentgroup1.usertype", "Employee1"); // NOSONAR
		Config.setParameter("sso.mapping.asagentgroup1.groups", asagentSalesManagerGroup); // NOSONAR

		final String id = "id12345";
		final String userName = "testUserName";
		final String asagentGroup = "asagentgroup";

		defaultSSOService.getOrCreateSSOUser(id, userName, Arrays.asList(asagentGroup, "asagentgroup1"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenIdEmpty()
	{
		final String id = StringUtils.EMPTY;
		final String userName = "testUserName";
		final String asagentGroup = "asagentgroup";


		defaultSSOService.getOrCreateSSOUser(id, userName, Arrays.asList(asagentGroup));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenIdNameEmpty()
	{
		final String id = "id12345";
		final String userName = StringUtils.EMPTY;
		final String asagentGroup = "asagentgroup";


		defaultSSOService.getOrCreateSSOUser(id, userName, Arrays.asList(asagentGroup));
	}

}
