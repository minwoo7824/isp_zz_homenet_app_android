package com.kd.One.sip;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NwUtil { //extends Activity {

    private Context context;

    public NwUtil(Context context) {
        this.context = context;
    }

    // 호출할 페이지 번호 저장용
    private int pageCnt = 1;
    //페이지 정렬 순서
    private String order = "new";

    private final String encKey = "sptp";

    public int getPageCnt() {
        return this.pageCnt;
    }

    public void setPageCnt(int pageCnt) {
        this.pageCnt = pageCnt;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }


    // 파일 쓰기 및 읽기 관련
    //private final String cacheDir_SD = "/Android/data/com.kd.One/dataCache";
    private final String cacheDir_SD = "/Android/data/com.kd.One/dataCache";
    private File targetPath = null;
    private File targetfile = null;
    private String sState = Environment.getExternalStorageState();

    // HTML CONTENT 추출 패턴
    @SuppressWarnings("unused")
    private static interface Patterns {
        public static final Pattern SCRIPTS = Pattern.compile(
                "<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);
        public static final Pattern STYLE = Pattern.compile(
                "<style[^>]*>.*</style>", Pattern.DOTALL);
        public static final Pattern TAGS = Pattern
                .compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
        public static final Pattern nTAGS = Pattern
                .compile("<\\w+\\s+[^<]*\\s*>");
        public static final Pattern ENTITY_REFS = Pattern.compile("&[^;]+;");
        public static final Pattern WHITESPACE = Pattern.compile("\\s\\s+");

        // src속성 분리
        public static final Pattern IMGSRC1 = Pattern
                .compile("(?i)src[a-zA-Z0-9_.\\-%&=?!:;@\"'/]*");
        // 이미지 경로만 추출
        public static final Pattern IMGSRC2 = Pattern
                .compile("(?i)http://[a-zA-Z0-9_.\\-%&=?!:;@/]*");
        // img태그 자체를 분리
        public static final Pattern IMGSRC3 = Pattern
                .compile("(?i)<img(\\s+[a-zA-Z0-9_]*=[^>]*)*(\\s)*(/)?>");
    }

    // HTML CONTENT 추출
    public static HashMap<String, Object> ConetntOutput(String s) {
        if (s == null) {
            return null;
        }

        HashMap<String, Object> result = new HashMap<String, Object>();
        ArrayList<String> imgList = new ArrayList<String>();

        Matcher m;

        s = s.replaceAll("&lt;", "<");
        s = s.replaceAll("&gt;", ">");
        s = s.replaceAll("&amp;", "&");
        s = s.replaceAll("&#034;", "\"");
        s = s.replaceAll("&#039;", "'");

        m = Patterns.IMGSRC2.matcher(s);
        while (m.find()) {
            imgList.add(s.substring(m.start(), m.end()));
        }

        m = Patterns.SCRIPTS.matcher(s);
        s = m.replaceAll("");
        m = Patterns.STYLE.matcher(s);
        s = m.replaceAll("");
        m = Patterns.TAGS.matcher(s);
        s = m.replaceAll("");
        m = Patterns.ENTITY_REFS.matcher(s);
        s = m.replaceAll("");
        m = Patterns.WHITESPACE.matcher(s);
        s = m.replaceAll(" ");

        result.put("imgList", imgList);
        result.put("content", s);
        return result;
    }

    // stream을 가지고 image 사이즈 구하기
    public BitmapFactory.Options getBitmapWidth(InputStream is, BitmapFactory.Options opt)
            throws IOException {
        int targetWidth = 0;
        int targetHeight = 0;
        BufferedInputStream bin = null;
        try {
            opt.inJustDecodeBounds = true;
            byte[] bytes = new byte[is.available()];
            bin = new BufferedInputStream(is);
            bin.read(bytes);
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt);
            if (opt.outWidth > opt.outHeight) {
                targetWidth = (int) (300 * 1.3);
                targetHeight = 300;
            } else {
                targetWidth = 300;
                targetHeight = (int) (300);
            }
            boolean scaleByHeight = Math.abs(opt.outHeight - targetHeight) >= Math
                    .abs(opt.outWidth - targetWidth);
            if (opt.outHeight * opt.outWidth * 2 >= 16384) {
                double sampleSize = scaleByHeight ? opt.outHeight
                        / targetHeight : opt.outWidth / targetWidth;
                opt.inSampleSize = (int) Math.pow(2d, Math.floor(Math
                        .log(sampleSize)
                        / Math.log(2d)));
            }
            opt.inJustDecodeBounds = false;
        } catch (Exception e) {
            Log.e("ERR", e.toString());
            return null;
        } finally {
            if (bin != null) {
                bin.close();
            }
        }
        return opt;
    }

    public String encodeMd5(String str) {
        StringBuffer md5 = new StringBuffer();
        try {
            byte[] digest = java.security.MessageDigest.getInstance("MD5")
                    .digest(str.getBytes());
            for (int i = 0; i < digest.length; i++) {
                md5.append(Integer.toString((digest[i] & 0xf0) >> 4, 16));
                md5.append(Integer.toString(digest[i] & 0x0f, 16));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return md5.toString().toUpperCase();
    }


    // 자동 로그인 여부 , 로그인 상태 , ID , PW
    //public void setLoginState(String autoLogin , String loginState , String adult ,String id , String pw){
    public void setLoginState(String autoLogin , String loginState  ,String id , String pw , String userid, String tutoFg){

        if ("".equals(userid) || "null".equals(userid) || null == userid){
            userid = "null";
        }

        if ("".equals(id) || "null".equals(id) || null == id){
            id = "null";
        }
        if ("".equals(pw) || "null".equals(pw) || null == pw){
            pw = "null";
        }else{
            pw = EncodeUtil.encData(pw, encKey);
        }

        String loginCache = autoLogin + "||" + loginState+ "||" + id + "||" + pw + "||" + userid + "||" + tutoFg;

        BufferedWriter bWrite = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(sState)) {
                targetPath = new File(Environment.getExternalStorageDirectory().toString()+ cacheDir_SD);
                if(!targetPath.exists()){
                    targetPath.mkdirs();
                }
            }else{
                targetPath = context.getFilesDir();
                //targetfile = new File(targetPath , fileNm+".jpg");
                //saveRemoteFile(url, targetfile);
            }
            targetfile = new File(targetPath , "loginCache.dat");

            bWrite = new BufferedWriter(new FileWriter(targetfile));
            bWrite.write(loginCache,0,loginCache.length());
            bWrite.flush();

            bWrite.close();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
            e.setStackTrace(e.getStackTrace());
        }
    }

    //로그인 캐쉬 Read
    @SuppressWarnings("unchecked")
    public HashMap LoginCacheRead(){
        HashMap hm = new HashMap();
        if (Environment.MEDIA_MOUNTED.equals(sState)) {
            targetPath = new File(Environment.getExternalStorageDirectory().toString()+ cacheDir_SD);
        }else{
            targetPath = context.getFilesDir();
        }

        targetfile = new File(targetPath , "loginCache.dat");
        try {
            if (targetfile.exists()) {
                String[] data = fileRead(targetfile).split("\\|\\|");
                hm.put("autoLogin", data[0]);
                hm.put("loginState", data[1]);
                //hm.put("adult", data[2]);
                hm.put("id", data[2]);
                if (!"null".equals(data[3])) {
                    hm.put("pw", EncodeUtil.decData(data[3], encKey));
                }
                else{
                    hm.put("pw", data[3]);
                }
                hm.put("encodingUserid",data[4]); //userid
                hm.put("tutorialFg",data[5]); 	//튜토리얼 확인여부
            }else{
                hm.put("autoLogin", "F");
                hm.put("loginState", "F");
                //hm.put("adult","F");
                hm.put("id", "null");
                hm.put("pw", "null");
                hm.put("encodingUserid", "null");
                hm.put("tutorialFg", "F");
            }
        } catch (Exception e) {}
        return hm;
    }


    //Url Shorten(url줄이기)
    public String urlShoretn(String oriUrl){
        String encURL = "";
        String responseString = "";
        try {
            encURL = URLEncoder.encode(oriUrl,"UTF-8");
            URL url = new URL("http://211.110.205.60/mobile/makeurl.php?url="+encURL);

            URLConnection conn = url.openConnection();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                responseString += line;
            }

            rd.close();

        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
        return responseString;
    }

    // 문서 파일의 내용 길이를 구한다
    public int fileLength(File file) {
        int b, count = 0;
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader(file));
            while ((b = buffRead.read()) != -1) {
                count++;
            }
            buffRead.close();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
        return count;
    }

    //기존 문서 파일 내용을 읽어온다
    static String fileRead(File file) {
        int b;
        String fileContent = "";
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader(file));
            while ((b = buffRead.read()) != -1) {
                fileContent += (char) b;
            }
            buffRead.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return fileContent;
    }

    //기존 스크랩 내용중  특정  스크랩 내용을 제외한후 읽어온다
    static String scrapIngoreRead(File file , String list_id) {
        int b;
        String content = "";
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while( ( line = br.readLine()) != null){
                if(line == null || "".equals(line)) continue;
                String [] data = line.split("\\|\\|");
                if(!data[0].equals(list_id)){
                    content = content+line+"\n";
                }
            }
            br.close();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
        return content;
    }

}
