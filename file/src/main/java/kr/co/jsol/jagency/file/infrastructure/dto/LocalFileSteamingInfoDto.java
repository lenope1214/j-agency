package kr.co.jsol.jagency.file.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;

@Getter
@Setter
public class LocalFileSteamingInfoDto {
    private String path;
    private FileSystemResource resource;
    private ResourceRegion region;

    public LocalFileSteamingInfoDto(String path, FileSystemResource resource, ResourceRegion region) {
        this.path = path;
        this.resource = resource;
        this.region = region;
    }
}
