import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DeliverThread extends Thread{
    Socket mClientSocket;
    //�����������ܿͷ��˵�Socket����
    BufferedReader mInputStream;
    //�����
    PrintStream moutputStream;
    //���󷽷���GET,POST��
    String httpMethod;
    //��·��
    String subPath;
    //���ķָ���
    String boundary;
    //�������
    Map<String,String> mParams=new HashMap<String, String>();
    //����ͷ����
    Map<String,String> mHeader=new HashMap<String,String>();
    //�Ƿ��Ѿ���������header
    boolean isParseHeader=false;
    public DeliverThread(Socket socket){
        mClientSocket=socket;
    }

    @Override
    public void run() {
        try {
            mInputStream=new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
            moutputStream=new PrintStream(mClientSocket.getOutputStream());
            //��������
            parseRequest();
            //����Response
            handleResponse();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            //�ر�����Socket
            try {
                moutputStream.close();
                mInputStream.close();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void parseRequest(){
        String line;
        try {
            int lineNum=0;
            while ((line=mInputStream.readLine())!=null){
                //����������
                if(lineNum==0){
                    parseRequestLine(line);
                }
                //�ж��Ƿ������ݵĽ�����
                if(isEnd(line)){
                    break;
                }
                //����header����
                if(lineNum!=0&&!isParseHeader){
                    parseHeaders(line);
                }
                //�����������
                if(isParseHeader){
                    parseRequestParams(line);
                }
                lineNum++;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private boolean isEnd(String line){
        if(line.equals("")){
            return true;
        }else {
            return false;
        }
    }
    //����������
    private void parseRequestLine(String lineOne){
        String[] tempStrings=lineOne.split(" ");
        httpMethod=tempStrings[0];
        subPath=tempStrings[1];
        System.out.println("����ʽ�ǣ�"+tempStrings[0]);
        System.out.println("��·���ǣ�"+tempStrings[1]);
        System.out.println("Http�汾��"+tempStrings[2]);
    }
    //����header,����Ϊÿ��header���ַ���
    private void parseHeaders(String headerLine){
        if(headerLine.equals("")){
            isParseHeader=true;
            System.out.println("----->header�������\n");
            return;
        }else if (headerLine.contains("boundary")){
            boundary=parseSecondField(headerLine);
            System.out.println("�ָ�����"+boundary);
        }else {
            parseHeaderParam(headerLine);
        }
    }
    //����header�еĵڶ�������
    private String parseSecondField(String line){
        String[] headerArray=line.split(";");
        parseHeaderParam(headerArray[0]);
        if(headerArray.length>1){
            return headerArray[1].split("=")[1];
        }
        return "";
    }
    //��������header
    private void parseHeaderParam(String headerLine){
        String[] keyvalue=headerLine.split(":");
        mHeader.put(keyvalue[0].trim(),keyvalue[1].trim());
        System.out.println("header��������"+keyvalue[0].trim()+"������ֵ��"+keyvalue[1].trim());
    }
    //�����������
    private void parseRequestParams(String paramLine)throws IOException{
        if(paramLine.equals("--"+boundary)){
            String ContentDisponsition=mInputStream.readLine();
            String paramName=parseSecondField(ContentDisponsition);
            mInputStream.readLine();
            String paramValue=mInputStream.readLine();
            mParams.put(paramName,paramValue);
            System.out.println("��������"+paramName+"������ֵ��"+paramValue);
        }
    }
    //���ؽ��
    private void handleResponse(){
        //ģ�⴦���ʱ
        sleep();
        //�������д����
        moutputStream.println("HTTP/1.1 200 OK");
        moutputStream.println("Content-Type: application/json");
        moutputStream.println();
        moutputStream.println("{\"stCode\":\"success\"}");
    }
    private void sleep(){
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
