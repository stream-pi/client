import java.io.*;

public class io {
    public static String readFileRaw(String fileName)
    {
        String toBeReturned = null;
        try
        {
            BufferedReader bf = new BufferedReader(new FileReader(fileName));
            toBeReturned = bf.readLine();
            bf.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return toBeReturned;
    }

    public static String[] readFileArranged(String fileName, String s)
    {
        return readFileRaw(fileName).split(s);
    }

    public static void writeToFile(String content,String fileName)
    {
        try
        {
            BufferedWriter bf = new BufferedWriter(new FileWriter(fileName));
            bf.write(content);
            bf.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void writeToFileRaw(byte[] toWrite, String fileName)
    {
        try
        {
            FileOutputStream fs = new FileOutputStream(fileName);
            fs.write(toWrite);
            fs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
