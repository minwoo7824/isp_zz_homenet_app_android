package com.kd.One.Service.Protocol.InfoProtocol;

import com.kd.One.Common.KDData;

/**
 * Created by HN_USER on 2016-08-12.
 */
public class KDNotice {
    public static String KDKDNoticeRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>21070001</FunctionID>")
                .append("<FunctionCategory>NoticeBoard</FunctionCategory>")
                .append("<sql>")
                .append("<id>d_notice_board_list_select_page</id>")
                .append("<param>")
                .append("<type>page</type>")
                .append("<column>top1</column>")
                .append("<value>20</value>")
                .append("</param>")
                .append("<param>")
                .append("<type>page</type>")
                .append("<column>top2</column>")
                .append("<value>").append(tKDData.ListNum).append("</value>")
                .append("</param>")
                .append("</sql>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

}
