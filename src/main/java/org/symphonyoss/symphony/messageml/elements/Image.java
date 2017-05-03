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

package org.symphonyoss.symphony.messageml.elements;

import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;
import org.symphonyoss.symphony.messageml.util.XmlPrintStream;

/**
 * Class representing an image.
 *
 * @author lukasz
 * @since 3/27/17
 */
public class Image extends Element {

  public static final String MESSAGEML_TAG = "img";
  private static final String ATTR_SRC = "src";

  public Image(int index, Element parent) {
    super(index, parent, MESSAGEML_TAG);
  }

  @Override
  public void asPresentationML(XmlPrintStream out) {
    out.printElement(MESSAGEML_TAG, null, getAttributes());
  }

  @Override
  protected void buildAttribute(org.w3c.dom.Node item) throws InvalidInputException {
    switch (item.getNodeName()) {
      case ATTR_SRC:
        setAttribute(ATTR_SRC, getStringAttribute(item));
        break;
      default:
        super.buildAttribute(item);
    }
  }

  @Override
  public void validate() throws InvalidInputException {
    assertNoContent();
    if (!getAttributes().containsKey(ATTR_SRC)) {
      throw new InvalidInputException("The attribute \"src\" is required");
    }
  }
}
