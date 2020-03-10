package com.kd.One.Service.Protocol.SetupProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by lwg on 2016-09-05.
 */
public class KDWithdrawal {
    public static String Withdrawal(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>51070009</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"2\">")
                .append("<Data name=\"id\">").append(tKDData.ID).append("</Data>")
                .append("<Data name=\"device\">").append("APP").append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
