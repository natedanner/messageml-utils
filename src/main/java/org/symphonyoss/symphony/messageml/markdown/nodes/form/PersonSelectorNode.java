package org.symphonyoss.symphony.messageml.markdown.nodes.form;

/**
 * Class that Represents a Markdown Node for the "PersonSelector" form element.
 *
 * @author Lucas Macedo
 * @since 10/21/2019
 */
public class PersonSelectorNode extends FormElementNode implements PlaceholderLabelTooltipNode {
  private static final String MARKDOWN = "Person Selector";

  private final String placeholder;
  private final String label;
  private final String tooltip;

  public PersonSelectorNode(String placeholder, String label, String tooltip) {
    super(MARKDOWN, placeholder);
    this.placeholder = placeholder;
    this.label = label;
    this.tooltip = tooltip;
  }
  
  @Override
  public String getText() {
    return generateMarkdownPlaceholderLabelAndTooltip(placeholder, label, tooltip);
  }
}
