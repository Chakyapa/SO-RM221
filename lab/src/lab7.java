public class Client extends Thread {
    private final Barbershop shop;

    public Client(Barbershop shop, String name) {
        super(name);
        this.shop = shop;
    }

    @Override
    public void run() {
        if (!shop.enterBarbershop(this)) {
            System.out.println(getName() + " pleacă. Nu sunt scaune libere.");
        }
    }
}
