package UI;

import java.util.ArrayList;

public class FormatFile {

    public static String getCoverageFileText(ArrayList<ArrayList<ArrayList<String>>> coverageOfAllMethods, ArrayList<String> methodNames){

        StringBuilder text = new StringBuilder();

        int count = 0;
        for(String method : methodNames){
            text.append(method);
            text.append("\n");
            for(ArrayList<String> coverage : coverageOfAllMethods.get(count)){
                if(coverage.size() == 1){
                    text.append("\nTotal Coverage: ");
                    text.append(coverage.get(0));
                    text.append("%\n\n");
                }else{
                    text.append(coverage.get(0));
                    text.append(" : ");
                    text.append(coverage.get(2));
                    text.append("% | Pairs Covered: ");
                    text.append(coverage.get(1));
                    text.append("\n");
                }
            }
            count++;
        }

        return text.toString();
    }

    public static String getTestCasesFileText(ArrayList<String> methodNames, ArrayList<ArrayList<String>> methodParameters, ArrayList<ArrayList<ArrayList<Integer>>> prioritisedTestDataOfAllMethods, String className, ArrayList<String> returnTypes){

        StringBuilder text = new StringBuilder();

        text.append("/*\n*\n*\n\tThis is an autogenerated file by TestGen. \n\tTODO:Please Fill Expected Values\n*\n*\n*/\n");
        text.append("\nimport org.junit.Test;\n");
        text.append("import org.junit.Assert;\n\n");
        text.append("public class Test");
        text.append(className);
        text.append(" {\n\n");
        text.append("\t");
        text.append(className);
        text.append(" ");
        text.append(className.toLowerCase());
        text.append(" = new ");
        text.append(className);
        text.append("();\n\n");

        int count = 0;
        for(String method : methodNames){

            int testCaseNum = 1;
            for(ArrayList<Integer> testCases: prioritisedTestDataOfAllMethods.get(count)){
                text.append("\t@Test\n");
                text.append("\tpublic void Test");
                String methodName = method.replace(method.charAt(0),Character.toUpperCase(method.charAt(0)));
                text.append(methodName);
                text.append(testCaseNum);
                text.append("(){\n\n");
                text.append("\t\t//TODO:Fill Expected Value\n");
                text.append("\t\t");
                text.append(returnTypes.get(count));
                text.append(" expectedValue = ");
                if(Character.isLowerCase(returnTypes.get(count).charAt(0)))
                    text.append("0;\n");
                else
                    text.append("null;\n");
                text.append("\t\t");
                text.append(returnTypes.get(count));
                text.append(" actualValue = ");
                text.append(className.toLowerCase());
                text.append(".");
                text.append(method);
                text.append("(");
                for(int i=0;i<testCases.size();i++){
                    text.append(testCases.get(i));
                    if(i!=testCases.size()-1){
                        text.append(", ");
                    }
                }
                text.append(");\n\t\t");
                text.append("Assert.assertEquals(expectedValue,actualValue);\n");
                text.append("\t}\n\n");
                testCaseNum++;

            }

            count++;
        }

        text.append("}");

        return text.toString();
    }
}
