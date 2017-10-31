/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.la.alert.domain.config;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement(name = "to")
@XmlAccessorType(XmlAccessType.FIELD)
public class To {
    @XmlElement()
    ArrayList<Property> property = new ArrayList<Property>();
    @XmlAttribute()
    private String eventAdapterType;

    public String getEventAdapterType() {
        return eventAdapterType;
    }

    public void setEventAdapterType(String eventAdapterType) {
        this.eventAdapterType = eventAdapterType;
    }

    public ArrayList<Property> getProperty() {
        return property;
    }

    public void setProperty(ArrayList<Property> property) {
        this.property = property;
    }

}
