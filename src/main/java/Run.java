import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Run {
    public static void main(String[] args) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Scanner scanner = new Scanner(System.in);
        while(true) {
            try {
                System.out.print("Directory path: ");
                String pathToFile = scanner.nextLine();
                List<File> filesInFolder = Files.walk(Paths.get(pathToFile))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                for (File files : filesInFolder) {
                    Metadata metadata = ImageMetadataReader.readMetadata(files);
                    ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                    Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                    System.out.println(files.getName()+" ---> "+sdf.format(date));
                    Path fileToMovePath = Paths.get(String.valueOf(files));
                    Path targetPath = Paths.get(pathToFile + "/" + sdf.format(date) + ".jpg");
                    Files.move(fileToMovePath, targetPath);
                    System.out.println("Name was succesfully changed.");
                }
                break;
            } catch (NoSuchFileException e) {
                System.out.println("Wrong path.");
            } catch (ImageProcessingException e){
                e.printStackTrace();
            }
        }
    }
}
