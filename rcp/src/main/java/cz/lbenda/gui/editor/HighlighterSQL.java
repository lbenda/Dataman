/*
 * Copyright 2014 Lukas Benda <lbenda at lbenda.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.lbenda.gui.editor;

import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Lukas Benda <lbenda @ lbenda.cz> on 6.9.15.
 * Highlighting SQL text */
public class HighlighterSQL implements Highlighter {

  private static final String[] KEYWORDS = new String[] {
      "select", "insert", "delete", "update", "alter",
      "table", "column", "grant", "role", "user",
      "where", "from", "join", "left join", "right join",
      "outer join", "inner join", "on", "create", "schema",
      "primary key", "not null", "null", "into", "set", "values",
      "drop", "comment", "to", "is", "constraint", "foreign key", "add",
      "references", "unique"
  };
  private static final String[] DATATYPE = new String[] {
      "int", "varchar", "char", "date", "timestamp",
      "decimal", "double", "boolean", "tinyint", "smallint",
      "identity", "real", "time", "binary", "other", "varchar_ignorecase",
      "uuid", "array", "geometry", "clob", "blob", "text", "bigint"
  };

  private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
  private static final String DATATYPE_PATTERN = "\\b(" + String.join("|", DATATYPE) + ")\\b";
  private static final String PAREN_PATTERN = "\\(|\\)";
  private static final String BRACE_PATTERN = "\\{|\\}";
  private static final String BRACKET_PATTERN = "\\[|\\]";
  private static final String SEMICOLON_PATTERN = "\\;";
  private static final String STRING_PATTERN = "\"([^'\\\\]|\\\\.)*'";
  private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

  private static final Pattern PATTERN = Pattern.compile(
      "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
          + "|(?<DATATYPE>" + DATATYPE_PATTERN + ")"
          + "|(?<PAREN>" + PAREN_PATTERN + ")"
          + "|(?<BRACE>" + BRACE_PATTERN + ")"
          + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
          + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
          + "|(?<STRING>" + STRING_PATTERN + ")"
          + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
  );

  @Override
  public StyleSpans<Collection<String>> computeHighlighting(String text) {
    Matcher matcher = PATTERN.matcher(text);
    int lastKwEnd = 0;
    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
    while(matcher.find()) {
      String styleClass =
          matcher.group("KEYWORD") != null ? "keyword" :
              matcher.group("DATATYPE") != null ? "datatype" :
                  matcher.group("PAREN") != null ? "paren" :
                      matcher.group("BRACE") != null ? "brace" :
                          matcher.group("BRACKET") != null ? "bracket" :
                              matcher.group("SEMICOLON") != null ? "semicolon" :
                                  matcher.group("STRING") != null ? "string" :
                                      matcher.group("COMMENT") != null ? "comment" :
                                          null; /* never happens */ assert styleClass != null;
      spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
      spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
      lastKwEnd = matcher.end();
    }
    spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
    return spansBuilder.create();
  }

  @Override
  public String stylesheetPath() {
    return HighlighterJava.class.getResource("sql-highlighting.css").toExternalForm();
  }
}
