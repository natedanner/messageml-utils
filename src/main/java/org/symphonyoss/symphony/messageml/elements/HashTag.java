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

import org.commonmark.node.Node;
import org.symphonyoss.symphony.messageml.markdown.nodes.KeywordNode;

/**
 * Class representing a convenience element for a hash tag. Translated to an anchor element.
 *
 * @author lukasz
 * @since 3/27/17
 */
public class HashTag extends Keyword {
  public static final String MESSAGEML_TAG = "hash";
  public static final String PREFIX = "#";
  public static final String ENTITY_TYPE = "org.symphonyoss.taxonomy";
  private static final String ENTITY_SUBTYPE = "org.symphonyoss.taxonomy.hashtag";
  private static final String ENTITY_VERSION = "1.0";

  public HashTag(int index, Element parent, FormatEnum format) {
    this(index, parent, null, format);
  }

  public HashTag(int index, Element parent, String textContent, FormatEnum format) {
    super(index, parent, MESSAGEML_TAG, textContent, format);
  }

  @Override
  public String asText() {
    return "#" + getTag();
  }

  @Override
  public Node asMarkdown() {
    return new KeywordNode(PREFIX, getTag());
  }

  @Override
  public String toString() {
    return "HashTag(" + getTag() + ")";
  }

  @Override
  protected String getEntitySubType() {
    return ENTITY_SUBTYPE;
  }

  @Override
  protected String getEntityVersion() {
    return ENTITY_VERSION;
  }

  @Override
  protected String getEntityType() {
    return ENTITY_TYPE;
  }
}
