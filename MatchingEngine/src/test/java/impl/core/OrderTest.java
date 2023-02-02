package impl.core;

import api.messages.responses.ITradeResponse;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.requests.PlaceOrderRequest;
import impl.messages.responses.TradeResponse;
import impl.time.InstantTimestampProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private static final int DUMMY_TIMESTAMP_SECONDS = 1002;
    private final ITimestampProvider mockTimestampProvider = Mockito.mock(InstantTimestampProvider.class);

    @Before
    public void setupTests() {
        Mockito.when(mockTimestampProvider.getTimestampNow()).thenReturn(DUMMY_TIMESTAMP_SECONDS);
    }

    @Test
    public void testFillVolume() {
        Order order = new Order(0, 0, 10.5, 1000, Side.BUY, 20, 0);
        assertEquals(order.getUnfilledVolume(), 20);
        order.fillVolume(10);
        assertEquals(order.getUnfilledVolume(), 10);
        order.fillVolume(5);
        assertEquals(order.getUnfilledVolume(), 5);
    }

    @Test
    public void testIsClosed() {
        Order order = new Order(0, 0, 10.5, 1000, Side.BUY, 20, 0);
        assertFalse(order.isClosed());
        order.fillVolume(20);
        assertTrue(order.isClosed());
    }

    @Test
    public void testMatchBuySidePlaceOrderRequest() {
        Order sellOrder = new Order(0, 0, 10, 1000, Side.SELL, 20, 0);
        PlaceOrderRequest buyOrderRequest = new PlaceOrderRequest(1, 0, 11, Side.BUY, 10, 0, 1001);
        ITradeResponse trade = Order.matchOrderRequest(sellOrder, buyOrderRequest, mockTimestampProvider);
        assertEquals(trade, new TradeResponse(1, 0, 0, 0, 10, 10, DUMMY_TIMESTAMP_SECONDS));
        assertTrue(buyOrderRequest.isMatched());
        assertFalse(sellOrder.isClosed());
        assertEquals(sellOrder.getUnfilledVolume(), 10);

        sellOrder = new Order(0, 0, 10, 1000, Side.SELL, 20, 10);
        buyOrderRequest = new PlaceOrderRequest(1, 0, 11, Side.BUY, 10, 0, 1001);
        trade = Order.matchOrderRequest(sellOrder, buyOrderRequest, mockTimestampProvider);
        assertEquals(trade, new TradeResponse(1, 0, 0, 0, 10, 10, DUMMY_TIMESTAMP_SECONDS));
        assertTrue(buyOrderRequest.isMatched());
        assertTrue(sellOrder.isClosed());

        sellOrder = new Order(0, 0, 20, 1000, Side.SELL, 100, 0);
        buyOrderRequest = new PlaceOrderRequest(1, 0, 21, Side.BUY, 100, 0, 1001);
        trade = Order.matchOrderRequest(sellOrder, buyOrderRequest, mockTimestampProvider);
        assertEquals(trade, new TradeResponse(1, 0, 0, 0, 20, 100, DUMMY_TIMESTAMP_SECONDS));
        assertTrue(buyOrderRequest.isMatched());
        assertTrue(sellOrder.isClosed());
    }

    @Test
    public void testMatchSellSidePlaceOrderRequest() {
        Order buyOrder = new Order(0, 0, 11, 1000, Side.BUY, 20, 0);
        PlaceOrderRequest sellOrderRequest = new PlaceOrderRequest(1, 0, 10, Side.SELL, 10, 0, 1001);
        ITradeResponse trade = Order.matchOrderRequest(buyOrder, sellOrderRequest, mockTimestampProvider);
        assertEquals(trade, new TradeResponse(0, 0, 1, 0, 11, 10, DUMMY_TIMESTAMP_SECONDS));
        assertTrue(sellOrderRequest.isMatched());
        assertFalse(buyOrder.isClosed());
        assertEquals(buyOrder.getUnfilledVolume(), 10);

        buyOrder = new Order(0, 0, 11, 1000, Side.BUY, 20, 10);
        sellOrderRequest = new PlaceOrderRequest(1, 0, 10, Side.SELL, 10, 0, 1001);
        trade = Order.matchOrderRequest(buyOrder, sellOrderRequest, mockTimestampProvider);
        assertEquals(trade, new TradeResponse(0, 0, 1, 0, 11, 10, DUMMY_TIMESTAMP_SECONDS));
        assertTrue(sellOrderRequest.isMatched());
        assertTrue(buyOrder.isClosed());

        buyOrder = new Order(0, 0, 21, 1000, Side.BUY, 100, 0);
        sellOrderRequest = new PlaceOrderRequest(1, 0, 20, Side.SELL, 100, 0, 1001);
        trade = Order.matchOrderRequest(buyOrder, sellOrderRequest, mockTimestampProvider);
        assertEquals(trade, new TradeResponse(0, 0, 1, 0, 21, 100, DUMMY_TIMESTAMP_SECONDS));
        assertTrue(sellOrderRequest.isMatched());
        assertTrue(buyOrder.isClosed());
    }

}
