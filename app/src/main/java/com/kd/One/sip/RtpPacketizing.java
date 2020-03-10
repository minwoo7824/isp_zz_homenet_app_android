package com.kd.One.sip;

import android.util.Log;

public class RtpPacketizing {
    public static int RTP_FIXED_HEADER_SIZE = 12;
    public static int H264_RFC3984_SU_HEADER_SIZE = 1; // single unit
    public static int H264_RFC3984_FU_A_HEADER_SIZE = 2; // fragment unit A
    public static int H264_CODEC_NAL_TYPE_SIZE = 1;
    public static int H264_START_CODE_SIZE = 4;
    public static int VIDEO_FRAME_BUFFER_SIZE = 50000;

    enum RET_RFC3894_CODE{
        RET_RFC3894_NONE,
        RET_RFC3894_SPS,
        RET_RFC3894_PPS,
        RET_RFC3894_END
    }

    public int decodelen;
    public byte[] decodebuffer;
    public Boolean packetErr;
    private int lastSeq;

    public RtpPacketizing()
    {
        decodelen = 0;
        packetErr = false;
        decodebuffer = new byte[VIDEO_FRAME_BUFFER_SIZE];
    }

    public RET_RFC3894_CODE RtpDepacketizing(byte[] buffer, int len) {
        byte[] pData = new byte[len];

        System.arraycopy(buffer, 0, pData, 0, len);

        byte rtpMarkbit = pData[1];
        byte FU_Indicator = pData[12];
        byte FU_Header;
        byte[] nalHeader = new byte[1];

        int nalDataLen = 0;

        byte[] startcode = {0x0, 0x0, 0x0, 0x1};

        int remainLen = decodelen;



        NalUnitHeader H264NalUnitHdr = new NalUnitHeader((int)((FU_Indicator >> 7) & (byte)0x01), (int)((FU_Indicator >> 5) & (byte)0x03), NalUnitType.parse(FU_Indicator & (byte)0x1f));

        if(decodebuffer == null)
        {
            decodebuffer = new byte[VIDEO_FRAME_BUFFER_SIZE];
        }



        int sequence1=(int)pData[2] & 0xFF;
        int sequence2=(int)pData[3] & 0xFF;
        int sequence=(sequence1 << 8) | sequence2;

        Log.e("h.264 nal", String.format("[SEQ  %d]", sequence));

        if(lastSeq != 0)
        {
            if(sequence != lastSeq+1)
            {
                packetErr = true;
            }
        }

        lastSeq = sequence;

        // RTP End packet
        if((rtpMarkbit & (byte)0x80) != 0x00)
        {
            if(H264NalUnitHdr.decodeNalUnitType == NalUnitType.RTP_H264_FU_A)
            {
                FU_Header = pData[13];

                //End
                if((FU_Header & (byte)0x40) != 0x00)
                {
                    nalDataLen = len - RTP_FIXED_HEADER_SIZE - H264_RFC3984_FU_A_HEADER_SIZE;

                    if(nalDataLen > 0)
                    {
                        System.arraycopy(pData, RTP_FIXED_HEADER_SIZE +	H264_RFC3984_FU_A_HEADER_SIZE, decodebuffer, remainLen, nalDataLen);

                        remainLen += nalDataLen;
                        decodelen = remainLen;

                        //����ó��
                        if(decodebuffer[0] == 0x00 && decodebuffer[1] == 0x00 && decodebuffer[2] == 0x00 && decodebuffer[3] == 0x01 )
                        {
                            return RET_RFC3894_CODE.RET_RFC3894_END;
                        }
                        else
                        {
                            packetErr = true;
                        }
                    }
                    else
                    {
                        Log.e("h.264 Nal", String.format("ERR 1 len:%d", nalDataLen));

                        //
                        packetErr = true;
                    }
                }
                else {
                    Log.e("h.264 Nal", String.format("ERR SEQ[%02x%02x] len:%d\n", pData[2], pData[3], len));

                    packetErr = true;
                }
            }
            else if (H264NalUnitHdr.decodeNalUnitType == NalUnitType.RTP_H264_NALU_TYPE_SLICE)
            {
                remainLen = 0;

                System.arraycopy(startcode, 0, decodebuffer, 0, H264_START_CODE_SIZE);

                remainLen += H264_START_CODE_SIZE;
                nalDataLen = len - RTP_FIXED_HEADER_SIZE;

                if(nalDataLen > 0)
                {
                    System.arraycopy(pData, RTP_FIXED_HEADER_SIZE, decodebuffer, remainLen, nalDataLen);

                    remainLen += nalDataLen;
                    decodelen = remainLen;

                    //����ó��
                    if(decodebuffer[0] == 0x00 && decodebuffer[1] == 0x00 && decodebuffer[2] == 0x00 && decodebuffer[3] == 0x01 )
                    {
                        return RET_RFC3894_CODE.RET_RFC3894_END;
                    }
                    else
                    {
                        packetErr = true;
                    }
                }
                else
                {
                    Log.e("h.264 Nal", String.format("ERR 2 len:%d", nalDataLen));

                    packetErr = true;
                }
            }
            else if (H264NalUnitHdr.decodeNalUnitType == NalUnitType.RTP_H264_NALU_TYPE_IDR)
            {
                System.arraycopy(startcode, 0, decodebuffer, remainLen, H264_START_CODE_SIZE);

                remainLen += H264_START_CODE_SIZE;
                nalDataLen = len - RTP_FIXED_HEADER_SIZE;

                if(nalDataLen > 0)
                {
                    System.arraycopy(pData, RTP_FIXED_HEADER_SIZE, decodebuffer, remainLen, nalDataLen);

                    remainLen += nalDataLen;
                    decodelen = remainLen;

                    //����ó��
                    if(decodebuffer[0] == 0x00 && decodebuffer[1] == 0x00 && decodebuffer[2] == 0x00 && decodebuffer[3] == 0x01 )
                    {
                        return RET_RFC3894_CODE.RET_RFC3894_END;
                    }
                    else
                    {
                        packetErr = true;
                    }
                }
                else
                {
                    Log.e("h.264 Nal", String.format("ERR 3 len:%d", nalDataLen));

                    packetErr = true;
                }
            }
            else {
                Log.e("h.264 Nal",String.format("ERR:TYPE:%d SEQ[%02x%02x] len:%d\n", NalUnitType.reparse(H264NalUnitHdr.decodeNalUnitType), pData[2], pData[3], len));

                packetErr = true;
            }
        }

        //RTP Normal Packet
        else
        {
            if (H264NalUnitHdr.decodeNalUnitType == NalUnitType.RTP_H264_FU_A)
            {
                FU_Header = pData[13];

                //IDR
                if ((byte)H264NalUnitHdr.nalRefId == (byte)0x03) // IDR
                {
                    if ((FU_Header & 0x80) != 0x00) // START I-FRAME
                    {
                        nalHeader[0] = (byte) ((FU_Indicator - NalUnitType.reparse(NalUnitType.RTP_H264_FU_A)) | (FU_Header & 0x1f));
                        System.arraycopy(startcode, 0, decodebuffer, remainLen, H264_START_CODE_SIZE);

                        remainLen += H264_START_CODE_SIZE;
                        System.arraycopy(nalHeader, 0, decodebuffer, remainLen, 1);

                        remainLen += H264_CODEC_NAL_TYPE_SIZE;
                        nalDataLen = len - RTP_FIXED_HEADER_SIZE - H264_RFC3984_FU_A_HEADER_SIZE;

                        if(nalDataLen > 0)
                        {
                            System.arraycopy(pData, RTP_FIXED_HEADER_SIZE +	H264_RFC3984_FU_A_HEADER_SIZE, decodebuffer, remainLen, nalDataLen);

                            remainLen += nalDataLen;
                            decodelen = remainLen;
                        }
                        else
                        {
                            Log.e("h.264 Nal", String.format("ERR 4 len:%d", nalDataLen));

                            packetErr = true;
                        }
                    }
                    else
                    {
                        nalDataLen = len - RTP_FIXED_HEADER_SIZE - H264_RFC3984_FU_A_HEADER_SIZE;

                        if(nalDataLen > 0)
                        {
                            System.arraycopy(pData, RTP_FIXED_HEADER_SIZE + H264_RFC3984_FU_A_HEADER_SIZE, decodebuffer, remainLen, nalDataLen);

                            remainLen += nalDataLen;
                            decodelen = remainLen;
                        }
                        else
                        {
                            Log.e("h.264 Nal", String.format("ERR 5 len:%d", nalDataLen));

                            packetErr = true;
                        }
                    }
                }
                else if ((byte)H264NalUnitHdr.nalRefId == (byte)0x02)
                {
                    // P-FRAME
                    if ((FU_Header & 0x80) != 0x00) // START P-FRAME
                    {
                        remainLen = 0;
                        nalHeader[0] = (byte)((FU_Indicator - NalUnitType.reparse(NalUnitType.RTP_H264_FU_A)) | (FU_Header & 0x1f));

                        remainLen += H264_START_CODE_SIZE;
                        System.arraycopy(startcode, 0, decodebuffer, 0, remainLen);
                        System.arraycopy(nalHeader, 0, decodebuffer, remainLen, 1);

                        remainLen += H264_CODEC_NAL_TYPE_SIZE;
                        nalDataLen = len - RTP_FIXED_HEADER_SIZE - H264_RFC3984_FU_A_HEADER_SIZE;

                        if(nalDataLen > 0)
                        {
                            System.arraycopy(pData, RTP_FIXED_HEADER_SIZE + H264_RFC3984_FU_A_HEADER_SIZE, decodebuffer, remainLen, nalDataLen);

                            remainLen += nalDataLen;
                            decodelen = remainLen;
                        }
                        else
                        {
                            Log.e("h.264 Nal", String.format("ERR 6 len:%d", nalDataLen));

                            packetErr = true;
                        }
                    }
                    else {
                        nalDataLen = len - RTP_FIXED_HEADER_SIZE - H264_RFC3984_FU_A_HEADER_SIZE;

                        if(nalDataLen > 0)
                        {
                            System.arraycopy(pData, RTP_FIXED_HEADER_SIZE + H264_RFC3984_FU_A_HEADER_SIZE, decodebuffer, remainLen, nalDataLen);

                            remainLen += nalDataLen;
                            decodelen = remainLen;
                        }
                        else
                        {
                            Log.e("h.264 Nal", String.format("ERR 7 len:%d", nalDataLen));

                            packetErr = true;
                        }
                    }
                }
                else
                {
                    Log.e("h.264 nal", String.format("P-M %d SEQ[%02x%02x] len:%d remain:%d\n", H264NalUnitHdr.nalRefId, pData[2], pData[3], len, remainLen));

                    packetErr = true;
                }
            }

            else if (H264NalUnitHdr.decodeNalUnitType == NalUnitType.RTP_H264_NALU_TYPE_SPS)
            {
                remainLen = 0;
                System.arraycopy(startcode, 0, decodebuffer, remainLen, H264_START_CODE_SIZE);

                remainLen += H264_START_CODE_SIZE;
                nalDataLen = len - RTP_FIXED_HEADER_SIZE;

                if(nalDataLen > 0)
                {
                    System.arraycopy(pData, RTP_FIXED_HEADER_SIZE, decodebuffer, remainLen, nalDataLen);

                    remainLen += nalDataLen;
                    decodelen = remainLen;

                    //Log.e("Log", "SPS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
                else
                {
                    Log.e("h.264 Nal", String.format("ERR 8 len:%d", nalDataLen));

                    packetErr = true;
                }
            }
            else if (H264NalUnitHdr.decodeNalUnitType == NalUnitType.RTP_H264_NALU_TYPE_PPS)
            {
                System.arraycopy(startcode, 0, decodebuffer, remainLen, H264_START_CODE_SIZE);

                remainLen += H264_START_CODE_SIZE;
                nalDataLen = len - RTP_FIXED_HEADER_SIZE;

                if(nalDataLen > 0)
                {
                    System.arraycopy(pData, RTP_FIXED_HEADER_SIZE, decodebuffer, remainLen, nalDataLen);

                    remainLen += nalDataLen;
                    decodelen = remainLen;
                }
                else
                {
                    Log.e("h.264 Nal", String.format("ERR 9 len:%d", nalDataLen));

                    packetErr = true;
                }
            }
            else
            {
                Log.e("h.264 nal", String.format("UNKNOWN SEQ[%02x%02x] len:%d\n", pData[2], pData[3], len));

                packetErr = true;
            }
        }
        return RET_RFC3894_CODE.RET_RFC3894_NONE;
    }

    byte decideFrameIsHeader(byte[] data, int datasize)
    {
        byte ret = 0x00;

        int i = 0;
        int count = 3;
        int syncword=0;
        byte[] pPacket = data;

        for(i=0 ; i<datasize ; datasize++)
        {
            syncword = (syncword << 8) | pPacket[count];

            if( (syncword >> 8) == 1 && (syncword & 0x1F) == 7)
            {
                ret=0x01;
                break;
            }
            count++;
        }
        return ret;
    }
}
