package servlets;

import client.ExchangeClientManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;

@WebListener
public class BrokerServletContextListener implements ServletContextListener {

    private final ExchangeClientManager exchangeClientManager = new ExchangeClientManager();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Context Initialized");
        ServletContextListener.super.contextInitialized(sce);
        try {
            exchangeClientManager.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Context Destroyed");
        ServletContextListener.super.contextDestroyed(sce);
        try {
            exchangeClientManager.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
