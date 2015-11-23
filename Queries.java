import java.util.*;
public class Queries
{
  public static void main(String [] args)
    {
      Scanner input = new Scanner(System.in);
      ArrayList<String> queries = new ArrayList<String>();

      System.out.print("Please enter a query: ");
      String queryString = input.nextLine();
      Scanner queryInput = new Scanner(queryString);
      //Creates a ArrayList of queries all with 0 whitespace
      while (queryInput.hasNext())
      {
        String curr = queryInput.next();
        if (curr.equals("pprice") || curr.equals("rscore") || curr.equals("rdate"))
        {
          String curr2 = queryInput.next();
          if (curr2.equals(">") || curr2.equals("<"))
          {
            queries.add(curr + curr2 + queryInput.next());
          } else if (curr2.contains(">")|| curr2.contains("<"))
          {
            queries.add(curr + curr2);
          }
        } else if (curr.contains("pprice") || curr.contains("rscore") || curr.contains("rdate"))
        {
          if ( curr.matches(".*\\d+.*") )
          {
            queries.add(curr);
          } else
          {
            queries.add( curr + queryInput.next());
          }
        } else
        {
          queries.add(curr);
        }

            
      }
      
      for ( int i =0; i < queries.size(); i++)
      {
        System.out.println(queries.get(i));
      }

    }





}
