package server.direct;

import api.core.IMatchingEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ExchangeServer implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ExchangeServer.class);

    private final IMatchingEngine matchingEngine;
    private final ExecutorService threadPool;
    private final int port;
    private final BroadcastService broadcastService;

    public ExchangeServer(IMatchingEngine matchingEngine, ExecutorService threadPool, int port, BroadcastService broadcastService) {
        LOGGER.info("Creating ExchangeServer at port: " + port);
        this.matchingEngine = matchingEngine;
        this.threadPool = threadPool;
        this.port = port;
        this.broadcastService = broadcastService;
    }

    @Override
    public void run() {
        LOGGER.info("Starting ExchangeServer at port: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                Socket brokerSocket = serverSocket.accept();
                InetSocketAddress brokerSocketAddress = (InetSocketAddress) brokerSocket.getRemoteSocketAddress();
                String brokerIpAddress = brokerSocketAddress.getAddress().getHostAddress();
                LOGGER.info("Accepting broker connection at IP address: " + brokerIpAddress);
                ConnectionHandler handler = new ConnectionHandler(matchingEngine, brokerSocket, brokerIpAddress, broadcastService);
                broadcastService.registerBrokerConnectionHandler(handler);
                threadPool.execute(handler);
            }
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            LOGGER.info("Closing ExchangeServer at port: " + port);
            threadPool.shutdown();
        }
    }

}
