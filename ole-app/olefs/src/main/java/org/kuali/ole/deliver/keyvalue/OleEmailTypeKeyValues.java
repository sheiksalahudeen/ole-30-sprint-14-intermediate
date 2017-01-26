package org.kuali.ole.deliver.keyvalue;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * OleEmailTypeKeyValues returns code and name for EntityEmailTypeBo.
 */
public class OleEmailTypeKeyValues extends KeyValuesBase {


    /**
     * This method will populate the code as a key and name as a value and return it as list
     *
     * @return keyValues(list)
     */
    @Override
    public List getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        Collection<EntityEmailTypeBo> agendaBo = KRADServiceLocator.getBusinessObjectService().findAll(EntityEmailTypeBo.class);
        //   keyValues.add(new ConcreteKeyValue("", ""));
        for (EntityEmailTypeBo typ : agendaBo) {
            keyValues.add(new ConcreteKeyValue(typ.getCode(), typ.getName()));
        }
        return keyValues;
    }

}
