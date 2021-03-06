package com.intel.fibclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.intel.fibcommon.IFibListener;
import com.intel.fibcommon.IFibService;
import com.intel.fibcommon.Request;

public class FibActivity extends Activity {
	EditText input;
	TextView output;
	IFibService fibService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fib);

		input = (EditText) findViewById(R.id.input);
		output = (TextView) findViewById(R.id.output);

		bindService(new Intent("com.intel.fibcommon.IFibService"),
				new FibServiceConnection(), BIND_AUTO_CREATE);
	}

	class FibServiceConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder service) {
			fibService = IFibService.Stub.asInterface(service);
		}

		public void onServiceDisconnected(ComponentName name) {
			fibService = null;
		}
	}

	class IFibListenerImpl extends IFibListener.Stub {

		public void response(long n) throws RemoteException {
			output.append(String.format("\nfibJ=%d", n) );
			Log.d("FibActivity", String.format("\nfibJ=%d", n));
		}
	}

	IFibListenerImpl fibListener = new IFibListenerImpl();

	public void onButtonClick(View v) throws RemoteException {
		long n = Long.parseLong(input.getText().toString());
		long start;

		// Java Recursive
		fibService.asyncFib(new Request(1, n), fibListener);

		// Native Recursive
		start = System.currentTimeMillis();
		long resultN = fibService.fib(new Request(2, n));
		long timeN = System.currentTimeMillis() - start;
		output.append(String.format("\nfibN(%d)=%d (%d ms)", n, resultN, timeN));

		// Java Iterative
		start = System.nanoTime();
		long resultJI = fibService.fib(new Request(3, n));
		long timeJI = System.nanoTime() - start;
		output.append(String.format("\nfibJI(%d)=%d (%d ns)", n, resultJI,
				timeJI));

		// Native Iterative
		start = System.nanoTime();
		long resultNI = fibService.fib(new Request(4, n));
		long timeNI = System.nanoTime() - start;
		output.append(String.format("\nfibNI(%d)=%d (%d ns)", n, resultNI,
				timeNI));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_fib, menu);
		return true;
	}

}
