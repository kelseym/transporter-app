package org.nrg.transporter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Payload {

    private Long id;
    private String name;
    private String description;
    private List<FileManifest> fileManifests;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileManifest {
        private String name; // This can be a file name or a directory name
        private FileType fileType; // Enum to differentiate between FILE and DIRECTORY
        private String fileHash;  // Typically a hash like SHA-256 to verify file integrity (relevant for files)
        private Long fileSize;    // File size in bytes (relevant for files)
        private List<String> directoryContents; // List of contents if this is a directory

        public enum FileType {
            FILE,
            DIRECTORY
        }
    }


}
