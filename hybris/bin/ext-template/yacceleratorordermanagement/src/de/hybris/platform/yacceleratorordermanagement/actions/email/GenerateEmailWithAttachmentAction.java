/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.yacceleratorordermanagement.actions.email;

import de.hybris.platform.acceleratorservices.email.CMSEmailPageService;
import de.hybris.platform.acceleratorservices.email.EmailGenerationService;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.warehousing.labels.service.PrintMediaService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A process action to generate email with attachment.
 */
public class GenerateEmailWithAttachmentAction extends AbstractSimpleDecisionAction
{
	private static final Logger LOG = Logger.getLogger(GenerateEmailWithAttachmentAction.class);

	private CMSEmailPageService cmsEmailPageService;
	private String frontendTemplateName;
	private String documentTemplateName;
	private ProcessContextResolutionStrategy contextResolutionStrategy;
	private EmailGenerationService emailGenerationService;
	private EmailService emailService;
	private PrintMediaService printMediaService;
	private MediaService mediaService;
	private String emailAttachmentName;
	private KeyGenerator guidKeyGenerator;

	@Override
	public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException
	{

		final CatalogVersionModel contentCatalogVersion = getContextResolutionStrategy().getContentCatalogVersion(
				businessProcessModel);
		if (contentCatalogVersion != null)
		{
			final EmailPageModel emailPageModel = getCmsEmailPageService().getEmailPageForFrontendTemplate(
					getFrontendTemplateName(), contentCatalogVersion);

			if (emailPageModel != null)
			{
				final EmailMessageModel emailMessageModel = getEmailGenerationService()
						.generate(businessProcessModel, emailPageModel);
				if (emailMessageModel != null)
				{
					Assert.isTrue(businessProcessModel instanceof ReturnProcessModel,
							"Business Process is not a return process type ");
					MediaModel mediaModel = getOrCreateReturnLabelFromMedia((ReturnProcessModel) businessProcessModel);
					EmailAttachmentModel emailAttachmentModel = castMedialModelToEmailAttachmentModel(mediaModel);

					if (emailAttachmentModel != null)
					{
						List<EmailAttachmentModel> emailAttachmentModelList = new ArrayList<>();
						emailAttachmentModelList.add(emailAttachmentModel);
						emailMessageModel.setAttachments(emailAttachmentModelList);
						getModelService().save(emailMessageModel);
						final List<EmailMessageModel> emails = new ArrayList<EmailMessageModel>();
						emails.addAll(businessProcessModel.getEmails());
						emails.add(emailMessageModel);
						businessProcessModel.setEmails(emails);
						getModelService().save(businessProcessModel);
						LOG.info("Email message generated");
						return Transition.OK;
					}
					else
					{
						LOG.warn("Failed to add attachment to email");
					}
				}
				else
				{
					LOG.warn("Failed to generate email message");
				}
			}
			else
			{
				LOG.warn("Could not retrieve email page model for " + getFrontendTemplateName() + " and "
						+ contentCatalogVersion.getCatalog().getName() + ":" + contentCatalogVersion.getVersion()
						+ ", cannot generate email content");
			}
		}
		else
		{
			LOG.warn("Could not resolve the content catalog version, cannot generate email content");
		}

		return Transition.NOK;
	}

	protected MediaModel getOrCreateReturnLabelFromMedia(ReturnProcessModel returnProcessModel)
	{
		ReturnRequestModel returnRequestModel = returnProcessModel.getReturnRequest();
		MediaModel returnLabel = returnRequestModel.getReturnLabel();
		if (returnLabel == null)
		{
			returnLabel = getPrintMediaService().getMediaForTemplate(getDocumentTemplateName(), returnProcessModel);
			returnRequestModel.setReturnLabel(returnLabel);
			getModelService().save(returnRequestModel);
		}
		LOG.debug(String.format("Found [%s] return shipping labels to be sent to customers", returnLabel.getCode()));
		return returnLabel;
	}

	protected EmailAttachmentModel castMedialModelToEmailAttachmentModel(MediaModel mediaModel)
	{
		EmailAttachmentModel emailAttachmentModel = getEmailService()
				.createEmailAttachment(new DataInputStream(mediaService.getStreamFromMedia(mediaModel)),
						getEmailAttachmentName() + "-" + getGuidKeyGenerator().generate().toString(),
						mediaModel.getMime());
		return emailAttachmentModel;
	}

	protected CMSEmailPageService getCmsEmailPageService()
	{
		return cmsEmailPageService;
	}

	@Required
	public void setCmsEmailPageService(final CMSEmailPageService cmsEmailPageService)
	{
		this.cmsEmailPageService = cmsEmailPageService;
	}

	protected String getFrontendTemplateName()
	{
		return frontendTemplateName;
	}

	@Required
	public void setFrontendTemplateName(final String frontendTemplateName)
	{
		this.frontendTemplateName = frontendTemplateName;
	}

	protected ProcessContextResolutionStrategy getContextResolutionStrategy()
	{
		return contextResolutionStrategy;
	}

	@Required
	public void setContextResolutionStrategy(final ProcessContextResolutionStrategy contextResolutionStrategy)
	{
		this.contextResolutionStrategy = contextResolutionStrategy;
	}

	protected EmailGenerationService getEmailGenerationService()
	{
		return emailGenerationService;
	}

	@Required
	public void setEmailGenerationService(final EmailGenerationService emailGenerationService)
	{
		this.emailGenerationService = emailGenerationService;
	}

	@Required
	public void setEmailService(final EmailService emailService)
	{
		this.emailService = emailService;
	}

	protected EmailService getEmailService()
	{
		return emailService;
	}

	protected PrintMediaService getPrintMediaService()
	{
		return printMediaService;
	}

	@Required
	public void setPrintMediaService(final PrintMediaService printMediaService)
	{
		this.printMediaService = printMediaService;
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected String getDocumentTemplateName()
	{
		return documentTemplateName;
	}

	@Required
	public void setDocumentTemplateName(final String documentTemplateName)
	{
		this.documentTemplateName = documentTemplateName;
	}

	protected KeyGenerator getGuidKeyGenerator()
	{
		return guidKeyGenerator;
	}

	@Required
	public void setGuidKeyGenerator(final KeyGenerator guidKeyGenerator)
	{
		this.guidKeyGenerator = guidKeyGenerator;
	}

	protected String getEmailAttachmentName()
	{
		return emailAttachmentName;
	}

	@Required
	public void setEmailAttachmentName(final String emailAttachmentName)
	{
		this.emailAttachmentName = emailAttachmentName;
	}

}
