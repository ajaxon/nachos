package nachos.threads;

import nachos.machine.Machine;

public class AlarmTester {
	public static void selfTest() {
        System.out.println("****START ALARM TESTING****");
        
        final Alarm alarm = new Alarm();
        
        KThread t1 = new KThread(new Runnable() {
	        public void run() {
	            for (int k=0; k<6000; k++) {
	                if (k % 1000 == 0)
	                    System.out.println("T1 at " + k / 1000);
	                else if (k == 1499) {
	                    System.out.println("putting T1 to waiting queue until 1000 ticks at "+Machine.timer().getTime());
	                    alarm.waitUntil(1000);
	                }
	            }
	        }
	    });
        
        KThread t2 = new KThread(new Runnable() {
            public void run() {
            	for (int k = 0; k<5000; k++)
                    if (k % 1000 == 0) {
                        System.out.println("T2 at "+ k / 1000);
                    }
                    else if (k == 2499) {
						System.out.println("putting T2 to waiting queue until 200 ticks at "+Machine.timer().getTime());
						alarm.waitUntil(200);
                    }
            }
        });
        
        KThread t3 = new KThread(new Runnable() {
            public void run() {
            	for (int k = 0; k<2500; k++)
                    if (k % 1000 == 0) {
                        System.out.println("T3 at "+ k / 1000);
                    } else if (k==1501) {
                        System.out.println("putting T3 in queue at "+Machine.timer().getTime()+" for 0 ticks");
                        alarm.waitUntil(0);
                    }
            }
        });
        t1.setName("T1");
        t2.setName("T2");
        t3.setName("T3");
        
        t1.fork();
        t2.fork();
        t3.fork();
        
        t1.join();
        t2.join();
        t3.join();
        
        System.out.println("****ALARM TESTING FINISH****");
    }
}
