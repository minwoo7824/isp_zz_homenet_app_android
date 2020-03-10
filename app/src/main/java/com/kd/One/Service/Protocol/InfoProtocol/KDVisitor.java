package com.kd.One.Service.Protocol.InfoProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by HN_USER on 2016-08-12.
 */
public class KDVisitor {
    public static String VisitListRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11070015</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"5\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"StartTime\">0000-00-00T00:00:00</Data>")
                .append("<Data name=\"EndTime\">").append(tKDData.EndDate).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String VisitVideoRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11070017</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"5\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"VideoId\">").append(tKDData.VideoID).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String VisitConfirmRequest(KDData tKDData){
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>11070029</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"5\">")
                .append("<Data name=\"Complex\">0000</Data>")
                .append("<Data name=\"Dong\">").append(tKDData.Dong).append("</Data>")
                .append("<Data name=\"Ho\">").append(tKDData.Ho).append("</Data>")
                .append("<Data name=\"VideoId\">").append(tKDData.VideoID).append("</Data>")
                .append("<Data name=\"Status\">").append(tKDData.VideoState).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
