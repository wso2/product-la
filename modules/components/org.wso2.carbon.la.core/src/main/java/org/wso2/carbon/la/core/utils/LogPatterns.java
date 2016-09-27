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

package org.wso2.carbon.la.core.utils;

import org.wso2.carbon.utils.CarbonUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogPatterns {

    private static final String LOG_PATTERN = CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator + "conf" + File.separator + "patterns";

    public LogPatterns() {
    }

    public static String[] getPatternTypes() {
        File patternFolder = new File(LOG_PATTERN);
        File[] listOfFiles = patternFolder.listFiles();
        if (listOfFiles != null) {
            String[] patternTypes = new String[listOfFiles.length];
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    patternTypes[i] = listOfFiles[i].getName();
                }
            }
            return patternTypes;
        } else {
            return null;
        }
    }

    public static String readLogPatterns(String fileName) throws IOException {
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();

            }
            return "";
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static String getDefaultPattern(String fileName) throws IOException {
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();

            }
            return "";
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}

