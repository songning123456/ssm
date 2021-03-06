package com.sn.springlean.framework.webmvc;

import lombok.Data;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sn
 */
@Data
public class SnViewResolver {

    private String viewName;
    private File templateFile;

    public SnViewResolver(String viewName, File templateFile) {
        this.viewName = viewName;
        this.templateFile = templateFile;
    }

    public String processViews(SnModelAndView mv) throws Exception {

        RandomAccessFile randomAccessFile = new RandomAccessFile(this.templateFile, "r");
        StringBuilder sb = new StringBuilder();
        String strLine = "";
        while ((strLine = randomAccessFile.readLine()) != null) {
            // strLine = new String(strLine.getBytes("ISO-8859-1"), "utf-8");
            Matcher m = matchLine(strLine);
            while (m.find()) {
                for (int i = 0; i < m.groupCount(); i++) {
                    String paraName = m.group(i);
                    paraName = paraName.replace("${", "").replace("}", "");
                    Object paraValue = mv.getDataModel().get(paraName);
                    if (paraValue == null) {
                        continue;
                    }

                    strLine = strLine.replaceAll("\\$\\{" + paraName + "\\}", paraValue.toString());
                    // strLine = new String(strLine.getBytes("utf-8"), "ISO-8859-1");
                }

            }
            sb.append(strLine);
        }
        randomAccessFile.close();
        return sb.toString();

    }

    private Matcher matchLine(String strLine) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(strLine);
    }

}
