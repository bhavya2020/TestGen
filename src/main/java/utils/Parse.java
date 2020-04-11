package utils;

import com.intellij.psi.PsiNameValuePair;

import java.util.ArrayList;

public class Parse {

    public static ArrayList<ArrayList<Integer>> getParsedAttributes(PsiNameValuePair[] attributes) {

        ArrayList<ArrayList<Integer>> parsedAttributes = new ArrayList<>();
        ArrayList<String> attributesValues = new ArrayList<>();

        for (PsiNameValuePair attribute : attributes) {
            attributesValues.add(attribute.getLiteralValue());
        }

        for(String attributeValue : attributesValues){
            parsedAttributes.add(parseAttribute(attributeValue));
        }

        return parsedAttributes;

    }

    private  static ArrayList<Integer> parseAttribute(String attribute) {
        ArrayList<Integer> parsedAttribute = new ArrayList<>();
        String[] rangeBased = attribute.split(":");
        String[] discrete = attribute.split(" ");
        if (rangeBased.length > 1) {
            for (int i = Integer.parseInt(rangeBased[0]); i <= Integer.parseInt(rangeBased[1]); i++) {
                parsedAttribute.add(i);
            }
        } else if (discrete.length > 1) {

            for (String value : discrete) {
                parsedAttribute.add(Integer.parseInt(value));
            }
        }
        return parsedAttribute;
    }
}