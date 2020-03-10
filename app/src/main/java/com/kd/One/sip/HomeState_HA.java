package com.kd.One.sip;

import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

public class HomeState_HA {
    private String TAG = "HomeState_HA";
    public boolean completeFlag; 		// 두쌍의 상태정보를 다 받으면
    public boolean ErrorFlag;       	// 보일러 에러 발생 여부

    public String HeaterrorCode; 		// 1Byte. 난방 장애 Code : 고장시 이상에 대한 코드값.
    public String GaserrorCode; 		// 1Byte. 가스 장애 Code : 고장시 이상에 대한 코드값.
    public String LighterrorCode; 		// 1Byte. 조명 장애 Code : 고장시 이상에 대한 코드값.
    public String StandbyerrorCode; 	// 1Byte. 대기전력 장애 Code : 고장시 이상에 대한 코드값.
    public String PowershuterrorCode; 	// 1Byte. 일괄차단 장애 Code : 고장시 이상에 대한 코드값.
    public String GoouterrorCode;		// 1Byte. 외출 장애 Code : 고장시 이상에 대한 코드값.
    public String DoorerrorCode;		// 1Byte. 문열림 장애 Code : 고장시 이상에 대한 코드값.
    public String AirconerrorCode;		// 1Byte. 에어컨 장애 Code : 고장시 이상에 대한 코드값.


    // 프로토콜의 기본정보
    public String sn; 					// 8Byte. GW 고유번호(16진수값로 Mac + FixCode + Random)
    public byte swRev; 					// 1Byte. 소프트웨어 버전(0x01~0xFF)
    public byte controlType;        	// 1Byte. 보일러컨트롤 타입(0x01: 나비엔One, 0x02: 난방비디오폰)
    public byte boilerModelType; 		// 1Byte. UHA-750C(0x01), UHA-1010C(0x02)
    public String optionUseFirstFg;    	// 1Byte. 옴션기능사용유무 01 //2bit : 홈뷰h/w (사용:1, 미사용:0), 1bit : 홈뷰가능s/w (:1, :0)
    public String optionUseSecondFg;   	// 1Byte. 옴션기능사용유무 02
    public byte modeState;          	// 1Byte. 작동모드


    public byte[][] roomState;			 // Error Code [0][0]
    // Room Count [0][1]
    // 작동모드 [1][0] ~ [8][0]
    // 현재온도 [1][1] ~ [8][1]
    // 설정온도 [1][2] ~ [8][2]

    public byte[][] gasState;            // Error Code [0][0]
    // Gas count  [0][1]
    // 가스상태          [1][0] ~ [8][0]

    public byte[][] lightState;          // Error Code [0][0]
    // Light Count[0][1]
    // 조명상태          [1][0] ~ [8][0]

    public byte[][] dimState;            // Error Code [0][0]
    // Dim Count  [0][1]
    // 디밍상태          [1][0] ~ [8][0]

    public byte[][] standbyState;        // Error Code        [0][0]
    // Standby Count     [0][1]
    // 자동차단 설정값   [1][0] ~ [8][0] 2byte
    // 현재 사용량       [1][2] ~ [8][2] 4byte

    // 자동차단 On/Off   [1][6] ~ [8][6]
    // 차단기준 On/Off   [1][7] ~ [8][7]
    // 과부하 On/Off     [1][8] ~ [8][8]
    // 전력공급 On/Off   [1][9] ~ [8][9]

    public byte[][] powershutState;      // Error Code        [0][0]
    // Powershut Count   [0][1]
    // 전등차단          [1][0] ~ [8][0]
    // 가스차단          [1][1] ~ [8][1]

    public byte[][] gooutState; 		// Error Code		  [0][0]
    // goout Count       [0][1]
    // 외출상태          [1][0] ~ [8][0]

    public byte[][] doorState; 		// Error Code		  [0][0]
    // goout Count       [0][1]
    // 문열림상태          [1][0] ~ [8][0]

    public byte[][] airconState; 		// Error Code		  [0][0]
    // aircon Count       [0][1]
    // power          [1][0] ~ [8][0] : 0x01 On 0x00 Off
    // 현재 온도        	  [1][1] ~ [8][1] : temp *2
    // 설정 온도 		  [1][2] ~ [8][2] : temp *2
    // 동작 모드		  [1][3] ~ [8][3] : 0x01 Auto 0x02 cool 0x03 dry 0x04 wind 0x05 heat 0x06 resv
    // 풍향 모드		  [1][4] ~ [8][4] : 0x00 fix 0x01 auto
    // 풍량 모드		  [1][5] ~ [8][5] : 1~5
    // 온도 단위		  [1][6] ~ [8][6] : 0x00 1도 제어 0x01 0.5도 제어
    // 온도 min		  [1][7] ~ [8][7] : 온도 최저 값
    // 온도 max		  [1][8] ~ [8][8] : 온도 최대 값

    public byte[][] heatGroupCnt;
    //그룹 개수 			[0][0]
    //그룹별 방개수 		[0][1] ~ [0][3]

    public byte[][] ventState;
    //vent Count		[0][1]
    //power				 	[1][1] ~ [20][1]
    //모드상태			 	[1][2] ~ [20][2]		0x01 일반  0x02 취침 0x03 전열 0x04 자동 0x05 절약
    //바람세기			 	[1][3] ~ [20][3]       0x01 미풍 0x02 약풍 0x03 강풍
    //히터 상세정보		 	[1][4] ~ [20][4]		0x01 동작 0x00 정지
    //팬상태 상세정보	 	[1][5] ~ [20][5]		0x01 과열 0x00 정상
    //제연운전 상세정보	 	[1][6] ~ [20][6]		0x01 제연운전 0x00 정상
    //필터 상세정보		 	[1][7] ~ [20][7]		0x01 필터교체 0x00 정상
    //전열교환기 상세정보	[1][8] ~ [20][8]		0x01 교체 0x00 정상
    //Co2 농도과다알림(환기)[1][9] ~ [20][9]        0x01 농도과다 0x00 정상

    public EncodeUtil eutil;

    public boolean bUseHeatFunc;		 	//난방 사용 유무 확인
    public boolean bUseGasFunc;			 	//가스 사용 유무 확인
    public boolean bUseLightFunc;		 	//조명 사용 유무 확인
    public boolean bUseHotWaterFunc;	 	//온수 사용 유무 확인
    public boolean bUseMinorityFunc;	 	//0.5도 사용 유무 확인
    public boolean bUseStandbyFunc;		 	//대기전력 사용 유무 확인
    public boolean bUsePowershutLightFunc;	//일괄차단 사용 유무 확인
    public boolean bUsePowershutGasFunc;	//일괄차단 사용 유무 확인
    public boolean bUseGooutFunc;			//외출모드 사용 유무 확인
    public boolean bUseDoorFunc;			//문열림기능 사용 유무 확인
    public boolean bUseHomeviewHwFunc;	 	//홈뷰 사용 유무 확인(Hw)
    public boolean bUseHomeviewSwFunc;	 	//홈뷰 사용 유무 확인(Sw)
    public boolean bUseHomeviewVideoFunc;	//홈뷰 비디오 유뮤 확인

    public boolean bUseAirconFunc;			//에어컨 사용 유무 확인

    //
    public String saUserEmail;
    public String saUserId;
    public String saAccessToken;
    public ArrayList<JSONObject> aircon_SA = new ArrayList<JSONObject>();

    public boolean setState(byte [] data)
    {
        //Log.e("data : ", EncodeUtil.hexStringFromCharacter(data, 0, data.length));
        int nHeaderSize = 13; //header size(고정)

        eutil = new EncodeUtil();

        boolean result = false;

        try {
            //001ec00cb8f31444  01  00  0a  02  01  01  01  00  00  00  51  02  80  04  2c  4c970532b24892000000000000000000000000

			/*
			String str1 = “Hello World!”;
			// 변수 str1의 바이트 값
			// 72101108108111328711111410810033
			bytes[] buffers = str1.getBytes();

			// 바이트 배열 자체의 문자열 값
			// [B@ca0b6
			String buffersArrayString = buffers.toString();

			// 바이트 배열을 문자열로 변환한 값
			// Hello World!
			String str2 = new String(buffers);
			 */

            // 프로토콜의 기본정보
            //sn             		= [Utility hexStringFromCharacter:data start:0 length:8];   //serial number

            sn             			= EncodeUtil.hexStringFromCharacter(data, 0, 8);
            Log.i(TAG,"data : " + data + " sn : " + sn);
            swRev               	= data[8];
            controlType         	= data[9];
            boilerModelType     	= data[10]; //UHA-750C : 0x01, UHA-1010C : 0x02, UHA-1010C VE : 0x03, UHA-1020 : 0x04 UHA-775 : 0x07
            optionUseFirstFg       	= EncodeUtil.hexStringFromCharacter(data, 11, 1);
            optionUseSecondFg      	= EncodeUtil.hexStringFromCharacter(data, 12, 1);

            //NEW
            hexstringFrombinarystringFirst(optionUseFirstFg.substring(0,1));
            hexstringFrombinarystringSecond(optionUseFirstFg.substring(1,2));
            hexstringFrombinarystringThird(optionUseSecondFg.substring(0,1));
            hexstringFrombinarystringFourth(optionUseSecondFg.substring(1,2));

            modeState           	= data[13];

            //====================================
            // 원격제어 홈오토만 이용 (7인치, 10인치)
            //====================================

            //----------
            //roomState
            //----------
            roomState = new byte[30][3];		//각방 배열 선언
            roomState[0][0] = data[14];		//룸 에러코드
            roomState[0][1] = data[15]; 	//룸 카운트
            int nRoomCnt = data[15];

            if(nRoomCnt != 0)
            {
                int roomIdx = 16;
                int tempidx = roomIdx;
                //[roomState removeAllObjects];

                for (int i = 0; i < nRoomCnt; i++) {
                    tempidx = roomIdx + (i*3);

                    roomState[i+1][0] = data[tempidx];		//각방 제어시 작동모드
                    roomState[i+1][1] = data[tempidx + 1];	//각방 제어시 현재온도
                    roomState[i+1][2] = data[tempidx + 2];	//각방 제어시 설정온도
                }
            }

            //실내난방 에러발생
            if(roomState[0][0] != 0)	//에러가 발생하면 (0이아닌값이 들어오는경우)
            {
                HeaterrorCode       	= EncodeUtil.DecimalStringFromCharacter(roomState[0][0]);
                ErrorFlag = true;
            }
            //정상
            else
            {
                ErrorFlag = false;
            }

            //----------
            //gasState
            //----------
            gasState = new byte[20][3];		//각방 배열 선언
            gasState[0][0] = data[nHeaderSize + 2 + nRoomCnt*3 + 1];	 //가스  에러코드
            gasState[0][1] = data[nHeaderSize + 2 + nRoomCnt*3 + 1 + 1]; //가스  카운트
            int nGasCnt = data[nHeaderSize + 2 + nRoomCnt*3 + 1 + 1];

            if(nGasCnt != 0)
            {
                int gasIdx = nHeaderSize + 2 + nRoomCnt * 3 + 1 + 1 + 1;
                int tempidx = gasIdx;
                //[roomState removeAllObjects];


                for (int i = 0; i < nGasCnt; i++) {
                    tempidx = gasIdx + i;

                    gasState[i+1][0] = data[tempidx];		//가스상태
                }

            }

            //가스 에러발생
            if(gasState[0][0] != 0)	//에러가 발생하면 (0이아닌값이 들어오는경우)
            {
                GaserrorCode        	= EncodeUtil.DecimalStringFromCharacter(gasState[0][0]);
                ErrorFlag = true;
            }
            //정상
            else
            {
                ErrorFlag = false;
            }


            //----------
            //lightState
            //----------
            lightState = new byte[30][3];		//각방 배열 선언
            lightState[0][0] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt *1 + 1];	 	//조명  에러코드
            lightState[0][1] = data[nHeaderSize +2 + nRoomCnt*3 +2 + nGasCnt*1 +1 +1]; 	//조명  카운트
            int nLightCnt = data[nHeaderSize +2 + nRoomCnt*3 +2 + nGasCnt*1 +1 +1];

            if(nLightCnt !=0)
            {
                int lightIdx = nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt * 1 + 1 + 1 +1;
                int tempidx = lightIdx;
                //[roomState removeAllObjects];


                for (int i = 0; i < nLightCnt; i++) {
                    tempidx = lightIdx + i;

                    lightState[i+1][0] = data[tempidx];		//조명상태
                }

            }

            //조명 에러발생
            if(lightState[0][0] != 0)	//에러가 발생하면 (0이아닌값이 들어오는경우)
            {
                LighterrorCode           	= EncodeUtil.DecimalStringFromCharacter(lightState[0][0]);
                ErrorFlag = true;
            }
            //정상
            else
            {
                ErrorFlag = false;
            }


            //----------
            //DimState
            //----------
            dimState = new byte[20][3];		//각방 배열 선언
            dimState[0][1] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1]; 	//디밍  카운트
            int nDimCnt = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt * 1 + 2 + nLightCnt * 1 + 1];

            if(nDimCnt !=0)
            {
                int dimIdx = nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt * 1 + 2 + nLightCnt * 1 + 1 + 1;
                int tempidx = dimIdx;
                //[roomState removeAllObjects];


                for (int i = 0; i < nDimCnt; i++) {
                    tempidx = dimIdx + i;

                    dimState[i+1][0] = data[tempidx];		//디밍상태
                }

            }

            //----------
            //standbyStatus
            //----------

            standbyState = new byte[20][10];		//대기전력 배열 선언
            int nStandbyCnt = 0;

            //Buffer Length Check
            if(data.length >= (nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 1))
            {
                standbyState[0][0] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 1];		//대기전력 에러코드
                standbyState[0][1] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2]; 		//대기전력 카운트
                nStandbyCnt = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2];

                if(nStandbyCnt != 0)
                {
                    int standbyIdx = nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 3;
                    int tempidx = standbyIdx;


                    for (int i = 0; i < nStandbyCnt; i++) {
                        tempidx = standbyIdx + (i*10);

                        standbyState[i+1][0] = data[tempidx];		//대기전력 설정값 1
                        standbyState[i+1][1] = data[tempidx + 1];	//대기전력 설정값 2

                        standbyState[i+1][2] = data[tempidx + 2];	//현재 사용량 1
                        standbyState[i+1][3] = data[tempidx + 3];	//현재 사용량 2
                        standbyState[i+1][4] = data[tempidx + 4];	//현재 사용량 3
                        standbyState[i+1][5] = data[tempidx + 5];	//현재 사용량 4

                        standbyState[i+1][6] = data[tempidx + 6];	//자동차단 상태
                        standbyState[i+1][7] = data[tempidx + 7];	//차단기준
                        standbyState[i+1][8] = data[tempidx + 8];	//과부하
                        standbyState[i+1][9] = data[tempidx + 9];	//Power 상태
                    }
                }

                //대기전력 에러발생
                if(standbyState[0][0] != 0)	//에러가 발생하면 (0이아닌값이 들어오는경우)
                {
                    StandbyerrorCode       	= EncodeUtil.DecimalStringFromCharacter(standbyState[0][0]);
                    ErrorFlag = true;
                }
                //정상
                else
                {
                    ErrorFlag = false;
                }

            }
            else
            {
                nStandbyCnt = 0;
            }
            //----------
            //PowerShut
            //----------
            powershutState = new byte[20][2];		//일괄차단 스위치 배열 선언
            int nPowershutCnt = 0;

            //Buffer Length Check
            if(data.length >= (nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 +1))
            {
                powershutState[0][0] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 +1]; 	//에러코드
                powershutState[0][1] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 +2]; 	//카운트
                nPowershutCnt = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2];

                if(nPowershutCnt != 0)
                {
                    int powershutIdx = nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 3;
                    int tempidx = powershutIdx;


                    for (int i = 0; i < nPowershutCnt; i++) {
                        tempidx = powershutIdx + (i*2);

                        powershutState[i+1][0] = data[tempidx];		//일괄차단 전등차단
                        powershutState[i+1][1] = data[tempidx + 1];	//일괄차단 가스차단
                    }
                }

                //일괄차단 에러발생
                if(powershutState[0][0] != 0)	//에러가 발생하면 (0이아닌값이 들어오는경우)
                {
                    PowershuterrorCode       	= EncodeUtil.DecimalStringFromCharacter(powershutState[0][0]);
                    ErrorFlag = true;
                }
                //정상
                else
                {
                    ErrorFlag = false;
                }

            }
            else
            {
                nPowershutCnt = 0;
            }

            //----------
            //Goout
            //----------
            gooutState = new byte[3][2];		//외출 배열 선언
            int nGooutCnt = 0;

            //Buffer Length Check
            if(data.length >= (nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2  +1))
            {


                gooutState[0][0] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2  +1]; 	//에러코드
                gooutState[0][1] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2  +2]; 	//카운트
                nGooutCnt = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2  +2];

                //if(nGooutCnt != 0)
                //{
                int gooutIdx = nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3;
                int tempidx = gooutIdx;

                gooutState[1][0] = data[tempidx];		//외출 상태

                //}
                Log.e("외출상태", gooutState[1][0] + "//");

                if(gooutState[0][0] != 0)	//에러가 발생하면 (0이아닌값이 들어오는경우)
                {
                    GoouterrorCode       	= EncodeUtil.DecimalStringFromCharacter(gooutState[0][0]);
                    ErrorFlag = true;
                }
                //정상
                else
                {
                    ErrorFlag = false;
                }

            }
            else
            {
                nGooutCnt = 0;
            }

            //----------
            //Door
            //----------

            doorState = new byte[3][2];		//외출 배열 선언
            int nDoorCnt = 0;

            //Buffer Length Check
            if(data.length >= (nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 1))
            {


                doorState[0][0] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 1]; 	//에러코드
                doorState[0][1] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 2]; 	//카운트
                nDoorCnt = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 2];

                int doorIdx = nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3;
                int tempidx = doorIdx;

                doorState[1][0] = data[tempidx];		//외출 상태

                if(doorState[0][0] != 0)	//에러가 발생하면 (0이아닌값이 들어오는경우)
                {
                    DoorerrorCode       	= EncodeUtil.DecimalStringFromCharacter(doorState[0][0]);
                    ErrorFlag = true;
                }
                //정상
                else
                {
                    ErrorFlag = false;
                }

            }
            else
            {
                nDoorCnt = 0;
            }

            //----------
            //Aircon
            //----------

            airconState = new byte[20][10];
            int nAirconCnt = 0;

            //Buffer Length Check
            if(data.length >= (nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 1))
            {


                airconState[0][0] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 1]; 	//에러코드
                airconState[0][1] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2]; 	//카운트
                nAirconCnt = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2];

                if(nAirconCnt != 0)
                {
                    int airconIdx = nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1;
                    int tempidx = airconIdx;


                    for (int i = 0; i < nAirconCnt; i++)
                    {
                        tempidx = airconIdx + (i*10);

                        airconState[i+1][0] = data[tempidx];		//Power
                        airconState[i+1][1] = data[tempidx + 1];	//현재온도
                        airconState[i+1][2] = data[tempidx + 2];	//설정온도
                        airconState[i+1][3] = data[tempidx + 3];	//모드
                        airconState[i+1][4] = data[tempidx + 4];	//풍향
                        airconState[i+1][5] = data[tempidx + 5];	//풍량
                        airconState[i+1][6] = data[tempidx + 6];	//온도 단위
                        airconState[i+1][7] = data[tempidx + 7];	//온도 min
                        airconState[i+1][8] = data[tempidx + 8];	//온도 max
                        airconState[i+1][9] = data[tempidx + 9];	//reserve
                    }
                }

                //일괄차단 에러발생
                if(airconState[0][0] != 0)	//에러가 발생하면 (0이아닌값이 들어오는경우)
                {
                    AirconerrorCode       	= EncodeUtil.DecimalStringFromCharacter(airconState[0][0]);
                    ErrorFlag = true;
                }
                //정상
                else
                {
                    ErrorFlag = false;
                }


            }
            else
            {
                nAirconCnt = 0;
            }

            heatGroupCnt = new byte[1][4];

            //Buffer Length Check
            if(data.length >= (nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + nAirconCnt*10 + 1)) {
                heatGroupCnt[0][0] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt * 1 + 2 + nLightCnt * 1 + 1 + nDimCnt * 1 + 2 + nStandbyCnt * 10 + 2 + nPowershutCnt * 2 + 3 + 3 + 2 + nAirconCnt * 10 + 1 ];    //난방 그룹 개수
                Log.e("heatGroupCnt[0][0]", heatGroupCnt[0][0] + "//");
                heatGroupCnt[0][1] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt * 1 + 2 + nLightCnt * 1 + 1 + nDimCnt * 1 + 2 + nStandbyCnt * 10 + 2 + nPowershutCnt * 2 + 3 + 3 + 2 + nAirconCnt * 10 + 1 + 1];    //난방 그룹별 방개수 1
                Log.e("heatGroupCnt[0][1]", heatGroupCnt[0][1] + "//");
                heatGroupCnt[0][2] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt * 1 + 2 + nLightCnt * 1 + 1 + nDimCnt * 1 + 2 + nStandbyCnt * 10 + 2 + nPowershutCnt * 2 + 3 + 3 + 2 + nAirconCnt * 10 + 1 + 1 + 1];    //난방 그룹별 방개수 2
                Log.e("heatGroupCnt[0][2]", heatGroupCnt[0][2] + "//");
                heatGroupCnt[0][3] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt * 1 + 2 + nLightCnt * 1 + 1 + nDimCnt * 1 + 2 + nStandbyCnt * 10 + 2 + nPowershutCnt * 2 + 3 + 3 + 2 + nAirconCnt * 10 + 1 + 1 + 2];    //난방 그룹별 방개수 3
                Log.e("heatGroupCnt[0][3]", heatGroupCnt[0][3] + "//");
            }

            ventState = new byte[20][10];

            if(data.length > (nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 1)) {
                int ventCnt = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 1];
                ventState[0][1] = (byte) ventCnt;
                if(ventCnt != 0){
                    for(int i = 0; i < ventCnt; i++){
                        ventState[i+1][1] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 2 + 9 * i];
                        ventState[i+1][2] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 2 + 9 * i + 1];
                        ventState[i+1][3] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 2 + 9 * i + 2];
                        ventState[i+1][4] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 2 + 9 * i + 3];
                        ventState[i+1][5] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 2 + 9 * i + 4];
                        ventState[i+1][6] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 2 + 9 * i + 5];
                        ventState[i+1][7] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 2 + 9 * i + 6];
                        ventState[i+1][8] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 2 + 9 * i + 7];
                        ventState[i+1][9] = data[nHeaderSize + 2 + nRoomCnt * 3 + 2 + nGasCnt*1 + 2 + nLightCnt*1 + 1 + nDimCnt*1 + 2 + nStandbyCnt*10 + 2 + nPowershutCnt*2 + 3 + 3 + 2 + 1 + nAirconCnt*10 + 3 + 2 + 9 * i + 8];

                        for(int k = 1; k <= 9; k++){
                            Log.e("ventState["+(i+1)+"]["+k+"]", ventState[i+1][k] + "//");
                        }
                    }
                }
            }

            completeFlag = true;
            result = true;

        }

        catch (Exception e) {
            completeFlag = false;
            result = false;
            e.printStackTrace();
        }
        finally {
            return result;
        }
    }

    public void hexstringFrombinarystringFirst(String string) {

        bUseHeatFunc     	= false;
        bUseGasFunc  	 	= false;
        bUseLightFunc 	 	= false;
        bUseHotWaterFunc 	= false;

        if(string.equals("0")) {
            bUseHeatFunc     = false;bUseGasFunc  	 = false;bUseLightFunc 	 = false;bUseHotWaterFunc = false;
        }
        if(string.equals("1")) {
            bUseHeatFunc     = false;bUseGasFunc  	 = false;bUseLightFunc 	 = false;bUseHotWaterFunc = true;
        }
        if(string.equals("2")) {
            bUseHeatFunc     = false;bUseGasFunc  	 = false;bUseLightFunc 	 = true;bUseHotWaterFunc = false;
        }
        if(string.equals("3")) {
            bUseHeatFunc     = false;bUseGasFunc  	 = false;bUseLightFunc 	 = true;bUseHotWaterFunc = true;
        }
        if(string.equals("4")) {
            bUseHeatFunc     = false;bUseGasFunc  	 = true;bUseLightFunc 	 = false;bUseHotWaterFunc = false;
        }
        if(string.equals("5")) {
            bUseHeatFunc     = false;bUseGasFunc  	 = true;bUseLightFunc 	 = false;bUseHotWaterFunc = true;
        }
        if(string.equals("6")) {
            bUseHeatFunc     = false;bUseGasFunc  	 = true;bUseLightFunc 	 = true;bUseHotWaterFunc = false;
        }
        if(string.equals("7")) {
            bUseHeatFunc     = false;bUseGasFunc  	 = true;bUseLightFunc 	 = true;bUseHotWaterFunc = true;
        }
        if(string.equals("8")) {
            bUseHeatFunc     = true;bUseGasFunc  	 = false;bUseLightFunc 	 = false;bUseHotWaterFunc = false;
        }
        if(string.equals("9")) {
            bUseHeatFunc     = true;bUseGasFunc  	 = false;bUseLightFunc 	 = false;bUseHotWaterFunc = true;
        }
        if(string.equals("A") || string.equals("a")) {
            bUseHeatFunc     = true;bUseGasFunc  	 = false;bUseLightFunc 	 = true;bUseHotWaterFunc = false;
        }
        if(string.equals("B") || string.equals("b")) {
            bUseHeatFunc     = true;bUseGasFunc  	 = false;bUseLightFunc 	 = true;bUseHotWaterFunc = true;
        }
        if(string.equals("C") || string.equals("c")) {
            bUseHeatFunc     = true;bUseGasFunc  	 = true;bUseLightFunc 	 = false;bUseHotWaterFunc = false;
        }
        if(string.equals("D") || string.equals("d")) {
            bUseHeatFunc     = true;bUseGasFunc  	 = true;bUseLightFunc 	 = false;bUseHotWaterFunc = true;
        }
        if(string.equals("E") || string.equals("e")) {
            bUseHeatFunc     = true;bUseGasFunc  	 = true;bUseLightFunc 	 = true;bUseHotWaterFunc = false;
        }
        if(string.equals("F") || string.equals("f")) {
            bUseHeatFunc     = true;bUseGasFunc  	 = true;bUseLightFunc 	 = true;bUseHotWaterFunc = true;
        }
    }

    public void hexstringFrombinarystringSecond(String string) {

        bUseMinorityFunc    = false;
        bUseHomeviewHwFunc 	= false;
        bUseHomeviewSwFunc 	= false;
        bUseStandbyFunc  	= false;

        if(string.equals("0")) {
            bUseMinorityFunc    = false;
            bUseHomeviewHwFunc 	= false;
            bUseHomeviewSwFunc 	= false;
            bUseStandbyFunc  	= false;
        }
        if(string.equals("1")) {
            bUseMinorityFunc    = false;
            bUseHomeviewHwFunc 	= false;
            bUseHomeviewSwFunc 	= false;
            bUseStandbyFunc  	= true;
        }
        if(string.equals("2")) {
            bUseMinorityFunc    = false;
            bUseHomeviewHwFunc 	= false;
            bUseHomeviewSwFunc 	= true;
            bUseStandbyFunc  	= false;
        }
        if(string.equals("3")) {
            bUseMinorityFunc    = false;
            bUseHomeviewHwFunc 	= false;
            bUseHomeviewSwFunc 	= true;
            bUseStandbyFunc  	= true;
        }
        if(string.equals("4")) {
            bUseMinorityFunc    = false;
            bUseHomeviewHwFunc 	= true;
            bUseHomeviewSwFunc 	= false;
            bUseStandbyFunc  	= false;
        }
        if(string.equals("5")) {
            bUseMinorityFunc    = false;
            bUseHomeviewHwFunc 	= true;
            bUseHomeviewSwFunc 	= false;
            bUseStandbyFunc  	= true;
        }
        if(string.equals("6")) {
            bUseMinorityFunc    = false;
            bUseHomeviewHwFunc 	= true;
            bUseHomeviewSwFunc 	= true;
            bUseStandbyFunc  	= false;
        }
        if(string.equals("7")) {
            bUseMinorityFunc    = false;
            bUseHomeviewHwFunc 	= true;
            bUseHomeviewSwFunc 	= true;
            bUseStandbyFunc  	= true;
        }
        if(string.equals("8")) {
            bUseMinorityFunc    = true;
            bUseHomeviewHwFunc 	= false;
            bUseHomeviewSwFunc 	= false;
            bUseStandbyFunc  	= false;
        }
        if(string.equals("9")) {
            bUseMinorityFunc    = true;
            bUseHomeviewHwFunc 	= false;
            bUseHomeviewSwFunc 	= false;
            bUseStandbyFunc  	= true;
        }
        if(string.equals("A") || string.equals("a")) {
            bUseMinorityFunc    = true;
            bUseHomeviewHwFunc 	= false;
            bUseHomeviewSwFunc 	= true;
            bUseStandbyFunc  	= false;
        }
        if(string.equals("B") || string.equals("b")) {
            bUseMinorityFunc    = true;
            bUseHomeviewHwFunc 	= false;
            bUseHomeviewSwFunc 	= true;
            bUseStandbyFunc  	= true;
        }
        if(string.equals("C") || string.equals("c")) {
            bUseMinorityFunc    = true;
            bUseHomeviewHwFunc 	= true;
            bUseHomeviewSwFunc 	= false;
            bUseStandbyFunc  	= false;
        }
        if(string.equals("D") || string.equals("d")) {
            bUseMinorityFunc    = true;
            bUseHomeviewHwFunc 	= true;
            bUseHomeviewSwFunc 	= false;
            bUseStandbyFunc  	= true;
        }
        if(string.equals("E") || string.equals("e")) {
            bUseMinorityFunc    = true;
            bUseHomeviewHwFunc 	= true;
            bUseHomeviewSwFunc 	= true;
            bUseStandbyFunc  	= false;
        }
        if(string.equals("F") || string.equals("f")) {
            bUseMinorityFunc    = true;
            bUseHomeviewHwFunc 	= true;
            bUseHomeviewSwFunc 	= true;
            bUseStandbyFunc  	= true;
        }
    }

    public void hexstringFrombinarystringThird(String string) {

        bUsePowershutLightFunc  = false;
        bUsePowershutGasFunc    = false;
        bUseHomeviewVideoFunc	= false;
        bUseGooutFunc			= false;

        if(string.equals("0")) {
            bUsePowershutLightFunc  = false;
            bUseHomeviewVideoFunc	= false;
            bUsePowershutGasFunc    = false;
            bUseGooutFunc			= false;

        }
        if(string.equals("1")) {
            bUsePowershutLightFunc  = false;
            bUseHomeviewVideoFunc	= false;
            bUsePowershutGasFunc    = false;
            bUseGooutFunc			= true;
        }
        if(string.equals("2")) {
            bUsePowershutLightFunc  = false;
            bUseHomeviewVideoFunc	= false;
            bUsePowershutGasFunc    = true;
            bUseGooutFunc			= false;
        }
        if(string.equals("3")) {
            bUsePowershutLightFunc  = false;
            bUseHomeviewVideoFunc	= false;
            bUsePowershutGasFunc    = true;
            bUseGooutFunc			= true;
        }
        if(string.equals("4")) {
            bUsePowershutLightFunc  = false;
            bUseHomeviewVideoFunc	= true;
            bUsePowershutGasFunc    = false;
            bUseGooutFunc			= false;
        }
        if(string.equals("5")) {
            bUsePowershutLightFunc  = false;
            bUseHomeviewVideoFunc	= true;
            bUsePowershutGasFunc    = false;
            bUseGooutFunc			= true;
        }
        if(string.equals("6")) {
            bUsePowershutLightFunc  = false;
            bUseHomeviewVideoFunc	= true;
            bUsePowershutGasFunc    = true;
            bUseGooutFunc			= false;
        }
        if(string.equals("7")) {
            bUsePowershutLightFunc  = false;
            bUseHomeviewVideoFunc	= true;
            bUsePowershutGasFunc    = true;
            bUseGooutFunc			= true;
        }
        if(string.equals("8")) {
            bUsePowershutLightFunc  = true;
            bUseHomeviewVideoFunc	= false;
            bUsePowershutGasFunc    = false;
            bUseGooutFunc			= false;
        }
        if(string.equals("9")) {
            bUsePowershutLightFunc  = true;
            bUseHomeviewVideoFunc	= false;
            bUsePowershutGasFunc    = false;
            bUseGooutFunc			= true;
        }
        if(string.equals("A") || string.equals("a")) {
            bUsePowershutLightFunc  = true;
            bUseHomeviewVideoFunc	= false;
            bUsePowershutGasFunc    = true;
            bUseGooutFunc			= false;
        }
        if(string.equals("B") || string.equals("b")) {
            bUsePowershutLightFunc  = true;
            bUseHomeviewVideoFunc	= false;
            bUsePowershutGasFunc    = true;
            bUseGooutFunc			= true;
        }
        if(string.equals("C") || string.equals("c")) {
            bUsePowershutLightFunc  = true;
            bUseHomeviewVideoFunc	= true;
            bUsePowershutGasFunc    = false;
            bUseGooutFunc			= false;
        }
        if(string.equals("D") || string.equals("d")) {
            bUsePowershutLightFunc  = true;
            bUseHomeviewVideoFunc	= true;
            bUsePowershutGasFunc    = false;
            bUseGooutFunc			= true;
        }
        if(string.equals("E") || string.equals("e")) {
            bUsePowershutLightFunc  = true;
            bUseHomeviewVideoFunc	= true;
            bUsePowershutGasFunc    = true;
            bUseGooutFunc			= false;
        }
        if(string.equals("F") || string.equals("f")) {
            bUsePowershutLightFunc  = true;
            bUseHomeviewVideoFunc	= true;
            bUsePowershutGasFunc    = true;
            bUseGooutFunc			= true;
        }
    }

    public void hexstringFrombinarystringFourth(String string) {

        bUseDoorFunc  = false;

        if(string.equals("0")) {
            bUseDoorFunc  = false;	bUseAirconFunc = false;
        }
        if(string.equals("1")) {
            bUseDoorFunc  = false;	bUseAirconFunc = false;
        }
        if(string.equals("2")) {
            bUseDoorFunc  = false;	bUseAirconFunc = false;
        }
        if(string.equals("3")) {
            bUseDoorFunc  = false;	bUseAirconFunc = false;
        }
        if(string.equals("4")) {
            bUseDoorFunc  = false; bUseAirconFunc = true;
        }
        if(string.equals("5")) {
            bUseDoorFunc  = false;	bUseAirconFunc = true;
        }
        if(string.equals("6")) {
            bUseDoorFunc  = false;	bUseAirconFunc = true;
        }
        if(string.equals("7")) {
            bUseDoorFunc  = false;	bUseAirconFunc = true;
        }
        if(string.equals("8")) {
            bUseDoorFunc  = true;	bUseAirconFunc = false;
        }
        if(string.equals("9")) {
            bUseDoorFunc  = true;	bUseAirconFunc = false;
        }
        if(string.equals("A") || string.equals("a")) {
            bUseDoorFunc  = true;	bUseAirconFunc = false;
        }
        if(string.equals("B") || string.equals("b")) {
            bUseDoorFunc  = true;	bUseAirconFunc = false;
        }
        if(string.equals("C") || string.equals("c")) {
            bUseDoorFunc  = true;	bUseAirconFunc = true;
        }
        if(string.equals("D") || string.equals("d")) {
            bUseDoorFunc  = true;	bUseAirconFunc = true;
        }
        if(string.equals("E") || string.equals("e")) {
            bUseDoorFunc  = true;	bUseAirconFunc = true;
        }
        if(string.equals("F") || string.equals("f")) {
            bUseDoorFunc  = true;	bUseAirconFunc = true;
        }
    }

    public boolean isAvailable()
    {
        return completeFlag;
    }

    public boolean isErrorApper()
    {
        return ErrorFlag;
    }
    //난방 에러 전달
    public String HeatErrordescription()
    {
        return HeaterrorCode;
    }
    //가스 에러 전달
    public String GasErrordescription()
    {
        return GaserrorCode;
    }
    //조명 에러 전달
    public String LightErrordescription()
    {
        return LighterrorCode;
    }
    //대기전력 에러 전달
    public String StandbyErrordescription()
    {
        return StandbyerrorCode;
    }
    //일괄차단 에러 전달
    public String PowershutErrordescription()
    {
        return PowershuterrorCode;
    }
    //에어컨 에러 전달
    public String AirconErrordescription()
    {
        return AirconerrorCode;
    }
}
