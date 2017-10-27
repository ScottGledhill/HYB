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
/**
 *
 */
package de.hybris.platform.personalizationcmsweb.compatibility


import static javax.ws.rs.core.MediaType.*
import static javax.ws.rs.core.Response.Status.*

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationwebservices.BaseWebServiceTest
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer

import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType

import org.junit.Before
import org.junit.Test

import groovy.json.JsonSlurper

/**
 * Compatibility test for 6.1 format. If this test breaks, it means that you might have broken the backward
 * compatility of this webservice format with 6.1 version.
 */
@IntegrationTest
@NeedsEmbeddedServer(webExtensions = [
	PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME
])
class PersonalizationWebservicesCompatibilityTest extends BaseWebServiceTest {

	static final String BASE_FILE_PATH =  "/personalizationcmsweb/test/"
	static final String CUSTOMIZATION_ENDPOINT =  "/catalogs/testCatalog/catalogVersions/Online/customizations"
	static final String QUERY_ENDPOINT = "/query/cxReplaceComponentWithContainer"

	@Before
	void setup() {
		importCsv(BASE_FILE_PATH+"personalizationcmsweb_testdata.impex", "utf-8");
		importCsv(BASE_FILE_PATH+"webcontext_testdata.impex", "utf-8");
	}

	@Test
	void "replace components on page: json"(){
		"replace components on page"("json", APPLICATION_JSON)
	}

	@Test
	void "replace components on page: xml"(){
		"replace components on page"("xml", APPLICATION_XML)
	}

	def "replace components on page"(ext, format) {
		given: "predefined request and response"
		def request = loadText(BASE_FILE_PATH+"wstests/replaceComponent-request."+ext)
		def expected = loadObject(BASE_FILE_PATH+"wstests/replaceComponent-response."+ext, format )

		when: "actual request is made"
		def response = getWsSecuredRequestBuilderForCmsManager() //
				.path(VERSION)//
				.path(QUERY_ENDPOINT)//
				.build().accept(format) //
				.post(Entity.entity(request, format));
		def actual = parse(response, format)

		then: "request was made "
		assert response.status == OK.statusCode
		assert expected.uid != null

		when: "random field is normalized"
		actual.uid = expected.uid

		then: "actual response is the same as expected"
		assert actual == expected
	}


	@Test
	void "create cms action: json"() {
		"create cms action"("json", APPLICATION_JSON)
	}

	@Test
	void "create cms action: xml"() {
		"create cms action"("xml", APPLICATION_XML)
	}

	def "create cms action"(ext, format) {
		given: "predefined request and response"
		def request = loadText(BASE_FILE_PATH+"wstests/createAction-request."+ext)
		def expected = loadObject(BASE_FILE_PATH+"wstests/createAction-response."+ext, format )

		when: "actual request is made"
		def response = getWsSecuredRequestBuilderForCmsManager() //
				.path(VERSION) //
				.path(CUSTOMIZATION_ENDPOINT) //
				.path(CUSTOMIZATION) //
				.path(VARIATION_ENDPOINT) //
				.path(VARIATION) //
				.path(ACTION_ENDPOINT)//
				.build().accept(format) //
				.post(Entity.entity(request, format));
		def actual = parse(response, format)

		then: "request was made"
		assert response.status == CREATED.statusCode

		then: "actual response is the same as expected"
		assert actual == expected
	}

	def loadText(name) {
		this.getClass().getResource(name).text
	}

	def loadObject(name, format) {
		stringParse( loadText(name), format )
	}

	def parse(response, format) {
		def text = response.readEntity(String.class)
		stringParse(text, format)
	}

	def stringParse(text, format) {
		switch(format) {
			case APPLICATION_JSON:
				return new JsonSlurper().parseText(text);
			case APPLICATION_XML:
				return new XmlSlurper().parseText(text);
			default:
				return null;
		}
	}
}
