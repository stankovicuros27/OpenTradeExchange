package server.direct.core;

import api.core.IMatchingEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeServer.class);

    private final IMatchingEngine matchingEngine;
    private final int port;
    private final ExchangeResponseSenderService exchangeResponseSenderService;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public ExchangeServer(IMatchingEngine matchingEngine, int port, ExchangeResponseSenderService exchangeResponseSenderService) {
        LOGGER.info("Creating ExchangeServer at port: " + port);
        this.matchingEngine = matchingEngine;
        this.port = port;
        this.exchangeResponseSenderService = exchangeResponseSenderService;
    }

    @Override
    public void run() {
        LOGGER.info("Starting ExchangeServer at port: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                Socket clientSocket = serverSocket.accept();
                InetSocketAddress brokerSocketAddress = (InetSocketAddress) clientSocket.getRemoteSocketAddress();
                String clientIpAddress = brokerSocketAddress.getAddress().getHostAddress();
                LOGGER.info("Accepting client connection at IP address: " + clientIpAddress);
                ExchangeConnectionHandler handler = new ExchangeConnectionHandler(matchingEngine, clientSocket, clientIpAddress, exchangeResponseSenderService);
                threadPool.execute(handler);
            }
        } catch (IOException e) {
            LOGGER.error(e.toString());
        } finally {
            LOGGER.info("Closing ExchangeServer at port: " + port);
            threadPool.shutdown();
        }
    }

}
