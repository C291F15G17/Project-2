import java.io.*;
import java.util.*;
import java.nio.file.*;
                 
public class createFiles
{
  public static void main(String[] args) throws IOException
    {
      BufferedReader scan = new BufferedReader(new InputStreamReader(System.in));
      String s;
      String s2;
      String s3 = "";
      String s4 = "";
      String s5 = "";
      String s6 = "";
      String s7 = "";
      String score = "review/score";
      String pterm = "product/title";
      int review_id = 1;
      int score_id = 1;
      try
      {
        File reviews = new File("reviews.txt");
        File scores = new File("scores.txt");
        File pterms = new File("pterms.txt");
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
        FileWriter fwr = new FileWriter(reviews.getAbsoluteFile());
        BufferedWriter bwr = new BufferedWriter(fwr);
        FileWriter fws = new FileWriter(scores.getAbsoluteFile());
        BufferedWriter bws = new BufferedWriter(fws);
        FileWriter fwp = new FileWriter(pterms.getAbsoluteFile());
        BufferedWriter bwp = new BufferedWriter(fwp);
        bwr.write(review_id + "");
      
        while((s = scan.readLine()) != null)
        {
          if(s.length() == 0)
          {
            s = scan.readLine();
            review_id++;
            bwr.write("\n");
            bwr.write(review_id + "");
          }
          s2 = s.substring(s.indexOf(':')+2);
          s3 = s2.replaceAll("\"", "&quot;");
          s4 = s3.replaceAll("\\\\", "\\\\\\");
          if (s.toLowerCase().contains(score.toLowerCase()))
          {
            bws.write(s4 + "," + score_id + "\n");
            score_id++;
          }
          if (s.toLowerCase().contains(pterm.toLowerCase()))
          {
            s5 = s2;
            Scanner thing = new Scanner(s5);
            while(thing.next() != null)
            {
              s6 = thing.next();
              s7   = s6.replaceAll("[^a-zA-Z0-9_]", "");
              if(s7.length() >= 3)
              {
                bwp.write(s7 + review_id + "\n");
              }
            }
          }
          }
          }
          System.out.println(s4);
          bwr.write("," + s4);
        }
        bwr.close();
        bws.close();
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
}
