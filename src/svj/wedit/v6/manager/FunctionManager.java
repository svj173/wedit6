package svj.wedit.v6.manager;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.function.FunctionGroup;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.book.ReloadBookFunction;
import svj.wedit.v6.function.book.TrimBookTextFunction;
import svj.wedit.v6.function.book.bookmark.BookmarkFunction;
import svj.wedit.v6.function.book.edit.DeleteBookNodeFunction;
import svj.wedit.v6.function.book.edit.EditBookNodeFunction;
import svj.wedit.v6.function.book.edit.desc.EditDescElementFunction;
import svj.wedit.v6.function.book.edit.desc.EditDescElementsFunction;
import svj.wedit.v6.function.book.edit.newNode.AddBookNodeAfterFunction;
import svj.wedit.v6.function.book.edit.newNode.AddBookNodeInFunction;
import svj.wedit.v6.function.book.edit.paste.CopyBookNodeFunction;
import svj.wedit.v6.function.book.edit.paste.CutBookNodeFunction;
import svj.wedit.v6.function.book.edit.paste.PasteBookNodeAfterFunction;
import svj.wedit.v6.function.book.edit.paste.PasteBookNodeInFunction;
import svj.wedit.v6.function.book.export.*;
import svj.wedit.v6.function.book.export.html.ConvertToHtmlFunction;
import svj.wedit.v6.function.book.export.html.SelectedToHtmlFunction;
import svj.wedit.v6.function.book.imports.doc.ImportBookFromDocFunction;
import svj.wedit.v6.function.book.imports.txt.ImportBookFromTxtFunction;
import svj.wedit.v6.function.book.imports.we1.ImportFromWe1Function;
import svj.wedit.v6.function.book.text.ReplaceBlockTextFunction;
import svj.wedit.v6.function.book.undo.RedoFunction;
import svj.wedit.v6.function.book.undo.UndoFunction;
import svj.wedit.v6.function.option.DecoratorFunction;
import svj.wedit.v6.function.option.changeIconSize.ChangeMenuIconSizeFunction;
import svj.wedit.v6.function.option.changeIconSize.ChangePanelIconSizeFunction;
import svj.wedit.v6.function.option.changeIconSize.ChangeToolBarIconSizeFunction;
import svj.wedit.v6.function.project.CloseProjectFunction;
import svj.wedit.v6.function.project.SaveAbsoluteAllProjectsFunction;
import svj.wedit.v6.function.project.SaveAllProjectsFunction;
import svj.wedit.v6.function.project.ZipAllProjectsFunction;
import svj.wedit.v6.function.project.archive.ArchiveProjectFunction;
import svj.wedit.v6.function.project.create_new.NewProjectFunction;
import svj.wedit.v6.function.project.edit.book.DeleteBookFunction;
import svj.wedit.v6.function.project.edit.book.EditBookParamsFunction;
import svj.wedit.v6.function.project.edit.book.EditBookTitleFunction;
import svj.wedit.v6.function.project.edit.book.MoveBookFunction;
import svj.wedit.v6.function.project.edit.book.create.CreateBookFunction;
import svj.wedit.v6.function.project.edit.book.open.OpenBookFunction;
import svj.wedit.v6.function.project.edit.section.AddSectionAfterFunction;
import svj.wedit.v6.function.project.edit.section.AddSectionInFunction;
import svj.wedit.v6.function.project.edit.section.DeleteSectionFunction;
import svj.wedit.v6.function.project.edit.section.EditSectionFunction;
import svj.wedit.v6.function.project.open.OpenProjectFunction;
import svj.wedit.v6.function.project.reopen.ReopenProjectFunction;
import svj.wedit.v6.function.project.sync.SyncProjectFunction;
import svj.wedit.v6.function.service.search.SimpleSearchFunction;
import svj.wedit.v6.function.statistic.StatAllBookFunction;
import svj.wedit.v6.function.statistic.StatAllOpenFunction;
import svj.wedit.v6.function.statistic.StatEditBookInfoFunction;
import svj.wedit.v6.function.system.MemoryCheckFunction;
import svj.wedit.v6.function.text.*;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;
import svj.wedit.v6.tools.Convert;

import java.util.HashMap;
import java.util.Map;


/**
 * Хранит все функции.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.07.2011 16:01:25
 */
public class FunctionManager
{
    // Hashtable - т.к. синхронизованный (SynchronizedMap, Map)    - надо ли?
    private final Map<FunctionId,Function> functions;


    public FunctionManager ()
    {
        functions   = new HashMap<FunctionId,Function>();

    }

    public Map<FunctionId, Function> getFunctions ()
    {
        return functions;
    }

    public void add ( Function function )
    {
        getFunctions().put ( function.getId(), function );
    }

    public Function get ( FunctionId functionId )
    {
        return getFunctions().get ( functionId );
    }

    /**
     * Закрыть все функции. Чтобы они навели порядок со своими
     * параметрами, перед их сохранением в динамических конфигах.
     *  Lang - взял текущий язык,
     * Reopen - последний открытый файл, и т.д.
     * А также скинули свои внутренние параметры в исходное состояние - при
     *  закрытии книги и открытии следующей книги.
     */
    public void closeAll ()
    {
        for ( Function function : getFunctions().values() )
        {
            function.close();
        }
    }

    /**
     * Подписывать функции согласно их желаниям.
     */
    public void signFunction () //throws WEditException
    {
        FunctionGroup  str;

        for ( Function function : functions.values() )
        {
            // Установить слушатель
            str = function.getListenerGroup();
            if ( str != null )
            {
                // Есть группа на которую хочет подписаться данная функция
                //cmd = function.getListenerCmd();
                addListener ( str, function );
            }
        }
    }

    /**
     * Добавить в функции заданной функциональной группы функцию в качестве листенера.
     *
     * @param functionGroup       Функциональная группа
     * @param listenerFunction    Функция, добавляемая как слушатель.
     */
    public void addListener ( FunctionGroup functionGroup, Function listenerFunction )
    {
        String      testList;
        int         ic;

        ic          = 0;     // for TEST
        testList    = "";    // for TEST

        for ( Function function : functions.values() )
        {
            if ( function.containGroup ( functionGroup ) )
            {
                // Функция принадлежит к данной группе - добавить в нее слушателя
                function.addListener ( listenerFunction );
                testList    = Convert.concatObj ( testList, "; ", function.getName() );
                ic++;
            }
        }
        Log.l.debug ( "Add listener '", listenerFunction.getName(), "' to ", ic, " functions (", testList, ").");
    }


    public void init ()
    {
        // ---------------- Project ---------------
        add ( new SyncProjectFunction()     );
        add ( new ArchiveProjectFunction()  );
        add ( new NewProjectFunction()      );
        add ( new OpenProjectFunction()     );
        add ( new CloseProjectFunction()    );
        add ( new ReopenProjectFunction()   );
        add ( new SaveAllProjectsFunction() );
        add ( new SaveAbsoluteAllProjectsFunction() );
        add ( new ZipAllProjectsFunction()  );

        add ( new AddSectionAfterFunction() );
        add ( new AddSectionInFunction()    );
        add ( new EditSectionFunction()     );
        add ( new EditBookTitleFunction()   );
        add ( new EditBookParamsFunction()  );
        add ( new DeleteSectionFunction()   );

        add ( new CreateBookFunction()      );
        add ( new OpenBookFunction()        );
        add ( new DeleteBookFunction()      );

        // Перенос книги или раздела.
        add ( new MoveBookFunction()        );

        // ---------------- Book -----------------------

        add ( new ReloadBookFunction()        );

        // - элементы книги
        add ( new AddBookNodeInFunction()       );
        add ( new AddBookNodeAfterFunction()    );
        add ( new CopyBookNodeFunction()        );
        add ( new CutBookNodeFunction()         );
        add ( new PasteBookNodeInFunction()     );
        add ( new PasteBookNodeAfterFunction()  );
        add ( new DeleteBookNodeFunction()      );
        add ( new EditBookNodeFunction()        );

        // - Описание элементов книги
        add ( new EditDescElementFunction()     );
        add ( new EditDescElementsFunction()    );

        // - Tools For Current Book
        add ( new TrimBookTextFunction()        );
        add ( new ReplaceTextFunction()         );
        add ( new ReplaceBlockTextFunction()    );

        // ------------------ Options ------------------------
        add ( new DecoratorFunction()               );
        add ( new ChangeToolBarIconSizeFunction()   );
        add ( new ChangePanelIconSizeFunction()     );
        add ( new ChangeMenuIconSizeFunction()      );

        // ---------------- Text -----------------------
        add ( new OpenTextFunction()        );
        add ( new SaveTextFunction()        );
        add ( new CloseTextFunction()       );
        add ( new CloseAllTextTabFunction() );
        add ( new InfoElementTypeFunction() );
        add ( new SelectElementFunction()   );
        add ( new SelectAlignFunction()     );
        add ( new SelectStyleFunction()     );
        add ( new SelectImageFunction()     );
        add ( new UndoFunction()            );
        add ( new RedoFunction()            );
        add ( new ViewTreeFromTabFunction() );
        add ( new InsertTableFunction()     );
        add ( new SetAllTextAsSimpleFunction()     );

        // ---------------- Statistic -----------------------
        add ( new StatAllOpenFunction()         );
        add ( new StatAllBookFunction()         );
        add ( new StatEditBookInfoFunction()    );

        // ---------------- Convert -----------------------
        add ( new SaveAsRTFSelectFunction()     );
        add ( new ConvertToRtfFunction()        );
        add ( new ConvertToHtmlFunction ()       );
        add ( new SelectedToHtmlFunction ()      );
        add ( new ConvertContentToRtfFunction() );
        add ( new ConvertToDoc() );
        add ( new ConvertToTxt() );

        add ( new SimpleSearchFunction() );

        // ----------------- System -----------------------
        add ( new MemoryCheckFunction()     );

        // ----------------- Import -----------------------
        add ( new ImportFromWe1Function()           );
        add ( new ImportBookFromTxtFunction()       );
        add ( new ImportBookFromDocFunction()       );

        add ( new BookmarkFunction()       );

    }

    /**
     *  Стартуем функции.
     *  @param errMsg Буфер сборки сообщений об ошибках. Отображается по окончанию инсталляции Редактора.
     */
    public void startAll ( StringBuilder errMsg )
    {
        for ( Function function : functions.values() )
        {
            try
            {
                function.start();
            } catch ( WEditException e )   {
                errMsg.append ( "Ошибка запуска функции '" );
                errMsg.append ( function.getName() );
                errMsg.append ( "' : " );
                errMsg.append ( e.getMessage() );
                errMsg.append ( "\n" );
                // Функцию выключить
                function.disable();
            }
        }
    }

}
