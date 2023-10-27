package threads.c_advance.visuals;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;


// TODO WIP, CURRENTLY NOT WORKING
public class C_CoreSelectorSyncro {
    private static int coreCount = Runtime.getRuntime().availableProcessors();
    private static JSlider slider;
    private static AtomicBoolean running = new AtomicBoolean(false);
    private static Thread[] threads;
    private static AtomicInteger firstToFire = new AtomicInteger(-1); 
    private static int[] firstFiredCount;
    private static JFrame resultFrame;
    private static JLabel[] resultLabels;
    private static int firedCount = 0;
    private static JLabel firedLabel;
    private static Semaphore[] semaphores; 
    private static JCheckBox orderedExecutionCheckbox;

    public static void mainNotWorking(String[] args) {
        JFrame frame = new JFrame("Select Number of Cores");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        frame.add(centerPanel, BorderLayout.CENTER);

        JLabel label = new JLabel("Number of available cores: " + coreCount, JLabel.CENTER);
        centerPanel.add(label);

        slider = new JSlider(JSlider.HORIZONTAL, 1, coreCount, coreCount);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        centerPanel.add(slider);

        orderedExecutionCheckbox = new JCheckBox("Ordered Execution");
        frame.add(orderedExecutionCheckbox, BorderLayout.NORTH);

        JButton startButton = new JButton("Start Threads");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopThreads();
                startThreads(slider.getValue());
                createResultsWindow(slider.getValue());
            }
        });
        frame.add(startButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void startThreads(int num) {
        running.set(true);
        threads = new Thread[num];
        firstFiredCount = new int[num];
        semaphores = new Semaphore[num];
        for (int i = 0; i < num; i++) {
            semaphores[i] = new Semaphore(0);
        }

        // Allow the first thread to start.
        semaphores[0].release();

        for (int i = 0; i < num; i++) {
            int threadNumber = i;
            threads[i] = new Thread(() -> {
                int count = 0;
                while (running.get()) {

                    if (firstToFire.compareAndSet(-1, threadNumber)) {
                        firstFiredCount[threadNumber]++;
                    }

                    try {
                        semaphores[threadNumber].acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    count++;

                    System.out.println("This is thread \"" + (char) ('A' + threadNumber) + "\", sending message number \"" + count + "\"");

                    try {
                    	if (orderedExecutionCheckbox.isSelected()) {
                    	    Thread.sleep(100); // small delay to demonstrate the order
                    	    semaphores[(threadNumber + 1) % num].release();
                    	} else {
                    	    semaphores[(threadNumber + 1) % num].release(); // Release the next thread's semaphore

                    	    if (threadNumber == num - 1) { // Last thread releases all the other semaphores except its own
                    	        for (int j = 0; j < num - 1; j++) {
                    	            semaphores[j].release();
                    	        }
                    	    }
                    	}

                        if (count % num == 0) {
                            firstToFire.set(-1);
                            firedCount++;
                            Thread.sleep(900); // to fill the gap of the 1-second total delay
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            threads[i].start();
        }
    }


    private static void stopThreads() {
        running.set(false);
        if (threads != null) {
            for (Thread thread : threads) {
                try {
                    thread.join(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void createResultsWindow(int numThreads) {
        if (resultFrame != null) {
            resultFrame.dispose();
        }

        resultFrame = new JFrame("First Thread to Fire");
        resultFrame.setSize(300, 20 * numThreads + 50);
        resultFrame.setLayout(new GridLayout(numThreads + 2, 1));

        firedLabel = new JLabel("Total Cycles: " + firedCount, JLabel.CENTER);
        resultFrame.add(firedLabel);

        JLabel title = new JLabel("First Thread to Fire", JLabel.CENTER);
        resultFrame.add(title);

        resultLabels = new JLabel[numThreads];
        for (int i = 0; i < numThreads; i++) {
            resultLabels[i] = new JLabel((char) ('A' + i) + " : " + firstFiredCount[i], JLabel.CENTER);
            resultFrame.add(resultLabels[i]);
        }

        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < numThreads; i++) {
                    resultLabels[i].setText((char) ('A' + i) + " : " + firstFiredCount[i]);
                    firedLabel.setText("Total Cycles: " + firedCount);
                }
            }
        });
        timer.start();

        resultFrame.setVisible(true);
    }
}
