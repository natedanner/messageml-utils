package org.symphonyoss.symphony.messageml.markdown.nodes.form;

public class TimePickerNode extends FormElementNode implements PlaceholderLabelTooltipNode {
  private static final String MARKDOWN = "Time Picker";

  private final String label;
  private final String tooltip;
  private final String placeholder;


  public TimePickerNode(String label, String tooltip, String placeholder) {
    super(MARKDOWN, placeholder);
    this.label = label;
    this.tooltip = tooltip;
    this.placeholder = placeholder;
  }

  @Override
  public String getText() {
    return generateMarkdownPlaceholderLabelAndTooltip(placeholder, label, tooltip);
  }
}
