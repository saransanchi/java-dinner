import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {
        //String[] names = {"Plato", "Aristotle", "Cicero", "Confucius", "Eratosthenes"};
        Fork[] fork = new Fork[5];
       final Philosopher[] philosopher = new Philosopher[5];

        for (int i = 0; i < fork.length; i++) {
            fork[i] = new Fork();
        }

        for (int i = 0; i < philosopher.length; i++) {
            Fork fork_left = fork[i];
            Fork fork_right = fork[(i + 1) % fork.length];

            if (i == philosopher.length - 1) {

                // The last philosopher picks up the right fork first
                philosopher[i] = new Philosopher(fork_right,fork_left);
            } else {
                philosopher[i] = new Philosopher(fork_left,fork_right);
            }
            Thread t
                    = new Thread( philosopher[i],"philosopher" + (i + 1));
            t.start();
        }
    }
}

class Fork {
    public  Semaphore fork = new Semaphore(1);
    public int id ;

    Fork(int id) {
        this.id = id;
    }

    public Fork() {

    }

    public int getId() {
        return id;
    }

    public boolean take() {
        return fork.tryAcquire();
    }

    public void putDown() {
        fork.release();
    }
}

class Philosopher extends Thread {

    private Fork fork_left;
    private Fork fork_right;


    Philosopher(Fork fork_left, Fork fork_right) {
        this.fork_left = fork_left;
        this.fork_right = fork_right;

    }



    private void doAction(String action) throws InterruptedException {
        System.out.println(
                Thread.currentThread().getName() + " " + action);
        Thread.sleep(((int) (Math.random() * 100)));
    }

    @Override
    public void run() {
        try {
            while (true) {

                // thinking
                doAction(System.nanoTime() + ": Thinking");
                synchronized (fork_left) {
                    doAction(
                            System.nanoTime()
                                    + ": Picked up left fork");
                    synchronized (fork_right) {
                        // eating
                        doAction(
                                System.nanoTime()
                                        + ": Picked up right fork - eating");

                        doAction(
                                System.nanoTime()
                                        + ": Put down right fork");
                    }

                    // Back to thinking
                    doAction(
                            System.nanoTime()
                                    + ": Put down left fork. Back to thinking");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }
}
