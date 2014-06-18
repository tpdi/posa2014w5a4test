package org.diffenbach.posa2014.w5_a4.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Semaphore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import edu.vuum.mocca.AndroidPlatformStrategy;

/**
 * Unit test of AndroidPlatformStrategy, 
 * using Junit4, Mockito, and PowerMock
 * 
 * Runs on the standard Java, not on an Android device or emulator,
 * so it's a bit faster, and really is a unit test. 
 * 
 * It needs to use the PowerMock runner, and we need to tell it 
 * about the Android classes PowerMockito will mock.
 * 
 * Because we make the test runner explicit, we don't have to derive 
 * our test class from any particular parent class.
 * 
 * For both of these we have annotations: 
 * 
 */
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest({Activity.class, Log.class, TextView.class})
public class AndroidPlatformStrategyTest {

	
	/*
	 * JUnit4, so setup is no longer a magic name. 
	 * Instead, we use the Before annotation
	 */
	@Before
    public void setup() {
    	
    	/*
    	 * As we are not testing on a device or an emulator, we just have the 
    	 * version of the android.jar, which throws an exception ("Stub!")
    	 * for EVERY method call.
    	 * 
    	 * So we need to mock ANY Android class we construct or call.
    	 * 
    	 * And to create those mocks, we need to use PowerMockito.mock, not Mockito.mock.
    	 * 
    	 * For the static methods, we need to use mockStatic.
    	 */
    	//mockStatic(Activity.class);
    	PowerMockito.mockStatic(Log.class);
    }

    /*
     *  While not a requirement of the assignment, we'll test that 
     *  a null Activity or TextView results in a log message in 
     *  the ctor, and when WeakReference.get() is called.
     *  
     *  This test depends on your having defined public static final error 
     *  messages in AndroidPlatformStrategy. 
     *  
     *  It will test that you don't try to dereference a null Activity.
     *  To also test that nulls are logged (not a requirement of the
     *  assignment), uncomment the lines that define the log messages.
     */
    
    @Test // check one Activity that is always null
    public void NullWeakReferenceIsLoggedButNotCalled() {
    	checkNullWeakReference(new AndroidPlatformStrategy(null, null),
    			new String[]{
    		
    		
    // uncomment these lines to actually test something
    // note, uncommenting will cause compilation errors
    // unless you've changed AndroidPlatformStrategy
    // to have the referenced public static strings
    		
    		
					//AndroidPlatformStrategy.OUTPUT_IS_NULL,
					//AndroidPlatformStrategy.ACTIVITYPARAM_IS_NULL,
					//AndroidPlatformStrategy.ACTIVITY_IS_NULL
    	});
    }
    
    /*
     * It turns out I can't get a test of an actual garbage collected
     * reference to work: we get a "Stub!" exception from Context if we don't mock
     * and (?) PowerMock seems to hold on to the reference if we do mock.
     * 
     * Note that with the @Test annotation commented out, the test won't be run.
     * This is a JUnit4 annotation, which means tests don't have to be named "test..."
     * as in JUnit3.
     */
    
    //@Test // check another Activity that isn't initially null
    // This is not possible, 
    public void GarbageCollectedWeakReferenceIsLogged() {
    	checkNullWeakReference(new AndroidPlatformStrategy(null, PowerMockito.mock(Activity.class)), 
    			new String[]{
					//AndroidPlatformStrategy.OUTPUT_IS_NULL,
					//AndroidPlatformStrategy.ACTIVITY_IS_NULL
		});
    }
    
    private void checkNullWeakReference( final AndroidPlatformStrategy uut, String[] expectedLogs) {
    	//System.gc();
    	String className = AndroidPlatformStrategy.class.getSimpleName();

    	uut.done();
    	System.out.println(className);
    	for(String e: expectedLogs) {
    		// we need to call this before EVERY verification of a static method
    		PowerMockito.verifyStatic();

    		// Now what looks like a call to Log.e is actually a verification that
    		// the call was made by the class under test (directly or indirectly).
    		// eq and startsWith are Mockito Argument Matchers.
    		// We could statically import Mockito if we wanted to leave off "Mockito." 
    		// but to make the code easier to understand, we'll be explicit:
    		Log.e(Mockito.eq(className), Mockito.startsWith(e + " in " + className));
    	}
    }

    
    /*
     * In testDoneIsRunOnUIThread we won't actually run the runnable,
     * which means we won't actually get an NPE if begin isn't called.
     * Here, we'll run the runnable we get back, and get the NPE.
     * 
     * In concert with the tests that do call begin, this should verify
     * that begin is doing what it is supposed to do.
     * 
     * NOTE: THIS TEST WILL FAIL
     * if you use the assignment version where mLatch is static
     * unless you run this test FIRST or by itself.
     * Thus it comes before the other tests which do call begin.
     */

    // Tell Junit4 that we're a test and we success if we throw an NPE:
    @Test(expected=NullPointerException.class)
    public void DoneWillCauseNPEIfBeginNotCalled() {
    	// create a PowerMock mock. Normally we'd just use a Mockito mock,
    	// but all Android methods throw exceptions when using the JAVA SE Android.jar:
    	final Activity mockActivity = PowerMockito.mock(Activity.class);
    	
    	// create the Unit Under Test, or uut: this is the class we are testing:
    	final AndroidPlatformStrategy uut = new AndroidPlatformStrategy(null, mockActivity);
    	
    	// create an ArgumentCaptor, a kind of argument matcher that 
    	// keeps a copy of the argument:
    	final ArgumentCaptor<Runnable> runnableArg = ArgumentCaptor.forClass(Runnable.class);
    	
    	// we won't call uut.begin() before calling done(); 
    	
    	uut.done();
    	
    	// Verifying a mock is more clear than verifying a static.
    	// In this case, we're also going to capture the Runnable argument
    	// AndroidPlatformStrategy passes to runOnUIThread, with the
    	// Mockito ArgumentCaptor we created above:
    	// Mockito.verify(mock).methodOnMock(argmentMatcher)
    	Mockito.verify(mockActivity).runOnUiThread(runnableArg.capture());
    	
    	// And now if we run the runnable, we should throw.
    	// Note that we don't need or want to run it in a separate thread.
    	// There is no actual UI thread here, there doesn't need to be:
    	// this is a UNIT test, not an integration test.

    	// runnableArg is the Argument captor
    	// getValue() returns the last thing captured
    	// which is a runnable we can call run() on.
    	runnableArg.getValue().run();
    	
    }
    
    /*
     * Test that AndroidPlatformStrategy.print actually calls runOnUiThread,
     * with a Runnable, and that that runnable appends to the output object.
     * 
     * It would be "better" practice to make this two tests, but that would
     * duplicate most of the test fixtures, so I won't.
     */

    @Test
    public void PrintIsRunOnUIThreadAndAppendsToTextView() {
    	// arrange
    	final Activity mockActivity = PowerMockito.mock(Activity.class);
    	final TextView textView = PowerMockito.mock(TextView.class);
    	final AndroidPlatformStrategy uut = new AndroidPlatformStrategy(textView, mockActivity);
    	final String stringToPrint = "stringToPrint";
    	final ArgumentCaptor<Runnable> runnableArg = ArgumentCaptor.forClass(Runnable.class);
    	
    	// act
    	uut.print(stringToPrint);
    	
    	// assert
    	Mockito.verify(mockActivity).runOnUiThread(runnableArg.capture());
    	runnableArg.getValue().run();
    	Mockito.verify(textView).append(stringToPrint + "\n");
    }
    
    /*
     * Test that AndroidPlatformStrategy.done is call runOnUIThread.
     */
    @Test
    public void DoneIsRunOnUIThread() {
    	final Activity mockActivity = PowerMockito.mock(Activity.class);
    	final AndroidPlatformStrategy uut = new AndroidPlatformStrategy(null, mockActivity);
    	uut.begin(); //class is a little fragile
    	
    	uut.done();

    	Mockito.verify(mockActivity).runOnUiThread(Mockito.any(Runnable.class));
    }
    
    /*
     * Test that AndroidPlatformStrategy.done must be called twice before
     * AndroidPlatformStrategy.awairDone returns.
     */
    @Test
    public void DoneMustBeCalledTwiceToUnblock() throws InterruptedException {
    	final Activity mockActivity = PowerMockito.mock(Activity.class);
    	final AndroidPlatformStrategy uut = new AndroidPlatformStrategy(null, mockActivity);
    	final ArgumentCaptor<Runnable> runnableArg = ArgumentCaptor.forClass(Runnable.class);
    	
    	// Unfortunately, PlatformStrategy.NUMBER_OF_THREADS is not public
    	// this makes PlatformStrategy's public interface cleaner, but makes testing harder
    	// final int numberOfTimes = AndroidPlatformStrategy.NUMBER_OF_THREADS
    	final int numberOfTimes = 2;
    			
    	uut.begin(); // reset the AndroidPlatformStrategy
    	
    	for(int i = 0 ; i < numberOfTimes; ++i) {
    		uut.done();
    		// we're calling done(), but note that the Runnables won't yet be run 
    		// because Activity is a mock
    	}
    		 
    	// verify and capture the calls to Activity which pass the Runnables
    	Mockito.verify(mockActivity, Mockito.times(numberOfTimes)).runOnUiThread(runnableArg.capture());
    	
    	// I need something that is final, so it's in the anonymous class's closure
    	// and yet mutable
    	//final Boolean[] blocked = new Boolean[]{true};
    	final Semaphore semaphore = new Semaphore(0);
    	
    	// create a thread to call awaitDone and, well, await.
    	// when it stops awaiting, it'll release a permit on the Semaphore
    	final Thread thread = new Thread() {
    		@Override 
    		public void run() {
    			uut.awaitDone();
    			semaphore.release();
    		}
    	};
    	
    	thread.start();
    	
    	// now we'll run the Runnables
    	for( Runnable r: runnableArg.getAllValues()) {
    		// until we've run all done() Runnables, the available permits should be zero
    		assertEquals(0, semaphore.availablePermits());
    		r.run();
    	}

    	thread.join();
    	// after we've run all Runnables (and allowed the awaiting thread to finish) 
    	// available permits should be 1
    	assertEquals(1, semaphore.availablePermits());
    }
    
}
