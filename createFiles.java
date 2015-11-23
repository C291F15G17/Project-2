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
      String score = "review/score";
      int review_id = 1;
      int score_id = 1;
      try
      {
        File reviews = new File("reviews.txt");
        File scores = new File("scores.txt");
        if (!reviews.exists())
        {
          reviews.createNewFile();
        }
        if (!scores.exists())
        {
          scores.createNewFile();
        }
        FileWriter fwr = new FileWriter(reviews.getAbsoluteFile());
        BufferedWriter bwr = new BufferedWriter(fwr);
        FileWriter fws = new FileWriter(scores.getAbsoluteFile());
        BufferedWriter bws = new BufferedWriter(fws);
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
