package trader.runners;

import api.core.IOrderBook;
import api.messages.authentication.IMicroFIXAuthenticationMessageFactory;
import api.messages.authentication.IMicroFIXAuthenticationRequest;
import api.messages.authentication.IMicroFIXAuthenticationResponse;
import api.messages.data.IMicroFIXL1DataMessage;
import api.messages.trading.request.IMicroFIXRequest;
import api.messages.trading.response.IMicroFIXResponse;
import impl.messages.authentication.MicroFIXAuthenticationMessageFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import trader.agents.ITraderAgent;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPTraderAgentRunner implements ITraderAgentRunner {

    private static final Logger LOGGER = LogManager.getLogger(TCPTraderAgentRunner.class);
    private static final String EXCHANGE_SERVER_IP = "localhost";
    private static final int EXCHANGE_SERVER_SOCKET = 9999;

    private static final String MULTICAST_IP_ADDRESS = "225.4.5.6";
    private static final int L1_DATA_MULTICAST_PORT = 9998;

    private final ITraderAgent traderAgent;
    private final IOrderBook orderBook;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final IMicroFIXAuthenticationMessageFactory microFIXAuthenticationMessageFactory = new MicroFIXAuthenticationMessageFactory();

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ResponseListenerThread responseListenerThread;

    public TCPTraderAgentRunner(ITraderAgent traderAgent, IOrderBook orderBook) {
        this.traderAgent = traderAgent;
        this.orderBook = orderBook;
    }

    @Override
    public void run() {
        LOGGER.info("Starting TCPTraderAgentRunner for book: " + orderBook.getBookID());
        try (Socket client = new Socket(EXCHANGE_SERVER_IP, EXCHANGE_SERVER_SOCKET)) {
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            authenticate();
            responseListenerThread = new ResponseListenerThread(traderAgent, in);
            threadPool.execute(responseListenerThread);
            L1MarketDataListenerThread l1MarketDataListenerThread = new L1MarketDataListenerThread();
            threadPool.execute(l1MarketDataListenerThread);
            while(true) {
                IMicroFIXRequest externalRequest = traderAgent.getNextRequest();
                out.writeObject(externalRequest);
                out.flush();
                try {
                    Thread.sleep((long) (Math.random() * SLEEP_TIME_MS));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void authenticate() {
        try {
            IMicroFIXAuthenticationRequest microFIXAuthenticationRequest = microFIXAuthenticationMessageFactory.getAuthenticationRequest(traderAgent.getUserID(), "dummyPassHash");
            out.writeObject(microFIXAuthenticationRequest);
            IMicroFIXAuthenticationResponse microFIXAuthenticationResponse = (IMicroFIXAuthenticationResponse) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ResponseListenerThread implements Runnable {

        private final ITraderAgent traderAgent;
        private final ObjectInputStream in;

        private ResponseListenerThread(ITraderAgent traderAgent, ObjectInputStream in) {
            this.traderAgent = traderAgent;
            this.in = in;
        }

        @Override
        public void run() {
            try {
                IMicroFIXResponse response = (IMicroFIXResponse) in.readObject();
                while(response != null) {
                    traderAgent.registerResponse(response);
                    response = (IMicroFIXResponse) in.readObject();
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class L1MarketDataListenerThread implements Runnable {

        @Override
        public void run() {
            InetAddress address = null;
            try {
                address = InetAddress.getByName(MULTICAST_IP_ADDRESS);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            try (MulticastSocket clientSocket = new MulticastSocket(L1_DATA_MULTICAST_PORT)){
                //Joint the Multicast group.
                clientSocket.joinGroup(address);

                while (true) {
                    // Receive the information and print it.
                    byte[] buf = new byte[5000];
                    DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                    clientSocket.receive(msgPacket);
                    ByteArrayInputStream byteStream = new
                            ByteArrayInputStream(buf);
                    ObjectInputStream is = new
                            ObjectInputStream(new BufferedInputStream(byteStream));
                    IMicroFIXL1DataMessage microFIXL1DataMessage = (IMicroFIXL1DataMessage) is.readObject();
                    System.out.println(microFIXL1DataMessage);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
