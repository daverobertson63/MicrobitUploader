package dhr.microbituploader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

public class MicrobitUploader
{

/**
 * @param args
 * @throws IOException 
 */

public static void  main(String[] args) 
{
	// We make the simple assumption that the firmware.hex file is in the same location as the jar file being executed

	if ( args.length < 2 )
	{
		System.out.println("Needs two args");
		return;
	
	}
	
	try{
	      String executionPath = System.getProperty("user.dir");
	      System.out.print("Executing at =>"+executionPath.replace("\\", "/"));
	      
	      File f = new File("firmwaremaster.hex");
	      if(f.exists() && !f.isDirectory()) { 
	          // do the conversion and copy over
	    	  FirmwareGenerator fg = new FirmwareGenerator("firmwaremaster.hex",args[0]);
	    	  // This should create a new file called firmware.hex
	    	  fg.createMicrobitFirmware();
	    	  
	    	  File fo = new File("firmware.hex");
		      
	    	  if(fo.exists() && !fo.isDirectory()) {
	    		  File fw = new File("firmware.hex");
	    		  File mb = new File(args[1] + File.separator + "firmware.hex");
	    		  
	    		  FileUtils.copyFile(fw, mb);
	    	      System.out.println("Microbit updated!");
	    	  }
	    	  
	      }
	      
	    }catch (Exception e){
	      System.out.println("Exception caught ="+e.getMessage());
	    }
	
	
	
	return;

}
}