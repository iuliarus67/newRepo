import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String REPLACE_WITH_DIGIT = "Replace with a digit";
    private static final String REPLACE_REPETITIVE = "Fix a repetitive sequence by replacing the 3rd element accordingly";
    private static final String REPLACE_WITH_UPPERCASE = "Replace with an uppercase letter";
    private static final String REPLACE_WITH_LOWERCASE = "Replace with a lowercase letter";

    public static boolean testPasswordLength(String password) {
        return password.length() >= 6 && password.length() <= 20;
    }


    public static int returnChanges(List<Integer> lowercasePositions, List<Integer> uppercasePositions, List<Integer> digitPositions, List<Integer> repeatingPositions, String password) {
        int changes = 0;
        StringBuffer copy = new StringBuffer(password);

        //If the password is longer than the upper limit, we would have to delete those extra characters such that, if possible, we would not cause
        // any more weak spots( delete the only upper char, lower char or digit or create 3 consecutive occurrences)
        int cont = 0;
        int size = copy.length();
        boolean entered = false;
        //at first, we see if we can delete repeating sequence issues
        while (size > 20 && !repeatingPositions.isEmpty()) {
            size--;
            cont++;
            entered = true;
        }
        changes += cont;

        for (int i = cont - 1; i >= 0; i--) {
            copy = copy.deleteCharAt(repeatingPositions.get(i));
        }

        //If we repeated any sequence we now have to recompute their positions in the new array
        if(entered) {
            repeatingPositions = new ArrayList<>();
            Matcher repeating = Pattern.compile("(.)\\1{2}").matcher(copy);
            while (repeating.find()) {
                repeatingPositions.add(repeating.start());
            }
        }

        changes += Math.max(copy.length() - 20, 0);

        //dealing with 3 consecutive chars first because we may simply replace the 3rd occurrence each time and replace it with something
        // the password lacks. In this way, we avoid inserting something at the end or looking again through the array, what we need to add
        // if we have enough repeating occurrences
        for (int i = 0; i < repeatingPositions.size(); i++) {
            if (lowercasePositions.isEmpty()) {
                // if we have no lowercase letters, but we have a repeating recurrence we change the end element of the repeating sequence
                changes = modifyList(copy, changes, i, 'a', lowercasePositions, repeatingPositions);
                System.out.println(REPLACE_WITH_LOWERCASE);
                continue;
            }
            if (uppercasePositions.isEmpty()) {
                // if we have no uppercase letters, but we have a repeating recurrence we change the end element of the repeating sequence
                changes = modifyList(copy, changes, i, 'A', uppercasePositions, repeatingPositions);
                System.out.println(REPLACE_WITH_UPPERCASE);
                continue;
            }
            if (digitPositions.isEmpty()) {
                // if we have no digits, but we have a repeating recurrence we change the end element of the repeating sequence
                changes = modifyList(copy, changes, i, '3', digitPositions, repeatingPositions);
                System.out.println(REPLACE_WITH_DIGIT);
                continue;
            }
            //if we lacked nothing, but we still have a repeating sequence we would just change the 3rd apparition in such a way that we avoid
            //creating another repeating sequence

            System.out.println(REPLACE_REPETITIVE);
            changes++;
        }

        //If by adding we can solve the missing of other necessary tokens, we add them not some random value
        if (copy.length() <= 6) {
            changes += 6 - copy.length();
            for (int i = 0; i < 6 - copy.length(); i++) {
                if (lowercasePositions.isEmpty()) {
                    lowercasePositions.add(0);
                    continue;
                }
                if (uppercasePositions.isEmpty()) {
                    uppercasePositions.add(0);
                    continue;
                }
                if (digitPositions.isEmpty()) {
                    digitPositions.add(0);
                    continue;
                }
            }
            //Add whatever will not create other problems
            System.out.println("Add " + (6 - copy.length()) + " to get to minimum length");
        }

        //if after solving the repetition and length issues we still lack something, we  just append the missing item or replace what we can with what we need
        if (lowercasePositions.isEmpty()) {
            changes++;
        }

        if (uppercasePositions.isEmpty()) {
            changes++;
        }

        if (digitPositions.isEmpty()) {
            changes++;
        }


        return changes;
    }

    private static int modifyList(StringBuffer copy, int changes, int i, char corresponding, List<Integer> positionList, List<Integer> repeatingPositionList) {

        copy.setCharAt(repeatingPositionList.get(i) + 2, corresponding);
        changes++;
        positionList.add(i);
        return changes;
    }

    public static int minimumMoves(String password) {

        List<Integer> repeatingPos = new ArrayList<>();
        List<Integer> lowerLetters = new ArrayList<>();
        List<Integer> upperLetters = new ArrayList<>();
        List<Integer> digits = new ArrayList<>();

        Matcher lowerCaseLetter = Pattern.compile("[a-z]").matcher(password);
        Matcher upperCaseLetter = Pattern.compile("[A-Z]").matcher(password);
        Matcher digit = Pattern.compile("(\\d)").matcher(password);
        Matcher repeating = Pattern.compile("(.)\\1{2}").matcher(password);

        while (lowerCaseLetter.find()) {
            lowerLetters.add(lowerCaseLetter.start());
        }
        while (upperCaseLetter.find()) {
            upperLetters.add(upperCaseLetter.start());
        }
        while (digit.find()) {
            digits.add(digit.start());
        }
        while (repeating.find()) {
            repeatingPos.add(repeating.start());
        }
        if (repeatingPos.isEmpty() && !upperLetters.isEmpty() && !lowerLetters.isEmpty() && testPasswordLength(password)) {
            return 0;
        } else {
            return returnChanges(lowerLetters, upperLetters, digits, repeatingPos, password);
        }
    }

    public static void main(String[] args) {

        System.out.println(minimumMoves("aaab1"));
    }
}
