import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class SimpleHttpServer extends Thread {
    public static final int HTTP_PORT=8000;   //�����˿�
    ServerSocket serverSocket=null;

    public SimpleHttpServer() {
        try {
            serverSocket=new ServerSocket(HTTP_PORT);
        }catch (IOException e){
            e.printStackTrace();
        }
        if(serverSocket==null){
            throw new RuntimeException("������Socket��ʼ��ʧ��");
        }
    }

    @Override
    public void run() {
        try {
            //����ѭ��������ȴ�����״̬
            while (true){
                System.out.println("�ȴ�������...");
                //һ�����յ��������󣬹���һ���߳����������������������߳�
                new DeliverThread(serverSocket.accept()).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SimpleHttpServer().start();
//        InetAddress inetAddress= null;
//        try {
//            inetAddress = InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } finally {
//        }
//        System.out.println(inetAddress.getHostAddress());
    }
}
