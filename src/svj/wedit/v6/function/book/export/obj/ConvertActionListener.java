package svj.wedit.v6.function.book.export.obj;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.listener.WActionListener;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.DialogTools;
import svj.wedit.v6.tools.StringTools;
import svj.wedit.v6.util.Buffer;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Слушатель на выборке закладки в диалоге настроек параметров конвертирования.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 10.10.2013 19:14
 */
public class ConvertActionListener  extends WActionListener
{
    private ConvertDialog convertDialog;


    public ConvertActionListener ( ConvertDialog convertDialog )
    {
        super ( "ConvertActionListener" );

        this.convertDialog = convertDialog;
    }

    @Override
    public void handleAction ( ActionEvent event ) throws WEditException
    {
        ConvertAction   action;
        String          cmd;
        ConvertDialog   dialog;
        Object          eventSource;
        ConvertParameter currentParameter;

        Log.l.debug ( "Start (convertAction). event = ", event );

        cmd     = event.getActionCommand();

        try
        {
            action  = ConvertAction.valueOf ( cmd );

            // Получить текущий обьект
            currentParameter    = convertDialog.getCurrentBookmark();

            switch ( action )
            {
                case CONVERT_LIST_EDIT:
                    // Показать диалог
                    ConvertListDialog convertListDialog = new ConvertListDialog ( convertDialog, "Список видов конвертаций." );
                    convertListDialog.showDialog();
                    //throw new WEditException ( "Не реализована!" );
                    break;

                case COPY:  // Скопировать выделенный в общий буфер.
                    copyBookmark();
                    break;

                case PASTE:  // Вставить в конец из общего буфера. Предварительно запрашивает имя букмарки.
                    pasteBookmark();
                    break;

                case CREATE:
                    createNewBookmark();
                    break;

                case EDIT:
                    convertDialog.editBookmark ( currentParameter );
                    break;

                case SAVE:
                    convertDialog.saveBookmarkParams ( currentParameter );
                    break;

                case CANCEL:
                    convertDialog.cancelBookmarkParams ( currentParameter );
                    break;

                case DELETE:
                    // переспрос - Component parentFrame, Object msg, String buttonNameOk, String buttonNameCancel  - "Удаление"
                    int ic = DialogTools.showConfirmDialog ( convertDialog, "Вы действительно желаете удалить '"+currentParameter.getName()+"' ?", "Удалить", "Отменить" );
                    if ( ic == 0 ) convertDialog.deleteBookmarkParams ( currentParameter );
                    break;

                case SELECT_NEW_BOOKMARK:  // изменился обьект в списке закладок
                    // Перерисовываем рабочие панели
                    convertDialog.showWorkPanel ( currentParameter );
                    break;
            }

        } catch ( WEditException we )         {
            throw we;
        } catch ( Exception e )         {
            Log.l.error ( Convert.concatObj ( "Системная ошибка обработки команды '", cmd, "'." ), e);
            throw new WEditException ( e, "Системная ошибка обработки команды '", cmd, "' :\n", e );
        }
    }

    private void copyBookmark ()  throws WEditException
    {
        ConvertParameter cp;

        // Взять выделенную
        cp = convertDialog.getResult ();

        if ( cp == null )
        {
            //DialogTools.showError ( "Ничего не выбрано для копирования!", "Ошибка копирования" );
            throw new WEditException ( "Ничего не выбрано для копирования!" );
        }
        else
        {
            // Сохранить в буфер
            Buffer.setBuffer ( cp.clone() );
        }
    }

    private void pasteBookmark ()  throws WEditException
    {
        String           name;
        ConvertParameter cp;
        Object           obj;

        // Взять из буфера
        obj = Buffer.getBuffer();
        if ( obj == null )
        {
            // Нет ничего - поругаться и выйти
            throw new WEditException ( "В буфере ничего нет!" );
        }
        else if ( obj instanceof ConvertParameter )
        {
            cp   = (ConvertParameter) obj;
        }
        else
        {
            // Иначе - ошибка. В буфере - не элемент дерева оглавления.
            // - поругаться и выйти
            throw new WEditException ( "В буфере - не элемент дерева вариантов конвертаций!" );
        }

        // Запрос нового имени
        // - Открыть диалог
        name = JOptionPane.showInputDialog ( convertDialog, "Название", cp.getName() );
        name = name.trim();

        if ( name.isEmpty() )
            throw new WEditException ( null, "Название не задано." );

        // Проверяем - вдруг такое имя уже есть?
        if ( checkDouble ( name ) )
        {
            cp.setName ( name );
            convertDialog.addBookmark ( cp );
            // OK. чистим буффер
            Buffer.clear();
        }
        else
        {
            throw new WEditException ( null, "Название '", name, "' уже существует." );
        }
    }

    private void createNewBookmark ()  throws WEditException
    {
        String name;

        // Открыть диалог
        name = JOptionPane.showInputDialog ( convertDialog, "Название" );

        if ( StringTools.isEmpty ( name ) )
            throw new WEditException ( null, "Название не задано." );

        name = name.trim();
        // Проверяем - вдруг такое имя уже есть?
        if ( checkDouble ( name ) )
        {
            // Создать новую закладку
            convertDialog.addNewBookmark ( name );
        }
        else
        {
            throw new WEditException ( null, "Название '", name, "' уже существует." );
        }
    }

    /**
     * Проверяем - вдруг такое имя уже есть?
     * @param name Новое имя.
     * @return   TRUE - нет такого имени
     */
    private boolean checkDouble  ( String name )
    {
        java.util.List<ConvertParameter> list = convertDialog.getBookmarkList();
        for ( ConvertParameter cp : list )
        {
            if ( cp.getName().equals ( name ) ) return false;
        }
        return true;
    }

}
