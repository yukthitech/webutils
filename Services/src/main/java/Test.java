import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.yukthitech.webutils.TestEnum;

public class Test
{

	public static void main(String[] args) throws Exception
	{
		Set<Object> set = new HashSet<>();
		set.add("val1");
		set.add(TestEnum.ONE);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
		
		System.out.println(mapper.writeValueAsString(set));
		
		System.out.println(mapper.readValue("[\"com.yukthitech.webutils.TestEnum\",\"ONE\"]", Object.class));
	}

}
