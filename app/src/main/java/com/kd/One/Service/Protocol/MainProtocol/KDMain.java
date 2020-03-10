package com.kd.One.Service.Protocol.MainProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by lwg on 2016-07-14.
 */
public class KDMain {
    public static String KDMainInformationRequest(KDData tKDData){
        StringBuffer   tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>1107001E</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"4\">")
                .append("<Data name=\"Complex\">").append("0000").append("</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"DeviceType\">").append("All").append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String KDMainDisplayRequest(KDData tKDData){
        StringBuffer   tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>51070063</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"3\">")
                .append("<Data name=\"Complex\">").append("0000").append("</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
