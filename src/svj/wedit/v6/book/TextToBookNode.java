package svj.wedit.v6.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.panel.TextPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.TreeObj;
import svj.wedit.v6.obj.book.*;
import svj.wedit.v6.obj.book.element.StyleName;
import svj.wedit.v6.obj.book.element.StyleType;
import svj.wedit.v6.tools.BookStructureTools;
import svj.wedit.v6.tools.DumpTools;
import svj.wedit.v6.tools.TreeObjTools;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;



/**
 * Скидывает текст из области редактирования в обьекты дерева.
 * <BR> Основной упор - на имя стиля анализируемого куска текста. Имя стиля задаем мы.
 *  Если стиль неизвестен - считается 'сложным текстом', причем данный (неизвестный) стиль сохраняется.
 * <BR> Текущий (стартовый) узел в этом варианте считается неизменным. Т.е.
 * если стиль самого первого заголовка изменить, то это проигнорируется.
 * <BR> Вариант создан, чтобы иметь возможность при открытых фрагментах сохранять книгу.
 * <BR/> Стиль простого текста не сохраняется.
 * <BR/>
 * <BR/> При скидывании в обьект:
 -- флаг на титл и игнорировать пустые строки (в т.ч. и для анотации) пока не появится текст.
 -- по окончании работы с титлом (элементом книги) - trim на все его тексты (т.е. удалить все последние Eol-обьекты).
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 10.04.2012 22:32:53
 */
public class TextToBookNode
{
    /* Текущий обрабатываемый обьект-узел - т.к. в данном тексте могут встречаться и другие заголовки кроме первого - динамический обьект. */
    private BookNode currentNode;

    /** Информация о предыдущем обьекте.
     * Т.к. обьектов может быть несколько, то надо определять текущий currentNode - знать кому добавлять текст, титл, аннотацию. */
    private StyleType   lastStyleType = StyleType.UNKNOW;
    /* Имя стиля текста. Для заголовка - это число - уровень заголовка. */
    private String      lastStyleName = "-1";

    /* Флаг, сообщающий о том - был ли обработан самый первый
    элемент, или еще пока нет (самое начало) - т.к. впереди могут быть пустые строки. */
    private boolean wasProcessFirst;

    // -------------- запоминаем для работы чтобы не таскать по сигнатурам методов. ------------------
    /* текущий узел. Запоминаем при открытии текст этого узла. */
    private BookNode    oldNode;

    /* Вся книга в целом, которой принадлежит данный элемент. */
    private BookContent bookContent;

    private boolean changeTree = false;


    public void process ( TextPanel textPanel, BookContent bookContent )
        throws WEditException
    {
        StyledDocument              doc;
        StringBuilder               errorMessage;
        javax.swing.text.Element    elm;
        TreePanel<BookContent>      treePanel;
        TreeObj                     newRoot;

        Log.l.debug ( "Start" );

        oldNode             = textPanel.getBookNode();
        currentNode         = oldNode;  // говорим что текущий элемент - это наш старый исходный. В нем уже есть парент, а следовательно - и уровень.
        this.bookContent    = bookContent;
        wasProcessFirst     = false;

        try
        {
            doc     = textPanel.getDocument();

            elm     = doc.getDefaultRootElement();

            // Очистить обьект дерева (оставив парентов) - т.к. потом он полностью перепишется заново.
            clearNode ( oldNode );

            errorMessage = new StringBuilder ( 256 );

            // - Запустить преобразование. Ошибки накапливаются в буфере  errorMessage
            editDocument2node ( elm, errorMessage );

            if ( errorMessage.length() > 0 )
                throw new WEditException ( null, "Copy NODE to DOC error.\n Chapter '", oldNode.getName(), "'\n Error : \n", errorMessage );

            // Сообщить панели, что общий текст книги был изменен - чтобы высветилось на панели --- ??? Итак высветится при изменениях текста.
            // - наоборот, гасим панель - мол, не редактируема, а высветим уже панель книги.
            textPanel.setEdit ( false );

            // Сообщить книге что было изменение внутри текущего узла - т.е. книгу требуется сохранить на диске.
            this.bookContent.setEdit ( true );

            // Обновить дерево содержимого книги. --- ??? В каких это случаях?
            if ( changeTree )
            {
                // - создать новое дерево
                newRoot     = TreeObjTools.createTree ( bookContent.getBookNode() );
                // - взять текущую панель с деревом
                treePanel   = Par.GM.getFrame().getCurrentBookContentPanel();
                treePanel.setRootNode ( newRoot );
                treePanel.rewrite();
            }

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.l.error ( "err", e );
            throw new WEditException ( e, "Ошибка преобразования обьекта в текст :\n", e );
        }

        Log.l.debug ( "Finish" );
    }

    /**
     * Преобразовать текстовый документ в древовидный обьект книги.
     *
     * <BR> Дерево определяется исходя из стиля текста. Если стиль незнаком,
     *  кидается как простой текст, описанный данным стилем.
     * <BR> Т.е. текст имеет атрибут - имя стиля. Если у текста стиля
     *  нет - значит стандартный, по умолчанию.
     *
     * @param elm        Анализируемый элемент документа.
     * @param errorMessage Массив сообщений об ошибках.
     */
    private void editDocument2node ( Element elm, StringBuilder errorMessage )
    {
        String          textStr, str, styleName, type;
        int             i, isize;
        javax.swing.text.Element el2;
        AttributeSet    style;

        textStr     = null;

        //Log.l.debug ( "Start. elm = " + elm );
        try
        {
            //Log.l.debug ( );
            str     = elm.getName();        // paragraph, icon, content, section, component (для таблиц)
            Log.l.debug ( "- element name = %s", str );
            if ( str.equals(AbstractDocument.ContentElementName ))
            {
                // LeafElement(content).
                // Это конечный элемент -  с текстом. Анализировать и Занести в дерево.
                Log.l.debug ( "- element = %s", elm );
                style      = getStyle ( elm);
                Log.l.debug ( "AttributeSet for element = %s", style );

                // - Взять имя стиля текста - из набора параметров в атрибуте стиля элемента - styleName.
                styleName   = getStyleName ( style );
                Log.l.debug ( "-- styleName = %s", styleName );

                // - Взять имя стиля текста - из набора параметров в атрибуте стиля элемента - styleName.
                type        = getTypeName ( style );
                Log.l.debug ( "-- type = %s", type );

                // - Выделить сам текст
                textStr     = getText ( elm );
                Log.l.debug ( "-- textStr = '%s'.", textStr );
                if ( textStr != null )    // textStr == null -- Это последний перенос строки - являющийся ошибкой java-редактора.
                {
                    // Проверка на пустую строку с переводом строки.
                    if ( (textStr.length() == 1) && (textStr.charAt(0) == '\n') )
                    {
                        //Log.l.debug ( "Finish. Process EndLine");
                        processEndLine ( elm );
                    }
                    else
                    {
                        // Основной метод обработки текста.
                        processText ( elm, style, styleName, type, textStr );
                    }
                }
            }
            else if ( str.equals(AbstractDocument.ParagraphElementName ) || str.equals(AbstractDocument.SectionElementName ) )
            {
                //Log.l.debug ( "---- Treat Children" );
                // Это элемент, содержащий подэлементы. -- paragraph
                // - Прогнать на вложенные обьекты - рекурсия
                isize   = elm.getElementCount();
                for ( i=0; i<isize; i++ )
                {
                    el2 = elm.getElement(i);
                    editDocument2node ( el2, errorMessage );
                }
            }
            else if ( str.equals("icon" ))
            {
                Log.l.debug ( "------- Treat IMG ---" );
                // Картинка
                style      = getStyle ( elm);
                // $ename=icon,icon=/home/svj/Serg/SvjStores/zs/zs-6/images/Barracuda_01.jpg,FirstLineIndent=40.0,Alignment=1,styleName=image,
                Log.l.debug ( "AttributeSet for element ICON = %s", style );
                processImage ( style );
            }
            //else if ( str.equals("component" ))
            else if ( str.equals(StyleConstants.ComponentElementName ))
            {
                Log.l.debug ( "------- Treat TABLE ---" );
                // Таблица
                style      = getStyle ( elm);
                // name=table,$ename=component,component=javax.swing.JScrollPane
                Log.l.debug ( "AttributeSet for element TABLE = %s", style );
                processTable ( style );
            }
            else
            {
                Log.l.error ( "Неизвестный тип элемента = %s", str );
            }

        } catch ( Exception e )         {
            str = e.toString();
            createError ( errorMessage, str, textStr );
            Log.l.error ( "Error", e );
            //throw new WEditException ( errorMessage, e );
        //} finally          {
            //Log.l.debug ( "Finish" );
        }
    }

    private void processTable ( AttributeSet style )
    {
        String fileName;
        Object obj, name;
        Enumeration en;

        obj = null;

        // Обьект ScrollPane хранится как пара - ключ: CharacterConstants / value: JScrollPane
        en  = style.getAttributeNames ();
        while ( en.hasMoreElements() )
        {
            name = en.nextElement ();
            if ( name.toString().equals ( StyleConstants.ComponentElementName ) )
            {
                Log.l.debug ( "--- FIND Table obj" );
                obj = style.getAttribute(name);
                break;
            }
        }

        //obj    = style.getAttribute ( StyleConstants.CharacterConstants.ComponentElementName );
        //Log.l.debug ( "--- Table obj = %s", obj );
        if ( obj != null )
        {
            // Есть JScrollPane
            //Log.l.debug ( "--- Table obj class = %s", obj.getClass ().getName () );
            // Вытаскивает обьект JTable
            if ( obj instanceof JScrollPane )
            {
                JScrollPane scrollPane;
                Component comp;
                JTable table;
                int row, column;
                TableTextObject tableObject;
                String str;
                Object  value;

                scrollPane  = (JScrollPane) obj;
                comp        = scrollPane.getViewport().getView();
                Log.l.debug ( "--- Table comp = %s", comp );
                table   = (JTable) comp;

                // Берем и сохраняем
                row         = table.getRowCount();
                column      = table.getColumnCount();
                tableObject = new TableTextObject ( row, column );
                // Имена колонок
                for ( int k=0; k<column; k++ )
                {
                    str = table.getColumnName ( k );
                    Log.l.debug ( "--- Table COLUMN %d = %s", k, str );
                    tableObject.addColumnName ( k, str );
                }
                // todo Ширина колонок
                // todo Позиция таблицы - левый правый отступы, алигн и прочее.
                // Данные таблицы  (могут содержаться и обьекты - картинки, другие таблицы, цветной текст, TextArea)
                for ( int i=0; i<row; i++ )
                {
                    for ( int k=0; k<column; k++ )
                    {
                        value = table.getValueAt ( i, k );
                        Log.l.debug ( "--- Table CELL %d:%d = %s", i, k, value );
                        tableObject.setValue ( i, k, value );
                    }
                }
            }
        }

        /*
        if ( fileName != null )
        {
            getCurrentNode().addText ( new ImgTextObject ( fileName ) );
        }
        else
        {
            Log.l.error ( "В стиле IMG не задано имя файла. style = %s", style );
        }
        */
    }


    // $ename=icon,icon=/home/svj/Serg/SvjStores/zs/zs-6/images/Barracuda_01.jpg,FirstLineIndent=40.0,Alignment=1,styleName=image,
    private void processImage ( AttributeSet style )
    {
        String fileName;

        fileName    = (String) style.getAttribute ( "iconFile" );
        Log.l.debug ( "--- ICON file name = %s", fileName );

        if ( fileName != null )
        {
            getCurrentNode().addText ( new ImgTextObject ( fileName ) );
        }
        else
        {
            Log.l.error ( "В стиле IMG не задано имя файла. style = %s", style );
        }
    }

    /**
     * Обработка полученного текста.
     * <br/> Проверяется стиль текста и из него выясняется - это просто текст, заголовок, аннотация и т.д.
     * @param elm         Свинг-элемент редактора.
     * @param style       Стиль тескта, взятый из текстового редактора.
     * @param styleName   Имя стиля текста. Для заголвока - это число - уровень заголовка.
     * @param type        Тип элемента - work, hidden.. может быть null - т.е. work..
     * @param textStr     Собственно текст.
     */
    private void processText ( Element elm, AttributeSet style, String styleName, String type, String textStr )
    {
        StyleType       styleType;

        // Анализ стиля по его имени - styleName - к какому элементу книги он принадлежит (что это за элемент книги?).
        // - т.к. в этом тексте мы можем создать элемент того же уровня или выше
        //styleType   = BookTools.getStyleType ( elm, styleName );
        styleType   = getBookContent().getBookStructure().getStyleType ( styleName, style );  // Тип - заголовок, техт, анотация...
        Log.l.debug ( "---- processText: styleType = %s for text = %s; style = %s", styleType, textStr, style );

        // Обработать текст согласно его стилю
        switch ( styleType )
        {
            case ELEMENT:
                Log.l.debug ( "---- It is TITLE. %s", textStr );
                // Это заголовок
                // - обрабатываем
                processTitle ( textStr, styleName, type );
                lastStyleType = StyleType.ELEMENT;
                break;

            case ANNOTATION:
                Log.l.debug ( "---- It is ANNOTATION. %s", textStr );
                // Это аннотация элемента - добавить (т.к. аннотация может быть многострочной)
                getCurrentNode().addAnnotation ( textStr );
                lastStyleType = StyleType.ANNOTATION;
                break;

            case COLOR_TEXT:
                Log.l.debug ( "---- It is COLOR_TEXT. text=%s; style=%s", textStr, style );
                // Это текст с изменным стилем - цвет, размер, наклон и т.д. Берем этот стиль из элемента.
                getCurrentNode().addText ( textStr, style ); // TextObject cо стилем
                lastStyleType = StyleType.COLOR_TEXT;
                break;

            case IMG:
                // todo Это картинка.
                lastStyleType = StyleType.IMG;
                break;

            case TEXT:
            default:
                Log.l.debug ( "---- It is TEXT. %s", textStr );
                // Это текст
                /*
                // - Выяснить - применяем стиль для стандартного текста или применяем стиль, который задан (изменили шрифт, размер, цвет.... метка...)
                // - Взять имя стиля простого текста
                str = (String) getBookContent().getBookStructure().getTextStyle().getAttribute ( StyleName.STYLE_NAME );
                //str = (String) em.getSwingText().getAttribute ( WCons.STYLE_NAME );
                //Log.l.debug ( "style TEXT = " + str + ", styleName = " + styleName );
                // - если мы в тексте изменяем подсветку то она не сохраняется (null) -- ???
                if ( styleName.equals(str) )
                    style   = null;   // Это простой текст - игнорировать все изменения цвета
                else
                    style   = getStyle ( elm );  // Это текст с изменным стилем - цвет, размер, наклон и т.д. Берем этот стиль из элемента.
                //style   = getStyle(elm);
                Log.l.debug ( "------ style = ", style );
                // заносим в обьект текст вместе со стилем.
                getCurrentNode().addText ( textStr, style ); // TextObject
                */
                getCurrentNode().addText ( textStr, null ); // TextObject - простой текст
                lastStyleType = StyleType.TEXT;
        }

        if ( styleName == null )
            lastStyleName = "text";
        else
            lastStyleName = styleName;

        // Проверяем, может в конце текста стоит перенос строки - обрабатываем.
        /*
        if ( textStr.indexOf ( '\n' ) >= 0 )
        {
            processEndLine ( elm );
        }
        */
    }

    /**
     * Обработать заголовок.
     * <BR/> Основная проблема - если самый первый заголовок вдруг изменит свой уровень, то к какому паренту его привязывать?
     * Следовательно запрещаем менять уровень первого заголовка. И если он все-таки изменен - игнорируем.  
     * <BR/>
     * <BR/> Конверт текста в обьекты:  Для титлов - анализировать предыдущее состояние (стиль). И если - тоже титл и этого же типа - сливать вместе.
     * (Заголовок почему-то внутри себя разбился на несколько лексем одного стиля).
     * <BR/>
     *
     * @param name       Собственно текст заголовка.
     * @param styleName  Имя стиля элемента в виде уровень_тип (например: 2, 3_hidden).
     * @param type       Тип элемента - work, hidden.. может быть null - т.е. work..
     */
    private void processTitle ( String name, String styleName, String type )
    {
        BookNode node;

        Log.l.debug ( "Start. title = '", name, "'; styleName = ", styleName, "; styleType = ", type );

        // Проверка есть ли в данном тексте (заголовке) перенос строки того же стиля.
        // - Для многострочных заголовков - НЕ делаем - пусть остаются переносы строк.
        // -- Вернул удаление переносов - т.к. пока НЕ применяется многострочный заголовок.
        //endLine = false;
        if ( name.indexOf ( '\n') >= 0 )
        {
            // Есть перенос строки внутри или в конце заголовка - удалить его.
            //Log.l.debug ( "Has endLine in this text" );
            name    = name.replace ( "\n", "" );
        }

        // это заголовок - убираем крайние пробелы, т.к. - т.к. пока НЕ применяется многострочный заголовок.
        name    = name.trim();
        Log.l.debug ( "new name = '", name, "'" );

        // Проверить  - это самое начало работы или нет
        if ( wasProcessFirst )
        {
            //Log.l.debug ( "Normal work. Create new node");
            // Продолжение работы
            // - Сраниваем данный стиль с предыдыущим стилем. (Пример: заголовок - sgTEST_33_01
            // Style: title = 'sg'; styleName = 2; lastStyle = TEXT
            // Style: title = 'TEST'; styleName = 2; lastStyle = ELEMENT
            // Style: title = '_33_'; styleName = 2; lastStyle = ELEMENT
            // Style: title = '01'; styleName = 2; lastStyle = ELEMENT
            //
            Log.l.debug ( "Style: title = '", name, "'; styleName = ", styleName, "; lastStyle = ", lastStyleType, "; lastStyleName = ", lastStyleName );
            //if ( lastStyle.getName().equals ( styleName ) )
            if ( (lastStyleType == StyleType.ELEMENT) && (styleName != null) && ( lastStyleName.equals ( styleName ) ) )
            {
                // Стиль совпал - предыдущий тоже был заголовком.
                // - Проверяем на совпадение уровня заголовка.
                // - значит это просто продолжение заголовка (заголовок почему-то внутри себя разбился на несколько лексем одного стиля).
                name = getCurrentNode().getName() + name;
                getCurrentNode().setName ( name );
            }
            else
            {
                // - значит надо создать новый узел
                // - trim для старого
                trim ( currentNode );
                // - Создать новый обьект элемента в дереве -- String name, BookNode parentNode - c родителем
                //node  = new BookNode ( name, currentNode );
                node  = new BookNode ( name, null );
                node.setName ( name );
                node.setElementType ( type );
                // Обработать новый узел - Получить родителя и добавить новый узел (т.к. новый узел может быть выше уровнем чем предыдущий)
                processNewNode ( node, styleName );
                // сменить текущий узел
                currentNode = node;
            }
        }
        else
        {
            //Log.l.debug ( "First work. Use current node");
            // Начало - работать с самым верхним (первым) узлом. Уровень из-за смены стиля ему не меняем. - просто меняем Название узла (обьекта).
            getCurrentNode().setName ( name );
            // отметить что обработка самого первого заголовка уже прошла.
            wasProcessFirst = true;
        }
    }

    /* по окончании работы с титлом (элементом книги) - trim на все его тексты (т.е. удалить все последние Eol-обьекты). */
    private void trim ( BookNode bookNode )
    {
        Collection<TextObject>  texts, deleteList;
        List<TextObject>        list;
        TextObject              textObject;
        int                     ic;

        if ( bookNode != null )
        {
            // взять текст
            texts   = bookNode.getText();
            if ( ! texts.isEmpty() )
            {
                deleteList = new LinkedList<TextObject>();

                // пробегаем с конца списка
                list = new LinkedList<TextObject> ( texts );
                ic   = list.size() - 1;
                for ( int i=ic; i>=0; i-- )
                {
                    textObject  = list.get ( i );
                    if ( textObject instanceof EolTextObject )
                    {
                        deleteList.add ( textObject );
                    }
                    else
                    {
                        break;
                    }
                }
                // удаляем
                for ( TextObject to : deleteList )   texts.remove ( to );
            }
        }
    }

    /**
     * Имеем элемент текста. Хотим получить из него наименование его стиля.
     * <BR/>
     * <BR/>
     * @param style ГУИ-стиль, состоящий из набора атрибутов..
     * @return    Имя стиля.
     */
    private String getStyleName ( AttributeSet style )
    {
        String          result;

        //Log.l.debug ( "element from text = ", elm );

        result  = (String) style.getAttribute ( StyleName.STYLE_NAME );
        Log.l.debug ( "styleName for element = ", result );
        if ( result == null )    result   = StyleType.TEXT.toString();         // нет имя стиля - значит простой текст

        return result;
    }

    // type may be null
    private String getTypeName ( AttributeSet style )
    {
        String          result;

        //Log.l.debug ( "element from text = ", elm );

        result  = (String) style.getAttribute ( StyleName.TYPE_NAME );
        Log.l.debug ( "type for element = %s", result );
        //if ( result == null )    result   = StyleType.TEXT.toString();         // нет имя стиля - значит простой текст

        return result;
    }

    private AttributeSet getStyle ( Element elm )
    {
        AttributeSet    result;
        AbstractDocument.LeafElement    le;

        le      = ( AbstractDocument.LeafElement ) elm;
        /*
        //result  = le.getAttributes();
        Log.l.debug ( "--- getAttributes = %s", le.getAttributes() );
        Log.l.debug ( "--- getAttributes 2 = %s", DumpTools.printAttributeSet ( le.getAttributes () ) );
        Log.l.debug ( "--- getResolveParent = %s", le.getResolveParent () );
        Log.l.debug ( "--- getResolveParent 2 = %s", DumpTools.printAttributeSet ( le.getResolveParent () ) );
        */
        result  = le.copyAttributes();
        //Log.l.debug ( "--- copyAttributes = %s", result );
        //Log.l.debug ( "--- copyAttributes 2 = %s", DumpTools.printAttributeSet ( result ) );
        //Log.l.debug ( "--- getAttributes = ", le.getAttributes() );
        /*
        // Нет атрибута выравнивания - толкьо size=10. Не смог получить этот атрибут.
        //result = elm.getAttributes();  // Получаем  --     LeafElement(content) 22,37
        AttributeSet attributeSet = le.getAttributes();
        Log.l.debug ( "--- element = ", elm, "; style = ", attributeSet );

        AbstractDocument.AbstractElement ad;
        if ( attributeSet instanceof AbstractDocument.AbstractElement )
        {
            ad = ( AbstractDocument.AbstractElement ) attributeSet;
            Log.l.debug ( "--- style 2 = ", ad.getAttributes() );
        }
        */

        return result;
    }

    private String getText ( Element elm ) throws Exception
    {
        String      result;
        int         istart, iend, ic, docSize;
        Document    ed;

        istart  = elm.getStartOffset();
        iend    = elm.getEndOffset();
        ic      = iend - istart;
        ed      = elm.getDocument();
        result  = ed.getText(istart,ic);
        //Log.l.debug ( "istart = " + istart + ", iend = " + iend + ", text = '" + result + "'" );

        // Проверка, вдруг это - самый последний перенос строки, который ошибочно добавляется java-редактором
        docSize = ed.getLength();
        //Log.l.debug ( "docSize = " + docSize );
        if ( iend > docSize )   result  = null;
        
        return result;
    }

    /* Обработать окончание строки. */
    private void processEndLine ( Element elm )
    {
        //AttributeSet    st;

        // Перенос строки здесь зависит от того, какие данные были перед этим. Например, если был заголовок или аннотация - то не применять пустые переносы строки.
        switch ( lastStyleType )
        {
            case ELEMENT:
                //nodeObject  = (BookNodeObject) currentNode.getUserObject ();
                //nodeObject.addTitleVk ();
                // Увеличить пропуск после заголовка  -- лишнее
                //getCurrentNode().setName();
                break;

            case ANNOTATION:
                //lastStyle   = StyleType.TEXT;
                break;

            case TEXT:
            case UNKNOW:
            default:
                // Занести окончание строки в текст
                //nodeObject  = (BookNodeObject) currentNode.getUserObject ();
                // -- если стиль переноса - размер шрифта лучше сохранять в стиле ?.
                //st      = getStyle ( elm );
                //st      = null;
                //getCurrentNode().addText ("\n",st);
                // Применить стиль элемента
                getCurrentNode().addEol();
        }

        // Всегда применяем переносы строки. А стиль? - простого текста?
        //getCurrentNode().addEol();
    }

    /**
     * Очистить содержимое данного элемента книги (заголовок, тексты, аннотацию), оставив только тип и уровень.
     * Также удалить все вложенные узлы.
     * @param node   узел
     */
    private void clearNode ( BookNode node )
    {
        node.clear();
    }

    /**
     * Обработать новый узел - Найти его родителя и Вставить в дерево.
     * <BR/> Определяем реального парента для данного обьекта, находим его и заносим,
     *  т.к. этот обьект может быть выше чем исходный узел.
     * <BR/>
     * <BR> По идее - здесь должен осуществляться анализ обрабатываемых
     * узлов - вдруг данный узел открыт в другом окне, а мы изменяем
     * ему атрибуты или место положения - чего делать нельзя.
     * <BR/> Здесь - запрещаем открыть один и тот же узел в разных окнах.
     * <BR/>
     * @param nodeObject   Обьект, взятый из текста.
     * @param styleName    Имя стиля элемента в виде уровень_тип (например: 2, 3_hidden).
     */
    private void processNewNode ( BookNode nodeObject, String styleName )
    {
        String      str2;
        int         icurr, inew, inum, isize, i;
        BookNode    parent, node2;

        //parent  = getCurrentNode().getParentNode();

        // Взять уровень текущего узла
        icurr       = getCurrentNode().getLevel();

        // Определить уровень нового узла
        /*
        // - Берем элемент описания данного заголовка - по имени его стиля (уровню?).
        // Примеры: Часть_work, Эпизод_hidden, h3_work
        bookElement = getBookContent().getElement ( styleName );
        Log.l.debug ( "------ bookElement = '", bookElement, "'." );
        inew        = bookElement.getElementLevel();
        */
        // Уровень выделяем из имени стиля - первая лексема до разделителя "подчеркивание".
        inew        = BookStructureTools.getLevel ( styleName );

        // Понижаем на шаг - для удобства сравнения. Тогда текущий элемент становится как-бы родителем нашему новому.
        inew--;
        parent      = getCurrentNode();
        // Сравнить с нашим. Исходя их этого взять или создать родителя - parent.
        if ( icurr == inew )
        {
            // Родителем нового элемента является текущий элемент - добавляем текущему в конец.
            //Log.l.debug ( "Parent = currentNode (" + currentNode + ").");
            // в конец
            parent.addBookNode ( parent.getChildSize(), nodeObject );
        }
        else if ( icurr > inew )
        {
            // Родитель находится где-то выше - на inum шагов. Подняться вверх.
            // Подняться по дереву - getParent()
            inum    = icurr - inew; // чтобы подняться выше - до родителя
            //Log.l.debug ( "Go up. Step = " + inum );
            isize   = parent.getChildSize() - 1;
            for ( i=0; i<inum; i++ )
            {
                // TODO Анализ на переподьем - выше 0 уровня.  (parent == null) -- так не должно быть.
                //isize   = parent.getDepth (); // Номер предыдущего парента
                node2   = parent.getParentNode();
                if ( node2 == null )  break;
                // Определить индекс обьекта
                isize   = node2.getChildIndex ( parent );
                parent  = node2;
                //Log.l.debug ( "Number = " + isize );
            }
            // Нашли обьект куда надо добавить наш новый обьект. Подсчитали индекс - т.е. после текущего, по всей их ветке.
            parent.addBookNode ( isize+1, nodeObject );
        }
        else if ( icurr < inew )
        {
            // Родитель находится гораздо ниже - на inum шагов. Опустится.
            //  Если необходимо - создавая по пути новые (пустые) узлы.
            // -- Создать новые элементы ветви дерева по принципу : Node_ + i
            inum    = inew - icurr;
            //Log.l.debug ( "Go down. Step = " + inum );
            //ic      = element.getLevel();
            parent  = currentNode;
            for ( i = 0; i < inum; i++ )
            {
                // TODO Анализ на переспуск - ниже последнего уровня. --- Не должно быть
                str2        = "Node-" + i;
                node2   = new BookNode ( str2, parent );
                // Установить тип элемента - разберется по уровню
                //element2    = em.getFirstElement ( ic - i - 1 );
                //
                //str     = em.createContentName ( str2, str );
                //nodeObject2.setContentName ( str );
                // Создать новый, промежуточный, узел
                //contentPanel.insertNode ( node2, parent, parent.getChildCount() );
                parent.addBookNode ( parent.getChildSize(), node2 );
                //parent.add ( node2 );
                parent  = node2;
            }
            // Добавить в дерево части - первым
            //Log.l.debug ( "Parent = (" + parent + ").");
            //contentPanel.insertNode ( node, parent, 0 );
            parent.addBookNode ( 0, nodeObject );
        }
        //Log.l.debug ( "Add NODE. parent = " + parent + ", node = " + node );
        currentNode = nodeObject;

        // дерево изменилось - необходимо перерисовать и его.
        changeTree  = true;
    }

    private void createError ( StringBuilder result, String errorMess, String text )
    {
        result.append ( "Error: " );
        result.append ( errorMess );
        result.append ( ".   Message: " );
        result.append ( text );
        result.append ( WCons.NEW_LINE );
    }

    public BookNode getCurrentNode ()
    {
        return currentNode;
    }

    public BookContent getBookContent ()
    {
        return bookContent;
    }


    public static void main ( String[] args )
    {
        BookNode        bookNode;
        TextToBookNode  handler;

        bookNode = new BookNode ( "bbb_01", null );

        bookNode.addEol ();
        bookNode.addText ( new TextObject ( "ttt ttt ttt 01" ) );
        bookNode.addText ( new TextObject ( "ttt ttt ttt 02" ) );
        bookNode.addEol ();
        bookNode.addText ( new TextObject ( "ttt ttt ttt 03" ) );
        bookNode.addText ( new TextObject ( "ttt ttt ttt 04" ) );
        bookNode.addEol ();
        bookNode.addEol ();
        bookNode.addEol ();

        System.out.println ( "book 1 = " + DumpTools.printBookNode ( bookNode ) );

        handler = new TextToBookNode ();
        handler.trim ( bookNode );
        System.out.println ( "book 2 = " + DumpTools.printBookNode ( bookNode ) );
    }

}
