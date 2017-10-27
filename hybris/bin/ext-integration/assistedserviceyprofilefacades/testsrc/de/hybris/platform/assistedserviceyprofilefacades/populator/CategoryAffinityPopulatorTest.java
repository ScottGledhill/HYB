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
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityData;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.yaasyprofileconnect.constants.YaasyprofileconnectConstants;
import de.hybris.platform.yaasyprofileconnect.data.NeighbourData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UnitTest
public class CategoryAffinityPopulatorTest
{
    @InjectMocks
    private CategoryAffinityPopulator categoryPopulator = new CategoryAffinityPopulator<>();

    @Mock
    private Converter<CategoryModel, CategoryData> categoryUrlConverter;
    @Mock
    private CategoryService categoryService;

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
        CategoryModel categoryModel = Mockito.mock(CategoryModel.class);
        CategoryData categoryData = Mockito.mock(CategoryData.class);
        ImageData imageData = Mockito.mock(ImageData.class);

        final List<NeighbourData> affinityList = parsedNeighbours.stream()
            .filter(n -> n.getSchema().contains(YaasyprofileconnectConstants.SCHEMA_COMMERCE_CATEGORY_AFFINITY))
            .collect(Collectors.toList());

        assertEquals(1, affinityList.size());

        final NeighbourData neighbourData = affinityList.get(0);

        Mockito.when(categoryService.getCategoryForCode(neighbourData.getProperties().get(YaasyprofileconnectConstants.CATEGORY_ID_FIELD))).thenReturn(categoryModel);
        Mockito.when(categoryUrlConverter.convert(categoryModel)).thenReturn(categoryData);
        Mockito.when(categoryData.getImage()).thenReturn(imageData);

        final CategoryAffinityData categoryAffinityData = new CategoryAffinityData();

        categoryPopulator.populate(neighbourData, categoryAffinityData);

        assertTrue(categoryAffinityData.getViewCount().equalsIgnoreCase(neighbourData.getProperties().get(YaasyprofileconnectConstants.CATEGORY_VIEW_COUNT_FIELD)));
        assertEquals(categoryAffinityData.getCategoryData(), categoryData);
        assertEquals(categoryAffinityData.getImage(), imageData);
    }
}
