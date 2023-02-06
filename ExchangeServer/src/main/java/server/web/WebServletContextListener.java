package server.web;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import server.ExchangeServerContext;
import server.direct.ExchangeServerManager;

@WebListener
public class WebServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        ExchangeServerContext.initialize();
        ExchangeServerManager exchangeServerManager = new ExchangeServerManager(ExchangeServerContext.getInstance());
        exchangeServerManager.startDirectExchangeServer();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
