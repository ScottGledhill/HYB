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
package de.hybris.platform.cmswebservices.types.controller;

import de.hybris.platform.cmsfacades.types.ComponentTypeFacade;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.cmswebservices.data.ComponentTypeListData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller to deal with component types.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(value = "/types")
public class TypeController
{
	@Resource
	private ComponentTypeFacade componentTypeFacade;

	/**
	 * Find all CMS component types.
	 *
	 * @return a dto which serves as a wrapper object that contains a list of {@link ComponentTypeData}; never
	 *         <tt>null</tt>
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ComponentTypeListData getAllComponentTypes()
	{
		final ComponentTypeListData listDto = new ComponentTypeListData();
		listDto.setComponentTypes(getComponentTypeFacade().getAllComponentTypes());
		return listDto;
	}

	/**
	 * Find all CMS component types filtered by a given category.
	 * @queryparam category The component type category of the types to be returned.
	 * @return a dto which serves as a wrapper object that contains a list of {@link ComponentTypeData}; never
	 *         <tt>null</tt>
	 */
	@RequestMapping(method = RequestMethod.GET, params = {"category"})
	@ResponseBody
	public ComponentTypeListData getAllComponentTypesByCategory(@RequestParam(value = "category") final String category)
	{
		final ComponentTypeListData listDto = new ComponentTypeListData();
		listDto.setComponentTypes(getComponentTypeFacade().getAllComponentTypes(category));
		return listDto;
	}

	/**
	 * Find a single CMS component types.
	 *
	 * @pathparam code Component type code
	 * @return a dto which serves as a wrapper object that contains a {@link ComponentTypeData} DTO
	 * @throws ComponentTypeNotFoundException
	 *            when the code provided does not match any existing type
	 */
	@RequestMapping(value="/{code}", method = RequestMethod.GET)
	@ResponseBody
	public ComponentTypeData getComponentTypeByCode(@PathVariable final String code) throws ComponentTypeNotFoundException
	{
		return getComponentTypeFacade().getComponentTypeByCode(code);
	}

	public ComponentTypeFacade getComponentTypeFacade()
	{
		return componentTypeFacade;
	}

	public void setComponentTypeFacade(final ComponentTypeFacade componentTypeFacade)
	{
		this.componentTypeFacade = componentTypeFacade;
	}



}
