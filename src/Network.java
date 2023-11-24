import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.FileWriter;
import java.io.IOException;

class Router {
    private List<Connection> connections;
    private Semaphore connectionSemaphore;

    public Router(int maxConnections) {
        connections = new ArrayList<>();
        connectionSemaphore = new Semaphore(maxConnections);
    }

    public void occupyConnection(Device device) throws InterruptedException {
        connectionSemaphore.P(); // Acquire a connection permit
        Connection connection = new Connection(device);
        connections.add(connection);
        writeToOutput("- " + device.getDeviceName() + "(" + device.getType() + ") arrived");
        writeToOutput("- Connection " + connection.getId() + ": " + device.getDeviceName() + " Occupied");
    }

    public void releaseConnection(Device device) {
        Connection connection = findConnection(device);
        if (connection != null) {
            connections.remove(connection);
            connectionSemaphore.V(); // Release a connection permit
            writeToOutput("- " + device.getDeviceName() + " Logged out");
        } else {
            writeToOutput("- " + device.getDeviceName() + " does not have a connection to disconnect.");
        }
    }

    public Connection findConnection(Device device) {
        for (Connection connection : connections) {
            if (connection.getDevice() == device) {
                return connection;
            }
        }
        return null;
    }

    private void writeToOutput(String message) {
        try {
            FileWriter writer = new FileWriter("output.txt", true);
            writer.write(message + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Semaphore {
        private int permits;

        public Semaphore(int initialPermits) {
            this.permits = initialPermits;
        }

        public synchronized void P() throws InterruptedException {
            while (permits == 0) {
                wait();
            }
            permits--;
        }

        public synchronized void V() {
            permits++;
            notify();
        }
    }

    public class Connection {
        private static int connectionIdCounter = 1;
        private int id;
        private Device device;

        public Connection(Device device) {
            this.id = connectionIdCounter++;
            this.device = device;
        }

        public int getId() {
            return id;
        }

        public Device getDevice() {
            return device;
        }
    }
}

class Device extends Thread {
    private String name;
    private String type;
    private Router router;
    private int id;

    public Device(String name, String type, Router router, int id) {
        this.name = name;
        this.type = type;
        this.router = router;
        this.id = id;
    }

    public String getDeviceName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public void run() {
        try {
            router.occupyConnection(this);
            writeToOutput("- Connection " + router.findConnection(this).getId() + ": " + name + " login");

            // Simulate online activity
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 3000));
            writeToOutput("- Connection " + router.findConnection(this).getId() + ": " + name + " performs online activity");

            router.releaseConnection(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void writeToOutput(String message) {
        try {
            FileWriter writer = new FileWriter("output.txt", true);
            writer.write(message + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Network {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = scanner.nextInt();

        System.out.println("What is the number of devices Clients want to connect?");
        int totalDevices = scanner.nextInt();

        Router router = new Router(maxConnections);
        Device[] devices = new Device[totalDevices];

        for (int i = 0; i < totalDevices; i++) {
            System.out.println("Enter the name and type of device : ");
            String deviceName = scanner.next();
            String deviceType = scanner.next();

            devices[i] = new Device(deviceName, deviceType, router, i + 1);
        }

        for (Device device : devices) {
            device.start();
        }

        scanner.close();
    }
}
