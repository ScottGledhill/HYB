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
package de.hybris.platform.cmswebservices.enumdata.controller;

import de.hybris.platform.cmsfacades.enumdata.EnumDataFacade;
import de.hybris.platform.cmswebservices.data.EnumListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * EnumController exposes the values of enum types.
 *
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping("/enums")
public class EnumDataController {

	@Resource(name="cmsEnumDataFacade")
	private EnumDataFacade enumDataFacade;

	/**
	 * This resource is deprecated since version 6.2, please use <code>PUT /types/{typecode}</code> instead. <br>
	 * <br>
	 *
	 * This accepts a parameter of a classname and for this given classname, retrieves all of the possible enumeration
	 * values.
	 *
	 * @queryparam enumClass Class name of the enumeration which values should be retrieved
	 * @return a {@link EnumListData} object representing these values.
	 *
	 * @deprecated since version 6.2
	 */
	@Deprecated
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public EnumListData getEnumValuesByClass(@RequestParam final String enumClass)
	{
		final EnumListData enumListData = new EnumListData();
		enumListData.setEnums(getEnumDataFacade().getEnumValues(enumClass));
		return enumListData;
	}

	protected EnumDataFacade getEnumDataFacade()
	{
		return enumDataFacade;
	}

	public void setEnumDataFacade(final EnumDataFacade enumDataFacade)
	{
		this.enumDataFacade = enumDataFacade;
	}

}
