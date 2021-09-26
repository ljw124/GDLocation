package com.hzcominfo.governtool.utils;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author hesc
 * 
 */
public class StringUtils {
    private static final String emailAddressPattern = "\\b(^['_A-Za-z0-9-]+(\\.['_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";

    public static String msString(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }

    /**
     * 是否为空
     * 
     * @param str String
     * @return Boolean
     */
    public static boolean isEmptyString(String str) {
        if (str == null) {
            return true;
        }

        if (str.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 判断变量是否为空
     * 
     * @param s String
     * @return Boolean
     */
    public static boolean isEmpty(String s) {
        if (null == s || "".equals(s) || "".equals(s.trim()) || "null".equalsIgnoreCase(s)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断变量是否为空
     * 
     * @param s String
     * @return Boolean
     */
    public static boolean isNotEmpty(String s) {
        if (null == s || "".equals(s) || "".equals(s.trim()) || "null".equalsIgnoreCase(s)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取指定长度字符串，如长度小于指定长度，会根据isPre变量在前或在后补充指定字符；如大于指定长度会根据isPre或前或后截取字符串
     * 
     * @param str String待处理字符串
     * @param c char长度不够时填充的字符
     * @param length int返回字符串长度
     * @param isPre boolean为true，在前补0，或者从最后一位开始截取字符串； 为false，在后补0，或者从第一位开始截取字符串
     * @return 处理后定长字符串
     */
    public static String getTheLengthString(String str, int length, char c, boolean isPre) {
        int strLength = 0;
        if (str != null) {
            strLength = str.length();
        } else {
            str = "";
        }
        if (strLength > length) {
            if (isPre) {
                str = str.substring(strLength - length, strLength);
            } else {
                str = str.substring(0, length);
            }
        } else if (strLength == length) {
            return str;
        } else {
            StringBuffer sb = new StringBuffer();
            length = length - strLength;
            for (int i = 0; i < length; i++) {
                sb.append(c);
            }
            if (isPre) {
                sb.append(str);
            } else {
                sb.insert(0, str);
            }
            str = sb.toString();
        }
        return str;
    }

    /**
     * 获取指定长度字符串，源字符串或前补0，或从最后一位截取,舍去高位
     * 
     * @param str
     *            待处理字符串
     * @param length
     *            输出字符串长度
     * @return 指定长度字符串
     */
    public static String getPreLengthString(String str, int length) {
        return getTheLengthString(str, length, '0', true);
    }

    /**
     * 获取指定长度字符串，源字符串或后补0，或从第一位截取，舍去低位
     * 
     * @param str
     *            待处理字符串
     * @param length
     *            输出字符串长度
     * @return 指定长度字符串
     */
    public static String getPostLengthString(String str, int length) {
        return getTheLengthString(str, length, '0', false);
    }

    /**
     * 
     * shieldStr(使用*屏蔽一些字符串)
     * 
     * @param text String
     * @param start 开始
     * @param end 结束
     * @return String
     */
    public static String shieldStr(String text, int start, int end) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        if (text.length() < end || end <= start) {
            return text;
        }
        StringBuffer with = new StringBuffer("");
        for (int i = 0; i < end - start; i++) {
            with.append("*");
        }
        return text.substring(0, start) + with + text.substring(start + with.length(), text.length());
    }

    /**
     * 
     * subLenthString(截取目标字符串第一个字符开始指定长度的字符串)
     * 
     * @param str String
     * @param length 长度
     * @return String
     */
    public static String subLengthString(String str, int length) {
        if (isEmptyString(str)) {
            return "";
        }
        if (str.length() <= length) {
            return str;
        }
        return str.substring(0, length);
    }

    /**
     * 手机号隐码处理
     * 
     * @param mobilePhone 手机号
     * @return String
     */
    public static String shieldMobilePhone(String mobilePhone) {
        return StringUtils.shieldStr(mobilePhone, 3, 7);
    }

    /**
     * 身份证号隐码处理
     * 
     * @param certificateNo 身份证号
     * @return String
     */
    public static String shieldCertificateNo(String certificateNo) {
        return StringUtils.shieldStr(certificateNo, 10, 14);
    }

    /**
     * 检测字符串是否为空
     * @param str String
     * @return boolean
     */
    public static boolean strIsNull(String str) {
        return ((str == null) || "".equals(str));
    }

    /**
     * 检测两个字符串大写状态下是否相同
     * @param str String
     * @param val String
     * @return boolean
     */
    public static boolean strUpperEquals(String str, String val) {
        if (strIsNull(val))
            return false;
        return str.toUpperCase().equals(val.toUpperCase());
    }

    /**
     * 检测两个字符串小写状态下是否相同
     * @param str String
     * @param val String
     * @return boolean
     */
    public static boolean strLowerEquals(String str, String val) {
        if (strIsNull(val))
            return false;
        return str.toLowerCase().equals(val.toLowerCase());
    }

    /**
     * 去空格，如为null则转化为空字符串
     * @param str String
     * @return String
     */
    public static String trim(String str) {
        if (str == null) {
            return "";
        }
        return str.trim();
    }

    public static String trim(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return trim(obj.toString());
        }
    }

    public static String getRadom(int count) {
        StringBuffer sb = new StringBuffer();
        String str = "0123456789";
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            int num = r.nextInt(str.length());
            sb.append(str.charAt(num));
            str = str.replace((str.charAt(num) + ""), "");
        }
        return sb.toString();
    }

    /**
     * 取字符的前几位 value,n
     * @param value String
     * @param i int
     * @return String
     */
    public static String getSubString(String value, int i) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        if (value.length() <= i) {
            return value;
        } else {
            return value.substring(0, i) + "...";
        }
    }

    /**
     * Description:截取字符串并以"..."结尾
     * @param inputText
     *            输入内容
     * @param length
     *            截取字节数
     * @return 截取后的字符串
     */
    public static String trimStr(String inputText, int length) {
        // inputText = "[转贴] 独立Wap发展应以内容为王 ";// 输入字符
        int len = 0;
        if (length < 0) {
            length = 24;
        }
        char[] charArray = inputText.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < charArray.length; i++) {
            char cn = charArray[i];
            byte[] bytes = (String.valueOf(cn)).getBytes();
            len = len + bytes.length;
            if (len > length) {
                sb.append("...");
                break;
            }
            sb.append(cn);

        }
        return sb.toString();
    }

    /**
     * 字符串GBK到UTF-8码的转化
     * 
     * @param inStr GBK编码的字符串
     * @return UTF-8编码的字符串
     */
    public static String wapGbkToUtf(String inStr) {
        char temChr;
        int ascInt;
        int i;
        String result = new String("");
        if (inStr == null) {
            inStr = "";
        }
        for (i = 0; i < inStr.length(); i++) {
            temChr = inStr.charAt(i);
            ascInt = temChr + 0;
            if (ascInt > 255) {
                result = result + "&#x" + Integer.toHexString(ascInt) + ";";
            } else {
                result = result + temChr;
            }
        }
        return result;
    }

    /**
     * EMAIL验证
     * @param str String
     * @return boolean
     */
    public static boolean isMail(String str) {
        String check = "^([a-z0-9A-Z]+[-|\\._]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(str);
        boolean isMatched = matcher.matches();
        if (isMatched) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证手机号
     * @param telno String
     * @return boolean
     */
    public static boolean isTelNo(String telno) {
        String expression = "(^13\\d{9}$|^15[8-9]{1}\\d{8}$)";
        return (isValidate(telno, expression));
    }

    /**
     * 特殊字符替换
     * @param inStr String
     * @return String
     */
    public static String replaceStrHtml(String inStr) {
        String result = inStr;
        if (result != null && "".equals(result)) {
            result = result.replaceAll("\r\n", "<br>");
            result = result.replaceAll(" ", "&nbsp;");
        }
        return result;
    }

    /**
     * 验证正则表达式
     * 
     * @param value String
     * @param expression String
     * @return boolean
     */
    public static boolean isValidate(String value, String expression) {

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(value);

        return matcher.find();
    }

    public static boolean isGradeCoe(String value, String expression) {

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(value);

        return matcher.find();
    }

    /**
     * 得到指定符号前或后的字符
     * @param str String
     * @param action int
     * @param splitSign String
     * @return String
     */
    private static String getPreOrSufStr(String str, int action, String splitSign) {
        if (str == null || str.equals(""))
            return "";
        int index = -1;
        if ((index = str.indexOf(splitSign)) != -1) {
            if (action == 0)
                return str.substring(index + 1).trim();
            return str.substring(0, index).trim();
        }
        return str;
    }

    /**
     * 得到指定符号前的字符
     * @param str String
     * @param splitSign String
     * @return String
     */
    public static String getPreStr(String str, String splitSign) {
        return getPreOrSufStr(str, 1, splitSign);
    }

    /**
     * 得到指定符号后的字符
     * @param str String
     * @param splitSign String
     * @return String
     */
    public static String getSufStr(String str, String splitSign) {
        return getPreOrSufStr(str, 0, splitSign);
    }

    /**
     * 循环删除最后的某个字符，至不是以该字符结尾
     * 
     * @param value String
     * @param c char
     * @return String
     */
    public static String removeEnd(String value, char c) {

        if (StringUtils.isEmpty(value)) {
            return "";
        }
        String ret = value;
        // int i = 0;
        while (StringUtils.isNotEmpty(ret) && (ret.lastIndexOf(c) == (ret.length() - 1))) {
            ret = StringUtils.removeEnd(ret, c);
        }
        return ret;
    }

    /**
     * 去除字符串最后一个字符
     * 
     * @param para String
     * @return String
     */
    public static String removeEnd(String para) {
        if (StringUtils.isNotEmpty(para)) {
            if (para.endsWith(",")) {
                return para.substring(0, para.length() - 1);
            } else {
                return para;
            }
        } else {
            return "";
        }
    }

    /**
     * 循环删除最前的某个字符，至不是以该字符开始
     * 
     * @param value String
     * @param c char
     * @return String
     */
    public static String removeStart(String value, char c) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }

        String ret = value;
        // int i = 0;
        while (StringUtils.isNotEmpty(ret) && (ret.lastIndexOf(String.valueOf(c)) == 0)) {
            ret = StringUtils.removeStart(ret, c);
        }
        return ret;
    }

    /**
     * 循环删除字符串头尾的某个字符，至不是以该字符开始和结尾
     * 
     * @param value String
     * @param c char
     * @return String
     */
    public static String removeFirstAndEnd(String value, char c) {
        String ret = removeEnd(value, c);
        return removeStart(ret, c);
    }

    /**
     * 把bean的属性名转成数据库表的字段名
     * 
     * @param field String
     * @return String
     */
    public static String prop2tablefield(String field) {
        if (field == null || "".equals(field))
            return null;
        if (field.matches("[a-z]+[A-Z][a-z]+([A-Z][a-z]+)*")) {
            Pattern pttern = Pattern.compile("[A-Z]");
            Matcher matcher = pttern.matcher(field);
            while (matcher.find()) {
                String old = matcher.group();
                String ne = matcher.group().toLowerCase();

                field = field.replaceAll(old, "_" + ne);
            }
        }
        return field;
    }

    /**
     * 把数据库表的字段名转成bean的属性名
     * 
     * @param column String
     * @return String
     */
    public static String tablefield2prop(String column) {
        if (column == null || "".equals(column)) {
            return "";
        }
        column = column.toLowerCase();
        char[] chars = column.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '_') {
                int j = i + 1;
                if (j < chars.length) {
                    sb.append(String.valueOf(chars[j]).toUpperCase());
                    i++;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 把bean的类名转成数据库的表名
     * 
     * @param className 类名
     * @return 表名
     */
    public static String class2table(String className) {
        if (className.matches("[A-Z][a-z]+[A-Z][a-z]+([A-Z][a-z]+)*")) {
            Pattern pttern = Pattern.compile("[A-Z]");
            Matcher matcher = pttern.matcher(className);
            while (matcher.find()) {
                String old = matcher.group();
                String ne = matcher.group().toLowerCase();

                className = className.replaceAll(old, "_" + ne);
            }
        }
        if (className.indexOf("_") == 0)
            className = className.substring(1);
        return className;
    }

    // 首字母转小写
    public static String toLowerCaseFirstOne(String str) {
        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(str.charAt(0)))
                    .append(str.substring(1)).toString();
        }
    }

    // 首字母转大写
    public static String toUpperCaseFirstOne(String str) {
        if (Character.isUpperCase(str.charAt(0))) {
            return str;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(str.charAt(0)))
                    .append(str.substring(1)).toString();
        }
    }

    /**
     * 与えられた文字列に前詰めで、指定文字数に達するまで、指定文字を追加します。
     * 
     * @param str
     *            元の文字列
     * @param length
     *            　指定文字数
     * @param addStr
     *            　追加する文字
     * @return　作成した文字列
     */
    public static String leftPad(String str, int length, String addStr) {
        String tempVar = str;
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < (length - tempVar.length()); i++) {
            temp.append(addStr);
        }
        return (temp.toString() + tempVar);
    }

    /**
     * 与えられた文字列に後ろ詰めで、指定文字数に達するまで、指定文字を追加します。
     * 
     * @param str
     *            元の文字列
     * @param length
     *            　指定文字数
     * @param addStr
     *            　追加する文字
     * @return　作成した文字列
     */
    public static String rightPad(String str, int length, String addStr) {
        String tempVar = str;
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < (length - tempVar.length()); i++) {
            temp.append(addStr);
        }
        return (tempVar + temp.toString());
    }

    public static String formatCurrency(String strNumber, Locale locale) {
        if (strNumber == null || strNumber.equals("")) {
            return "";
        }
        if (locale == null) {
            locale = new Locale("zh", "CN");
        }
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        return numberFormat.format(Long.parseLong(strNumber));
    }

    public static boolean validEmail(String email) {
        Pattern pattern = Pattern.compile(emailAddressPattern);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 入力パラメータがnull又は""の場合、""を変換後文字列として戻る。 上記以外の場合、strParaをtrimして、変換後文字列として戻る。<br>
     * 
     * @param strPara
     *            文字列
     * 
     * @return 変換した文字列
     */
    public static String nvl(String strPara) {

        if (!isEmpty(strPara)) {
            return strPara.toString().trim();
        }
        return "";
    }

    /**
     * 入力パラメータがnullの場合、""を変換後文字列として戻る。 上記以外の場合、objを文字列に変換して、戻る。<br>
     * 
     * @param obj
     *            転換Object
     * 
     * @return 変換した文字列
     */
    public static String nvlObj(Object obj) {

        if (obj != null) {
            return obj.toString();
        }
        return "";
    }

    /**
     * 任意個数の文字列を連結して、戻る。。<br>
     * 
     * @param strings
     *            連結文字
     * 
     * @return 引数で指定された文字を連結して戻す
     */
    public static String concat(String... strings) {
        StringBuilder strBuffer = new StringBuilder();

        // strings長さの計算する
        int iCnt = strings.length;

        // 文字を連結
        for (int i = 0; i < iCnt; i++) {
            strBuffer.append(strings[i]);
        }
        return strBuffer.toString();
    }

    /**
     * SQL句を変更するために、replaceInputForLikeを使う。入力したinputDataの中に、
     * すべての"#"を"##"に変換する。すべての"%|_"を"#$0"に変換する。
     * 
     * @param inputData
     *            入力したＳＱＬ
     * 
     * @return 変換したＳＱＬ
     */
    public static String replaceInputForLike(String inputData) {
        return inputData.replaceAll("#", "##").replaceAll("%|_", "#$0").replaceAll("％|＿", "#$0");
    }

    /**
     * データに入力した値は空の場合、半角スペースを戻る。<br>
     * 
     * @param param
     *            更新のデータ
     * 
     * @return 更新後の値
     */
    public static String isNullReturnSpace(String param) {

        // ◎更新のデータは空白の場合
        if (isEmpty(param)) {
            // ○半角スペースを戻る
            return "";
        }
        return param;
    }

    /**
     * データに入力した値は空の場合、半角"0"を戻る。<br>
     * 
     * @param param
     *            更新のデータ
     * 
     * @return 更新した値
     */
    public static String isNullReturnZero(String param) {

        // ◎更新のデータは空白の場合
        if (isEmpty(param)) {
            // ○半角スペースを戻る
            return "0";
        }
        return param;
    }

    /**
     * srcを基に、指定されたlenに" "を補足する。<br>
     * 
     * @param src
     *            入力内容
     * 
     * @param len
     *            補足結果桁数
     * 
     * @return スペースを補足した内容 <br>
     */
    public static String fillSpaceToLen(String src, int len) {
        // Null処理
        if (isEmpty(src)) {
            src = "";
        }
        StringBuilder sbTemp = new StringBuilder();
        sbTemp.append(src);
        while (sbTemp.length() < len) {
            sbTemp.append(" ");
        }
        String strTemp = sbTemp.toString();
        return strTemp;
    }

    public static boolean isUpper(String s) {
        Pattern p = Pattern.compile("[A-Z]+");
        Matcher m = p.matcher(s);
        return m.matches();
    }
}
