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
package de.hybris.platform.cmswebservices.pages.service;

/**
 * Represents meta-information about a <code>ComposedTypeModel</code> and the type code of the target data model to be
 * converted to. The type data is the type code representation of an object that extends a
 * <code>AbstractPageData</code>. <br>
 * <br>
 * For example, a ContentPageModel will be converted to a ContentPageData, therefore the typecode will be 'ContentPage'
 * and the typedata will be 'ContentPageData.class'.
 * 
 * @deprecated since version 6.3. Please use {@link de.hybris.platform.cmsfacades.pages.service.PageTypeMapping} in the
 *             cmsfacades extension instead.
 */
@Deprecated
public interface PageTypeMapping extends de.hybris.platform.cmsfacades.pages.service.PageTypeMapping
{

}
