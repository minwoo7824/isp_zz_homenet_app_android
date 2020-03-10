package com.kd.One.Service.Protocol.SetupProtocol;

import com.kd.One.Common.KDData;

public class KDPushSetting {
    public static String KDPushSettingChange(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>510700101</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"5\">")
                .append("<Data name=\"id\">").append(tKDData.ID).append("</Data>")
                .append("<Data name=\"emergency_push\">").append(tKDData.PushEmergency).append("</Data>")
                .append("<Data name=\"car_inout_push\">").append(tKDData.PushCarInOut).append("</Data>")
                .append("<Data name=\"delivery_push\">").append(tKDData.PushDelivery).append("</Data>")
                .append("<Data name=\"sip_push\">").append(tKDData.PushSip).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");
        return tStringBuffer.toString();
    }

    public static String KDPushSettingLookUp(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>510700100</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"5\">")
                .append("<Data name=\"id\">").append(tKDData.ID).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");
        return tStringBuffer.toString();
    }
}
