package kojak.com.sample;

import java.awt.image.BufferedImage;

import com.integratedbiometrics.IB.controller.IbScanController;
import com.integratedbiometrics.ibscanultimate.IBScan;
import com.integratedbiometrics.ibscanultimate.IBScanDevice;
import com.integratedbiometrics.ibscanultimate.IBScanDeviceListener;
import com.integratedbiometrics.ibscanultimate.IBScanException;
import com.integratedbiometrics.ibscanultimate.IBScanListener;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.FingerCountState;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.FingerQualityState;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageData;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageType;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.PlatenState;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.SegmentPosition;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
//import sun.misc.BASE64Encoder;

/**
 * Principal class for function tester demo UI.
 */
public class InvokeDevice implements IBScanListener, IBScanDeviceListener {

	protected IBScan ibScan = null;
	protected IBScanDevice ibScanDevice = null;
	protected BufferedImage lastScanImage = null;
	public static IBScanException.Type errorcode;

//	public static void main(String[] args) {
//		new InvokeDevice().captureImages(1);
//	}

	public void captureImages(String hand) {
		try {
			ibScan = IBScan.getInstance();
			ibScan.setScanListener(this);
			IBScan.SdkVersion sdkVersion = ibScan.getSdkVersion();
			int deviceCount = ibScan.getDeviceCount();
			IBScan.DeviceDesc deviceDesc = ibScan.getDeviceDescription(0);
			if (ibScanDevice != null) {
				if (ibScanDevice.isOpened()) {
					ibScanDevice.close();
				}
			}
			ibScanDevice = ibScan.openDevice(0);
			_BeepOk(ibScanDevice);
			ibScanDevice.setScanDeviceListener(this);
			String propertyValue = ibScanDevice.getProperty(IBScanDevice.PropertyId.DEVICE_ID);
			IBScanDevice.LEOperationMode leOperationMode = ibScanDevice.getLEOperationMode();
			IBScanDevice.LedState ledState = ibScanDevice.getOperableLEDs();
			// long activeLEDs = ibScanDevice.getLEDs();
			// ibScanDevice.setLEDs(IBScanDevice.IBSU_LED_F_BLINK_RED);
			IBScanDevice.ImageResolution imageResolution = IBScanDevice.ImageResolution.RESOLUTION_500;
			boolean active = ibScanDevice.isCaptureActive();
			boolean available = false;
			ibScanDevice.setContrast(21);
			switch (hand) {
			case "left":
			case "right":
				ibScanDevice.beginCaptureImage(IBScanDevice.ImageType.FLAT_FOUR_FINGERS, imageResolution,
						IBScanDevice.OPTION_AUTO_CONTRAST);
				available = ibScanDevice.isCaptureAvailable(IBScanDevice.ImageType.FLAT_FOUR_FINGERS, imageResolution);
				break;
			case "thumbs":
				ibScanDevice.beginCaptureImage(IBScanDevice.ImageType.FLAT_TWO_FINGERS, imageResolution,
						IBScanDevice.OPTION_AUTO_CONTRAST);
				available = ibScanDevice.isCaptureAvailable(IBScanDevice.ImageType.FLAT_TWO_FINGERS, imageResolution);
				break;
			case "single":
				ibScanDevice.beginCaptureImage(IBScanDevice.ImageType.FLAT_SINGLE_FINGER, imageResolution,
						IBScanDevice.OPTION_AUTO_CONTRAST);
				available = ibScanDevice.isCaptureAvailable(IBScanDevice.ImageType.FLAT_SINGLE_FINGER, imageResolution);
				break;
			}
			// boolean isfptouching = ibScanDevice.isFingerTouching();
			int contrastValue = ibScanDevice.getContrast();

			// ibScanDevice.cancelCaptureImage(); //Cancel capture image
			// ibScanDevice.close();
		} catch (IBScanException ex) { // Handle errors here
			errorcode = ex.getType();
			ex.printStackTrace();
		}
	}

	public void scanDeviceCountChanged(int i) {
		System.out.println("scanDeviceCountChanged");
	}

	public void scanDeviceInitProgress(int i, int i1) {
		System.out.println("Progress:: " + i1);
	}

	public void scanDeviceOpenComplete(int i, IBScanDevice ibsd, IBScanException ibse) {
		System.out.println("scanDeviceOpenComplete");
	}

	public void deviceCommunicationBroken(IBScanDevice ibsd) {
		try {
			_BeepFail(ibScanDevice);
			ibScanDevice.close();
			System.out.println("deviceCommunicationBroken");
		} catch (IBScanException ex) {
			if (ex.getType().equals(IBScanException.Type.RESOURCE_LOCKED)) {
				deviceCommunicationBroken(ibsd);
			}
		}
	}

	public void deviceImagePreviewAvailable(IBScanDevice ibsd, ImageData id) throws IBScanException {
		_BeepSuccess(ibScanDevice);
		ibScanDevice.captureImageManually();
		System.out.println("deviceImagePreviewAvailable");
	}

	public void deviceFingerCountChanged(IBScanDevice ibsd, FingerCountState fcs) {
		// Handle here if wrong number of finger are placed
		// Expected outputs:
		// FINGER_COUNT_OK,FINGER_NOT_PRESENT,TOO_FEW_FINGERS,NON_FINGER
		System.out.println("deviceFingerCountChanged:" + fcs.name());
	}

	public void deviceFingerQualityChanged(IBScanDevice ibsd, FingerQualityState[] fqss) {
		// Expected outputs: GOOD,POOR,FAIR
		System.out.println("deviceFingerQualityChanged: " + fqss[0]);
	}

	public void deviceAcquisitionBegun(IBScanDevice ibsd, ImageType it) {
		// _BeepSuccess(ibScanDevice);
		System.out.println("deviceAcquisitionBegun");
	}

	public void deviceAcquisitionCompleted(IBScanDevice ibsd, ImageType it) {
		_BeepSuccess(ibScanDevice);
		System.out.println("deviceAcquisitionCompleted");
	}

	public void deviceImageResultAvailable(IBScanDevice ibsd, ImageData id, ImageType it, ImageData[] ids) {
//        System.err.println("deviceImageResultAvailable");
//        ImageUtils imageUtils = new ImageUtils();
//
//        System.err.println("Image type:: " + it);
//        BASE64Encoder encoder = new BASE64Encoder();
//        byte[] seg = ids[0].buffer;
//        String imageString = encoder.encode(ids[0].buffer);
		// System.out.println("Image:: " + imageString);
	}

	public void devicePlatenStateChanged(IBScanDevice ibsd, PlatenState ps) {
		System.out.println("devicePlatenStateChanged");
	}

	public void deviceWarningReceived(IBScanDevice ibsd, IBScanException ibse) {
		System.out.println("deviceWarningReceived");
	}

	public void devicePressedKeyButtons(IBScanDevice ibsd, int i) {
		System.out.println("devicePressedKeyButtons");
	}

	protected void _BeepFail(IBScanDevice ibScanDevice) {
		try {
			IBScanDevice.BeeperType beeperType = ibScanDevice.getOperableBeeper();
			if (beeperType != IBScanDevice.BeeperType.BEEPER_TYPE_NONE) {
				ibScanDevice.setBeeper(IBScanDevice.BeepPattern.BEEP_PATTERN_GENERIC, 2/* Sol */,
						12/* 300ms = 12*25ms */, 0, 0);
				_Sleep(150);
				ibScanDevice.setBeeper(IBScanDevice.BeepPattern.BEEP_PATTERN_GENERIC, 2/* Sol */, 6/* 150ms = 6*25ms */,
						0, 0);
				_Sleep(150);
				ibScanDevice.setBeeper(IBScanDevice.BeepPattern.BEEP_PATTERN_GENERIC, 2/* Sol */, 6/* 150ms = 6*25ms */,
						0, 0);
				_Sleep(150);
				ibScanDevice.setBeeper(IBScanDevice.BeepPattern.BEEP_PATTERN_GENERIC, 2/* Sol */, 6/* 150ms = 6*25ms */,
						0, 0);
			}
		} catch (IBScanException ibse) {
			try {
				// devices for without beep chip
				PlaySound.tone(3500, 300);
				_Sleep(150);
				PlaySound.tone(3500, 150);
				_Sleep(150);
				PlaySound.tone(3500, 150);
				_Sleep(150);
				PlaySound.tone(3500, 150);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

	protected void _BeepSuccess(IBScanDevice ibScanDevice) {
		try {
			IBScanDevice.BeeperType beeperType = ibScanDevice.getOperableBeeper();
			if (beeperType != IBScanDevice.BeeperType.BEEPER_TYPE_NONE) {
				ibScanDevice.setBeeper(IBScanDevice.BeepPattern.BEEP_PATTERN_GENERIC, 2/* Sol */, 4/* 100ms = 4*25ms */,
						0, 0);
				_Sleep(50);
				ibScanDevice.setBeeper(IBScanDevice.BeepPattern.BEEP_PATTERN_GENERIC, 2/* Sol */, 4/* 100ms = 4*25ms */,
						0, 0);
			}
		} catch (IBScanException ibse) {
			try {
				// devices for without beep chip
				PlaySound.tone(3500, 100);
				_Sleep(50);
				PlaySound.tone(3500, 100);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

	protected void _BeepOk(IBScanDevice ibScanDevice) {
		try {
			IBScanDevice.BeeperType beeperType = ibScanDevice.getOperableBeeper();
			if (beeperType != IBScanDevice.BeeperType.BEEPER_TYPE_NONE) {
				ibScanDevice.setBeeper(IBScanDevice.BeepPattern.BEEP_PATTERN_GENERIC, 2/* Sol */, 4/* 100ms = 4*25ms */,
						0, 0);
			}
		} catch (IBScanException ibse) {
			try {
				// devices for without beep chip
				PlaySound.tone(3500, 100);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

	protected void _Sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	public void deviceImageResultExtendedAvailable(IBScanDevice device, IBScanException imageStatus, ImageData image,
			ImageType imageType, int detectedFingerCount, ImageData[] segmentImageArray,
			SegmentPosition[] segmentPositionArray) {
		// Finger positions
		JSONArray jsonArray = new JSONArray();
		
		try {
//            ibScanDevice.SaveBitmapImage("D:\\Projects\\Equity\\KOJAK\\Kojak\\sample.bmp", image.buffer, image.width, image.height, image.pitch, image.resolutionX, image.resolutionY);
			for (int i = 0; i < detectedFingerCount; i++) {
				JSONObject jsonObject = new JSONObject();
				BufferedImage bufferedImage = image.toImage(segmentImageArray[i].buffer, segmentImageArray[i].width,
						segmentImageArray[i].height);
				String encodeToString = ImageUtils.encodeToString(bufferedImage, "bmp");

//                ibScanDevice.SaveBitmapImage("D:\\Projects\\Equity\\KOJAK\\Kojak\\sample" + i + ".bmp",
//                        segmentImageArray[i].buffer, segmentImageArray[i].width, segmentImageArray[i].height,
//                        segmentImageArray[i].pitch, segmentImageArray[i].resolutionX, segmentImageArray[i].resolutionY);
				// System.out.println("Split:: " + i + encodeToString);
				jsonObject.put("Position", String.valueOf(i));
				jsonObject.put("fingerprint", encodeToString);
				jsonObject.put("quality", "80");
				jsonArray.add(jsonObject);
			}
			IbScanController.response=new JSONObject();
			IbScanController.response.put(IbScanController.jsonrequest.get("name").toString() + "Hand", jsonArray);
			IbScanController.response.put("hand", IbScanController.jsonrequest.get("name"));
			IbScanController.response.put("status", true);

			System.out.println("deviceImageResultExtendedAvailable");
		} catch (Exception ex) {
			Logger.getLogger(InvokeDevice.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
