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
CPQ.overview = {
    bindAll: function()
    {
    	CPQ.overview.bindStaticOnClickHandlers();
    	CPQ.overview.bindDynamicOnClickHandlers();
    	CPQ.overview.bindFacets();
	},

	bindStaticOnClickHandlers: function()
	{
		$(".product-details .name").on("click keypress", function(e) {
			CPQ.uihandler.clickHideShowImageGallery(CPQ.overview.doPost, e);
		});
		$(".cpq-btn-goToCart").on("click", function(e) {
			CPQ.overview.clickGoToCartButton(e);
		});
		$(".cpq-btn-skip").on("click", function(e) {
			CPQ.core.firePost(CPQ.core.actionAndRedirect, [ e, CPQ.core.getResetUrl(), "/cart" ]);
		});
		$(".facet__list > li > a").on("click", function(e) {
			CPQ.overview.clickRemoveFilter(e);
		});
		$(".cpq-back-button, .cpq-btn-backToConfig").on("click", function(e) {
			CPQ.overview.clickBackToConfigButton(e);
		});
		$(".cpq-copyvalues-facet .facet__checkbox").on("change", function(e) {
			CPQ.overview.toggleFilterCheckbox(e);
		});
		$(".cpq-btn-addVariantToCartCleanUp").on("click", function(e) {
			CPQ.overview.clickAddVariantToCartCleanUp(e);
		});
	},

	bindDynamicOnClickHandlers: function()
	{
		$(".facet__list > li > a").on("click", function(e) {
			CPQ.overview.clickRemoveFilter(e);
		});
		$(".cpq-overview-filter-item .facet__checkbox").on("change", function(e) {
			CPQ.overview.clickApplyFilter(e);
		});
		
	},
	
	

    bindFacets: function () {
        $(document).on("click", ".js-show-facets", function (e) {
            e.preventDefault();
            var selectRefinementsTitle = $(this).data("selectRefinementsTitle");
            ACC.colorbox.open(selectRefinementsTitle, {
                href: "#cpq-overview-facet",
                inline: true,
                width: "480px",
                onComplete: function () {
                    $(document).on("click", ".cpq-js-overview-facet .js-facet__name", function (e) {
                        e.preventDefault();
                        $(".cpq-js-overview-facet  .js-facet").removeClass("active");
                        $(this).parents(".js-facet").addClass("active");
                        $.colorbox.resize()
                    })
                },
                onClosed: function () {
                    $(document).off("click", ".cpq-js-overview-facet .js-facet__name");
                }
            });
        });
        enquire.register("screen and (max-width:" + screenSmMax + ")", function () {
            $("#cboxClose").click();
        });
    },

	
	doPost: function(e, data) {
		$.post(CPQ.core.getPageUrl(), data, function() {
			CPQ.core.ajaxRunning = false;
			CPQ.core.ajaxRunCounter--;
		});

		e.preventDefault();
		e.stopPropagation();
	},
	
	doAddVariantToCartCleanUp: function(e, data) {
		$.post(CPQ.core.getAddVariantToCartCleanUpUrl(), data, function() {
			CPQ.core.ajaxRunning = false;
			CPQ.core.ajaxRunCounter--;
		});
		// no explicit add to cart allowed as Hybris event handler is registered on addToCartForm and this would lead to a double post which causes error in Firefox
	}, 

	doFilterPost: function(e) {
		$("#filterCPQAction").val("APPLY_FILTER");
		var data = $("#filterform").serialize();

		$.post(CPQ.core.getPageUrl(), data, function(response) {
			if (CPQ.core.ajaxRunCounter === 1) {
				CPQ.overview.updateContent(response);
			}
			
			CPQ.overview.bindDynamicOnClickHandlers();
			CPQ.overview.bindFacets();
			CPQ.core.ajaxRunning = false;
			CPQ.core.ajaxRunCounter--;
		});

		e.preventDefault();
		e.stopPropagation();

	},
	
	clickGoToCartButton: function(e) {
		if ($("#copyAndRedirectCheckBox").is(":checked")){
			CPQ.core.firePost(CPQ.core.actionAndRedirect, [ e, CPQ.core.getCopyUrl(), CPQ.core.getConfigureUrl() ]);
		} else {
			CPQ.core.firePost(CPQ.core.actionAndRedirect, [ e, CPQ.core.getResetUrl(), "/cart" ]);
		}
	},
	
	clickBackToConfigButton: function(e) {
		var sUrl = $(e.currentTarget).data("backToConfig");
		window.location = sUrl;
	},
	
	clickAddVariantToCartCleanUp: function(e) {
		CPQ.core.firePost(CPQ.overview.doAddVariantToCartCleanUp, [e]);
	},
	
	clickApplyFilter: function(e) {
		CPQ.overview.toggleFilterCheckbox(e);
		
		CPQ.core.firePost(CPQ.overview.doFilterPost, [e]);
	},
	
	toggleFilterCheckbox: function(e) {
		var facet = $(e.currentTarget).siblings().find(".facet__list__mark");
		
		var className = "filter-selected";
		
		if (facet.hasClass(className)) {
			facet.removeClass(className);
		} else {
			facet.addClass(className);
		}
	},
	
	clickRemoveFilter: function(e) {
		var facet = $(e.currentTarget);
		var facetCheckBox = $("#"+facet.data("filter-id"));
		facetCheckBox.prop("value", false);

		CPQ.core.firePost(CPQ.overview.doFilterPost, [e]);
	},
	
	updateContent: function(response) {
		CPQ.uihandler.updateSlotContent(response, "overviewContentSlot");
		CPQ.uihandler.updateSlotContent(response, "overviewSidebarSlot");
	}
};

$(document).ready(function ()
{
	if ($("#overviewContent").length > 0) {
		if ($("#productCode").val().length > 0) {
			CPQ.core.pageType = "variantOverview";
		} else {			
			CPQ.core.pageType = "configOverview";
		}
		CPQ.core.formNameId = "#overviewform";
		CPQ.overview.bindAll();
	}
});
