I've put a lot of comments in the source to explain what the tests do and how they do it.

Please give it a read. Some tests will fail until you modify your AndroidPlatformStrategy code.

Code is copyright (c) 2014, TP Diffenbach. You are granted a non-exclusive, non-transferable license to
use the code, as an enrolled student in Coursera 2014 POSA MOOC course.

To set up the test, you'll need to 

\1. get the PowerMock, Mockito, and JUnit jars, and put them into the libs/ directory of the project.
A zip file containing all those jars can be can be found at:
https://code.google.com/p/powermock/wiki/Downloads?tm=2
You need the zip powermock-mockito-junit-1.5.5.zip

\2. Link to a copy of the android.jar in your Android by editing the .classpath file in
the same directory as this read.me. Please note that the android.jar must come AFTER the
junit classpath entry. If your Andorid sdk setup is the same as mine, you won't need to edit this.

Unfortunately, I can't find a good, portable way to reference android.jar; 
adding the android Classpath Container doesn't work, because javanature projects apparently 
don't read the android project.proerities file to find the android version.

\3. Reference your W5-A4-Android project in the .classpath file.

This is already done in the .classpath file, and should work as-is if you import this project
into the same workspace that contains the  W5-A4-Android project.

\4. Either edit your AndroidPlatformStrategy to conform to checkNullWeakReference, or comment out
NullWeakReferenceIsLoggedButNotCalled, and GarbageCollectedWeakReferenceIsLogged 
in AndroidPlatformStrategyTest. In fact, AndroidPlatformStrategy should probably throw rather than log.

\5. When you first run the test from within Eclipse, you may have to specify the Eclipse JUnit test runner, 
and the Junit4  test runner.