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
package de.hybris.platform.cmsfacades.util.models;

import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.ONLINE;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED1;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED2;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_LAPTOPS;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_PHONES;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import de.hybris.platform.catalog.daos.CatalogVersionDao;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemCronJobModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.cmsfacades.util.builder.CatalogVersionModelBuilder;
import de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate;
import de.hybris.platform.servicelayer.cronjob.CronJobService;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;


public class CatalogVersionModelMother extends AbstractModelMother<CatalogVersionModel>
{
	public static enum CatalogVersion
	{
		ONLINE, STAGED, STAGED1, STAGED2;

		public String getVersion()
		{
			return lowerCase(this.name());
		}
	}

	private CatalogVersionDao catalogVersionDao;
	private ContentCatalogModelMother contentCatalogModelMother;
	private ProductCatalogModelMother productCatalogModelMother;
	private CurrencyModelMother currencyModelMother;
	private ContentPageModelMother contentPageModelMother;
	private CronJobService cronJobService;
	private LanguageModelMother languageModelMother;

	protected CatalogVersionModel defaultCatalogVersion(final boolean active)
	{
		return CatalogVersionModelBuilder.aModel().withActive(active).withDefaultCurrency(currencyModelMother.createUSDollar())
				.build();
	}

	public CatalogVersionModel createAppleStagedCatalogVersionModel()
	{
		return createContentCatalogVersionModelByIdAndVersion(ID_APPLE, STAGED);
	}

	public CatalogVersionModel createStagedCatalogVersionModelWithId(CatalogTemplate catalogId)
	{
		return createContentCatalogVersionModelByIdAndVersion(catalogId, STAGED);
	}

	public CatalogVersionModel createOnlineCatalogVersionModelWithId(CatalogTemplate catalogId)
	{
		return createContentCatalogVersionModelByIdAndVersion(catalogId, ONLINE);
	}
	
	public CatalogVersionModel createPhoneStaged1CatalogVersionModel()
	{
		return createProductCatalogVersionModelByIdAndVersion(ID_PHONES, STAGED1);
	}
	
	public CatalogVersionModel createPhoneStaged2CatalogVersionModel()
	{
		return createProductCatalogVersionModelByIdAndVersion(ID_PHONES, STAGED2);
	}

	public CatalogVersionModel createPhoneOnlineCatalogVersionModel()
	{
		return createProductCatalogVersionModelByIdAndVersion(ID_PHONES, ONLINE);
	}

	public CatalogVersionModel createLaptopStaged1CatalogVersionModel()
	{
		return createProductCatalogVersionModelByIdAndVersion(ID_LAPTOPS, STAGED1);
	}
	
	public CatalogVersionModel createLaptopStaged2CatalogVersionModel()
	{
		return createProductCatalogVersionModelByIdAndVersion(ID_LAPTOPS, STAGED2);
	}

	public CatalogVersionModel createLaptopOnlineCatalogVersionModel()
	{
		return createProductCatalogVersionModelByIdAndVersion(ID_LAPTOPS, ONLINE);
	}

	protected CatalogVersionModel createContentCatalogVersionModelByIdAndVersion(CatalogTemplate catalogId, final CatalogVersion version)
	{
		
		return getFromCollectionOrSaveAndReturn(() -> getCatalogVersionDao().findCatalogVersions(catalogId.name(), version.getVersion()),
				() -> CatalogVersionModelBuilder
						.fromModel(defaultCatalogVersion(true)).withCatalog(getContentCatalogModelMother()
								.createContentCatalogModelWithIdAndName(catalogId.name(), catalogId.getFirstInstanceOfHumanName()))
						.withLanguages(Arrays.asList(languageModelMother.createEnglish()))
						.withVersion(version.getVersion()).build());
	}

	protected CatalogVersionModel createProductCatalogVersionModelByIdAndVersion(CatalogTemplate catalogId, final CatalogVersion version)
	{
		return getFromCollectionOrSaveAndReturn(() -> getCatalogVersionDao().findCatalogVersions(catalogId.name(), version.getVersion()),
				() -> CatalogVersionModelBuilder
						.fromModel(defaultCatalogVersion(true)).withCatalog(getProductCatalogModelMother()
								.createProductCatalogModelWithIdAndName(catalogId.name(), catalogId.getFirstInstanceOfHumanName()))
						.withVersion(version.getVersion()).build());
	}

	public CatalogVersionModel createAppleOnlineCatalogVersionModel()
	{
		return createContentCatalogVersionModelByIdAndVersion(ID_APPLE, ONLINE);
	}

	public void performCatalogSyncronization(final CatalogVersionModel source, final CatalogVersionModel target)
	{
		final SyncItemJobModel syncItemJobModel = new SyncItemJobModel();
		syncItemJobModel.setActive(true);
		syncItemJobModel.setSourceVersion(source);
		syncItemJobModel.setTargetVersion(target);

		final SyncItemCronJobModel cronJob = new SyncItemCronJobModel();
		cronJob.setCode(UUID.randomUUID().toString());
		cronJob.setJob(syncItemJobModel);
		getModelService().saveAll(cronJob, syncItemJobModel);
		final boolean synchronousJob = true;
		getCronJobService().performCronJob(cronJob, synchronousJob);
	}

	public SyncItemCronJobModel createCatalogSyncronizationSyncItemCronJobModel(final CatalogVersionModel source,
			final CatalogVersionModel target)
	{
		final SyncItemJobModel syncItemJobModel = new SyncItemJobModel();
		syncItemJobModel.setActive(true);
		syncItemJobModel.setSourceVersion(source);
		syncItemJobModel.setTargetVersion(target);

		final SyncItemCronJobModel cronJob = new SyncItemCronJobModel();
		cronJob.setCode(UUID.randomUUID().toString());
		cronJob.setJob(syncItemJobModel);
		getModelService().saveAll(cronJob, syncItemJobModel);
		getCronJobService().performCronJob(cronJob);
		return cronJob;
	}

	public CatalogVersionModel createAppleCatalogVersionModel(final String version)
	{
		final CatalogVersionModel catalogVersion = CatalogVersionModelBuilder.fromModel(defaultCatalogVersion(false))
				.withCatalog(contentCatalogModelMother.createAppleContentCatalogModel()).withVersion(version).build();

		final CatalogVersionModel catalogVersionSaved = getFromCollectionOrSaveAndReturn(
				() -> getCatalogVersionDao().findCatalogVersions(ID_APPLE.name(), version), () -> catalogVersion);

		return catalogVersionSaved;
	}

	public CatalogVersionModel createCatalogVersionModel(final String catalogId, final String version)
	{
		final CatalogVersionModel catalogVersion = CatalogVersionModelBuilder.fromModel(defaultCatalogVersion(false))
				.withCatalog(contentCatalogModelMother.createContentCatalogModelWithIdAndName(catalogId, catalogId)).withVersion(version)
				.build();

		final CatalogVersionModel catalogVersionSaved = getFromCollectionOrSaveAndReturn(
				() -> getCatalogVersionDao().findCatalogVersions(catalogId, version), () -> catalogVersion);

		return catalogVersionSaved;
	}

	public ContentCatalogModelMother getContentCatalogModelMother()
	{
		return contentCatalogModelMother;
	}

	@Required
	public void setContentCatalogModelMother(final ContentCatalogModelMother catalogModelMother)
	{
		this.contentCatalogModelMother = catalogModelMother;
	}

	public CatalogVersionDao getCatalogVersionDao()
	{
		return catalogVersionDao;
	}

	@Required
	public void setCatalogVersionDao(final CatalogVersionDao catalogVersionDao)
	{
		this.catalogVersionDao = catalogVersionDao;
	}

	public CurrencyModelMother getCurrencyModelMother()
	{
		return currencyModelMother;
	}

	@Required
	public void setCurrencyModelMother(final CurrencyModelMother currencyModelMother)
	{
		this.currencyModelMother = currencyModelMother;
	}

	public ContentPageModelMother getContentPageModelMother()
	{
		return contentPageModelMother;
	}

	@Required
	public void setContentPageModelMother(final ContentPageModelMother contentPageModelMother)
	{
		this.contentPageModelMother = contentPageModelMother;
	}
	
	public ProductCatalogModelMother getProductCatalogModelMother()
	{
		return productCatalogModelMother;
	}
	
	@Required
	public void setProductCatalogModelMother(ProductCatalogModelMother productCatalogModelMother)
	{
		this.productCatalogModelMother = productCatalogModelMother;
	}

	@Required
	protected CronJobService getCronJobService()
	{
		return cronJobService;
	}

	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	protected LanguageModelMother getLanguageModelMother()
	{
		return languageModelMother;
	}

	@Required
	public void setLanguageModelMother(final LanguageModelMother languageModelMother)
	{
		this.languageModelMother = languageModelMother;
	}
}