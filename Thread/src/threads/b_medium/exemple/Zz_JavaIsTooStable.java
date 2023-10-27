package threads.b_medium.exemple;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Zz_JavaIsTooStable {
	
	
	static String sharedString = "1234567890" ;
	
	public static void main(String[] args) {
 
		AtomicBoolean threadShouldRun = new AtomicBoolean(true); 
		
		for (int i = 0; i < 10; i++) {
			char threadName = ((char)('A' + i)) ; 
			new Thread(() -> {
	            while (threadShouldRun.get()) {
	                sharedString += threadName ;
	                sharedString = removeRandomChar(sharedString);  
	            }
	        }).start();
		}
        
		
		new Thread(() -> {
            while (threadShouldRun.get()) {
               System.out.println(sharedString);
            }
        }).start();
		
		
        new Thread(() -> {
            System.out.println("I'll kill you all !");
            try {
                Thread.sleep(10000);  
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threadShouldRun.set(false) ; 
            System.out.println("DIE");
        }).start();
    }
	
	 public static String removeRandomChar(String str) {
	        Random random = new Random();
	        int randomIndex = random.nextInt(str.length());
	        return str.substring(0, randomIndex) + str.substring(randomIndex + 1);
	    }
}
