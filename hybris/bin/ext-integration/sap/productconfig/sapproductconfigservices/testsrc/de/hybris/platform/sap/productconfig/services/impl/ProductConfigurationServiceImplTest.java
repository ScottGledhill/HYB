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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@UnitTest
public class ProductConfigurationServiceImplTest
{
	private static Logger LOG = Logger.getLogger(ProductConfigurationServiceImplTest.class);

	@Mock
	private TrackingRecorder recorder;


	static class ThreadBlocking extends Thread
	{

		private static final long WAIT_TIME = 100;

		@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SWL_SLEEP_WITH_LOCK_HELD", justification = "Intended behaviour to test that locking is working")
		@Override
		public void run()
		{
			synchronized (ProductConfigurationServiceImpl.PROVIDER_LOCK)
			{
				try
				{
					Thread.sleep(WAIT_TIME);
				}
				catch (final InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	static class ThreadAccessing extends Thread
	{
		long duration = 0;

		@Override
		public void run()
		{
			final long startTime = System.currentTimeMillis();
			ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
			duration = System.currentTimeMillis() - startTime;
		}
	}

	private static final String DUMMY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"8\" VALUE_TXT=\"Value 8\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";
	private static final String NEW_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"9\" VALUE_TXT=\"Value 9\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";

	private static final String CONFIG_ID_2 = "asdasdwer4543556zgfhvchtr";
	private static final String CONFIG_ID_1 = "asdsafsdgftert6er6erzz";

	private ProductConfigurationServiceImpl cut;

	@Mock
	private ConfigurationProvider configurationProviderMock;

	@Mock
	private ConfigModel modelMock;

	@Mock
	private ConfigurationProviderFactory configurationProviderFactoryMock;

	private static final String CONFIG_ID = "abc123";

	@Mock
	private CartEntryModel cartEntry;

	@Mock
	private ProductModel productModel;

	@Mock
	private SessionAccessService sessionAccessService;


	private static final long keyAsLong = 12;


	private final PK primaryKey = PK.fromLong(keyAsLong);

	private CommerceCartParameter parameters;

	private static final String configId = "1";


	private final ConfigModel configModel = new ConfigModelImpl();

	private final InstanceModel instanceModel = new InstanceModelImpl();



	@Before
	public void setup()
	{
		cut = new ProductConfigurationServiceImpl();
		MockitoAnnotations.initMocks(this);
		cut.setConfigurationProviderFactory(configurationProviderFactoryMock);
		Mockito.when(configurationProviderFactoryMock.getProvider()).thenReturn(configurationProviderMock);
		Mockito
				.when(configurationProviderMock.createConfigurationFromExternalSource(Mockito.any(KBKey.class), Mockito.anyString()))
				.thenReturn(configModel);
		Mockito.when(configurationProviderMock.createDefaultConfiguration((Mockito.any(KBKey.class)))).thenReturn(configModel);
		cut.setSessionAccessService(sessionAccessService);
		cut.setRecorder(recorder);

		Mockito.when(modelMock.getId()).thenReturn(CONFIG_ID);
		Mockito.when(cartEntry.getPk()).thenReturn(primaryKey);
		Mockito.when(cartEntry.getProduct()).thenReturn(productModel);
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(configId);

		Mockito.when(sessionAccessService.getConfigurationModelEngineState(configId)).thenReturn(configModel);

		configModel.setRootInstance(instanceModel);
		configModel.setId(configId);
		instanceModel.setSubInstances(Collections.EMPTY_LIST);

		parameters = new CommerceCartParameter();
		parameters.setConfigId(CONFIG_ID);
	}

	@Test
	public void testRetrieveConfiguration() throws Exception
	{
		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);

		final ConfigModel retrievedModel = cut.retrieveConfigurationModel(CONFIG_ID);

		assertTrue("Not delegated", retrievedModel == modelMock);
	}


	@Test
	public void testRetrieveExternalConfiguration() throws Exception
	{
		Mockito.when(configurationProviderMock.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(DUMMY_XML);

		final String xmlString = cut.retrieveExternalConfiguration(CONFIG_ID);

		assertTrue("Not delegated", xmlString == DUMMY_XML);
	}

	@Test
	public void testUpdateCartEntryExternalConfiguration() throws Exception
	{
		Mockito.when(configurationProviderMock.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(DUMMY_XML);
		cut.updateCartEntryExternalConfiguration(parameters, cartEntry);
		Mockito.verify(cartEntry).setExternalConfiguration(DUMMY_XML);

	}

	@Test
	public void testUpdateCartEntryExternalConfiguration_withXML() throws Exception
	{
		final CartEntryModel cartEntry = Mockito.spy(new CartEntryModel());
		Mockito.when(cartEntry.getPk()).thenReturn(primaryKey);
		Mockito.when(cartEntry.getProduct()).thenReturn(productModel);
		cartEntry.setExternalConfiguration(DUMMY_XML);
		final ConfigModel cfgModel = createConfigModel();
		Mockito.when(configurationProviderMock.createConfigurationFromExternalSource(Mockito.any(), Mockito.eq(NEW_XML)))
				.thenReturn(cfgModel);

		Mockito.when(configurationProviderMock.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(NEW_XML);

		cut.updateCartEntryExternalConfiguration(NEW_XML, cartEntry);

		Mockito.verify(cartEntry).setExternalConfiguration(NEW_XML);
		assertEquals("New XML should be set on cartEntry", NEW_XML, cartEntry.getExternalConfiguration());
	}

	@Test
	public void testUpdateCartEntryBasePrice_NoPrice() throws Exception
	{
		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);
		Mockito.when(modelMock.getCurrentTotalPrice()).thenReturn(PriceModel.NO_PRICE);

		final boolean entryUpdated = cut.updateCartEntryBasePrice(cartEntry);

		assertFalse("Entry should not be updated", entryUpdated);

	}

	@Test
	public void testUpdateCartEntryBasePrice() throws Exception
	{
		final ConfigModel cfgModel = createConfigModel();
		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenReturn(cfgModel);
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(Mockito.any())).thenReturn(CONFIG_ID);

		final boolean entryUpdated = cut.updateCartEntryBasePrice(cartEntry);

		Mockito.verify(cartEntry, Mockito.times(1)).setBasePrice(
				Double.valueOf(cfgModel.getCurrentTotalPrice().getPriceValue().doubleValue()));

		assertTrue("Entry should be updated", entryUpdated);

	}

	private ConfigModel createConfigModel()
	{
		final PriceModel currentTotalPrice = new PriceModelImpl();
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		currentTotalPrice.setCurrency("EUR");
		currentTotalPrice.setPriceValue(BigDecimal.valueOf(132.85));
		configModel.setCurrentTotalPrice(currentTotalPrice);
		return configModel;
	}

	@Test
	public void testRetrieveConfigurationNull() throws Exception
	{
		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenReturn(null);

		final ConfigModel retrievedModel = cut.retrieveConfigurationModel(CONFIG_ID);

		assertNull("Not just delegated", retrievedModel);

	}

	@Test
	public void testGetLockNotNull()
	{
		Assert.assertNotNull("Lock objects may not be null", ProductConfigurationServiceImpl.getLock(CONFIG_ID_1));
	}

	@Test
	public void testGetLockDifferrentForDifferntConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_2);
		Assert.assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testGetLockSameforSameConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		Assert.assertSame("Lock objects should be same!", lock1, lock2);
	}

	@Test
	public void testGetLockMapShouldNotGrowEndless()
	{

		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final int maxLocks = ProductConfigurationServiceImpl.getMaxLocksPerMap() * 2;
		for (int ii = 0; ii <= maxLocks; ii++)
		{
			ProductConfigurationServiceImpl.getLock(String.valueOf(ii));
		}
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		Assert.assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testRetrieveConfigurationCached()
	{

		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);

		ConfigModel retrievedModel = cut.retrieveConfigurationModel(CONFIG_ID);

		Mockito.verify(sessionAccessService, Mockito.times(1)).setConfigurationModelEngineState(Mockito.contains(CONFIG_ID),
				Mockito.same(retrievedModel));

		Mockito.when(sessionAccessService.getConfigurationModelEngineState(Mockito.contains(CONFIG_ID))).thenReturn(modelMock);

		retrievedModel = cut.retrieveConfigurationModel(CONFIG_ID);

		Mockito.verify(configurationProviderMock, Mockito.times(1)).retrieveConfigurationModel(CONFIG_ID);
		assertTrue("Not delegated", retrievedModel == modelMock);
	}


	@Test
	public void testUpdateConfigurationInvalidateCache()
	{
		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);
		Mockito.when(Boolean.valueOf(configurationProviderMock.updateConfiguration(modelMock))).thenReturn(Boolean.TRUE);

		cut.updateConfiguration(modelMock);

		Mockito.verify(sessionAccessService, Mockito.times(1)).removeConfigurationModelEngineState(CONFIG_ID);
	}


	@Test
	public void testConfigCacheGrowsNotEndless()
	{
		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);
		cut.retrieveConfigurationModel(CONFIG_ID);
		final int maxCachedConfigs = cut.getMaxCachedConfigsInSession() * 2;
		for (int ii = 0; ii <= maxCachedConfigs; ii++)
		{
			final String configId = String.valueOf(ii);
			cut.retrieveConfigurationModel(configId);
		}

		Mockito.verify(sessionAccessService, Mockito.times(1)).removeConfigurationModelEngineState(Mockito.contains(CONFIG_ID));
	}

	@Test
	public void testSessionAccessService()
	{
		final SessionAccessService sessionAccessService = new SessionAccessServiceImpl();
		cut.setSessionAccessService(sessionAccessService);
		assertEquals("Service should be available", sessionAccessService, cut.getSessionAccessService());
	}

	@Test
	public void testGetCartEntryConfigurationAttributesEmptyConfig()
	{
		final CartEntryConfigurationAttributes entryAttribs = cut.calculateCartEntryConfigurationAttributes(cartEntry);
		assertNotNull(entryAttribs);
		assertEquals("Empty configuration not consistent", Boolean.FALSE, entryAttribs.getConfigurationConsistent());
		assertEquals("No errors expected", 0, entryAttribs.getNumberOfErrors().intValue());
	}

	@Test
	public void testGetCartEntryConfigurationAttributesNoExternalCFG()
	{
		Mockito.when(sessionAccessService.getConfigurationModelEngineState(configId)).thenReturn(null);

		//no configuration: in this case we create a default configuration which should not contain issues
		final CartEntryConfigurationAttributes cartEntryConfigurationAttributes = cut
				.calculateCartEntryConfigurationAttributes(cartEntry);
		assertEquals("No errors expected", 0, cartEntryConfigurationAttributes.getNumberOfErrors().intValue());

	}

	@Test
	public void testGetCartEntryConfigurationAttributesNumberOfIssues()
	{
		final SolvableConflictModel conflict = new SolvableConflictModelImpl();
		configModel.setSolvableConflicts(Arrays.asList(conflict));
		final CartEntryConfigurationAttributes entryAttribs = cut.calculateCartEntryConfigurationAttributes(cartEntry);
		assertNotNull(entryAttribs);
		assertEquals("One error expected", 1, entryAttribs.getNumberOfErrors().intValue());
	}

	@Test
	public void testGetCartEntryConfigurationAttributes()
	{
		configModel.setComplete(true);
		configModel.setConsistent(true);
		checkCartEntryConsistent();
	}

	@Test
	public void testGetCartEntryConfigurationAttributesNotComplete()
	{
		configModel.setComplete(false);
		configModel.setConsistent(true);
		checkCartEntryNotConsistent();
	}

	@Test
	public void testGetCartEntryConfigurationAttributesNotConsistent()
	{
		configModel.setComplete(true);
		configModel.setConsistent(false);
		checkCartEntryNotConsistent();
	}

	private void checkCartEntryConsistent()
	{
		final CartEntryConfigurationAttributes entryAttribs = cut.calculateCartEntryConfigurationAttributes(cartEntry);
		assertNotNull(entryAttribs);
		assertEquals("Configuration should be consistent ", Boolean.TRUE, entryAttribs.getConfigurationConsistent());
	}

	private void checkCartEntryNotConsistent()
	{
		final CartEntryConfigurationAttributes entryAttribs = cut.calculateCartEntryConfigurationAttributes(cartEntry);
		assertNotNull(entryAttribs);
		assertEquals("Configuration shouldn't be consistent ", Boolean.FALSE, entryAttribs.getConfigurationConsistent());
	}


	@Test
	public void testGetNumberOfConflictsEmptyConfig()
	{
		final int numberOfConflicts = cut.countNumberOfSolvableConflicts(configModel);
		assertEquals("No conflicts", 0, numberOfConflicts);
	}

	@Test
	public void testGetNumberOfConflicts()
	{
		final SolvableConflictModel conflict = new SolvableConflictModelImpl();
		configModel.setSolvableConflicts(Arrays.asList(conflict));
		final int numberOfConflicts = cut.countNumberOfSolvableConflicts(configModel);
		assertEquals("We expect one conflict", 1, numberOfConflicts);
	}

	@Test
	public void testNoConfigID()
	{
		final String externalConfig = "testExternalConfig";
		Mockito.when(cartEntry.getExternalConfiguration()).thenReturn(externalConfig);
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(null);
		final CartEntryConfigurationAttributes entryAttribs = cut.calculateCartEntryConfigurationAttributes(cartEntry);
		assertNotNull(entryAttribs);
	}

	@Test
	@SuppressFBWarnings(value = "SWL_SLEEP_WITH_LOCK_HELD", justification = "required by test scenario")
	public void testSynchronizationBlockingIsFirst() throws InterruptedException
	{
		synchronized (ProductConfigurationServiceImplTest.class)
		{
			final ThreadBlocking threadBlocking = new ThreadBlocking();
			final ThreadAccessing threadAccessing = new ThreadAccessing();
			threadBlocking.start();
			Thread.sleep(10);
			threadAccessing.start();
			threadAccessing.join();
			LOG.info("BF - Accessing took: " + threadAccessing.duration);
			assertTrue(
					"We expect accessing thread needs to wait (wait time is 100), so it should consume more than 50 ms, but duration was only "
							+ threadAccessing.duration, threadAccessing.duration > 50);
		}
	}

	@Test
	@SuppressFBWarnings(value = "SWL_SLEEP_WITH_LOCK_HELD", justification = "required by test scenario")
	public void testSynchronizationAccessingIsFirst() throws InterruptedException
	{
		synchronized (ProductConfigurationServiceImplTest.class)
		{
			final ThreadBlocking threadBlocking = new ThreadBlocking();
			final ThreadAccessing threadAccessing = new ThreadAccessing();
			threadAccessing.start();
			Thread.sleep(10);
			threadBlocking.start();
			threadAccessing.join();
			LOG.info("AF - Accessing took: " + threadAccessing.duration);
			assertTrue(
					"We expect accessing thread does not needs to wait (wait time is 100ms), so it should be faster as 25ms, but durtaion was "
							+ threadAccessing.duration, threadAccessing.duration < 25);
		}

	}




}
