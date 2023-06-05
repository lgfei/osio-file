package com.lgfei.osio.file.api.v1;

import com.lgfei.osio.file.converter.MdToPdfConverter;
import com.lgfei.osio.file.util.IOUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/v1/mdToPdf")
public class MdToPdfController {

    @Value("${osio.file.storage.code:E:\\Test\\code\\mybook\\}")
    private String CODE_DIR;
    @Value("${osio.file.storage.mdtopdf:E:\\Test\\mdtopdf\\}")
    private String WORK_DIR;

    /**
     * <p>
     * 1.下载markdown文件
     * 先尝试重 raw.githubusercontent.com 域名直接下载md文件，
     * 如果 raw.githubusercontent.com 不可访问，则克隆整个工程
     * 2.将markdown文件转为html文件
     * 3.将html文件转为pdf文件
     * 4.下载pdf文件到浏览器
     * </p>
     * @return
     */
    @GetMapping("/downloadResume")
    public ResponseEntity<Resource> downloadResume(){
        long currTime = System.currentTimeMillis();
        String mdFileUrl = "https://raw.githubusercontent.com/lgfei/mybook/master/README.md";
        String fileName = "lgf_resume_" + currTime;
        String mdFilePath = null;
        try {
            mdFilePath = WORK_DIR + fileName  + ".md";
            IOUtil.downloadFile(mdFileUrl, mdFilePath);
        } catch (IOException e) {
            IOUtil.cloneRepository("https://github.com/lgfei/mybook.git", CODE_DIR, currTime);
            mdFilePath = CODE_DIR + currTime + File.separator + "README.md";
        }
        String md = IOUtil.readFileToStr(mdFilePath);
        String html = MdToPdfConverter.convertMarkdownToHtml(md);
        String pdfFilePath = MdToPdfConverter.convertHtmlToPdf(html, WORK_DIR, fileName);
        Path pdfFile = Paths.get(pdfFilePath);
        try {
            // 创建文件资源
            Resource resource = new UrlResource(pdfFile.toUri());
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
            // 返回文件响应
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 如果发生错误，返回404 Not Found
        return ResponseEntity.notFound().build();
    }
}
