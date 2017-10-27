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
package de.hybris.platform.cmsfacades.synchronization.validator;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_DOES_NOT_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cmswebservices.data.SyncJobRequestData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates the DTO of {@link SyncJobData} for {@link SynchronizationFacade}.
 * <p>
 * Rules:</br>
 * <ul>
 * <li>Catalog ID cannot be null</li>
 * <li>A catalog should exist with Catalog ID</li>
 * <li>Source Catalog Version cannot be null</li>
 * <li>A catalog version should exist with Source Catalog Version</li>
 * <li>Target Catalog Version cannot be null</li>
 * <li>A catalog version should exist with Target Catalog Version</li>
 * <li>syncJobRequest cannot be null</li>
 * </ul>
 * </p>
 */
public class SyncJobRequestValidator implements Validator
{
	private static final Logger LOG = LoggerFactory.getLogger(SyncJobRequestValidator.class);

	public static final String SYNCJOBREQUEST = "SyncJobRequest";
	public static final String CATALOG = "CatalogId";
	public static final String SOURCE_VERSION = "SourceVersionId";
	public static final String TARGET_VERSION = "TargetVersionId";

	private CatalogVersionService catalogVersionService;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return clazz.isAssignableFrom(SyncJobRequestData.class);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final SyncJobRequestData syncJobData = (SyncJobRequestData) obj;
		if (Objects.isNull(syncJobData))
		{
			errors.rejectValue(SYNCJOBREQUEST, FIELD_REQUIRED);
		}
		else
		{

			if (Objects.isNull(syncJobData.getCatalogId()))
			{
				errors.rejectValue(CATALOG, FIELD_REQUIRED);
			}
			else
			{
				if (!isFindableCatalogById(syncJobData.getCatalogId()))
				{
					errors.rejectValue(CATALOG, FIELD_DOES_NOT_EXIST);
				}
				else
				{
					if (Objects.isNull(syncJobData.getTargetVersionId()))
					{
						errors.rejectValue(TARGET_VERSION, FIELD_REQUIRED);
					}
					else
					{
						if (!isFindableCatalogByIdVersion(syncJobData.getCatalogId(), syncJobData.getTargetVersionId()))
						{
							errors.rejectValue(TARGET_VERSION, FIELD_DOES_NOT_EXIST);
						}
					}
					if (Objects.isNull(syncJobData.getSourceVersionId()))
					{
						errors.rejectValue(SOURCE_VERSION, FIELD_REQUIRED);
					}
					else
					{
						if (!isFindableCatalogByIdVersion(syncJobData.getCatalogId(), syncJobData.getSourceVersionId()))
						{
							errors.rejectValue(SOURCE_VERSION, FIELD_DOES_NOT_EXIST);
						}
					}
				}
			}
		}

	}

	/**
	 * Check if the catalog and version exist
	 *
	 * @param catalogId
	 *           {@link String} the catalog ID
	 * @param version
	 *           the version id
	 * @return true if the object was found.
	 */
	protected boolean isFindableCatalogByIdVersion(final String catalogId, final String version)
	{

		boolean result = false;
		try
		{
			result = !Objects.isNull(getCatalogVersionService().getCatalogVersion(catalogId, version));
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.info(e.getMessage());
		}
		return result;
	}


	/**
	 * Check if the catalog exist
	 *
	 * @param catalogId
	 * @return true if the catalog exists
	 */
	protected boolean isFindableCatalogById(final String catalogId)
	{
		return getCatalogVersionService().getAllCatalogVersions().stream()
				.anyMatch(cat -> cat.getCatalog().getId().equals(catalogId));
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

}
