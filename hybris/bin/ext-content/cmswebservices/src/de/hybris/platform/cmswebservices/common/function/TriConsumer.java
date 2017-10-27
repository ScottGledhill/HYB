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
package de.hybris.platform.cmswebservices.common.function;

/**
 * Represents an operation that accepts three input arguments and returns no result. This is the two-arity
 * specialization of {@link java.util.function.Consumer}. Unlike most other functional interfaces, {@code TriConsumer}
 * is expected to operate via side-effects.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #accept(Object, Object, Object)}.
 *
 * @param <T>
 *           the type of the first argument to the operation
 * @param <U>
 *           the type of the second argument to the operation
 * @param <B>
 *           the type of the third argument to the operation
 * @see java.util.function.Consumer
 * @since 1.8
 * @deprecated since version 6.3. Please use {@link de.hybris.platform.cmsfacades.common.function.TriConsumer} in the
 *             cmsfacades extension instead.
 */
@Deprecated
@FunctionalInterface
public interface TriConsumer<T, U, B> extends de.hybris.platform.cmsfacades.common.function.TriConsumer<T, U, B>
{

}
