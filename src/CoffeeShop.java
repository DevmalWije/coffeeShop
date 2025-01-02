import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoffeeShop {
    private int capacity;
    private Queue<String> orderQueue = new LinkedList<>();
    private AtomicInteger orderCounter = new AtomicInteger(1); // Unique order number
    private final Lock lock = new ReentrantLock();
    private final Condition full = lock.newCondition();
    private final Condition empty = lock.newCondition();
    private boolean open = true;

    public CoffeeShop(int capacity) {
        this.capacity = capacity;
    }

    public void placeOrder(String customerName) {
        boolean acquired = false;
        try {
            acquired = lock.tryLock(1, TimeUnit.SECONDS);
            if (acquired) {
                while (orderQueue.size() >= capacity) {
                    full.await();
                }
                String order = "Order#" + orderCounter.getAndIncrement();
                orderQueue.add(order);
                System.out.printf("[%s] %s placed successfully by %s.%n",
                        Thread.currentThread().getName(), order, customerName);
                // Notify a waiting barista to prepare the order
                empty.signalAll();
            } else {
                System.out.printf("[%s] %s could not place order due to timeout.%n",
                        Thread.currentThread().getName(), customerName);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Customer thread interrupted: " + e.getMessage());
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    public String prepareOrder() {
        boolean acquired = false;
        try {
            acquired = lock.tryLock(1, TimeUnit.SECONDS);
            if (acquired) {
                while (orderQueue.isEmpty() && open) {
                    empty.await();
                }
                if (orderQueue.isEmpty() && !open) {
                    return null; // No more orders to prepare
                }
                String order = orderQueue.poll();
                System.out.printf("[%s] %s prepared successfully.%n",
                        Thread.currentThread().getName(), order);
                // Notify a waiting customer that space is available
                full.signalAll();
                return order;
            } else {
                System.out.printf("[%s] could not prepare order due to timeout.%n",
                        Thread.currentThread().getName());
                return null;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Barista thread interrupted: " + e.getMessage());
            return null;
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    public void closeShop() {
        lock.lock();
        try {
            open = false;
            empty.signalAll(); // Wake up all waiting baristas
        } finally {
            lock.unlock();
        }
    }
}