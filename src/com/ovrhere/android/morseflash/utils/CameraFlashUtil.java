package com.ovrhere.android.morseflash.utils;

import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * <p>A camera flash utility class, designed to work with 
 * problematic devices. Requires there be a "visible" SurfaceView for the camera. 
 * It is suggested that one call {@link #close()} when the activity suspends to 
 * free the camera service. To start using the utility again, 
 * reset it by calling {@link #setCameraSurfaceView(SurfaceView)}.
 * </p>
 * <p>
 * In order to work on as many devices as possible, the SurfaceView in question
 * MUST:
 * <ul>
 * <li>Have {@link View#VISIBLE}</li>
 * <li>Have a height x width > 0dp</li>
 * <li>Be within visible frame</li>
 * </ul> 
 * </p>
 * <p>
 * Requires the following permissions in the manifest:
 * <code>
 * <br/>&lt;uses-permission android:name="android.permission.CAMERA" /&gt;
 * <br/>&lt;uses-permission android:name="android.permission.FLASHLIGHT"/&gt;
 * <br/>&lt;uses-feature android:name="android.hardware.camera" /&gt;
 * <br/>&lt;uses-feature android:name="android.hardware.camera.flash" /&gt;
 *</code>
 * </p>
 * @author Jason J.
 * @version 0.2.0-20140605
 */
public class CameraFlashUtil implements SurfaceHolder.Callback {
	//TODO: futher reading on this topic http://stackoverflow.com/questions/5503480/use-camera-flashlight-in-android
	/** The class name. */
	final static private String CLASS_NAME = CameraFlashUtil.class.getSimpleName();
	
	/** The surface holder in view. */
	private SurfaceHolder mHolder = null;
	/** The camera reference for turning on an off the flash. */
	private Camera mCamera = null;
	
	public CameraFlashUtil(SurfaceView surfaceView){
		_setCameraSurfaceView(surfaceView);
	}
	/**
	 * Sets the camera surface view require for the camera flash to 
	 * work on all devices. Note that this view may be hidden behind other views 
	 * but it MUST:
	 * <ul>
	 * <li>Be {@link View#VISIBLE}</li>
	 * <li>Have a height x width > 0dp</li>
	 * <li>Be within visible frame</li>
	 * </ul> in order to work with all devices.
	 * @param surfaceView The visible {@link SurfaceView}.
	 */
	public void setCameraSurfaceView(SurfaceView surfaceView){
		_setCameraSurfaceView(surfaceView);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Functional methods
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Returns whether or not flash is currently available.
	 * Should be called after the {@link SurfaceView} is set.
	 * @return <code>true</code> if a flash LED is available, 
	 * <code>false</code> otherwise. 
	 * @see #setCameraSurfaceView(SurfaceView)
	 */
	public boolean isFlashAvailable() {
		/* http://stackoverflow.com/questions/13413938/how-to-check-if-device-has-flash-light-led-android
		 * Not supported by 2013 Nexus 7:
		 * context.getPackageManager()
        .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);*/
		
        if (mCamera == null) {
            return false;            
        }

        Camera.Parameters param = mCamera.getParameters();
        if (param.getFlashMode() == null) {
            return false;
        }

        List<String> supportedFlashModes = param.getSupportedFlashModes();
        if (	supportedFlashModes == null 	|| 
        		supportedFlashModes.isEmpty() 	|| 
        		(supportedFlashModes.size() == 1 && 
        		supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF))
        	) {
            return false;
        }

        return true;
    }
	
	/**
	 * Turns on and off LED depending on the value of the boolean passed.
	 * Note that this still may require the {@link SurfaceView} to be visible 
	 * (i.e. {@link View#VISIBLE}).
	 * @param on If set to <code>true</code>, turns on LED. If <code>false</code>
	 * turns off LED.
	 * @throws IllegalStateException If the camera is not set, either by not being
	 * available or the object not being initialized (such as after 
	 * calling {@link #close()}).
	 */
	public void flashLed(boolean on) throws IllegalStateException {
		//if not available, quit.
		if (mCamera == null){
			throw new IllegalStateException("Flash is not available");
		}
		Parameters params = mCamera.getParameters();
		if (on){
			//Turn flash LED on.
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(params);      
			mCamera.startPreview();
		} else {
			//Turn flash LED off.
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(params);
			mCamera.stopPreview();
			mCamera.release();
		}
	}
	/** Closes the utility safely and releases any lingering camera references.
	 * To reopen, call {@link #setCameraSurfaceView(SurfaceView)}. 
	 */
	public void close(){
		//avoids camera service hogging.
		mHolder = null;
		if (mCamera != null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper methods
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Attempts to initialize the surfaceView holder and camera for use with 
	 * flash.  
	 * @param surfaceView The visible surface view. If the view is not visible
	 * the flash will not work.
	 * @return <code>true</code> if the holder and camera have been set
	 * without incident, <code>false</code> if it has failed.
	 */
	private boolean _setCameraSurfaceView(SurfaceView surfaceView){
		mHolder = surfaceView.getHolder();
		mHolder.addCallback(this);
		try {
			mCamera = Camera.open();
		} catch (RuntimeException e){
			//may fail to connect to service, e.g. simulator
			Log.w(CLASS_NAME, "Run time error: " + e);
			return false;
		}
		
		if (mCamera != null){
			return _setCameraPreview();
		} 
		return false;
	}
	
	/**
	 * Sets the camera preview to the surface view holder.
	 * @return <code>true</code> if the camera preview is set without incident,
	 * <code>false</code> if a problem occurs.
	 */
	private boolean _setCameraPreview() {
		if (mCamera == null) return false;
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/// Implemented interfaces etc.
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
	        int height) {}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	    mHolder = holder;
	    _setCameraPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	    if (mCamera != null){
	    	mCamera.stopPreview();
	    	mCamera.release(); 
	    } //release camera fully
	    mHolder = null;
	}
	
	
}
