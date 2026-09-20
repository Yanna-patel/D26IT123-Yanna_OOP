public class PasswordChecker {
    public static boolean hasLength (String pw) {
        return pw.length() >= 8;
    }

    public static boolean hasUppercase(String pw) {
        return pw.matches(".*[A-Z].*");
    }

    public static boolean hasDigit (String pw){
        return pw.matches(".*[0-9].*");
    }

    public static boolean hasSpecial (String pw){
        return pw.matches (".*[^a-zA-Z0-9].*");
    }

    public static String strength (String pw) {
        int count = 0;

        if (hasLength(pw)) count++;
        if (hasUppercase(pw)) count++;
        if (hasDigit(pw)) count++;
        if (hasSpecial(pw)) count++;

        switch (count) {
            case 4:
                return "Very Strong";
            case 3:
                return "Strong";
            case 2:
                return "Medium";
            default:
                return "Weak";
        }
    }

}