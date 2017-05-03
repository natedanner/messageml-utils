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
import org.symphonyoss.symphony.messageml.markdown.nodes.TableCellNode;

/**
 * Class representing a table cell container.
 *
 * @author lukasz
 * @since 3/27/17
 */
public class TableCell extends Element {
  public static final String MESSAGEML_TAG = "td";

  public TableCell(int index, Element parent) {
    super(index, parent, MESSAGEML_TAG);
  }

  @Override
  public Node asMarkdown() {
    return new TableCellNode();
  }

  @Override
  public String toString() {
    return "Cell";
  }
}
