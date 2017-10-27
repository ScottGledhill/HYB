<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set var="variantOverview" value="${overviewUiData.overviewMode eq 'VARIANT_OVERVIEW'}"/>

<c:if test="${not empty config and (not config.consistent or not config.complete)}">
	<div class="cpq-overview-error-panel">
		<div class="cpq-overview-error-sign">&#xe101;</div>
		<div class="cpq-overview-error-message">
			<span><spring:message code="sapproductconfig.cart.entrytext.conflicts.responsive"
					text="{0} issues must be resolved before checkout" arguments="${errorCount}" />&nbsp; <spring:url
					value="/cart/${overviewUiData.cartEntryNumber}/configuration/CPQCONFIGURATOR" var="resolveConfigUrl"></spring:url> <a href="${resolveConfigUrl}"><spring:message
						code="sapproductconfig.addtocart.resolve.button" text="Resolve Issues Now" /></a> </span>
		</div>
	</div>
</c:if>

<c:choose>
	<c:when test="${variantOverview}">
		<spring:url value="/${product.baseProduct}/configure/CPQCONFIGURATOR" var="configUrl" />
		<spring:theme code="sapproductconfig.overview.variant.title" text="Back to Configuration (Default)" var="linkText" />
	</c:when>
	<c:otherwise>
		<spring:url value="/${product.code}/configure/CPQCONFIGURATOR" var="configUrl" />
		<spring:theme code="sapproductconfig.overview.title" text="Review your Selections (Default)"  var="linkText"/>
	</c:otherwise>
</c:choose>

<div class="back-link border">
	<button type="button" class="cpq-back-button" data-back-to-config="${configUrl}">
		<span class="glyphicon glyphicon-chevron-left"></span>
	</button>
	<span class="label">${linkText}</span>
</div>
