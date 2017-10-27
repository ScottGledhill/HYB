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
package de.hybris.platform.cmsfacades.common.service.impl;

import de.hybris.platform.cmsfacades.common.service.StringDecapitalizer;

import java.util.Optional;

/**
 * Default implementation of {@link StringDecapitalizer}.
 * This class contains a copy of the code found in {@code org.eclipse.persistence.jaxb.DefaultXMLNameTransformer}
 * and should be discarded if this class become available or the deserialization process use a different way to discriminate 
 * concrete classes. 
 */
public class DefaultStringDecapitalizer implements StringDecapitalizer
{
	
	@Override
	public Optional<String> decapitalize(Class theClass) {
		if (theClass == null)
		{
			return Optional.empty();
		}
		return decapitalize(theClass.getSimpleName());
	}

	/**
	 * Decapitalize all the first characters that are upper case. 
	 * @param className the class name 
	 * @return the decapitalized string
	 */
	protected Optional<String> decapitalize(final String className)
	{
		char[] name = className.toCharArray();
		int i = 0;
		while (i < name.length && Character.isUpperCase(name[i]))
		{
			++i;
		}

		if(i <= 0) {
			return Optional.of(className);
		} else {
			if(name.length > i && Character.isLetter(name[i])) {
				--i;
			}
			name[0] = Character.toLowerCase(name[0]);
			for(int j = 1; j < i; ++j) {
				name[j] = Character.toLowerCase(name[j]);
			}
			return Optional.of(new String(name));
		}
	}
}
