package com.kd.One.Service.Protocol.LoginProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by lwg on 2016-07-10.
 */
public class KDComplex {
    public static String KDComplex() {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>00000000</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"1\">")
                .append("<Data name=\"Name\">SP</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
