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
package de.hybris.platform.cmsfacades.common.populator;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;


/**
 * Interface {@link LocalizedPopulator} is used to retrieve the languages supported by the current base store and to
 * populate localized attributes.
 */
public interface LocalizedPopulator
{
	
	/**
	 * Populates the localized attributes
	 *
	 * @param consumer
	 * @param function
	 */
	<T> void populate(final BiConsumer<Locale, T> consumer, final Function<Locale, T> function);

	/**
	 * Returns the language for that specific locale. 
	 * @param locale the locale we want to get the language from. 
	 * @return the language for that locale
	 */
	String getLanguage(Locale locale);

}
