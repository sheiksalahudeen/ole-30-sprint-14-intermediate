/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.ole.module.purap.document;

import org.apache.commons.lang.StringUtils;
import org.kuali.ole.module.purap.PurapConstants;
import org.kuali.ole.module.purap.PurapConstants.InvoiceStatuses;
import org.kuali.ole.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.ole.module.purap.PurapParameterConstants;
import org.kuali.ole.module.purap.PurapPropertyConstants;
import org.kuali.ole.module.purap.PurapWorkflowConstants;
import org.kuali.ole.module.purap.businessobject.*;
import org.kuali.ole.module.purap.document.service.*;
import org.kuali.ole.module.purap.service.PurapGeneralLedgerService;
import org.kuali.ole.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.ole.select.businessobject.OleLineItemReceivingItem;
import org.kuali.ole.select.businessobject.OlePurchaseOrderItem;
import org.kuali.ole.select.document.OleLineItemReceivingDocument;
import org.kuali.ole.select.document.OlePurchaseOrderDocument;
import org.kuali.ole.select.document.service.OleInvoiceService;
import org.kuali.ole.sys.OLEConstants;
import org.kuali.ole.sys.businessobject.AccountingLine;
import org.kuali.ole.sys.businessobject.Bank;
import org.kuali.ole.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.ole.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.ole.sys.context.SpringContext;
import org.kuali.ole.sys.service.BankService;
import org.kuali.ole.sys.service.UniversityDateService;
import org.kuali.ole.sys.service.impl.OleParameterConstants;
import org.kuali.ole.vnd.VendorPropertyConstants;
import org.kuali.ole.vnd.businessobject.PaymentTermType;
import org.kuali.ole.vnd.businessobject.PurchaseOrderCostSource;
import org.kuali.ole.vnd.businessobject.ShippingPaymentTerms;
import org.kuali.ole.vnd.businessobject.VendorDetail;
import org.kuali.ole.vnd.document.service.VendorService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.DocumentHeaderService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Payment Request Document Business Object. Contains the fields associated with the main document table.
 */
public class InvoiceDocument extends AccountsPayableDocumentBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceDocument.class);

    protected Date invoiceDate;
    protected String invoiceNumber;
    protected KualiDecimal vendorInvoiceAmount;
    protected String vendorPaymentTermsCode;
    protected String vendorShippingPaymentTermsCode;
    protected Date invoicePayDate;
    protected String invoiceCostSourceCode;
    protected boolean invoiceCancelIndicator;
    protected boolean paymentAttachmentIndicator;
    protected boolean immediatePaymentIndicator;
    protected String specialHandlingInstructionLine1Text;
    protected String specialHandlingInstructionLine2Text;
    protected String specialHandlingInstructionLine3Text;
    protected Timestamp paymentPaidTimestamp;
    protected boolean invoiceElectronicInvoiceIndicator;
    protected String accountsPayableRequestCancelIdentifier;
    protected Integer originalVendorHeaderGeneratedIdentifier;
    protected Integer originalVendorDetailAssignedIdentifier;
    protected Integer alternateVendorHeaderGeneratedIdentifier;
    protected Integer alternateVendorDetailAssignedIdentifier;
    protected String purchaseOrderNotes;
    protected String recurringPaymentTypeCode;
    protected boolean receivingDocumentRequiredIndicator;
    protected boolean invoicePositiveApprovalIndicator;

    // TAX EDIT AREA FIELDS
    protected String taxClassificationCode;
    protected String taxCountryCode;
    protected String taxNQIId;
    protected BigDecimal taxFederalPercent; // number is in whole form so 5% is 5.00
    protected BigDecimal taxStatePercent; // number is in whole form so 5% is 5.00
    protected KualiDecimal taxSpecialW4Amount;
    protected Boolean taxGrossUpIndicator;
    protected Boolean taxExemptTreatyIndicator;
    protected Boolean taxForeignSourceIndicator;
    protected Boolean taxUSAIDPerDiemIndicator;
    protected Boolean taxOtherExemptIndicator;

    // NOT PERSISTED IN DB
    protected String vendorShippingTitleCode;
    protected Date purchaseOrderEndDate;
    protected String primaryVendorName;

    // BELOW USED BY ROUTING
    protected Integer requisitionIdentifier;

    // REFERENCE OBJECTS
    protected PaymentTermType vendorPaymentTerms;
    protected ShippingPaymentTerms vendorShippingPaymentTerms;
    protected PurchaseOrderCostSource invoiceCostSource;
    protected RecurringPaymentType recurringPaymentType;
    private List<OlePurchaseOrderDocument> purchaseOrderDocuments = new ArrayList<OlePurchaseOrderDocument>();
    private static transient OlePurapService olePurapService;

    public List<OlePurchaseOrderDocument> getPurchaseOrderDocuments() {
        return purchaseOrderDocuments;
    }

    public void setPurchaseOrderDocuments(List<OlePurchaseOrderDocument> purchaseOrderDocuments) {
        this.purchaseOrderDocuments = purchaseOrderDocuments;
    }

    /*private InvoiceService invoiceService;*/


    /**
     * Default constructor.
     */
    public InvoiceDocument() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    public boolean isBoNotesSupport() {
        return true;
    }

    public static OlePurapService getOlePurapService() {
        if (olePurapService == null) {
            olePurapService = SpringContext.getBean(OlePurapService.class);
        }
        return olePurapService;
    }

    public Integer getPostingYearPriorOrCurrent() {
        if (SpringContext.getBean(InvoiceService.class).allowBackpost(this)) {
            // allow prior; use it
            return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear() - 1;
        }
        // don't allow prior; use CURRENT
        return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }


    /**
     * Overrides the method in PurchasingAccountsPayableDocumentBase to add the criteria specific to Payment Request Document.
     *
     * @see org.kuali.ole.module.purap.document.PurchasingAccountsPayableDocumentBase#isInquiryRendered()
     */
    @Override
    public boolean isInquiryRendered() {
        if (isPostingYearPrior() && (getApplicationDocumentStatus().equals(InvoiceStatuses.APPDOC_DEPARTMENT_APPROVED) || getApplicationDocumentStatus().equals(InvoiceStatuses.APPDOC_AUTO_APPROVED) || getApplicationDocumentStatus().equals(InvoiceStatuses.APPDOC_CANCELLED_POST_AP_APPROVE))) {
            return false;
        } else {
            return true;
        }
    }

    public Integer getRequisitionIdentifier() {
        return getPurchaseOrderDocument().getRequisitionIdentifier();
    }

    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    /**
     * @see org.kuali.ole.module.purap.document.AccountsPayableDocumentBase#populateDocumentForRouting()
     */
   /* @Override
    public void populateDocumentForRouting() {
        this.setRequisitionIdentifier(getPurchaseOrderDocument().getRequisitionIdentifier());
        super.populateDocumentForRouting();
    }*/

    /**
     * Decides whether receivingDocumentRequiredIndicator functionality shall be enabled according to the controlling parameter.
     */
    public boolean isEnableReceivingDocumentRequiredIndicator() {
        return SpringContext.getBean(OleInvoiceService.class).getParameterBoolean(OLEConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE, OleParameterConstants.DOCUMENT_COMPONENT,PurapParameterConstants.RECEIVING_DOCUMENT_REQUIRED_IND);
    }

    /**
     * Decides whether invoicePositiveApprovalIndicator functionality shall be enabled according to the controlling parameter.
     */
    public boolean isEnableInvoicePositiveApprovalIndicator() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(OleParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.PAYMENT_REQUEST_POSITIVE_APPROVAL_IND);
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        if (!StringUtils.isEmpty(invoiceNumber)) {
            this.invoiceNumber = invoiceNumber.toUpperCase();
        } else {
            this.invoiceNumber = invoiceNumber;
        }
    }

    public KualiDecimal getVendorInvoiceAmount() {
        return vendorInvoiceAmount;
    }

    public void setVendorInvoiceAmount(KualiDecimal vendorInvoiceAmount) {
        this.vendorInvoiceAmount = vendorInvoiceAmount;
    }

    public String getVendorPaymentTermsCode() {
        return vendorPaymentTermsCode;
    }

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
        refreshReferenceObject("vendorPaymentTerms");
    }

    public PaymentTermType getVendorPaymentTerms() {
        if (ObjectUtils.isNull(vendorPaymentTerms) || !StringUtils.equalsIgnoreCase(getVendorPaymentTermsCode(), vendorPaymentTerms.getVendorPaymentTermsCode())) {
            refreshReferenceObject(VendorPropertyConstants.VENDOR_PAYMENT_TERMS);
        }
        return vendorPaymentTerms;
    }

    public void setVendorPaymentTerms(PaymentTermType vendorPaymentTerms) {
        this.vendorPaymentTerms = vendorPaymentTerms;
    }

    public String getVendorShippingPaymentTermsCode() {
        if (ObjectUtils.isNull(vendorPaymentTerms)) {
            refreshReferenceObject(VendorPropertyConstants.VENDOR_SHIPPING_PAYMENT_TERMS);
        }
        return vendorShippingPaymentTermsCode;
    }

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public Date getInvoicePayDate() {
        return invoicePayDate;
    }

    public void setInvoicePayDate(Date invoicePayDate) {
        this.invoicePayDate = invoicePayDate;
    }

    public String getInvoiceCostSourceCode() {
        return invoiceCostSourceCode;
    }

    public void setInvoiceCostSourceCode(String invoiceCostSourceCode) {
        this.invoiceCostSourceCode = invoiceCostSourceCode;
    }

    public boolean getInvoiceCancelIndicator() {
        return invoiceCancelIndicator;
    }

    public boolean isInvoiceCancelIndicator() {
        return invoiceCancelIndicator;
    }

    public void setInvoiceCancelIndicator(boolean invoiceCancelIndicator) {
        this.invoiceCancelIndicator = invoiceCancelIndicator;
    }

    public boolean getPaymentAttachmentIndicator() {
        return paymentAttachmentIndicator;
    }

    public void setPaymentAttachmentIndicator(boolean paymentAttachmentIndicator) {
        this.paymentAttachmentIndicator = paymentAttachmentIndicator;
    }

    public boolean getImmediatePaymentIndicator() {
        return immediatePaymentIndicator;
    }

    public void setImmediatePaymentIndicator(boolean immediatePaymentIndicator) {
        this.immediatePaymentIndicator = immediatePaymentIndicator;
    }

    public String getSpecialHandlingInstructionLine1Text() {
        return specialHandlingInstructionLine1Text;
    }

    public void setSpecialHandlingInstructionLine1Text(String specialHandlingInstructionLine1Text) {
        this.specialHandlingInstructionLine1Text = specialHandlingInstructionLine1Text;
    }

    public String getSpecialHandlingInstructionLine2Text() {
        return specialHandlingInstructionLine2Text;
    }

    public void setSpecialHandlingInstructionLine2Text(String specialHandlingInstructionLine2Text) {
        this.specialHandlingInstructionLine2Text = specialHandlingInstructionLine2Text;
    }

    public String getSpecialHandlingInstructionLine3Text() {
        return specialHandlingInstructionLine3Text;
    }

    public void setSpecialHandlingInstructionLine3Text(String specialHandlingInstructionLine3Text) {
        this.specialHandlingInstructionLine3Text = specialHandlingInstructionLine3Text;
    }

    public Timestamp getPaymentPaidTimestamp() {
        return paymentPaidTimestamp;
    }

    public void setPaymentPaidTimestamp(Timestamp paymentPaidTimestamp) {
        this.paymentPaidTimestamp = paymentPaidTimestamp;
    }

    public boolean getInvoiceElectronicInvoiceIndicator() {
        return invoiceElectronicInvoiceIndicator;
    }

    public void setInvoiceElectronicInvoiceIndicator(boolean invoiceElectronicInvoiceIndicator) {
        this.invoiceElectronicInvoiceIndicator = invoiceElectronicInvoiceIndicator;
    }

    public String getAccountsPayableRequestCancelIdentifier() {
        return accountsPayableRequestCancelIdentifier;
    }

    public void setAccountsPayableRequestCancelIdentifier(String accountsPayableRequestCancelIdentifier) {
        this.accountsPayableRequestCancelIdentifier = accountsPayableRequestCancelIdentifier;
    }

    public Integer getOriginalVendorHeaderGeneratedIdentifier() {
        return originalVendorHeaderGeneratedIdentifier;
    }

    public void setOriginalVendorHeaderGeneratedIdentifier(Integer originalVendorHeaderGeneratedIdentifier) {
        this.originalVendorHeaderGeneratedIdentifier = originalVendorHeaderGeneratedIdentifier;
    }

    public Integer getOriginalVendorDetailAssignedIdentifier() {
        return originalVendorDetailAssignedIdentifier;
    }

    public void setOriginalVendorDetailAssignedIdentifier(Integer originalVendorDetailAssignedIdentifier) {
        this.originalVendorDetailAssignedIdentifier = originalVendorDetailAssignedIdentifier;
    }

    public Integer getAlternateVendorHeaderGeneratedIdentifier() {
        return alternateVendorHeaderGeneratedIdentifier;
    }

    public void setAlternateVendorHeaderGeneratedIdentifier(Integer alternateVendorHeaderGeneratedIdentifier) {
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
    }

    public Integer getAlternateVendorDetailAssignedIdentifier() {
        return alternateVendorDetailAssignedIdentifier;
    }

    public void setAlternateVendorDetailAssignedIdentifier(Integer alternateVendorDetailAssignedIdentifier) {
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
    }

    public ShippingPaymentTerms getVendorShippingPaymentTerms() {
        return vendorShippingPaymentTerms;
    }

    public void setVendorShippingPaymentTerms(ShippingPaymentTerms vendorShippingPaymentTerms) {
        this.vendorShippingPaymentTerms = vendorShippingPaymentTerms;
    }

    public String getVendorShippingTitleCode() {
        if (ObjectUtils.isNotNull(this.getPurchaseOrderDocument())) {
            return this.getPurchaseOrderDocument().getVendorShippingTitleCode();
        }
        return vendorShippingTitleCode;
    }

    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }

    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    /**
     * Gets the invoicePositiveApprovalIndicator attribute.
     *
     * @return Returns the invoicePositiveApprovalIndicator.
     */
    public boolean isInvoicePositiveApprovalIndicator() {
        return invoicePositiveApprovalIndicator;
    }

    /**
     * Sets the invoicePositiveApprovalIndicator attribute value.
     *
     * @param invoicePositiveApprovalIndicator
     *         The invoicePositiveApprovalIndicator to set.
     */
    public void setInvoicePositiveApprovalIndicator(boolean invoicePositiveApprovalIndicator) {
        // if invoicePositiveApprovalIndicator functionality is disabled, always set it to false, overriding the passed-in value
        if (!isEnableInvoicePositiveApprovalIndicator()) {
            invoicePositiveApprovalIndicator = false;
        } else {
            this.invoicePositiveApprovalIndicator = invoicePositiveApprovalIndicator;
        }
    }

    /**
     * Gets the receivingDocumentRequiredIndicator attribute.
     *
     * @return Returns the receivingDocumentRequiredIndicator.
     */
    public boolean isReceivingDocumentRequiredIndicator() {
        return receivingDocumentRequiredIndicator;
    }

    /**
     * Sets the receivingDocumentRequiredIndicator attribute value.
     *
     * @param receivingDocumentRequiredIndicator
     *         The receivingDocumentRequiredIndicator to set.
     */
    public void setReceivingDocumentRequiredIndicator(boolean receivingDocumentRequiredIndicator) {
        // if receivingDocumentRequiredIndicator functionality is disabled, always set it to false, overriding the passed-in value
        if (!isEnableReceivingDocumentRequiredIndicator()) {
            this.receivingDocumentRequiredIndicator = false;
        } else {
            this.receivingDocumentRequiredIndicator = receivingDocumentRequiredIndicator;
        }
    }

    /**
     * Perform logic needed to initiate PRQS Document
     */
    public void initiateDocument() throws WorkflowException {
        LOG.debug("initiateDocument() started");
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (this.getDocumentHeader().getDocumentNumber() == null) {
            this.setDocumentHeader(SpringContext.getBean(DocumentHeaderService.class).getDocumentHeaderById(getDocumentNumber()));
        }
        //oleInvoiceDocument.setItems(items);
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        this.setPostingYear(universityDateService.getCurrentUniversityDate().getUniversityFiscalYear());
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(this.getClass());
        if (defaultBank != null) {
            this.setBankCode(defaultBank.getBankCode());
            this.setBank(defaultBank);
        }
        updateAndSaveAppDocStatus(InvoiceStatuses.APPDOC_INITIATE);
        this.setAccountsPayableProcessorIdentifier(currentUser.getPrincipalId());
        this.setProcessingCampusCode(currentUser.getCampusCode());
        this.refreshNonUpdateableReferences();
        String description = getOlePurapService().getParameter(OLEConstants.INV_DESC);
        description = getOlePurapService().setDocumentDescription(description,null);
        this.getDocumentHeader().setDocumentDescription(description);
    }

    /**
     * Perform logic needed to clear the initial fields on a PRQS Document
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");
        // Clearing document overview fields
        this.getDocumentHeader().setDocumentDescription(null);
        this.getDocumentHeader().setExplanation(null);
        this.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(null);
        this.getDocumentHeader().setOrganizationDocumentNumber(null);

        // Clearing document Init fields
        //this.setPurchaseOrderIdentifier(null);
        this.setInvoiceNumber(null);
        this.setInvoiceDate(null);
        this.setVendorInvoiceAmount(null);
        this.setSpecialHandlingInstructionLine1Text(null);
        this.setSpecialHandlingInstructionLine2Text(null);
        this.setSpecialHandlingInstructionLine3Text(null);
    }

    /**
     * Populates a preq from a PO - delegate method
     *
     * @param po -
     */
    public void populateInvoiceFromPurchaseOrder(PurchaseOrderDocument po) {
        populateInvoiceFromPurchaseOrder(po, new HashMap<String, ExpiredOrClosedAccountEntry>());
    }


    /**
     * Populates a preq from a PO
     *
     * @param po                         Purchase Order Document used for populating the PRQS
     * @param expiredOrClosedAccountList a list of closed or expired accounts
     */
    public void populateInvoiceFromPurchaseOrder(PurchaseOrderDocument po, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        //this.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        this.getDocumentHeader().setOrganizationDocumentNumber(po.getDocumentHeader().getOrganizationDocumentNumber());
        this.setPostingYear(this.getPostingYearPriorOrCurrent());
        this.setReceivingDocumentRequiredIndicator(po.isReceivingDocumentRequiredIndicator());
        //this.setUseTaxIndicator(po.isUseTaxIndicator());
        this.setInvoicePositiveApprovalIndicator(po.isPaymentRequestPositiveApprovalIndicator());
        //this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        //this.setAccountDistributionMethod(po.getAccountDistributionMethod());

        if (po.getPurchaseOrderCostSource() != null) {
            this.setInvoiceCostSource(po.getPurchaseOrderCostSource());
            this.setInvoiceCostSourceCode(po.getPurchaseOrderCostSourceCode());
        }

        if (po.getVendorShippingPaymentTerms() != null) {
            this.setVendorShippingPaymentTerms(po.getVendorShippingPaymentTerms());
            this.setVendorShippingPaymentTermsCode(po.getVendorShippingPaymentTermsCode());
        }

        if (po.getVendorPaymentTerms() != null) {
            this.setVendorPaymentTermsCode(po.getVendorPaymentTermsCode());
            this.setVendorPaymentTerms(po.getVendorPaymentTerms());
        }

        if (po.getRecurringPaymentType() != null) {
            this.setRecurringPaymentType(po.getRecurringPaymentType());
            this.setRecurringPaymentTypeCode(po.getRecurringPaymentTypeCode());
        }

        /*this.setVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        this.setVendorName(po.getVendorName());*/

        // set original vendor
        this.setOriginalVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setOriginalVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());

        // set alternate vendor info as well
        this.setAlternateVendorHeaderGeneratedIdentifier(po.getAlternateVendorHeaderGeneratedIdentifier());
        this.setAlternateVendorDetailAssignedIdentifier(po.getAlternateVendorDetailAssignedIdentifier());

        // populate preq vendor address with the default remit address type for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getPerson().getCampusCode();
        /*VendorAddress vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(po.getVendorHeaderGeneratedIdentifier(), po.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.REMIT, userCampus);
        if (vendorAddress != null) {
            this.templateVendorAddress(vendorAddress);
            this.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
            setVendorAttentionName(StringUtils.defaultString(vendorAddress.getVendorAttentionName()));
        }
        else {
            // set address from PO
            this.setVendorAddressGeneratedIdentifier(po.getVendorAddressGeneratedIdentifier());
            this.setVendorLine1Address(po.getVendorLine1Address());
            this.setVendorLine2Address(po.getVendorLine2Address());
            this.setVendorCityName(po.getVendorCityName());
            this.setVendorAddressInternationalProvinceName(po.getVendorAddressInternationalProvinceName());
            this.setVendorStateCode(po.getVendorStateCode());
            this.setVendorPostalCode(po.getVendorPostalCode());
            this.setVendorCountryCode(po.getVendorCountryCode());

            boolean blankAttentionLine = StringUtils.equalsIgnoreCase("Y", SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.BLANK_ATTENTION_LINE_FOR_PO_TYPE_ADDRESS));

            if (blankAttentionLine) {
                setVendorAttentionName(StringUtils.EMPTY);
            }
            else {
                setVendorAttentionName(StringUtils.defaultString(po.getVendorAttentionName()));
            }
        }*/

        this.setInvoicePayDate(SpringContext.getBean(InvoiceService.class).calculatePayDate(this.getInvoiceDate(), this.getVendorPaymentTerms()));

        AccountsPayableService accountsPayableService = SpringContext.getBean(AccountsPayableService.class);

        if (SpringContext.getBean(InvoiceService.class).encumberedItemExistsForInvoicing(po)) {
            for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) po.getItems()) {
                // check to make sure it's eligible for payment (i.e. active and has encumbrance available
                if (getDocumentSpecificService().poItemEligibleForAp(this, poi)) {
                    InvoiceItem invoiceItem = new InvoiceItem(poi, this, expiredOrClosedAccountList);
                    invoiceItem.setReceivingDocumentRequiredIndicator(po.isReceivingDocumentRequiredIndicator());
                    this.getItems().add(invoiceItem);
                    PurchasingCapitalAssetItem purchasingCAMSItem = po.getPurchasingCapitalAssetItemByItemIdentifier(poi.getItemIdentifier());
                    if (purchasingCAMSItem != null) {
                        invoiceItem.setCapitalAssetTransactionTypeCode(purchasingCAMSItem.getCapitalAssetTransactionTypeCode());
                    }

                    /*
                    // copy usetaxitems over
                    invoiceItem.getUseTaxItems().clear();
                    for (PurApItemUseTax useTax : poi.getUseTaxItems()) {
                        invoiceItem.getUseTaxItems().add(useTax);
                    }
                    */
                }
            }
        }

        // add missing below the line
        SpringContext.getBean(PurapService.class).addBelowLineItems(this);
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

        //fix up below the line items
        SpringContext.getBean(InvoiceService.class).removeIneligibleAdditionalCharges(this);

        this.fixItemReferences();
        this.refreshNonUpdateableReferences();
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        if (SpringContext.getBean(OleInvoiceService.class).getParameterBoolean(OLEConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE, OLEConstants.InvoiceDocument.CMPNT_CD, PurapParameterConstants.PURAP_OVERRIDE_PRQS_DOC_TITLE)) {
            return getCustomDocumentTitle();
        }
        return this.buildDocumentTitle(super.getDocumentTitle());
    }

    /**
     * Returns a custom document title based on the workflow document title. Depending on what route level the document is currently
     * in, the PO, vendor, amount, account number, dept, campus may be added to the documents title.
     *
     * @return - Customized document title text dependent upon route level.
     */
    protected String getCustomDocumentTitle() {

        // set the workflow document title
        //String poNumber = getPurchaseOrderIdentifier().toString();
        String vendorName = StringUtils.trimToEmpty(getVendorName());
        String preqAmount = getGrandTotal().toString();

        String documentTitle = "";
        Set<String> nodeNames = this.getFinancialSystemDocumentHeader().getWorkflowDocument().getCurrentNodeNames();

        // if this doc is final or will be final
        if (nodeNames.size() == 0 || this.getFinancialSystemDocumentHeader().getWorkflowDocument().isFinal()) {
            // documentTitle = (new StringBuffer("PO: ")).append(poNumber).append(" Vendor: ").append(vendorName).append(" Amount: ").append(preqAmount).toString();
            documentTitle = (new StringBuffer()).append(" Vendor: ").append(vendorName).append(" Amount: ").append(preqAmount).toString();
        } else {
            PurApAccountingLine theAccount = getFirstAccount();
            String accountNumber = (theAccount != null ? StringUtils.trimToEmpty(theAccount.getAccountNumber()) : "n/a");
            String accountChart = (theAccount != null ? theAccount.getChartOfAccountsCode() : "");
            String payDate = (new SimpleDateFormat(RiceConstants.SIMPLE_DATE_FORMAT_FOR_DATE)).format(getInvoicePayDate());
            String indicator = getTitleIndicator();

            // set title to: PO# - VendorName - Chart/Account - total amt - Pay Date - Indicator (ie Hold, Request Cancel)
            documentTitle = (new StringBuffer()).append(" Vendor: ").append(vendorName).append(" Account: ").append(accountChart).append(" ").append(accountNumber).append(" Amount: ").append(preqAmount).append(" Pay Date: ").append(payDate).append(" ").append(indicator).toString();
        }
        return documentTitle;
    }

    /**
     * Returns the first payment item's first account (assuming the item list is sequentially ordered).
     *
     * @return - Accounting Line object for first account of first payment item.
     */
    public PurApAccountingLine getFirstAccount() {
        // loop through items, and pick the first item
        if ((getItems() != null) && (!getItems().isEmpty())) {
            InvoiceItem itemToUse = null;
            for (Iterator iter = getItems().iterator(); iter.hasNext(); ) {
                InvoiceItem item = (InvoiceItem) iter.next();
                if ((item.isConsideredEntered()) && ((item.getSourceAccountingLines() != null) && (!item.getSourceAccountingLines().isEmpty()))) {
                    // accounting lines are not empty so pick the first account
                    PurApAccountingLine accountLine = item.getSourceAccountingLine(0);
                    accountLine.refreshNonUpdateableReferences();
                    return accountLine;
                }
                /*
                if (((item.getExtendedPrice() != null) && item.getExtendedPrice().compareTo(BigDecimal.ZERO) > 0) && ((item.getAccounts() != null) && (!item.getAccounts().isEmpty()))) {
                    // accounting lines are not empty so pick the first account
               List accts = (List)item.getAccounts();
               InvoiceAccount accountLine = (InvoiceAccount)accts.get(0);
                    return accountLine.getFinancialChartOfAccountsCode() + "-" + accountLine.getAccountNumber();
                }
                */
            }
        }
        return null;
    }

    /**
     * Determines the indicator text that will appear in the workflow document title
     *
     * @return - Text of hold or request cancel
     */
    protected String getTitleIndicator() {
        if (isHoldIndicator()) {
            return PurapConstants.InvoiceIndicatorText.HOLD;
        } else if (isInvoiceCancelIndicator()) {
            return PurapConstants.InvoiceIndicatorText.REQUEST_CANCEL;
        }
        return "";
    }


    /**
     * @see org.kuali.rice.krad.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        LOG.debug("doRouteStatusChange() started");

        super.doRouteStatusChange(statusChangeEvent);
        try {
            // DOCUMENT PROCESSED
            if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isProcessed()) {
                if (!InvoiceStatuses.APPDOC_AUTO_APPROVED.equals(getApplicationDocumentStatus())) {
                    populateDocumentForRouting();
                    updateAndSaveAppDocStatus(InvoiceStatuses.APPDOC_DEPARTMENT_APPROVED);

                    //PurchaseOrderDocument purchaseOrderDocument = this.getPurchaseOrderDocument();
                    /*if (purchaseOrderDocument.getOrderType().getPurchaseOrderType().equals(OLEConstants.ORD_TYPE_FIRM_FIX) || purchaseOrderDocument.getOrderType().getPurchaseOrderType().equals(OLEConstants.APPROVAL) || purchaseOrderDocument.getOrderType().getPurchaseOrderType().equals(OLEConstants.FIRM_MUL_PART)) {
                        DocumentHeader invoiceDocumentHeader = this.getDocumentHeader();
                    	closePurchaseOrder();
                    	this.setDocumentHeader(invoiceDocumentHeader);
                    	} */

                }
            }

            // DOCUMENT DISAPPROVED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isDisapproved()) {
                String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(getDocumentHeader().getWorkflowDocument());
                String disapprovalStatus = InvoiceStatuses.getInvoiceAppDocDisapproveStatuses().get(nodeName);

                if (ObjectUtils.isNotNull(nodeName)) {
                    if (((StringUtils.isBlank(disapprovalStatus)) && ((InvoiceStatuses.APPDOC_INITIATE.equals(getApplicationDocumentStatus())) || (InvoiceStatuses.APPDOC_IN_PROCESS.equals(getApplicationDocumentStatus()))))) {
                        disapprovalStatus = InvoiceStatuses.APPDOC_CANCELLED_POST_AP_APPROVE;
                    }
                    if (StringUtils.isNotBlank(disapprovalStatus)) {
                        SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(this, nodeName);
                        updateAndSaveAppDocStatus(disapprovalStatus);
                    }
                } else {
                    logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + nodeName + "'");
                }
            }
            // DOCUMENT CANCELED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isCanceled()) {
                String currentNodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(this.getDocumentHeader().getWorkflowDocument());
                String cancelledStatus = InvoiceStatuses.getInvoiceAppDocDisapproveStatuses().get(currentNodeName);

                //**START AZ** KATTS-37 KevinMcO
                if (StringUtils.isBlank(cancelledStatus) &&
                        StringUtils.isBlank(InvoiceStatuses.getInvoiceAppDocDisapproveStatuses().get(currentNodeName)) &&
                        (InvoiceStatuses.APPDOC_INITIATE.equals(getStatusCode()) || InvoiceStatuses.APPDOC_IN_PROCESS.equals(getStatusCode()))) {
                    cancelledStatus = InvoiceStatuses.APPDOC_CANCELLED_POST_AP_APPROVE;
                }
                //**END AZ**

                if (ObjectUtils.isNotNull(cancelledStatus)) {
                    SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(this, currentNodeName);
                    updateAndSaveAppDocStatus(cancelledStatus);
                } else {
                    logAndThrowRuntimeException("No status found to set for document being canceled in node '" + currentNodeName + "'");
                }
            }
        } catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    /**
     * Generates correcting entries to the GL if accounts are modified.
     *
     * @see org.kuali.rice.krad.document.Document#doActionTaken(org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent)
     */
    @Override
    public void doActionTaken(ActionTakenEvent event) {
        super.doActionTaken(event);
        WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
        String currentNode = null;
        Object[] names = workflowDocument.getCurrentNodeNames().toArray();
        if (names.length > 0) {
            currentNode = (String) names[0];
        }

        // everything in the below list requires correcting entries to be written to the GL
        //   if (InvoiceStatuses.getNodesRequiringCorrectingGeneralLedgerEntries().contains(currentNode)) {
        //       SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesModifyInvoice(this);
        //    }
    }

    /**
     * @see org.kuali.ole.module.purap.document.AccountsPayableDocumentBase#processNodeChange(String, String)
     */
    @Override
    public boolean processNodeChange(String newNodeName, String oldNodeName) {
        if (InvoiceStatuses.APPDOC_AUTO_APPROVED.equals(getApplicationDocumentStatus())) {
            // do nothing for an auto approval
            return false;
        }
        if (InvoiceStatuses.NODE_ADHOC_REVIEW.equals(oldNodeName)) {
            SpringContext.getBean(AccountsPayableService.class).performLogicForFullEntryCompleted(this);
        }
        return true;
    }

    /**
     * @see org.kuali.ole.module.purap.document.AccountsPayableDocumentBase#saveDocumentFromPostProcessing()
     */
    @Override
    public void saveDocumentFromPostProcessing() {
        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);

        // if we've hit full entry completed then close/reopen po
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(this) && this.isClosePurchaseOrderIndicator()) {
            SpringContext.getBean(PurapService.class).performLogicForCloseReopenPO(this);
        }
    }

    /**
     * @see org.kuali.ole.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return InvoiceItem.class;
    }

    @Override
    public Class getItemUseTaxClass() {
        return InvoiceItemUseTax.class;
    }

    /**
     * @see org.kuali.ole.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public PurchaseOrderDocument getPurApSourceDocumentIfPossible() {
        return getPurchaseOrderDocument();
    }

    /**
     * @see org.kuali.ole.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(OLEConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER);
    }

    public String getPurchaseOrderNotes() {

        ArrayList poNotes = (ArrayList) this.getPurchaseOrderDocument().getNotes();

        if (poNotes.size() > 0) {
            return "Yes";
        }
        return "No";
    }

    public void setPurchaseOrderNotes(String purchaseOrderNotes) {
        this.purchaseOrderNotes = purchaseOrderNotes;
    }

    public String getRecurringPaymentTypeCode() {
        return recurringPaymentTypeCode;
    }

    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }

    /**
     * Returns the total encumbered amount from the purchase order excluding below the line.
     *
     * @return Total cost excluding below the line
     */
    public KualiDecimal getItemTotalPoEncumbranceAmount() {
        // get total from po excluding below the line and inactive
        return this.getPurchaseOrderDocument().getTotalDollarAmount(false, false);
    }

    public KualiDecimal getItemTotalPoEncumbranceAmountRelieved() {
        return getItemTotalPoEncumbranceAmountRelieved(false);
    }

    public KualiDecimal getItemTotalPoEncumbranceAmountRelieved(boolean includeBelowTheLine) {

        KualiDecimal total = KualiDecimal.ZERO;

        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getPurchaseOrderDocument().getItems()) {
            ItemType it = item.getItemType();
            if (includeBelowTheLine || it.isLineItemIndicator()) {
                total = total.add(item.getItemEncumbranceRelievedAmount());
            }
        }
        return total;
    }

    public KualiDecimal getLineItemTotal() {
        return this.getTotalDollarAmountAboveLineItems();
    }

    public KualiDecimal getLineItemPreTaxTotal() {
        return this.getTotalPreTaxDollarAmountAboveLineItems();
    }

    public KualiDecimal getLineItemTaxAmount() {
        return this.getTotalTaxAmountAboveLineItems();
    }

    @Override
    public KualiDecimal getGrandTotal() {
        return this.getTotalDollarAmount();
    }

    public KualiDecimal getGrandTotalExcludingDiscount() {
        String[] discountCode = new String[]{PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE};
        return this.getTotalDollarAmountWithExclusions(discountCode, true);
    }

    /**
     * This method is here due to a setter requirement by the htmlControlAttribute
     *
     * @param amount - Grand total for document, excluding discount
     */
    public void setGrandTotalExcludingDiscount(KualiDecimal amount) {
        // do nothing
    }

    public KualiDecimal getGrandPreTaxTotal() {
        return this.getTotalPreTaxDollarAmount();
    }

    public KualiDecimal getGrandPreTaxTotalExcludingDiscount() {
        String[] discountCode = new String[]{PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE};
        return this.getTotalPreTaxDollarAmountWithExclusions(discountCode, true);
    }

    public KualiDecimal getGrandTaxAmount() {
        return this.getTotalTaxAmount();
    }

    public KualiDecimal getGrandTaxAmountExcludingDiscount() {
        String[] discountCode = new String[]{PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE};
        return this.getTotalTaxAmountWithExclusions(discountCode, true);
    }

    public boolean isDiscount() {
        return SpringContext.getBean(InvoiceService.class).hasDiscountItem(this);
    }

    /**
     * The total that was paid on the po excluding below the line
     *
     * @return total paid
     */
    public KualiDecimal getItemTotalPoPaidAmount() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getPurchaseOrderDocument().getItems()) {
            ItemType iT = item.getItemType();
            if (iT.isLineItemIndicator()) {
                KualiDecimal itemPaid = item.getItemPaidAmount();
                total = total.add(itemPaid);
            }
        }
        return total;
    }

    /**
     * Returns the name of who requested cancel.
     *
     * @return - name of who requested cancel.
     */
    public String getAccountsPayableRequestCancelPersonName() {
        String personName = null;
        Person user = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPerson(getAccountsPayableRequestCancelIdentifier());
        if (user != null) {
            personName = user.getName();
        } else {
            personName = "";
        }

        return personName;
    }

    /**
     * Exists due to a setter requirement by the htmlControlAttribute
     *
     * @param amount - total po amount paid
     */
    public void setItemTotalPoPaidAmount(KualiDecimal amount) {
        // do nothing
    }

    /**
     * Exists due to a setter requirement by the htmlControlAttribute
     *
     * @param amount - total po encumbrance
     */
    public void setItemTotalPoEncumbranceAmount(KualiDecimal amount) {
        // do nothing
    }

    /**
     * Exists due to a setter requirement by the htmlControlAttribute
     *
     * @param amount - total po encumbrance amount relieved
     */
    public void setItemTotalPoEncumbranceAmountRelieved(KualiDecimal amount) {
        // do nothing
    }

    /**
     * Determinines the route levels for a given document.
     *
     * @param workflowDocument - work flow document
     * @return List - list of route levels
     */
    protected List<String> getCurrentRouteLevels(WorkflowDocument workflowDocument) {
        Set<String> names = workflowDocument.getCurrentNodeNames();
        return new ArrayList<String>(names);
    }

    public RecurringPaymentType getRecurringPaymentType() {
        /*if (ObjectUtils.isNull(recurringPaymentType)) {
            refreshReferenceObject(PurapPropertyConstants.RECURRING_PAYMENT_TYPE);
        }*/
        return recurringPaymentType;
    }

    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }

    public PurchaseOrderCostSource getInvoiceCostSource() {
        return invoiceCostSource;
    }

    public void setInvoiceCostSource(PurchaseOrderCostSource invoiceCostSource) {
        this.invoiceCostSource = invoiceCostSource;
    }

    /**
     * @see AccountsPayableDocumentBase#getPoDocumentTypeForAccountsPayableDocumentCancel()
     */
    @Override
    public String getPoDocumentTypeForAccountsPayableDocumentCancel() {
        return PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT;
    }

    /**
     * @see AccountsPayableDocumentBase#getInitialAmount()
     */
    @Override
    public KualiDecimal getInitialAmount() {
        return this.getVendorInvoiceAmount();
    }

    /**
     * Populates the payment request document, then continues with preparing for save.
     *
     * @see org.kuali.rice.krad.document.Document#prepareForSave(org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        WorkflowDocument workflowDocument = this.getDocumentHeader().getWorkflowDocument();
        String workflowDocumentTitle = this.buildDocumentTitle(workflowDocument.getTitle());

        this.getFinancialSystemDocumentHeader().getWorkflowDocument().setTitle(workflowDocumentTitle);

        // first populate, then call super
        /*if (event instanceof AttributedContinuePurapEvent) {
            SpringContext.getBean(InvoiceService.class).populateInvoice(this);
        }*/
        super.prepareForSave(event);

    }

    /**
     * @see AccountsPayableDocumentBase#isAttachmentRequired()
     */
    @Override
    protected boolean isAttachmentRequired() {
        if (getInvoiceElectronicInvoiceIndicator()) {
            return false;
        }
        return StringUtils.equalsIgnoreCase("Y", SpringContext.getBean(OleInvoiceService.class).getParameter(OLEConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE, OLEConstants.InvoiceDocument.CMPNT_CD,PurapParameterConstants.PURAP_PRQS_REQUIRE_ATTACHMENT));
    }

    /**
     * @see AccountsPayableDocument#getDocumentSpecificService()
     */
    @Override
    public AccountsPayableDocumentSpecificService getDocumentSpecificService() {
        return SpringContext.getBean(InvoiceService.class);
    }

    /**
     * @see PurchasingAccountsPayableDocumentBase#getItem(int)
     */
    @Override
    public PurApItem getItem(int pos) {
        InvoiceItem item = (InvoiceItem) super.getItem(pos);
        if (item.getInvoice() == null) {
            item.setInvoice(this);
        }
        return item;
    }

    public String getPrimaryVendorName() {

        if (primaryVendorName == null) {
            VendorDetail vd = SpringContext.getBean(VendorService.class).getVendorDetail(this.getOriginalVendorHeaderGeneratedIdentifier(), this.getOriginalVendorDetailAssignedIdentifier());

            if (vd != null) {
                primaryVendorName = vd.getVendorName();
            }
        }

        return primaryVendorName;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setPrimaryVendorName(String primaryVendorName) {
    }

    /**
     * Forces general ledger entries to be approved, does not wait for payment request document final approval.
     *
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.ole.sys.document.AccountingDocument,
     *      org.kuali.ole.sys.businessobject.AccountingLine, org.kuali.ole.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);

        SpringContext.getBean(PurapGeneralLedgerService.class).customizeGeneralLedgerPendingEntry(this, (AccountingLine) postable, explicitEntry, getPurchaseOrderIdentifier(), getDebitCreditCodeForGLEntries(), PurapDocTypeCodes.INVOICE_DOCUMENT, isGenerateEncumbranceEntries());

        // PRQSs do not wait for document final approval to post GL entries; here we are forcing them to be APPROVED
        explicitEntry.setFinancialDocumentApprovedCode(OLEConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
    }

    /**
     * Provides answers to the following splits: PurchaseWasReceived VendorIsEmployeeOrNonResidentAlien
     *
     * @see org.kuali.ole.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(PurapWorkflowConstants.REQUIRES_IMAGE_ATTACHMENT)) {
            return requiresAccountsPayableReviewRouting();
        }
        if (nodeName.equals(PurapWorkflowConstants.PURCHASE_WAS_RECEIVED)) {
            return shouldWaitForReceiving();
        }
        if (nodeName.equals(PurapWorkflowConstants.VENDOR_IS_EMPLOYEE_OR_NON_RESIDENT_ALIEN)) {
            return isVendorEmployeeOrNonResidentAlien();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    protected boolean isVendorEmployeeOrNonResidentAlien() {
        String vendorHeaderGeneratedId = this.getVendorHeaderGeneratedIdentifier().toString();
        if (StringUtils.isBlank(vendorHeaderGeneratedId)) {
            // no vendor header id so can't check for proper tax routing
            return false;
        }
        VendorService vendorService = SpringContext.getBean(VendorService.class);
        boolean routeDocumentAsEmployeeVendor = vendorService.isVendorInstitutionEmployee(Integer.valueOf(vendorHeaderGeneratedId));
        boolean routeDocumentAsForeignVendor = vendorService.isVendorForeign(Integer.valueOf(vendorHeaderGeneratedId));
        if ((!routeDocumentAsEmployeeVendor) && (!routeDocumentAsForeignVendor)) {
            // no need to route
            return false;
        }

        return true;
    }

    /**
     * Payment Request needs to wait for receiving if the receiving requirements have NOT been met.
     *
     * @return
     */
    protected boolean shouldWaitForReceiving() {
        // only require if PO was marked to require receiving
        if (isReceivingDocumentRequiredIndicator()) {
            return !isReceivingRequirementMet();
        }

        //receiving is not required or has already been fulfilled, no need to stop for routing
        return false;
    }

    /**
     * Determine if the receiving requirement has been met for all items on the payment request. If any item does not have receiving
     * requirements met, return false. Receiving requirement has NOT been met if the quantity invoiced on the Payment Request is
     * greater than the quantity of "unpaid and received" items determined by (poQtyReceived - (poQtyInvoiced)).
     *
     * @return boolean return true if the receiving requirement has been met for all items on the payment request; false if
     *         requirement has not been met
     */
    public boolean isReceivingRequirementMet() {

        for (Iterator iter = getItems().iterator(); iter.hasNext(); ) {
            InvoiceItem prqsItem = (InvoiceItem) iter.next();

            if (prqsItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                PurchaseOrderItem poItem = prqsItem.getPurchaseOrderItem();
                KualiDecimal prqsQuantityInvoiced = prqsItem.getItemQuantity() == null ? KualiDecimal.ZERO : prqsItem.getItemQuantity();
                KualiDecimal poQuantityReceived = poItem.getItemReceivedTotalQuantity() == null ? KualiDecimal.ZERO : poItem.getItemReceivedTotalQuantity();
                KualiDecimal poQuantityInvoiced = poItem.getItemInvoicedTotalQuantity() == null ? KualiDecimal.ZERO : poItem.getItemInvoicedTotalQuantity();

                // receiving has NOT been met if prqsQtyInvoiced is greater than (poQtyReceived & (poQtyInvoiced & prqsQtyInvoiced))
                if (prqsQuantityInvoiced.compareTo(poQuantityReceived.subtract(poQuantityInvoiced)) > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Date getTransactionTaxDate() {
        return getInvoiceDate();
    }

    public String getTaxClassificationCode() {
        return taxClassificationCode;
    }

    public void setTaxClassificationCode(String taxClassificationCode) {
        this.taxClassificationCode = taxClassificationCode;
    }

    public KualiDecimal getTaxFederalPercentShort() {
        return new KualiDecimal(taxFederalPercent);
    }

    public BigDecimal getTaxFederalPercent() {
        return taxFederalPercent;
    }

    public void setTaxFederalPercent(BigDecimal taxFederalPercent) {
        this.taxFederalPercent = taxFederalPercent;
    }

    public KualiDecimal getTaxStatePercentShort() {
        return new KualiDecimal(taxStatePercent);
    }

    public BigDecimal getTaxStatePercent() {
        return taxStatePercent;
    }

    public void setTaxStatePercent(BigDecimal taxStatePercent) {
        this.taxStatePercent = taxStatePercent;
    }

    public String getTaxCountryCode() {
        return taxCountryCode;
    }

    public void setTaxCountryCode(String taxCountryCode) {
        this.taxCountryCode = taxCountryCode;
    }

    public Boolean getTaxGrossUpIndicator() {
        return taxGrossUpIndicator;
    }

    public void setTaxGrossUpIndicator(Boolean taxGrossUpIndicator) {
        this.taxGrossUpIndicator = taxGrossUpIndicator;
    }

    public Boolean getTaxExemptTreatyIndicator() {
        return taxExemptTreatyIndicator;
    }

    public void setTaxExemptTreatyIndicator(Boolean taxExemptTreatyIndicator) {
        this.taxExemptTreatyIndicator = taxExemptTreatyIndicator;
    }

    public Boolean getTaxForeignSourceIndicator() {
        return taxForeignSourceIndicator;
    }

    public void setTaxForeignSourceIndicator(Boolean taxForeignSourceIndicator) {
        this.taxForeignSourceIndicator = taxForeignSourceIndicator;
    }

    public KualiDecimal getTaxSpecialW4Amount() {
        return taxSpecialW4Amount;
    }

    public void setTaxSpecialW4Amount(KualiDecimal taxSpecialW4Amount) {
        this.taxSpecialW4Amount = taxSpecialW4Amount;
    }

    public Boolean getTaxUSAIDPerDiemIndicator() {
        return taxUSAIDPerDiemIndicator;
    }

    public void setTaxUSAIDPerDiemIndicator(Boolean taxUSAIDPerDiemIndicator) {
        this.taxUSAIDPerDiemIndicator = taxUSAIDPerDiemIndicator;
    }

    public Boolean getTaxOtherExemptIndicator() {
        return taxOtherExemptIndicator;
    }

    public void setTaxOtherExemptIndicator(Boolean taxOtherExemptIndicator) {
        this.taxOtherExemptIndicator = taxOtherExemptIndicator;
    }

    public String getTaxNQIId() {
        return taxNQIId;
    }

    public void setTaxNQIId(String taxNQIId) {
        this.taxNQIId = taxNQIId;
    }

    public boolean isInvoiceCancelIndicatorForSearching() {
        return invoiceCancelIndicator;
    }

    /**
     * @return the payment request positive approval indicator
     */
    public boolean getInvoicePositiveApprovalIndicatorForSearching() {
        return invoicePositiveApprovalIndicator;
    }

    /**
     * @return the receiving document required indicator
     */
    public boolean getReceivingDocumentRequiredIndicatorForSearching() {
        return receivingDocumentRequiredIndicator;
    }


    public String getRequestCancelIndicatorForResult() {
        return isInvoiceCancelIndicator() ? "Yes" : "No";
    }

    public String getPaidIndicatorForResult() {
        return getPaymentPaidTimestamp() != null ? "Yes" : "No";
    }

    public Date getAccountsPayableApprovalDateForSearching() {
        if (this.getAccountsPayableApprovalTimestamp() == null) {
            return null;
        }
        try {
            Date date = SpringContext.getBean(DateTimeService.class).convertToSqlDate(this.getAccountsPayableApprovalTimestamp());
            if (LOG.isDebugEnabled()) {
                LOG.debug("getAccountsPayableApprovalDateForSearching() returns " + date);
            }
            return date;
        } catch (Exception e) {
            return new Date(this.getAccountsPayableApprovalTimestamp().getTime());
        }
    }

    /**
     * Checks all documents notes for attachments.
     *
     * @return - true if document does not have an image attached, false otherwise
     */
    @Override
    public boolean documentHasNoImagesAttached() {
        List boNotes = this.getNotes();
        if (ObjectUtils.isNotNull(boNotes)) {
            for (Object obj : boNotes) {
                Note note = (Note) obj;

                note.refreshReferenceObject("attachment");
                if (ObjectUtils.isNotNull(note.getAttachment()) && PurapConstants.AttachmentTypeCodes.ATTACHMENT_TYPE_INVOICE_IMAGE.equals(note.getAttachment().getAttachmentTypeCode())) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void closePurchaseOrder() {
        //Added for jira OLE-3529 Starts
        Integer poIdentifier = purchaseOrderDocument.getPurapDocumentIdentifier();
        String docNumber = purchaseOrderDocument.getDocumentNumber();
        Map purchaseOrderMap = new HashMap();
        purchaseOrderMap.put("documentNumber", docNumber);
        purchaseOrderMap.put("itemTypeCode", OLEConstants.ITEM);
        KualiDecimal itemQuantity = new KualiDecimal(0);
        KualiDecimal itemOrderQuantity = new KualiDecimal(0);
        List<OlePurchaseOrderItem> olePurchaseOrderItemList = (List<OlePurchaseOrderItem>) getBusinessObjectService().findMatching(OlePurchaseOrderItem.class, purchaseOrderMap);
        for (OlePurchaseOrderItem olePurchaseOrderItem : olePurchaseOrderItemList) {
            itemQuantity = itemQuantity.add(olePurchaseOrderItem.getItemQuantity());
        }
        Map lineItemReceivingMap = new HashMap();
        lineItemReceivingMap.put("purchaseOrderIdentifier", poIdentifier);
        List<OleLineItemReceivingDocument> oleLineItemReceivingDocumentList = (List<OleLineItemReceivingDocument>) getBusinessObjectService().findMatching(OleLineItemReceivingDocument.class, lineItemReceivingMap);
        for (OleLineItemReceivingDocument oleLineItemReceivingDocument : oleLineItemReceivingDocumentList) {
            String docId = oleLineItemReceivingDocument.getDocumentNumber();
            Map docIdMap = new HashMap();
            docIdMap.put("documentNumber", docId);
            List<OleLineItemReceivingItem> oleLineItemReceivingItemList = (List<OleLineItemReceivingItem>) getBusinessObjectService().findMatching(OleLineItemReceivingItem.class, docIdMap);
            for (OleLineItemReceivingItem oleLineItemReceivingItem : oleLineItemReceivingItemList) {
                itemOrderQuantity = itemOrderQuantity.add(oleLineItemReceivingItem.getItemOrderedQuantity());
            }
            if (itemQuantity.equals(itemOrderQuantity)) {
                try {
                    this.setDocumentHeader(purchaseOrderDocument.getDocumentHeader());
                    updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED);
                } catch (WorkflowException e) {
                    logAndThrowRuntimeException("Error while Closing the PO from Payment Request "
                            + getDocumentNumber(), e);
                }
            }
        }
        //End
    }

    /**
     * @see AccountsPayableDocument#getPurchaseOrderDocument()
     */
    @Override
    public PurchaseOrderDocument getPurchaseOrderDocument() {
        if ((ObjectUtils.isNull(purchaseOrderDocument) || ObjectUtils.isNull(purchaseOrderDocument.getPurapDocumentIdentifier())) && (ObjectUtils.isNotNull(getPurchaseOrderIdentifier()))) {
            Map map = new HashMap();
            map.put("purapDocumentIdentifier",this.getPurchaseOrderIdentifier());
            List<OlePurchaseOrderDocument> purchaseOrderDocumentList = (List<OlePurchaseOrderDocument>)getBusinessObjectService().findMatching(OlePurchaseOrderDocument.class,map);
            if(purchaseOrderDocumentList!=null && purchaseOrderDocumentList.size()>0){
                setPurchaseOrderDocument(purchaseOrderDocumentList.get(0));
            }
        }
        return purchaseOrderDocument;
    }

    /**
     * @see AccountsPayableDocument#getPurchaseOrderDocument()
     */

    public PurchaseOrderDocument getPurchaseOrderDocument(Integer poID) {
        if (ObjectUtils.isNull(purchaseOrderDocument) || ObjectUtils.isNull(purchaseOrderDocument.getPurapDocumentIdentifier())) {
            // && (ObjectUtils.isNotNull(getPurchaseOrderIdentifier()))) {
            Map map = new HashMap();
            map.put("purapDocumentIdentifier",poID);
            List<OlePurchaseOrderDocument> purchaseOrderDocumentList = (List<OlePurchaseOrderDocument>)getBusinessObjectService().findMatching(OlePurchaseOrderDocument.class,map);
            if(purchaseOrderDocumentList!=null && purchaseOrderDocumentList.size()>0){
                setPurchaseOrderDocument(purchaseOrderDocumentList.get(0));
            }
          //  setPurchaseOrderDocument(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(poID));
        }
        return purchaseOrderDocument;
    }


   /* private InvoiceService getInvoiceService() {
        if (invoiceService == null ) {
            invoiceService = SpringContext.getBean(OleInvoiceService.class);
        }
        return invoiceService;
    }
*/

}
