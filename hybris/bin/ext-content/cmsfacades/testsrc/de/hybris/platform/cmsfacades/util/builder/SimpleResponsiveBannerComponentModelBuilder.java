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
package de.hybris.platform.cmsfacades.util.builder;

import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.core.model.media.MediaContainerModel;

import java.util.List;
import java.util.Locale;


public class SimpleResponsiveBannerComponentModelBuilder {

	private final SimpleResponsiveBannerComponentModel model;


	private SimpleResponsiveBannerComponentModelBuilder()
	{
		model = new SimpleResponsiveBannerComponentModel();
	}

	private SimpleResponsiveBannerComponentModelBuilder(final SimpleResponsiveBannerComponentModel model)
	{
		this.model = model;
	}

	protected SimpleResponsiveBannerComponentModel getModel()
	{
		return this.model;
	}

	public static SimpleResponsiveBannerComponentModelBuilder aModel()
	{
		return new SimpleResponsiveBannerComponentModelBuilder();
	}

	public static SimpleResponsiveBannerComponentModelBuilder fromModel(final SimpleResponsiveBannerComponentModel model)
	{
		return new SimpleResponsiveBannerComponentModelBuilder(model);
	}

	public SimpleResponsiveBannerComponentModelBuilder withMediaContainer(final MediaContainerModel container)
	{
		getModel().setMedia(container);
		return this;
	}

	public SimpleResponsiveBannerComponentModelBuilder withCatalogVersion(final CatalogVersionModel cv)
	{
		getModel().setCatalogVersion(cv);
		return this;
	}

	public SimpleResponsiveBannerComponentModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public SimpleResponsiveBannerComponentModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}

	public SimpleResponsiveBannerComponentModelBuilder withContentSlots(final List<ContentSlotModel> slots)
	{
		getModel().setSlots(slots);
		return this;
	}

	public SimpleResponsiveBannerComponentModelBuilder withUrlLink(final String urlLink)
	{
		getModel().setUrlLink(urlLink);
		return this;
	}

	public SimpleResponsiveBannerComponentModelBuilder withMedia(final MediaContainerModel media, final Locale locale)
	{
		getModel().setMedia(media, locale);
		return this;
	}

	public SimpleResponsiveBannerComponentModel build()
	{
		return this.getModel();
	}
}
