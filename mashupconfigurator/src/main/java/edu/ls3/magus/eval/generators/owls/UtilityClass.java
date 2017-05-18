package edu.ls3.magus.eval.generators.owls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Random;

public class UtilityClass {
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	public static String readFile(File inp, Charset encoding) throws IOException{
		byte[] encoded = Files.readAllBytes(inp.toPath());
		return new String(encoded,encoding);
	}
	public static void writeFile(File inp, String output) throws IOException{
		BufferedWriter writer = new BufferedWriter(new  FileWriter(inp));
		writer.write( output);
		writer.close( );
	}
	public static String Clean(String inp) {
		
		
		if(inp.startsWith("#"))
			return inp.substring(1);
		return inp;
	}
}
