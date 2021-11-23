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
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+1"));//set your current timezone
        Scanner scanner = new Scanner(System.in);
        while(true) {
            try {
                System.out.print("Path to directory: ");
                String pathToFile = scanner.nextLine();
                System.out.println("\n");
                List<File> filesInFolder = Files.walk(Paths.get(pathToFile))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                for (File files : filesInFolder) {
                    try {
                        Metadata metadata = ImageMetadataReader.readMetadata(files);
                        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                        Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                        if (date != null) {
                            System.out.println(files.getName() + " ---> " + sdf.format(date));
                            Path fileToMovePath = Paths.get(String.valueOf(files));
                            Path targetPath = Paths.get(pathToFile + "/" + sdf.format(date) + ".jpg");
                            Files.move(fileToMovePath, targetPath);
                            System.out.println("Name was sucessfully changed.\n");
                        } else {
                            System.out.println("Cannot access creation date: " + files.getName());
                            System.out.println("Name was not changed.\n");
                        }
                    }catch (NullPointerException e){
                        System.out.println("Error: " + files.getName());
                        System.out.println("Name was not changed.\n");
                    } catch (ImageProcessingException e){
                        System.out.println("File format could not be determined.");
                    }catch (FileAlreadyExistsException e){
                        System.out.println("File: "+files.getName()+" already exist.");
                        System.out.println("Name was not changed.\n");
                    }
                }
                break;
            } catch (NoSuchFileException e) {
                System.out.println("Wrong path.");
            }
        }
    }
}
