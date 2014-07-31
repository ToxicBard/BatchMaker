/*
 * -PFB 07/30/14 - This is one of the first programs that
 * I ever wrote in my spare time, and the first one that was
 * actually useful for anything.  As such it was written 
 * terribly, and I intend to rewrite it at some point, 
 * but not tonight.  I just retrieved it from my
 * old files, and I'm going to put it in source control so that
 * I don't lose it.
 */
import java.io.*;
import java.util.StringTokenizer;

public class BatchMaker
{

    public BatchMaker()
    {
    }

    public static void main(String args[])
    {
    	
        System.out.println("1. Manually input paths");
        System.out.print("2. Get paths from save file");
        String op = inputFromKb("");
        if(op.equals("1"))
            getInfoFromUser();
        else
        if(op.equals("2"))
        {
            loadPaths();
        } else
        {
            System.out.println("Enter either 1 or 2.  Exiting.");
            System.exit(1);
        }
        subtitleTrack = Integer.parseInt(inputFromKb("Enter the number of the desired subtitle track, or '-2' for none:"));
        audioTrack = Integer.parseInt(inputFromKb("Enter the number of the desired audio track, or '-1' if only 1 is available:"));
        extraCLI = inputFromKb("Enter any additional CLI options:");
        PrintWriter outputFile = null;
        String batLocation = inputFromKb("Enter the location and name of the output batch or bash file:");
        String toWrite = fileBatch();
        try
        {
            outputFile = new PrintWriter(new File(batLocation));
        }
        catch(FileNotFoundException g)
        {
            System.out.println("FileNotFoundException when trying to create output batch file. Exiting.");
            System.exit(1);
        }
        outputFile.print(toWrite);
        outputFile.close();
        System.out.println("File created.");
    }

    public static void savePaths()
    {
        PrintWriter saveWriter = null;
        try
        {
            saveWriter = new PrintWriter(new File("SavedPaths.sav"));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("FileNotFoundException while saving.  Exiting.");
            System.exit(1);
        }
        saveWriter.println(OS);
        saveWriter.println(MEncoderPath);
        saveWriter.println(inputDirectory);
        saveWriter.println(outputDirectory);
        saveWriter.close();
        System.out.println("Paths saved");
    }

    public static void loadPaths()
    {
        BufferedReader loadFile = null;
        try
        {
            loadFile = new BufferedReader(new FileReader(new File("SavedPaths.sav")));
            OS = loadFile.readLine();
            MEncoderPath = loadFile.readLine();
            inputDirectory = loadFile.readLine();
            outputDirectory = loadFile.readLine();
            loadFile.close();
        }
        catch(IOException e)
        {
            System.out.println("IOException while loading file.  Exiting.");
            System.exit(1);
        }
        System.out.println("Loaded.");
        System.out.println((new StringBuilder()).append("MEncoder path:    ").append(MEncoderPath).toString());
        System.out.println((new StringBuilder()).append("Input Directory:  ").append(inputDirectory).toString());
        System.out.println((new StringBuilder()).append("Output Directory: ").append(outputDirectory).toString());
    }

    public static void getInfoFromUser()
    {
        getMEncoderPath();
        getInputOutputDirectory();
        savePaths();
    }

    public static String fileBatch()
    {
        String toReturn = "";
        if(OS.equals("linux")) toReturn += "#!/bin/bash\n\n";
        if(OS.equals("windows")) toReturn += "@ echo off\n";
        File InputDir = new File(inputDirectory);
        
        //putting the filenames into an array and sorting:
        String [] files = new String[InputDir.list().length];
        for(int i=0;i < InputDir.list().length; i++)
        	files[i] = InputDir.list()[i];
        java.util.Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
        for(int i=0;i < files.length;i++)
        	System.out.println(files[i]);
        
        
        for(int i = 0; i < InputDir.list().length; i++)
        {
        	if(OS.equals("linux")) toReturn += "echo \"Encoding " + files[i] + ".";
        	if(OS.equals("windows")) toReturn += "echo Encoding " + files[i] + ".";
            //toReturn = (new StringBuilder()).append(toReturn).append("echo Encoding ").append(files[i]).append(".  ").toString();
            if(audioTrack >= 0)
                toReturn = (new StringBuilder()).append(toReturn).append("Audio track ").append(audioTrack).append(".  ").toString();
            if(subtitleTrack >= -1)
                toReturn = (new StringBuilder()).append(toReturn).append("Subtitle track ").append(subtitleTrack).append(".").toString();
            if(OS.equals("linux")) toReturn += "\"";
            toReturn = (new StringBuilder()).append(toReturn).append("\n").toString();
            toReturn = (new StringBuilder()).append(toReturn).append(MEncoderPath).toString();
            toReturn = (new StringBuilder()).append(toReturn).append(" \"").append(inputDirectory).append(files[i]).append("\" -oac mp3lame ").toString();
            if(audioTrack >= 0)
                toReturn = (new StringBuilder()).append(toReturn).append("-aid ").append(audioTrack).append(" ").toString();
            if(subtitleTrack >= -1)
                toReturn = (new StringBuilder()).append(toReturn).append("-sid ").append(subtitleTrack).append(" ").toString();
            toReturn +=extraCLI + " ";
            toReturn = (new StringBuilder()).append(toReturn).append("-ass -ovc xvid -xvidencopts pass=1 ").toString();
            toReturn = (new StringBuilder()).append(toReturn).append("-o \"").append(outputDirectory).toString();
            toReturn = (new StringBuilder()).append(toReturn).append(getFileNameWithoutExtension(files[i])).append(".avi\"").toString();
            toReturn = (new StringBuilder()).append(toReturn).append("\n").toString();
        }

        if(OS.equals("linux")) toReturn += "echo \"Finished\"";
        if(OS.equals("windows")) toReturn += "echo Finished";
        return toReturn;
    }

    public static String inputFromKb(String display)
    {
        in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(display);
        try
        {
            return in.readLine();
        }
        catch(IOException e)
        {
            System.out.println("IOException.  This should not happen.");
        }
        return "error";
    }

    public static void getMEncoderPath()
    {
    	System.out.print("Enter OS:\n1. Windows\n2. Linux");
    	String temp = inputFromKb("");
    	if(temp.equals("1"))
    		{
    		OS = "windows";
    		System.out.println("Windows selected");
    		}
    	else if(temp.equals("2"))
    		{
    		OS = "linux";
    		System.out.println("Linux selected");
    		}
    	else
    		{
    		System.out.println("Enter either 1 or 2.  Exiting");
    		System.exit(1);
    		}
    	if(OS.equals("linux"))
    	{
    		MEncoderPath = "mencoder";
    	}
    	else if(OS.equals("windows"))
    	{
    		MEncoderPath = inputFromKb("Enter the location of mencoder.exe:");
    		File MEncoderTest = new File(MEncoderPath);
    		if(MEncoderTest.exists() && MEncoderTest.isFile())
    		{
    			System.out.println("File found. Continuing..");
    		} else
    		{
    			System.out.println("Location given either isn't found or is a directory.  Exiting.");
    		    System.exit(1);
    		}
    	}
    	else
    		{
    		System.out.println("OS String Error.  This should not happen");
    		System.exit(1);
    		}

    }

    public static void getInputOutputDirectory()
    {
        inputDirectory = inputFromKb("Enter the location of the input files:");
        File InputDirTest = new File(inputDirectory);
        if(InputDirTest.exists() && InputDirTest.isDirectory())
        {
            System.out.println("Directory found.  Continuing..");
        } else
        {
            System.out.println("Directory either isn't found or isn't a directory.  Exiting");
            System.exit(1);
        }
        outputDirectory = inputFromKb("Enter the location of the output files:");
        File OutputDirTest = new File(outputDirectory);
        if(OutputDirTest.exists() && OutputDirTest.isDirectory())
        {
            System.out.println("Directory found.  Continuing..");
        } else
        {
            System.out.println("Directory either isn't found or isn't a directory.  Exiting");
            System.exit(1);
        }
    }

    public static String getFileNameWithoutExtension(String name)
    {
        StringTokenizer st = new StringTokenizer(name, ".");
        return st.nextToken();
    }

    static BufferedReader in;
    static String extraCLI;
    static String MEncoderPath = "";
    static String OS = "";
    static String inputDirectory = "";
    static String outputDirectory = "";
    static int subtitleTrack = -1;
    static int audioTrack = -1;

}