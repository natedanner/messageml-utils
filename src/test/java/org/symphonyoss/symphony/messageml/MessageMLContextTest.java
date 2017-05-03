/*
 * Copyright 2016-2017 MessageML - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.symphony.messageml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.symphonyoss.symphony.messageml.elements.BulletList;
import org.symphonyoss.symphony.messageml.elements.CashTag;
import org.symphonyoss.symphony.messageml.elements.Div;
import org.symphonyoss.symphony.messageml.elements.Element;
import org.symphonyoss.symphony.messageml.elements.HashTag;
import org.symphonyoss.symphony.messageml.elements.LineBreak;
import org.symphonyoss.symphony.messageml.elements.Link;
import org.symphonyoss.symphony.messageml.elements.Mention;
import org.symphonyoss.symphony.messageml.elements.MessageML;
import org.symphonyoss.symphony.messageml.elements.Paragraph;
import org.symphonyoss.symphony.messageml.elements.TextNode;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.exceptions.ProcessingException;
import org.symphonyoss.symphony.messageml.util.IDataProvider;
import org.symphonyoss.symphony.messageml.util.UserPresentation;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

public class MessageMLContextTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final IDataProvider dataProvider = mock(IDataProvider.class);

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  MessageMLContext context;

  @Before
  public void setUp() throws InvalidInputException, ProcessingException, ParserConfigurationException {
    UserPresentation user = new UserPresentation(1L, "bot.user1", "Bot User01", "bot.user1@localhost.com");
    when(dataProvider.getUserPresentation(anyLong())).thenReturn(user);
    when(dataProvider.getUserPresentation(anyString())).thenReturn(user);
    context = new MessageMLContext(dataProvider);
  }

  private void validateMessageML(String expectedPresentationML, JsonNode expectedEntityJson,
      String expectedMarkdown, JsonNode expectedEntities) throws Exception {

    assertEquals("PresentationML", expectedPresentationML, context.getPresentationML());
    assertEquals("EntityJSON", MAPPER.writeValueAsString(expectedEntityJson),
        MAPPER.writeValueAsString(context.getEntityJson()));
    assertEquals("Markdown", expectedMarkdown, context.getMarkdown());

    JsonNode entities = context.getEntities();
    assertEquals("Entity URL text", expectedEntities.get("urls").get(0).get("text"),
        entities.get("urls").get(0).get("text"));
    assertEquals("Entity URL text", expectedEntities.get("urls").get(0).get("id"),
        entities.get("urls").get(0).get("id"));
    assertEquals("Entity URL text", expectedEntities.get("urls").get(0).get("expandedUrl"),
        entities.get("urls").get(0).get("expandedUrl"));
    assertEquals("Entity URL text", expectedEntities.get("urls").get(0).get("indexStart"),
        entities.get("urls").get(0).get("indexStart"));
    assertEquals("Entity URL text", expectedEntities.get("urls").get(0).get("indexEnd"),
        entities.get("urls").get(0).get("indexEnd"));
    assertEquals("Entity URL text", expectedEntities.get("urls").get(0).get("type"),
        entities.get("urls").get(0).get("type"));

    assertEquals("Entity user mention text", expectedEntities.get("userMentions").get(0).get("id").longValue(),
        entities.get("userMentions").get(0).get("id").longValue());
    assertEquals("Entity user mention text", expectedEntities.get("userMentions").get(0).get("screenName"),
        entities.get("userMentions").get(0).get("screenName"));
    assertEquals("Entity user mention text", expectedEntities.get("userMentions").get(0).get("prettyName"),
        entities.get("userMentions").get(0).get("prettyName"));
    assertEquals("Entity user mention text", expectedEntities.get("userMentions").get(0).get("text"),
        entities.get("userMentions").get(0).get("text"));
    assertEquals("Entity user mention text", expectedEntities.get("userMentions").get(0).get("indexStart"),
        entities.get("userMentions").get(0).get("indexStart"));
    assertEquals("Entity user mention text", expectedEntities.get("userMentions").get(0).get("indexEnd"),
        entities.get("userMentions").get(0).get("indexEnd"));
    assertEquals("Entity user mention text", expectedEntities.get("userMentions").get(0).get("userType"),
        entities.get("userMentions").get(0).get("userType"));
    assertEquals("Entity user mention text", expectedEntities.get("userMentions").get(0).get("type"),
        entities.get("userMentions").get(0).get("type"));

    for (int i = 0; i < 2; i++) {
      assertEquals("Entity hashtag text", expectedEntities.get("hashtags").get(i).get("id"),
          entities.get("hashtags").get(i).get("id"));
      assertEquals("Entity hashtag text", expectedEntities.get("hashtags").get(i).get("text"),
          entities.get("hashtags").get(i).get("text"));
      assertEquals("Entity hashtag text", expectedEntities.get("hashtags").get(i).get("indexStart"),
          entities.get("hashtags").get(i).get("indexStart"));
      assertEquals("Entity hashtag text", expectedEntities.get("hashtags").get(i).get("indexEnd"),
          entities.get("hashtags").get(i).get("indexEnd"));
      assertEquals("Entity hashtag text", expectedEntities.get("hashtags").get(i).get("type"),
          entities.get("hashtags").get(i).get("type"));
    }
  }

  @Test
  public void testParseMessageML() throws Exception {
    String message = getPayload("payloads/templated_message_all_tags.messageml");
    String data = getPayload("payloads/templated_message_all_tags.json");

    final String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\"> "
        + "<p><img src=\"https://symphony.com/images/web/logo/symphony-logo-nav-light.svg\"/> <br/> "
        + "Sample JIRA issue</p> "
        + "<div class=\"entity\"> "
        + "<h1>Bot User01 updated Bug "
        + "<a href=\"https://whiteam1.atlassian.net/browse/SAM-24\"> "
        + "<i>SAM-24</i>,<b>Sample Bug Blocker</b> "
        + "</a> "
        + "</h1> "
        + "<div class=\"card barStyle\" "
        + "data-icon-src=\"https://symphony.com/images/web/logo/symphony-logo-nav-light.svg\"> "
        + "<div class=\"cardHeader\"> "
        + "<span class=\"label\">Field</span><span class=\"info\">Old Value =&gt; New Value</span> "
        + "</div> "
        + "<div class=\"cardBody\"> "
        + "<ol> "
        + "<li> "
        + "<span class=\"label\">resolution</span> "
        + "<span class=\"info\">Open =&gt; Done</span> "
        + "</li> "
        + "<li> "
        + "<span class=\"label\">status</span> "
        + "<span class=\"info\">To Do =&gt; Done</span> </li> "
        + "</ol> "
        + "</div> "
        + "</div> "
        + "<hr/> "
        + "<table> "
        + "<thead> "
        + "<th> "
        + "<td>Field</td> "
        + "<td>Value</td> "
        + "</th> "
        + "</thead> "
        + "<tbody> "
        + "<tr> "
        + "<td>Assignee</td> "
        + "<td><span class=\"entity\" data-entity-id=\"mention31\">@Bot User01</span></td> "
        + "</tr> "
        + "<tr> "
        + "<td>Labels</td> "
        + "<td> "
        + "<ul> "
        + "<li><span class=\"entity\" data-entity-id=\"keyword37\">#production</span></li> "
        + "<li><span class=\"entity\" data-entity-id=\"keyword39\">#major</span></li> "
        + "</ul> "
        + "</td> "
        + "</tr> "
        + "</tbody> "
        + "<tfoot> "
        + "<tr> "
        + "<th>Priority</th> "
        + "<td>Highest</td> "
        + "</tr> "
        + "<tr> "
        + "<th>Status</th> "
        + "<td>Done</td> "
        + "</tr> "
        + "</tfoot> "
        + "</table> "
        + "</div> "
        + "</div>";
    final String expectedMarkdown = " \n"
        + " \n"
        + " Sample JIRA issue\n"
        + " \n"
        + " **Bot User01 updated Bug https://whiteam1.atlassian.net/browse/SAM-24 **  \n"
        + " FieldOld Value => New Value \n"
        + " \n"
        + " \n"
        + " 1.  resolution Open => Done \n"
        + " 2.  status To Do => Done \n"
        + " \n"
        + " \n"
        + "  \n"
        + "---\n"
        + " Table:\n"
        + "---\n"
        + "   Field |  Value |   |    \n"
        + " Assignee |  @Bot User01 |  \n"
        + " \n"
        + " Labels |   \n"
        + " - #production\n"
        + " - #major\n"
        + " \n"
        + "  |  \n"
        + "   \n"
        + " Priority |  Highest |  \n"
        + " \n"
        + " Status |  Done |  \n"
        + "  ---\n"
        + " \n"
        + " ";
    final JsonNode expectedEntities = MAPPER.readTree("{\n"
        + "    \"urls\": [{\n"
        + "        \"text\": \"https://whiteam1.atlassian.net/browse/SAM-24\",\n"
        + "        \"id\": \"https://whiteam1.atlassian.net/browse/SAM-24\",\n"
        + "        \"expandedUrl\": \"https://whiteam1.atlassian.net/browse/SAM-24\",\n"
        + "        \"indexStart\": 51,\n"
        + "        \"indexEnd\": 95,\n"
        + "        \"type\": \"URL\"\n"
        + "    }],\n"
        + "    \"userMentions\": [{\n"
        + "        \"id\": 1,\n"
        + "        \"screenName\": \"bot.user1\",\n"
        + "        \"prettyName\": \"Bot User01\",\n"
        + "        \"text\": \"@Bot User01\",\n"
        + "        \"indexStart\": 256,\n"
        + "        \"indexEnd\": 267,\n"
        + "        \"userType\": \"lc\",\n"
        + "        \"type\": \"USER_FOLLOW\"\n"
        + "    }],\n"
        + "    \"hashtags\": [{\n"
        + "        \"id\": \"#production\",\n"
        + "        \"text\": \"#production\",\n"
        + "        \"indexStart\": 290,\n"
        + "        \"indexEnd\": 301,\n"
        + "        \"type\": \"KEYWORD\"\n"
        + "    }, {\n"
        + "        \"id\": \"#major\",\n"
        + "        \"text\": \"#major\",\n"
        + "        \"indexStart\": 305,\n"
        + "        \"indexEnd\": 311,\n"
        + "        \"type\": \"KEYWORD\"\n"
        + "    }]\n"
        + "}");
    final ObjectNode expectedEntityJson = (ObjectNode) MAPPER.readTree(data);
    final String generatedEntities = "{\"mention31\":{"
        + "\"type\":\"com.symphony.user.mention\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"com.symphony.user.userId\","
        + "\"value\":\"1\""
        + "}]},"
        + "\"keyword37\":{"
        + "\"type\":\"org.symphonyoss.taxonomy\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.taxonomy.hashtag\","
        + "\"value\":\"production\""
        + "}]},"
        + "\"keyword39\":{"
        + "\"type\":\"org.symphonyoss.taxonomy\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.taxonomy.hashtag\","
        + "\"value\":\"major\""
        + "}]}}";
    expectedEntityJson.setAll((ObjectNode) MAPPER.readTree(generatedEntities));

    context.parseMessageML(message, data, MessageML.MESSAGEML_VERSION);

    MessageML messageML = context.getMessageML();
    assertNotNull("MessageML", messageML);
    assertEquals("Chime", false, messageML.isChime());

    List<Element> children = messageML.getChildren();
    assertEquals("MessageML children", 5, children.size());
    assertEquals("Child #1 class", TextNode.class, children.get(0).getClass());
    assertEquals("Child #1 text", " ", ((TextNode) children.get(0)).getText());
    assertTrue("Child #1 attributes", children.get(0).getAttributes().isEmpty());
    assertTrue("Child #1 children", children.get(0).getChildren().isEmpty());

    assertEquals("Child #2 class", Paragraph.class, children.get(1).getClass());
    assertTrue("Child #2 attributes", children.get(1).getAttributes().isEmpty());
    assertEquals("Child #2 children", 4, children.get(1).getChildren().size());

    assertEquals("Child #3 class", TextNode.class, children.get(2).getClass());
    assertEquals("Child #3 text", " ", ((TextNode) children.get(2)).getText());
    assertTrue("Child #3 attributes", children.get(2).getAttributes().isEmpty());
    assertTrue("Child #3 children", children.get(2).getChildren().isEmpty());

    assertEquals("Child #4 class", Div.class, children.get(3).getClass());
    assertEquals("Child #4 attributes", 1, children.get(3).getAttributes().size());
    assertEquals("Child #4 attribute", "entity", children.get(3).getAttribute("class"));
    assertEquals("Child #4 children", 9, children.get(3).getChildren().size());

    assertEquals("Child #5 class", TextNode.class, children.get(4).getClass());
    assertEquals("Child #5 text", " ", ((TextNode) children.get(4)).getText());
    assertTrue("Child #5 attributes", children.get(4).getAttributes().isEmpty());
    assertTrue("Child #5 children", children.get(4).getChildren().isEmpty());

    validateMessageML(expectedPresentationML, expectedEntityJson, expectedMarkdown, expectedEntities);
  }

  @Test
  public void testParseMarkdown() throws Exception {
    String message = getPayload("payloads/messageml_v1_payload.json");
    JsonNode messageNode = MAPPER.readTree(message);

    final String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\"><br/>Hello!<br/>"
        + "<b>bold</b> <i>italic</i> "
        + "<span class=\"entity\" data-entity-id=\"keyword5\">#hashtag</span> "
        + "<span class=\"entity\" data-entity-id=\"keyword6\">$cashtag</span> "
        + "<span class=\"entity\" data-entity-id=\"mention7\">@Bot User01</span> "
        + "<a href=\"http://example.com\">http://example.com</a>"
        + "<ul>"
        + "<li>list</li>"
        + "<li>item</li>"
        + "</ul>"
        + "</div>";
    final String expectedMarkdown = "Hello!\n"
        + "**bold** _italic_ #hashtag $cashtag @Bot User01 http://example.com\n"
        + "- list\n"
        + "- item\n";
    final JsonNode expectedEntities = MAPPER.readTree("{\n"
        + "    \"hashtags\": [{\n"
        + "        \"id\": \"#hashtag\",\n"
        + "        \"text\": \"#hashtag\",\n"
        + "        \"indexStart\": 25,\n"
        + "        \"indexEnd\": 33,\n"
        + "        \"type\": \"KEYWORD\"\n"
        + "    }, {\n"
        + "        \"id\": \"$cashtag\",\n"
        + "        \"text\": \"$cashtag\",\n"
        + "        \"indexStart\": 34,\n"
        + "        \"indexEnd\": 42,\n"
        + "        \"type\": \"KEYWORD\"\n"
        + "    }],\n"
        + "    \"userMentions\": [{\n"
        + "        \"id\": 1,\n"
        + "        \"screenName\": \"bot.user1\",\n"
        + "        \"prettyName\": \"Bot User01\",\n"
        + "        \"text\": \"@Bot User01\",\n"
        + "        \"indexStart\": 43,\n"
        + "        \"indexEnd\": 54,\n"
        + "        \"userType\": \"lc\",\n"
        + "        \"type\": \"USER_FOLLOW\"\n"
        + "    }],\n"
        + "    \"urls\": [{\n"
        + "        \"text\": \"http://example.com\",\n"
        + "        \"id\": \"http://example.com\",\n"
        + "        \"expandedUrl\": \"http://example.com\",\n"
        + "        \"indexStart\": 55,\n"
        + "        \"indexEnd\": 73,\n"
        + "        \"type\": \"URL\"\n"
        + "    }]\n"
        + "}");

    final String generatedEntities = "{"
        + "\"keyword5\":{"
        + "\"type\":\"org.symphonyoss.taxonomy\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.taxonomy.hashtag\","
        + "\"value\":\"hashtag\""
        + "}]},"
        + "\"keyword6\":{"
        + "\"type\":\"org.symphonyoss.fin.security\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.fin.security.id.ticker\","
        + "\"value\":\"cashtag\""
        + "}]},"
        + "\"mention7\":{"
        + "\"type\":\"com.symphony.user.mention\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"com.symphony.user.userId\","
        + "\"value\":\"1\""
        + "}]}"
        + "}";
    final JsonNode expectedEntityJson =  MAPPER.readTree(generatedEntities);

    context.parseMarkdown(messageNode.get("text").textValue(), messageNode.get("entities"));

    MessageML messageML = context.getMessageML();
    assertNotNull("MessageML", messageML);
    assertEquals("Chime", false, messageML.isChime());

    List<Element> children = messageML.getChildren();
    assertEquals("MessageML children", 15, children.size());
    assertEquals("Child #1 class", LineBreak.class, children.get(0).getClass());
    assertTrue("Child #1 attributes", children.get(0).getAttributes().isEmpty());
    assertTrue("Child #1 children", children.get(0).getChildren().isEmpty());

    assertEquals("Child #2 class", TextNode.class, children.get(1).getClass());
    assertEquals("Child #2 text", "Hello!", ((TextNode) children.get(1)).getText());
    assertTrue("Child #2 attributes", children.get(1).getAttributes().isEmpty());
    assertEquals("Child #2 children", 0, children.get(1).getChildren().size());

    assertEquals("Child #8 class", HashTag.class, children.get(7).getClass());
    assertEquals("Child #8 text", "hashtag", ((HashTag) children.get(7)).getTag());
    assertTrue("Child #8 attributes", children.get(7).getAttributes().isEmpty());
    assertTrue("Child #8 children", children.get(7).getChildren().isEmpty());

    assertEquals("Child #10 class", CashTag.class, children.get(9).getClass());
    assertEquals("Child #10 text", "cashtag", ((CashTag) children.get(9)).getTag());
    assertEquals("Child #10 attributes", 0, children.get(9).getAttributes().size());
    assertEquals("Child #10 children", 0, children.get(9).getChildren().size());

    assertEquals("Child #12 class", Mention.class, children.get(11).getClass());
    assertEquals("Child #12 user ID", 1, ((Mention) children.get(11)).getUserPresentation().getId());
    assertEquals("Child #12 user email", "bot.user1@localhost.com",
        ((Mention) children.get(11)).getUserPresentation().getEmail());
    assertEquals("Child #12 user name", "bot.user1",
        ((Mention) children.get(11)).getUserPresentation().getScreenName());
    assertTrue("Child #12 attributes", children.get(11).getAttributes().isEmpty());
    assertTrue("Child #12 children", children.get(11).getChildren().isEmpty());

    assertEquals("Child #14 class", Link.class, children.get(13).getClass());
    assertEquals("Child #14 text", new URI("http://example.com"), ((Link) children.get(13)).getUri());
    assertEquals("Child #14 attributes", 1, children.get(13).getAttributes().size());
    assertEquals("Child #14 attribute", "http://example.com", children.get(13).getAttribute("href"));
    assertEquals("Child #14 children", 0, children.get(13).getChildren().size());

    assertEquals("Child #15 class", BulletList.class, children.get(14).getClass());
    assertEquals("Child #15 attributes", 0, children.get(14).getAttributes().size());
    assertEquals("Child #15 children", 2, children.get(14).getChildren().size());

    validateMessageML(expectedPresentationML, expectedEntityJson, expectedMarkdown, expectedEntities);
  }

  @Test
  public void testFailOnInvalidMessageML() throws Exception {
    String invalidMarkup = "<message></message>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Root tag must be <messageML>");
    context.parseMessageML(invalidMarkup, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testFailOnInvalidCharacters() throws Exception {
    String invalidMarkup = "<messageML>Hello\bworld!</messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Invalid control characters in message");
    context.parseMessageML(invalidMarkup, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testFailOnInvalidAttibuteMarkup() throws Exception {
    String invalidMarkup = "<messageML><div class=invalid>Test</div></messageML>";
    expectedException.expect(InvalidInputException.class);
    // Local test throws the correct message, but Sonar doesn't, so commenting this check out
//    expectedException.expectMessage("Invalid messageML: "
//        + "Open quote is expected for attribute \"class\" associated with an  element type  \"div\"");
    context.parseMessageML(invalidMarkup, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testFailOnInvalidTagMarkup() throws Exception {
    String invalidMarkup = "<messageML><div class=\"invalid\">Test</span></messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Invalid messageML: "
        + "The element type \"div\" must be terminated by the matching end-tag \"</div>\"");
    context.parseMessageML(invalidMarkup, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testFailOnInvalidTag() throws Exception {
    String invalidMarkup = "<messageML><code>Test</code></messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Invalid MessageML content at element \"code\"");
    context.parseMessageML(invalidMarkup, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testFailOnInvalidJSON() throws Exception {
    String message = "<messageML>MessageML</messageML>";
    String json = "{invalid: json}";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Error parsing EntityJSON: "
        + "Unexpected character ('i' (code 105)): was expecting double-quote to start field name");
    context.parseMessageML(message, json, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testFailOnMismatchedEntities() throws Exception {
    String message = "<messageML><div class=\"entity\" data-entity-id=\"obj123\">This will fail</div></messageML>";
    String entityJson = "{\"obj456\": \"Won't match\"}";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Error processing EntityJSON: "
        + "no entity data provided for \"data-entity-id\"=\"obj123\"");
    context.parseMessageML(message, entityJson, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testFailOnNoTemplateData() throws Exception {
    String message = getPayload("payloads/single_jira_ticket.messageml");

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Error parsing Freemarker template: invalid input at line 5, column 29");
    context.parseMessageML(message, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testFailOnInvalidTemplate() throws Exception {
    String message = "<messageML><#invalid>template</#invalid>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Syntax error in template \"messageML\" in line 1, column 13");
    context.parseMessageML(message, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testGetMessageMLFailOnUnparsedMessage() throws Exception {
    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("The message hasn't been parsed yet. "
        + "Please call MessageMLContext.parse() first.");
    context.getMessageML();
  }

  @Test
  public void testGetPresentationMLFailOnUnparsedMessage() throws Exception {
    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("The message hasn't been parsed yet. "
        + "Please call MessageMLContext.parse() first.");
    context.getPresentationML();
  }

  @Test
  public void testGetMarkdownMessageMLFailOnUnparsedMessage() throws Exception {
    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("The message hasn't been parsed yet. "
        + "Please call MessageMLContext.parse() first.");
    context.getMarkdown();
  }

  @Test
  public void testGetEntitiesFailOnUnparsedMessage() throws Exception {
    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("The message hasn't been parsed yet. "
        + "Please call MessageMLContext.parse() first.");
    context.getEntities();
  }

  @Test
  public void testGetEntityJSONFailOnUnparsedMessage() throws Exception {
    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage("The message hasn't been parsed yet. "
        + "Please call MessageMLContext.parse() first.");
    context.getEntityJson();
  }

  private String getPayload(String filename) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return new Scanner(classLoader.getResourceAsStream(filename)).useDelimiter("\\A").next();
  }

}