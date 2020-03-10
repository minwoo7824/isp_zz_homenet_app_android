package com.kd.One.Service.Protocol.InfoProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by lwg on 2016-09-08.
 */
public class KDEms {
    public static String KDEms(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>1F030105</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"5\">")
                .append("<Data name=\"HomeID\">").append(tKDData.HomeID).append("</Data>")
                .append("<Data name=\"StartTime\">").append(tKDData.EnergyStartTime).append("</Data>")
                .append("<Data name=\"EndTime\">").append(tKDData.EnergyEndTime).append("</Data>")
                .append("<Data name=\"CategoryType\">").append(tKDData.EnergyCategoryType).append("</Data>")
                .append("<Data name=\"QueryType\">").append(tKDData.EnergyQueryType).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
