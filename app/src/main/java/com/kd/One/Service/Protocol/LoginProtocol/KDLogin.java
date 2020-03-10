package com.kd.One.Service.Protocol.LoginProtocol;

import com.kd.One.Common.KDData;
import com.kd.One.Common.KDUtil;

/**
 * Created by lwg on 2016-07-13.
 */
public class KDLogin {

    public static String KDLogin(KDData tKDData){
        StringBuffer   tStringBuffer = new StringBuffer();
        String          tPass        = KDUtil.sha256Base64(tKDData.Password);

        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>51070000</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"2\" name=\"Login\">")
                .append("<Data name=\"UserID\">").append(tKDData.ID).append("</Data>")
                .append("<Data name=\"Password\">").append(tPass).append("</Data>")
                .append("<Data name=\"AppCertify\">").append(tKDData.AppCertify).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String KDAuthentication(KDData tKDData){
        StringBuffer   tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>51070062</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"2\">")
                .append("<Data name=\"ID\">").append(tKDData.ID).append("</Data>")
                .append("<Data name=\"CertifyNumber\">").append(tKDData.AppCertify).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String KDCertify(KDData tKDData){
        StringBuffer   tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>51070061</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"1\" name=\"HomeServerInfo\">")
                .append("<Data name=\"ID\">").append(tKDData.ID).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String KDIDDuplication(KDData tKDData){
        StringBuffer   tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>51070007</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"1\" name=\"IDCheck\">")
                .append("<Data name=\"UserID\">").append(tKDData.ID).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String KDFindID(KDData tKDData){
        StringBuffer   tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>51070006</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"2\" name=\"IDSearch\">")
                .append("<Data name=\"Name\">").append(tKDData.Name).append("</Data>")
                .append("<Data name=\"Smartphone\">").append(tKDData.SmartPhone).append("</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String KDRegistration(KDData tKDData){
        StringBuffer   tStringBuffer = new StringBuffer();
        String          tPass        = KDUtil.sha256Base64(tKDData.Password);

        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>51070008</FunctionID>")
                .append("<FunctionCategory>Insert</FunctionCategory>")
                .append("<sql>")
                .append("<id>web_member_info_insert</id>")

                // id
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(tKDData.ID).append("</value>")
                .append("</param>")

                // password
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(tPass).append("</value>")
                .append("</param>")

                // name
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(tKDData.Name).append("</value>")
                .append("</param>")

                // home phone number
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append("000-000-0000").append("</value>")
                .append("</param>")

                // cell phone number
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(tKDData.SmartPhone).append("</value>")
                .append("</param>")

                // E-mail
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append("0000").append("</value>")
                .append("</param>")

                // complex
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append("0000").append("</value>")
                .append("</param>")

                // dong
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(tKDData.Dong).append("</value>")
                .append("</param>")

                // ho
                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(tKDData.Ho).append("</value>")
                .append("</param>")

                // member level
                .append("<param>")
                .append("<type>char</type>")
                .append("<value>").append("22").append("</value>")
                .append("</param>")

                .append("<param>")
                .append("<type>char</type>")
                .append("<value>").append("Y").append("</value>")
                .append("</param>")

                .append("<param>")
                .append("<type>char</type>")
                .append("<value>").append("APP").append("</value>")
                .append("</param>")

                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append("</value>")
                .append("</param>")

                .append("<param>")
                .append("<type>varchar</type>")
                .append("<value>").append(KDData.ReturnKDDate().trim()).append("</value>")
                .append("</param>")

                .append("<param>")
                .append("<type>char</type>")
                .append("<value>").append("F").append("</value>") // F : 무료, P : 유료
                .append("</param>")

                .append("</sql>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }

    public static String KDDeviceInfomationRequest(KDData tKDData) {
        StringBuffer tStringBuffer = new StringBuffer();
        tStringBuffer.append("<HNML>")
                .append("<ControlRequest TransID=\"").append(KDData.ReturnKDTransID().trim()).append("\">")
                .append("<FunctionID>510700F0</FunctionID>")
                .append("<FunctionCategory>Control</FunctionCategory>")
                .append("<InputList size=\"1\">")
                .append("<Input size=\"9\">")
                .append("<Data name=\"ID\">").append(tKDData.ID).append("</Data>")
                .append("<Data name=\"deviceId\">").append(tKDData.DeviceInfoID).append("</Data>")
                .append("<Data name=\"devType\">").append(tKDData.DeviceInfoType).append("</Data>")
                .append("<Data name=\"devOS\">").append(tKDData.DeviceInfoOS).append("</Data>")
                .append("<Data name=\"devOSVer\">").append(tKDData.DeviceInfoOSVer).append("</Data>")
                .append("<Data name=\"devAppVer\">").append(tKDData.DeviceInfoAppVer).append("</Data>")
                .append("<Data name=\"devModel\">").append(tKDData.DeviceInfoModel).append("</Data>")
                .append("<Data name=\"pushType\">").append(tKDData.DeviceInfoPushType).append("</Data>")
                .append("<Data name=\"pushKey\">").append(tKDData.DeviceInfoPushKey).append("</Data>")
                .append("<Data name=\"appId\">com.kyungdong.kdhomenet</Data>")
                .append("</Input>")
                .append("</InputList>")
                .append("</ControlRequest>")
                .append("</HNML>");

        return tStringBuffer.toString();
    }
}
