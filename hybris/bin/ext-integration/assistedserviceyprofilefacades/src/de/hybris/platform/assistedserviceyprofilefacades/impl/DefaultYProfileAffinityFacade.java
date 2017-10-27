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
package de.hybris.platform.assistedserviceyprofilefacades.impl;

import com.hybris.charon.exp.HttpException;
import de.hybris.platform.assistedservicefacades.customer360.exception.AIFProviderDataLoadingException;
import de.hybris.platform.assistedserviceyprofilefacades.YProfileAffinityFacade;
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.KeywordSearchAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.KeywordSearchAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.RecentlyUpdatedComparator;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.yaasyprofileconnect.client.ProfileSecuredGraphClient;
import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import com.hybris.charon.exp.HttpException;

import rx.Observable;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;
import rx.Observable;


/**
 * Default implementation of {@link YProfileAffinityFacade} that uses graph data from yprofile
 * {@link ProfileSecuredGraphClient#getNeighbours(String, String, String)}
 */
public class DefaultYProfileAffinityFacade implements YProfileAffinityFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultYProfileAffinityFacade.class);

	private UserService userService;
	private ProfileSecuredGraphClient profileSecuredGraphClient;

	private Converter<NeighbourData, ProductAffinityData> productAffinityConverter;
	private Converter<NeighbourData, CategoryAffinityData> categoryAffinityConverter;
	private Converter<NeighbourData, KeywordSearchAffinityData> keywordSearchAffinityConverter;

	@Override
	public List<ProductAffinityData> getProductAffinities(final ProductAffinityParameterData affinityParameterData)
	{

		final List<NeighbourData> sortedAndLimitedNeighbours = fetchAndSortAffinities(affinityParameterData.getSizeLimit(),
				YaasyprofileconnectConstants.SCHEMA_COMMERCE_PRODUCT_AFFINITY);

		//now convert the most recent NeighbourData to products list
		return Converters.convertAll(sortedAndLimitedNeighbours, getProductAffinityConverter());
	}

	@Override
	public List<CategoryAffinityData> getCategoryAffinities(final CategoryAffinityParameterData affinityParameterData)
	{
		final List<NeighbourData> sortedAndLimitedNeighbours = fetchAndSortAffinities(affinityParameterData.getSizeLimit(),
				YaasyprofileconnectConstants.SCHEMA_COMMERCE_CATEGORY_AFFINITY);

		//now convert the most recent NeighbourData to categories list
		return Converters.convertAll(sortedAndLimitedNeighbours, getCategoryAffinityConverter());
	}

	@Override
	public List<KeywordSearchAffinityData> getKeywordSearchAffinities(
			final KeywordSearchAffinityParameterData affinityParameterData)
	{

		final List<NeighbourData> sortedAndLimitedNeighbours = fetchAndSortAffinities(affinityParameterData.getSizeLimit(),
				YaasyprofileconnectConstants.SCHEMA_COMMERCE_KEYWORD_SEARCH_AFFINITY);

		//now convert the most recent NeighbourData to search keywords list
		return Converters.convertAll(sortedAndLimitedNeighbours, getKeywordSearchAffinityConverter());
	}

	/**
	 *
	 * This method is responsible for fetch neighbordata, in our case all affinities associated to profile and the
	 * affinity type and then sort based on most recently updated affinity
	 *
	 * @param sizeLimit
	 *           number of neighbors to return
	 * @param affinitySchema
	 *           type of affinity to return
	 *
	 * @return list of neighbor data after sorting and limit
	 *
	 */
	protected List<NeighbourData> fetchAndSortAffinities(final int sizeLimit, final String affinitySchema)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final List<NeighbourData> neighbours = Lists.newArrayList();
		try
		{
			//fetch all NeighbourData based on profile and affinity schema
			getProfileNeighboursByIdentity(encodeUid(currentCustomer.getUid())).toBlocking().toIterable()
					.forEach(profileList -> profileList.stream().parallel()
							.forEach(profileNode -> getAffinitiesNeighboursByProfile(profileNode.getId(), affinitySchema).toBlocking()
									.toIterable().forEach(neighbours::addAll)));
		}
		//exception happened during communicating with yprofile
		catch (final HttpException err)
		{
			err.getServerMessage().subscribe(message -> LOG.error(
					"Exception happend during communicating with YaaS while trying to get affinity with schema [" + affinitySchema
							+ "] for customer [" + currentCustomer.getUid() + "] with message '" + err.getMessage() + "'\n" + message));
		}

		//now from the list of top X recent affinities now we need to sort again to limit only to top X across all profiles
		final List<NeighbourData> sortedAndLimitedNeighbours = neighbours.stream()
				.parallel().sorted((final NeighbourData affinityData1,
						final NeighbourData affinityData2) -> new RecentlyUpdatedComparator().compare(affinityData1, affinityData2))
				.limit(sizeLimit).collect(Collectors.toList());

		return sortedAndLimitedNeighbours;

	}

	/**
	 * The customer uid need to encode twice. possible because it is email address format.
	 *
	 * @param uid
	 * @return
	 */
	protected String encodeUid(final String uid)
	{
		try
		{
			return URLEncoder.encode(URLEncoder.encode(uid, StandardCharsets.UTF_8.toString()), StandardCharsets.UTF_8.toString());
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new AIFProviderDataLoadingException("Encoding is unsupported!", e);
		}
	}

	/**
	 * Returns Profile Nodes for provided Identity
	 *
	 * @param customerUid
	 * @return
	 */
	protected Observable<List<NeighbourData>> getProfileNeighboursByIdentity(final String customerUid)
	{
		return getProfileSecuredGraphClient()
				.getNeighbours(YaasyprofileconnectConstants.NAMESPACE_CORE, YaasyprofileconnectConstants.TYPE_IDENTITY, customerUid)
				.map(list -> list.stream().filter(n -> n.getSchema().contains(YaasyprofileconnectConstants.SCHEMA_CORE_PROFILE))
						.collect(Collectors.toList()));
	}

	protected Observable<List<NeighbourData>> getAffinitiesNeighboursByProfile(final String profileId, final String affinitySchema)
	{
		return getProfileSecuredGraphClient()
				.getNeighbours(YaasyprofileconnectConstants.NAMESPACE_CORE, YaasyprofileconnectConstants.TYPE_PROFILE, profileId)
				.map(p -> p.stream().filter(n -> n.getSchema().contains(affinitySchema)).collect(Collectors.toList()));
	}

	protected void logError(final Throwable err)
	{
		if (err instanceof HttpException)
		{
			final HttpException exp = (HttpException) err;
			exp.getServerMessage().subscribe(message -> LOG.warn(exp.getMessage() + ": " + message));
			return;
		}
		LOG.error(err.getMessage(), err);
	}


	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected ProfileSecuredGraphClient getProfileSecuredGraphClient()
	{
		return profileSecuredGraphClient;
	}

	@Required
	public void setProfileSecuredGraphClient(final ProfileSecuredGraphClient profileSecuredGraphClient)
	{
		this.profileSecuredGraphClient = profileSecuredGraphClient;
	}

	protected Converter<NeighbourData, ProductAffinityData> getProductAffinityConverter()
	{
		return productAffinityConverter;
	}

	@Required
	public void setProductAffinityConverter(final Converter<NeighbourData, ProductAffinityData> productAffinityConverter)
	{
		this.productAffinityConverter = productAffinityConverter;
	}

	protected Converter<NeighbourData, CategoryAffinityData> getCategoryAffinityConverter()
	{
		return categoryAffinityConverter;
	}

	@Required
	public void setCategoryAffinityConverter(final Converter<NeighbourData, CategoryAffinityData> categoryAffinityConverter)
	{
		this.categoryAffinityConverter = categoryAffinityConverter;
	}

	public Converter<NeighbourData, KeywordSearchAffinityData> getKeywordSearchAffinityConverter()
	{
		return keywordSearchAffinityConverter;
	}

	@Required
	public void setKeywordSearchAffinityConverter(
			final Converter<NeighbourData, KeywordSearchAffinityData> keywordSearchAffinityConverter)
	{
		this.keywordSearchAffinityConverter = keywordSearchAffinityConverter;
	}
}