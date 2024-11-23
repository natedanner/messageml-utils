package org.symphonyoss.symphony.messageml.util.instrument.resolver;



import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProviderId {
  EDI("edi"),
  MANUAL("manual");
  private final String value;

  ProviderId(String value) {
    this.value = value;
  }

  @JsonCreator
  public static ProviderId fromValue(String text) {
    for (ProviderId b : ProviderId.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
