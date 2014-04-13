package io.cran.phonesat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.cran.phonesat.R;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * This is the main activity of the HelloIOIO example application.
 * 
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */
public class MainActivity extends IOIOActivity implements SensorEventListener {
	private ToggleButton button_;
	private ToggleButton magnetorquer_;
	
	private String cmd;

	
	private SensorManager sManager;
	private Sensor mSensor;
	
	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		button_ = (ToggleButton) findViewById(R.id.button);
		magnetorquer_ = (ToggleButton) findViewById(R.id.button2);
		Context context = getApplicationContext();
		CharSequence text = "Hello!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		
		IOIOLooper looper = createIOIOLooper();
	}

	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
		private DigitalOutput led_;
		
		private DigitalOutput magnetorquerOutput1_;

		
		
		
	    Uart uart; 
	    int pinToDIN = 39; //Serial data is sent on this pin into the XBee (RX or DIN) to be transmitted wirelessly
	    int pinToDOUT = 37;
	   // int pinRx	= 35;
	    
	    OutputStream uartOutputStream;
	    InputStream uartInputStream;
	    ByteArrayOutputStream bas = null;
	    DataOutputStream dos = null;
		

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
			magnetorquerOutput1_ = ioio_.openDigitalOutput( 13, true);		// pin tres para controlar un sentido del magnetorquer
				        
	        uart = ioio_.openUart(pinToDOUT, pinToDIN, 9600, Uart.Parity.NONE, Uart.StopBits.ONE);
	        uartOutputStream = uart.getOutputStream();
	        uartInputStream = uart.getInputStream();
	        
            cmd = "Hola XBEE!";

		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		@Override
		public void loop() throws ConnectionLostException, InterruptedException {
			led_.write(!button_.isChecked());
/*			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
	        isIOIOConnected = true;*/
	        if (button_.isChecked()) {
	            try {
	                

	                uartOutputStream.write(cmd.getBytes());
	                
	            } catch (IOException exception) {
	   //     	      Log.e(TAG, "Error: ", exception);
	            } catch (Exception e) {
	   //             Log.e(TAG, "Error: ", e);
	            }

	        }
	        Thread.sleep(300);
	        if(magnetorquer_.isChecked()){
	        	magnetorquerOutput1_.write(!magnetorquer_.isChecked());
	        }
		}

	}
    protected void onResume()  
    {  
        super.onResume();  
        /*register the sensor listener to listen to the gyroscope sensor, use the 
        callbacks defined in this class, and gather the sensor information as quick 
        as possible*/  
        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_FASTEST);  
    } 
    protected void onStop()  
    {  
        //unregister the sensor listener  
        sManager.unregisterListener(this);  
        super.onStop();  
    }  
	@Override
    public void onAccuracyChanged(Sensor arg0, int arg1)  
    {  
        //Do nothing.  
    }  
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		cmd = Float.toString(event.values[2]) + "\n" + Float.toString(event.values[1]) + "\n" + Float.toString(event.values[0]);

	}
	
	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
}