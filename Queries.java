import java.util.*;
import java.nio.ByteBuffer;
import com.sleepycat.db.*;


public class Queries
{
  public static ArrayList<String> getQueries()
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
      return queries;
    }

  public static HashSet<String> searchPTerms(String term)
    {
      HashSet<String> set = new HashSet<String>();
       try
       {
         DatabaseConfig dbConfig = new DatabaseConfig();
         dbConfig.setType(DatabaseType.BTREE);
         //dbConfig.setSortedDuplicates(true);
         Database pterms = new Database("pt.idx", null, dbConfig);
         DatabaseEntry key = new DatabaseEntry(), data = new DatabaseEntry();
         key.setData(term.getBytes());
         key.setSize(term.length());
         Cursor cursor = pterms.openCursor(null, null);
         if ( cursor.getSearchKey(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
         {
           System.out.println(new String(data.getData()));          
           set.add(new String(data.getData()));
           while ( cursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
           {
             System.out.println(new String(data.getData()));
             set.add(new String(data.getData()));
           }
         }
         pterms.close();
       }
       catch (Exception ex)
       {
         ex.getMessage();
       }

       return set;  
    }

  public static HashSet<String> searchRTerms(String term)
    {
      HashSet<String> set = new HashSet<String>();
      try
      {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setType(DatabaseType.BTREE);
        //dbConfig.setSortedDuplicates(true);
        Database rterms = new Database("rt.idx", null, dbConfig);
        DatabaseEntry key = new DatabaseEntry(), data = new DatabaseEntry();
        OperationStatus oprStatus;
        key.setData(term.getBytes());
        key.setSize(term.length());
        Cursor cursor = rterms.openCursor(null, null);
        if ( cursor.getSearchKey(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
        {
          System.out.println(new String(data.getData()));
          set.add(new String(data.getData()));
          while ( cursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            System.out.println(new String(data.getData()));
            set.add(new String(data.getData()));
          }
        }
        
        
        cursor.close();
        rterms.close();
      }
      catch (Exception ex)
      {
        ex.getMessage();
      }
      
      return set;
    }

     public static HashSet<String> searchScores(String score, String operation)
    {
      HashSet<String> set = new HashSet<String>();
      try
      {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setType(DatabaseType.BTREE);
        //dbConfig.setSortedDuplicates(true);
        Database scores = new Database("sc.idx", null, dbConfig);
        DatabaseEntry key = new DatabaseEntry(), data = new DatabaseEntry();
        if (operation.equals("<"))
        {
          key.setData("0.0".getBytes());
          key.setSize("0.0".length());
          
        } else
        {
          
        }
        
        scores.close();
      }
      catch (Exception ex)
      {
        ex.getMessage();
      }
      
      return set;
    }



  public static void main(String [] args)
    {
      ArrayList<String> queries = getQueries();
      System.out.println(searchRTerms(queries.get(0)));
    }





}
