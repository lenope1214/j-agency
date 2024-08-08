package kr.co.jsol.jagency.reader.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 태그 읽은 결과 서버로 전송 데이터
 */
@Schema(description = "검사실 태깅")
public class TagDto {
    @JsonProperty("tagNo")
    @Schema(description = "태그 번호")
    private String tagNo;

    @JsonProperty("roomId")
    @Schema(description = "검사실 아이디")
    private String roomId;

    public TagDto() {
    }

    public TagDto(String tagNo, String roomId) {
        this.tagNo = tagNo;
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTagNo() {
        return tagNo;
    }

    public void setTagNo(String tagNo) {
        this.tagNo = tagNo;
    }
}
