package com.kd.One.Service.Protocol.ControlProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by HN_USER on 2016-07-29.
 */
public class KDVentilation {
    public static String VentilationStateRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11071601</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"5\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">").append(tKDData.GroupID).append("</Data>")
                .append("<Data name=\"SubID\">").append(tKDData.SubID).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String VentilationEachRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11071602</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"6\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">").append(tKDData.GroupID).append("</Data>")
                .append("<Data name=\"SubID\">").append(tKDData.SubID).append("</Data>");

        if(tKDData.VentilationEachState.equals("PowerStatus")){
            tStringBuffer.append("<Data name=\"PowerStatus\">").append(tKDData.VentilationPower).append("</Data>");
        }else if(tKDData.VentilationEachState.equals("WindPower")){
            tStringBuffer.append("<Data name=\"WindPower\">").append(tKDData.VentilationWind).append("</Data>");
        }else if(tKDData.VentilationEachState.equals("Mode")){
            tStringBuffer.append("<Data name=\"Mode\">").append(tKDData.VentilationMode).append("</Data>");
        }

        tStringBuffer.append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String VentilationGroupRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11071603</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"7\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"GroupID\">").append(tKDData.GroupID).append("</Data>");

        if(tKDData.VentilationEachState.equals("PowerStatus")){
            tStringBuffer.append("<Data name=\"PowerStatus\">").append(tKDData.VentilationPower).append("</Data>");
        }else if(tKDData.VentilationEachState.equals("WindPower")){
            tStringBuffer.append("<Data name=\"WindPower\">").append(tKDData.VentilationWind).append("</Data>");
        }else if(tKDData.VentilationEachState.equals("Mode")){
            tStringBuffer.append("<Data name=\"Mode\">").append(tKDData.VentilationMode).append("</Data>");
        }

        tStringBuffer.append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
