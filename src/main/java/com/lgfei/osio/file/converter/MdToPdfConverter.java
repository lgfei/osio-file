package com.lgfei.osio.file.converter;

import com.groupdocs.conversion.Converter;
import com.groupdocs.conversion.options.convert.PdfConvertOptions;
import com.lgfei.osio.file.util.IOUtil;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.misc.Extension;

import java.util.List;
import java.util.Arrays;

public class MdToPdfConverter {

    public static String convertMarkdownToHtml(String markdown) {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, getExtensions());
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        Parser parser = Parser.builder(options).build();
        Node document = parser.parse(markdown);

        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        String html = renderer.render(document);

        return html;
    }

    private static List<Extension> getExtensions() {
        return Arrays.asList(
                TablesExtension.create(),
                YamlFrontMatterExtension.create()
        );
    }

    public static String convertHtmlToPdf(String html, String filePath, String fileName) {
        String htmlFilePath = filePath + fileName+".html";
        IOUtil.writeStrToFile(html, htmlFilePath);
        Converter converter = new Converter(htmlFilePath);
        PdfConvertOptions options = new PdfConvertOptions();
        String pdfFilePath = filePath + fileName +".pdf";
        converter.convert(pdfFilePath, options);
        return pdfFilePath;
    }
}
