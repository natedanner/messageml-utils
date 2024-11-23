package org.symphonyoss.symphony.messageml.markdown.nodes.form;

public class OptionNode extends FormElementNode {
  private static final String LEFT_OPTION_DELIMITER = "-";
  private static final String RIGHT_OPTION_DELIMITER = "";

  public OptionNode() {
    super();
  }

  @Override
  public String getOpeningDelimiter() {
    return LEFT_OPTION_DELIMITER;
  }

  @Override
  public String getClosingDelimiter() {
    return RIGHT_OPTION_DELIMITER + "\n";
  }
}
