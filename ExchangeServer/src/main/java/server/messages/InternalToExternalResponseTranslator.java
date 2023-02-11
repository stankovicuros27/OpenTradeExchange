package server.messages;

import api.messages.trading.MicroFIXSide;
import api.messages.trading.response.IMicroFIXResponse;
import api.messages.trading.response.IMicroFIXResponseFactory;
import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.IResponse;
import api.messages.responses.ITradeResponse;
import api.messages.responses.ResponseType;
import impl.messages.trading.response.MicroFIXResponseFactory;

import java.util.ArrayList;
import java.util.List;

public enum InternalToExternalResponseTranslator {
    ;

    private static final IMicroFIXResponseFactory externalResponseFactory = new MicroFIXResponseFactory();

    public static List<IMicroFIXResponse> getExternalResponse(IResponse internalResponse) {
        if (internalResponse.getType() == ResponseType.OrderStatusResponse) {
            IOrderStatusResponse orderStatusInternalResponse = (IOrderStatusResponse) internalResponse;
            return List.of(getExternalResponseFromOrderStatusInternalResponse(orderStatusInternalResponse));
        } else if (internalResponse.getType() == ResponseType.TradeResponse) {
            ITradeResponse tradeInternalResponse = (ITradeResponse) internalResponse;
            return getExternalResponsesFromTradeInternalResponse(tradeInternalResponse);
        }
        return List.of();
    }

    private static IMicroFIXResponse getExternalResponseFromOrderStatusInternalResponse(IOrderStatusResponse orderStatusInternalResponse) {
        switch (orderStatusInternalResponse.getStatus()) {
            case PLACED_ORDER -> {
                return externalResponseFactory.getPlacedOrderResponse(
                    orderStatusInternalResponse.getBookID(),
                    orderStatusInternalResponse.getUserID(),
                    orderStatusInternalResponse.getOrderID()
                );
            }
            case CANCELLED_ORDER -> {
                return externalResponseFactory.getCancelledOrderResponse(
                        orderStatusInternalResponse.getBookID(),
                        orderStatusInternalResponse.getUserID(),
                        orderStatusInternalResponse.getOrderID()
                );
            }
            case CLOSED_ORDER -> {
                return externalResponseFactory.getClosedOrderResponse(
                        orderStatusInternalResponse.getBookID(),
                        orderStatusInternalResponse.getUserID(),
                        orderStatusInternalResponse.getOrderID()
                );
            }
            case ERROR_ORDER, NULL_ORDER -> {
                return externalResponseFactory.getErrorResponse(
                        orderStatusInternalResponse.getBookID(),
                        orderStatusInternalResponse.getUserID(),
                        orderStatusInternalResponse.getOrderID()
                );
            }
        }
        return externalResponseFactory.getErrorResponse(
                "ERROR",
                -1,
                -1
        );
    }

    private static List<IMicroFIXResponse> getExternalResponsesFromTradeInternalResponse(ITradeResponse tradeResponse) {
        List<IMicroFIXResponse> externalResponses = new ArrayList<>();
        externalResponses.add(externalResponseFactory.getTradeResponse(
                tradeResponse.getBookID(),
                tradeResponse.getBuyUserID(),
                tradeResponse.getBuyOrderID(),
                tradeResponse.getPrice(),
                MicroFIXSide.BUY,
                tradeResponse.getVolume()
        ));
        externalResponses.add(externalResponseFactory.getTradeResponse(
                tradeResponse.getBookID(),
                tradeResponse.getSellUserID(),
                tradeResponse.getSellOrderID(),
                tradeResponse.getPrice(),
                MicroFIXSide.SELL,
                tradeResponse.getVolume()
        ));
        return externalResponses;
    }

}
