package Exceptions;


public class InputTypoException extends Exception {
      public InputTypoException() {}

      public InputTypoException(String message) {
         super(message);
      }
 }