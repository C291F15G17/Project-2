import java.util.*;
import java.nio.ByteBuffer;
import com.sleepycat.db.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
                              
public class Queries
{
  //Method ask for a query and then returns a list of sub_queries
  public static ArrayList<String> getQueries()
    {
      Scanner input = new Scanner(System.in);
      ArrayList<String> queries = new ArrayList<String>();
      //Get the query from the user
      System.out.print("Please enter a query: ");
      String queryString = input.nextLine();
      Scanner queryInput = new Scanner(queryString);
      //Creates a ArrayList of queries all with 0 whitespace, handled based off of format
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
  //Takes in a term and then finds all ids that match term in pterms
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
         
         Cursor cursor = pterms.openCursor(null, null);
         //Handle wildcards
         if (term.contains("%"))
         {
           term = term.replaceAll("%", "");
           key.setData(term.getBytes());
           key.setSize(term.length());
           System.out.println(term);
           //Key range used to find smallest that contains the term
           if (cursor.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
           {
             set.add(new String(data.getData()));
             data = new DatabaseEntry();
             //Look for the remaining duplicates
             while ( cursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
             {
               //System.out.println(new String(data.getData()));
               set.add(new String(data.getData()));
               data = new DatabaseEntry();
             }
           }
           //After no duplicates go to the next key, check to see if it still contains the term
           while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS && new String(key.getData()).contains(term))
           {
             set.add(new String(data.getData()));
             //Get duplicates of that key
             while (cursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
             {
               set.add(new String(data.getData()));
               data = new DatabaseEntry();
             }
           }
         }
         //If no wildcard present
         else
         {
           key.setData(term.getBytes());
           key.setSize(term.length());
           if (cursor.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
           {
             set.add(new String(data.getData()));
             while ( cursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
             {
               //System.out.println(new String(data.getData()));
               set.add(new String(data.getData()));
             }
           }
         }
         cursor.close();
         pterms.close();
       }
       catch (Exception ex)
       {
         ex.getMessage();
       }

       return set;  
    }
  //Takes in a term and finds the ids of those that match in rterms
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
        
        Cursor cursor = rterms.openCursor(null, null);
        //Handle wildcards
        if (term.contains("%"))
        {
          term = term.replaceAll("%", "");
          key.setData(term.getBytes());
          key.setSize(term.length());
          System.out.println(term);
          //Key range used to find smalled that contains the term
          if (cursor.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            set.add(new String(data.getData()));
            data = new DatabaseEntry();
            //Find duplicates of the key
            while ( cursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              //System.out.println(new String(data.getData()));
              set.add(new String(data.getData()));
              data = new DatabaseEntry();
            }
          }
          //After the duplicates are done go to next key, make sure it still contains the term
          while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS && new String(key.getData()).contains(term))
          {
            set.add(new String(data.getData()));
            //Get any duplicates
            while (cursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              set.add(new String(data.getData()));
              data = new DatabaseEntry();
            }
          }
          
        }
        //If no wildcard
        else
        {
          key.setData(term.getBytes());
          key.setSize(term.length());
          if (cursor.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            set.add(new String(data.getData()));
            while ( cursor.getNextDup(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              //System.out.println(new String(data.getData()));
              set.add(new String(data.getData()));
            }
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
    public static HashSet<String> searchScores(String score)
    {
      String string;
      String[] sub_query;
      HashSet<String> set = new HashSet<String>();
      try
      {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setType(DatabaseType.BTREE);
        Database scores = new Database("sc.idx", null, dbConfig);
        DatabaseEntry key = new DatabaseEntry(), data = new DatabaseEntry();
        
        if (score.contains(">"))
        {
          sub_query = score.split(">");
          sub_query[1] += ".0";
          key.setData(sub_query[1].getBytes());
          key.setSize(sub_query[1].length());
          Cursor cursor = scores.openCursor(null, null);
          if (cursor.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            String str = new String(key.getData());
            if (!sub_query[1].equals(str))
            {
              set.add(new String(data.getData()));
            }
            
            while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              string = new String(key.getData());
              if (!sub_query[1].equals(string))
              {
                //System.out.println(string);
                set.add(new String(data.getData()));
              }
            }
          }
        } 
        //If searching for "<"
        else
        {
          sub_query = score.split("<");
          sub_query[1] += ".0";
          key.setData(sub_query[1].getBytes());
          key.setSize(sub_query[1].length());
          Cursor cursor = scores.openCursor(null, null);
          if (cursor.getSearchKeyRange(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            String str = new String(key.getData());
            if (!sub_query[1].equals(str))
            {
              set.add(new String(data.getData()));
            }
            
            while (cursor.getPrev(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              string = new String(key.getData());
              if (!sub_query[1].equals(string))
              {
                //System.out.println(string);
                set.add(new String(data.getData()));
              }
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

    public static HashSet<String> searchDates(String query)
    {
      HashSet<String> set = new HashSet<String>();
      DateFormat format = new SimpleDateFormat("yyyy/MM/dd z");
      Date date = new Date();
      String[] sub_query;
      if (query.contains("<"))
      {
        sub_query = query.split("<");
        try
        {
          date = format.parse(sub_query[1] + " UTC");
          
          DatabaseConfig dbConfig = new DatabaseConfig();
          dbConfig.setType(DatabaseType.HASH);
          
          Database reviews = new Database("rw.idx", null, dbConfig);
          DatabaseEntry key = new DatabaseEntry(), data = new DatabaseEntry();
          Cursor cursor = reviews.openCursor(null, null);
          if (cursor.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            String string = new String(data.getData());
            
            String[] sub_data = string.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            if (Long.parseLong(sub_data[7]) < (date.getTime()/1000L))
            {
              set.add(new String(key.getData()));
            }
            while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              string = new String(data.getData());
              sub_data = string.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
              if (Long.parseLong(sub_data[7]) < (date.getTime()/1000L))
              {
                set.add(new String(key.getData()));
              }
            }
          }
          cursor.close();
          reviews.close();
        }
        catch(Exception ex)
        {
          ex.getMessage();
        }

      }
      else if (query.contains(">"))
      {
        sub_query = query.split(">");
        try
        {
          date = format.parse(sub_query[1] + " UTC");
          
          DatabaseConfig dbConfig = new DatabaseConfig();
          dbConfig.setType(DatabaseType.HASH);
          
          Database reviews = new Database("rw.idx", null, dbConfig);
          DatabaseEntry key = new DatabaseEntry(), data = new DatabaseEntry();
          Cursor cursor = reviews.openCursor(null, null);
          if (cursor.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            String string = new String(data.getData());
            
            String[] sub_data = string.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            if (Long.parseLong(sub_data[7]) > (date.getTime()/1000L))
            {
              set.add(new String(key.getData()));
            }
            while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              string = new String(data.getData());
              sub_data = string.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
              if (Long.parseLong(sub_data[7]) > (date.getTime()/1000L))
              {
                set.add(new String(key.getData()));
              }
            }
          }
          cursor.close();
          reviews.close();
        }
        catch(Exception ex)
        {
          ex.getMessage();
        }
      }

  

      return set;
    }

    public static HashSet<String> searchPrices(String query)
    {
      HashSet<String> set = new HashSet<String>();
      String[] sub_query;
      if (query.contains("<"))
      {
        sub_query = query.split("<");
        try
        {
          DatabaseConfig dbConfig = new DatabaseConfig();
          dbConfig.setType(DatabaseType.HASH);
          
          Database reviews = new Database("rw.idx", null, dbConfig);
          DatabaseEntry key = new DatabaseEntry(), data = new DatabaseEntry();
          Cursor cursor = reviews.openCursor(null, null);
          if (cursor.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            String string = new String(data.getData());
            
            String[] sub_data = string.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            if (sub_data[2].equals("unknown"))
            {
            }
            //May need to convert to number
            else if (Float.parseFloat(sub_data[2]) < Float.parseFloat(sub_query[1]))
            {
              set.add(new String(key.getData()));
            }
            
            while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              string = new String(data.getData());
              sub_data = string.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
              if (sub_data[2].equals("unknown"))
              {}
              //May need to convert to number
              else if (Float.parseFloat(sub_data[2]) < Float.parseFloat(sub_query[1]))
              {
                set.add(new String(key.getData()));
              }
            }
          }
          cursor.close();
          reviews.close();
        }
        catch(Exception ex)
        {
          ex.getMessage();
        }

      }
      else if (query.contains(">"))
      {
        sub_query = query.split(">");
        try
        {
          DatabaseConfig dbConfig = new DatabaseConfig();
          dbConfig.setType(DatabaseType.HASH);
          
          Database reviews = new Database("rw.idx", null, dbConfig);
          DatabaseEntry key = new DatabaseEntry(), data = new DatabaseEntry();
          Cursor cursor = reviews.openCursor(null, null);
          if (cursor.getFirst(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
          {
            String string = new String(data.getData());
            
            String[] sub_data = string.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            if (sub_data[2].equals("unknown"))
            {
            }
            //May need to convert to number
            else if (Float.parseFloat(sub_data[2]) > Float.parseFloat(sub_query[1]))
            {
              set.add(new String(key.getData()));
            }
            
            while (cursor.getNext(key, data, LockMode.DEFAULT) == OperationStatus.SUCCESS)
            {
              string = new String(data.getData());
              sub_data = string.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
              if (sub_data[2].equals("unknown"))
              {}
              //May need to convert to number
              else if (Float.parseFloat(sub_data[2]) > Float.parseFloat(sub_query[1]))
              {
                set.add(new String(key.getData()));
              }
            }
          }
          cursor.close();
          reviews.close();
        }
        catch(Exception ex)
        {
          ex.getMessage();
        }
      }


      return set;
    }

    public static void printReviews(HashSet<String> ids) {
      try
      {      
      DatabaseConfig dbConfig = new DatabaseConfig();
      dbConfig.setType(DatabaseType.HASH);
      //dbConfig.setSortedDuplicates(true);
      Database reviews = new Database("rw.idx", null, dbConfig);
      DatabaseEntry key = new DatabaseEntry(), data = new DatabaseEntry();
      OperationStatus oprStatus;

      Iterator it = ids.iterator();
      while (it.hasNext()) {
        String revid =(String) it.next();
        key.setData(revid.getBytes());
        key.setSize(revid.length());
        if ((reviews.get(null, key, data, LockMode.DEFAULT)) == OperationStatus.SUCCESS)
        {
          System.out.println("REVIEW ID: " + revid);
          String review = new String(data.getData());
          String[] revparts = review.split("(?x),(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
          System.out.println("PRODUCT NO = " + revparts[0] + " | PRODUCT NAME = " + revparts[1] + " | PRICE = " + revparts[2]);
          System.out.println("USER NO = " + revparts[3] + " | USERNAME = " + revparts[4]);
          System.out.println("HELPFUL RATING = " + revparts[5] + " | RATING = " + revparts[6]);
          System.out.println("REVIEW NO = " + revparts[7] + " | REVIEW TITLE = " + revparts[8]);
          System.out.println("REVIEW = " + revparts[9]);

          System.out.println();
//System.out.println(new String(data.getData()));
        }
      }
      reviews.close();
      }
      catch (Exception ex)
      {
        ex.getMessage();
      }
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
        else if (queries.get(i).contains("<") || queries.get(i).contains(">"))
        {
          //Handle price, dates and scores
          if ( queries.get(i).contains("rdate"))
          {
            if (valid == null)
            {
              valid = searchDates(queries.get(i));
            }
            else
            {
              valid.retainAll(searchDates(queries.get(i)));
            }
          }
          else if (queries.get(i).contains("pprice"))
          {
            if (valid == null)
            {
              valid = searchPrices(queries.get(i));
            }
            else
            {
              valid.retainAll(searchPrices(queries.get(i)));
            }
          }
          else if (queries.get(i).contains("rscore"))
          {
            if (valid == null)
            {
              valid = searchScores(queries.get(i));
            }
            else
            {
              valid.retainAll(searchScores(queries.get(i)));
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
      printReviews(valid);
    }





}
