package org.symphonyoss.symphony.messageml.markdown.nodes.form;

import org.apache.commons.lang3.StringUtils;

import java.util.StringJoiner;
import java.util.stream.Stream;

public class DatePickerNode extends FormElementNode {
  private static final String MARKDOWN = "Date Picker";

  private final String label;
  private final String tooltip;
  private final String placeholder;

  public DatePickerNode(String label, String tooltip, String placeholder) {
    super(MARKDOWN, placeholder);
    this.label = label;
    this.tooltip = tooltip;
    this.placeholder = placeholder;
  }

  @Override
  public String getText() {
    return Stream.of(label, tooltip, placeholder)
        .filter(StringUtils::isNotEmpty)
        .collect(() -> new StringJoiner("][",":[", "]").setEmptyValue(""),
            StringJoiner::add,
            StringJoiner::merge)
        .toString();
  }
}
