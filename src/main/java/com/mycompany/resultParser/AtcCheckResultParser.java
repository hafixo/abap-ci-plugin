/*
 * The MIT License
 *
 * Copyright 2020 Andreas Gautsch.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mycompany.resultParser;

import com.iwombat.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author Andreas Gautsch
 */
public class AtcCheckResultParser {

    public int parseXmlForFailedElements(String xml) throws IOException {
        int numFailedAtcChecks = 0;

        JSONObject jsonObject = XML.toJSONObject(xml);

        JSONObject atcWorklistJsonObject = jsonObject.optJSONObject("atcworklist:worklistRun");

        if (atcWorklistJsonObject != null && !atcWorklistJsonObject.isEmpty()) {
            {
                JSONObject atcWorklistInfoJsonObject = atcWorklistJsonObject.optJSONObject("atcworklist:infos");
                if (atcWorklistInfoJsonObject != null && !atcWorklistInfoJsonObject.isEmpty()) {
                    JSONObject atcInfoJsonObject = atcWorklistInfoJsonObject.optJSONObject("atcinfo:info");
                    if (atcInfoJsonObject != null && !atcInfoJsonObject.isEmpty()) {
                        numFailedAtcChecks = ExtractFailedAtcChecks(atcInfoJsonObject);
                    } else {
                        JSONArray atcInfoJsonArray = atcWorklistInfoJsonObject.optJSONArray("atcinfo:info");
                        if (atcInfoJsonArray != null) {
                            for (int numElement = 0; numElement < atcInfoJsonArray.length(); numElement++) {
                                JSONObject atcInfoDescriptionJsonObject = atcInfoJsonArray.getJSONObject(numElement);
                                numFailedAtcChecks = ExtractFailedAtcChecks(atcInfoDescriptionJsonObject);
                            }
                        }
                    }
                }
            }
        }

        return numFailedAtcChecks;
    }

    private int ExtractFailedAtcChecks(JSONObject atcInfoDescriptionJsonObject) throws NumberFormatException, JSONException {
        int numFailedAtcChecks = 0;
        String description = atcInfoDescriptionJsonObject.getString("atcinfo:description");
        String[] descriptionInfo = description.split(",");
        if (descriptionInfo.length == 3) {
            int critical = Integer.parseInt(descriptionInfo[0]);
            int warning = Integer.parseInt(descriptionInfo[1]);
            numFailedAtcChecks = critical + warning;
        }
        return numFailedAtcChecks;
    }

}
