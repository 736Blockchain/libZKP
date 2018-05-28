package tutorial;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.json.JSONObject;

public class generateJson {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		JSONObject js = new JSONObject();
		js.put("1024A", 2);
		js.put("1024B", 3);
		js.put("1024C", 2);
		js.put("2048A", 3);
		js.put("2048B", 2);
		js.put("2048C", 4);
		js.put("3072A", 5);
		js.put("3072B", 4);
		js.put("3072C", 1);
		
        
        FileWriter fw = new FileWriter("/Users/jiaxyan/Desktop/data.json");  
        PrintWriter out = new PrintWriter(fw);  
        out.write(js.toString());  
        out.println();  
        fw.close();  
        out.close();  
	}

}
