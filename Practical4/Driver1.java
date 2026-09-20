public class Driver1 {
    public static void main(String[] args) {
        // Test passwords from the problem statement + a couple extras
        String[] testPasswords = {
            "abc",          // Should be Weak
            "Abcd1234!",    // Should be Strong
            "password123",  // Length + Digit (Medium)
            //"Hello!"        // Uppercase + Special (Medium)
        };

        for (String pw : testPasswords) {
            System.out.println("Testing Password: \"" + pw + "\"");
            
            // Print individual rule checks
            System.out.println(" - Length >= 8: " + PasswordChecker.hasLength(pw));
            System.out.println(" - Uppercase:   " + PasswordChecker.hasUppercase(pw));
            System.out.println(" - Digit:       " + PasswordChecker.hasDigit(pw));
            System.out.println(" - Special:     " + PasswordChecker.hasSpecial(pw));
            
            // Print overall strength label
            System.out.println(" -> Final Label: " + PasswordChecker.strength(pw));
            System.out.println("------------------------------------");
        }
    }
}