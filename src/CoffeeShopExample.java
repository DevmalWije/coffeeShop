public class CoffeeShopExample {
    public static void main(String[] args) {
        int numCustomers = 10;
        int numBaristas = 5;
        CoffeeShop coffeeShop = new CoffeeShop(5);
        Thread[] threads = new Thread[numCustomers + numBaristas];
        // Creating Customer threads
        for (int i = 0; i < numCustomers; i++) {
            threads[i] = new Thread(new Customer(coffeeShop, "Customer-" + (i + 1)));
            threads[i].setName("CustomerThread-" + (i + 1));
        }
        // Creating Barista threads
        for (int i = 0; i < numBaristas; i++) {
            threads[numCustomers + i] = new Thread(new Barista(coffeeShop));
            threads[numCustomers + i].setName("BaristaThread-" + (i + 1));
        }
        // Starting threads
        for (Thread thread : threads) {
            thread.start();
        }
        // Wait for customer threads to complete
        for (int i = 0; i < numCustomers; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }
        // Close the shop after all customers have placed their orders
        coffeeShop.closeShop();
        // Wait for barista threads to complete
        for (int i = numCustomers; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }
        System.out.println("All orders completed!");
    }
}