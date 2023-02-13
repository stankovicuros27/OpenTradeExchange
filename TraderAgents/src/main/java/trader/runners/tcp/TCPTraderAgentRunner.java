package trader.runners.tcp;

import api.core.IOrderBook;
import api.messages.authentication.IMicroFIXAuthenticationMessageFactory;
import api.messages.authentication.IMicroFIXAuthenticationRequest;
import api.messages.authentication.IMicroFIXAuthenticationResponse;
import api.messages.data.IMicroFIXL1DataMessage;
import api.messages.data.MicroFIXDataMessageConstants;
import api.messages.trading.request.IMicroFIXRequest;
import api.messages.trading.response.IMicroFIXResponse;
import impl.messages.authentication.MicroFIXAuthenticationMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trader.agents.ITraderAgent;
import trader.runners.TraderAgentRunner;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPTraderAgentRunner extends TraderAgentRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TCPTraderAgentRunner.class);

    private final String exchangeServerIp;
    private final int exchangeServerSocket;
    private final String multicastIp;
    private final int l1MarketDataMulticastPort;
    private final int l2MarketDataMulticastPort;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final IMicroFIXAuthenticationMessageFactory microFIXAuthenticationMessageFactory = new MicroFIXAuthenticationMessageFactory();

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public TCPTraderAgentRunner(ITraderAgent traderAgent, IOrderBook orderBook, int timeoutMs, String exchangeServerIp, int exchangeServerSocket, String multicastIp, int l1MarketDataMulticastPort, int l2MarketDataMulticastPort) {
        super(traderAgent, orderBook, timeoutMs);
        this.exchangeServerIp = exchangeServerIp;
        this.exchangeServerSocket = exchangeServerSocket;
        this.multicastIp = multicastIp;
        this.l1MarketDataMulticastPort = l1MarketDataMulticastPort;
        this.l2MarketDataMulticastPort = l2MarketDataMulticastPort;
    }

    @Override
    public void run() {
        LOGGER.info("Starting TCPTraderAgentRunner for book: " + orderBook.getBookID());
        try (Socket client = new Socket(exchangeServerIp, exchangeServerSocket)) {
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            if (!authenticate()) {
                LOGGER.info("User: " + traderAgent.getUserID() + " not authenticated!");
                return;
            }
            ResponseListenerThread responseListenerThread = new ResponseListenerThread(traderAgent, in);
            threadPool.execute(responseListenerThread);
            L1MarketDataListenerThread l1MarketDataListenerThread = new L1MarketDataListenerThread(traderAgent, multicastIp, l1MarketDataMulticastPort);
            threadPool.execute(l1MarketDataListenerThread);
            while(true) {
                IMicroFIXRequest externalRequest = traderAgent.getNextRequest();
                if (externalRequest != null) {
                    out.writeObject(externalRequest);
                    out.flush();
                }
                try {
                    Thread.sleep((long) (Math.random() * timeoutMs));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean authenticate() {
        try {
            IMicroFIXAuthenticationRequest microFIXAuthenticationRequest = microFIXAuthenticationMessageFactory.getAuthenticationRequest(traderAgent.getUserID(), "dummyPassHash");
            out.writeObject(microFIXAuthenticationRequest);
            IMicroFIXAuthenticationResponse microFIXAuthenticationResponse = (IMicroFIXAuthenticationResponse) in.readObject();
            return microFIXAuthenticationResponse.isAccepted();
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

        private final ITraderAgent traderAgent;
        private final String multicastIp;
        private final int l1MarketDataMulticastPort;

        private L1MarketDataListenerThread(ITraderAgent traderAgent, String multicastIp, int l1MarketDataMulticastPort) {
            this.traderAgent = traderAgent;
            this.multicastIp = multicastIp;
            this.l1MarketDataMulticastPort = l1MarketDataMulticastPort;
        }

        @Override
        public void run() {
            try (MulticastSocket clientSocket = new MulticastSocket(l1MarketDataMulticastPort)) {
                InetAddress multicastAddress = InetAddress.getByName(multicastIp);
                clientSocket.joinGroup(multicastAddress);
                while (true) {
                    byte[] objectBuffer = new byte[MicroFIXDataMessageConstants.L1_MARKET_DATA_MESSAGE_MAX_SIZE_BYTES];
                    DatagramPacket datagramPacket = new DatagramPacket(objectBuffer, objectBuffer.length);
                    clientSocket.receive(datagramPacket);
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(objectBuffer);
                    ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(byteArrayInputStream));
                    IMicroFIXL1DataMessage microFIXL1DataMessage = (IMicroFIXL1DataMessage) objectInputStream.readObject();
                    traderAgent.registerL1MarketDataUpdate(microFIXL1DataMessage);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
