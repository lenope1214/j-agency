package kr.co.jsol.jagency.file.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class File {
    @Column(name = "file_original_name")
    @Comment("원본 파일명")
    private String originalName;

    @Column(name = "file_name")
    @Comment("저장된 파일명, 기본은 random UUID")
    private String name;

    @Column(name = "file_extension")
    @Comment("확장자")
    private String extension;

    @Column(name = "download_url")
    @Comment("다운로드 URL")
    private String downloadUrl;

    @Column(name = "file_size")
    @Comment("파일 크기")
    private Long size;
}
