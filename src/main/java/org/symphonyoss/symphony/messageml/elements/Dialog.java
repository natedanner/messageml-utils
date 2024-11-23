package org.symphonyoss.symphony.messageml.elements;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

import org.symphonyoss.symphony.messageml.MessageMLContext;
import org.symphonyoss.symphony.messageml.MessageMLParser;
import org.symphonyoss.symphony.messageml.bi.BiContext;
import org.symphonyoss.symphony.messageml.bi.BiFields;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.markdown.nodes.form.DialogNode;
import org.symphonyoss.symphony.messageml.util.ShortID;
import org.symphonyoss.symphony.messageml.util.XmlPrintStream;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the Symphony Element Dialog which is represented with tag name "dialog".
 * The messageML representation of the dialog element can contain the following attributes:
 * <ul>
 *   <li> id (required) -> used to identify the dialog </li>
 *   <li> width -> used to specify dialog width, must be among values "small", "medium", "large", "full-width". Default value is medium</li>
 *   <li> state -> used to specify dialog state, must be among values "open", "close". Default value is "close"</li>
 * </ul>
 * It can contain the following child tags:
 * <ul>
 *   <li> title (required) -> used to specify dialog title, of type {@link DialogChild.Title}</li>
 *   <li> body (required) -> used to specify dialog body, of type {@link DialogChild.Body}</li>
 *   <li> footer -> used to specify dialog footer, of type {@link DialogChild.Footer}</li>
 * </ul>
 */
public class Dialog extends Element {

  public static final String MESSAGEML_TAG = "dialog";

  public static final String STATE_ATTR = "state";
  public static final String CLOSE_STATE = "close";
  public static final List<String> ALLOWED_STATE_VALUES = asList("open", CLOSE_STATE);

  public static final String WIDTH_ATTR = "width";
  public static final String MEDIUM_WIDTH = "medium";
  public static final List<String> ALLOWED_WIDTH_VALUES = asList("small", MEDIUM_WIDTH, "large", "full-width");

  private static final String DATA_ATTRIBUTE_PREFIX = "data-";
  private static final String OPEN_ATTR = "open";

  public static final String PRESENTATIONML_CLASS = MESSAGEML_TAG;

  private static final ShortID SHORT_ID = new ShortID();

  private String presentationMlIdAttribute;

  public Dialog(Element parent, FormatEnum format) {
    super(parent, MESSAGEML_TAG, format);
  }

  @Override
  public Boolean hasIdAttribute() {
    return true;
  }

  @Override
  protected void buildAttribute(MessageMLParser parser, Node item) throws InvalidInputException {
    switch (item.getNodeName()) {
      case DATA_ATTRIBUTE_PREFIX + WIDTH_ATTR:
      case WIDTH_ATTR:
      case DATA_ATTRIBUTE_PREFIX + STATE_ATTR:
      case STATE_ATTR:
      case DATA_ATTRIBUTE_PREFIX + OPEN_ATTR:
      case ID_ATTR:
        setAttribute(item.getNodeName().replace(DATA_ATTRIBUTE_PREFIX, ""), item.getNodeValue());
        break;
      case OPEN_ATTR:
        if (format == FormatEnum.MESSAGEML) {
          throwInvalidInputException(item);
        }
        setAttribute(item.getNodeName(), item.getNodeValue());
        break;
      default:
        throwInvalidInputException(item);
    }
  }

  @Override
  public void validate() throws InvalidInputException {
    super.validate();

    checkAttributes();
    validateChildrenTypes();
  }

  @Override
  public void asPresentationML(XmlPrintStream out, MessageMLContext context) {
    out.openElement(getPresentationMLTag(), getPresentationMLAttributes());
    for (Element child : getChildren()) {
      child.asPresentationML(out, context);
    }
    out.closeElement();
  }

  @Override
  org.commonmark.node.Node asMarkdown() throws InvalidInputException {
    return new DialogNode();
  }

  @Override
  void updateBiContext(BiContext context) {
    super.updateBiContext(context);
    context.updateItemCount(BiFields.POPUPS.getValue());
  }

  public String getPresentationMlIdAttribute() {
    if (presentationMlIdAttribute == null) {
      presentationMlIdAttribute = SHORT_ID.generate() + "-" + getAttribute(ID_ATTR);
    }
    return presentationMlIdAttribute;
  }

  private void checkAttributes() throws InvalidInputException {
    validateIdAttribute(ID_ATTR);
    checkAttributeOrPutDefaultValue(WIDTH_ATTR, MEDIUM_WIDTH, ALLOWED_WIDTH_VALUES);
    checkAttributeOrPutDefaultValue(STATE_ATTR, CLOSE_STATE, ALLOWED_STATE_VALUES);
  }

  private void checkAttributeOrPutDefaultValue(String attributeName, String defaultValue, List<String> allowedValues)
      throws InvalidInputException {
    if (getAttribute(attributeName) == null) {
      setAttribute(attributeName, defaultValue);
    }
    assertAttributeValue(attributeName, allowedValues);
  }

  private void validateChildrenTypes() throws InvalidInputException {
    long formsCount = getChildren().stream().filter(Form.class::isInstance).count();
    if (formsCount == 1) {
      assertContentModel(singleton(Form.class),
          "A \"dialog\" element can't contain a \"form\" element and any other element.");
    } else if (formsCount > 1) {
      throw new InvalidInputException("A \"dialog\" element can contain only one \"form\" element");
    } else {
      if (this.format == FormatEnum.MESSAGEML) {
        assertContainsAlwaysChildOfType(singleton(DialogChild.Title.class));
        assertContainsAlwaysChildOfType(singleton(DialogChild.Body.class));
        assertContentModel(asList(DialogChild.Footer.class, DialogChild.Title.class, DialogChild.Body.class));
      } else {
        assertContainsAlwaysChildMatching(
            e -> e.isPresentationMLElement("dialog-title") || e.isPresentationMLElement("dialog-body"),
            "The \"dialog\" element must have at least one child that is any of the following elements: [title,body]."
        );
        assertContentModel(
            e -> e.isPresentationMLElement("dialog-title") || e.isPresentationMLElement("dialog-body") || e.isPresentationMLElement("dialog-footer"),
            child -> "Element \"" + child.getMessageMLTag() + "\" is not allowed in \"" + this.getMessageMLTag() + "\"");
      }
    }
  }

  private Map<String, String> getPresentationMLAttributes() {
    Map<String, String> pmlAttributes = new HashMap<>();

    if (this.format == FormatEnum.MESSAGEML) {
      pmlAttributes.put(OPEN_ATTR, "");
    }

    for (Map.Entry<String, String> mmlAttribute : getAttributes().entrySet()) {
      if (mmlAttribute.getKey().equals(ID_ATTR)) {
        pmlAttributes.put(mmlAttribute.getKey(), getPresentationMlIdAttribute());
      } else if (mmlAttribute.getKey().equals(OPEN_ATTR)) {
        pmlAttributes.put(OPEN_ATTR, "");
      } else {
        pmlAttributes.put(DATA_ATTRIBUTE_PREFIX + mmlAttribute.getKey(), mmlAttribute.getValue());
      }
    }
    return pmlAttributes;
  }
}
