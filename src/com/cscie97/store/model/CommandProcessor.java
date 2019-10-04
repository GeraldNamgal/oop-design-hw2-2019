package com.cscie97.store.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandProcessor
{
    /* My Variables */

    private Modeler modeler;
    private int lineNum = 0;
    
    /* *
     * API Methods
     */

    public void processCommand(String command)
    {       
        parseAndProcess(command);
    }
    
    // Referenced https://www.journaldev.com/709/java-read-file-line-by-line
    public void processCommandFile(String commandFile)
    {       
        // Check if the file is empty
        try
        {
            File newFile = new File(commandFile);
            if (newFile != null)
            {
                if (newFile.length() == 0)
                    throw new CommandProcessorException("in processCommandFile method", "file is empty");
            }
        }

        catch (CommandProcessorException exception)
        {
            System.out.println(exception.getMessage());
            return;
        }

        // Read file
        try
        {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(commandFile));
            String line = reader.readLine();

            while (line != null)
            {
                // Counter up lineNum
                lineNum++;

                // Call parseAndProcess method if line isn't empty
                if (line.length() > 0)
                    parseAndProcess(line);

                // Read next line
                line = reader.readLine();
            }

            reader.close();
        }

        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }
    
    /* *
     * Modeler-ish API Utility Methods
     */

    public void createModeler()
    {
        Modeler modeler = new Modeler();
        this.modeler = modeler;
    }
    
    /* *
     * Other Utility Methods
     */

    public void parseAndProcess(String input)
    {
    	// Trim leading and trailing whitespace
        String trimmedInput = input.trim();

        // Check if input is a comment
        if (trimmedInput.charAt(0) == '#')
        {
            System.out.println(trimmedInput);
            return;
        }

        // Delimit input string on whitespace and add each value to array
        String[] splitInputArr = trimmedInput.split("\\s+");
        
        /* If input contained quotes, then validate their correct usage and fix array - code block BEGINNING */
        
        boolean openQuote = false;
        ArrayList<Integer> indicesOfOpeningQuotes = new ArrayList<Integer>();
        ArrayList<Integer> indicesOfClosingQuotes = new ArrayList<Integer>();
        
        for (int i = 0; i < splitInputArr.length; i++)
        {
        	// If a stand-alone quote, must check if it's an opening or closing quote
        	if ((splitInputArr[i].length() == 1) && (splitInputArr[i].charAt(0) == '"'))
        	{
        		if (openQuote == false)
        		{
                    indicesOfOpeningQuotes.add(i);
                    openQuote = true;
        		}

                else
                {
                    indicesOfClosingQuotes.add(i);
                    openQuote = false;
                }
        	}    
	        
        	// If not a stand-alone quote
	        else
	        {
	        	// Checks string if has a quote as first character
	            if (splitInputArr[i].charAt(0) == '"')
	            {
	                if (openQuote == true)
	                {
	                	try
	                    {
	                        if (lineNum == 0)
	                            throw new CommandProcessorException("in processCommand method", "missing closing quote in user input");
	
	                        else
	                            throw new CommandProcessorException("in processCommandFile method", "missing closing quote in user input", lineNum);            
	                    }
	                    
	                    catch (CommandProcessorException exception)
	                    {
	                        if (lineNum == 0)
	                        {
	                            System.out.println("-: " + trimmedInput);
	                            System.out.println();
	                            System.out.println(exception.getMessage());
	                            
	                            return;
	                        }
	            
	                        else
	                        {
	                            System.out.println("-: " + trimmedInput);
	                            System.out.println();
	                            System.out.println(exception.getMessageLine());
	                            
	                            return;
	                        }
	                    }
	                }
	                
	                else
	                {
	                	indicesOfOpeningQuotes.add(i);
	                	openQuote = true;
	                }
	            }
	
	            // Checks string if has a quote as last character
	            if (splitInputArr[i].charAt(splitInputArr[i].length() - 1) == '"')
	            {
	                if (openQuote == true)
	                {
	                	indicesOfClosingQuotes.add(i);
	                	openQuote = false;
	                }
	                
	                else
	                {
	                	try
	                    {
	                        if (lineNum == 0)
	                            throw new CommandProcessorException("in processCommand method", "missing opening quote in user input");
	
	                        else
	                            throw new CommandProcessorException("in processCommandFile method", "missing opening quote in user input", lineNum);            
	                    }
	                    
	                    catch (CommandProcessorException exception)
	                    {
	                        if (lineNum == 0)
	                        {
	                            System.out.println("-: " + trimmedInput);
	                            System.out.println();
	                            System.out.println(exception.getMessage());
	                            
	                            return;
	                        }
	            
	                        else
	                        {
	                            System.out.println("-: " + trimmedInput);
	                            System.out.println();
	                            System.out.println(exception.getMessageLine());
	                            
	                            return;
	                        }
	                    }
	                }
	            }
	        }
        }
        
        // If there is an ultimate open quote without a matching closing quote then throw exception 
        if (openQuote == true)
        {
        	try
            {
                if (lineNum == 0)
                    throw new CommandProcessorException("in processCommand method", "missing closing quote in user input");

                else
                    throw new CommandProcessorException("in processCommandFile method", "missing closing quote in user input", lineNum);            
            }
            
            catch (CommandProcessorException exception)
            {
                if (lineNum == 0)
                {
                    System.out.println("-: " + trimmedInput);
                    System.out.println();
                    System.out.println(exception.getMessage());
                    
                    return;
                }
    
                else
                {
                    System.out.println("-: " + trimmedInput);
                    System.out.println();
                    System.out.println(exception.getMessageLine());
                    
                    return;
                }
            }
        }
        
        // If input had quotes, string quoted input(s) back together
        if (indicesOfOpeningQuotes.size() > 0)
        {       	
        	// Create a modified splitInputArr, named splitStringQuotesArr, with quoted input(s) back together
            ArrayList<String> splitStringQuotesArr = new ArrayList<String>();
            
            // Initialize index counter for opening and closing quotes arrays
        	int index = 0;
        	
        	// Initialize a quote string
        	String quote = "";
        	
        	// Loop through splitInputArr to create new splitStringQuotesArr string array
        	for (int i = 0; i < splitInputArr.length; i++)
	        {
        		// If found all quotes then just transfer the element to new array
        		if (index >= indicesOfOpeningQuotes.size())        			
        			splitStringQuotesArr.add(splitInputArr[i]);        		
        		
        		else
        		{
	        		if (openQuote == false)
	        		{
	        			if (i == indicesOfOpeningQuotes.get(index))       				
	        				openQuote = true;
	        			        			
	        			else	        			
	        				splitStringQuotesArr.add(splitInputArr[i]);	        			
	        		}
	        			
	        		if (openQuote == true)
	        		{
			        	// If element contains the closing quote
	        			if (i == indicesOfClosingQuotes.get(index))
			        	{
			        		// Append element to quote string
			        		quote += splitInputArr[i];
			        		
			        		// Remove quotes from quote and trim its whitespace
			        		StringBuffer sbf = new StringBuffer(quote);
			                quote = sbf.deleteCharAt(0).toString();
			                sbf = new StringBuffer(quote);
			                quote = sbf.deleteCharAt(quote.length() - 1).toString();
			                quote = quote.trim();
			        		
			        		// Add quote to new array
			        		splitStringQuotesArr.add(quote);			        		
			        		
			        		// Set openQuote to false, increment counter, and reset quote string since found closing quote
			        		openQuote = false;			        		
			        		index++;
			        		quote = "";
			        	}
			        			        	
			        	else
			        	{
			        		// Append element to quote string with a space added
			        		quote += splitInputArr[i] + " ";
			        	}
	        		}
        		}
	        }    	
        	
        	// Redefine splitInputArr with new array
        	splitInputArr = new String[splitStringQuotesArr.size()]; 
        	splitStringQuotesArr.toArray(splitInputArr);        	
        }
        
        /* If input contained quotes, then validate their correct usage and fix array - code block END */        
        
        // If input is create-modeler command
        if ((splitInputArr[0].equalsIgnoreCase("create-modeler")) && (splitInputArr.length == 1))
        {
        	// Check if modeler already exists
            if (this.modeler != null)
        	{
            	try
                {
                    if (lineNum == 0)
                        throw new CommandProcessorException("in processCommand method", "modeler already exists; input rejected");

                    else
                        throw new CommandProcessorException("in processCommandFile method", "modeler already exists; input rejected", lineNum);            
                }
                
                catch (CommandProcessorException exception)
                {
                    if (lineNum == 0)
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessage());
                        
                        return;
                    }
        
                    else
                    {
                        System.out.println("-: " + trimmedInput);
                        System.out.println();
                        System.out.println(exception.getMessageLine());
                        
                        return;
                    }
                }
        	}      	
        	
        	System.out.println("-: " + trimmedInput);
            createModeler();

            System.out.println();        	
        }
        
        // If modeler does not exist, don't accept commands and return
        else if (modeler == null)
        {
            try
            {
                if (lineNum == 0)
                    throw new CommandProcessorException("in processCommand method", "no modeler exists; create modeler first (\"create-modeler\")");

                else
                    throw new CommandProcessorException("in processCommandFile method", "no modeler exists; create modeler first (\"create-modeler\")", lineNum);            
            }
            
            catch (CommandProcessorException exception)
            {
                if (lineNum == 0)
                {
                    System.out.println("-: " + trimmedInput);
                    System.out.println();
                    System.out.println(exception.getMessage());
                    
                    return;
                }
    
                else
                {
                    System.out.println("-: " + trimmedInput);
                    System.out.println();
                    System.out.println(exception.getMessageLine());
                    
                    return;
                }
            }
        }
        
        // TODO: If input is other valid command, call corresponding method
        else if (splitInputArr.length > 0)
        {
        	// TODO: Change the '-' commands back to space, e.g., define store, not define-store
        	if (splitInputArr[0].equalsIgnoreCase("define-store"))
            {
        		
            }
        	
        	// TODO: Debugging           
            System.out.println(Arrays.deepToString(splitInputArr));            
        }       
    }
}
