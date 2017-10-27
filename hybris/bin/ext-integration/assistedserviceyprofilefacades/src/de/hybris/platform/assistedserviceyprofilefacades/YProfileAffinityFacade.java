/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedserviceyprofilefacades;


import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.KeywordSearchAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.KeywordSearchAffinityParameterData;
import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityParameterData;

import java.util.List;


public interface YProfileAffinityFacade
{
	/**
	 * Returns list of products affinities from all profile nodes, belongs to customer with populated product, affinity,
	 * view count data.
	 *
	 * @param productAffinityParameterData
	 *           holds parameters to be used for data retrieval if needed
	 *
	 * @return List<YProfileProductAffinityData>
	 */
	List<ProductAffinityData> getProductAffinities(ProductAffinityParameterData productAffinityParameterData);

	/**
	 * Returns list of categories affinities associated to the current session customer
	 *
	 * @param categoryAffinityParameterData
	 *           holds parameters to be used for data retrieval if needed
	 *
	 * @return List<YProfileCategoryAffinityData>
	 */
	List<CategoryAffinityData> getCategoryAffinities(CategoryAffinityParameterData categoryAffinityParameterData);

	/**
	 * Returns list of keyword search affinities associated to the current session customer
	 *
	 * @param keywordSearchAffinityParameterData
	 *           holds parameters to be used for data retrieval if needed
	 * @return List<KeywordSearchAffinityData>
	 */
	List<KeywordSearchAffinityData> getKeywordSearchAffinities(
			KeywordSearchAffinityParameterData keywordSearchAffinityParameterData);

}
