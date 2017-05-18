package edu.ls3.magus.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;

import org.apache.commons.io.FileUtils;



public class UtilityClass {
	/**
	 * This method generates a random integer between min inclusive and max inclusive
	 * @param min
	 * @param max
	 * @return
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
	
	public static int randInt(int min, int max, List<Integer> exclusions) throws Exception {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
		
		if(exclusions.size() >= (max-min)+1)
			throw new Exception("Exceptions is bigger than range");
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		 
		while(exclusions.contains((Integer)randomNum ))
			randomNum = rand.nextInt((max - min) + 1) + min;
	   

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive

	    
	    
	    		

	    return randomNum;
	}
	
	public static String getLocalName(URI inp){
		return inp.getFragment();
	}
	
	public static boolean IsAbsolute(String inp){
		return (inp.indexOf("://")>-1);
	}

	public static String Clean(String inp) {
		
		
		if(inp.startsWith("#"))
			return inp.substring(1);
		return inp;
	}
	public static String readFile(String address) throws IOException{
		return new String(Files.readAllBytes(Paths.get(address)));
	}
	
	public static String[] readFileInLines(String address) throws IOException{
		List<String> lines = new ArrayList<String>();
		File f = new File(address);
		
		BufferedReader br = new BufferedReader(new FileReader(f));
	    String line;
	    while ((line = br.readLine()) != null) {
	       lines.add(line);
	    }
		
		
		br.close();
		
		return lines.toArray(new String[0]);
		
		
	}
	
	public static void writeFile(File inp, String output) throws IOException{
		BufferedWriter writer = new BufferedWriter(new  FileWriter(inp));
		writer.write( output);
		writer.close( );
	}
	public static void createFolder(String address){
		File dir = new File(address);
	 	dir.mkdir();
	}
	public static double randValueNormal(double mid, double stddev){
		 Random rand = new Random();
		 
		 return (rand.nextGaussian()*stddev)+mid;
	}
	
	public static double randValueNormalP(double mid, double stddev){
		 Random rand = new Random();
		 double r= (rand.nextGaussian()*stddev)+mid;
		 return (r>0)?r:0 ;
	}
	
	public static double CuttedRandValueNormal(double mid, double stddev, double low){
		 Random rand = new Random();
		 
		 double result =  (rand.nextGaussian()*stddev)+mid;
		 if(result<low)
			 return low;
		 return result;
	}
	public static <E> boolean includesAll(List<E> mainlist, List<E> sublist){
		for(E x: sublist)
			if(!mainlist.contains(x))
				return false;
		return true;
	}
	public static String readURL(String url) throws MalformedURLException, IOException{
		 InputStream in = new URL( url ).openStream();

		 
		 return IOUtils.toString( in ) ;
		 
		 
	}

	public static <E> void addNewOnes(List<E> mainList, List<E> newList) {
		
		for(E x: newList)
			if(!mainList.contains(x))
				mainList.add(x);
		
		
	}

	public static <E> List<List<E>> getPowerSet(List<E> originalSet) {
		
		
		List<List<E>> sets = new ArrayList<List<E>>();
        if (originalSet.isEmpty()) {
            sets.add(new ArrayList<E>());
            return sets;
        }
        List<E> list = new ArrayList<E>(originalSet);
        E head = list.get(0);
        List<E> rest = new ArrayList<E>(list.subList(1, list.size()));
        for (List<E> set : getPowerSet(rest)) {
        	List<E> newSet = new ArrayList<E>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
	    
	}

	public static Set<Integer> randIntSet(int max,int no) {
		
		Set<Integer> result = new HashSet<Integer>();
		
		
		
		for(int cntr=0; cntr<no; cntr++){
			int newval = UtilityClass.randInt(0, max);
			
			while(result.contains(newval))
				newval =  UtilityClass.randInt(0, max);
			
			result.add(newval);
		}
		
		
		return result;
	}

	public static double findAverage(double[] nums) {

		double avg = 0;
		
		if(nums.length==0)
			return 0;
		
		for(double num:nums)
			avg+=num;
		
		avg /= nums.length;
		return avg;
	}
	
	public static  double getVariance(double[] data)
    {
        double mean = UtilityClass.findAverage(data);
        double temp = 0;
        for(double a :data)
            temp += (a-mean)*(a-mean);
        return temp/data.length;
    }

	public static double getStdDev(double[] data)
    {
        return Math.sqrt(getVariance(data));
    }

	
	public static double[][] readArrayFromCSV(String[] input){
		int rows = input.length;
		int cols = (input[0].split(",")).length;
		double[][] result = new double[rows][cols];
		
		for(int rowCntr =0; rowCntr<rows; rowCntr++){
			String [] spLine = input[rowCntr].split(",");
			for(int colCntr=0; colCntr<cols; colCntr++){
				result[rowCntr][colCntr] = Double.valueOf( spLine[colCntr]);
			}
				
		}
				
		
		
		return result;
		
	}
	
	public static String writeAsCSV(double[][] input){
		StringBuilder sb = new StringBuilder();
		for(int rcntr=0; rcntr < input.length; rcntr++){
			String sep = "";
			long binVal =0;
			for(int colcntr=0; colcntr< input[0].length; colcntr++)
			{
				
				binVal += input[rcntr][colcntr]* Math.pow(2, colcntr);
				sb.append(sep);
				sb.append(input[rcntr][colcntr]);
				sep = ",";
			}
			sb.append(sep);
			sb.append(binVal);
			if(rcntr!=input.length-1)
				sb.append(System.lineSeparator());
		}
		
		
		return sb.toString();
	}
	
	public static boolean hasDuplicateRows(double[][] input){
		
		for(int frcntr = 0; frcntr<input.length-1; frcntr++)
			for(int srcntr=frcntr+1; srcntr<input.length; srcntr++)
			{
				boolean sameValues = true;
				for(int ccntr=0;ccntr<input[0].length; ccntr++)
					if(input[frcntr][ccntr]!=input[srcntr][ccntr])
					{
						sameValues= false;
						break;
					}
				if(sameValues)
					return true;
			}
		
		
		return false;
		
	}
	
	public static boolean hasDuplicateCols(double[][] input){
		
		for(int fccntr = 0; fccntr<input[0].length-1; fccntr++)
			for(int sccntr=fccntr+1; sccntr<input[0].length; sccntr++)
			{
				boolean sameValues = true;
				for(int rcntr=0;rcntr<input.length; rcntr++)
					if(input[rcntr][fccntr]!=input[rcntr][sccntr])
					{
						sameValues= false;
						break;
					}
				if(sameValues)
					return true;
			}
		
		
		return false;
		
	}
	
	
	public static void DoubleShuffle(double[][] input , double[] input2){

	    Random rnd = ThreadLocalRandom.current();
	    for (int i = input.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      double a = input2[index];
	      input2[index] = input2[i];
	      input2[i] = a;
	      
	      double[] al = input[index];
	      input[index] = input[i];
	      input[i] = al;
	    }
	    
	  
	
	}
	
	public static double[][] shuffleRows(double[][] input){
		double[][] result = new double[input.length][input[0].length];
		
		int rowSize= input.length;
		
		List<Integer> indices = new ArrayList<Integer>();
		
		for(int cntr=0; cntr<rowSize; cntr++)
			indices.set(cntr,cntr);
		
		Collections.shuffle(indices);
		
		for(int cntr=0; cntr<rowSize; cntr++)
			result[cntr]= input[ indices.get(cntr)].clone();
		
		return result;
	}
	
	
	public static double[] shuffleArray(double[] ar)
	  {
	    // If running on Java 6 or older, use `new Random()` on RHS here
		double[] result = ar.clone();
		
	    Random rnd = ThreadLocalRandom.current();
	    for (int i = result.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      double a = result[index];
	      result[index] = result[i];
	      result[i] = a;
	    }
	    return result;
	  }
	
	public static void copyFile(String src, String tgt) throws IOException{
		FileUtils.copyFile(new File(src), new File(tgt));
	}
	
	public static URL getLocation(final Class<?> c) {
	    if (c == null) return null; // could not load the class

	    // try the easy way first
	    try {
	        final URL codeSourceLocation =
	            c.getProtectionDomain().getCodeSource().getLocation();
	        if (codeSourceLocation != null) return codeSourceLocation;
	    }
	    catch (final SecurityException e) {
	        // NB: Cannot access protection domain.
	    }
	    catch (final NullPointerException e) {
	        // NB: Protection domain or code source is null.
	    }

	    // NB: The easy way failed, so we try the hard way. We ask for the class
	    // itself as a resource, then strip the class's path from the URL string,
	    // leaving the base path.

	    // get the class's raw resource path
	    final URL classResource = c.getResource(c.getSimpleName() + ".class");
	    if (classResource == null) return null; // cannot find class resource

	    final String url = classResource.toString();
	    final String suffix = c.getCanonicalName().replace('.', '/') + ".class";
	    if (!url.endsWith(suffix)) return null; // weird URL

	    // strip the class's path from the URL string
	    final String base = url.substring(0, url.length() - suffix.length());

	    String path = base;

	    // remove the "jar:" prefix and "!/" suffix, if present
	    if (path.startsWith("jar:")) path = path.substring(4, path.length() - 2);

	    try {
	        return new URL(path);
	    }
	    catch (final MalformedURLException e) {
	        e.printStackTrace();
	        return null;
	    }
	} 
	
	
	
	
}
