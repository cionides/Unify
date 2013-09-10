
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cionides 
 * 
 * >>javac Unify.java
 * >>java Unify
 */
public class Unify {
    /*a List of lists, where the inner lists are the substituitons*/

    private static List<List<String>> subst = new ArrayList<List<String>>();
    /*helps keep track of the difference in subst size each iteration*/
    private static int pastSize = 0;
    /*hold the literals passed in*/
    private static List<String> lit1 = new ArrayList<String>();
    private static List<String> lit2 = new ArrayList<String>();
    /*contains all the bound variables, so that they may not be used again*/
    private static List<String> bound = new ArrayList<String>();

    public static void main(String[] args) {
        /*To read user input*/
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        /*loop will continue until user enters 'quit' */
        while (true) {
            try {
                System.out.println("Please enter the first literal in the following format inst ?x human or 'quit': ");
                String literal1 = br.readLine();
                if (literal1.equals("quit")) {
                    System.out.println("The full list of substituitons");
                    System.out.println(subst);
                    break;
                }
                lit1 = addLiteral(literal1);

                System.out.println("Please enter the second literal in the following format inst ?x human or 'quit' : ");
                String literal2 = br.readLine();
                if (literal2.equals("quit")) {
                    System.out.println("The full list of substituitons");
                    System.out.println(subst);
                    break;
                }
                lit2 = addLiteral(literal2);

            } catch (IOException e) {
                System.err.print(e);
            }
            /*this method will generate the substitutions for the two literals passed in, or return false*/
            unify(lit1, lit2);

            /*
             * The most recently added substitution sets are checked for "false" 
             * and the substitution list is printed in full
             */
            printSubList();
        }
    }

    public static void unify(List<String> x, List<String> y) {
        //if the length of the literals do not match
        if (x.size() != y.size()) {
            return;
        }

        //setting term1 and term2 to the first elements in the list passed in
        String term1 = x.get(0);
        String term2 = y.get(0);

        //if term1 is a variable
        if (term1.contains("?")) {
            //check to see if [term1, term2] is a substitution that is compatible with subst
            List<String> substList = isCompatible(term1, term2);  
            if (substList.contains("False")) {
                subst.add(substList);
                return;
            } else {
                subst.add(substList);
                x.remove(0);
                y.remove(0);
                if (x.isEmpty()) {
                    return;
                } else {
                    unify(x, y);
                }
                return;
            }
        }
        //if term2 is a variable
        if (term2.contains("?")) {
            //check to see if [term2, term1] is a substitution that is compatible with subst
            List<String> subList = isCompatible(term2, term1);
            if (subList.contains("False")) {
                subst.add(subList);
                return;
            } else {
                subst.add(subList);
                x.remove(0);
                y.remove(0);
                if (x.isEmpty()) {
                    return;
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
                    return;
                } else {

                    unify(x, y);
                }
            } else {
                List<String> f = new ArrayList<String>();
                f.add("False");
                subst.add(f);
                return;
            }
        }
    }
/*This method returns a list containing the elements of the literal passed in*/
    public static List<String> addLiteral(String l) {
        List<String> lit = new ArrayList<String>();
        String[] tokens = l.split(" ");

        for (int i = 0; i < tokens.length; i++) {
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
                    if (t1.equals(tokens[0]) && !bound.contains(t1)) {
                        if (tokens[1].contains("?")) {
                            //t2 is a constant
                            if (!t2.contains("?")) {
                                //tokens[1] is now bound
                                bound.add(tokens[1]);
                                substList.add(tokens[1] + "/" + t2);
                                return substList;
                            }
                            substList.add(tokens[1] + "/" + t2);
                            return substList;
                        } else {
                            substList.add("False");
                            return substList;
                        }
                    }
                    if (t1.equals(tokens[1]) && !bound.contains(t1)) {
                        if (tokens[0].contains("?")) {
                            //t2 is a constant
                            if (!t2.contains("?")) {
                                //tokens[1] is now bound
                                bound.add(tokens[1]);
                                substList.add(tokens[1] + "/" + t2);
                                return substList;
                            }
                            substList.add(tokens[1] + "/" + t2);
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

        substList.add(t1
                + "/" + t2);
        return substList;
    }
/*This method checks the subst for False statements, and prints the subst*/
    public static void printSubList() {

        int currentSize = subst.size();
        int lastIndex = subst.size() - 1;
        int nextTo = subst.size() - 2;

        int diff = currentSize - pastSize;

        if (subst.isEmpty()) {
            return;
        } else {
            if (diff == 0) {
                System.out.println(subst);
                pastSize = subst.size();
            }
            if (diff == 1) {
                if (subst.get(lastIndex).contains("False")) {
                    System.out.println("False");
                    subst.remove(lastIndex);
                    pastSize = subst.size();
                } else {
                    System.out.println(subst);
                    pastSize = subst.size();
                }

            }
            if (diff == 2) {
                if (subst.get(lastIndex).contains("False") || subst.get(nextTo).contains("False")) {
                    System.out.println("False");
                    subst.remove(lastIndex);
                    subst.remove(nextTo);
                    pastSize = subst.size();
                } else {
                    System.out.println(subst);
                    pastSize = subst.size();
                }
            }
        }
    }
}
