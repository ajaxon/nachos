package nachos.threads;
import nachos.ag.BoatGrader;

public class Boat
{
	static BoatGrader bg;
	private static boolean done;
	//private static Lock oahuLock, molokaiLock, boatLock;
	//private static Condition2 oahuCond, molokaiCond, boatCond;
	//private static Lock adultLock, childOahuLock, childMolokaiLock;
	//private static Condition2 adultCond, childOahuCond, childMolokaiCond;
	private static Lock lock;
	private static Condition2 adultCond, childMolokaiCond, childOahuCond;
	private static boolean boatOnOahu, aChildJustRowed;
	private static int childrenOnOahu, adultsOnOahu, childrenOnMolokai, adultsOnMolokai;

	public static void selfTest()
	{
		BoatGrader b = new BoatGrader();

		System.out.println("\n ***Testing Boats with only 2 children***");
		//begin(0, 2, b);
		begin(5, 5, b);


		//	System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
		//  	begin(1, 2, b);

		//  	System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
		//  	begin(3, 3, b);
	}

	public static void begin( int adults, int children, BoatGrader b )
	{
		// Store the externally generated autograder in a class
		// variable to be accessible by children.
		bg = b;
	
		lock = new Lock();
		adultCond = new Condition2(lock);
		childOahuCond = new Condition2(lock);
		childMolokaiCond = new Condition2(lock);
		aChildJustRowed = false;

		boatOnOahu = true;
		childrenOnOahu = adultsOnOahu = childrenOnMolokai = adultsOnMolokai = 0;
		done = false;
		// Create threads here. See section 3.4 of the Nachos for Java
		// Walkthrough linked from the projects page.

		Runnable a = new Runnable()  {
			public void run() {
				AdultItinerary();
			}
		};

		Runnable c = new Runnable() {
			public void run() {
				ChildItinerary();
			}
		};
		KThread[] A = new KThread[adults];
		for(int i = 0; i < adults; i++){
			A[i] = new KThread(a);
			A[i].setName("Adult Thread Number " + i);
			A[i].fork();
		}
		KThread[] C = new KThread[children];
		for(int i = 0; i < children; i++){
			C[i] = new KThread(c);
			C[i].setName("Child Thread Number " + i);
			C[i].fork();
		}

		for(int i = 0; i < adults; i++){
			A[i].join();
		}
		for(int i = 0; i < children; i++){
			C[i].join();
		}

		System.out.println("Done!");
		/*
		Runnable r = new Runnable() {
			public void run() {
				SampleItinerary();
			}
		};
		KThread t = new KThread(r);
		t.setName("Sample Boat Thread");
		t.fork();
		 */
	}

	static void AdultItinerary()
	{

		adultsOnOahu++;
		KThread.yield();
		
		lock.acquire();
		while(childrenOnOahu > 1 || !boatOnOahu){
			if(childrenOnOahu > 1){
				childOahuCond.wake();
			}else{
				childMolokaiCond.wake();
			}
			adultCond.sleep();
		}
		adultsOnOahu--;
		bg.AdultRowToMolokai();
		boatOnOahu = false;
		childMolokaiCond.wake();	
		lock.release();
		KThread.finish();

	}

	static void ChildItinerary()
	{

		childrenOnOahu++;
		KThread.yield();
		boolean imOnOahu = true;
		lock.acquire();
		while(!done){

			if(imOnOahu){ //On Oahu
				while(childrenOnOahu == 1 || !boatOnOahu){
					if(childrenOnOahu == 1){
						adultCond.wake();
					}else{
						childMolokaiCond.wake();
					}
					childOahuCond.sleep();
				}
				//now on oahu with >1 children and boat present
				if(!aChildJustRowed){
					bg.ChildRowToMolokai();
					aChildJustRowed = true;
					imOnOahu = false;
					childOahuCond.wake();
				}else{
					int numAdultsSeen = adultsOnOahu; //checks how many adults were there when s/he left
					bg.ChildRideToMolokai();
					boatOnOahu = false;
					childrenOnOahu -= 2;
					childrenOnMolokai += 2;
					aChildJustRowed = false;
					imOnOahu = false;
					//childMolokaiCond.wake();
					//TODO: what if nobody is here to catch this???
					if(numAdultsSeen == 0){ 
						System.out.println("WE'VE MADE IT CLOSE");
						done = true;
						childMolokaiCond.wakeAll();
						break;
					}else if(childrenOnMolokai==2){
						bg.ChildRowToOahu();
						childrenOnOahu++;
						childrenOnMolokai--;
						boatOnOahu = true;
						imOnOahu = true;
						adultCond.wake();
						childOahuCond.wake();						
						childOahuCond.sleep();
					}else{
						adultCond.wake();
					}
				}
				if(!imOnOahu){
					childMolokaiCond.sleep();
				}

			}else{ //On Molokai
				while(boatOnOahu){
					childMolokaiCond.sleep();
				}
				//now we are on molokai with the boat and the lock
				bg.ChildRowToOahu();
				//System.out.println("we got here at least once");
				boatOnOahu = true;
				imOnOahu = true;
				childrenOnOahu++;
				childrenOnMolokai--;
				adultCond.wake();
				childOahuCond.wake();	
				childOahuCond.sleep();
			
			}
		}
		lock.release();
		KThread.finish();
	}

	static void SampleItinerary()
	{
		// Please note that this isn't a valid solution (you can't fit
		// all of them on the boat). Please also note that you may not
		// have a single thread calculate a solution and then just play
		// it back at the autograder -- you will be caught.
		System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
		bg.AdultRowToMolokai();
		bg.ChildRideToMolokai();
		bg.AdultRideToMolokai();
		bg.ChildRideToMolokai();
	}

}
