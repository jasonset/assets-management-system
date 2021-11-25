package com.gdn.warehouse.assetsmanagement.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = DocumentSequence.DOCUMENT_SEQUENCES_COLLECTIONS)
public class DocumentSequence {
   public static final String DOCUMENT_SEQUENCES_COLLECTIONS = "document_sequences";

   @Id
   @JsonSerialize(using = ToStringSerializer.class)
   private ObjectId id;
   private DocumentType documentType;
   private AssetCategory category;
   private String year;
   private String month;
   private Long sequence;
   @Version
   private Long version;
}
