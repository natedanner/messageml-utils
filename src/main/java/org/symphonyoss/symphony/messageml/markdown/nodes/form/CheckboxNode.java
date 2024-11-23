package org.symphonyoss.symphony.messageml.markdown.nodes.form;

import org.apache.commons.lang3.StringUtils;

/**
 * Class that Represents a Markdown Node for the "Checkbox" form element.
 *
 * @author Lucas Macedo
 * @since 10/21/2019
 */
public class CheckboxNode extends FormElementNode {
  private static final String MARKDOWN = "Checkbox";
  private static final String CHECKBOX_DELIMITER = " ";

  private String label;
  
  public CheckboxNode() {
    super(MARKDOWN);
  }

  public CheckboxNode(String label) {
    super(MARKDOWN);
    this.label = label;
  }

  @Override
  public String getOpeningDelimiter() {
    return CHECKBOX_DELIMITER;
  }

  @Override
  public String getClosingDelimiter() {
    return CHECKBOX_DELIMITER;
  }

  @Override
  public String getText() {
    return StringUtils.defaultIfBlank(label, StringUtils.EMPTY);
  }
}
