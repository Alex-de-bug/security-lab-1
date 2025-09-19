package com.example.securityapi.util;

import org.owasp.encoder.Encode;

public final class Sanitizer {

  private Sanitizer() {
  }

  public static String forHtml(String input) {
    if (input == null) {
      return null;
    }
    return Encode.forHtml(input);
  }

  public static String forHtmlAttribute(String input) {
    if (input == null) {
      return null;
    }
    return Encode.forHtmlAttribute(input);
  }

  public static String forJavaScript(String input) {
    if (input == null) {
      return null;
    }
    return Encode.forJavaScript(input);
  }

  public static String forCssString(String input) {
    if (input == null) {
      return null;
    }
    return Encode.forCssString(input);
  }

  public static String forUriComponent(String input) {
    if (input == null) {
      return null;
    }
    return Encode.forUriComponent(input);
  }
}
