package com.kd.One.Service.Protocol.DoorOpenProtocol;

import com.kd.One.Common.KDData;
import com.kd.One.Common.KDUtil;

public class KDDoorOpen {
    public static String KDDoorOpen(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>1F500202</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"5\" name =\"OpenDoor\">")
                .append("<Data name=\"Type\">").append(tKDData.PushType).append("</Data>")
                .append("<Data name=\"AuthCode\">").append(tKDData.PushPassword).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");
        return tStringBuffer.toString();
    }
}
