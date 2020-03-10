package com.kd.One.Service.Protocol.ControlProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by lwg on 2016-07-25.
 */
public class KDGas {
    public static String GasStateRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11071301</FunctionID>")
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

    public static String GasEachRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11071302</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"7\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">").append(tKDData.GroupID).append("</Data>")
                .append("<Data name=\"SubID\">").append(tKDData.SubID).append("</Data>")
                .append("<Data name=\"StopBuzzer\">").append("Stop").append("</Data>")
                .append("<Data name=\"CloseValve\">").append("Close").append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String GasGroupRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11071303</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"6\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">").append(tKDData.GroupID).append("</Data>")
                .append("<Data name=\"StopBuzzer\">").append("Stop").append("</Data>")
                .append("<Data name=\"CloseValve\">").append("Close").append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
