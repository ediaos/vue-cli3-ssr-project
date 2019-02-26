package com.terwergreen.jvue.util;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.issues.GfmIssuesExtension;
import com.vladsch.flexmark.ext.gfm.users.GfmUsersExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.util.Arrays;

public class MarkdownUtil {

    /**
     * Markdown转换为Html
     * @param input 原始Markdown字符串
     * @return 输出的Html
     */
    public static String md2html(String input) {
        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.KRAMDOWN);
        options.set(Parser.EXTENSIONS, Arrays.asList(
                AbbreviationExtension.create(),
                DefinitionExtension.create(),
                FootnoteExtension.create(),
                TablesExtension.create(),
                TypographicExtension.create(),
                GfmIssuesExtension.create(),
                GfmUsersExtension.create()
        ));

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(input);
        String result = renderer.render(document);
        return result;
    }
}
