/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.util.ArrayList;

public class RemainderCalculator {

    //
    private String xor(String a, String b) {

        //   # initialize result
        String result = "";
        // # Traverse all bits, if bits are
        //# same, then XOR is 0, else 1
        for (int i = 0; i < b.length(); i++) {
            if (a.substring(i, i + 1).equals(b.substring(i, i + 1))) {
                result = result + "0";
            } else {
                result = result + "1";
            }

        }
        return (result);
    }

    private  String mod2div(String divisor, String divident) {

        int pick = divisor.length();

      
        String tmp = divident.substring(0, pick);
        int len = tmp.length();
        while (pick < divident.length()) {
            if ((tmp.substring(0, 1)).equals("1")) {

              
                String tmp1 = xor(divisor, tmp);
                tmp = tmp1.substring(len - 3, len) + divident.substring(pick, pick + 1);
            } else {
               
                String tmp2 = xor("0000", tmp);
                tmp = tmp2.substring(len - 3, len) + divident.substring(pick, pick + 1);
            }
           
            pick += 1;

        }

        
        if ((tmp.substring(0, 1)).equals("1")) {
            tmp = xor(divisor, tmp);
        } else {
            tmp = xor("0000", tmp);
        }

        String checkword = tmp;
        return checkword.substring((checkword.length() - 3), checkword.length());

    }

    public ArrayList<String> encodeData(String divi, String divisor) {

             
        String divident = divi + "000";
        String remainder = mod2div(divisor, divident);

            String codeword = divi + remainder;
        System.out.println("Remainder : " + remainder);
        System.out.println("CodeWord : "
                + codeword);
        ArrayList<String> list_result = new ArrayList<>();
        list_result.add(remainder);
         list_result.add(codeword);
        return  list_result;
    }

   
}
