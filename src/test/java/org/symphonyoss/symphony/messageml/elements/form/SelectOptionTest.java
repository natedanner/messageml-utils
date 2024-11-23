package org.symphonyoss.symphony.messageml.elements.form;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.symphonyoss.symphony.messageml.markdown.MarkdownRenderer.addEscapeCharacter;

import org.junit.Test;
import org.symphonyoss.symphony.messageml.MessageMLContext;
import org.symphonyoss.symphony.messageml.bi.BiFields;
import org.symphonyoss.symphony.messageml.bi.BiItem;
import org.symphonyoss.symphony.messageml.elements.Element;
import org.symphonyoss.symphony.messageml.elements.ElementTest;
import org.symphonyoss.symphony.messageml.elements.MessageML;
import org.symphonyoss.symphony.messageml.elements.Option;
import org.symphonyoss.symphony.messageml.elements.Select;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.exceptions.ProcessingException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectOptionTest extends ElementTest {

  private static final String NAME_ATTR = "name";
  private static final String REQUIRED_ATTR = "required";
  private static final String DATA_PLACEHOLDER_ATTR = "data-placeholder";
  private static final String VALUE_ATTR = "value";
  private static final String SELECTED_ATTR = "selected";
  private static final String FORM_ID_ATTR = "text-field-form";
  private static final String LABEL_ATTR = "label";
  private static final String TITLE_ATTR = "title";

  @Test
  public void testCompleteRequiredSelect() throws Exception {
    String name = "complete-required-id";
    boolean required = true;
    String placeholder = "placeholder-here";
    String input =
        "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select data-placeholder=\"" + placeholder + "\" name=\"" + name
            + "\" required=\"" + required +
            "\"><option value=\"\">Option 1</option></select>" + ACTION_BTN_ELEMENT + "</form></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element form = messageML.getChildren().get(0);
    Element select = form.getChildren().get(0);

    assertEquals("Select class", Select.class, select.getClass());
    verifySelectPresentation((Select) select, name, true, required, placeholder, false, false);
  }

  @Test
  public void testCompleteRequiredSelectWithLabelAndTooltip() throws Exception {
    String name = "complete-required-id";
    String label = "label";
    String title = "tooltip";
    boolean required = true;
    String placeholder = "placeholder-here";
    String input =
        "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select data-placeholder=\"" + placeholder + "\" name=\"" + name
            + "\" required=\"" + required + "\"" +
            " label=\"" + label + "\" title=\"" + title +
            "\"><option value=\"\">Option 1</option></select>" + ACTION_BTN_ELEMENT + "</form></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element form = messageML.getChildren().get(0);
    Element select = form.getChildren().get(0);

    assertEquals("Select class", Select.class, select.getClass());
    verifySelectPresentation((Select) select, name, true, required, placeholder, true, true);
  }

  @Test
  public void testSelectWithUnderscore() throws Exception {
    String name = "complete-required-id";
    String label = "label-here";
    String title = "tooltip-here";
    boolean required = true;
    String placeholder = "placeholder-here";
    String input =
        "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select data-placeholder=\"" + placeholder + "\" name=\"" + name
            + "\" required=\"" + required + "\"" +
            " label=\"" + label + "\" title=\"" + title +
            "\"><option value=\"\">Option 1</option></select>" + ACTION_BTN_ELEMENT + "</form></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element form = messageML.getChildren().get(0);
    Element select = form.getChildren().get(0);

    assertEquals("Select class", Select.class, select.getClass());
    verifySelectPresentation((Select) select, name, true, required, placeholder, true, true);
  }

  @Test
  public void testCompleteNotRequiredSelect() throws Exception {
    String name = "complete-id";
    boolean required = false;
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name + "\" required=\"" + required +
        "\"><option value=\"\">Option 1</option></select>" + ACTION_BTN_ELEMENT + "</form></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element form = messageML.getChildren().get(0);
    Element select = form.getChildren().get(0);

    assertEquals("Select class", Select.class, select.getClass());
    verifySelectPresentation((Select) select, name, true, required, null, false, false);
  }

  @Test
  public void testSimpleSelect() throws Exception {
    String name = "simple-id";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\"><option value=\"\">Option 1</option></select>" + ACTION_BTN_ELEMENT
        + "</form></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element form = messageML.getChildren().get(0);
    Element select = form.getChildren().get(0);

    assertEquals("Select class", Select.class, select.getClass());
    verifySelectPresentation((Select) select, name, false, false, null, false, false);
  }

  @Test
  public void testDoubleOptionSelect() throws Exception {
    String name = "simple-id";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\"><option value=\"1\">Option 1</option><option value=\"2\">" +
        "Option 2</option></select>" + ACTION_BTN_ELEMENT + "</form></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element form = messageML.getChildren().get(0);
    Element select = form.getChildren().get(0);

    assertEquals("Select class", Select.class, select.getClass());
    verifySelectPresentation((Select) select, name, false, false, null, false, false);
  }

  @Test
  public void testOptionWithSelectedAttr() throws Exception {
    String name = "simple-id";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\"><option selected=\"true\" value=\"1\">Option 1</option><option value=\"2\">" +
        "Option 2</option></select>" + ACTION_BTN_ELEMENT + "</form></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element form = messageML.getChildren().get(0);
    Element select = form.getChildren().get(0);

    assertEquals("Select class", Select.class, select.getClass());
    verifySelectPresentation((Select) select, name, false, false, null, false, false);
  }

  @Test
  public void testDoubleOptionWithSelectedAttrAsTrue() throws Exception {
    String name = "simple-id";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\"><option value=\"1\" selected=\"true\">Option 1</option><option selected=\"true\" value=\"2\">" +
        "Option 2</option></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Element \"select\" can only have one selected \"option\"");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testOptionWithInvalidValueForSelectedAttr() throws Exception {
    String name = "simple-id";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\"><option value=\"1\" selected=\"something\">Option 1</option><option selected=\"true\" value=\"2\">" +
        "Option 2</option></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage(
        "Attribute \"selected\" of element \"option\" can only be one of the following values: [true, false].");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testChildlessSelect() throws Exception {
    String name = "childless-select";
    String input =
        "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name + "\"></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage(
        "The \"select\" element must have at least one child that is any of the following elements: [option].");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testSelectWithEmptyChild() throws Exception {
    String name = "empty-child-select";
    String input =
        "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name + "\"> </select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage(
        "The \"select\" element must have at least one child that is any of the following elements: [option].");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testSelectWithoutName() throws Exception {
    String input = "<messageML><form id=\"" + FORM_ID_ATTR
        + "\"><select><option value=\"\">Option 1</option></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("The attribute \"name\" is required");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testSelectWithBlankName() throws Exception {
    String input = "<messageML><form id=\"" + FORM_ID_ATTR
        + "\"><select name=\" \"><option value=\"\">Option 1</option></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("The attribute \"name\" is required");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testOptionWithoutValue() throws Exception {
    String name = "nameless-option";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\"><option>Option 1</option></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("The attribute \"value\" is required");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testSelectWithInvalidRequiredAttribute() throws Exception {
    String name = "invalid-required-select";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\" required=\"potato\"><option value=\"\">Option 1</option></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage(
        "Attribute \"required\" of element \"select\" can only be one of the following values: [true, false].");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testSelectWithInvalidAttribute() throws Exception {
    String name = "invalid-attribute-select";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\"  invalid=\"attribute\"><option value=\"\">Option 1</option></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"invalid\" is not allowed in \"select\"");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testOptionWithInvalidAttribute() throws Exception {
    String name = "invalid-attribute-option";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\"><option value=\"\" invalid=\"attribute\">Option 1</option></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"invalid\" is not allowed in \"option\"");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testSelectOutOfForm() throws Exception {
    String name = "out-of-form-select";
    String input = "<messageML><select name=\"" + name + "\"><option value=\"\">Option 1</option></select></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Element \"select\" can only be a inner child of the following elements: [form]");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testOptionOutOfSelect() throws Exception {
    String input = "<messageML><option value=\"\">Option 1</option></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Element \"option\" can only be a child of the following elements: [select]");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testSelectWithInvalidChild() throws Exception {
    String name = "invalid-child-select";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\"><span></span></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Element \"span\" is not allowed in \"select\"");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testOptionWithInvalidChild() throws Exception {
    String name = "invalid-child-option";
    String input = "<messageML><form id=\"" + FORM_ID_ATTR + "\"><select name=\"" + name
        + "\"><option value=\"\"><span></span></option></select></form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Element \"span\" is not allowed in \"option\"");

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelect() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"true\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";
    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);

    Element form = context.getMessageML().getChildren().get(0);
    Element select = form.getChildren().get(0);
    assertEquals("Select class", Select.class, select.getClass());

    //language=HTML
    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">\n"
        + "    <form id=\"id\">\n"
        + "        <select multiple=\"true\" name=\"multi\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</div>";
    assertEquals(trimXml(expectedPresentationML), context.getPresentationML());
  }

  @Test
  public void testMultiSelectMinMax() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"true\" min=\"2\" max=\"2\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";
    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);

    Element form = context.getMessageML().getChildren().get(0);
    Element select = form.getChildren().get(0);
    assertEquals("Select class", Select.class, select.getClass());

    //language=HTML
    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">\n"
        + "    <form id=\"id\">\n"
        + "        <select data-max=\"2\" data-min=\"2\" multiple=\"true\" name=\"multi\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</div>";
    assertEquals(trimXml(expectedPresentationML), context.getPresentationML());
  }

  @Test
  public void testMultiSelectMultiSelected() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"true\">\n"
        + "            <option value=\"opt1\" selected=\"true\">Option 1</option>\n"
        + "            <option value=\"opt2\" selected=\"true\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";
    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);

    //language=HTML
    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">\n"
        + "    <form id=\"id\">\n"
        + "        <select multiple=\"true\" name=\"multi\">\n"
        + "            <option selected=\"true\" value=\"opt1\">Option 1</option>\n"
        + "            <option selected=\"true\" value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</div>";
    assertEquals(trimXml(expectedPresentationML), context.getPresentationML());
  }

  @Test
  public void testMultiSelectWithoutMultiple() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" min=\"1\" max=\"2\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"min\" is not allowed. Attribute \"multiple\" missing");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelectMultipleFalse() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"false\" min=\"1\" max=\"2\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"min\" is not allowed. Attribute \"multiple\" missing");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelectWithoutMultipleMinOnly() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" min=\"1\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"min\" is not allowed. Attribute \"multiple\" missing");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelectWithoutMultipleMaxOnly() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" max=\"1\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"max\" is not allowed. Attribute \"multiple\" missing");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelectMinGreaterThanMax() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"true\" min=\"3\" max=\"2\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"min\" is greater than attribute \"max\"");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelectMinGreaterThanMaxUnset() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"true\" min=\"2\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);

    //language=HTML
    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">\n"
        + "    <form id=\"id\">\n"
        + "        <select data-min=\"2\" multiple=\"true\" name=\"multi\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</div>";
    assertEquals(trimXml(expectedPresentationML), context.getPresentationML());
  }

  @Test
  public void testMultiSelectMinInvalid() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"true\" min=\"-1\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"min\" is not valid");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelectRequiredMinInvalid() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" required=\"true\" multiple=\"true\" min=\"0\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"min\" cannot be 0 if \"required\" is true");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelectRequiredMinDefault() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" required=\"true\" multiple=\"true\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);

    //language=HTML
    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">\n"
        + "    <form id=\"id\">\n"
        + "        <select multiple=\"true\" name=\"multi\" required=\"true\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</div>";
    assertEquals(trimXml(expectedPresentationML), context.getPresentationML());
  }

  @Test
  public void testMultiSelectMinInvalidType() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"true\" min=\"abc\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"min\" is not valid");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelectMaxInvalid() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"true\" max=\"0\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"max\" is not valid");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelectMaxInvalidType() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"true\" max=\"abc\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"max\" is not valid");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testMultiSelectMultipleInvalid() throws Exception {
    //language=XML
    String input = "<messageML>\n" +
        "    <form id=\"" + "id" + "\">\n"
        + "        <select name=\"" + "multi" + "\" multiple=\"invalid\">\n"
        + "            <option value=\"opt1\">Option 1</option>\n"
        + "            <option value=\"opt2\">Option 2</option>\n"
        + "        </select>\n"
        + "        <button type=\"action\" name=\"actionName\">Send</button>\n"
        + "    </form>\n"
        + "</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage(
        "Attribute \"multiple\" of element \"select\" can only be one of the following values: [true, false]");

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testSelectWithReadyOnlyAndDisabledAttributes() throws Exception {
    String input =
        "<messageML><form id=\"form_id\"><select name=\"init\" disabled=\"true\" "
            + "readonly=\"true\"><option value=\"opt1\">Unselected option 1</option><option "
            + "value=\"opt2\" selected=\"true\">With selected option</option><option "
            + "value=\"opt3\">Unselected option 2</option></select><button name=\"submit\" "
            + "type=\"action\">Submit</button></form></messageML>";

    String expectedPresentationML =
        "<div data-format=\"PresentationML\" data-version=\"2.0\"><form id=\"form_id\"><select "
            + "disabled=\"true\" name=\"init\" readonly=\"true\"><option "
            + "value=\"opt1\">Unselected option 1</option><option selected=\"true\" "
            + "value=\"opt2\">With selected option</option><option value=\"opt3\">Unselected "
            + "option 2</option></select><button type=\"action\" "
            + "name=\"submit\">Submit</button></form></div>";

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    assertEquals(expectedPresentationML, context.getPresentationML());
  }

  @Test
  public void testSelectWithInvalidReadyOnlyAttribute() throws Exception {
    String input =
        "<messageML><form id=\"form_id\"><select name=\"init\" readonly=\"invalid\"><option "
            + "value=\"opt1\">Unselected option 1</option><option "
            + "value=\"opt2\" selected=\"true\">With selected option</option><option "
            + "value=\"opt3\">Unselected option 2</option></select><button name=\"submit\" "
            + "type=\"action\">Submit</button></form></messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage(
        "Attribute \"readonly\" of element \"select\" can only be one of the following values: "
            + "[true, false].");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testSelectWithInvalidDisabledAttribute() throws Exception {
    String input =
        "<messageML><form id=\"form_id\"><select name=\"init\" disabled=\"invalid\"><option "
            + "value=\"opt1\">Unselected option 1</option><option "
            + "value=\"opt2\" selected=\"true\">With selected option</option><option "
            + "value=\"opt3\">Unselected option 2</option></select><button name=\"submit\" "
            + "type=\"action\">Submit</button></form></messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage(
        "Attribute \"disabled\" of element \"select\" can only be one of the following values: "
            + "[true, false].");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  public void testAutoSubmitSelect() throws Exception {
    //language=XML
    String input =
        "<messageML><form id=\"form_id\"><select name=\"auto-submit\" "
            + "auto-submit=\"true\"><option value=\"opt1\">option 1</option><option "
            + "value=\"opt2\">option 2</option><option value=\"opt3\">option "
            + "3</option></select><button name=\"dropdown\">Submit</button></form></messageML>";

    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
    //language=HTML
    String expectedPresentationML =
        "<div data-format=\"PresentationML\" data-version=\"2.0\"><form id=\"form_id\"><select "
            + "data-auto-submit=\"true\" name=\"auto-submit\"><option value=\"opt1\">option "
            + "1</option><option value=\"opt2\">option 2</option><option value=\"opt3\">option "
            + "3</option></select><button type=\"action\" "
            + "name=\"dropdown\">Submit</button></form></div>";
    assertEquals(trimXml(expectedPresentationML), context.getPresentationML());
  }

  @Test
  public void testAutoSubmitSelectInvalidAttribute() throws Exception {
    //language=XML
    String input =
        "<messageML><form id=\"form_id\"><select name=\"auto-submit\" "
            + "auto-submit=\"invalid\"><option value=\"opt1\">option 1</option><option "
            + "value=\"opt2\">option 2</option><option value=\"opt3\">option "
            + "3</option></select><button name=\"dropdown\">Submit</button></form></messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage(
        "Attribute \"data-auto-submit\" of element \"select\" can only be one of the following "
            + "values: "
            + "[true, false]");
    context.parseMessageML(trimXml(input), null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testBiContextSelect()
      throws InvalidInputException, IOException, ProcessingException {
    MessageMLContext messageMLContext = new MessageMLContext(null);
    String input = "<messageML>\n"
        + "  <form id=\"form_id\">\n"
        + "<select name=\"init\" required=\"true\" title=\"title01\" label=\"label01\" "
        + "data-placeholder=\"placeholder01\">\n"
        + "      <option value=\"opt1\">Unselected option 1</option>\n"
        + "      <option value=\"opt2\" selected=\"true\">With selected option</option>\n"
        + "      <option value=\"opt3\">Unselected option 2</option>\n"
        + "      </select>\n"
        + "      <button name=\"dropdown\">Submit</button>\n"
        + "  </form>\n"
        + "</messageML>";

    messageMLContext.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    List<BiItem> items = messageMLContext.getBiContext().getItems();

    Map<Object, Object> selectExpectedAttributes = Stream.of(new Object[][] {
        {BiFields.TITLE.getValue(), 1},
        {BiFields.LABEL.getValue(), 1},
        {BiFields.PLACEHOLDER.getValue(), 1},
        {BiFields.REQUIRED.getValue(), 1},
        {BiFields.OPTIONS_COUNT.getValue(), 3},
        {BiFields.DEFAULT.getValue(), 1}
    }).collect(Collectors.toMap(property -> property[0], property -> property[1]));

    BiItem selectBiItemExpected = new BiItem(BiFields.SELECT.getValue(),
        selectExpectedAttributes.entrySet()
            .stream()
            .collect(Collectors.toMap(e ->
                String.valueOf(e.getKey()), Map.Entry::getValue)));

    BiItem formBiItemExpected = new BiItem(BiFields.FORM.getValue(), Collections.emptyMap());

    assertEquals(4, items.size());
    assertEquals(BiFields.SELECT.getValue(), items.get(0).getName());
    assertSameBiItem(selectBiItemExpected, items.get(0));
    assertSameBiItem(formBiItemExpected, items.get(2));
    assertMessageLengthBiItem(items.get(3), input.length());
  }

  @Test
  public void testBiContextMultiSelect() throws InvalidInputException, IOException, ProcessingException {
    MessageMLContext messageMLContext = new MessageMLContext(null);
    String input = "<messageML>\n"
        + "  <form id=\"form_id\">\n"
        + "<select name=\"init\" required=\"true\" title=\"title01\" label=\"label01\" multiple=\"true\" "
        + "data-placeholder=\"placeholder01\">\n"
        + "      <option value=\"opt1\">Unselected option 1</option>\n"
        + "      <option value=\"opt2\" selected=\"true\">With selected option</option>\n"
        + "      <option value=\"opt3\">Unselected option 2</option>\n"
        + "      </select>\n"
        + "      <button name=\"dropdown\">Submit</button>\n"
        + "  </form>\n"
        + "</messageML>";

    messageMLContext.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    List<BiItem> items = messageMLContext.getBiContext().getItems();

    Map<Object, Object> selectExpectedAttributes = Stream.of(new Object[][] {
        {BiFields.MULTI_SELECT.getValue(), 1},
        {BiFields.TITLE.getValue(), 1},
        {BiFields.LABEL.getValue(), 1},
        {BiFields.PLACEHOLDER.getValue(), 1},
        {BiFields.REQUIRED.getValue(), 1},
        {BiFields.OPTIONS_COUNT.getValue(), 3},
        {BiFields.DEFAULT.getValue(), 1}
    }).collect(Collectors.toMap(property -> property[0], property -> property[1]));

    BiItem selectBiItemExpected = new BiItem(BiFields.SELECT.getValue(),
        selectExpectedAttributes.entrySet()
            .stream()
            .collect(Collectors.toMap(e ->
                String.valueOf(e.getKey()), Map.Entry::getValue)));

    assertEquals(4, items.size());
    assertEquals(BiFields.SELECT.getValue(), items.get(0).getName());
    assertSameBiItem(selectBiItemExpected, items.get(0));
  }


  @Test
  public void testBiContextMultipleSelect() throws InvalidInputException, IOException, ProcessingException {
    MessageMLContext messageMLContext = new MessageMLContext(null);
    String input = "<messageML>\n" +
            "  <form id=\"form_id\">\n" +
            "    <h2>dropdown menus</h2>\n" +
            "      <select name=\"data-placeholder\" data-placeholder=\"Only data-placeholder\"><option value=\"opt1\">Unselected option 1</option><option value=\"opt2\">Unselected option 2</option><option value=\"opt3\">Unselected option 3</option></select>\n" +
            "      <select name=\"multiple\" label=\"With multiple select options - between 3 and 5\" multiple=\"true\" min=\"3\" max=\"5\"><option value=\"opt1\" selected=\"true\">Preselected option 1</option><option value=\"opt2\" selected=\"true\">Preselected option 2</option><option value=\"opt3\" selected=\"true\">Preselected option 3</option><option value=\"opt4\">Unselected option 4</option><option value=\"opt5\">Unselected option 5</option><option value=\"opt6\">Unselected option 6</option></select>\n" +
            "      <button name=\"dropdown\">Submit</button>\n" +
            "  </form>\n" +
            "</messageML>";

    messageMLContext.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    List<BiItem> items = messageMLContext.getBiContext().getItems();

    Map<Object, Object> selectExpectedAttributes = Stream.of(new Object[][] {
            {BiFields.MULTI_SELECT.getValue(), 1},
            {BiFields.LABEL.getValue(), 1},
            {BiFields.OPTIONS_COUNT.getValue(), 6},
            {BiFields.DEFAULT.getValue(), 1}
    }).collect(Collectors.toMap(property -> property[0], property -> property[1]));

    BiItem selectBiItemExpected = new BiItem(BiFields.SELECT.getValue(),
            selectExpectedAttributes.entrySet()
                    .stream()
                    .collect(Collectors.toMap(e ->
                            String.valueOf(e.getKey()), Map.Entry::getValue)));

    Map<Object, Object> selectExpectedAttributes1 = Stream.of(new Object[][] {
            {BiFields.PLACEHOLDER.getValue(), 1},
            {BiFields.OPTIONS_COUNT.getValue(), 3},
            {BiFields.DEFAULT.getValue(), 0}
    }).collect(Collectors.toMap(property -> property[0], property -> property[1]));

    BiItem selectBiItemExpected1 = new BiItem(BiFields.SELECT.getValue(),
            selectExpectedAttributes1.entrySet()
                    .stream()
                    .collect(Collectors.toMap(e ->
                            String.valueOf(e.getKey()), Map.Entry::getValue)));


    assertEquals(6, items.size());
    assertEquals(BiFields.SELECT.getValue(), items.get(1).getName());
    assertSameBiItem(selectBiItemExpected1, items.get(1));
    assertEquals(BiFields.SELECT.getValue(), items.get(2).getName());
    assertSameBiItem(selectBiItemExpected, items.get(2));
  }
  private String getRequiredPresentationML(String required) {
    if (required != null) {
      if ("true".equals(required) || "false".equals(required)) {
        return String.format(" required=\"%s\"", required);
      }
    }

    return "";
  }

  private String getExpectedSelectMarkdown(Select select, boolean hasLabel, boolean hasTitle) {
    String formMarkdownHeader = "\n   \n";
    String formMarkdownFooter = "   \n";

    StringBuilder expectedMarkdown = new StringBuilder(formMarkdownHeader);
    expectedMarkdown.append(" ");
    expectedMarkdown.append(hasLabel ? addEscapeCharacter(select.getAttribute(LABEL_ATTR)) : "");
    expectedMarkdown.append(" \n");

    for (Element option : select.getChildren()) {
      if (option instanceof Option) {
        expectedMarkdown.append("-").append(option.getChild(0).asText()).append("\n");
      }
    }

    expectedMarkdown.append(ACTION_BTN_MARKDOWN + "\n").append(formMarkdownFooter);
    return expectedMarkdown.toString();
  }

  private String getExpectedSelectPresentation(Select select, boolean hasLabel, boolean hasTitle,
      String uniqueLabelId) {
    String selectOpeningTag =
        "<div data-format=\"PresentationML\" data-version=\"2.0\"><form id=\"" + FORM_ID_ATTR
            + "\">"
            + (hasLabel || hasTitle ? "<div class=\"dropdown-group\" data-generated=\"true\">" : "")
            + (hasLabel ? "<label for=\"dropdown-" + uniqueLabelId + "\">" + select.getAttribute(LABEL_ATTR)
            + "</label>" : "")
            + (hasTitle ? "<span class=\"info-hint\" data-target-id=\"dropdown-" + uniqueLabelId + "\" data-title=\""
            + select.getAttribute(TITLE_ATTR) + "\"></span>" : "")
            + "<select " + getPlaceholderAttribute(select.getAttribute(DATA_PLACEHOLDER_ATTR))
            + "name=\"" + select.getAttribute(NAME_ATTR) + "\""
            + getRequiredPresentationML(select.getAttribute(REQUIRED_ATTR))
            + (hasLabel || hasTitle ? " id=\"dropdown-" + uniqueLabelId + "\"" : "")
            + ">";
    String selectClosingTag = "</select>";
    String formDivClosingTag = "</form></div>";
    String selectChildren = "";

    for (Element option : select.getChildren()) {
      if (option instanceof Option) {
        selectChildren = selectChildren + "<option" + getOptionSelectedExpectedText(option) + " value=\"" +
            option.getAttribute(VALUE_ATTR) + "\">" + option.getChild(0).asText() + "</option>";
      }
    }
    return selectOpeningTag + selectChildren + selectClosingTag + (hasLabel || hasTitle ? "</div>" : "")
        + ACTION_BTN_ELEMENT + formDivClosingTag;
  }

  private String getPlaceholderAttribute(String placeholder) {
    return placeholder != null ? "data-placeholder=\"" + placeholder + "\" " : "";
  }

  private String getOptionSelectedExpectedText(Element option) {
    return option.getAttribute(SELECTED_ATTR) != null ? " selected=\"" + option.getAttribute(SELECTED_ATTR) + "\"" : "";
  }

  private void verifySelectPresentation(Select select, String name, boolean requiredAttrProvided, boolean requiredValue,
      String placeholder, boolean hasLabel, boolean hasTitle) {
    assertEquals("Select name attribute", name, select.getAttribute(NAME_ATTR));
    if (requiredAttrProvided) {
      assertEquals("Select required attribute", String.valueOf(requiredValue), select.getAttribute(REQUIRED_ATTR));
    } else {
      assertNull("Select required attribute", select.getAttribute(REQUIRED_ATTR));
    }

    if (placeholder != null) {
      assertEquals("Select placeholder attribute", placeholder, select.getAttribute(DATA_PLACEHOLDER_ATTR));
    } else {
      assertNull("Select placeholder attribute", select.getAttribute(DATA_PLACEHOLDER_ATTR));
    }

    String presentationML = context.getPresentationML();
    String dropdownRegex = ".*(\"dropdown-(.*?)\").*";
    Pattern pattern = Pattern.compile(dropdownRegex);
    Matcher matcher = pattern.matcher(presentationML);

    assertEquals("Select presentationML",
        getExpectedSelectPresentation(select, hasLabel, hasTitle, matcher.matches() ? matcher.group(2) : null),
        presentationML);
    assertEquals("Select markdown", getExpectedSelectMarkdown(select, hasLabel, hasTitle), context.getMarkdown());
  }

  private static String trimXml(String input) { // to avoid empty text element upon parsing
    return input.replace("\n", "").replace("  ", "");
  }
}
