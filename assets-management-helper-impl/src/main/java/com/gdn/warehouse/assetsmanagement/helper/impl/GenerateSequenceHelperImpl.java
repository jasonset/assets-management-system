package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.entity.DocumentSequence;
import com.gdn.warehouse.assetsmanagement.entity.TemplateDocument;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.Purchase;
import com.gdn.warehouse.assetsmanagement.enums.TemplateDocumentVariable;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.GenerateAssetNumberRequest;
import com.gdn.warehouse.assetsmanagement.repository.DocumentSequenceRepository;
import com.gdn.warehouse.assetsmanagement.repository.TemplateDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GenerateSequenceHelperImpl implements GenerateSequenceHelper {
   @Autowired
   private DocumentSequenceRepository documentSequenceRepository;

   @Autowired
   private TemplateDocumentRepository templateDocumentRepository;

   @Override
   public Mono<String> generateDocumentNumber(DocumentType documentType) {
      return generateDocumentNumberForAsset(documentType, null);
   }

   @Override
   public Mono<String> generateDocumentNumberForAsset(DocumentType documentType, GenerateAssetNumberRequest request) {
      return getDocumentTemplate(documentType)
            .flatMap(template -> generateDocumentSequence(template,documentType,request)
                  .map(documentSequence -> generateDocumentNumber(template,documentSequence,request)));
   }

   private Mono<String> getDocumentTemplate(DocumentType documentType) {
      return templateDocumentRepository.findByDocumentType(documentType)
            .map(TemplateDocument::getTemplate);
   }

   private Mono<DocumentSequence> generateDocumentSequence(String documentTemplate, DocumentType documentType,
                                                           GenerateAssetNumberRequest request) {
      String month = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
      String year;
      if(DocumentType.ITEM.equals(documentType)){
         year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
      }else {
         year = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
      }

      return getDocumentSequence(documentType,request)
            .map(documentSequence -> {
               boolean resetSequence = false;
               if (documentTemplate.contains(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.YEAR))
                     || documentTemplate.contains(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.MONTH))) {
                  if (documentTemplate.contains(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.YEAR))) {
                     resetSequence =
                           !ObjectUtils.isEmpty(documentSequence) && !documentSequence.getYear().equals(year);
                  }
                  if (documentTemplate
                        .contains(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.MONTH))) {
                     resetSequence =
                           !ObjectUtils.isEmpty(documentSequence) && !documentSequence.getMonth().equals(month);
                  }
               }
               if (resetSequence) {
                  documentSequence.setMonth(month);
                  documentSequence.setYear(year);
                  documentSequence.setSequence(1L);
               } else {
                  documentSequence.setSequence(documentSequence.getSequence() + 1);
               }
               return documentSequence;
            })
            .switchIfEmpty(Mono.defer(()-> {
               if(ObjectUtils.isEmpty(request)){
                  return constructDocumentSequence(documentType,year, month,null);
               }else {
                  return constructDocumentSequence(documentType,year, month,
                        AssetCategory.valueOf(request.getCategory()));
               }
            }))
            .flatMap(documentSequenceRepository::save);
   }

   private Mono<DocumentSequence> getDocumentSequence(DocumentType documentType, GenerateAssetNumberRequest request) {
      if(DocumentType.ASSET.equals(documentType)){
         return documentSequenceRepository.findByDocumentTypeAndCategory(documentType, AssetCategory.valueOf(request.getCategory()));
      }
      return documentSequenceRepository.findByDocumentType(documentType);
   }

   private String getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable documentVariable) {
      return "{" + documentVariable.name() + "}";
   }

   private Mono<DocumentSequence> constructDocumentSequence(DocumentType documentType, String year, String month,
                                                            AssetCategory category){
      DocumentSequence documentSequence =
            DocumentSequence.builder().documentType(documentType).year(year)
                  .month(month).sequence(1L).category(category).build();
      return Mono.fromSupplier(()->documentSequence);
   }

   private String generateDocumentNumber(String documentTemplate, DocumentSequence documentSequence,
                                         GenerateAssetNumberRequest request) {
      Tuple2<String[], String[]> tupleVariableAndReplacement =
            toTupleVariablesAndReplacementDocument(documentTemplate,documentSequence,request);
      return StringUtils.replaceEach(documentTemplate, tupleVariableAndReplacement.getT1(), tupleVariableAndReplacement.getT2());
   }

   private Tuple2<String[], String[]> toTupleVariablesAndReplacementDocument
         (String documentTemplate, DocumentSequence documentSequence, GenerateAssetNumberRequest request){
      List<String> documentVariables = new ArrayList<>();
      List<String> documentReplacement = new ArrayList<>();
      documentVariables.add(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.SEQUENCE));
      documentReplacement.add(String.format("%05d", documentSequence.getSequence()));

      if(documentTemplate.contains(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.YEAR))){
         documentVariables.add(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.YEAR));
         documentReplacement.add(String.valueOf(documentSequence.getYear()));
      }
      if(documentTemplate.contains(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.MONTH))){
         documentVariables.add(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.MONTH));
         documentReplacement.add(String.valueOf(documentSequence.getMonth()));
      }
      if(documentTemplate.contains(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.ORGANISATION))){
         documentVariables.add(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.ORGANISATION));
         documentReplacement.add(request.getOrganisation());
      }
      if(documentTemplate.contains(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.PURCHASE))){
         documentVariables.add(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.PURCHASE));
         if(Purchase.BUY.name().equals(request.getPurchase())){
            documentReplacement.add("B");
         }else {
            documentReplacement.add("R");
         }
      }
      if(documentTemplate.contains(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.CATEGORY))){
         documentVariables.add(getTemplateDocumentVariablePlaceholder(TemplateDocumentVariable.CATEGORY));
         documentReplacement.add(request.getCategory());
      }
      return Tuples.of(documentVariables.stream().toArray(String[]::new), documentReplacement.stream().toArray(String[]::new));
   }
}
