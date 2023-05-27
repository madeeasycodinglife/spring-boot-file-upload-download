package com.madeeasy.model;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileUploadResponse {

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private Long size;

}
