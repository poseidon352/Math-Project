package Exceptions;


public class UnknownSymbolException extends Exception {
      public UnknownSymbolException() {}

      public UnknownSymbolException(String message) {
         super(message);
      }
 }