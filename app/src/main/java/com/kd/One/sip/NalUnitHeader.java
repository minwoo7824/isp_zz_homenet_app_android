package com.kd.One.sip;

public class NalUnitHeader {
    /**
     * Forbidden zero bit
     */
    public int forbiddenZeroBit;

    /**
     * NAL Reference id
     */
    public int nalRefId;

    /**
     * NAL Unit Type
     */
    public NalUnitType decodeNalUnitType;

    /**
     * Class constructor
     *
     * @param forbiddenZeroBit Forbidden zero bit
     * @param nalRefId NAL Reference id
     * @param nalUnitType NAL Unit Type value
     */
    public NalUnitHeader(int forbiddenZeroBit, int nalRefId, NalUnitType nalUnitType) {
        this.forbiddenZeroBit = forbiddenZeroBit;
        this.nalRefId = nalRefId;
        this.decodeNalUnitType = nalUnitType;
    }

    /**
     * Checks if the Forbidden Zero Bit is set.
     *
     * @return <code>True</code> if it is, <code>false</code> false otherwise.
     */
    public int isForbiddenBitSet() {
        return forbiddenZeroBit;
    }

    /**
     * Gets the NAL Reference ID
     *
     * @return NAL Reference ID
     */
    public int getNalRefId() {
        return nalRefId;
    }

    /**
     * Gets the NAL Unit Type
     *
     * @return
     */
    public NalUnitType getNalUnitType() {
        return decodeNalUnitType;
    }


    /**
     * Verifies if the H264 packet is a Fragmentation Unit Packet
     *
     * @return <code>True</code> if it is, <code>false</code> false otherwise.
     */
    public boolean isFragmentationUnit() {
        return decodeNalUnitType == NalUnitType.RTP_H264_FU_A || decodeNalUnitType == NalUnitType.RTP_H264_FU_B;
    }

    /**
     * Extracts the NAL Unit header from a H264 Packet
     *
     * @param h264Packet H264 Packet
     * @return {@link NalUnitHeader} Extracted NAL Unit Header
     * @throws {@link RuntimeException} If the H264 packet data is null
     */
    public static NalUnitHeader extract(byte[] h264Packet) {
        if (h264Packet == null) {
            throw new RuntimeException("Cannot extract H264 header. Invalid H264 packet");
        }

        NalUnitHeader header = new NalUnitHeader((byte)0x0, 0, NalUnitType.RESERVED);
        extract(h264Packet, header);

        return header;
    }

    /**
     * Extracts the NAL Unit header from a H264 Packet. Puts the extracted info
     * in the given header object
     *
     * @param h264Packet H264 packet
     * @param header Header object to fill with data
     * @throws {@link RuntimeException} If the H264 packet data is null or the
     *         header is null;
     */
    public static void extract(byte[] h264Packet, NalUnitHeader header) {
        if (h264Packet == null) {
            throw new RuntimeException("Cannot extract H264 header. Invalid H264 packet");
        }

        if (header == null) {
            throw new RuntimeException("Cannot extract H264 header. Invalid header packet");
        }

        byte headerByte = h264Packet[0];

        if(((headerByte & 0x80) >> 7) != 0)
        {
            header.forbiddenZeroBit = 0x1;
        }
        else
        {
            header.forbiddenZeroBit = 0x0;
        }

        header.nalRefId = ((headerByte & 0x60) >> 5);
        int nalUnitType = (headerByte & 0x1f);
        header.decodeNalUnitType = NalUnitType.parse(nalUnitType);
    }

    /**
     * Extracts the NAL Unit header from a H264 Packet
     *
     * @param h264Packet H264 Packet
     * @return {@link NalUnitHeader} Extracted NAL Unit Header
     * @throws {@link RuntimeException} If the H264 packet data is null
     */
    public static NalUnitHeader extract(int position, byte[] h264Packet) {
        if (h264Packet == null) {
            throw new RuntimeException("Cannot extract H264 header. Invalid H264 packet");
        }

        NalUnitHeader header = new NalUnitHeader((byte)0x0, 0, NalUnitType.RESERVED);
        extract(position, h264Packet, header);

        return header;
    }

    /**
     * Extracts the NAL Unit header from a H264 Packet. Puts the extracted info
     * in the given header object
     *
     * @param h264Packet H264 packet
     * @param header Header object to fill with data
     * @throws {@link RuntimeException} If the H264 packet data is null or the
     *         header is null;
     */
    public static void extract(int position, byte[] h264Packet, NalUnitHeader header) {
        if (h264Packet == null) {
            throw new RuntimeException("Cannot extract H264 header. Invalid H264 packet");
        }

        if (header == null) {
            throw new RuntimeException("Cannot extract H264 header. Invalid header packet");
        }

        byte headerByte = h264Packet[position];

        if(((headerByte & 0x80) >> 7) != 0)
        {
            header.forbiddenZeroBit = 0x1;
        }
        else
        {
            header.forbiddenZeroBit = 0x0;
        }

        header.nalRefId = ((headerByte & 0x60) >> 5);
        int nalUnitType = (headerByte & 0x1f);
        header.decodeNalUnitType = NalUnitType.parse(nalUnitType);
    }
}
