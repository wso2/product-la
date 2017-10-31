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

@XmlRootElement(name = "mapping")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mapping {
    @XmlAttribute()
    private String customMapping;
    @XmlAttribute()
    private String type;
    @XmlElement
    private String inline;

    public String getCustomMapping() {
        return customMapping;
    }

    public void setCustomMapping(String customMapping) {
        this.customMapping = customMapping;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInline() {
        return inline;
    }

    public void setInline(String inline) {
        this.inline = inline;
    }
}
