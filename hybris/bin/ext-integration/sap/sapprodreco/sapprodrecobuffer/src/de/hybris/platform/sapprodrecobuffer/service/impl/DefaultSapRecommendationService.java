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
package de.hybris.platform.sapprodrecobuffer.service.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sapprodrecobuffer.constants.SapprodrecobufferConstants;
import de.hybris.platform.sapprodrecobuffer.dao.SapRecommendationDao;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecoTypeMappingModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationBufferModel;
import de.hybris.platform.sapprodrecobuffer.model.SAPRecommendationMappingModel;
import de.hybris.platform.sapprodrecobuffer.service.SapRecommendationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class DefaultSapRecommendationService implements SapRecommendationService
{
	private SapRecommendationDao recommendationDao;
	private boolean enableRecommendationBuffer;
	private int expiryOffset;

	@Resource
	private ModelService modelService;

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSapRecommendationService.class);

	/**
	 * Returns comma separated string of hash id
	 */
	private String getHashIdsForUser(final String userId, final String scenarioId)
	{
		final List<SAPRecommendationMappingModel> mappings = recommendationDao.findRecommendationMapping(userId, scenarioId);
		return mappings.stream().map(SAPRecommendationMappingModel::getHashId).collect(Collectors.joining(","));
	}

	/**
	 * Returns comma separated string of hash id
	 */
	private String getHashIdsForType(final String scenarioId, final String recoType)
	{
		final List<SAPRecoTypeMappingModel> mappings = recommendationDao.findRecoTypeMapping(recoType, scenarioId);
		return mappings.stream().map(SAPRecoTypeMappingModel::getHashId).collect(Collectors.joining(","));
	}

	@Override
	public SAPRecommendationBufferModel getRecommendation(final String userId, final String scenarioId, final String leadingItems)
	{
		if (!enableRecommendationBuffer)
		{
			LOGGER.warn("SAP Recommendation buffer is disabled!");
			return null;
		}

		LOGGER.debug("Retrieving SAP recommendation from buffer for userId='{}', scenarioId='{}', leadingItems='{}'", //
				userId, scenarioId, leadingItems);

		final String hashIds = this.getHashIdsForUser(userId, scenarioId);
		final List<SAPRecommendationBufferModel> recommendations = //
				recommendationDao.findRecommendation(scenarioId, hashIds, leadingItems);

		return recommendations.isEmpty() ? null : recommendations.get(0);
	}

	/**
	 * Get recommendations with scope R. If none are found, get recommendations with scope G
	 */
	@Override
	public SAPRecommendationBufferModel getGenericRecommendation(final String scenarioId, final String leadingItems)
	{
		if (!enableRecommendationBuffer)
		{
			LOGGER.warn("SAP Recommendation buffer is disabled!");
			return null;
		}

		LOGGER.debug("Retrieving SAP recommendation from buffer for scenarioId='{}', leadingItems='{}'", //
				scenarioId, leadingItems);

		String hashIds = this.getHashIdsForType(scenarioId, SapprodrecobufferConstants.RESTRICTED_RECO_TYPE);

		if (hashIds.isEmpty())
		{
			hashIds = this.getHashIdsForType(scenarioId, SapprodrecobufferConstants.GENERIC_RECO_TYPE);
		}

		if (hashIds.isEmpty())
		{
			return null;
		}

		final List<SAPRecommendationBufferModel> recommendations = //
				recommendationDao.findRecommendation(scenarioId, hashIds, leadingItems);

		return recommendations.isEmpty() ? null : recommendations.get(0);
	}

	@Override
	public boolean isRecommendationExpired(final String scenarioId, final String hashId, final String leadingItems)
	{
		try
		{
			final List<SAPRecommendationBufferModel> recommendationList = //
					recommendationDao.findRecommendation(scenarioId, hashId, leadingItems);
			final Date expiresOn = recommendationList.get(0).getExpiresOn();
			return expiresOn.before(new Date());
		}
		catch (final Exception e)
		{
			LOGGER.error("Error checking isRecommendationExpired on scenarioId=" + scenarioId + " hashId=" + hashId
					+ " leadingItems=" + leadingItems, e);
			return true;
		}
	}

	@Override
	public boolean isRecommendationExpired(final SAPRecommendationBufferModel recommendation)
	{
		return recommendation.getExpiresOn().before(new Date());
	}

	protected void removeExpiredAny(final Function<Date, List<? extends ItemModel>> serviceMethod)
	{
		if (!enableRecommendationBuffer)
		{
			LOGGER.warn("SAP Recommendation buffer is disabled!");
			return;
		}

		LOGGER.debug("Removing expired SAP recommendations from buffer");

		try
		{
			final Date expiryDate = DateUtils.addDays(new Date(), -this.getExpiryOffset());
			serviceMethod.apply(expiryDate).stream().map(ItemModel::getPk).forEach(modelService::remove);
		}
		catch (final Exception e)
		{
			LOGGER.error("An error occurred while removing expired recommendations", e);
		}
	}

	@Override
	public void removeExpiredRecommendations()
	{
		this.removeExpiredAny(recommendationDao::findExpiredRecommendations);
	}

	@Override
	public void removeExpiredMappings()
	{
		this.removeExpiredAny(recommendationDao::findExpiredRecommendationMappings);
	}

	@Override
	public void removeExpiredTypeMappings()
	{
		this.removeExpiredAny(recommendationDao::findExpiredRecoTypeMappings);
	}

	@Override
	public synchronized void saveRecommendation(final String userId, final String scenarioId, final String hashId,
			final String leadingItems, final String recoList, final String recoType, final Date expiresOn)
	{
		if (!enableRecommendationBuffer)
		{
			LOGGER.warn("SAP Recommendation buffer is disabled!");
			return;
		}

		LOGGER.debug(
				"Saving SAP recommendation in buffer userId='{}', scenarioId='{}', hashId='{}', leadingItems='{}', recoList='{}', recoType='{}', expiresOn='{}'", //
				userId, scenarioId, hashId, leadingItems, recoList, recoType, expiresOn);

		if (!userId.isEmpty())
		{
			//Save User-Scenario-Hash mapping
			saveRecommendationMapping(userId, scenarioId, hashId, expiresOn);
		}

		if (SapprodrecobufferConstants.GENERIC_RECO_TYPE.equals(recoType)
				|| SapprodrecobufferConstants.RESTRICTED_RECO_TYPE.equals(recoType))
		{
			//Save Scenario-RecoType-Hash mapping
			saveRecommendationTypeMapping(scenarioId, hashId, recoType, expiresOn);
		}

		saveRecommendationBuffer(scenarioId, hashId, leadingItems, recoList, expiresOn);
	}

	private void saveRecommendationBuffer(final String scenarioId, final String hashId, final String leadingItems,
			final String recoList, final Date expiresOn)
	{
		try
		{
			final List<SAPRecommendationBufferModel> recoBufferList = //
					recommendationDao.findRecommendation(scenarioId, hashId, leadingItems);
			SAPRecommendationBufferModel recoModel = null;

			if (recoBufferList.isEmpty())
			{
				recoModel = modelService.create(SAPRecommendationBufferModel.class);
			}
			else
			{
				recoModel = modelService.get(recoBufferList.get(0).getPk());
			}

			recoModel.setScenarioId(scenarioId);
			recoModel.setHashId(hashId);
			recoModel.setLeadingItems(leadingItems);
			recoModel.setRecoList(recoList);
			recoModel.setExpiresOn(expiresOn);
			modelService.save(recoModel);
		}
		catch (final Exception e)
		{
			LOGGER.error("An error occurred while saving recommendation with scenarioId=" + scenarioId + " hashId=" + hashId
					+ " leadingItems=" + leadingItems, e);
		}
	}

	private void saveRecommendationMapping(final String userId, final String scenarioId, final String hashId, final Date expiresOn)
	{
		try
		{
			final List<SAPRecommendationMappingModel> recoMappingList = //
					recommendationDao.findRecommendationMapping(userId, scenarioId, hashId);
			SAPRecommendationMappingModel recoModel = null;

			if (recoMappingList.isEmpty())
			{
				recoModel = modelService.create(SAPRecommendationMappingModel.class);
			}
			else
			{
				recoModel = modelService.get(recoMappingList.get(0).getPk());
			}

			recoModel.setUserId(userId);
			recoModel.setScenarioId(scenarioId);
			recoModel.setHashId(hashId);
			recoModel.setExpiresOn(expiresOn);
			modelService.save(recoModel);
		}
		catch (final Exception e)
		{
			LOGGER.error("An error occurred while saving recommendation mapping with userId=" + userId + " scenarioId=" + scenarioId
					+ " hashId=" + hashId, e);
		}
	}

	private void saveRecommendationTypeMapping(final String scenarioId, final String hashId, final String recoType,
			final Date expiresOn)
	{
		try
		{
			final List<SAPRecoTypeMappingModel> recoMappingList = //
					recommendationDao.findRecoTypeMapping(recoType, scenarioId);
			SAPRecoTypeMappingModel recoModel = null;

			if (recoMappingList.isEmpty())
			{
				recoModel = modelService.create(SAPRecoTypeMappingModel.class);
			}
			else
			{
				recoModel = modelService.get(recoMappingList.get(0).getPk());
			}

			recoModel.setScenarioId(scenarioId);
			recoModel.setHashId(hashId);
			recoModel.setRecoType(recoType);
			recoModel.setExpiresOn(expiresOn);
			modelService.save(recoModel);
		}
		catch (final Exception e)
		{
			LOGGER.error(
					"An error occurred while saving recommendation type mapping with scenarioId=" + scenarioId + " hashId=" + hashId,
					e);
		}
	}

	public SapRecommendationDao getRecommendationDao()
	{
		return recommendationDao;
	}

	@Required
	public void setRecommendationDao(final SapRecommendationDao recommendationDao)
	{
		this.recommendationDao = recommendationDao;
	}

	/**
	 * @return the expiryOffset
	 */
	public int getExpiryOffset()
	{
		return expiryOffset;
	}

	/**
	 * @param expiryOffset
	 *           the expiryLimit to set
	 */
	public void setExpiryOffset(final int expiryOffset)
	{
		this.expiryOffset = expiryOffset;
	}

	/**
	 * @return the enableRecommendationBuffer
	 */
	public boolean isEnableRecommendationBuffer()
	{
		return enableRecommendationBuffer;
	}

	/**
	 * @param enableRecommendationBuffer
	 *           the enableRecommendationBuffer to set
	 */
	public void setEnableRecommendationBuffer(final boolean enableRecommendationBuffer)
	{
		this.enableRecommendationBuffer = enableRecommendationBuffer;
	}

}
