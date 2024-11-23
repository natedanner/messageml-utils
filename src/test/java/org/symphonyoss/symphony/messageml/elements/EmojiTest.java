package org.symphonyoss.symphony.messageml.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.symphonyoss.symphony.messageml.bi.BiFields;
import org.symphonyoss.symphony.messageml.bi.BiItem;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.exceptions.ProcessingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmojiTest extends ElementTest {

  @Test
  public void testEmojiDefaultNonRequiredAttributes() throws Exception {
    String input = "<messageML><emoji shortcode=\"smiley\"><b>Test of content</b></emoji></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element emoji = messageML.getChildren().get(0);

    assertEquals("Emoji class", Emoji.class, emoji.getClass());
    verifyEmojiPresentation((Emoji) emoji, "smiley", null, "normal", "😃");
  }

  @Test
  public void testEmojiWithBlockLevelContent() throws Exception {
    String invalidContent = "<messageML><emoji shortcode=\"smiley\"><p>Invalid content</p></emoji></messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Element \"p\" is not allowed in \"emoji\"");
    context.parseMessageML(invalidContent, null, MessageML.MESSAGEML_VERSION);
    context.getMessageML();
  }

  @Test
  public void testEmojiWithNonRequiredAttributes() throws Exception {
    String input =
        "<messageML><emoji family=\"Rick and Morty\" size=\"big\" shortcode=\"smiley\"><b>Test of content</b></emoji></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element emoji = messageML.getChildren().get(0);

    assertEquals("Emoji class", Emoji.class, emoji.getClass());
    verifyEmojiPresentation((Emoji) emoji, "smiley", "Rick and Morty", "big", "😃");
  }

  @Test
  public void testEmojiNonValidShortcode() throws Exception {
    String input =
        "<messageML><emoji family=\"Rick and Morty\" size=\"big\" shortcode=\"smiley.something invalid\"><b>Test of content</b></emoji></messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage(
        "Shortcode or Annotation parameter may only contain alphanumeric characters, underscore, plus sign and dash");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

  }

  @Test
  public void testEmojiUnicodeNotFound() throws Exception {
    String input = "<messageML><emoji shortcode=\"notfoundemoji\"><b>Test of content</b></emoji></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element emoji = messageML.getChildren().get(0);

    assertEquals("Emoji class", Emoji.class, emoji.getClass());
    verifyEmojiPresentation((Emoji) emoji, "notfoundemoji", null, "normal", null);
  }

  @Test
  public void testEmojiMultipleUnicodes() throws Exception {
    String input = "<messageML><emoji shortcode=\"surfer_tone3\"><b>Test of content</b></emoji></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    Element emoji = messageML.getChildren().get(0);

    assertEquals("Emoji class", Emoji.class, emoji.getClass());
    verifyEmojiPresentation((Emoji) emoji, "surfer_tone3", null, "normal", "\uD83C\uDFC4\uD83C\uDFFD");
  }

  @Test
  public void testIgnoreMarkdownEmoji() throws Exception {
    String message = "Hello :smiley:!";
    context.parseMarkdown(message, null, null);

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">Hello :smiley:!</div>";
    String expectedMarkdown = "Hello :smiley:!";
    JsonNode expectedEntities = new ObjectNode(JsonNodeFactory.instance);

    assertEquals("Parse tree", "Text(Hello :smiley:!)", context.getMessageML().getChildren().get(0).toString());
    assertEquals("Presentation ML", expectedPresentationML, context.getPresentationML());
    assertEquals("Markdown", expectedMarkdown, context.getMarkdown());
    assertEquals("EntityJSON", new ObjectNode(JsonNodeFactory.instance), context.getEntityJson());
    assertEquals("Legacy entities", new ObjectNode(JsonNodeFactory.instance), context.getEntities());
  }

  @Test
  public void testEmojiBi() throws Exception {
    String input = "<messageML><emoji shortcode=\"smiley\"><b>Test of content</b></emoji></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    List<BiItem> expectedBiItems = getExpectedEmojiBiItems();

    List<BiItem> biItems = context.getBiContext().getItems();
    assertEquals(biItems.size(), expectedBiItems.size());
    assertTrue(biItems.containsAll(expectedBiItems));
    assertTrue(expectedBiItems.containsAll(biItems));
  }

  @Test
  public void testBiContextEmojiEntity() throws InvalidInputException, IOException,
      ProcessingException {
    String input = "<messageML><emoji shortcode=\"smiley\">Test of content</emoji></messageML>";

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    List<BiItem> items = context.getBiContext().getItems();

    Map<String, Object> emojiExpectedAttributes =
        Collections.singletonMap(BiFields.COUNT.getValue(), 1);
    Map<String, Object> entityExpectedAttributes =
        Collections.singletonMap(BiFields.ENTITY_TYPE.getValue(), "com.symphony.emoji");

    BiItem emojiBiItemExpected = new BiItem(BiFields.EMOJIS.getValue(), emojiExpectedAttributes);
    BiItem entityBiItemExpected = new BiItem(BiFields.ENTITY.getValue(), entityExpectedAttributes);

    assertEquals(3, items.size());
    assertSameBiItem(emojiBiItemExpected, items.get(0));
    assertSameBiItem(entityBiItemExpected, items.get(1));
    assertMessageLengthBiItem(items.get(2), input.length());
  }

  private List<BiItem> getExpectedEmojiBiItems() {
    List<BiItem> biItems = new ArrayList<>();
    biItems.add(new BiItem(BiFields.EMOJIS.getValue(), Collections.singletonMap(BiFields.COUNT.getValue(), 1)));
    biItems.add(new BiItem(BiFields.ENTITY.getValue(), Collections.singletonMap(BiFields.ENTITY_TYPE.getValue(), "com.symphony.emoji")));
    biItems.add(new BiItem(BiFields.MESSAGE_LENGTH.getValue(), Collections.singletonMap(BiFields.COUNT.getValue(), 79)));
    return biItems;
  }


  private void verifyEmojiPresentation(Emoji emoji, String shortcode, String family, String size, String unicode) throws
      JsonProcessingException {
    assertEquals("Emoji name attribute", shortcode, emoji.getShortCode());
    assertEquals("PresentationML", "<div data-format=\"PresentationML\" data-version=\"2.0\"><span class=\"entity\" "
        + "data-entity-id=\"emoji1\"><b>Test of content</b></span></div>", context.getPresentationML());

    String familyAttr = family != null ? ",\"family\":\"" + family + "\"" : "";
    String unicodeAttr = unicode != null ? ",\"unicode\":\"" + unicode + "\"" : "";
    assertEquals("EntityJSON",
        "{" +
            "\"emoji1\":{" +
            "\"type\":\"com.symphony.emoji\"," +
            "\"version\":\"1.0\"," +
            "\"data\":{" +
            "\"shortcode\":\"" + shortcode + "\"," +
            "\"annotation\":\"" + shortcode + "\"," +
            "\"size\":\"" + size + "\"" +
            unicodeAttr +
            familyAttr +
            "}" +
            "}" +
            "}",
        MAPPER.writeValueAsString(context.getEntityJson()));
    assertEquals("Markdown", ":" + shortcode + ":", context.getMarkdown());
  }
}
