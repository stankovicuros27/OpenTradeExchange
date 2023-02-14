package server.direct.tcpbroadcast;

import api.core.IMatchingEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class L1MarketDataTCPServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(L1MarketDataTCPServer.class);

    private final int port;
    private final L1MarketDataSenderService l1MarketDataSenderService;
    private final L1MarketDataPublisher l1MarketDataPublisher;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public L1MarketDataTCPServer(IMatchingEngine matchingEngine, int port, int l1TimeoutMs, L1MarketDataSenderService l1MarketDataSenderService) {
        LOGGER.info("Creating L1 Market Data TCP Service at port: " + port);
        this.port = port;
        this.l1MarketDataSenderService = l1MarketDataSenderService;
        l1MarketDataPublisher = new L1MarketDataPublisher(matchingEngine, l1MarketDataSenderService, l1TimeoutMs);
    }

    @Override
    public void run() {
        LOGGER.info("Starting L1 Market Data TCP Service at port: " + port);
        threadPool.execute(l1MarketDataPublisher);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                InetSocketAddress brokerSocketAddress = (InetSocketAddress) clientSocket.getRemoteSocketAddress();
                String clientIpAddress = brokerSocketAddress.getAddress().getHostAddress();
                LOGGER.info("Accepting client L1 data subscription at IP address: " + clientIpAddress);
                L1MarketDataSubscriptionHandler l1MarketDataSubscriptionHandler = new L1MarketDataSubscriptionHandler(clientSocket, clientIpAddress, l1MarketDataSenderService);
                threadPool.execute(l1MarketDataSubscriptionHandler);
            }
        } catch (IOException e) {
            LOGGER.error(e.toString());
        } finally {
            LOGGER.info("Closing L1 Market Data TCP Service at port: " + port);
            threadPool.shutdown();
        }
    }
}
