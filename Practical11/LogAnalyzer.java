import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LogAnalyzer {
    public static void main (String [] args){
        String folder = "logs";
        String keyword = "ERROR";

        try {
            Path folderPath = Paths.get(folder);

            DirectoryStream<Path> files = Files.newDirectoryStream(folderPath,"*.txt");
            int totalLines = 0;
            int keywordLines = 0;

            for (Path file : files){
                int fileLines = 0;
                int fileKeywordLines = 0;

                try (BufferedReader reader = Files.newBufferedReader(file)){
                    String line;
                    while((line = reader.readLine()) != null){
                        totalLines ++;
                        fileLines ++;

                        if (line.contains(keyword)){
                            keywordLines ++;
                            fileKeywordLines ++;
                        }
                    }
                    long size = Files.size(file);

                    System.out.println("File: " + file.getFileName());
                    System.out.println("size: " + size + "bytes");
                    System.out.println("Lines: " + fileLines);
                    System.out.println("Lines containing: "+ keyword + ":" + fileKeywordLines);
                    System.out.println();
                } 
                System.out.println("Total lines: " + totalLines);
                System.out.println("Lines containing " + keyword + ": " + keywordLines);

            }
        }catch (IOException e) {
                    e.printStackTrace();
        }
    }
}