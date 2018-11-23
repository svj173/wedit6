package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.Par;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.params.*;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.ConfigParam;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.Utils;
import svj.wedit.v6.util.IClone;

import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Сложный параметр. Для конвертации книги (RTF, HTML...)
 * <BR/>
 * <BR/> Содержит список настроек элементов, и другие параметры. (Содержит в себе все параметры, входящие в закладку.)
 * <BR/>
 * <BR/> Для samizdat.lib.ru параметры:
 * <BR/> 1) Отображать или нет титульные теги HTML, TITLE, BODY  - отображать -- tornOffHtmlTitle
 * <BR/> 2) Часть - с нумерацией и названием.
 * <BR/> 3) Глава - с нумерацией, без названия.
 * <BR/> 4) Отсальные титлы - не оборажать.
 * <BR/> 5) Красная строка - \n< dd>&nbsp;&nbsp;&nbsp;
 * <BR/> 6) Комментарий в body - <!-- file:...  date: ... -->
 * <BR/> 7)
 * <BR/>  TitleViewMode - перечислить все варианты  (формат текста заголовка, формат)
  - выводить толкьо название (например, глава) с нумерацией.
  - выводить название (например, глава) с нумерацией и с названием титла.
  - выводить название (например, глава) без нумерации и с названием титла.
  - выводить только название титла.
  - не выводить ничего

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 26.09.2013 14:09
 */
public class ConvertParameter     extends FunctionParameter<Object>   implements Comparable<ConvertParameter>, IClone<ConvertParameter>
{
    // Заголовки
    private final Collection<ElementConvertParameter>   elementList     = new LinkedList<ElementConvertParameter>();
    // Типы элементов - применять-игнорировать
    private final Collection<SimpleParameter>           types           = new TreeSet<SimpleParameter>();

    // Разное - Другие параметры. Не через мап - т.к. тогда трудно контролировать, все ли параметры в списке.

    // --- Только для HTML
    // Включить HTML заголовок
    //private BooleanParameter    tornOffHtmlTitle;
    /* Текст для красной строки. */
    //private SimpleParameter     redLineParam;        -- и для ConvertToTxt (int)

    // ---- Оглавление - может исп во всех конвертерах
    // Создавать ли оглавление
    private BooleanParameter    createContentParam;
    /* Ширина оглавления (в столбиках). */
    private SimpleParameter     contentWidthParam;


    /* Тексты - сигнализировать о наличии в тексте произведения данных символов. Через запятую. todo - Описание в тул-тип. */
    private SimpleParameter     warnTextParam;
    /* Заключительный текст. Например: Продолжение следует. */
    private SimpleParameter     endTextParam;

    /* Имя файла. */
    private String              fileName;

    // Выводить ли аннотацию
    private BooleanParameter     printAnnotation;

    // Неизменяемые заголовки. Хранятся в виде пары: Уровень заголовка - Название заголовка. Параметры - ИД элемента.
    // Для одного уровня может быть несколкьо Названий - т.е. несколкьо пар.
    //private SimpleParameter      strongParameter;
    private OrderListParameter      strongParameter;

    // Локальные параметры - индивидуальные для конкретной функции Конвертации (например, для HTML - Включать HTML-заголовок)
    private final Collection<FunctionParameter>  localeParams = new LinkedList<FunctionParameter>();


    public ConvertParameter ( String param_name )
    {
        super ( param_name );
    }

    public void mergeToOther ( ConvertParameter other )
    {
        // html
        //other.setTornOffHtmlTitle ( tornOffHtmlTitle.clone() );
        //other.setRedLineParam ( redLineParam.clone() );

        // elementList
        for ( ElementConvertParameter p : elementList )
        {
            other.addElement ( p.clone() );
        }
        // types
        for ( SimpleParameter p : types )
        {
            other.addType ( p.clone() );
        }
        // localeParams
        for ( FunctionParameter p : localeParams )
        {
            other.addLocale ( p.clone() );
        }

        other.setStrongParameter ( strongParameter.clone() );

        other.setCreateContentParam ( createContentParam.clone() );
        other.setContentWidthParam ( contentWidthParam.clone() );
        other.setWarnTextParam ( warnTextParam.clone() );
        other.setEndTextParam ( endTextParam.clone() );
        other.setPrintAnnotation ( printAnnotation.clone() );

        //other.setFileName ( fileName );  - лишнее
        other.setFileName ( fileName );
    }

    public ConvertParameter clone ()
    {
        ConvertParameter result;

        result = new ConvertParameter ( getName() );

        super.mergeToOther ( result );
        mergeToOther ( result );

        return result;
    }

    public void addElement ( FunctionParameter element )
    {
        elementList.add ( ( ElementConvertParameter ) element );
    }

    public void addType ( FunctionParameter type )
    {
        if ( type instanceof SimpleParameter )
            types.add ( (SimpleParameter) type );
        else
        {
            String msg = "";
            if ( Par.CURRENT_PARSE_BOOK != null )
                msg = "Book: "+ Par.CURRENT_PARSE_BOOK.getName() + "; File: "+Par.CURRENT_PARSE_BOOK.getFileName()+
                        "; ID = "+Par.CURRENT_PARSE_BOOK.getId();
            Log.l.error ("%s. None SimpleParameter type (file: %s) [%s]: %s", getName(), getFileName(), msg, type );
            //Log.l.error ( DumpTools.printCurrentStackTrace() );
        }
    }

    public void addLocale ( FunctionParameter param )
    {
        localeParams.add ( param );
    }

    public void setLocale ( Collection<FunctionParameter> localeParamsList )
    {
        localeParams.clear ();
        localeParams.addAll ( localeParamsList );
    }

    public void addOtherParam ( String paramName, FunctionParameter param )
    {
        if ( (paramName != null) && (param != null) )
        {
            //if ( paramName.equals ( "tornOffHtmlTitle" ) )          tornOffHtmlTitle    = (BooleanParameter) param;
            //else if ( paramName.equals ( "redLine" ) )              redLineParam        = (SimpleParameter) param;
            if ( paramName.equals ( "printAnnotation" ) )      printAnnotation     = (BooleanParameter) param;
            else if ( paramName.equals ( "warnText" ) )             warnTextParam       = (SimpleParameter) param;
            else if ( paramName.equals ( "endText" ) )              endTextParam        = (SimpleParameter) param;
            else if ( paramName.equals ( "createContentParam" ) )   createContentParam  = (BooleanParameter) param;
            else if ( paramName.equals ( "contentWidthParam" ) )    contentWidthParam   = (SimpleParameter) param;
        }
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int    ic1, ic2;

        try
        {
            ic1 = level+1;
            ic2 = ic1+1;

            outString ( level, Convert.concatObj ( "<param name=\"", getName(), "\" type=\"", ParameterType.CONVERT, "\">\n" ), out );

            // ------------------------ элементы -----------------------
            outString ( ic1, "<elements>\n", out );
            for ( ElementConvertParameter cp : elementList )
            {
                cp.toXml ( ic2, out );
            }
            outString ( ic1, "</elements>\n", out );

            // ------------------------ типы -----------------------
            outString ( ic1, "<types>\n", out );
            for ( SimpleParameter type : types )
            {
                type.toXml ( ic2, out );
            }
            outString ( ic1, "</types>\n", out );

            // ------------------------ Неизменяемые заголовки -----------------------
            outString ( ic1, "<strongTitleParam>\n", out );
            //getStrongParameter().toXml ( ic1, ConfigParam.STRONG_TITLE, out );
            getStrongParameter().toXml ( ic2, out );
            outString ( ic1, "</strongTitleParam>\n", out );

            // --------------------- Другие параметры -----------------------
            outString ( ic1, "<others>\n", out );
            // - tornOffHtmlTitle
            //getTornOffHtmlTitle().toXml ( ic2, out );
            // - redLine
            //getRedLineParam().toXml ( ic2, out );
            // - printAnnotation
            getPrintAnnotation().toXml ( ic2, out );
            // - warnText
            getWarnTextParam().toXml ( ic2, out );
            // - endTextParam
            getEndTextParam().toXml ( ic2, out );
            // - createContentParam
            getCreateContentParam().toXml ( ic2, out );
            // - contentWidthParam
            getContentWidthParam().toXml ( ic2, out );
            //
            outString ( ic1, "</others>\n", out );

            // ------------------------ Локальные -----------------------
            outString ( ic1, "<locale>\n", out );
            for ( FunctionParameter param : localeParams )
            {
                param.toXml ( ic2, out );
            }
            outString ( ic1, "</locale>\n", out );

            // Имя файла
            outTag ( ic1, ConfigParam.FILE, getFileName(), out );

            outString ( level, "</param>\n", out );

        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления Параметра '", getName(), "' в поток :\n", e );
        }
    }

    public boolean equals ( Object obj )
    {
        boolean result;
        result = false;
        if ( (obj != null) && (obj instanceof ConvertParameter ))
        {
            ConvertParameter sp = (ConvertParameter) obj;
            result  = compareTo ( sp ) == 0;
        }
        return result;
    }

    @Override
    public int compareTo ( ConvertParameter o )
    {
        if ( o == null )
            return -1;
        else
            return Utils.compareToWithNull ( getName(), o.getName() );
    }

    public void setFileName ( String fileName )
    {
        this.fileName = fileName;
    }

    public String getFileName ()
    {
        return fileName;
    }

    public Collection<ElementConvertParameter> getElementList ()
    {
        return elementList;
    }

    public Collection<SimpleParameter> getTypes ()
    {
        return types;
    }

    public Collection<FunctionParameter> getLocaleParams ()
    {
        return localeParams;
    }

    /*
    public BooleanParameter getTornOffHtmlTitle ()
    {
        // Параметр должен быть всегда.
        if ( tornOffHtmlTitle == null )  tornOffHtmlTitle = new BooleanParameter ( "tornOffHtmlTitle", true );
        return tornOffHtmlTitle;
    }

    public SimpleParameter getRedLineParam ()
    {
        // Параметр должен быть всегда.
        if ( redLineParam == null )  redLineParam = new SimpleParameter ( "redLine", "<dd>&nbsp;&nbsp;&nbsp;", true );
        return redLineParam;
    }
    */

    public SimpleParameter getWarnTextParam ()
    {
        // Параметр должен быть всегда.
        if ( warnTextParam == null )  warnTextParam = new SimpleParameter ( "warnText", "==", true );
        return warnTextParam;
    }

    @Override
    public Object getValue ()
    {
        return null;
    }

    @Override
    public void setValue ( Object value )
    {
    }

    public ElementConvertParameter getElementParam ( int level )
    {
        for ( ElementConvertParameter ecp : elementList )
        {
            if ( ecp.getLevel() == level )  return ecp;
        }
        return null;
    }

    public TitleViewMode getTitleViewType ( int level )
    {
        TitleViewMode result = null;

        for ( ElementConvertParameter ecp : elementList )
        {
            if ( ecp.getLevel() == level )
            {
                result = ecp.getFormatType();
                break;
            }
        }

        if ( result == null )  result = TitleViewMode.NOTHING;
        return result;
    }

    public OrderListParameter getStrongParameter ()
    {
        // Параметр должен быть всегда.
        //if ( strongParameter == null )  strongParameter = new SimpleParameter ( "strongTitleParam", "", true );
        //return strongParameter;
        if ( strongParameter == null )  strongParameter = new OrderListParameter ( ConfigParam.STRONG_TITLE  );
        return strongParameter;
    }

    public SimpleParameter getContentWidthParam ()
    {
        // Параметр должен быть всегда.
        if ( contentWidthParam == null )  contentWidthParam = new SimpleParameter ( "contentWidthParam", "2" );
        return contentWidthParam;
    }

    public BooleanParameter getCreateContentParam ()
    {
        // Параметр должен быть всегда.
        if ( createContentParam == null )  createContentParam = new BooleanParameter ( "createContentParam", false );
        return createContentParam;
    }

    /* Надо ли формировать оглавление. */
    public boolean isCreateContent ()
    {
        return getCreateContentParam().getValue();
    }

    public void setElementList ( Collection<ElementConvertParameter> list )
    {
        elementList.clear ();
        elementList.addAll ( list );
    }

    public void setTypeList ( Collection<SimpleParameter> list )
    {
        types.clear();
        types.addAll ( list );
    }

    public SimpleParameter getEndTextParam ()
    {
        // Параметр должен быть всегда.
        if ( endTextParam == null )  endTextParam = new SimpleParameter ( "endText", "<center><i>(продолжение следует)</i></center><br/>", true );
        return endTextParam;
    }

    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder ( 64 );
        result.append ( "[ ConvertParameter: " );
        result.append ( super.toString() );
        result.append ( "; elementList size = " );
        result.append ( elementList.size () );
        result.append ( "; types size = " );
        result.append ( types.size() );
        result.append ( "; locale size = " );
        result.append ( localeParams.size() );
        result.append ( "; strongParameter = " );
        result.append ( strongParameter );
        //result.append ( "; tornOffHtmlTitle = " );
        //result.append ( tornOffHtmlTitle );
        result.append ( "; createContentParam = " );
        result.append ( createContentParam );
        result.append ( "; printAnnotation = " );
        result.append ( getPrintAnnotation() );
        //result.append ( "; redLineParam = " );
        //result.append ( redLineParam );
        result.append ( "; warnTextParam = " );
        result.append ( warnTextParam );
        result.append ( "; contentWidthParam = " );
        result.append ( contentWidthParam );
        result.append ( "; endTextParam = " );
        result.append ( endTextParam );
        result.append ( "; fileName = " );
        result.append ( fileName );
        result.append ( " ]" );

        return result.toString();
    }

    public BooleanParameter getPrintAnnotation ()
    {
        // Параметр должен быть всегда.
        if ( printAnnotation == null )  printAnnotation = new BooleanParameter ( "printAnnotation", false );
        return printAnnotation;
    }

    public void setPrintAnnotation ( BooleanParameter printAnnotation )
    {
        this.printAnnotation = printAnnotation;
    }

    public boolean isPrintAnnotation ()
    {
        return getPrintAnnotation().getValue();
    }

    /*
    public void setTornOffHtmlTitle ( BooleanParameter tornOffHtmlTitle )
    {
        this.tornOffHtmlTitle = tornOffHtmlTitle;
    }

    public void setRedLineParam ( SimpleParameter redLineParam )
    {
        this.redLineParam = redLineParam;
    }
    */
    public void setCreateContentParam ( BooleanParameter createContentParam )
    {
        this.createContentParam = createContentParam;
    }

    public void setContentWidthParam ( SimpleParameter contentWidthParam )
    {
        this.contentWidthParam = contentWidthParam;
    }

    public void setWarnTextParam ( SimpleParameter warnTextParam )
    {
        this.warnTextParam = warnTextParam;
    }

    public void setEndTextParam ( SimpleParameter endTextParam )
    {
        this.endTextParam = endTextParam;
    }

    public boolean hasLocaleParams ()
    {
        if ( getLocaleParams() == null || getLocaleParams().isEmpty() )
            return false;
        else
            return true;
    }

    public FunctionParameter getLocaleParam ( String paramName )
    {
        for ( FunctionParameter fp : getLocaleParams() )
        {
            if ( fp.getName ().equals ( paramName ) )  return fp;
        }
        return null;
    }

    public void setStrongParameter ( OrderListParameter strongParameter )
    {
        this.strongParameter = strongParameter;
    }
}
