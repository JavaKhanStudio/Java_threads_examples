package threads.a_simple.exemple;

public class Ba_MultiThread_lambda {
	public static void main(String[] args) {
     
        new Thread(() -> {
            while (true) {
                System.out.println("Hello world! I am Thread A");
                try {
                    Thread.sleep(1000);  // Sleeps for 1000 milliseconds (1 second)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

      
        new Thread(() -> {
            while (true) {
                System.out.println("Hello world! I am Thread B");
                try {
                    Thread.sleep(1000); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

       
        new Thread(() -> {
            while (true) {
                System.out.println("Hello world! I am Thread C");
                try {
                    Thread.sleep(1000);  
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
