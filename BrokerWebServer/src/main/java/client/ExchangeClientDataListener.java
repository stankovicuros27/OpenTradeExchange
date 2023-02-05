package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ExchangeClientDataListener implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ExchangeClientDataListener.class);

    private final ObjectInputStream in;

    public ExchangeClientDataListener(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        while(true) {
            try {
                // Dummy task
                Object o = in.readObject();
                LOGGER.info(o);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
