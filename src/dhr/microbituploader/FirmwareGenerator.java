/**
 * Firmware generator component
 * 
 * Developed by Dave Robertson
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *
 * @author   Dave Robertson
 * 
 * Hexifly the python code and combine with the firmware for a micropython file 
 * which can be copied to the Microbit as a drive or file - works on the command line
 */
package dhr.microbituploader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

/**
 * @author daver
 *
 */
public class FirmwareGenerator {

	public String outputFilename = "firmware.hex";
	public String firmwareString;
	public String pythonString;
	
	/**
	 * 
	 */
	
	
	public FirmwareGenerator(String Firmware, String Python) {
		
		// Open the master firmware file
		File sketchFile = new File(Firmware);
		File pythonFile = new File(Python);
		
		try {
			firmwareString = FileUtils.readFileToString(sketchFile,"UTF-8");
			pythonString = FileUtils.readFileToString(pythonFile,"UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @param script to hexifly and combine 
	 *  
	 */
	
	public String createFirmware(String pythonScript)
	{
		
		return null;
		
		
	}
	
	public Boolean createMicrobitFirmware() 
	{
		
		String output = generateFirmware(firmwareString,pythonString);
		
		File outputFile = new File(outputFilename); 
		
		try {
			FileUtils.writeStringToFile(outputFile, output, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return true;
			
		
	}
	
	
	public String generateFirmware(File file)
	{
		String script;
		try {
			
			script = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
			return generateFirmware("",script);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	public String generateFirmware(String Firmware, String script)
		
	{
		
		System.out.println("Sketch being processed");
		System.out.println(script);
		
		StringBuilder output = new StringBuilder();
		StringBuilder tempScript = new StringBuilder();
		
		// Get the whole script into output.
		output.append(Firmware);
		
		//System.out.println("Firmware is: " + output.toString());
			
		// add header, pad to multiple of 16 bytes
	    int[] data = new int[4 + script.length() + (16 - (4 + script.length()) % 16)];
	    data[0] = 77; // 'M'
	    data[1] = 80; // 'P'
	    data[2] = script.length() & 0xff;
	    data[3] = (script.length() >> 8) & 0xff;
	    
	    for (int i = 0; i < script.length(); ++i) {
	    	int codePointAt0 = Character.codePointAt(script, i);
	        data[4 + i] = codePointAt0;
	    }
	    
	    // convert to .hex format
	    int addr = 0x3e000; // magic start address in flash
	    int[] chunk = new int[5 + 16];
	    
	    //output.append(":020000040003F7\n");
	    
	    for (int i = 0; i < data.length; i += 16, addr += 16) {
	        chunk[0] = 16; // length of data section
	        chunk[1] = (addr >> 8) & 0xff; // high byte of 16-bit addr
	        chunk[2] = addr & 0xff; // low byte of 16-bit addr
	        chunk[3] = 0; // type (data)
	        for (int j = 0; j < 16; ++j) {
	            chunk[4 + j] = data[i + j];
	        }
	        int checksum = 0;
	        for (int j = 0; j < 4 + 16; ++j) {
	            checksum += chunk[j];
	        }
	        chunk[4 + 16] = (-checksum) & 0xff;
	        tempScript.append(':' + hexlify(chunk));
	        tempScript.append(System.lineSeparator());
	    }
	    
	    // Simple replace strategy on the firmware should allow updates of the firmware
	    //System.out.println(output.toString());
	    System.out.println(tempScript);
	    
	    replaceString(output,":::::::::::::::::::::::::::::::::::::::::::" ,tempScript.toString());
	    
	    //output.append(MicrobitFirmware.getFirmwareTrailer());
	    
	    // Return a full string - ready to copy
	    return output.toString();
	   
	    

	}
	/*
	 * Create a HEX representaion of the data... HEXLIFY!
	 * 
	 */
	public  String hexlify(int[] ar) {
		
        StringBuffer result= new StringBuffer();;
        int l = ar.length;
        
        for (int i = 0; i < ar.length; ++i) {
            
            result.append(String.format("%02X", ar[i]));
        }
        return result.toString();
    }
	
	public void replaceAll(StringBuilder builder, String from, String to)
	{
	    int index = builder.indexOf(from);
	    while (index != -1)
	    {
	        builder.replace(index, index + from.length(), to);
	        index += to.length(); // Move to the end of the replacement
	        index = builder.indexOf(from, index);
	    }
	}
	
	/**
	* Utility method to replace the string from StringBuilder.
	* @param sb          the StringBuilder object.
	* @param toReplace   the String that should be replaced.
	* @param replacement the String that has to be replaced by.
	* 
	*/
	public static void replaceString(StringBuilder sb,
	                                 String toReplace,
	                                 String replacement) {     
		
		
	   
		
		System.out.println("Pattern : " + toReplace + " Len: " + toReplace.length());
		
		int index = sb.indexOf(toReplace);
		
		System.out.println("Index of pattern: " + index);
		
		if (index <= 0 ) 
		{
			System.out.println("Failed to see correct replacement pattern in firmware file");
			sb.replace(0, 6, "Invalid");
			return;
		}
		int end = index + toReplace.length();
		
		
		System.out.println("Index of pattern: " + index);
		System.out.println("End of pattern: " + end);
		System.out.println("New Pattern : " + replacement);
		
		
		sb.replace(index, end, replacement);
		
		
		
		//String tail = new String(sb.substring(end));	// tail conmponent
		
	}

}
