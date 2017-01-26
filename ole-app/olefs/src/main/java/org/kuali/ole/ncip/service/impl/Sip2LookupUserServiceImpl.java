package org.kuali.ole.ncip.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.asr.handler.LookupUserResponseHandler;
import org.kuali.ole.OLEConstants;
import org.kuali.ole.deliver.controller.checkout.CircUtilController;
import org.kuali.ole.deliver.service.ParameterValueResolver;
import org.kuali.ole.deliver.util.DroolsResponse;
import org.kuali.ole.bo.OLEItemFines;
import org.kuali.ole.bo.OLEUserPrivilege;
import org.kuali.ole.utility.OleStopWatch;
import org.kuali.rice.core.api.config.property.ConfigContext;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by chenchulakshmig on 9/22/15.
 */
public class Sip2LookupUserServiceImpl extends LookupUserServiceImpl {

    @Override
    public String prepareResponse() {

        switch (responseFormatType) {
            case ("XML"):
                response = ((LookupUserResponseHandler) getResponseHandler()).marshalObjectToSIP2Xml(getOleLookupUser());
                break;
            case ("JSON"):
                response = getResponseHandler().marshalObjectToJson(getOleLookupUser());
                break;
        }

        return response;
    }

    @Override
    public String getOperatorId(String operatorId) {
        return ParameterValueResolver.getInstance().getParameter(OLEConstants
                .APPL_ID_OLE, OLEConstants.DLVR_NMSPC, OLEConstants.DLVR_CMPNT, operatorId);
    }

    @Override
    public void validatePatron() {
        OleStopWatch oleStopWatch = new OleStopWatch();
        oleStopWatch.start();
        ArrayList<Object> facts = new ArrayList<>();
        facts.add(getOlePatronDocument());
        DroolsResponse droolsResponse = new DroolsResponse();
        facts.add(droolsResponse);
        new CircUtilController().fireRules(facts, null, "lookup-user-sip2");

        if (StringUtils.isBlank(droolsResponse.getErrorMessage().getErrorMessage())) {
            getOleLookupUser().setValidPatron(true);
        } else {
            getOleLookupUser().setValidPatron(false);
        }
    }

    @Override
    public boolean isRenewalInfoNeeded() {
        return false;
    }

    @Override
    public OLEUserPrivilege getStatusPrivilege() {
        return null;
    }

    @Override
    protected void processErrorResponseForOperator() {
        getOleLookupUser().setCode("001");
        getOleLookupUser().setMessage(ConfigContext.getCurrentContextConfig().getProperty(OLEConstants.CIRCULATION_DESK_NOT_MAPPED_OPERATOR));
    }

    @Override
    protected void processErrorResponseForPatron() {
        getOleLookupUser().setCode("002");
        getOleLookupUser().setMessage(ConfigContext.getCurrentContextConfig().getProperty(OLEConstants.NO_PATRON_INFO));
    }

    @Override
    protected void processSuccessResponseForLookupUser() {
        getOleLookupUser().setCode("000");
        getOleLookupUser().setMessage(ConfigContext.getCurrentContextConfig().getProperty(OLEConstants.RTRVD_SUCCESS));
    }

    @Override
    protected void processSuccessResponseForItemFine(OLEItemFines oleItemFines) {
        oleItemFines.setCode("000");
        oleItemFines.setMessage(ConfigContext.getCurrentContextConfig().getProperty(OLEConstants.RTRVD_SUCCESS));
    }

    @Override
    protected void processInfoForItemFine(OLEItemFines oleItemFines) {
        oleItemFines.setCode("005");
        oleItemFines.setMessage(ConfigContext.getCurrentContextConfig().getProperty(OLEConstants.NO_FINE));
    }

    @Override
    protected boolean nameInformationDesired() {
        return true;
    }

    @Override
    protected boolean userAddressInformationDesired() {
        return true;
    }

    @Override
    protected boolean userPrivilegeDesired() {
        return false;
    }

    @Override
    protected boolean loanedItemsDesired() {
        return true;
    }

    @Override
    protected boolean requestedItemsDesired() {
        return true;
    }

    @Override
    protected boolean userFiscalAccountDesired() {
        return true;
    }

    @Override
    protected void preProcess(Map lookupUserParameters) {

    }
}
