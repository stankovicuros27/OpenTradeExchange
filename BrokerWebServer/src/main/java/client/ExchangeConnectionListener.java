package client;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ExchangeConnectionListener implements Runnable {

    private final ObjectInputStream in;

    public ExchangeConnectionListener(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Object o = in.readObject();
                System.out.println(o);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
