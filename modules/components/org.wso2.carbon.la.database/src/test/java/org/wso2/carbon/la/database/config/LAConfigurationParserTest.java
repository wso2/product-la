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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.la.database.config;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.la.database.internal.LAConfigurationParser;
import org.wso2.carbon.la.commons.domain.config.LAConfiguration;
import org.wso2.carbon.la.database.exceptions.LAConfigurationParserException;

public class LAConfigurationParserTest {

    @Test
    public void testMLConfigParser() throws LAConfigurationParserException {
        LAConfigurationParser configParser = new LAConfigurationParser();
        LAConfiguration mlConfig = configParser.getLAConfiguration("src/test/resources/log-analyzer.xml");
        Assert.assertNotNull(mlConfig);
        Assert.assertEquals(mlConfig.getDatasourceName(), "jdbc/WSO2LA_DB");
    }

    @Test
    public void testMLConfigParserOnError() throws LAConfigurationParserException {
        try {

            LAConfigurationParser parser = new LAConfigurationParser();
            LAConfiguration config = parser.getLAConfiguration("src/test/resources/log-analyzer.xml");
        } catch (Exception e) {
            Assert.assertEquals(true, e instanceof LAConfigurationParserException);
        }
    }
}
