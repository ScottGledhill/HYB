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
 * ValidationConsumer is an interface that works along with other Validators to help with the validation work. It
 * extends the Consumer interface and it should perform the validation work.
 * 
 * @deprecated since version 6.3. Please use {@link de.hybris.platform.cmsfacades.common.function.ValidationConsumer} in
 *             the cmsfacades extension instead.
 */
@Deprecated
public interface ValidationConsumer<T> extends de.hybris.platform.cmsfacades.common.function.ValidationConsumer<T>
{

}
