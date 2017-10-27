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
package de.hybris.platform.personalizationyprofile.service.impl;

import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.IDENTITY_NODE;
import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.PRODUCT_AFFINITY_NODE;
import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.PROFILE_NODE;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationyprofile.Neighbour;
import de.hybris.platform.personalizationyprofile.service.YprofileNodeReadConfig;
import de.hybris.platform.personalizationyprofile.util.CxYaasConfigurationUtil;
import de.hybris.platform.personalizationyprofile.yaas.client.CxSecuredGraphClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCxYProfileDataServiceTest
{
	private static final String USER_UID = "user@hybris.com";
	private static final String PROFILE_ID = "profileId";
	private static final String PRODUCT_AFFINITY_ID = "productAffinityId";
	private static final String CATEGORY_ID = "categoryId";
	private static final String PRODUCT_ID = "productId";
	private static final String IDENTITY_INCLUDE_PARAM = "include=relations/core/Identity/core/Profile/core/IDENTIFIES";
	private static final String PROFILE_INCLUDE_PARAM = "include=relations/core/Profile/commerce/Category/commerce/HAS_AFFINITY&include=relations/core/Profile/commerce/ProductAffinity/core/HAS";
	private static final String PROFILE_INCLUDE_PARAM_1 = "include=relations/core/Profile/commerce/ProductAffinity/core/HAS&include=relations/core/Profile/commerce/Category/commerce/HAS_AFFINITY";
	private static final String PRODUCT_AFFINITY_INCLUDE_PARAM = "include=relations/commerce/ProductAffinity/commerce/Product/commerce/TO";


	private final DefaultCxYprofileDataService cxYprofileDataService = new DefaultCxYprofileDataService();
	@Mock
	private CxSecuredGraphClient cxSecuredGraphClient;
	@Mock
	private CxYaasConfigurationUtil yaasConfigurationUtil;
	@Mock
	private UserModel user;
	@Mock
	private List<Neighbour> yprofileData;

	private String encodedUserId;
	private YprofileNodeReadConfig identityConfig;
	private YprofileNodeReadConfig profileConfig;
	private YprofileNodeReadConfig productAffinityConfig;
	private Collection<YprofileNodeReadConfig> nodesReadConfig;
	private List<Neighbour> identityData;
	private List<Neighbour> profileData;
	private List<Neighbour> productAffinityData;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		cxYprofileDataService.setCxSecuredGraphClient(cxSecuredGraphClient);
		cxYprofileDataService.setCxYaasConfigurationUtil(yaasConfigurationUtil);
		createNodesReadConfiguration();

		Mockito.when(yaasConfigurationUtil.isConfigurationPresent(CxSecuredGraphClient.class)).thenReturn(true);
		Mockito.when(user.getUid()).thenReturn(USER_UID);
		encodedUserId = cxYprofileDataService.encodeUserId(USER_UID);
		createYprofileData();
	}

	@Test
	public void nodeConfigurationTest()
	{
		//given
		cxYprofileDataService.setNodesReadConfig(nodesReadConfig);

		//when
		cxYprofileDataService.postConstruct();

		//then
		Assert.assertEquals(3, cxYprofileDataService.getNodesReadConfigMap().size());
		Assert.assertTrue(cxYprofileDataService.getNodesReadConfigMap().containsKey(IDENTITY_NODE));
		Assert.assertTrue(cxYprofileDataService.getNodesReadConfigMap().containsKey(PROFILE_NODE));
		Assert.assertTrue(cxYprofileDataService.getNodesReadConfigMap().containsKey(PRODUCT_AFFINITY_NODE));

		Assert.assertEquals(3, cxYprofileDataService.getIncludeParamMap().size());
		Assert.assertTrue(cxYprofileDataService.getIncludeParamMap().containsKey(IDENTITY_NODE));
		Assert.assertTrue(cxYprofileDataService.getIncludeParamMap().containsKey(PROFILE_NODE));
		Assert.assertTrue(cxYprofileDataService.getIncludeParamMap().containsKey(PRODUCT_AFFINITY_NODE));
	}

	@Test
	public void mergeNodeConfigurationTest()
	{
		//given
		final YprofileNodeReadConfig additionalProfileConfig = createNodeConfig(PROFILE_NODE);
		additionalProfileConfig.getNodesToExpand().add("additionalNode");
		additionalProfileConfig.getIncludeRelations().add("relations/additionalRelation");
		nodesReadConfig.add(additionalProfileConfig);
		cxYprofileDataService.setNodesReadConfig(nodesReadConfig);

		//when
		cxYprofileDataService.postConstruct();

		//then
		Assert.assertEquals(3, cxYprofileDataService.getNodesReadConfigMap().size());
		final Set<String> nodesToExpand = cxYprofileDataService.getNodesReadConfigMap().get(PROFILE_NODE).getNodesToExpand();
		Assert.assertEquals(2, nodesToExpand.size());
		Assert.assertTrue(nodesToExpand.contains("additionalNode"));
		final Set<String> includeRelation = cxYprofileDataService.getNodesReadConfigMap().get(PROFILE_NODE).getIncludeRelations();
		Assert.assertEquals(3, includeRelation.size());
		Assert.assertTrue(includeRelation.contains("relations/additionalRelation"));

		Assert.assertEquals(3, cxYprofileDataService.getIncludeParamMap().size());
		final String includeParam = cxYprofileDataService.getIncludeParamMap().get(PROFILE_NODE);
		includeParam.contains("relations/additionalRelation");
	}

	@Test
	public void nullConfigurationTest()
	{
		//given
		cxYprofileDataService.setNodesReadConfig(null);

		//when
		cxYprofileDataService.postConstruct();

		//then
		Assert.assertTrue(cxYprofileDataService.getIncludeParamMap().isEmpty());
		Assert.assertTrue(cxYprofileDataService.getNodesReadConfigMap().isEmpty());
	}

	@Test
	public void getUserProfileTest()
	{
		//given
		cxYprofileDataService.setNodesReadConfig(nodesReadConfig);
		cxYprofileDataService.postConstruct();
		Mockito.when(cxSecuredGraphClient.getNeighbour(IDENTITY_NODE, encodedUserId, IDENTITY_INCLUDE_PARAM)).thenReturn(
				identityData);
		Mockito.when(cxSecuredGraphClient.getNeighbour(PROFILE_NODE, PROFILE_ID, PROFILE_INCLUDE_PARAM)).thenReturn(profileData);
		Mockito.when(cxSecuredGraphClient.getNeighbour(PROFILE_NODE, PROFILE_ID, PROFILE_INCLUDE_PARAM_1)).thenReturn(profileData);
		Mockito.when(cxSecuredGraphClient.getNeighbour(PRODUCT_AFFINITY_NODE, PRODUCT_AFFINITY_ID, PRODUCT_AFFINITY_INCLUDE_PARAM))
				.thenReturn(productAffinityData);

		//when
		final List<Neighbour> result = cxYprofileDataService.getUserProfile(user);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		final Neighbour profile = result.get(0);
		Assert.assertEquals(PROFILE_ID, profile.getId());
		Assert.assertNotNull(profile.getNeighbours());
		Assert.assertEquals(2, profile.getNeighbours().size());
		final Neighbour category = profile.getNeighbours().get(0);
		Assert.assertEquals(CATEGORY_ID, category.getId());
		Assert.assertNull(category.getNeighbours());
		final Neighbour productAffinity = profile.getNeighbours().get(1);
		Assert.assertEquals(PRODUCT_AFFINITY_ID, productAffinity.getId());
		Assert.assertNotNull(productAffinity.getNeighbours());
		Assert.assertEquals(1, productAffinity.getNeighbours().size());
		final Neighbour product = productAffinity.getNeighbours().get(0);
		Assert.assertEquals(PRODUCT_ID, product.getId());
		Assert.assertNull(product.getNeighbours());
	}

	@Test
	public void getUserProfileWithEmptyNodeConfigurationTest()
	{
		//given
		cxYprofileDataService.setNodesReadConfig(Collections.emptyList());
		cxYprofileDataService.postConstruct();
		Mockito.when(cxSecuredGraphClient.getNeighbour(IDENTITY_NODE, encodedUserId)).thenReturn(identityData);

		//when
		final List<Neighbour> result = cxYprofileDataService.getUserProfile(user);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		final Neighbour profile = result.get(0);
		Assert.assertEquals(PROFILE_ID, profile.getId());
		Assert.assertNull(profile.getNeighbours());
	}


	@Test
	public void getUserProfileWithoutProfileDataTest()
	{
		//given
		cxYprofileDataService.setNodesReadConfig(nodesReadConfig);
		cxYprofileDataService.postConstruct();
		identityData = Arrays.asList(createNeighbour(encodedUserId, "https://api.yaas.io/metamodel/v1/nodes/core/Identity"));
		Mockito.when(cxSecuredGraphClient.getNeighbour(IDENTITY_NODE, encodedUserId, IDENTITY_INCLUDE_PARAM)).thenReturn(
				identityData);

		//when
		final List<Neighbour> result = cxYprofileDataService.getUserProfile(user);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void getUserProfileWithoutYaasConfigurationTest()
	{
		//given
		Mockito.when(yaasConfigurationUtil.isConfigurationPresent(CxSecuredGraphClient.class)).thenReturn(false);

		//when
		final List<Neighbour> result = cxYprofileDataService.getUserProfile(user);

		//then
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}

	private void createNodesReadConfiguration()
	{
		identityConfig = createNodeConfig(IDENTITY_NODE);
		identityConfig.getNodesToExpand().add(PROFILE_NODE);
		identityConfig.getIncludeRelations().add("relations/core/Identity/core/Profile/core/IDENTIFIES");

		profileConfig = createNodeConfig(PROFILE_NODE);
		profileConfig.getNodesToExpand().add(PRODUCT_AFFINITY_NODE);
		profileConfig.getIncludeRelations().add("relations/core/Profile/commerce/Category/commerce/HAS_AFFINITY");
		profileConfig.getIncludeRelations().add("relations/core/Profile/commerce/ProductAffinity/core/HAS");

		productAffinityConfig = createNodeConfig(PRODUCT_AFFINITY_NODE);
		productAffinityConfig.getIncludeRelations().add("relations/commerce/ProductAffinity/commerce/Product/commerce/TO");

		nodesReadConfig = new ArrayList<YprofileNodeReadConfig>();
		nodesReadConfig.add(identityConfig);
		nodesReadConfig.add(profileConfig);
		nodesReadConfig.add(productAffinityConfig);
	}

	private YprofileNodeReadConfig createNodeConfig(final String nodeTypeId)
	{
		final YprofileNodeReadConfig nodeConfig = new YprofileNodeReadConfig();
		nodeConfig.setNodeTypeId(nodeTypeId);
		final Set<String> nodesToExpand = new HashSet<String>();
		nodeConfig.setNodesToExpand(nodesToExpand);
		final Set<String> includeRelations = new HashSet<String>();
		nodeConfig.setIncludeRelations(includeRelations);
		return nodeConfig;
	}

	private void createYprofileData()
	{
		final Neighbour identity = createNeighbour(encodedUserId, "https://api.yaas.io/metamodel/v1/nodes/core/Identity");
		Neighbour profile = createNeighbour(PROFILE_ID, "https://api.yaas.io/metamodel/v1/nodes/core/Profile");
		identityData = Arrays.asList(identity, profile);

		profile = createNeighbour(PROFILE_ID, "https://api.yaas.io/metamodel/v1/nodes/core/Profile");
		Neighbour productAffinity = createNeighbour(PRODUCT_AFFINITY_ID,
				"https://api.yaas.io/metamodel/v1/nodes/commerce/ProductAffinity");
		final Neighbour category = createNeighbour(CATEGORY_ID, "https://api.yaas.io/metamodel/v1/nodes/commerce/Category");
		profileData = Arrays.asList(profile, category, productAffinity);

		productAffinity = createNeighbour(PRODUCT_AFFINITY_ID, "https://api.yaas.io/metamodel/v1/nodes/commerce/ProductAffinity");
		final Neighbour product = createNeighbour(PRODUCT_ID, "https://api.yaas.io/metamodel/v1/nodes/commerce/Product");
		productAffinityData = Arrays.asList(productAffinity, product);
	}

	private Neighbour createNeighbour(final String id, final String schema)
	{
		final Neighbour neighbour = new Neighbour();
		neighbour.setId(id);
		neighbour.setSchema(schema);
		return neighbour;
	}
}
