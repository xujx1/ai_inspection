import org.example.utils.JsonCleanerUtils;
import org.junit.jupiter.api.Test;

public class JsonCleanerTest {
    
    @Test
    public void testCleanJsonArguments() {
        // 测试包含markdown代码块的情况
        String input1 = "```json\n{\"actualValue\": 555, \"expectedValue\": 554, \"threshold\": 100}\n```";
        String result1 = JsonCleanerUtils.cleanJsonArguments(input1);
        System.out.println("Input: " + input1);
        System.out.println("Output: " + result1);
        
        // 测试包含额外文本的情况
        String input2 = "根据上述工具调用的结果，我们可以得出以下结论：\n{\"actualValue\": 555, \"expectedValue\": 554, \"threshold\": 100}\n### 详细业务数据部分";
        String result2 = JsonCleanerUtils.cleanJsonArguments(input2);
        System.out.println("Input: " + input2);
        System.out.println("Output: " + result2);
        
        // 测试正常JSON
        String input3 = "{\"actualValue\": 555, \"expectedValue\": 554, \"threshold\": 100}";
        String result3 = JsonCleanerUtils.cleanJsonArguments(input3);
        System.out.println("Input: " + input3);
        System.out.println("Output: " + result3);
    }
}