package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.CreateItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateItemCommandImplTest {
   @InjectMocks
   private CreateItemCommandImpl command;

   @Mock
   private ItemRepository itemRepository;

   @Mock
   private GenerateSequenceHelper generateSequenceHelper;

   private CreateItemCommandRequest createItemCommandRequest;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      createItemCommandRequest = CreateItemCommandRequest.builder()
            .itemName("ITEM_NAME").username("username").category("MHE").build();
      item = Item.builder().itemCode("code").build();
   }

   @Test
   public void execute() {
      when(itemRepository.findByItemName(anyString())).thenReturn(Mono.empty());
      when(generateSequenceHelper.generateDocumentNumber(DocumentType.ITEM)).thenReturn(Mono.just("ITEM-NUMBER"));
      when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));
      command.execute(createItemCommandRequest).block();
      verify(itemRepository).findByItemName(anyString());
      verify(generateSequenceHelper).generateDocumentNumber(DocumentType.ITEM);
      verify(itemRepository).save(any(Item.class));
   }

   @Test(expected = CommandErrorException.class)
   public void execute_already_exist() {
      when(itemRepository.findByItemName(anyString())).thenReturn(Mono.just(item));
      when(generateSequenceHelper.generateDocumentNumber(DocumentType.ITEM)).thenReturn(Mono.just("ITEM-NUMBER"));
      when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));
      command.execute(createItemCommandRequest).block();
      verify(itemRepository).findByItemName(anyString());
      verify(generateSequenceHelper).generateDocumentNumber(DocumentType.ITEM);
      verify(itemRepository).save(any(Item.class));
   }
}