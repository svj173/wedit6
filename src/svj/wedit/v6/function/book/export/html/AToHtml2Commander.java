package svj.wedit.v6.function.book.export.html;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.export.obj.ConvertParameter;
import svj.wedit.v6.function.params.BooleanParameter;
import svj.wedit.v6.function.params.FunctionParameter;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.function.AbstractConvertFunction;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.FileTools;
import svj.wedit.v6.tools.StyleTools;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

import static svj.wedit.v6.obj.book.element.StyleType.COLOR_TEXT;

/**
 * Абстракция конверетера в HTML - Общие методы.   -- NEW
 * <BR/>
 * <BR/> Перевести на общий механизм AbstractConvertFunction - как extends
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 08.12.2016 14:07
 */
public abstract class AToHtml2Commander extends AbstractConvertFunction
{
    private final String   BR    = "<br>";  //"<br/>";    Именно <br>, т.к. <br/> - самлиб не понимает и дописывает в начале строки <dd>&nbsp;
    private final String   RED_LINE_PARAM       = "redLine";
    private final String   TITLE_PARAM          = "tornOffHtmlTitle";

    // Включить HTML заголовок
    private final BooleanParameter tornOffHtmlTitle;
    /* Текст для красной строки. */
    private final SimpleParameter redLineParam;

    //protected abstract BookNode[] getSelectedNodes ( BookNode bookNode )   throws WEditException;
    //protected abstract TreeObj[] getNodesToConvert ( BookContent bookContent );


    public AToHtml2Commander ( FunctionId functionId, String functionName, String iconFile, boolean multiSelect )
    {
        super ( functionId, functionName, iconFile, multiSelect );

        tornOffHtmlTitle = new BooleanParameter ( TITLE_PARAM, true );
        tornOffHtmlTitle.setValue ( false );
        tornOffHtmlTitle.setRuName ( "Включить HTML-заголовок" );

        redLineParam = new SimpleParameter ( RED_LINE_PARAM, "<dd>&nbsp;&nbsp;&nbsp;", true );
        redLineParam.setValue ( "&nbsp;&nbsp;&nbsp;" );
        redLineParam.setRuName ( "Красная строка" );
    }


    /** Взять локальные (индивидуальные) параметры конвертации. */
    protected Collection<FunctionParameter> getOtherConvertParams ()
    {
        Collection<FunctionParameter> result;

        result = new ArrayList<FunctionParameter> ();
        result.add ( tornOffHtmlTitle );
        result.add ( redLineParam );

        return result;
    }

    protected void processImage ( String imgFileName, ConvertParameter cp )
    {
        String msg;

        // Скопировать картинку в директорию расположения html-файла
        try
        {
            FileTools.copyFileToDir ( imgFileName, cp.getRealFileName() );
            msg = "<center><IMG src='"+ imgFileName+ "' /></center>\n";

        } catch ( Exception e )        {
            Log.f.error ( "imgFileName = "+imgFileName+"; ConvertParameter = "+cp, e );
            msg = "Error for image '"+imgFileName+ "' : "+ e.getMessage ();
        }
        // Разместить в тексте тег ссылки на картинку (по идее в обьекте может быть и подпись для картинки).
        writeStr ( msg );
        // Иначе текст следующего заголовка пойдет прямо от иконки.
        writeStr ( BR );
    }

    // for toHTML - <BR/>
    protected void processEmptyTitle ( ConvertParameter cp )
    {
        writeStr ( BR );
        // Красная строка. Иначе текст после такого заголовка не будет начинаться с красной строки.
        writeStr ( getRedLineValue(cp) );
    }

    /**
     * Создать html заголовок.
     *
     * Преобразование обьекта Font в html.style:
     * тег < font
     * - color=
     * - size=
     * - имя_шрифта=
     *
     * @param title   Текст заголовка.
     * @param cp    Закладка (структура параметров), отвечающая за текущие параметры преобразования.
     * @return      Заголовок в виде html текста.
     */
    protected void processTitle ( String title, int level, ConvertParameter cp, BookNode nodeObject )
    {
        StringBuilder result;

        result  = new StringBuilder (64);
        result.append ( "<CENTER><H" ).append ( level + 2 ).append ( ">" );
        result.append ( title );
        result.append ( "</H" ).append ( level + 2 ).append ( "></CENTER>\n" );

        writeStr ( result.toString() );

        // Аннотация
        if ( cp.isPrintAnnotation() )
        {
            String str = nodeObject.getAnnotation();
            if ( str != null )
            {
                String[] htmlStyle = createHtmlStyle ( attributeFont );
                writeStr ( htmlStyle[0] );
                writeStr ( str );
                writeStr ( htmlStyle[1] );
            }
        }

        // Красная строка - для последующих текстов.
        writeStr ( BR );
        writeStr ( getRedLineValue(cp) );
    }

    protected void processText ( TextObject textObj, ConvertParameter cp )
    {
        StringBuilder result;
        String        text;
        AttributeSet  style;
        String[]      htmlStyle;  //стиль в виде тегов HTML - начальные теги [0] и конечные [1]

        result  = new StringBuilder (256);
        if ( textObj != null)
        {
            Log.file.info ( "ToHTML: textObj class = %s", textObj.getClass().getName() );
            if ( textObj instanceof EolTextObject )
            {
                // Перенос строки
                result.append ( BR );
                // Для последующих строк. Т.к. в абзацах мы тепепрь красную стрркоу выводим в конец а не в начале - из-за
                // колор стрктуры однйо строки, где сначала идут TextObject, а уже в самом конец - SLN.
                result.append ( getRedLineValue(cp) );
                //result.append ( "\n&nbsp;&nbsp;&nbsp;" );
            }
            else
            {
                style    = textObj.getStyle();
                //Log.file.info ( "ToHTML: style = %s", style );
                text     = textObj.getText();
                Log.file.info ( "ToHTML: text = %s", text );
                if ( text == null )
                {
                    //Log.file.error ( null, "Text is NULL. nodeObject = ", nodeObject, "; text = ", text, "; nodeObject = ",
                    //                 nodeObject, "; nodeLevel = ", nodeLevel, "; elementType = ", elementType );
                    text = " ";    // именно пробел, т.к. почему-то именно пробелы пропадают в null.
                }
                else
                {
                    // - красная строка
                    //result.append ( "\n" );
                    if ( style == null )
                    {
                        // Взять стиль текста
                        style = Par.GM.getFrame ().getCurrentBookContentPanel ().getObject ().getBookStructure ().getTextStyle ();
                    } // иначе это color_text
                    // Стиль текста - в виде тегов HTML - начальные теги [0] и конечные [1]
                    htmlStyle = createHtmlStyle ( style );
                    //Log.file.info ( "ToHTML: htmlStyle = %s", DumpTools.printArray ( htmlStyle, ' ' ) );

                    if ( textObj instanceof SlnTextObject )
                    {
                        //ic = createFirstLineIndent ( style );   -- в HTML он не нужен
                        //Log.file.info ( "ToHTML: style 2 = %s; firstLine = %d", style, ic );
                        // - текст для красной строки - берется из Локальных параметров
                        //if ( fp != null )  result.append ( getRedLineValue(cp) );
                        // - текст
                        result.append ( htmlStyle[ 0 ] );
                        result.append ( text );
                        result.append ( htmlStyle[ 1 ] );
                        result.append ( BR );
                        // красную строку ставим последней, т.к. для колор-конструкций получается что SLN идет последним текстом.
                        // Т.е. толкьоп осле него надо выводить перевод строки и саму красную строку - для последующих текстов.
                        // Здесь также необходимо после титлов и анаотаций обязательно выводить красную строку.
                        result.append ( getRedLineValue ( cp ) );
                    }
                    else
                    {
                        result.append ( htmlStyle[0] );
                        result.append ( text );
                        result.append ( htmlStyle[1] );
                    }
                }
            }
        }
        Log.file.info ( "ToHTML: result html text = %s", result );
        writeStr ( result.toString() );
    }

    /**
     * Создать стиль в виде начальных и конечных тегов HTML - только для простых текстов и анотаций.
     * @param style
     * @return
     */
    private String[] createHtmlStyle ( AttributeSet style )
    {
        String[] result;
        int ic;

        // TEST
        MutableAttributeSet htmlStyle = new SimpleAttributeSet ();
        convertToHTML ( style, htmlStyle );
        Log.file.info ( "ToHTML: style = %s", style );
        Log.file.info ( "ToHTML: htmlStyle = %s", htmlStyle );

        result = new String[2];
        result[0] = "";  //"\n";
        result[1] = "";  //"\n";

        if ( StyleConstants.isUnderline ( style ) )
        {
            result[0] = result[0] + "<U>";
            // заключительные теги - в обратной последовательности
            result[1] = "</U>" + result[1];
        }
        if ( StyleConstants.isItalic ( style ) )
        {
            result[0] = result[0] + "<I>";
            result[1] = "</I>" + result[1];
        }
        if ( StyleConstants.isBold ( style ) )
        {
            result[0] = result[0] + "<B>";
            result[1] = "</B>" + result[1];
        }

        // font - color
        if ( style instanceof WEditStyle )
        {
            WEditStyle wStyle = (WEditStyle) style;
            if ( wStyle.getStyleType() ==  COLOR_TEXT )
            {
                // color - т.е. цвет текста был изменен.
                Color color = StyleConstants.getForeground ( style );
                String font = "#"+StyleTools.color2hex ( color );
                result[0] = result[0] + "<FONT color="+font+">";
                result[1] = "</FONT>" + result[1];
            }
        }

        // align - смещение вправо, по центру.
        ic = StyleConstants.getAlignment ( style );
        switch ( ic )
        {
            case StyleConstants.ALIGN_CENTER:        // 1
                result[0] = result[0] + "<CENTER>";
                result[1] = "</CENTER>" + result[1];
                break;
            case StyleConstants.ALIGN_JUSTIFIED:     // 3
                break;
            case StyleConstants.ALIGN_LEFT:     // StyleConstants.ALIGN_LEFT  if not set
                //StyleConstants.getFirstLineIndent ( attributeFont ) ) - лишее, т.к. красная стркоа была задана отдельно
                break;
            case StyleConstants.ALIGN_RIGHT:         // 2
                result[0] = result[0] + "<P align=right>";
                result[1] = "</P>" + result[1];
                break;
        }

        return result;
    }

    // Занести выбранную в списке Закладку
    protected void initConvert ( ConvertParameter cp ) throws WEditException
    {
        boolean  tornOnHeader;

        tornOnHeader   = getTornOffHtmlTitle ( cp );
        Log.l.info ( "ConvertToHtml: tornOnHeader = %b; param = %s", tornOnHeader, cp );

        if ( tornOnHeader )
        {
            // <meta charset="имя кодировки">
            writeStr ( "<HTML>\n<HEAD>\n<META charset=\""+Par.CODE_BOOK+"\">\n<TITLE>"
                               + getBookContent().getName() + "</TITLE>\n</HEAD>\n\n<BODY>\n\n" );
        }

        // Имя файла и дата - как коментарий
        writeStr ( "\n<!--\nfile : " + cp.getFileName() + "\ndate : " + new Date() + "\n-->\n" );

    }

    protected void finishConvert(ConvertParameter cp, int currentLevel) throws WEditException
    {
        // Заключительная строка - уже скинуто
        //String str = cp.getEndTextParam().getValue();
        //if ( str != null )  writeStr ( str );

        // Оглавление - если задано  - Общее
        //if ( cp.isCreateContent() )   finished ( cp );

        // Заключительные html-теги - если разрешено.
        boolean tornOnHeader   = getTornOffHtmlTitle ( cp );
        if ( tornOnHeader )   writeStr ( "\n\n<BR/>\n</BODY>\n</HTML>\n\n" );
    }

    private boolean getTornOffHtmlTitle ( ConvertParameter cp )
    {
        boolean  tornOnHeader;

        FunctionParameter fp;
        fp = cp.getLocaleParam ( TITLE_PARAM );
        if ( fp == null )
            tornOnHeader = true;
        else
        {
            Object obj = fp.getValue();
            if ( obj == null )
                tornOnHeader = true;
            else if ( obj instanceof Boolean )
                tornOnHeader = (Boolean) obj;
            else
                tornOnHeader = Convert.getBoolean ( obj.toString(), true );
        }
        return tornOnHeader;
    }


    private int createFirstLineIndent ( AttributeSet style )
    {
        float         f1;
        int           result, ic, size;

        result = 0;
        if ( style != null )
        {
            // - красная стркоа в пикселях.
            f1   = StyleConstants.getFirstLineIndent ( style );
            size = StyleConstants.getFontSize ( style );
            Log.file.info ( "ToHTML: firstLine = %s; size = %d", f1, size );
            ic = (int) (f1 / size);
            if ( ic == 0 )  ic = 1;
            result = ic * 2;
        }
        return result;
    }

    @Override
    public void rewrite ()
    {
    }

    /**
     * Create an older style of HTML attributes. This will convert character
     * level attributes that have a StyleConstants mapping over to an HTML
     * tag/attribute. Other CSS attributes will be placed in an HTML style
     * attribute.
     */
    private void convertToHTML(AttributeSet from, MutableAttributeSet to) {
    	if (from == null) {
    		return;
    	}
    	Enumeration keys = from.getAttributeNames();
    	String value = "";
    	while (keys.hasMoreElements()) {
    		Object key = keys.nextElement();
    		if (key instanceof CSS.Attribute) {
    			// default is to store in a HTML style attribute
    			if (value.length() > 0) {
    				value = value + "; ";
    			}
    			value = value + key + ": " + from.getAttribute(key);
    		} else {
    			to.addAttribute(key, from.getAttribute(key));
    		}
    	}
    	if (value.length() > 0) {
    		to.addAttribute( HTML.Attribute.STYLE, value);
    	}
    }

    boolean isLink ( AttributeSet anchorAttr)
    {
    	if(anchorAttr != null)
    	{
    		return anchorAttr.isDefined(HTML.Attribute.HREF);
    	}
    	return false;
    }

    protected String getNewLineSymbol ()
    {
        return BR;
    }

    
}
