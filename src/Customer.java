public class Customer implements Runnable {
    private final CoffeeShop coffeeShop;
    private final String customerName;
    public Customer(CoffeeShop coffeeShop, String customerName) {
        this.coffeeShop = coffeeShop;
        this.customerName = customerName;
    }
    @Override
    public void run() {
        coffeeShop.placeOrder(customerName);
    }
}