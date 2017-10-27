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
package de.hybris.platform.cmssmarteditwebservices.products.controller;

import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmssmarteditwebservices.data.ProductData;
import de.hybris.platform.cmssmarteditwebservices.dto.PageableWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.ProductSearchResultWsDTO;
import de.hybris.platform.cmssmarteditwebservices.dto.ProductWsDTO;
import de.hybris.platform.cmssmarteditwebservices.products.facade.ProductSearchFacade;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to search Products within a Product Catalog Version. 
 *
 * @pathparam siteId Site identifier
 * @pathparam catalogId Catalog name
 * @pathparam versionId Catalog version identifier
 */
@Controller
@RequestMapping(value = "/productcatalogs/{catalogId}/versions/{versionId}/products")
public class ProductSearchController
{

	@Resource
	private ProductSearchFacade cmsSeProductSearchFacade;

	@Resource
	private WebPaginationUtils webPaginationUtils;
	
	@Resource
	private DataMapper dataMapper;
	
	/**
	 * Find products using a free text search field. <br>
	 *
	 * @queryparam text
	 * 			optional, the string value on which products will be filtered
	 * @queryparam pageSize 
	 * 			the maximum number of elements in the result list. 
	 * @queryparam currentPage 
	 * 			optional, the requested page number 
	 * @queryparam sort
	 * 			optional, the string field the results will be sorted with  
	 * @return a dto which serves as a wrapper object that contains a list of ProductData; never <tt>null</tt>
	 */
	@RequestMapping(method = RequestMethod.GET, params = {"pageSize"})
	@ResponseBody
	public ProductSearchResultWsDTO findProductsByText(@RequestParam(required = false) final String text,
			@ModelAttribute final PageableWsDTO pageableDto)
	{
		final SearchResult<ProductData> productSearchResult = getCmsSeProductSearchFacade().findProducts(text, Optional.of(pageableDto)
				.map(pageableWsDTO -> getDataMapper().map(pageableWsDTO, PageableData.class)).get());
		
		final ProductSearchResultWsDTO productList = new ProductSearchResultWsDTO();
		productList.setProducts(productSearchResult //
				.getResult() //
				.stream() //
				.map(productData -> getDataMapper().map(productData, ProductWsDTO.class)) //
				.collect(Collectors.toList()));
		productList.setPagination(getWebPaginationUtils().buildPagination(productSearchResult));
		return productList;
	}

	protected ProductSearchFacade getCmsSeProductSearchFacade()
	{
		return cmsSeProductSearchFacade;
	}

	public void setCmsSeProductSearchFacade(final ProductSearchFacade cmsSeProductSearchFacade)
	{
		this.cmsSeProductSearchFacade = cmsSeProductSearchFacade;
	}

	protected WebPaginationUtils getWebPaginationUtils()
	{
		return webPaginationUtils;
	}

	public void setWebPaginationUtils(final WebPaginationUtils webPaginationUtils)
	{
		this.webPaginationUtils = webPaginationUtils;
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
