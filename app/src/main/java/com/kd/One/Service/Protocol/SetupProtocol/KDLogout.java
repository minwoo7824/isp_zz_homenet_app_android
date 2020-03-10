package com.kd.One.Service.Protocol.SetupProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by lwg on 2016-09-01.
 */
public class KDLogout {
    public static String Logout(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>5107FFFF</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"3\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
