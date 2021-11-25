package com.gdn.warehouse.assetsmanagement.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = TemplateDocument.TEMPLATE_DOCUMENTS_COLLECTIONRS)
public class TemplateDocument {
   public static final String TEMPLATE_DOCUMENTS_COLLECTIONRS = "template_documents";

   @Id
   @JsonSerialize(using = ToStringSerializer.class)
   protected ObjectId id;
   private DocumentType documentType;
   private String template;
}
