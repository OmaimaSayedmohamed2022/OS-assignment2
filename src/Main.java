//import java.util.ArrayList;
//import java.util.List;
//
//class Router {
//    private List<Connection> connections;
//    private Semaphore connectionSemaphore;
//
//    public Router(int maxConnections) {
//        connections = new ArrayList<>();
//        connectionSemaphore = new Semaphore(maxConnections);
//    }
//
//    public void occupyConnection() throws InterruptedException {
//        connectionSemaphore.acquire();
//        Connection connection = new Connection();
//        connections.add(connection);
//        System.out.println("Device has occupied a connection.");
//    }
//
//    public void releaseConnection() {
//        Connection connection = connections.isEmpty() ? null : connections.remove(0);
//        if (connection != null) {
//            connectionSemaphore.release();
//            System.out.println("Device has released the connection.");
//        } else {
//            System.out.println("No connection to release.");
//        }
//    }
//
//    private static class Connection {
//        // Represents a connection, you can extend this class as needed
//    }
//
//    private static class Semaphore {
//        private int permits;
//
//        public Semaphore(int initialPermits) {
//            this.permits = initialPermits;
//        }
//
//        public synchronized void acquire() throws InterruptedException {
//            while (permits == 0) {
//                wait();
//            }
//            permits--;
//        }
//
//        public synchronized void release() {
//            permits++;
//            notify();
//        }
//    }
//
//    public static void main(String[] args) {
//        Router router = new Router(5); // Adjust the maximum number of connections
//        try {
//            router.occupyConnection();
//            // Perform operations with the connection
//            router.releaseConnection();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
import java.util.ArrayList;
import java.util.List;

class Router {
    private List<Connection> connections;
    private Semaphore connectionSemaphore;

    public Router(int maxConnections) {
        connections = new ArrayList<>();
        connectionSemaphore = new Semaphore(maxConnections);
    }

    public void occupyConnection() throws InterruptedException {
        connectionSemaphore.P(); // Acquire a connection permit
        Connection connection = new Connection();
        connections.add(connection);
        System.out.println("Device has occupied a connection.");
    }

    public void releaseConnection() {
        Connection connection = connections.isEmpty() ? null : connections.remove(0);
        if (connection != null) {
            connectionSemaphore.V(); // Release a connection permit
            System.out.println("Device has released the connection.");
        } else {
            System.out.println("No connection to release.");
        }
    }

    private static class Connection {
        // Represents a connection, you can extend this class as needed
    }

    private static class Semaphore {
        protected int value = 0;

        protected Semaphore() {
            value = 0;
        }

        protected Semaphore(int initial) {
            value = initial;
        }

        public synchronized void P() {
            value--;
            if (value < 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public synchronized void V() {
            value++;
            if (value <= 0) {
                notify();
            }
        }
    }

    public static void main(String[] args) {
        Router router = new Router(4);
        try {
            router.occupyConnection();
            router.releaseConnection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
enum Activity
{
    connect,
    perform_online_activity,
    disconnect

}

class Device
{
    public static Router router;
    public int id;
    private String name;
    private String type;
    private Activity activity;

    public Device(String name, String type, Router router){
        this.name=name;
        this.type=type;
        Device.router=router;
    }
    public String getDeviceName() {
        return name;
    }

    public void setDeviceName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
