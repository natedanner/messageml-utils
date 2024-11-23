package org.symphonyoss.symphony.messageml.markdown.nodes.form;


/**
 * Class that Represents a Markdown Node for the "RoomSelector" form element.
 *
 * @author Mohamed Rojbeni
 * @since 09/15/2023
 */
public class RoomSelectorNode extends FormElementNode implements PlaceholderLabelTooltipNode {
  private static final String MARKDOWN = "Room Selector";

  private final String placeholder;
  private final String label;
  private final String tooltip;

  public RoomSelectorNode(String placeholder, String label, String tooltip) {
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
