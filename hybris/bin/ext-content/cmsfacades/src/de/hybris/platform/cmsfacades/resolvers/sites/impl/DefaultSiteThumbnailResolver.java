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
package de.hybris.platform.cmsfacades.resolvers.sites.impl;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.startsWith;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.resolvers.sites.SiteThumbnailResolver;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Resolver that uses a {@link CMSSiteModel} to resolve a homepage thumbnail URL
 */
public class DefaultSiteThumbnailResolver implements SiteThumbnailResolver
{

	private CMSAdminPageService cmsAdminPageService;

	/**
	 * Can be called to resolve the homepage thumbnail url. In the event you need to act on the URL you can override this
	 * method and resolve the URL in an alternative way. This method will alse replace any leading '~' with a '/' using
	 * the {@link #resolveHomepageThumbnailUrl(CMSSiteModel) resolveHomepageThumbnailUrl} method
	 *
	 * @param cmsSiteModel
	 *           the cmsSiteModel
	 * @return Optional thumbnail url
	 */
	@Override
	public Optional<String> resolveHomepageThumbnailUrl(final CMSSiteModel cmsSiteModel)
	{
		if (isNotEmpty(cmsSiteModel.getContentCatalogs()))
		{
			final ContentPageModel homepage = getCmsAdminPageService().getHomepage(cmsSiteModel);
			if (homepage != null && homepage.getPreviewImage() != null)
			{
				final String imgURL = homepage.getPreviewImage().getDownloadURL();
				return (isBlank(imgURL) ? empty() : of(replacePrefixTildeWithSlash(imgURL)));
			}
		}
		return empty();
	}

	/**
	 * Used to replace a leading '~' with a '/'
	 *
	 * <pre>
	 * replacePrefixTildeWithSlash("~/someUri")    = "/someUri"
	 * replacePrefixTildeWithSlash("~someUri")     = "/someUri"
	 * replacePrefixTildeWithSlash("~//someUri")   = "//someUri"
	 * </pre>
	 *
	 * @param thumbnailUri
	 *           a uri to a thumbnail
	 * @return A uri with the '~' removed
	 */
	protected String replacePrefixTildeWithSlash(final String thumbnailUri)
	{
		String url = thumbnailUri;
		if (StringUtils.isNotEmpty(url))
		{
			url = url.trim();
		}
		return (startsWith(url, "~")) ? "/" + removeStart(removeStart(url, "~"), "/") : url;
	}

	@Required
	public void setCmsAdminPageService(final CMSAdminPageService cmsAdminPageService)
	{
		this.cmsAdminPageService = cmsAdminPageService;
	}

	protected CMSAdminPageService getCmsAdminPageService()
	{
		return cmsAdminPageService;
	}
}


