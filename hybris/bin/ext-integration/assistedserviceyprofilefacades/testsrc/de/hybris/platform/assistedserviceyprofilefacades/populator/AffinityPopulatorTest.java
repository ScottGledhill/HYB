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
package de.hybris.platform.assistedserviceyprofilefacades.populator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedserviceyprofilefacades.data.AffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityData;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UnitTest
public class AffinityPopulatorTest
{
    @InjectMocks
    private AffinityPopulator affinityPopulator = new AffinityPopulator();

    private List<NeighbourData> parsedNeighbours;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

        final ObjectMapper jacksonObjectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try
        {
            final InputStream in = getClass().getClassLoader()
                .getResourceAsStream("assistedserviceyprofilefacades/test/yprofile.json");

            parsedNeighbours = jacksonObjectMapper.readValue(in, new TypeReference<List<NeighbourData>>(){});
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void getAffinityTest()
    {

        final List<NeighbourData> affinityList = parsedNeighbours.stream()
            .filter(n -> StringUtils.containsAny(n.getSchema(),
                    YaasyprofileconnectConstants.SCHEMA_COMMERCE_CATEGORY_AFFINITY,
                    YaasyprofileconnectConstants.SCHEMA_COMMERCE_KEYWORD_SEARCH_AFFINITY,
                    YaasyprofileconnectConstants.SCHEMA_COMMERCE_PRODUCT_AFFINITY))
            .collect(Collectors.toList());

        final NeighbourData neighbourData = affinityList.get(0);

        final AffinityData affinityData = new AffinityData();

        affinityPopulator.populate(neighbourData, affinityData);

        assertEquals(affinityData.getAffinity(),
                neighbourData.getProperties().get(YaasyprofileconnectConstants.AFFINITY_FIELD));
        assertEquals(affinityData.getUpdated(),
                Date.from(ZonedDateTime.parse(neighbourData.getProperties().get(YaasyprofileconnectConstants.UPDATED_FIELD)).toInstant()));
    }
}
