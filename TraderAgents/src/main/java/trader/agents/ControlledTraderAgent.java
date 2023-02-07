package trader.agents;

public abstract class ControlledTraderAgent implements ITraderAgent {

    private static int GLOBAL_ID = 0;

    protected final int id = GLOBAL_ID++;
    protected double priceBase;
    protected double priceDeviation;
    protected int volumeBase;
    protected int volumeDeviation;
    protected int maxOrders;

    public ControlledTraderAgent(double priceBase, double priceDeviation, int volumeBase, int volumeDeviation, int maxOrders) {
        this.priceBase = priceBase;
        this.priceDeviation = priceDeviation;
        this.volumeBase = volumeBase;
        this.volumeDeviation = volumeDeviation;
        this.maxOrders = maxOrders;
    }

    public double getPriceBase() {
        return priceBase;
    }

    public void setPriceBase(double priceBase) {
        this.priceBase = priceBase;
    }

    public double getPriceDeviation() {
        return priceDeviation;
    }

    public void setPriceDeviation(double priceDeviation) {
        this.priceDeviation = priceDeviation;
    }

    public int getVolumeBase() {
        return volumeBase;
    }

    public void setVolumeBase(int volumeBase) {
        this.volumeBase = volumeBase;
    }

    public int getVolumeDeviation() {
        return volumeDeviation;
    }

    public void setVolumeDeviation(int volumeDeviation) {
        this.volumeDeviation = volumeDeviation;
    }

    public int getMaxOrders() {
        return maxOrders;
    }

    public void setMaxOrders(int maxOrders) {
        this.maxOrders = maxOrders;
    }

}
