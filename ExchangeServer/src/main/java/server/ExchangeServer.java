package server;

import api.core.IOrderBook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ExchangeServer implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ExchangeServer.class);

    private final IOrderBook orderBook;
    private final ExecutorService threadPool;
    private final int port;
    private final BroadcastService broadcastService;

    public ExchangeServer(IOrderBook orderBook, ExecutorService threadPool, int port, BroadcastService broadcastService) {
        LOGGER.info("Creating ExchangeServer at port: " + port);
        this.orderBook = orderBook;
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
                BrokerConnectionHandler handler = new BrokerConnectionHandler(orderBook, brokerSocket, brokerIpAddress, broadcastService);
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
