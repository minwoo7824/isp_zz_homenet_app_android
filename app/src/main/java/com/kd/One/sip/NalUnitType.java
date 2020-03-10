package com.kd.One.sip;

public enum  NalUnitType {
    RESERVED,
    RTP_H264_NALU_TYPE_SLICE,
    RTP_H264_NALU_TYPE_DPA,
    RTP_H264_NALU_TYPE_DPB,
    RTP_H264_NALU_TYPE_DPC,
    RTP_H264_NALU_TYPE_IDR,
    RTP_H264_NALU_TYPE_SEI,
    RTP_H264_NALU_TYPE_SPS,
    RTP_H264_NALU_TYPE_PPS,
    RTP_H264_NALU_TYPE_PD,
    RTP_H264_NALU_TYPE_EOSEQ,
    RTP_H264_NALU_TYPE_EOSTREAM,
    RTP_H264_NALU_TYPE_FILL,
    RTP_H264_SNAL_13,
    RTP_H264_SNAL_14,
    RTP_H264_SNAL_15,
    RTP_H264_SNAL_16,
    RTP_H264_SNAL_17,
    RTP_H264_SNAL_18,
    RTP_H264_SNAL_19,
    RTP_H264_SNAL_20,
    RTP_H264_SNAL_21,
    RTP_H264_SNAL_22,
    RTP_H264_SNAL_23,
    RTP_H264_STAP_A,
    RTP_H264_STAP_B,
    RTP_H264_MTAP16 ,
    RTP_H264_MTAP24,
    RTP_H264_FU_A,
    RTP_H264_FU_B;


    public static NalUnitType parse(int value) {
        switch (value) {
            case 1:
                return RTP_H264_NALU_TYPE_SLICE;
            case 2:
                return RTP_H264_NALU_TYPE_DPA;
            case 3:
                return RTP_H264_NALU_TYPE_DPB;
            case 4:
                return RTP_H264_NALU_TYPE_DPC;
            case 5:
                return RTP_H264_NALU_TYPE_IDR;
            case 6:
                return RTP_H264_NALU_TYPE_SEI;
            case 7:
                return RTP_H264_NALU_TYPE_SPS;
            case 8:
                return RTP_H264_NALU_TYPE_PPS;
            case 9:
                return RTP_H264_NALU_TYPE_PD;
            case 10:
                return RTP_H264_NALU_TYPE_EOSEQ;
            case 11:
                return RTP_H264_NALU_TYPE_EOSTREAM;
            case 12:
                return RTP_H264_NALU_TYPE_FILL;
            case 13:
                return RTP_H264_SNAL_13;
            case 14:
                return RTP_H264_SNAL_14;
            case 15:
                return RTP_H264_SNAL_15;
            case 16:
                return RTP_H264_SNAL_16;
            case 17:
                return RTP_H264_SNAL_17;
            case 18:
                return RTP_H264_SNAL_18;
            case 19:
                return RTP_H264_SNAL_19;
            case 20:
                return RTP_H264_SNAL_20;
            case 21:
                return RTP_H264_SNAL_21;
            case 22:
                return RTP_H264_SNAL_22;
            case 23:
                return RTP_H264_SNAL_23;
            case 24:
                return RTP_H264_STAP_A;
            case 25:
                return RTP_H264_STAP_B;
            case 26:
                return RTP_H264_MTAP16;
            case 27:
                return RTP_H264_MTAP24;
            case 28:
                return RTP_H264_FU_A;
            case 29:
                return RTP_H264_FU_B;
            default:
                return RESERVED;

        }
    }

    public static int reparse(NalUnitType value) {
        switch (value) {
            case RTP_H264_NALU_TYPE_SLICE:
                return 1;
            case RTP_H264_NALU_TYPE_DPA:
                return 2;
            case RTP_H264_NALU_TYPE_DPB:
                return 3;
            case RTP_H264_NALU_TYPE_DPC:
                return 4;
            case RTP_H264_NALU_TYPE_IDR:
                return 5;
            case RTP_H264_NALU_TYPE_SEI:
                return 6;
            case RTP_H264_NALU_TYPE_SPS:
                return 7;
            case RTP_H264_NALU_TYPE_PPS:
                return 8;
            case RTP_H264_NALU_TYPE_PD:
                return 9;
            case RTP_H264_NALU_TYPE_EOSEQ:
                return 10;
            case RTP_H264_NALU_TYPE_EOSTREAM:
                return 11;
            case RTP_H264_NALU_TYPE_FILL:
                return 12;
            case RTP_H264_SNAL_13:
                return 13;
            case RTP_H264_SNAL_14:
                return 14;
            case RTP_H264_SNAL_15:
                return 15;
            case RTP_H264_SNAL_16:
                return 16;
            case RTP_H264_SNAL_17:
                return 17;
            case RTP_H264_SNAL_18:
                return 18;
            case RTP_H264_SNAL_19:
                return 19;
            case RTP_H264_SNAL_20:
                return 20;
            case RTP_H264_SNAL_21:
                return 21;
            case RTP_H264_SNAL_22:
                return 22;
            case RTP_H264_SNAL_23:
                return 23;
            case RTP_H264_STAP_A:
                return 24;
            case RTP_H264_STAP_B:
                return 25;
            case RTP_H264_MTAP16:
                return 26;
            case RTP_H264_MTAP24:
                return 27;
            case RTP_H264_FU_A:
                return 28;
            case RTP_H264_FU_B:
                return 29;
            default:
                return 0;

        }
    }
}
