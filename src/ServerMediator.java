import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMediator {
    private final int tcpDiscussionPort = 9000;
    private final int tcpPunchPort = 9001;

    private BufferedReader inDiscussionA, inPunchA;
    private PrintWriter outDiscussionA, outPunchA;

    private BufferedReader inDiscussionB, inPunchB;
    private PrintWriter outDiscussionB, outPunchB;

    private ServerSocket socketConnect, socketPunch;

    private Socket clientAConnect, clientAPunch, clientBConnect, clientBPunch;

    private String clientAIp = "";
    private String clientAPort = "";
    private String clientAPortLocal = "";

    private String clientBIp = "";
    private String clientBPort = "";
    private String clientBPortLocal = "";

    public ServerMediator() {
        runServer();
    }

    private void runServer() {
        System.out.println("Server started with ports, connection port: " + tcpDiscussionPort + " punch port: " + tcpPunchPort);
        runDiscussionServer();
        runPunchServer();
    }

    private void runDiscussionServer() {
        new Thread(() -> {
            try {
                socketConnect = new ServerSocket(tcpDiscussionPort);

                System.out.println("Waiting for Client A...");
                clientAConnect = socketConnect.accept();
                System.out.println("Client 1 connected on address: " + clientAConnect.getInetAddress() + ":" + clientAConnect.getPort());
                inDiscussionA = new BufferedReader(new InputStreamReader(clientAConnect.getInputStream()));
                outDiscussionA = new PrintWriter(clientAConnect.getOutputStream(), true);

                System.out.println("Waiting for Client B...");
                clientBConnect = socketConnect.accept();
                System.out.println("Client 2 connected on address: " + clientBConnect.getInetAddress() + ":" + clientBConnect.getPort());
                inDiscussionB = new BufferedReader(new InputStreamReader(clientBConnect.getInputStream()));
                outDiscussionB = new PrintWriter(clientBConnect.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runPunchServer() {
        new Thread(() -> {
            try {
                socketPunch = new ServerSocket(tcpPunchPort);

                System.out.println("Waiting for Client A punch");
                clientAPunch = socketPunch.accept();
                clientAIp = ((InetSocketAddress)clientAPunch.getRemoteSocketAddress()).getAddress().getHostAddress().trim();
                clientAPortLocal = String.valueOf(clientAPunch.getPort());
                clientAPort = String.valueOf(clientAPunch.getLocalPort());
                System.out.println("Client A punch " + clientAPunch.getInetAddress() + " " + clientAPunch.getPort());
                inPunchA = new BufferedReader(new InputStreamReader(clientAPunch.getInputStream()));
                outPunchA = new PrintWriter(clientAPunch.getOutputStream());

                System.out.println("Waiting for Client B punch");
                clientBPunch = socketPunch.accept();
                clientBIp = ((InetSocketAddress)clientBPunch.getRemoteSocketAddress()).getAddress().getHostAddress().trim();
                clientBPortLocal = String.valueOf(clientBPunch.getPort());
                clientBPort = String.valueOf(clientBPunch.getLocalPort());
                System.out.println("Client 2 punch " + clientBPunch.getInetAddress() + " " + clientBPunch.getPort());
                inPunchB = new BufferedReader(new InputStreamReader(clientBPunch.getInputStream()));
                outPunchB = new PrintWriter(clientBPunch.getOutputStream());

                proceedInfosExchange();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void proceedInfosExchange() throws IOException {
        while (true) {
            String message = inPunchA.readLine();
            if (message.trim().equals("CLIENT_RESP:READY_TO_PUNCH")) {
                System.out.println("Initial punch message from CLIENT A: " + message);
                break;
            }
        }
        System.out.println("Client A IP and port detected: " + clientAIp + ":" +  clientAPortLocal + "->" + clientAPort);

        while (true) {
            String message = inPunchB.readLine();
            if (message.trim().equals("CLIENT_RESP:READY_TO_PUNCH")) {
                System.out.println("Initial punch message from CLIENT B: " + message);
                break;
            }
        }
        System.out.println("Client B IP and port detected: " + clientBIp + ":" +  clientBPortLocal + "->" + clientBPort);

        System.out.println("Exchanging public IP and port between the clients...");

        boolean clientAPunchReceived = false, clientBPunchReceived = false;
        while (true) {
            String string = clientAIp + "~~" + clientAPort + "~~" + clientAPortLocal + "~~" + clientBIp + "~~" + clientBPort + "~~" + clientBPortLocal;
            outDiscussionA.println(string);

            String string1 = clientBIp + "~~" + clientBPort + "~~" + clientBPortLocal + "~~" + clientAIp + "~~" + clientAPort + "~~" + clientAPortLocal;
            outDiscussionB.println(string);

            while (true) {
                String message = inPunchA.readLine();
                if (message.trim().equals("CLIENT_RESP:PUNCH_INFO_RECEIVED")) {
                    System.out.println("Client A received punch ip and port!");
                    clientAPunchReceived = true;
                    break;
                }
            }

            while (true) {
                String message = inPunchB.readLine();
                if (message.trim().equals("CLIENT_RESP:PUNCH_INFO_RECEIVED")) {
                    System.out.println("Client B received punch ip and port!");
                    clientBPunchReceived = true;
                    break;
                }
            }
            if (clientAPunchReceived && clientBPunchReceived) {
                break;
            }
        }
    }
}
