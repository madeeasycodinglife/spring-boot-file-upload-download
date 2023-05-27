package com.madeeasy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "files")
@Builder
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    /**
     TINYBLOB: maximum length of 255 bytes
     BLOB: maximum length of 65,535 bytes
     MEDIUMBLOB: maximum length of 16,777,215 bytes
     LONGBLOB: maximum length of 4,294,967,295 bytes

     you have to manually change data type in mysql workbench
     to longblob then it will be fine else will not save bigger MB
     */
    @Lob
    @Column(length = 1048576)
    private byte[] fileContent;

    public Files(String name, String type, byte[] fileContent) {
        this.name = name;
        this.type = type;
        this.fileContent = fileContent;
    }
}