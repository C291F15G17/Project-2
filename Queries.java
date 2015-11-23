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
      
      while (queryInput.hasNext())
      {
      queries.add(queryInput.next());
      }
      
      for ( int i =0; i < queries.size(); i++)
      {
        System.out.println(queries.get(i));
      }

    }





}
