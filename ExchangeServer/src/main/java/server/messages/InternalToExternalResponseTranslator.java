package server.messages;

import api.messages.external.ExternalSide;
import api.messages.external.response.IExternalResponse;
import api.messages.external.response.IExternalResponseFactory;
import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.IResponse;
import api.messages.responses.ITradeResponse;
import api.messages.responses.ResponseType;
import impl.messages.external.response.ExternalResponseFactory;

import java.util.ArrayList;
import java.util.List;

public enum InternalToExternalResponseTranslator {
    ;

    private static final IExternalResponseFactory externalResponseFactory = new ExternalResponseFactory();

    public static List<IExternalResponse> getExternalResponse(IResponse internalResponse) {
        if (internalResponse.getType() == ResponseType.OrderStatusResponse) {
            IOrderStatusResponse orderStatusInternalResponse = (IOrderStatusResponse) internalResponse;
            return List.of(getExternalResponseFromOrderStatusInternalResponse(orderStatusInternalResponse));
        } else if (internalResponse.getType() == ResponseType.TradeResponse) {
            ITradeResponse tradeInternalResponse = (ITradeResponse) internalResponse;
            return getExternalResponsesFromTradeInternalResponse(tradeInternalResponse);
        }
        return List.of();
    }

    private static IExternalResponse getExternalResponseFromOrderStatusInternalResponse(IOrderStatusResponse orderStatusInternalResponse) {
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

    private static List<IExternalResponse> getExternalResponsesFromTradeInternalResponse(ITradeResponse tradeResponse) {
        List<IExternalResponse> externalResponses = new ArrayList<>();
        externalResponses.add(externalResponseFactory.getTradeResponse(
                tradeResponse.getBookID(),
                tradeResponse.getBuyUserID(),
                tradeResponse.getBuyOrderID(),
                tradeResponse.getPrice(),
                ExternalSide.BUY,
                tradeResponse.getVolume()
        ));
        externalResponses.add(externalResponseFactory.getTradeResponse(
                tradeResponse.getBookID(),
                tradeResponse.getSellUserID(),
                tradeResponse.getSellOrderID(),
                tradeResponse.getPrice(),
                ExternalSide.SELL,
                tradeResponse.getVolume()
        ));
        return externalResponses;
    }

}
