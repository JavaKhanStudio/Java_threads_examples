package threads.b_medium.exemple.error;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Concurrent_Error 
{
    public static void main(String[] args) 
    {
        List<String> list = new ArrayList<>();

        // Thread 1: Ajouter un objet 
        new Thread(() -> {
            while (true) {
                list.add("Item");
                try {
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Thread 2: Iterates over the list and reads items
        new Thread(() -> {
            while (true) {
                Iterator<String> iterator = list.iterator();
                while (iterator.hasNext()) {
                    System.out.println(iterator.next());
                    try {
                        Thread.sleep(50); 
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}