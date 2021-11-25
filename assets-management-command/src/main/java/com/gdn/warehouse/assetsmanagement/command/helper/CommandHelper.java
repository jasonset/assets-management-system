package com.gdn.warehouse.assetsmanagement.command.helper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface CommandHelper {
   default <T> Mono<T> mono(Supplier<T> supplier) {
      return Mono.fromSupplier(supplier);
   }

   default <T> Flux<T> flux(Supplier<Stream<T>> supplier) {
      return Flux.from((s) -> {
         Flux.fromStream((Stream)supplier.get());
      });
   }
}
