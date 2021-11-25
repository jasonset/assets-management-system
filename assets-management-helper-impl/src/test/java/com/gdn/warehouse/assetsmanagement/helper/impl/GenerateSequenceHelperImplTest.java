package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.entity.DocumentSequence;
import com.gdn.warehouse.assetsmanagement.entity.TemplateDocument;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.Organisation;
import com.gdn.warehouse.assetsmanagement.enums.Purchase;
import com.gdn.warehouse.assetsmanagement.enums.TemplateDocumentVariable;
import com.gdn.warehouse.assetsmanagement.helper.model.GenerateAssetNumberRequest;
import com.gdn.warehouse.assetsmanagement.repository.DocumentSequenceRepository;
import com.gdn.warehouse.assetsmanagement.repository.TemplateDocumentRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GenerateSequenceHelperImplTest {

   private static final String ASSET_TEMPLATE =
         "{"+TemplateDocumentVariable.ORGANISATION+"}/OPS/{"+TemplateDocumentVariable.PURCHASE+"}/{"+
               TemplateDocumentVariable.YEAR+"}{"+TemplateDocumentVariable.MONTH+"}/{"+
               TemplateDocumentVariable.CATEGORY+"}/{"+TemplateDocumentVariable.SEQUENCE+"}";

   private static final String MAINTENANCE_TEMPLATE =
         "MT/{"+TemplateDocumentVariable.YEAR+"}{"+TemplateDocumentVariable.MONTH+"}/{"+
               TemplateDocumentVariable.SEQUENCE+"}";

   @Mock
   private DocumentSequenceRepository documentSequenceRepository;

   @Mock
   private TemplateDocumentRepository templateDocumentRepository;

   @InjectMocks
   private GenerateSequenceHelperImpl generateSequenceHelperImpl;

   private DocumentSequence documentSequence;
   private TemplateDocument templateDocument;
   private GenerateAssetNumberRequest request;

   @Before
   public void setUp() throws Exception {
      String month = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
      String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
      generateSequenceHelperImpl = Mockito.mock(GenerateSequenceHelperImpl.class, Mockito.CALLS_REAL_METHODS);
      MockitoAnnotations.initMocks(this);
      request = GenerateAssetNumberRequest.builder()
            .category(AssetCategory.MHE.name()).organisation(Organisation.GDN.name())
            .purchase(Purchase.BUY.name()).build();
      initializeDocumentSequence(DocumentType.ASSET, month,year, 1);
      initializeTemplateDocument(DocumentType.ASSET);
   }

   private void initializeDocumentSequence(DocumentType documentType, String month, String year, long sequence){
      documentSequence = DocumentSequence.builder()
            .documentType(documentType)
            .month(month)
            .year(year)
            .sequence(sequence).build();
   }

   private void initializeTemplateDocument(DocumentType documentType){
      templateDocument = TemplateDocument.builder()
            .documentType(documentType).template(ASSET_TEMPLATE).build();
   }

   private void mockRepository(DocumentType documentType){
      when(templateDocumentRepository.findByDocumentType(documentType)).thenReturn(Mono.just(templateDocument));
      when(documentSequenceRepository.findByDocumentType(documentType)).thenReturn(Mono.just(documentSequence));
      when(documentSequenceRepository.findByDocumentTypeAndCategory(documentType,AssetCategory.MHE)).thenReturn(Mono.just(documentSequence));
   }

   @Test
   public void testGenerateDocumentNumberAsset_documentSequenceNotExist() throws Exception{
      mockRepository(DocumentType.ASSET);
      when(documentSequenceRepository.findByDocumentType(DocumentType.ASSET)).thenReturn(Mono.empty());
      when(documentSequenceRepository.save(any(DocumentSequence.class))).thenReturn(Mono.just(documentSequence));
      generateSequenceHelperImpl.generateDocumentNumberForAsset(DocumentType.ASSET,request).block();
   }

   @Test
   public void testGenerateDocumentNumberAsset() throws Exception{
      request.setPurchase(Purchase.RENT.name());
      mockRepository(DocumentType.ASSET);
      when(documentSequenceRepository.save(any(DocumentSequence.class))).thenReturn(Mono.just(documentSequence));
      generateSequenceHelperImpl.generateDocumentNumberForAsset(DocumentType.ASSET,request).block();
   }

   @Test
   public void testGenerateDocumentNumberAsset_resetSequence() throws Exception{
      initializeDocumentSequence(DocumentType.ASSET,"13","100",2);
      mockRepository(DocumentType.ASSET);
      when(documentSequenceRepository.save(any(DocumentSequence.class))).thenReturn(Mono.just(documentSequence));
      generateSequenceHelperImpl.generateDocumentNumberForAsset(DocumentType.ASSET,request).block();
   }

   @Test
   public void testGenerateDocumentNumber() throws Exception{
      templateDocument.setTemplate(MAINTENANCE_TEMPLATE);
      mockRepository(DocumentType.MAINTENANCE);
      when(documentSequenceRepository.save(any(DocumentSequence.class))).thenReturn(Mono.just(documentSequence));
      generateSequenceHelperImpl.generateDocumentNumber(DocumentType.MAINTENANCE).block();
   }

   @Test
   public void testGenerateDocumentNumber_empty() throws Exception{
      initializeDocumentSequence(DocumentType.ASSET,"13","100",2);
      when(templateDocumentRepository.findByDocumentType(DocumentType.ASSET)).thenReturn(Mono.just(templateDocument));
      when(documentSequenceRepository.findByDocumentTypeAndCategory(any(DocumentType.class),any(AssetCategory.class)))
            .thenReturn(Mono.empty());
      when(documentSequenceRepository.save(any(DocumentSequence.class))).thenReturn(Mono.just(documentSequence));
      generateSequenceHelperImpl.generateDocumentNumberForAsset(DocumentType.ASSET,request).block();
   }
}