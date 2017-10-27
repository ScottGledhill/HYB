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
package com.sap.hybris.reco.be.impl;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.odata.util.MyCallback;
import de.hybris.platform.sap.core.odata.util.ODataClientService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.google.common.collect.Lists;
import com.sap.hybris.reco.be.ProductRecommendationManagerBackend;
import com.sap.hybris.reco.constants.SapproductrecommendationConstants;
import com.sap.hybris.reco.dao.ImpressionContext;
import com.sap.hybris.reco.dao.InteractionContext;
import com.sap.hybris.reco.dao.ProductRecommendationData;
import com.sap.hybris.reco.dao.RecommendationContext;
import com.sap.hybris.reco.util.ProductRecommendationManagerUtil;


/**
 * Manager Layer For OData Calls regarding product recommendations
 */
@BackendType("CEI")
public class OdataProductRecommendationManagerCEI implements ProductRecommendationManagerBackend
{
	private final static Logger LOG = Logger.getLogger(OdataProductRecommendationManagerCEI.class.getName());

	private String baseURL;
	private ODataClientService clientService;
	private GenericFactory genericFactory;
	private ProductRecommendationManagerUtil recommendationService;

	private static final String APPLICATION_JSON = "application/json";
	private static final String CSRF_TOKEN = "csrfToken";
	private static final String COOKIE = "cookie";
	private static final String EDM = "edm";
	private static final String USERID = "UserId";
	private static final String USERTYPE = "UserType";
	private static final String RESULT_OBJECT_ID = "ResultObjectId";
	private static final String LEADING_OBJECT_ID = "LeadingObjectId";
	private static final String LEADING_OBJECT_TYPE = "LeadingObjectType";
	private static final String BASKET_OBJECT_ID = "BasketObjectId";
	private static final String BASKET_OBJECT_TYPE = "BasketObjectType";
	private static final String INTERACTION_TYPE = "InteractionType";
	private static final String SOURCE_OBJECT_ID = "SourceObjectId";
	private static final String ITEM_TYPE = "ItemType";
	private static final String ITEM_ID = "ItemId";
	private static final String ITEM_NAV_URL = "ItemNavUrl";
	private static final String CLICK_THROUGH = "CLICK_THROUGH";
	private static final String PRI_CALLBACK = "PRICallback";
	private static final String IA_CALLBACK = "IACallback";
	private static final String TIMESTAMP = "TimeStamp";

	/**
	 * Creates payload for backend PRI call using OData
	 *
	 * @param contexts
	 * @return recommendendationScenario
	 */
	private void createPayload(final List<RecommendationContext> contexts, Map<String, Object> recommendendationScenario)
	{
		recommendendationScenario.put(USERID, contexts.get(0).getUserId());
		recommendendationScenario.put(USERTYPE, contexts.get(0).getUserType());
		recommendendationScenario.put(SapproductrecommendationConstants.CONTEXT_PARAMS, null);
		recommendendationScenario.put(SapproductrecommendationConstants.RESULT_OBJECTS, null);
		recommendendationScenario.put(SapproductrecommendationConstants.SCENARIO_HASHES, null);
		final List<Map<String, Object>> scenarios = new ArrayList<>();
		for (final RecommendationContext context : contexts)
		{
			final Map<String, Object> scenario = new HashMap<>();

			scenario.put(SapproductrecommendationConstants.SCENARIO_ID, context.getRecotype());
			
			final List<Map<String, String>> leadingObjects = new ArrayList<>();
			for (String leadingItem : getRecommendationService().getLeadingItemId(context))
			{
				Map<String, String> leadingObject = new HashMap<>();
				leadingObject.put(LEADING_OBJECT_ID, leadingItem);
				leadingObject.put(LEADING_OBJECT_TYPE, context.getLeadingItemDSType());
				leadingObjects.add(leadingObject);
			}

			final List<Map<String, String>> basketObjects = new ArrayList<>();
			addCartItems(basketObjects, context);
			if (context.isIncludeCart())
			{
				addCartItemsAsLeadingItems(leadingObjects, context);
			}
			if (context.isIncludeRecent())
			{
				addRecentItemsAsLeadingItems(leadingObjects, context);
			}

			scenario.put(SapproductrecommendationConstants.LEADING_OBJECTS, leadingObjects);
			scenario.put(SapproductrecommendationConstants.BASKET_OBJECTS, basketObjects);
			scenarios.add(scenario);
		}
		recommendendationScenario.put(SapproductrecommendationConstants.SCENARIOS, scenarios);
	}

	/**
	 * Creates payload for backend Interaction call using OData
	 *
	 * @param contexts
	 * @return recommendendationScenario
	 */
	private Map<String, Object> createInteractionPayload(final InteractionContext context)
	{
		final Map<String, Object> interactionScenario = new HashMap<>();
		interactionScenario.put(SapproductrecommendationConstants.SCENARIO_ID, context.getScenarioId());
		interactionScenario.put(USERID, context.getUserId());
		interactionScenario.put(USERTYPE, context.getUserType());
		interactionScenario.put(INTERACTION_TYPE, new String(CLICK_THROUGH));
		interactionScenario.put(TIMESTAMP, System.currentTimeMillis());
		interactionScenario.put(SOURCE_OBJECT_ID, context.getSourceObjectId());
		final List<Map<String, String>> interactionItems = new ArrayList<>();
		final Map<String, String> interactionItem = new HashMap<>();
		interactionItem.put(ITEM_TYPE, context.getProductType());
		interactionItem.put(ITEM_ID, context.getProductId());
		interactionItem.put(ITEM_NAV_URL, context.getProductNavURL());
		interactionItems.add(interactionItem);

		interactionScenario.put(SapproductrecommendationConstants.INTERACTION_ITEMS, interactionItems);
		return interactionScenario;
	}

	/**
	 * Adds cart items to payload
	 *
	 * @param basketObjects
	 * @param context
	 */
	private void addCartItems(final List<Map<String, String>> basketObjects, final RecommendationContext context)
	{
		if (StringUtils.isNotBlank(context.getCartItemDSType()))
		{
			for (final String cartItem : getRecommendationService().getCartItemsFromSession())
			{
				final Map<String, String> basketObject = new HashMap<>();
				basketObject.put(BASKET_OBJECT_ID, cartItem);
				basketObject.put(BASKET_OBJECT_TYPE, context.getCartItemDSType());
				basketObjects.add(basketObject);
			}
		}
		else
		{
			LOG.debug("Cart Item Data Source Type is not maintained - Cart Items not added");
		}
		
		
	}

	/**
	 * Adds cart items as leading objects to payload
	 *
	 * @param leadingObjects
	 * @param context
	 */
	private void addCartItemsAsLeadingItems(final List<Map<String, String>> leadingObjects, final RecommendationContext context)
	{
		if (StringUtils.isNotBlank(context.getLeadingItemDSType()))
		{
			for (final String cartItem : getRecommendationService().getCartItemsFromSession())
			{
				final Map<String, String> leadingObject = new HashMap<>();
				leadingObject.put(LEADING_OBJECT_ID, cartItem);
				leadingObject.put(LEADING_OBJECT_TYPE, context.getLeadingItemDSType());
				leadingObjects.add(leadingObject);
			}
		}
		else
		{
			LOG.debug("Leading Item Data Source Type is not maintained - Cart Items not added as Leading Item");
		}
	}
	
	/**
	 * Adds recent items as leading objects to payload
	 *
	 * @param leadingObjects
	 * @param context
	 */
	private void addRecentItemsAsLeadingItems(final List<Map<String, String>> leadingObjects, final RecommendationContext context)
	{
		if (StringUtils.isNotBlank(context.getLeadingItemDSType()))
		{
			for (final String recentItem : getRecommendationService().getRecentItemsFromSession(context.getLeadingItemType()))
			{
				final Map<String, String> leadingObject = new HashMap<>();
				leadingObject.put(LEADING_OBJECT_ID, recentItem);
				leadingObject.put(LEADING_OBJECT_TYPE, context.getLeadingItemDSType());
				leadingObjects.add(leadingObject);
			}
		}
		else
		{
			LOG.debug("Leading Item Data Source Type is not maintained - Recent Viewed Items not added as Leading Items");
		}
	}

	/**
	 * Fetch Product Recommendations from hybris Marketing based on scenario
	 *
	 * @param recommendationScenario
	 * @param recommendationsMap
	 * @param retriesLeft
	 * @return list of recommendations
	 */
	private List<String> getRecommendations(final Map<String, Object> recommendationScenario,
			final Map<String, List<String>> recommendationsMap )
	{
		final String user = getRecommendationService().getConfiguration().getHttpDestination().getUserid();
		final String password = getRecommendationService().getConfiguration().getHttpDestination().getPassword();
		final String client = getRecommendationService().getConfiguration().getHttpDestinationSAPClient();
		final String serviceUri = getRecommendationService().getConfiguration().getHttpDestinationURL() + baseURL;
		final List<String> recotypes = new ArrayList<>();
		
		try
		{
			final List<String> entities = Arrays.asList(	SapproductrecommendationConstants.SCENARIOS, 
                                       					SapproductrecommendationConstants.SCENARIO_HASHES,
                                       					SapproductrecommendationConstants.CONTEXT_PARAMS, 
                                       					SapproductrecommendationConstants.RESULT_OBJECTS);

			Map<String, Object> headerValues = null;
			synchronized (getRecommendationService().getSessionService().getCurrentSession())
			{

				if (getRecommendationService().getSessionService().getAttribute(CSRF_TOKEN) != null
						&& getRecommendationService().getSessionService().getAttribute(COOKIE) != null
						&& getRecommendationService().getSessionService().getAttribute(EDM) != null)
				{
					headerValues = new HashMap<String, Object>();
					headerValues.put(CSRF_TOKEN, getRecommendationService().getSessionService().getAttribute(CSRF_TOKEN));
					headerValues.put(COOKIE, getRecommendationService().getSessionService().getAttribute(COOKIE));
					headerValues.put(EDM, getRecommendationService().getSessionService().getAttribute(EDM));
				}
				else
				{
					headerValues = this.getClientService().getCSRFAndCookie(serviceUri,
							SapproductrecommendationConstants.APPLICATION_XML, SapproductrecommendationConstants.HTTP_METHOD_GET, user, password);


					if (headerValues.containsKey(CSRF_TOKEN) && headerValues.get(CSRF_TOKEN) != null)
					{
						getRecommendationService().getSessionService().setAttribute(CSRF_TOKEN, headerValues.get(CSRF_TOKEN));
					}
					if (headerValues.containsKey(COOKIE) && headerValues.get(COOKIE) != null)
					{
						getRecommendationService().getSessionService().setAttribute(COOKIE, headerValues.get(COOKIE));
					}
					if (headerValues.containsKey(EDM) && headerValues.get(EDM) != null)
					{
						getRecommendationService().getSessionService().setAttribute(EDM, headerValues.get(EDM));
					}
				}

			}

			final Map<String, Object> properties = 
					this.getClientService().writeEntity(serviceUri, SapproductrecommendationConstants.RECOMMENDATION_SCENARIOS, recommendationScenario, APPLICATION_JSON, SapproductrecommendationConstants.HTTP_METHOD_POST, user,
					password, client, entities, headerValues, getMyCallback(PRI_CALLBACK)).getProperties();

			if (properties != null)
			{
				for (final Entry<String, Object> entrye : properties.entrySet())
				{
					if (entrye.getKey().contains(SapproductrecommendationConstants.SCENARIO_HASHES))
					{
						for (final ODataEntry e : ((ODataFeed) entrye.getValue()).getEntries())
						{
							final Map<String, Object> scenHashPros =  e.getProperties();
							if (scenHashPros != null)
							{
								for (final Entry<String, Object> e2 : scenHashPros.entrySet())
								{
									if (e2.getKey().contains(SapproductrecommendationConstants.HASH_ID))
									{
										String hashId = e2.getValue().toString();
										recommendationScenario.put(SapproductrecommendationConstants.HASH_ID, hashId);
									}
									
									if (e2.getKey().contains(SapproductrecommendationConstants.RESULT_SCOPE))
									{
										String resultScope = e2.getValue().toString();
										recommendationScenario.put(SapproductrecommendationConstants.RESULT_SCOPE, resultScope);
									}
									
									if (e2.getKey().contains(SapproductrecommendationConstants.EXPIRES_ON))
									{
										GregorianCalendar expiresOn = (GregorianCalendar) e2.getValue();
										recommendationScenario.put(SapproductrecommendationConstants.EXPIRES_ON, expiresOn);
									}
									
								}
							}
						}
					}
					if (entrye.getKey().contains(SapproductrecommendationConstants.RESULT_OBJECTS))
					{
						for (final ODataEntry e : ((ODataFeed) entrye.getValue()).getEntries())
						{
							final Map<String, Object> properties2 = e.getProperties();
							if (properties2 != null)
							{
								String scenarioId = null;
								for (final Entry<String, Object> e2 : properties2.entrySet())
								{
									if (e2.getKey().contains(SapproductrecommendationConstants.SCENARIO_ID))
									{
										scenarioId = e2.getValue().toString();
										if (!recotypes.contains(scenarioId))
										{
											recotypes.add(scenarioId);
										}
									}
									if (e2.getKey().contains(RESULT_OBJECT_ID))
									{
										final String leadingItemId = e2.getValue().toString();
										List<String> productRecommendations = recommendationsMap.get(scenarioId);
										if (productRecommendations == null)
										{
											productRecommendations = new ArrayList<>();
										}
										productRecommendations.add(leadingItemId);
										recommendationsMap.put(scenarioId, productRecommendations);
									}
								}
							}
						}
					}
				}
			}
		}
		catch (EdmException | IOException | EntityProviderException | URISyntaxException e)
		{
			LOG.error("Error getting Recommendations from Backend System: " + e.getClass().getName(), e);
		}
		catch (final NullPointerException e)
		{
			LOG.error("NullPointerException while getting Recommendations from Backend System", e);
		}

		return recotypes;
	}

	@Override
	public List<ProductRecommendationData> getProductRecommendation(final RecommendationContext context, final Map<String, Object> recommendationScenario)
	{
		
		final List<RecommendationContext> contexts = Lists.newArrayList();
		contexts.add(context);
		createPayload(contexts, recommendationScenario);
		
		final Map<String, List<String>> recommendationsMap = new HashMap<>();
		final List<String> recoTypes = getRecommendations(recommendationScenario, recommendationsMap);
		List<String> recoIds = Lists.newArrayList();
		
		if (recoTypes != null && !recoTypes.isEmpty() && recommendationsMap.get(recoTypes.get(0)) != null)
		{
			recoIds = recommendationsMap.get(recoTypes.get(0));
		}
		
		final List<ProductRecommendationData> result = Lists.newArrayList();
		if (recoIds != null)
		{
			for (final String recommendationId : recoIds)
			{
				result.add(getRecommendationService().createProductRecommedation(recommendationId));
			}
		}
		
		return result;
	}

	public void postInteraction(final InteractionContext context)
	{
		LOG.debug("\n\n POST INTERACTION : ODATA \n\n");

		final String contentType = APPLICATION_JSON;
		final String user = getRecommendationService().getConfiguration().getHttpDestination().getUserid();
		final String password = getRecommendationService().getConfiguration().getHttpDestination().getPassword();
		final String client = getRecommendationService().getConfiguration().getHttpDestinationSAPClient();
		final String serviceUri = getRecommendationService().getConfiguration().getHttpDestinationURL() + baseURL;
		final String entitySetName = SapproductrecommendationConstants.INTERACTIONS;

		try
		{
			Map<String, Object> headerValues = null;
			if (getRecommendationService().getSessionService().getAttribute(CSRF_TOKEN) != null
					&& getRecommendationService().getSessionService().getAttribute(COOKIE) != null
					&& getRecommendationService().getSessionService().getAttribute(EDM) != null)
			{
				headerValues = new HashMap<String, Object>();
				headerValues.put(CSRF_TOKEN, getRecommendationService().getSessionService().getAttribute(CSRF_TOKEN));
				headerValues.put(COOKIE, getRecommendationService().getSessionService().getAttribute(COOKIE));
				headerValues.put(EDM, getRecommendationService().getSessionService().getAttribute(EDM));
			}
			else
			{
				headerValues = this.getClientService().getCSRFAndCookie(serviceUri, SapproductrecommendationConstants.APPLICATION_XML,
						SapproductrecommendationConstants.HTTP_METHOD_GET, user, password);
				if (headerValues.containsKey(CSRF_TOKEN) && headerValues.get(CSRF_TOKEN) != null)
				{
					getRecommendationService().getSessionService().setAttribute(CSRF_TOKEN, headerValues.get(CSRF_TOKEN));
				}
				if (headerValues.containsKey(COOKIE) && headerValues.get(COOKIE) != null)
				{
					getRecommendationService().getSessionService().setAttribute(COOKIE, headerValues.get(COOKIE));
				}
				if (headerValues.containsKey(EDM) && headerValues.get(EDM) != null)
				{
					getRecommendationService().getSessionService().setAttribute(EDM, headerValues.get(EDM));
				}
			}

			final List<String> entities = Arrays.asList(SapproductrecommendationConstants.INTERACTION_ITEMS);
			this.clientService.writeEntity(serviceUri, entitySetName, createInteractionPayload(context), contentType, SapproductrecommendationConstants.HTTP_METHOD_POST,
					user, password, client, entities, headerValues, getMyCallback(IA_CALLBACK));
			LOG.debug("\n ODATA : Interaction posted for product click \n");
		}
		catch (final NullPointerException | EdmException | EntityProviderException | IOException | URISyntaxException e)
		{
			LOG.error("Posting Interaction failed due to " + e.getClass().getName(), e);
		}
	}
	
	public void postImpression(final ImpressionContext context)
	{
		LOG.debug("\n\n POST IMPRESSION: ODATA \n\n");

		final String contentType = APPLICATION_JSON;
		final String user = getRecommendationService().getConfiguration().getHttpDestination().getUserid();
		final String password = getRecommendationService().getConfiguration().getHttpDestination().getPassword();
		final String client = getRecommendationService().getConfiguration().getHttpDestinationSAPClient();
		final String serviceUri = getRecommendationService().getConfiguration().getHttpDestinationURL() + baseURL;

		final Map<String, Object> headerValues;
		if (getRecommendationService().getSessionService().getAttribute(CSRF_TOKEN) != null
				&& getRecommendationService().getSessionService().getAttribute(COOKIE) != null
				&& getRecommendationService().getSessionService().getAttribute(EDM) != null)
		{
			headerValues = new HashMap<String, Object>();
			headerValues.put(CSRF_TOKEN, getRecommendationService().getSessionService().getAttribute(CSRF_TOKEN));
			headerValues.put(COOKIE, getRecommendationService().getSessionService().getAttribute(COOKIE));
			headerValues.put(EDM, getRecommendationService().getSessionService().getAttribute(EDM));
		}
		else
		{
			headerValues = this.getClientService().getCSRFAndCookie(serviceUri, SapproductrecommendationConstants.APPLICATION_XML,
					SapproductrecommendationConstants.HTTP_METHOD_GET, user, password);
			if (headerValues.containsKey(CSRF_TOKEN) && headerValues.get(CSRF_TOKEN) != null)
			{
				getRecommendationService().getSessionService().setAttribute(CSRF_TOKEN, headerValues.get(CSRF_TOKEN));
			}
			if (headerValues.containsKey(COOKIE) && headerValues.get(COOKIE) != null)
			{
				getRecommendationService().getSessionService().setAttribute(COOKIE, headerValues.get(COOKIE));
			}
			if (headerValues.containsKey(EDM) && headerValues.get(EDM) != null)
			{
				getRecommendationService().getSessionService().setAttribute(EDM, headerValues.get(EDM));
			}
		}

		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					LOG.info("\n Impression posting for scenario\n");
					String parameters = createPostParameters(context);
					String urlWithParams = serviceUri + "/" + SapproductrecommendationConstants.IMPRESSION + "?" + parameters;
					boolean success = clientService.post(urlWithParams, contentType, SapproductrecommendationConstants.IMPRESSION, 
							SapproductrecommendationConstants.HTTP_METHOD_POST, user, password, client, headerValues);
					if(!success)
					{
						LOG.error("Posting impression was not successful");
					}
				}
				catch (URISyntaxException | IOException | ODataException e)
				{
					LOG.error("Error posting recommendation impression to the backend", e);
				}
				LOG.debug("\n ODATA : Impression posted for Recommendation View\n");
			}
		}).start();
	}
	
	private String createPostParameters(ImpressionContext context)
	{		
		if (context != null)
		{
			String parameters = "";
			parameters += SapproductrecommendationConstants.SCENARIO_ID + "='" + context.getScenarioId()+"'";
			parameters += "&" + SapproductrecommendationConstants.TIMESTAMP + "=" + getTimeStampWithOffset();
			parameters += "&" + SapproductrecommendationConstants.IMPRESSION_COUNT + "=" + context.getImpressionCount();
			parameters += "&" + SapproductrecommendationConstants.ITEM_COUNT + "=" + context.getItemCount();
			return parameters;
		}
		return "";
	}
	
	private String getTimeStampWithOffset()
	{
		String timeStamp = "datetimeoffset'";
		String time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
		timeStamp += time + "Z'";
		return timeStamp;
	}

	@Override
	public boolean pingBackendSystem()
	{
		final String absoluteUri = getRecommendationService().getConfiguration().getHttpDestinationURL();
		return clientService.pingRemoteSystem(absoluteUri, APPLICATION_JSON, SapproductrecommendationConstants.HTTP_METHOD_HEAD, "", "");
	}

	/**
	 * @return clientService
	 */
	public ODataClientService getClientService()
	{
		return clientService;
	}

	/**
	 * @param clientService
	 */
	public void setClientService(final ODataClientService clientService)
	{
		this.clientService = clientService;
	}

	/**
	 * initialize Backend Object
	 */
	@Override
	public void initBackendObject() throws BackendException
	{

	}

	/**
	 * destroy Backend Object
	 */
	@Override
	public void destroyBackendObject()
	{
		
	}

	/**
	 * @return recommendationService
	 */
	public ProductRecommendationManagerUtil getRecommendationService()
	{
		return recommendationService;
	}

	/**
	 * @param recommendationService
	 */
	public void setRecommendationService(final ProductRecommendationManagerUtil recommendationService)
	{
		this.recommendationService = recommendationService;
	}

	/**
	 * @return genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * @param genericFactory
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * @param callbackBean
	 * @return ProdRecoCallback
	 */
	public MyCallback getMyCallback(final String callbackBean)
	{
		return getGenericFactory().getBean(callbackBean);
	}

	/**
	 *
	 * @return baseURL
	 */
	public String getBaseURL()
	{
		return baseURL;
	}

	/**
	 *
	 * @param baseURL
	 */
	public void setBaseURL(final String baseURL)
	{
		this.baseURL = baseURL;
	}

}