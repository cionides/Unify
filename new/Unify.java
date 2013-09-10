
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cionides
 *
 * >>javac Unify.java >>java Unify
 */
public class Unify {

    private static int pastSize = 0;
    private static List<String> bound = new ArrayList<String>();
    private static List<List<String>> subst = new ArrayList<List<String>>();

    public static void main(String[] args) {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        /*loop will continue until user enters 'quit' */
        while (true) {
            List<String> lit1 = new ArrayList<String>();
            List<String> lit2 = new ArrayList<String>();
            
            try {
                System.out.println("Please enter the first literal in the following format inst ?x human or 'quit': ");
                String input1 = br.readLine();
                if (input1.equals("quit")) {                   
                    break;
                }
                lit1 = addLiteral(input1);

                System.out.println("Please enter the second literal in the following format inst ?x human or 'quit' : ");
                String input2 = br.readLine();
                if (input2.equals("quit")) {                   
                    break;
                }
                lit2 = addLiteral(input2);

            } catch (IOException e) {
                System.err.print(e);
            }
            
            unify(lit1, lit2);
            printSubList();
            bound.clear();
            subst.clear();
        }
    }

    /*this method will generate the substitutions for the two literals passed in, or return false*/
    public static void unify(List<String> x, List<String> y) {
        //if the length of the literals do not match
        if (x.size() != y.size()) {
            List<String> invalid = new ArrayList<String>();
            invalid.add("invlaid entries");
            subst.add(invalid);          
        }

        //setting term1 and term2 to the first elements in the list passed in
        String term1 = x.get(0);
        String term2 = y.get(0);

        //if term1 is a variable
        if (term1.contains("?")) {
            //check to see if [term1, term2] is a substitution that is compatible with the substitutions list          
            List<String> substList = isCompatible(term1, term2);
            if (substList.contains("False")) {
                subst.add(substList);           
            } else {
                subst.add(substList);
                x.remove(0);
                y.remove(0);
                if (x.isEmpty()) {                  
                } else {
                    unify(x, y);
                }            
            }
        }
        //if term2 is a variable
        if (term2.contains("?")) {
            //check to see if [term2, term1] is a substitution that is compatible with subst
            List<String> substList = isCompatible(term1, term2);
            if (substList.contains("False")) {
                subst.add(substList);              
            } else {
                subst.add(substList);
                x.remove(0);
                y.remove(0);
                if (x.isEmpty()) {                   
                } else {
                    unify(x, y);
                }
            }

        } //otherwise they are both constants         
        else {
            //see if they are the same constant
            if (term1.equals(term2)) {
                x.remove(0);
                y.remove(0);
                if (x.isEmpty()) {                   
                } else {
                    unify(x, y);
                }
            } else {
                List<String> f = new ArrayList<String>();
                f.add("False");
                subst.add(f);               
            }
        }
        
    }
    
    /*This method returns a list containing the elements of the literal passed in*/
    public static List<String> addLiteral(String l) {
        List<String> lit = new ArrayList<String>();
        String[] tokens = l.split(" ");

        for(int i = 0; i < tokens.length; i++) {
            lit.add(tokens[i]);
        }

        return lit;
    }
    
    /*This method checks to see if t1 can be substituted by t2, and returns a subst list or false*/
    public static List<String> isCompatible(String t1, String t2) {
        List<String> substList = new ArrayList<String>();
        //check to see if t1 has been bound before
        for (List<String> l : subst) {
            for (String s : l) {
                if (s.contains(t1)) {
                    //if t1 was bound to a var then you can bind t2 to that var
                    String tokens[] = s.split("/");
                    String term1 = tokens[0];
                    String term2 = tokens[1];
                    
                    if (t1.equals(term1) && !bound.contains(t1)) {
                        if (term2.contains("?")) {
                            //AND t2 is a constant
                            if (!t2.contains("?")) {
                                //tokens[1] is now bound
                                bound.add(term2);
                                substList.add(term2 + "/" + t2);
                                return substList;
                            }
                            substList.add(term2 + "/" + t2);
                            return substList;
                        } else {
                            substList.add("False");
                            return substList;
                        }
                    }
                    if (t1.equals(term2) && !bound.contains(t1)) {
                        if (term1.contains("?")) {
                            //t2 is a constant
                            if (!t2.contains("?")) {
                                //tokens[1] is now bound
                                bound.add(term2);
                                substList.add(term2 + "/" + t2);
                                return substList;
                            }
                            substList.add(term2 + "/" + t2);
                            return substList;
                        } else {
                            substList.add("False");
                            return substList;
                        }
                    }
                    substList.add("False");
                    return substList;
                }
            }
        }

        substList.add(t1 + "/" + t2);
        return substList;
    }

    /*This method checks the subst for False statements, and prints the subst*/
    public static void printSubList() {
        /*
        boolean containsBad = false;
        for (int i = 0; i < subst.size(); i++) {
            if (subst.get(i).contains("False")) {
                //System.out.println("False");
                containsBad = true;
            }

        }
        if (containsBad == true) {
            System.out.println("False");
        } else {
            System.out.println(subst);
        }
 */
        System.out.println(subst);
    }
}
