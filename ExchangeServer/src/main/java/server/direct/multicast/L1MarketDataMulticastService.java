package server.direct.multicast;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.core.Side;
import api.messages.data.IMicroFIXDataMessageFactory;
import api.messages.data.IMicroFIXL1DataMessage;
import api.messages.data.MicroFIXDataMessageConstants;
import api.messages.info.IOrderBookInfo;
import impl.core.MatchingEngine;
import impl.messages.data.MicroFIXDataMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

public class L1MarketDataMulticastService implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(L1MarketDataMulticastService.class);

    private final IMatchingEngine matchingEngine;
    private final InetAddress multicastInetAddress;
    private final int l1DataMulticastPort;
    private final int l1TimeoutMS;

    private final IMicroFIXDataMessageFactory microFIXDataMessageFactory = new MicroFIXDataMessageFactory();

    public L1MarketDataMulticastService(IMatchingEngine matchingEngine, String multicastIpAddress, int l1DataMulticastPort, int l1TimeoutMS) throws UnknownHostException {
        this.matchingEngine = matchingEngine;
        this.multicastInetAddress = InetAddress.getByName(multicastIpAddress);
        this.l1DataMulticastPort = l1DataMulticastPort;
        this.l1TimeoutMS = l1TimeoutMS;
    }

    @Override
    public void run() {
        LOGGER.info("Starting L1 Data Multicast at Ip: " + multicastInetAddress.getHostAddress() + ", port: " + l1DataMulticastPort);
        try(DatagramSocket publisherSocket = new DatagramSocket()) {
            while(true) {
                for (IOrderBook orderBook : matchingEngine.getAllOrderBooks()) {
                    IMicroFIXL1DataMessage microFIXL1DataMessage = getL1DataMessage(orderBook);
                    sendDatagramPacket(publisherSocket, microFIXL1DataMessage);
                }
                Thread.sleep(l1TimeoutMS);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private IMicroFIXL1DataMessage getL1DataMessage(IOrderBook orderBook) {
        String bookID = orderBook.getBookID();
        IOrderBookInfo orderBookInfo = orderBook.getInfo();
        double bestBuyPrice = orderBookInfo.getBestPrice(Side.BUY);
        int totalBuyVolume = orderBookInfo.getLimitCollectionInfo(Side.BUY).getVolume();
        double bestSellPrice = orderBookInfo.getBestPrice(Side.SELL);
        int totalSellVolume = orderBookInfo.getLimitCollectionInfo(Side.SELL).getVolume();
        double lastTradePrice = orderBookInfo.getLastTradePrice();
        return microFIXDataMessageFactory.getL1DataMessage(bookID, bestBuyPrice, totalBuyVolume, bestSellPrice, totalSellVolume, lastTradePrice);
    }

    private void sendDatagramPacket(DatagramSocket socket, IMicroFIXL1DataMessage message) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(MicroFIXDataMessageConstants.L1_MARKET_DATA_MESSAGE_MAX_SIZE_BYTES);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteArrayOutputStream));
        objectOutputStream.flush();
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
        byte[] serializedObject = byteArrayOutputStream.toByteArray();
        DatagramPacket datagramPacket = new DatagramPacket(serializedObject, serializedObject.length, multicastInetAddress, l1DataMulticastPort);
        socket.send(datagramPacket);
    }

}
