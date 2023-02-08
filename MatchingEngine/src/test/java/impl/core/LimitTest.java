package impl.core;

import api.core.ILimit;
import api.core.IOrderLookupCache;
import api.messages.internal.responses.IResponse;
import api.messages.internal.responses.OrderResponseStatus;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.internal.requests.PlaceOrderRequest;
import impl.messages.internal.responses.OrderStatusResponse;
import impl.messages.internal.responses.TradeResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class LimitTest {

    private static final String BOOK_ID = "TestBookID";
    private static final int DUMMY_TIMESTAMP_SECONDS = 1002;

    private final ITimestampProvider mockTimestampProvider = Mockito.mock(ITimestampProvider.class);
    private final IOrderLookupCache mockOrderLookupCache = Mockito.mock(IOrderLookupCache.class);

    @Before
    public void setupTests() {
        Mockito.when(mockTimestampProvider.getTimestampNow()).thenReturn(DUMMY_TIMESTAMP_SECONDS);
    }

    @Test
    public void testAddOrder() {
        ILimit limit = new Limit(BOOK_ID, Side.BUY, 10, mockOrderLookupCache, mockTimestampProvider);
        assertTrue(limit.isEmpty());
        assertEquals(limit.getVolume(), 0);

        IResponse response;

        Order firstOrder = new Order(BOOK_ID, 0, 0, 10, 1000, Side.BUY, 20, 0);
        response = limit.addOrder(firstOrder);
        assertEquals(response, new OrderStatusResponse(BOOK_ID, firstOrder.getUserID(), firstOrder.getOrderID(), OrderResponseStatus.PLACED_ORDER, DUMMY_TIMESTAMP_SECONDS));
        verify(mockOrderLookupCache).addOrder(firstOrder);
        assertEquals(limit.getVolume(), 20);
        assertEquals(limit.getNumberOfOrders(), 1);

        Order secondOrder = new Order(BOOK_ID, 0, 1, 10, 1000, Side.BUY, 10, 0);
        response = limit.addOrder(secondOrder);
        assertEquals(response, new OrderStatusResponse(BOOK_ID, secondOrder.getUserID(), secondOrder.getOrderID(), OrderResponseStatus.PLACED_ORDER, DUMMY_TIMESTAMP_SECONDS));
        verify(mockOrderLookupCache).addOrder(secondOrder);
        assertEquals(limit.getVolume(), 30);
        assertEquals(limit.getNumberOfOrders(), 2);

        Order thirdOrder = new Order(BOOK_ID, 0, 2, 10, 1000, Side.BUY, 30, 25);
        response = limit.addOrder(thirdOrder);
        assertEquals(response, new OrderStatusResponse(BOOK_ID, thirdOrder.getUserID(), thirdOrder.getOrderID(), OrderResponseStatus.PLACED_ORDER, DUMMY_TIMESTAMP_SECONDS));
        verify(mockOrderLookupCache).addOrder(thirdOrder);
        assertEquals(limit.getVolume(), 35);
        assertEquals(limit.getNumberOfOrders(), 3);
    }

    @Test
    public void testCancelOrder() {
        ILimit limit = new Limit(BOOK_ID, Side.SELL, 10, mockOrderLookupCache, mockTimestampProvider);
        assertTrue(limit.isEmpty());
        assertEquals(limit.getVolume(), 0);

        IResponse response;

        Order firstOrder = new Order(BOOK_ID, 0, 0, 10, 1000, Side.SELL, 20, 0);
        response = limit.addOrder(firstOrder);
        assertEquals(response, new OrderStatusResponse(BOOK_ID, firstOrder.getUserID(), firstOrder.getOrderID(), OrderResponseStatus.PLACED_ORDER, DUMMY_TIMESTAMP_SECONDS));
        verify(mockOrderLookupCache).addOrder(firstOrder);
        assertEquals(limit.getVolume(), 20);
        assertEquals(limit.getNumberOfOrders(), 1);

        response = limit.cancelOrder(firstOrder);
        assertEquals(response, new OrderStatusResponse(BOOK_ID, firstOrder.getUserID(), firstOrder.getOrderID(), OrderResponseStatus.CANCELLED_ORDER, DUMMY_TIMESTAMP_SECONDS));
        verify(mockOrderLookupCache).removeOrder(firstOrder);
        assertTrue(limit.isEmpty());
        assertEquals(limit.getVolume(), 0);

        Order secondOrder = new Order(BOOK_ID, 0, 1, 10, 1000, Side.SELL, 10, 0);
        response = limit.addOrder(secondOrder);
        assertEquals(response, new OrderStatusResponse(BOOK_ID, secondOrder.getUserID(), secondOrder.getOrderID(), OrderResponseStatus.PLACED_ORDER, DUMMY_TIMESTAMP_SECONDS));
        verify(mockOrderLookupCache).addOrder(secondOrder);
        assertEquals(limit.getVolume(), 10);
        assertEquals(limit.getNumberOfOrders(), 1);

        Order thirdOrder = new Order(BOOK_ID, 0, 2, 10, 1000, Side.SELL, 30, 25);
        response = limit.addOrder(thirdOrder);
        assertEquals(response, new OrderStatusResponse(BOOK_ID, thirdOrder.getUserID(), thirdOrder.getOrderID(), OrderResponseStatus.PLACED_ORDER, DUMMY_TIMESTAMP_SECONDS));
        verify(mockOrderLookupCache).addOrder(thirdOrder);
        assertEquals(limit.getVolume(), 15);
        assertEquals(limit.getNumberOfOrders(), 2);

        response = limit.cancelOrder(thirdOrder);
        assertEquals(response, new OrderStatusResponse(BOOK_ID, thirdOrder.getUserID(), thirdOrder.getOrderID(), OrderResponseStatus.CANCELLED_ORDER, DUMMY_TIMESTAMP_SECONDS));
        verify(mockOrderLookupCache).removeOrder(thirdOrder);
        assertEquals(limit.getVolume(), 10);
        assertEquals(limit.getNumberOfOrders(), 1);

        response = limit.cancelOrder(secondOrder);
        assertEquals(response, new OrderStatusResponse(BOOK_ID, secondOrder.getUserID(), secondOrder.getOrderID(), OrderResponseStatus.CANCELLED_ORDER, DUMMY_TIMESTAMP_SECONDS));
        verify(mockOrderLookupCache).removeOrder(secondOrder);
        assertTrue(limit.isEmpty());
        assertEquals(limit.getVolume(), 0);
    }

    @Test
    public void testMatchCounterOrder() {
        ILimit limit = new Limit(BOOK_ID, Side.BUY, 10, mockOrderLookupCache, mockTimestampProvider);

        Order firstBuyOrder = new Order(BOOK_ID, 0, 0, 10, 1000, Side.BUY, 20, 0);
        limit.addOrder(firstBuyOrder);
        Order secondBuyOrder = new Order(BOOK_ID, 0, 1, 10, 1000, Side.BUY, 10, 0);
        limit.addOrder(secondBuyOrder);

        PlaceOrderRequest firstSellOrderRequest = new PlaceOrderRequest(BOOK_ID, 1, 0, 9,  Side.SELL, 5, 0, 1000);
        List<IResponse> responses = limit.matchOrderRequest(firstSellOrderRequest);
        assertThat(responses).hasSize(2).hasSameElementsAs(List.of(
                new TradeResponse(BOOK_ID, 0, 0, 1, 0, 10, 5, DUMMY_TIMESTAMP_SECONDS),
                new OrderStatusResponse(BOOK_ID, 1, 0, OrderResponseStatus.CLOSED_ORDER, DUMMY_TIMESTAMP_SECONDS)
        ));
        assertEquals(limit.getVolume(), 25);
        assertEquals(limit.getNumberOfOrders(), 2);

        PlaceOrderRequest secondSellOrderRequest = new PlaceOrderRequest(BOOK_ID, 1, 1, 8, Side.SELL, 25, 0, 1000);
        responses = limit.matchOrderRequest(secondSellOrderRequest);
        assertThat(responses).hasSize(5).hasSameElementsAs(List.of(
                new TradeResponse(BOOK_ID, 0, 0, 1, 1, 10, 15, DUMMY_TIMESTAMP_SECONDS),
                new TradeResponse(BOOK_ID, 0, 1, 1, 1, 10, 10, DUMMY_TIMESTAMP_SECONDS),
                new OrderStatusResponse(BOOK_ID, 0, 0, OrderResponseStatus.CLOSED_ORDER, DUMMY_TIMESTAMP_SECONDS),
                new OrderStatusResponse(BOOK_ID, 0, 1, OrderResponseStatus.CLOSED_ORDER, DUMMY_TIMESTAMP_SECONDS),
                new OrderStatusResponse(BOOK_ID, 1, 1, OrderResponseStatus.CLOSED_ORDER, DUMMY_TIMESTAMP_SECONDS)
        ));
        assertTrue(limit.isEmpty());
        assertEquals(limit.getNumberOfOrders(), 0);


        Order thirdBuyOrder = new Order(BOOK_ID, 0, 2, 10, 1000, Side.BUY, 100, 0);
        limit.addOrder(thirdBuyOrder);

        PlaceOrderRequest thirdSellOrderRequest = new PlaceOrderRequest(BOOK_ID, 1, 2, 7, Side.SELL, 50, 0, 1000);
        responses = limit.matchOrderRequest(thirdSellOrderRequest);
        assertThat(responses).hasSize(2).hasSameElementsAs(List.of(
                new TradeResponse(BOOK_ID, 0, 2, 1, 2, 10, 50, DUMMY_TIMESTAMP_SECONDS),
                new OrderStatusResponse(BOOK_ID, 1, 2, OrderResponseStatus.CLOSED_ORDER, DUMMY_TIMESTAMP_SECONDS)
        ));
        assertEquals(limit.getVolume(), 50);
        assertEquals(limit.getNumberOfOrders(), 1);
    }

}
