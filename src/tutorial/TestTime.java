package tutorial;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

public class TestTime {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		Stopwatch stopwatch = Stopwatch.createStarted();
		Thread.sleep(1000);
		stopwatch.stop(); // optional
		System.out.println("Time elapsed for myCall() is "+ stopwatch.elapsed(TimeUnit.NANOSECONDS));
	}

}
