package org.kuali.ole.oleng.resolvers.invoiceimport;

import org.kuali.ole.constants.OleNGConstants;
import org.kuali.ole.pojo.OleInvoiceRecord;

/**
 * Created by SheikS on 1/27/2016.
 */
public class InvoiceNumberValueResolver extends InvoiceRecordResolver {

    @Override
    public boolean isInterested(String attributeName) {
        return OleNGConstants.BatchProcess.INVOICE_NUMBER.equalsIgnoreCase(attributeName);
    }

    @Override
    public void setAttributeValue(OleInvoiceRecord oleInvoiceRecord, String attributeValue) {
        oleInvoiceRecord.setInvoiceNumber(attributeValue);
    }
}
