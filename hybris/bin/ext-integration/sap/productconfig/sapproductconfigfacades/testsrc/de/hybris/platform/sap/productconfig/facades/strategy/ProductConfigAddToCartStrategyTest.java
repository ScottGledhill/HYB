/*
 * [y] hybris Platform
 *
 * Copyright (c) 2016 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.facades.strategy;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import org.junit.Before;
import org.junit.Test;


public class ProductConfigAddToCartStrategyTest
{

	private ProductConfigAddToCartStrategy classUnderTest;
	private CommerceCartParameter params;
	private ProductModel product;
	private CartModel cart;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigAddToCartStrategy();
		createValidEntity();

	}

	private void createValidEntity()
	{
		params = new CommerceCartParameter();
		cart = new CartModel();
		params.setCart(cart);
		product = new ProductModel();
		params.setProduct(product);
		product.setSapConfigurable(Boolean.TRUE);
		params.setQuantity(1);
	}

	@Test
	public void testValidate_OK() throws CommerceCartModificationException
	{
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_cartNull() throws CommerceCartModificationException
	{
		params.setCart(null);
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidate_productNull() throws CommerceCartModificationException
	{
		params.setProduct(null);
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testValidate_quantityZero() throws CommerceCartModificationException
	{
		params.setQuantity(0);
		classUnderTest.validateAddToCart(params);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testValidate_variant() throws CommerceCartModificationException
	{
		product.setSapConfigurable(Boolean.FALSE);
		product.setVariantType(new VariantTypeModel());
		classUnderTest.validateAddToCart(params);
	}
}
