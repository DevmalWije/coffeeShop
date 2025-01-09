public class Barista implements Runnable {
    private final CoffeeShop coffeeShop;
    public Barista(CoffeeShop coffeeShop) {
        this.coffeeShop = coffeeShop;
    }
    @Override
    public void run() {
        while (true) {
            String order = coffeeShop.prepareOrder();
            if (order != null) {
                System.out.printf("[%s] %s completed.%n",
                        Thread.currentThread().getName(), order);
            } else {
                break; // Exit the loop if no more orders are available
            }
        }
    }
}