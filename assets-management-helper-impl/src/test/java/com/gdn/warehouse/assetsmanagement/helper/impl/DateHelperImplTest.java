package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Date;


public class DateHelperImplTest {

   @InjectMocks
   private DateHelperImpl dateHelper;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void validateScheduledDate_success() {
      dateHelper.validateScheduledDate(Long.MAX_VALUE).block();
   }

   @Test(expected = CommandErrorException.class)
   public void validateScheduledDate_fail() {
      dateHelper.validateScheduledDate(Long.MIN_VALUE).block();
   }

   @Test
   public void convertDateForEmail() {
      dateHelper.convertDateForEmail(new Date());
   }
}