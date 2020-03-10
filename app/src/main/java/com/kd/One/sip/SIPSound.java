package com.kd.One.sip;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class SIPSound {

    static int BUFFER_COUNT					= 3;
    static int BUFFER_SIZE					= 8000;
    static int DMA_SIZE						= 800;
    static int BUFFER_PLAY_INDEX			= 0;
    static int BUFFER_BUFFERREDSIZE			= 0;
    static byte[] captureBuffer 			= null;
    static byte[] dmaBuffer					= null;
    static short[] dma729Buffer				= null;
    static byte[] comportableNoise			= null;
    static int PTIME						= 20;

    boolean 			running				= false;
    Thread 			captureThread			= null;
    //2012 01 07
    static boolean 			recorderStarted	= false;
    public AudioRecord 		recorder		= null;


    //PLAY DEFINITION
    public AudioTrack 		track				= null;
    static boolean 			playStarted			= false;
    int 				minSize					= 0;
    int 				databufferredSize		= 0;
    //2012 02 13
    static int CONF_RECV_BUFFER_SIZE			= 8000;
    static int CONF1_RECV_PLAY_INDEX			= 0;
    static int CONF1_RECV_BUFFERREDSIZE			= 0;
    static byte conf1RecvBuffer[] 				= new byte[CONF_RECV_BUFFER_SIZE];
    static int CONF2_RECV_PLAY_INDEX			= 0;
    static int CONF2_RECV_BUFFERREDSIZE			= 0;
    static byte conf2RecvBuffer[] 				= new byte[CONF_RECV_BUFFER_SIZE];
    //2012 04 27
    static int	SOUND_TYPE_DEFAULT				= 0;
    static int	SOUND_TYPE_TELEPHONE			= 1;
    static int	SOUND_TYPE_MUSIC				= 2;
    static AudioManager amAudioManager			= null;
    static int	BSS_SOUND_MODE					= SOUND_TYPE_TELEPHONE;
    static boolean bSpeakerOn 					= false;
    //2012 03 22
    boolean bActive								= false;
    boolean bCaptureBufferLock					= false;
    //2012 03 24
    int	flowIndicator							= SIPStack.SIP_MEDIAFLOW_SENDRECV;
    //2012 12 05
    static boolean CAPTURE_DATA_TYPE_IS_BYTE	= false; //false:short true:byte
    static int 			remotePayloadType		= SIPStack.SIP_CODEC_NONE;
    byte[] lineardata							= new byte[320];

    public static boolean bMute					= false;
    public static boolean bAutoMute				= false;
    public boolean bTrackWritten				= false;
    public static float autoMuteCutLine			= 1700;

    public static double lastLevel				= 0;
    public static int muteCount					= 0;


    public SIPSound() //throws LineUnavailableException
    {
        BUFFER_PLAY_INDEX		= 0;
        BUFFER_BUFFERREDSIZE	= 0;
        bActive				= false;
        DMA_SIZE = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        dmaBuffer		= new byte[DMA_SIZE];
        dma729Buffer		= new short[DMA_SIZE/2];

        BUFFER_SIZE						= DMA_SIZE;
        captureBuffer 						= new byte[BUFFER_SIZE];
        comportableNoise					= new byte[BUFFER_SIZE];
        SIPG711.getPCMLinearComportableNoise(comportableNoise, BUFFER_SIZE);

		/*
		 amAudioManager.setRouting(AudioManager.MODE_CURRENT,
	               AudioManager.ROUTE_EARPIECE,
	               AudioManager.ROUTE_ALL);
		 amAudioManager.setRouting(AudioManager.MODE_NORMAL,
	               AudioManager.ROUTE_EARPIECE,
	               AudioManager.ROUTE_ALL);
		 amAudioManager.setRouting(AudioManager.MODE_RINGTONE,
	               AudioManager.ROUTE_SPEAKER, //ROUTE_EARPIECE
	               AudioManager.ROUTE_ALL);

		 amAudioManager.setRouting(AudioManager.MODE_IN_CALL,
	               AudioManager.ROUTE_EARPIECE,
	               AudioManager.ROUTE_ALL);
		 */

        //audioFlinger를 초기화 함. 수신음악 이나 다이얼톤이 안들리는 현상
        //이 생겨서 설정했는데 효과가 있어, 관련이 있다고 생각함.
        amAudioManager.setMode(AudioManager.MODE_NORMAL);//2014 09 30
        bSpeakerOn 					= false;
        bTrackWritten=false;
        track=null;
        recorder=null;
    }

    public void resetDevice()
    {
        try
        {
            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>RESETDEVICE ++++++++++");
            if(track!=null){
                playStarted 		= false;
                track.pause();
                track.stop();
                track.release();
                track=null;
            }
            if(recorder!=null)
            {
                recorderStarted=false;
                recorder.stop();
                recorder.release();
                recorder=null;
            }
        }catch(Exception e){}

        amAudioManager.setMode(AudioManager.MODE_NORMAL);//2014 09 30
        amAudioManager.setSpeakerphoneOn(false);
        bSpeakerOn 					= false;

    }

    public void captureAudio() //throws LineUnavailableException
    {
        if(recorder!=null)
        {
            recorderStarted = false;
            recorder.stop();
            recorder.release();
            recorder=null;
        }

        if (!recorderStarted) {



            captureThread = new Thread() {
                //@Override
                @SuppressLint("NewApi")
                public void run() {




                    if(BSS_SOUND_MODE == SOUND_TYPE_TELEPHONE)
                    {
                        //recorder = new AudioRecord(AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_MONO, //work normal
                        //		AudioFormat.ENCODING_PCM_16BIT, DMA_SIZE ); //2012 09 27
                        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, //work normal
                                AudioFormat.ENCODING_PCM_16BIT, DMA_SIZE ); //2012 09 27
                    }
                    else {
                        //recorder = new AudioRecord(AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_MONO, //work normal
                        //		AudioFormat.ENCODING_PCM_16BIT, DMA_SIZE ); //2012 09 27
                        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, //work normal
                                AudioFormat.ENCODING_PCM_16BIT, DMA_SIZE ); //2012 09 27
                    }

                    if(recorder != null) //2012 07 20
                    {
                        try
                        {
                            //int frames=DMA_SIZE/2;
                            int frames=160;
                            recorder.setPositionNotificationPeriod(frames);//short 단위:800

                            recorder.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
                                //@Override
                                public void onPeriodicNotification(AudioRecord recorder) {
                                    if(recorderStarted==false) return;//2012 01 10
                                    if(CAPTURE_DATA_TYPE_IS_BYTE==true)
                                    {
                                        int count=recorder.read(dmaBuffer, 0, 320);//byte단위:DMA_SIZE 1600

                                        if (count > 0) {
                                            bufferring(dmaBuffer,count);
                                        }


                                    }
                                    else {
                                        int count=recorder.read(dma729Buffer, 0, 160);//byte단위:DMA_SIZE 1600

                                        //playAudio(dma729Buffer,count);

                                        if(bAutoMute == true)
                                        {
                                            double Level = 0;
                                            double sumLevel = 0;

                                            for (int i = 0; i < 160; i++)
                                            {
                                                sumLevel += dma729Buffer [i] * dma729Buffer [i];
                                            }


                                            if (160 > 0) {
                                                Level = sumLevel / 160;
                                            }

                                            Log.e("out stream", "cutline : "+ autoMuteCutLine + " Mute : "+Level);

                                            if(bMute)
                                            {
                                                short[] mute = new short[DMA_SIZE/2];
                                                bufferring(mute,count);
                                            }
                                            else
                                            {
                                                bufferring(dma729Buffer,count);
                                            }

											/*
											if(Level >= 50000)
											{
												bufferring(dma729Buffer,count);
											}
											else
											{
												if(bMute)
												{
													short[] mute = new short[DMA_SIZE/2];
													bufferring(mute,count);
												}
												else
												{
													bufferring(dma729Buffer,count);
												}
											}
											*/
                                        }

                                        else
                                        {
                                            if(bMute)
                                            {
                                                short[] mute = new short[DMA_SIZE/2];
                                                bufferring(mute,count);
                                            }
                                            else
                                            {
                                                bufferring(dma729Buffer,count);
                                            }
                                        }



                                    }
                                    //2014 12 04
                                    if(bTrackWritten==false)
                                    {
                                        playAudio(comportableNoise,320);
                                        //System.out.println("not play...");
                                    }
                                    //

                                }

                                //@Override
                                public void onMarkerReached(AudioRecord recorder) {
                                }
                            });
                            recorder.startRecording();

                            if(CAPTURE_DATA_TYPE_IS_BYTE==true)
                            {
                                recorder.read(dmaBuffer, 0, frames*2);
                            }
                            else {
                                recorder.read(dma729Buffer, 0, frames);
                            }

                        }catch(IllegalStateException e){
                            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>CAPTUREAUDIO ++++++++++EXCEPTION COMPLETE");
                            if(track!=null && playStarted==true)
                            {
                                playStarted=false;
                            }
                            if(recorder!=null && recorderStarted==true)
                            {
                                recorderStarted = false;
                            }
                            bActive=false;//2014 06 05
                            return;
                        }
                        catch(Exception e1){
                            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>CAPTUREAUDIO ++++++++++EXCEPTION COMPLETE");
                            if(track!=null && playStarted==true)
                            {
                                playStarted=false;
                            }
                            if(recorder!=null && recorderStarted==true)
                            {
                                recorderStarted = false;
                            }
                            bActive=false;//2014 06 05
                            return;
                        }
                        try {
                            while (true) {
                                if (isInterrupted()) {
                                    if(track!=null)// && playStarted==true)
                                    {
                                        playStarted=false;
                                        track.stop();
                                        track.release();
                                        track=null;

                                        //System.out.println("<<< audio track end.");
                                    }
                                    //Thread.sleep(1000);
                                    if(recorder!=null)
                                    {
                                        recorderStarted = false;
                                        recorder.stop();
                                        recorder.release();
                                        recorder=null;
                                        //System.out.println("<<< audio recorder end.");
                                    }
                                    //Thread.sleep(1000);
                                    break;
                                }
                            }
                        }
                        catch (Exception e){
                            SIPStack.exceptionCountAtCurrentCall++;
                            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>CAPTUREAUDIO ++++++++++EXCETPION COMPLETE");
                        }
                        if(track!=null && playStarted==true)
                        {
                            playStarted=false;
                        }
                        if(recorder!=null && recorderStarted)
                        {
                            recorderStarted = false;
                        }
                        bActive=false;
                        //System.out.println("<<< audio capture end.");
                    }
                    else {
                        //System.out.println("SIPSound Error: recorder is null.  capture end.");
                    }//

                }
            };
            captureThread.start();

            recorderStarted = true;
        }
    }


    public void writeAudioDataToFile(byte[] sData, int length, boolean toPhone) {
        // Write the output audio in byte

        //int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
        String filePath = "";
        if(toPhone) {
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp1.pcm";
        }else{
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp2.pcm";
        }
        // short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filePath, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        try {
            os.write(sData, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/*public void writeAudioDataToFile(byte[] sData, int length, boolean flag) {
		// Write the output audio in byte

		//int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024

		String filePath = filename + "1.pcm";
		// short sData[] = new short[BufferElements2Rec];

		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filePath, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


		try {
			os.write(sData, 0, length);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    public void closeAudioDevice() {
        Log.i("SIPSound","closeAudioDevice");
        try
        {
            if (recorder!=null)// && recorderStarted)
            {
                recorderStarted = false;
                recorder.stop();

                if (captureThread != null && captureThread.isAlive() && !captureThread.isInterrupted()) {
                    captureThread.interrupt();
                }

                if(recorder!=null)
                    recorder.release();
                recorder=null;
            }


            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>CLOSEAUDIODEVICE CAPTURE++++++++++END");

        }catch(Exception e){
            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>CLOSEAUDIODEVICE CAPTURE++++++++++EXCEPTION");
        }

        try
        {
            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>CLOSEAUDIODEVICE TRACK++++++++++");

            if (track!=null /*&& playStarted*/)
            {
                playStarted=false;
                track.pause();
            }
            else if(track!=null )//&& track.getState()==AudioTrack.PLAYSTATE_PLAYING)
            {
                track.pause();
            }

            if(track!=null) track.release();
            track=null;

            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>CLOSEAUDIODEVICE TRACK++++++++++END");

            //if(amAudioManager!=null) amAudioManager.setMode(AudioManager.MODE_NORMAL);
        }catch(Exception e){
            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>CLOSEAUDIODEVICE TRACK++++++++++EXCEPTION");
        }

        //2014 10 01
        if(amAudioManager!=null) {
            amAudioManager.setMode(AudioManager.MODE_NORMAL);
            amAudioManager.setSpeakerphoneOn(false);
            bSpeakerOn 					= false;

        }
        //
        return;
    }
    public void closeCaptureDevice() {
        try
        {
            if (recorder!=null)
            {
                recorderStarted = false;
                recorder.stop();
                if (captureThread != null && captureThread.isAlive() && !captureThread.isInterrupted()) {
                    captureThread.interrupt();
                }
                if(recorder!=null) recorder.release();
                recorder=null;
            }


        }catch(Exception e){
            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>CLOSEAUDIODEVICE CAPTURE++++++++++EXCEPTION");
        }
        //2014 10 01
        if(amAudioManager!=null) {
            amAudioManager.setMode(AudioManager.MODE_NORMAL);
            amAudioManager.setSpeakerphoneOn(false);
            bSpeakerOn 					= false;

        }
        //
        return;
    }
    public void prepareAudioTrack() {
        bTrackWritten=false;

        if(track!=null && playStarted==true) {
            playStarted=false;
            track.stop();
            track.flush();
            track.release();
            track = null;
        }

        try
        {
            Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>PREPAREAUDIOTRACK ++++++++++");
            if(playStarted==true) return;//2014 06 05
            //if(amAudioManager!=null) amAudioManager.setMode(AudioManager.MODE_IN_CALL);

            //bulk case
            minSize = AudioTrack.getMinBufferSize( 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT );
            int playbufferSize=minSize;//((minSize/320)+1)*320;

            System.out.println("AUDIO minSize:"+minSize);

			/*
			String myVersion = android.os.Build.VERSION.RELEASE;

			String[] result = myVersion.split(".");


			float version = 0;
			if(myVersion.length() <= 4)
			{
				version = Float.parseFloat(myVersion);
			}
			else
			{
				version = Float.parseFloat(myVersion.substring(0, 3));
			}
			*/
            if(playbufferSize < 0)
            {
                playbufferSize = 320;
            }

            track = new AudioTrack( AudioManager.STREAM_VOICE_CALL , 8000,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    playbufferSize, AudioTrack.MODE_STREAM);	//original

            //Log.e("track", "5.1" + myVersion);

            track.setStereoVolume(track.getMaxVolume(), track.getMaxVolume());

			/*
			if(version >= (float)5.1){
			    //this code will be executed on devices running on DONUT (NOT ICS) or later
				track = new AudioTrack( AudioManager.STREAM_MUSIC , 8000,
						AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
						playbufferSize, AudioTrack.MODE_STREAM);	//original

				Log.e("track", "5.1" + myVersion);

				track.setStereoVolume(track.getMaxVolume(), track.getMaxVolume());
			}
			else
			{
				track = new AudioTrack( AudioManager.STREAM_VOICE_CALL, 8000,
						AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
						playbufferSize, AudioTrack.MODE_STREAM);	//original
				Log.e("track", "music" + myVersion);

				track.setStereoVolume(track.getMaxVolume(), track.getMaxVolume());
			}
			*/

			/*
			if(BSS_SOUND_MODE == SOUND_TYPE_TELEPHONE)
			{
					track = new AudioTrack( AudioManager.STREAM_MUSIC, 8000,
							AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
							playbufferSize, AudioTrack.MODE_STREAM);	//original
				track.setStereoVolume(track.getMaxVolume(), track.getMaxVolume());
			}
			else {
				track = new AudioTrack( AudioManager.STREAM_MUSIC, 8000,
						AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
						playbufferSize, AudioTrack.MODE_STREAM);

				track.setStereoVolume(track.getMaxVolume(), track.getMaxVolume());
			}
			*/
            if(track!=null) {
                //2014 12 05
                track.play();
                //
                playStarted=true;
                bActive=true;
            }
            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>PREPAREAUDIOTRACK ++++++++++END");
        }catch(Exception e)
        {
            //Log.i("SOUND",">>>>>>>>>>>>>>>>>>>>>PREPAREAUDIOTRACK ++++++++++EXCEPTION");
        }
        //
        return;

    }
    public void playAudio(byte[] data,int dataSize) {

        try
        {
            if(track!=null && playStarted==true && recorderStarted==true)
            {
                //2012 03 22

                if(bActive==false) {
                    System.out.println("Abnormal track play. must be stopped.");
                    track.stop();
                    track.release();
                    track=null;
                    return;
                }

                try {
                    if(bTrackWritten==false)
                        bTrackWritten=true;

                    //AEC
					/*
					if(bAutoMute == true)
					{
						//double lastLevel = 0;
						double sumLevel = 0;

						for (int i = 0; i < dataSize; i++)
						{
							sumLevel += data [i] * data [i];
						}


						if (dataSize > 0) {
							lastLevel = sumLevel / dataSize;
						}


						if(lastLevel >= autoMuteCutLine)
						{
							//Log.e("in stream", "cutline : "+ autoMuteCutLine + " Mute : "+lastLevel);
							//Log.e("in stream", "Mute : "+lastLevel);
							amAudioManager.setMicrophoneMute(true);

							muteCount = 10;

							setMute(true);
						}
						else
						{
							//Log.e("in stream", "lavel : "+lastLevel);

							muteCount--;
							if(muteCount <= 0)
							{
								amAudioManager.setMicrophoneMute(false);
								setMute(false);
							}
						}

					}
					*/

                    if(	track!=null && //2012 03 24
                            track.getPlayState()!=AudioTrack.PLAYSTATE_PLAYING && track.getState()==AudioTrack.STATE_INITIALIZED )
                    {
                        track.play();
                    }

                    if(track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
                    {
                        track.flush();

                        /*String str = "";
                        for(byte b : data){
							str += b;
						}
						Log.e("audio11", String.format("dp : %d, buffer : %s", HomeviewActivity.arrayAudio.size(), str));*/

                        int res = track.write(data, 0, dataSize);

                        track.stop();
                    }
                } catch (Exception e){
                    System.err.println(e);
                }

            }
        }catch(Exception e){}
        //아래 warning 연구결과: 2012 01 11
        //AudioTrack obtainBuffer() track 0x1bc5c8 disabled, restarting
        //Warning
        //track.write()가 일어나지 않는경우 발생함.
        //경고로서 응용프로그램의 중지와는 치명적 원인은 안되는것으로 보임.
        //안드로이드 Flinger Process의 프로세스 설계의 오류로 판단함
        //주식회사 광해소프트
        //
        return;
    }
    public void playAudio(short[] data,int dataSize) {
        try
        {
            //
            if(track!=null && playStarted==true && recorderStarted==true)
            {
                //2012 03 22

                if(bActive==false) {
                    System.out.println("Abnormal track play. must be stopped.");
                    track.stop();
                    track.release();
                    track=null;
                    return;
                }

                //
                try {
                    if(bTrackWritten==false) bTrackWritten=true;
                    if(
                            track!=null &&
                                    track.getPlayState()!=AudioTrack.PLAYSTATE_PLAYING &&
                                    track.getState()==AudioTrack.STATE_INITIALIZED
                    )
                    {
                        track.play();
                    }
                    if(track.getPlayState() != track.PLAYSTATE_PLAYING)
                    {
                        track.play();
                    }
                    int res = track.write(data, 0, dataSize);
                    Log.e("play", "play : " + res);
                } catch (Exception e){
                    System.err.println(e);
                }

            }
        }catch(Exception e){}
        //아래 warning 연구결과: 2012 01 11
        //AudioTrack obtainBuffer() track 0x1bc5c8 disabled, restarting
        //Warning
        //track.write()가 일어나지 않는경우 발생함.
        //경고로서 응용프로그램의 중지와는 치명적 원인은 안되는것으로 보임.
        //안드로이드 Flinger Process의 프로세스 설계의 오류로 판단함
        //주식회사 광해소프트
        //
        return;
    }

    public void bufferring(byte[] data,int datasize)
    {
        try
        {
            //2012 03 12
            bCaptureBufferLock=true;
            if(data==null || datasize<=0) {
                bCaptureBufferLock=false;
                return;
            }


            if(BUFFER_BUFFERREDSIZE+datasize > BUFFER_SIZE)
            {
                BUFFER_PLAY_INDEX	= 0;
                BUFFER_BUFFERREDSIZE= 0;
            }
            int savePos= (BUFFER_PLAY_INDEX+BUFFER_BUFFERREDSIZE) % BUFFER_SIZE;
            int bufferTrail=BUFFER_SIZE-savePos;
            if(bufferTrail < datasize)
            {
                System.arraycopy(data,0,captureBuffer,savePos,bufferTrail);
                System.arraycopy(data,bufferTrail,captureBuffer,0,datasize-bufferTrail);
            }
            else
            {
                System.arraycopy(data,0,captureBuffer,savePos,datasize);
            }
            BUFFER_BUFFERREDSIZE+=datasize;
            bCaptureBufferLock=false;//2012 03 22

            //2012 03 24
            if(flowIndicator == SIPStack.SIP_MEDIAFLOW_SENDONLY)
            {
                playAudio(comportableNoise,datasize);
            }
            //
        }catch(Exception e){}
        return;
    }

    public void bufferring(short[] data,int datasize)
    {
        try
        {
            bCaptureBufferLock=true;
            if(data==null || datasize<=0) {
                bCaptureBufferLock=false;
                return;
            }

            int packetsize=0;

            //
			/*

			short[] aecTmpOut = new short[inData.length/2];
			short[] aecTmpIn = new short[inData.length/2];
			//ByteBuffer.wrap(inData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(aecTmpOut);
			ByteBuffer.wrap(inData).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(aecTmpOut);

			MobileAEC aec = new MobileAEC();

			System.arraycopy(data, 0, aecTmpIn, 0, aecTmpOut.length);

			aec.speexACEopen(8000, aecTmpIn.length, aecTmpIn.length);
			Log.e("AEC", "aecTmpOut :"+aecTmpOut.length);
			short[] output = aec.speexACEdo(aecTmpIn, aecTmpOut);

			System.arraycopy(output, 0, data, 0, output.length);
			datasize = output.length;
			 */
            if(remotePayloadType==SIPStack.SIP_CODEC_G711U)
            {
                packetsize=SIPG711.encode2ULaw(data,datasize,lineardata,0);
                //SIPG711.decodeULawExt(lineardata, 0, packetsize, tmp);
            }
            else if(remotePayloadType==SIPStack.SIP_CODEC_G711A)
            {
                packetsize=SIPG711.encode2ALaw(data,datasize,lineardata,0);
                //SIPG711.decodeALawExt(lineardata, 0, packetsize, tmp);
            }
            else if(remotePayloadType==SIPStack.SIP_CODEC_G729)
            {
                //packetsize=SIPG729a.encode(data,datasize,lineardata,0);
            }

            if(BUFFER_BUFFERREDSIZE+packetsize > BUFFER_SIZE)
            {
                BUFFER_PLAY_INDEX	= 0;
                BUFFER_BUFFERREDSIZE= 0;
            }
            int savePos= (BUFFER_PLAY_INDEX+BUFFER_BUFFERREDSIZE) % BUFFER_SIZE;
            int bufferTrail=BUFFER_SIZE-savePos;
            if(bufferTrail < packetsize)
            {
                System.arraycopy(lineardata,0,captureBuffer,savePos,bufferTrail);
                System.arraycopy(lineardata,bufferTrail,captureBuffer,0,packetsize-bufferTrail);
            }
            else
            {
                System.arraycopy(lineardata,0,captureBuffer,savePos,packetsize);
            }
            BUFFER_BUFFERREDSIZE+=packetsize;
            bCaptureBufferLock=false;

            if(flowIndicator == SIPStack.SIP_MEDIAFLOW_SENDONLY)
            {
                playAudio(comportableNoise,datasize);
            }
        }catch(Exception e){}
        return;
    }

    public boolean getLineardata(byte[] data,int datasize)
    {
        if(bCaptureBufferLock)
        {
            try {
                Date 	lockTime				= null;
                Date 	currentTime				= null;
                boolean bOverTime				= false;
                int durationMilli				= 0;
                lockTime						= new Date();
                while(bCaptureBufferLock)
                {
                    Thread.sleep(1,1);
                    currentTime					= new Date();
                    durationMilli=(int)(currentTime.getTime()-lockTime.getTime());
                    if(durationMilli>2) {
                        bOverTime=true;
                        break;
                    }
                }
                if(bOverTime==true) {
                    return false;
                }
            }catch(Exception e) {
                return false;
            }
        }

        if(data==null || datasize<=0) return false;

        if(datasize>BUFFER_BUFFERREDSIZE) {
            System.arraycopy(comportableNoise,0,data,0,datasize);
            return true;

        }

        int bufferTrail=BUFFER_SIZE-BUFFER_PLAY_INDEX;
        if(bufferTrail <datasize)
        {
            System.arraycopy(captureBuffer,BUFFER_PLAY_INDEX,data,0,bufferTrail);
            System.arraycopy(captureBuffer,0,data,bufferTrail,datasize-bufferTrail);
        }
        else
        {
            System.arraycopy(captureBuffer,BUFFER_PLAY_INDEX,data,0,datasize);
        }
        BUFFER_PLAY_INDEX = (BUFFER_PLAY_INDEX + datasize) % BUFFER_SIZE;
        BUFFER_BUFFERREDSIZE-=datasize;
        return true;
    }
    public boolean getEncodeddata(byte[] data,int datasize,int pos)
    {
        if(bCaptureBufferLock)
        {
            try {
                Date 	lockTime				= null;
                Date 	currentTime				= null;
                boolean bOverTime				= false;
                int durationMilli				= 0;
                lockTime						= new Date();
                while(bCaptureBufferLock)
                {
                    Thread.sleep(1,1);
                    currentTime					= new Date();
                    durationMilli=(int)(currentTime.getTime()-lockTime.getTime());
                    if(durationMilli>2) {
                        bOverTime=true;
                        break;
                    }
                }
                if(bOverTime==true) {
                    return false;
                }
            }catch(Exception e) {
                return false;
            }
        }

        if(data==null || datasize<=0) return false;

        if(datasize>BUFFER_BUFFERREDSIZE) {
            return false;
        }

        int bufferTrail=BUFFER_SIZE-BUFFER_PLAY_INDEX;
        if(bufferTrail <datasize)
        {
            System.arraycopy(captureBuffer,BUFFER_PLAY_INDEX,data,pos,bufferTrail);
            System.arraycopy(captureBuffer,0,data,pos+bufferTrail,datasize-bufferTrail);
        }
        else
        {
            System.arraycopy(captureBuffer,BUFFER_PLAY_INDEX,data,pos,datasize);
        }
        BUFFER_PLAY_INDEX = (BUFFER_PLAY_INDEX + datasize) % BUFFER_SIZE;
        BUFFER_BUFFERREDSIZE-=datasize;
        return true;
    }
    //2012 02 13
    public void confBufferring(byte[] data,int datasize,int side)
    {

        if(data==null || datasize<=0) return;


        if(side==1)
        {
            if(CONF1_RECV_BUFFERREDSIZE+datasize > CONF_RECV_BUFFER_SIZE)
            {
                CONF1_RECV_PLAY_INDEX	= 0;
                CONF1_RECV_BUFFERREDSIZE= 0;
            }
            int savePos= (CONF1_RECV_PLAY_INDEX+CONF1_RECV_BUFFERREDSIZE) % CONF_RECV_BUFFER_SIZE;
            int bufferTrail=CONF_RECV_BUFFER_SIZE-savePos;
            if(bufferTrail <datasize)
            {
                System.arraycopy(data,0,conf1RecvBuffer,savePos,bufferTrail);
                System.arraycopy(data,bufferTrail,conf1RecvBuffer,0,datasize-bufferTrail);
            }
            else
            {
                System.arraycopy(data,0,conf1RecvBuffer,savePos,datasize);
            }
            CONF1_RECV_BUFFERREDSIZE+=datasize;

        }
        else if(side==2)
        {
            if(CONF2_RECV_BUFFERREDSIZE+datasize > CONF_RECV_BUFFER_SIZE)
            {
                CONF2_RECV_PLAY_INDEX	= 0;
                CONF2_RECV_BUFFERREDSIZE= 0;
            }
            int savePos= (CONF2_RECV_PLAY_INDEX+CONF2_RECV_BUFFERREDSIZE) % CONF_RECV_BUFFER_SIZE;
            int bufferTrail=CONF_RECV_BUFFER_SIZE-savePos;
            if(bufferTrail <datasize)
            {
                System.arraycopy(data,0,conf2RecvBuffer,savePos,bufferTrail);
                System.arraycopy(data,bufferTrail,conf2RecvBuffer,0,datasize-bufferTrail);
            }
            else
            {
                System.arraycopy(data,0,conf2RecvBuffer,savePos,datasize);
            }
            CONF2_RECV_BUFFERREDSIZE+=datasize;

        }
        return;
    }

    public boolean getConfRecvLineardata(byte[] data,int datasize,int side)
    {
        if(data==null || datasize<=0) return false;

        if(side==1)
        {
            if(datasize>CONF1_RECV_BUFFERREDSIZE) {
                return false;
            }

            int bufferTrail=CONF_RECV_BUFFER_SIZE-CONF1_RECV_PLAY_INDEX;
            if(bufferTrail <datasize)
            {
                System.arraycopy(conf1RecvBuffer,CONF1_RECV_PLAY_INDEX,data,0,bufferTrail);
                System.arraycopy(conf1RecvBuffer,0,data,bufferTrail,datasize-bufferTrail);
            }
            else
            {
                System.arraycopy(conf1RecvBuffer,CONF1_RECV_PLAY_INDEX,data,0,datasize);
            }
            CONF1_RECV_PLAY_INDEX = (CONF1_RECV_PLAY_INDEX + datasize) % CONF_RECV_BUFFER_SIZE;
            CONF1_RECV_BUFFERREDSIZE-=datasize;

        }
        else if(side==2)
        {
            if(datasize>CONF2_RECV_BUFFERREDSIZE) {
                return false;
            }

            int bufferTrail=CONF_RECV_BUFFER_SIZE-CONF2_RECV_PLAY_INDEX;

            if(bufferTrail <datasize)
            {
                System.arraycopy(conf2RecvBuffer,CONF2_RECV_PLAY_INDEX,data,0,bufferTrail);
                System.arraycopy(conf2RecvBuffer,0,data,bufferTrail,datasize-bufferTrail);
            }
            else
            {
                System.arraycopy(conf2RecvBuffer,CONF2_RECV_PLAY_INDEX,data,0,datasize);
            }

            CONF2_RECV_PLAY_INDEX = (CONF2_RECV_PLAY_INDEX + datasize) % CONF_RECV_BUFFER_SIZE;
            CONF2_RECV_BUFFERREDSIZE-=datasize;

        }
        return true;
    }

    public  void resetBufferInfo()
    {
        //System.out.println("SIPSound::  resetBufferInfo");
        BUFFER_PLAY_INDEX		= 0;
        BUFFER_BUFFERREDSIZE	= 0;
        try
        {
            if(track!=null) {
                track.flush();//2012 09 28
            }
        }catch(Exception e){}
        return;
    }
    //2012 08 16
    public static void setSpeakerRoute(boolean out)
    {
        if(out==true) {
            amAudioManager.setSpeakerphoneOn(true);
            bSpeakerOn 					= true;

        }
        else {
            bSpeakerOn 					= false;
            amAudioManager.setSpeakerphoneOn(false);
        }

    }

    public static void setMute(boolean bSet)
    {
        bMute=bSet;

		/*
		if(amAudioManager != null)
		{
			amAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, bSet);
		}
		*/

    }

    public static void setAutoMute(boolean bSet, float value)
    {
        //amAudioManager.setMicrophoneMute(bSet);
        bAutoMute = bSet;
        autoMuteCutLine = value;

        if(!bSet)
        {
            bMute = false;
        }
    }

    public static void setTalkingMode(int direction)
    {
        try
        {
            if(amAudioManager!=null) {
                amAudioManager.setMode(AudioManager.MODE_NORMAL);
            }

            if(direction==SIPStack.SIP_CALLDIRECTION_IN)
            {
                //
                if(SIPStack.bConstantSpeaker==true)
                {
                    //
                    amAudioManager.setSpeakerphoneOn(true);
                    bSpeakerOn 					= true;

                }
                else
                {
                    amAudioManager.setSpeakerphoneOn(false);
                    bSpeakerOn 					= false;
                }

            }
        }catch(Exception e){}
    }

    //AEC
	/*
	public byte[] doAEC(byte[] in, byte[] out)
	{
		MobileAEC aecm = new MobileAEC(null);
		aecm.setAecmMode(MobileAEC.AggressiveMode.MOST_AGGRESSIVE).prepare();

		byte[] aecBuf = null;

		//for (int i=0 ; i<in.length ; i++) {
		// convert bytes[] to shorts[], and make it into little endian.
		short[] aecTmpIn = new short[in.length / 2];
		short[] aecTmpOut = new short[in.length / 2];
		ByteBuffer.wrap(in).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(aecTmpIn);

		// aecm procession, for now the echo tail is hard-coded 10ms,
		// but you
		// should estimate it correctly each time you call
		// echoCancellation, otherwise aecm
		// cannot work.
		try {
			aecm.farendBuffer(aecTmpIn, in.length / 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			aecm.echoCancellation(aecTmpIn, null, aecTmpOut, (short) (in.length / 2), (short) 5000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// output
		aecBuf = new byte[in.length];
		ByteBuffer.wrap(aecBuf).order(ByteOrder.LITTLE_ENDIAN)
		.asShortBuffer().put(aecTmpOut);
		//}


		return aecBuf;
	}

	public short[] doAEC(short[] in, byte[] out)
	{

		MobileAEC aecm = new MobileAEC(null);
		aecm.setAecmMode(MobileAEC.AggressiveMode.MOST_AGGRESSIVE).prepare();


		// convert bytes[] to shorts[], and make it into little endian.

		short[] aecTmpOut = new short[160];

		// aecm procession, for now the echo tail is hard-coded 10ms,
		// but you
		// should estimate it correctly each time you call
		// echoCancellation, otherwise aecm
		// cannot work.
		try {
			//short[] aecTmpIn = new short[out.length / 2];
			//ByteBuffer.wrap(out).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(aecTmpIn);

			aecm.farendBuffer(in, 160);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("error", "echo error1");
			e.printStackTrace();
		}

		try {
			short[] aecTmpIn = new short[out.length / 2];
			ByteBuffer.wrap(out).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(aecTmpIn);

			aecm.echoCancellation(in, in, aecTmpOut, (short) (160), (short) 10);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("error", "echo error2");
			e.printStackTrace();
		}


		return aecTmpOut;
	}
	 */
}
