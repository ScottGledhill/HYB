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
package de.hybris.smartedit.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static de.hybris.smartedit.controllers.Page.LOGIN_PAGE;

@Controller
public class LoginSmartEditController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage()
    {
        return LOGIN_PAGE.getViewName();
    }

}