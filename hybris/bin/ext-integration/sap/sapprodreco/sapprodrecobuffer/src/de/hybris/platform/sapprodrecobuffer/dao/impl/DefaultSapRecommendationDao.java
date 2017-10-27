/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sapprodrecobuffer.dao.impl;

import de.hybris.platform.sapprodrecobuffer.dao.SapRecommendationDao;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecoTypeMappingModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationBufferModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationMappingModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default interface implementation
 */
public class DefaultSapRecommendationDao implements SapRecommendationDao
{
	private FlexibleSearchService flexibleSearchService;


	@Override
	public List<SAPRecommendationBufferModel> findRecommendation(final String scenarioId, final String hashIds,
			final String leadingItems)
	{
		final HashMap<String, String> params = new HashMap<String, String>();

		final String queryString = "SELECT {" + SAPRecommendationBufferModel.PK + "} " //
				+ "FROM {" + SAPRecommendationBufferModel._TYPECODE + "} " //
				+ "WHERE {" + SAPRecommendationBufferModel.SCENARIOID + "} = ?scenarioId " //
				+ "AND {" + SAPRecommendationBufferModel.LEADINGITEMS + "} = ?leadingItems " //
				+ "AND {" + SAPRecommendationBufferModel.HASHID + "} IN (?hashIds)";


		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(queryString);
		params.put("scenarioId", scenarioId);
		params.put("leadingItems", leadingItems);
		params.put("hashIds", hashIds);
		fQuery.addQueryParameters(params);

		final SearchResult<SAPRecommendationBufferModel> res = getFlexibleSearchService().search(fQuery);
		return res.getTotalCount() > 0 ? res.getResult() : Collections.emptyList();
	}


	@Override
	public List<SAPRecommendationMappingModel> findRecommendationMapping(final String userId, final String scenarioId)
	{
		final HashMap<String, String> params = new HashMap<String, String>();

		final String queryString = "SELECT {" + SAPRecommendationMappingModel.PK + "} " //
				+ "FROM {" + SAPRecommendationMappingModel._TYPECODE + "} " //
				+ "WHERE {" + SAPRecommendationMappingModel.USERID + "} = ?userId " //
				+ "AND {" + SAPRecommendationMappingModel.SCENARIOID + "} = ?scenarioId";

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(queryString);
		params.put("userId", userId);
		params.put("scenarioId", scenarioId);
		fQuery.addQueryParameters(params);

		final SearchResult<SAPRecommendationMappingModel> res = getFlexibleSearchService().search(fQuery);
		return res.getTotalCount() > 0 ? res.getResult() : Collections.emptyList();
	}

	@Override
	public List<SAPRecommendationMappingModel> findRecommendationMapping(final String userId, final String scenarioId,
			final String hashId)
	{
		final HashMap<String, String> params = new HashMap<String, String>();

		final String queryString = "SELECT {" + SAPRecommendationMappingModel.PK + "} " //
				+ "FROM {" + SAPRecommendationMappingModel._TYPECODE + "} " //
				+ "WHERE {" + SAPRecommendationMappingModel.USERID + "} = ?userId " //
				+ "AND {" + SAPRecommendationMappingModel.SCENARIOID + "} = ?scenarioId " //
				+ "AND {" + SAPRecommendationMappingModel.HASHID + "} = ?hashId";

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(queryString);
		params.put("userId", userId);
		params.put("scenarioId", scenarioId);
		params.put("hashId", hashId);
		fQuery.addQueryParameters(params);

		final SearchResult<SAPRecommendationMappingModel> res = getFlexibleSearchService().search(fQuery);
		return res.getTotalCount() > 0 ? res.getResult() : Collections.emptyList();
	}

	@Override
	public List<SAPRecoTypeMappingModel> findRecoTypeMapping(final String recoType, final String scenarioId)
	{

		final HashMap<String, String> params = new HashMap<String, String>();

		final String queryString = "SELECT {" + SAPRecoTypeMappingModel.PK + "} " //
				+ "FROM {" + SAPRecoTypeMappingModel._TYPECODE + "} " //
				+ "WHERE {" + SAPRecoTypeMappingModel.SCENARIOID + "} = ?scenarioId " //
				+ "AND {" + SAPRecoTypeMappingModel.RECOTYPE + "} = ?recoType";

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(queryString);
		params.put("scenarioId", scenarioId);
		params.put("recoType", recoType);
		fQuery.addQueryParameters(params);

		final SearchResult<SAPRecoTypeMappingModel> res = getFlexibleSearchService().search(fQuery);
		return res.getTotalCount() > 0 ? res.getResult() : Collections.emptyList();
	}


	@Override
	public List<SAPRecommendationBufferModel> findExpiredRecommendations(final Date expiredDate)
	{
		final HashMap<String, Date> params = new HashMap<String, Date>();

		final String queryString = "SELECT {" + SAPRecommendationBufferModel.PK + "} " //
				+ "FROM {" + SAPRecommendationBufferModel._TYPECODE + "} " //
				+ "WHERE {" + SAPRecommendationBufferModel.EXPIRESON + "} < ?expiredDate";

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(queryString);
		params.put("expiredDate", expiredDate);
		fQuery.addQueryParameters(params);

		final SearchResult<SAPRecommendationBufferModel> res = getFlexibleSearchService().search(fQuery);
		return res.getTotalCount() > 0 ? res.getResult() : Collections.emptyList();
	}


	@Override
	public List<SAPRecommendationMappingModel> findExpiredRecommendationMappings(final Date expiredDate)
	{
		final HashMap<String, Date> params = new HashMap<String, Date>();

		final String queryString = "SELECT {" + SAPRecommendationMappingModel.PK + "} " //
				+ "FROM {" + SAPRecommendationMappingModel._TYPECODE + "} " //
				+ "WHERE {" + SAPRecommendationMappingModel.EXPIRESON + "} < ?expiredDate";

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(queryString);
		params.put("expiredDate", expiredDate);
		fQuery.addQueryParameters(params);

		final SearchResult<SAPRecommendationMappingModel> res = getFlexibleSearchService().search(fQuery);
		return res.getTotalCount() > 0 ? res.getResult() : Collections.emptyList();
	}

	@Override
	public List<SAPRecoTypeMappingModel> findExpiredRecoTypeMappings(final Date expiredDate)
	{
		final HashMap<String, Date> params = new HashMap<String, Date>();

		final String queryString = "SELECT {" + SAPRecoTypeMappingModel.PK + "} " //
				+ "FROM {" + SAPRecoTypeMappingModel._TYPECODE + "} " //
				+ "WHERE {" + SAPRecoTypeMappingModel.EXPIRESON + "} < ?expiredDate";

		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(queryString);
		params.put("expiredDate", expiredDate);
		fQuery.addQueryParameters(params);

		final SearchResult<SAPRecoTypeMappingModel> res = getFlexibleSearchService().search(fQuery);
		return res.getTotalCount() > 0 ? res.getResult() : Collections.emptyList();
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}





}
