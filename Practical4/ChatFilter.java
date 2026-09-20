public class ChatFilter{
    public static String filter (String [] logs, String keyword){
        int count = 0;
        StringBuilder report = new StringBuilder(); 
        for (String log : logs){
            String [] parts = log.split(" ", 3);

            if (parts.length < 3){
                continue;
            }
            String time = parts[0];
            String name = parts[1];
            String message = parts[2];

            if (message.toLowerCase().contains(keyword.toLowerCase())){
                count ++;

                report.append(time).append(" ").append(name).append(": ").append(message).append("\n");
            }
        }
        return "Matches: " + count + "\n" + report;
    }
}