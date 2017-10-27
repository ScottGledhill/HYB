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
package de.hybris.platform.sapprodrecobuffer.cronjobs;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.sapprodrecobuffer.service.SapRecommendationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class SapRecommendationCleanupCronJob extends AbstractJobPerformable<CronJobModel> {

	private static final Logger LOG = Logger.getLogger(SapRecommendationCleanupCronJob.class);
	private SapRecommendationService recommendationBufferService;
	
	@Override
	public PerformResult perform(CronJobModel cronJobModel) {
		recommendationBufferService.removeExpiredRecommendations();		
		recommendationBufferService.removeExpiredMappings();
		recommendationBufferService.removeExpiredTypeMappings();
		LOG.info("SapRecommendationCleanupCronJob success");
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}
	
	@Required
	public void setRecommendationBufferService(SapRecommendationService recommendationBufferService) {
		this.recommendationBufferService = recommendationBufferService;
	}
	
	public SapRecommendationService getRecommendationBufferService() {
		return recommendationBufferService;
	}

}
