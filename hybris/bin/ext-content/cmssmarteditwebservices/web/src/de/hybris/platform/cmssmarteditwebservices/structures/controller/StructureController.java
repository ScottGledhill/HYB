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
package de.hybris.platform.cmssmarteditwebservices.structures.controller;

import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmssmarteditwebservices.dto.StructureListWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.StructureWsDTO;
import de.hybris.platform.cmssmarteditwebservices.structures.facade.StructureFacade;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller to deal with structures for CMS types.
 */
@Controller
@RequestMapping(value = "/structures")
public class StructureController
{
	@Resource
	private StructureFacade structureFacade;
	@Resource
	private DataMapper dataMapper;

	/**
	 * Get the structures that matches the given type code for all defined structure type modes.
	 *
	 * @pathparam code Component type code
	 * @param code
	 *           - the type code of the structure to retrieve
	 *
	 * @return all structures for the given type code. If a mode is specified, the list of structures should contain only
	 *         one item.
	 * @throws ComponentTypeNotFoundException
	 *            when the code provided does not match any existing type
	 */
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	public StructureListWsDTO getStructureByCode(@PathVariable final String code) throws ComponentTypeNotFoundException
	{
		final List<StructureWsDTO> structureDtos = new ArrayList<>();
		getStructureFacade().getComponentTypesByCode(code).forEach(structureData -> {
			final StructureWsDTO structureDto = getDataMapper().map(structureData, StructureWsDTO.class);
			structureDtos.add(structureDto);
		});

		final StructureListWsDTO dtoList = new StructureListWsDTO();
		dtoList.setStructures(structureDtos);
		return dtoList;
	}

	/**
	 * Get the structures that matches the given type code.
	 *
	 * @pathparam code Component type code
	 * @queryparam mode Context for which a specific structure is defined for the given type code
	 *
	 * @param code
	 *           - the type code of the structure to retrieve
	 * @param mode
	 *           - the mode of the structure to retrieve
	 *
	 * @return all structures for the given type code. If a mode is specified, the list of structures should contain only
	 *         one item.
	 * @throws ComponentTypeNotFoundException
	 *            when the code provided does not match any existing type
	 */
	@RequestMapping(value = "/{code}", method = RequestMethod.GET, params = { "mode" })
	@ResponseBody
	public StructureListWsDTO getStructureByCodeAndMode(@PathVariable final String code,
			@RequestParam(value = "mode") final String mode) throws ComponentTypeNotFoundException
	{
		final ComponentTypeData structureData = getStructureFacade().getComponentTypeByCodeAndMode(code, mode);
		final StructureWsDTO structureDto = getDataMapper().map(structureData, StructureWsDTO.class);

		final StructureListWsDTO dtoList = new StructureListWsDTO();
		dtoList.setStructures(Arrays.asList(structureDto));
		return dtoList;
	}

	protected StructureFacade getStructureFacade()
	{
		return structureFacade;
	}

	public void setStructureFacade(final StructureFacade structureFacade)
	{
		this.structureFacade = structureFacade;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

}
