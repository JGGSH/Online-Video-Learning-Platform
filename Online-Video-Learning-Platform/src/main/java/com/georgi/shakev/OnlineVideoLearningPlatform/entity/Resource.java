package com.georgi.shakev.OnlineVideoLearningPlatform.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private LocalDateTime created = LocalDateTime.now();

    @ContentId
    private String contentId;
    @ContentLength
    private long contentLength;
    private String mimeType = "video/mp4";
}
