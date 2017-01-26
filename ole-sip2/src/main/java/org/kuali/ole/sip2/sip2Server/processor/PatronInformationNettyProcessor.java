package org.kuali.ole.sip2.sip2Server.processor;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.kuali.ole.bo.OLELookupUser;
import org.kuali.ole.converter.OLELookupUserConverter;
import org.kuali.ole.request.OLESIP2PatronInformationRequestParser;
import org.kuali.ole.response.OLESIP2PatronInformationResponse;
import org.kuali.ole.sip2.response.OLESIP2PatronInformationTurnedOffResponse;

import java.util.Properties;

/**
 * Created by pvsubrah on 9/28/15.
 */
public class PatronInformationNettyProcessor extends NettyProcessor {

    private final Properties properties;
    private String serverURL;

    public PatronInformationNettyProcessor(Properties properties, String serverURL) {
        this.properties = properties;
        this.serverURL = serverURL;
    }

    @Override
    public boolean isInterested(String code) {
        return code.equals("63");
    }

    @Override
    public String process(String requestData) {
        String response = "";
        OLESIP2PatronInformationRequestParser sip2PatronInformationRequestParser = new OLESIP2PatronInformationRequestParser(requestData);
        requestData = createJSONForLookupUser(sip2PatronInformationRequestParser.getPatronIdentifier(), "SIP2_OPERATOR_ID");

        response = postRequest(requestData, "/lookupUserSIP2", serverURL);

        if (StringUtils.isNotBlank(response)) {
            OLELookupUser oleLookupUser = new OLELookupUserConverter().getLookupUser(response);
            OLESIP2PatronInformationResponse sip2PatronInformationResponse = new OLESIP2PatronInformationResponse();

            response = sip2PatronInformationResponse.getSIP2PatronInfoResponse(oleLookupUser,
                    sip2PatronInformationRequestParser, properties.getProperty("sip2.institution"), calculateTotalFineBalance(oleLookupUser));
        }
        return response;
    }

    @Override
    public boolean isServiceTurnedOn() {
        return properties.getProperty("sip2.service.patronInformation").equalsIgnoreCase("yes");
    }

    @Override
    public String getResponseForServiceTurnedOff(String requestData) {
        OLESIP2PatronInformationTurnedOffResponse patronInformationTurnedOffResponse = new OLESIP2PatronInformationTurnedOffResponse();
        return patronInformationTurnedOffResponse.getOLESIP2PatronInformationTurnedOffResponse(requestData);
    }

    private String createJSONForLookupUser(String patronBarcode, String operatorId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("patronBarcode", patronBarcode);
            jsonObject.put("operatorId", operatorId);
            jsonObject.put("requestFormatType", "JSON");
            jsonObject.put("responseFormatType", "XML");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public Properties getProperties() {
        return properties;
    }
}
