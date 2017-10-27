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
var CPQ = CPQ || {};

CPQ.core = {
	pageType : "config",
	baseUrl : "",
	ajaxRunning : false,
	ajaxRunCounter : 0,
	formNameId : "#configform",

	firePost : function(fx, args) {
		CPQ.core.ajaxRunCounter++;
		CPQ.core.waitToFirePost(fx, args);
	},

	waitToFirePost : function(fx, args) {
		if (CPQ.core.ajaxRunning === true) {
			setTimeout(function() {
				CPQ.core.waitToFirePost.call(this, fx, args);
			}, 100);
		} else {
			CPQ.core.ajaxRunning = true;
			fx.apply(this, args);
		}
	},

	// To use any of the meta-characters ( such as
	// !"#$%&'()*+,./:;<=>?@[\]^`{|}~ ) as a literal part of a name, it must be
	// escaped with with two backslashes
	// (https://api.jquery.com/category/selectors/)
	// can be replaced by jQuery.escapeSelector when upgrading to jQuery3
	// (https://api.jquery.com/jQuery.escapeSelector/)
	// Note in the expression below every character is escaped, although this
	// would only be necessary for characters, that have a special meaning in a
	// regex. However escaping all is no harm and more robust.
	// regex: /(char2|char2|char3|...)/g
	encodeId : function(id) {
		var encodedId = "#"
				+ id
						.replace(
								/(\!|\"|\#|\$|\%|\&|\'|\(|\)|\*|\+|\,|\.|\/|\:|\;|\<|\=|\>|\?|\@|\[|\\|\]|\^|\`|\{|\||\}|\~)/g,
								"\\\$1");
		return encodedId;
	},

	getPageUrl : function() {
		return this.baseUrl + "/" + this.pageType;
	},

	getVaraiantSearchUrl : function() {
		return this.baseUrl + "/searchConfigVariant";
	},

	getConfigureUrl : function() {
		return this.baseUrl + "/configure/CPQCONFIGURATOR";
	},

	getResetUrl : function() {
		return "reset";
	},

	getAddToCartUrl : function() {
		return "addToCart";
	},
	
	getAddVariantToCartCleanUpUrl : function() {
		return "addVariantToCartCleanUp";
	},

	getOverviewUrl : function() {
		if (CPQ.core.pageType === "variantOverview") {
			return "variantOverview";
		}
		return "configOverview";
	},

	getCopyUrl : function() {
		return "copy";
	},

	actionAndRedirect : function(e, action, url) {
		var input = $('input[name=CSRFToken]');
		var token = null;
		var form = null;

		if (input && input.length !== 0) {
			token = input.attr("value");
		}
		if (token) {
			form = $('<form action="' + action
					+ '" method="post" style="display: none;">'
					+ '<input type="text" name="url" value="' + url + '" />'
					+ '<input type="hidden" name="CSRFToken" value="' + token
					+ '" />' + '</form>');
		} else {
			form = $('<form action="' + action
					+ '" method="post" style="display: none;">'
					+ '<input type="text" name="url" value="' + url + '" />'
					+ '" />' + '</form>');
		}

		$('body').append(form);
		$(form).submit();
	}
};