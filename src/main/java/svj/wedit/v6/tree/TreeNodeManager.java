package svj.wedit.v6.tree;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.book.export.obj.*;
import svj.wedit.v6.function.params.SimpleParameter;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.obj.WType;
import svj.wedit.v6.obj.book.BookNode;
import svj.wedit.v6.obj.book.TextObject;
import svj.wedit.v6.tools.Convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Пробегает по всем обьектам дерева и дергает TreeNodeProcessor.
 * <BR/> Исп в HTML
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 06.10.2016 15:43
 */
public class TreeNodeManager
{
    private final TreeNodeProcessor processor;
    private final BookNode[] bookNode;
    private final ConvertParameter cp;
    private final Collection<String> hiddenTypes;   // Список типов узлов, которые игнорируем.

    /* Нумерация для элементов (для Глав - своя). */
    private final Map<Integer,Integer> numbers;


    /**
     *  @param bookNode     Стартовые узлы одного уровня.
     * @param cp           Глобальный параметр конвертации.
     * @param action       Имя акции, для которой делаем преобразования. Это необходимо для тех функций, которые несколько раз пробегают по дереву.
     *                     Например, сначала для сбора оглавления, затем - для конвертации.
     * @param processor    Процессор конкретных преобразований.
     */
    public TreeNodeManager ( BookNode[] bookNode, ConvertParameter cp, String action, TreeNodeProcessor processor )
    {
        this.processor = processor;
        this.bookNode = bookNode;
        this.cp = cp;

        numbers     = new HashMap<Integer,Integer> ();


        // todo Получить список типов, которые игнорируем.
        Collection<SimpleParameter> types;

        types   = cp.getTypes();
        Log.l.info ( "Create content: types = %s", types );
        hiddenTypes = new ArrayList<String> ();
    }

    public void handle ()  throws WEditException
    {
        for ( BookNode node : bookNode )
        {
            processNode ( node, 0 );
        }

        // В самом конце - заключительный пинок. Для тех кто на самом деле только собирал данные. И теперь только скинет их в выходнйо поток.
        processor.finished ( cp );
    }

    private void processNode ( BookNode bookNode, int level )   throws WEditException
    {
        BookNode                bo;
        String                  phase, str, elementType, title;
        Collection<TextObject>  text;
        Collection<WTreeObj>    childs;
        int                     nodeLevel;
        TitleViewMode titleViewMode;
        ElementConvertParameter elementParam;
        TypeHandleType handleType;

        phase       = "start";     // шаг процесса - для отладки
        try
        {
            nodeLevel   = bookNode.getLevel();
            // Взять тип заголовка элемента - может быть NULL (т.е. work)
            elementType = bookNode.getElementType();
            //if ( elementType == null )  elementType = ""; // work
            Log.file.debug ( "---- nodeLevel = '%s'; elementType = '%s'; book title = %s", nodeLevel, elementType, bookNode.getName() );

            elementParam    = cp.getElementParam(nodeLevel);
            Log.file.debug ( "-- elementParam = %s", elementParam );
            if ( elementParam == null )  throw new WEditException ( null, "Не найден элемент описания уровня ", nodeLevel );

            // Проверить - игнорировать этот элемент?
            // if ( cp.ignoreElement(elementType) )  return;

            WType wType = bookNode.getBookContent().getBookStructure().getType(elementType);
            Log.file.info ( "Find: elementType = '%s'; wType = %s", elementType, wType );
            handleType = ConvertTools.getType ( elementType, cp.getTypes () );
            switch ( handleType )
            {
                case NOTHING:
                    return;
                case PRINT_LATER:
                    //writeStr ( fos, "<...>" );
                    return;
            }


            // ------------- Заголовок -------------
            phase       = "title";

            titleViewMode   = cp.getTitleViewType ( nodeLevel );
            Log.file.debug ( "------ title = %s; titleViewMode = '%s'", bookNode.getName(), titleViewMode );

            processor.title ( titleViewMode, bookNode.getName(), elementParam.getName(), getNumber(nodeLevel), level, cp );


            // ------------- Текст -------------
            phase   = "text";
            text    = bookNode.getText();
            if ( ( text != null ) && ( ! text.isEmpty() ) )
            {
                Log.file.debug ( "------ write text lines = %d", text.size() );
                processor.text ( text );
                /*
                // По идее - до и перед оглавлением необходимо собирать пустые строки, а выводить столько пустых, сколько задано.  -- ???
                for ( TextObject textObj : text )
                {
                    if ( textObj instanceof ImgTextObject )
                    {
                        // ---------------- IMAGE -------------------
                        // Скопировать картинку в директорию расположения html-файла
                        FileTools.copyFileToDir ( textObj.getText (), cp.getFileName () );
                        // Разместить в тексте тег ссылки на картинку (по идее в обьекте может быть и подпись для картинки).
                        writeStr ( fos, "<center><IMG src='", textObj.getText(), "' /></center>\n" );
                        // Иначе текст следующего заголовка пойдет прямо от иконки.
                        writeStr ( fos, BR );
                        // Сбрасываем флаг
                        previosIsTitle = false;
                    }
                    else
                    {
                        checkTextForTitle ( textObj );
                        str             = createHtmlText ( textObj, cp );
                        // проверка на наличие сигнальных символов, например ==.
                        checkText ( cp, str, bookNode.getFullPathAsTitles() );
                        writeStr ( fos, str );
                    }
                }
                */
            }
            //Log.file.debug ( "------ after text previosIsTitle = %b", previosIsTitle );

            // ------------- Дочерние элементы -------------
            phase   = "children";
            // Проверка на вложенные обьекты
            childs  = bookNode.getChildrens();
            Log.file.debug ( "------ childs = '%s'", childs );
            if ( childs != null )
            {
                int newLevel = level + 1;
                for ( WTreeObj obj : childs )
                {
                    bo  = ( BookNode ) obj;
                    processNode ( bo, newLevel );
                }
            }

        } catch ( WEditException we )         {
            throw we;
        } catch ( Exception e )         {
            str = Convert.concatObj ( "Системная ошибка. bookNode = '", bookNode, "'. Phase = ", phase, "\n Error : \n", e );
            Log.file.error ( str, e );
            throw new WEditException ( e, str );
        }
    }

    private String getNumber ( int nodeLevel )
    {
        String   result;
        Integer  i1;

        i1      = numbers.get ( nodeLevel );
        if ( i1 != null )
            i1 = i1 + 1;
        else
            i1 = 1;

        numbers.put ( nodeLevel, i1 );
        result  = i1.toString();
        return result;
    }


}
