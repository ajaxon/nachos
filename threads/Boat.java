package nachos.threads;
import nachos.ag.BoatGrader;

public class Boat
{
	static BoatGrader bg;
	private static Lock oahuLock, molokaiLock, boatLock;
	private static Condition2 oahuCond, molokaiCond;
	private static boolean boatOnOahu;
	private static int childrenOnOahu, adultsOnOahu, childrenOnMolokai, adultsOnMolokai;
	
	public static void selfTest()
	{
		BoatGrader b = new BoatGrader();

		System.out.println("\n ***Testing Boats with only 2 children***");
		//begin(0, 2, b);
		begin(2, 1, b);
	

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
		// Instantiate global variables here
		oahuLock = new Lock();
		molokaiLock = new Lock();
		boatLock = new Lock();
		oahuCond = new Condition2(oahuLock);
		molokaiCond = new Condition2(molokaiLock);
		boatOnOahu = true;
		childrenOnOahu = adultsOnOahu = childrenOnMolokai = adultsOnMolokai = 0;
		
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
		oahuLock.acquire();
		while((childrenOnOahu > 1) || (!boatOnOahu)){
			oahuCond.sleep();
		}
		boatLock.acquire();
		bg.AdultRowToMolokai();
		adultsOnOahu--;
		adultsOnMolokai++;
		oahuCond.wake();
		boatLock.release();
		oahuLock.release();
		boatOnOahu = false;
		KThread.finish();
	}

	static void ChildItinerary()
	{
		childrenOnOahu++;
		KThread.yield();
		
		
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
