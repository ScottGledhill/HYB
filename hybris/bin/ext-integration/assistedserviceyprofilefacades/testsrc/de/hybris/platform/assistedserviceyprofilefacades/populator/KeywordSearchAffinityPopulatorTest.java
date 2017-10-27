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
import de.hybris.platform.assistedserviceyprofilefacades.data.KeywordSearchAffinityData;
import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UnitTest
public class KeywordSearchAffinityPopulatorTest
{
    protected KeywordSearchAffinityPopulator keywordSearchAffinityPopulator = new KeywordSearchAffinityPopulator();

    private List<NeighbourData> parsedNeighbours;

    @Before
    public void setUp()
    {
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
            .filter(n -> n.getSchema().contains(YaasyprofileconnectConstants.SCHEMA_COMMERCE_KEYWORD_SEARCH_AFFINITY))
            .collect(Collectors.toList());

        assertEquals(3, affinityList.size());

        final NeighbourData neighbourData = affinityList.get(0);
        final KeywordSearchAffinityData keywordSearchAffinityData = new KeywordSearchAffinityData();
        keywordSearchAffinityPopulator.populate(neighbourData, keywordSearchAffinityData);

        assertTrue(keywordSearchAffinityData.getSearchCount().equalsIgnoreCase(neighbourData.getProperties().get(YaasyprofileconnectConstants.KEYWORD_SEARCH_COUNT_FIELD)));
        assertTrue(keywordSearchAffinityData.getSearchText().equalsIgnoreCase(neighbourData.getProperties().get(YaasyprofileconnectConstants.KEYWORD_SEARCHID_FIELD)));
    }
}
