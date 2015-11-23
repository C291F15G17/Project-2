import java.io.*;
import java.util.*;
import java.nio.file.*;
                 
public class createFiles
{
  public static void main(String[] args) throws IOException
    {
      BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));
      //Strings used for various operations later
      String s = "";
      String s2 = "";
      String s3 = "";
      String s4 = "";
      String s5 = "";
      String s6 = "";
      String s7 = "";
      String s8 = "";
      String s9 = "";
      String s10 = "";
      String s11 = "";
      String s12 = "";
      String s13 = "";
      String score = "review/score";
      String pterm = "product/title";
      String rterm1 = "review/summary";
      String rterm2 = "review/text";
      
      //Initialize id numbers
      int review_id = 1;
      int score_id = 1;
      try
      {
        //Create files that are needed
        File reviews = new File("reviews.txt");
        File scores = new File("scores.txt");
        File pterms = new File("pterms.txt");
        File rterms = new File("rterms.txt");
        
        //If files don't already exist, create them
        if (!reviews.exists())
        {
          reviews.createNewFile();
        }
        if (!scores.exists())
        {
          scores.createNewFile();
        }
        if (!pterms.exists())
        {
          pterms.createNewFile();
        }
        if (!rterms.exists())
        {
          rterms.createNewFile();
        }
        
        //Create all writers for writing to file
        FileWriter fwr = new FileWriter(reviews.getAbsoluteFile());
        BufferedWriter bwr = new BufferedWriter(fwr);
        FileWriter fws = new FileWriter(scores.getAbsoluteFile());
        BufferedWriter bws = new BufferedWriter(fws);
        FileWriter fwp = new FileWriter(pterms.getAbsoluteFile());
        BufferedWriter bwp = new BufferedWriter(fwp);
        FileWriter fwrterms = new FileWriter(rterms.getAbsoluteFile());
        BufferedWriter bwrterms = new BufferedWriter(fwrterms);
        //Write initial review id
        bwr.write(review_id + "");
      
        while((s = scan.readLine()) != null)
        {
          s5 = "";
          //If empty line, skip to next line and write a new line to file
          if(s.length() == 0)
          {
            s = scan.readLine();
            review_id++;
            bwr.write("\n");
            bwr.write(review_id + "");
          }
          
          //Trim title off of each line
          s2 = s.substring(s.indexOf(':')+2);
          //Replace double quotations
          s3 = s2.replaceAll("\"", "&quot;");
          //Replace double backslashes
          s4 = s3.replaceAll("\\\\", "\\\\\\");
          
          //Write scores to file
          if (s.toLowerCase().contains(score.toLowerCase()))
          {
            bws.write(s4 + "," + score_id + "\n");
            score_id++;
          }
          
          //Check for terms in product title
          if (s.toLowerCase().contains(pterm.toLowerCase()))
          {
            s5 += s2;
            //Grab each string in the line of product title
            Scanner thing = new Scanner(s5);
            while(thing.hasNext())
            {
              s6 = thing.next();
              //Get rid of all nonalphanumeric, nonunderscore characters
              s7   = s6.replaceAll("[^a-zA-Z0-9_]", "");
              //If length is 3 or greater, write to file
              if(s7.length() >= 3)
              {
                bwp.write(s7.toLowerCase() + "," + review_id + "\n");
              }
            }
          }
          
          //Check for terms in review summary
          if (s.toLowerCase().contains(rterm1.toLowerCase()))
          {
            s8 += s2;
            //Grab each string in the line of product title
            Scanner thing = new Scanner(s8);
            while(thing.hasNext())
            {
              s9 = thing.next();
              //Get rid of all nonalphanumeric, nonunderscore characters
              s10   = s9.replaceAll("[^a-zA-Z0-9_]", "");
              //If length is 3 or greater, write to file
              if(s10.length() >= 3)
              {
                bwrterms.write(s10.toLowerCase() + "," + review_id + "\n");
              }
            }
          }
          
          //Check for terms in review text
          if (s.toLowerCase().contains(rterm2.toLowerCase()))
          {
            s11 += s2;
            //Grab each string in the line of product title
            Scanner thing = new Scanner(s11);
            while(thing.hasNext())
            {
              s12 = thing.next();
              //Get rid of all nonalphanumeric, nonunderscore characters
              s13   = s12.replaceAll("[^a-zA-Z0-9_]", "");
              //If length is 3 or greater, write to file
              if(s13.length() >= 3)
              {
                bwrterms.write(s13.toLowerCase() + "," + review_id + "\n");
              }
            }
          }
          bwr.write("," + s4);
        }
        //Close all writers
        bwr.close();
        bws.close();
        bwp.close();
        bwrterms.close();
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
}
