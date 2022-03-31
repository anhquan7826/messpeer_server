import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RendezvousServer {
    private final ServerSocket serverSocket;
    private Socket clientA;
    private Socket clientB;

    private String clientAPublicIP;
    private int clientAPublicPort;
    private String clientALocalIP;
    private int clientALocalPort;

    private String clientBPublicIP;
    private int clientBPublicPort;
    private String clientBLocalIP;
    private int clientBLocalPort;

    public RendezvousServer() throws IOException {
        serverSocket = new ServerSocket(1234);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Server started listening connection on port 1234...");
                try {
                    System.out.println("Waiting for client A...");
                    clientA = serverSocket.accept();
                    System.out.println("Client A connected!");

                    System.out.println("Waiting for client B...");
                    clientB = serverSocket.accept();
                    System.out.println("Client B connected!");

                    System.out.println("Acquiring infos...");
                    clientAPublicIP = clientA.getInetAddress().getHostAddress();
                    clientAPublicPort = clientA.getPort();
                    clientALocalIP = clientA.getLocalAddress().getHostAddress();
                    clientALocalPort = clientA.getLocalPort();

                    clientBPublicIP = clientB.getInetAddress().getHostAddress();
                    clientBPublicPort = clientB.getPort();
                    clientBLocalIP = clientB.getLocalAddress().getHostAddress();
                    clientBLocalPort = clientB.getLocalPort();
                    System.out.println("Infos acquired!");
                    System.out.println("Client A public and local addresses are: " + clientAPublicIP + ":" + clientAPublicPort + "/" + clientALocalIP + ":" + clientALocalPort);
                    System.out.println("Client B public and local addresses are: " + clientBPublicIP + ":" + clientBPublicPort + "/" + clientBLocalIP + ":" + clientBLocalPort);
                    exchangeInformation();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void exchangeInformation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Start exchanging information...");
                try {
                    PrintWriter clientAOutput = new PrintWriter(clientA.getOutputStream());
                    PrintWriter clientBOutput = new PrintWriter(clientB.getOutputStream());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                clientAOutput.println(clientBPublicIP + "~~" + clientBPublicPort + "~~" + clientALocalPort);
                                if (clientA.isClosed()) {
                                    break;
                                }
                            }
                        }
                    }).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                clientBOutput.println(clientAPublicIP + "~~" + clientAPublicPort + "~~" + clientBLocalPort);
                                if (clientB.isClosed()) {
                                    break;
                                }
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
