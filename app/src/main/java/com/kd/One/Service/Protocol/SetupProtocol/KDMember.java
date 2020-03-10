package com.kd.One.Service.Protocol.SetupProtocol;

import com.kd.One.Common.KDData;
import com.kd.One.Common.KDUtil;

/**
 * Created by lwg on 2016-09-06.
 */
public class KDMember {
    public static String KDMember(KDData tKDData){
        StringBuffer   tStringBuffer = new StringBuffer();
        String          tPass        = KDUtil.sha256Base64(tKDData.Password);

        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>51070001</FunctionID>")
                .append("<FunctionCategory>Update</FunctionCategory>")
                .append("<sql>")
                .append("<id>app_member_info_update</id>")

                // pw
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(tPass).append("</value>")
                .append("</param>")

                // phone
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(tKDData.SmartPhone).append("</value>")
                .append("</param>")

                // date
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(KDData.ReturnKDDate()).append("</value>")
                .append("</param>")

                // id
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(tKDData.ID).append("</value>")
                .append("</param>")

                .append("</sql>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
