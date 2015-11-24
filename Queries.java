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
           //System.out.println(new String(data.getData()));          
           set.add(new String(data.getData()));
           while ( cursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
           {
             //System.out.println(new String(data.getData()));
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
          //System.out.println(new String(data.getData()));
          set.add(new String(data.getData()));
          while ( cursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            //System.out.println(new String(data.getData()));
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

    //Method for handling search for scores 
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
        
        if (operation.equals(">"))
        {
          key.setData(score.getBytes());
          key.setSize(score.length());
          Cursor cursor = scores.openCursor(null, null);
          if (cursor.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            //set.add(new String(data.getData()));
            byte[] test = key.getData();
            while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              if(!key.getData().equals(test))
              {
                set.add(new String(data.getData()));
              }
            }
          }
        } 
        //If searching for "<"
        else
        {
          key.setData(score.getBytes());
          key.setSize(score.length());
          Cursor cursor = scores.openCursor(null, null);
          if (cursor.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            //set.add(new String(data.getData()));
            while (cursor.getPrev(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              set.add(new String(data.getData()));
            }
          }
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
      HashSet<String> valid = null;
      for (int i = 0; i < queries.size(); i++)
      {
        if (queries.get(i).contains(":"))
        {
          String[] sub_query = queries.get(i).split(":", 0);
          if (sub_query[0].equals("r"))
          {
            if (valid == null)
            {
              valid = searchRTerms(sub_query[1]);
            }
            else
            {
              valid.retainAll(searchRTerms(sub_query[1]));
            } 
          }
          else if (sub_query[0].equals("p"))
          {
            if (valid == null)
            {
              valid = searchPTerms(sub_query[1]);
            }
            else
            {
              valid.retainAll(searchPTerms(sub_query[1]));
            }
          }
        }
        //Handle prices, scores, dates
        else if (queries.get(i).contains("<"))
        {
          String[] sub_query = queries.get(i).split("<", 0);
          //Handle score
          if(sub_query[0].equals("rscore"))
          {
            if (valid == null)
            {
              valid = searchScores(sub_query[1], "<");
            }
            else
            {
              valid.retainAll(searchScores(sub_query[1], "<"));
            }
          }
          //Handle price
          else if(sub_query[0].equals("pprice"))
          {
            if (valid == null)
            {
             // valid = searchPrice(sub_query[1], "<");
            }
            else
            {
             // valid = retainAll(searchPrice(sub_query[1], "<"));
            }
          }
          //Handle dates
          else
          {
            if (valid == null)
            {
              //valid = searchDate(sub_query[1], "<");
            }
            else
            {
             // valid = retainAll(searchDate(sub_query[1], "<"));
            }
          }
        }
        
        else if (queries.get(i).contains(">"))
        {
          String[] sub_query = queries.get(i).split(">", 0);
          //Handle score
          if(sub_query[0].equals("rscore"))
          {
            if (valid == null)
            {
              valid = searchScores(sub_query[1], ">");
            }
            else
            {
              valid.retainAll(searchScores(sub_query[1], ">"));
            }
          }
          //Handle price
          else if(sub_query[0].equals("pprice"))
          {
            if (valid == null)
            {
             // valid = searchPrice(sub_query[1], ">");
            }
            else
            {
             // valid = retainAll(searchPrice(sub_query[1], ">"));
            }
          }
          //Handle dates
          else
          {
            if (valid == null)
            {
             // valid = searchDate(sub_query[1], ">");
            }
            else
            {
             // valid = retainAll(searchDate(sub_query[1], ">"));
            }
          }
        }
        //Assuming this is only terms with no r: or p:
        else
        {
          if (valid == null)
          {
            valid = searchRTerms(queries.get(i));
            valid.addAll(searchPTerms(queries.get(i)));
          }
          else
          {
            HashSet<String> valid2 = searchRTerms(queries.get(i));
            valid2.addAll(searchPTerms(queries.get(i)));
            valid.retainAll(valid2);
          }
        }

      }
      System.out.println(valid);
    }





}
