package trader.runners;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.messages.IMessage;
import api.messages.external.IExternalRequest;
import api.messages.internal.responses.IResponse;
import api.messages.internal.util.IOrderRequestFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import trader.agents.ITraderAgent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPTraderAgentRunner implements ITraderAgentRunner {

    private static final Logger LOGGER = LogManager.getLogger(TCPTraderAgentRunner.class);
    private static final String EXCHANGE_SERVER_IP = "localhost";
    private static final int EXCHANGE_SERVER_SOCKET = 9999;

    private final ITraderAgent traderAgent;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ResponseListenerThread responseListenerThread;

    public TCPTraderAgentRunner(ITraderAgent traderAgent, IMatchingEngine matchingEngine) {
        this.traderAgent = traderAgent;
    }

    @Override
    public void run() {
        LOGGER.info("Starting TCPTraderAgentRunner");
        try (Socket client = new Socket(EXCHANGE_SERVER_IP, EXCHANGE_SERVER_SOCKET)) {
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
            responseListenerThread = new ResponseListenerThread(traderAgent, in);
            threadPool.execute(responseListenerThread);
            while(true) {
                IExternalRequest externalRequest = traderAgent.getNextRequest();
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
                IMessage message = (IMessage) in.readObject();
                while(message != null) {
                    traderAgent.registerMessage(message);
                    message = (IMessage) in.readObject();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
