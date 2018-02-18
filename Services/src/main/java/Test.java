import java.io.FileInputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;

public class Test
{
	public static void main(String[] args) throws Exception
	{
		ToXMLContentHandler handler = new ToXMLContentHandler();
		AutoDetectParser parser = new AutoDetectParser();
		Metadata metadata = new Metadata();

		FileInputStream fis = new FileInputStream("C:\\Users\\akiran\\AppData\\Local\\Temp\\abimannan6537001908269022591.docx");
		parser.parse(fis, handler, metadata);
		String resultContent = handler.toString();
		
		fis.close();
		
		System.out.println(resultContent);

	}
}
