package com.kd.One.Service.Protocol.ControlProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by lwg on 2016-10-11.
 */

public class KDBchBreaker {
    public static String BreakerRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11071801</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"4\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">").append("All").append("</Data>")
                .append("<Data name=\"SubID\">").append("All").append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String BreakerEach(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11071802</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"6\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">").append(tKDData.GroupID).append("</Data>")
                .append("<Data name=\"SubID\">").append(tKDData.SubID).append("</Data>");

        if(tKDData.BreakerLight.equals("Off")){
            tStringBuffer.append("<Data name=\"BchBreakerRelay\">").append(tKDData.BreakerLight).append("</Data>");
        }

        if(tKDData.BreakerGas.equals("Off")){
            tStringBuffer.append("<Data name=\"GaBchBreakerRelay\">").append(tKDData.BreakerGas).append("</Data>");
        }

                //.append("<Data name=\"GaBchBreakerRelay\">").append(tKDData.BreakerGas).append("</Data>")
                tStringBuffer.append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String BreakerGroup(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11071803</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"6\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">").append(tKDData.GroupID).append("</Data>")
                .append("<Data name=\"BchBreakerRelay\">").append(tKDData.BreakerLight).append("</Data>")
                .append("<Data name=\"GaBchBreakerRelay\">").append(tKDData.BreakerGas).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
