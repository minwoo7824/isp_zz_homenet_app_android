package com.kd.One.Service.Protocol.SetupProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by lwg on 2016-09-06.
 */
public class KDVersion {
    public static String Version() {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>51070060</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
