package com.kd.One.Service.Protocol.ControlProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by HN_USER on 2016-07-27.
 */
public class KDStandbypower {
    public static String StandbypowerStateRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11073801</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"4\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">All</Data>")
                .append("<Data name=\"SubID\">All</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String StandbypowerEachRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11073802</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"8\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">").append(tKDData.GroupID).append("</Data>")
                .append("<Data name=\"SubID\">").append(tKDData.SubID).append("</Data>")
                .append("<Data name=\"AutoBlockSetting\">").append(tKDData.AutoBlockSetup).append("</Data>")
                .append("<Data name=\"AutoBlock\">").append(tKDData.AutoBlock).append("</Data>")
                .append("<Data name=\"StdbyPowerStatus\">").append(tKDData.PowerState).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String StandbypowerGroupRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11073803</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"7\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">").append(tKDData.GroupID).append("</Data>")
                .append("<Data name=\"AutoBlockSetting\">").append(tKDData.AutoBlockSetup).append("</Data>")
                .append("<Data name=\"AutoBlock\">").append(tKDData.AutoBlock).append("</Data>")
                .append("<Data name=\"StdbyPowerStatus\">").append(tKDData.PowerState).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
