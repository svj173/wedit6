package svj.wedit.v6.gui;

import svj.wedit.v6.Par;
import svj.wedit.v6.content.toolBar.BrowserToolBar;
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
import svj.wedit.v6.function.book.edit.paste.*;
import svj.wedit.v6.function.book.export.*;
import svj.wedit.v6.function.book.export.html.ConvertToHtmlFunction;
import svj.wedit.v6.function.book.export.html.SelectedToHtmlFunction;
import svj.wedit.v6.function.book.imports.doc.ImportBookFromDocFunction;
import svj.wedit.v6.function.book.imports.txt.ImportBookFromTxtFunction;
import svj.wedit.v6.function.book.imports.we1.ImportFromWe1Function;
import svj.wedit.v6.function.book.text.*;
import svj.wedit.v6.function.book.tree.GroupEditNodeTypeFunction;
import svj.wedit.v6.function.book.undo.RedoFunction;
import svj.wedit.v6.function.book.undo.UndoFunction;
import svj.wedit.v6.function.option.DecoratorFunction;
import svj.wedit.v6.function.option.changeIconSize.ChangeMenuIconSizeFunction;
import svj.wedit.v6.function.option.changeIconSize.ChangePanelIconSizeFunction;
import svj.wedit.v6.function.option.changeIconSize.ChangeToolBarIconSizeFunction;
import svj.wedit.v6.function.project.*;
import svj.wedit.v6.function.project.archive.ArchiveProjectFunction;
import svj.wedit.v6.function.project.create_new.NewProjectFunction;
import svj.wedit.v6.function.project.edit.book.*;
import svj.wedit.v6.function.project.edit.book.create.CreateBookFunction;
import svj.wedit.v6.function.project.edit.book.open.OpenBookFunction;
import svj.wedit.v6.function.project.edit.paste.CutProjectNodeFunction;
import svj.wedit.v6.function.project.edit.paste.PasteProjectNodeAfterFunction;
import svj.wedit.v6.function.project.edit.paste.PasteProjectNodeInFunction;
import svj.wedit.v6.function.project.edit.section.*;
import svj.wedit.v6.function.project.open.OpenProjectFunction;
import svj.wedit.v6.function.project.reopen.ReopenProjectFunction;
import svj.wedit.v6.function.project.sync.SyncProjectFunction;
import svj.wedit.v6.function.service.search.SimpleSearchFunction;
import svj.wedit.v6.function.statistic.StatAllBookFunction;
import svj.wedit.v6.function.statistic.StatAllOpenFunction;
import svj.wedit.v6.function.statistic.StatEditBookInfoFunction;
import svj.wedit.v6.function.system.MemoryCheckFunction;
import svj.wedit.v6.function.text.*;
import svj.wedit.v6.gui.menu.WEMenuItem;
import svj.wedit.v6.gui.panel.WorkPanel;
import svj.wedit.v6.gui.tree.TreePanel;
import svj.wedit.v6.manager.FunctionManager;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.function.Function;

/**
 * Создатель основных ГУИ элементов.
 * <BR/> Выделен для того чтобы основные элементы создавались в одном месте, так легче доабвлять новый функционал.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 05.11.20 16:29
 */
public class GuiCreator {

    /**
     * Создать общий Тулбар.
     */
    public static BrowserToolBar createToolBar()
    {
        BrowserToolBar toolbar = new BrowserToolBar(); 

        // Наполнить функциями
        toolbar.addFunction ( FunctionId.SAVE_ALL_PROJECTS );
        toolbar.addFunction ( FunctionId.SAVE_ABSOLUTE_ALL_PROJECTS );
        toolbar.addFunction ( FunctionId.ZIP_ALL_PROJECTS );

        return toolbar;
    }


    /**
     * Меню на дереве Проекта, выскакивающе по Правой Кнопке Мыши.
     *
     * @param projectPanel Панель дерева Проекта (Сборника).
     */
    public static void createProjectPopUpMenu ( TreePanel<Project> projectPanel )
    {
        WEMenuItem menuItem;
        Function function;

        function    = Par.GM.getFm().get ( FunctionId.ADD_SECTION_AFTER );
        menuItem    = new AddSectionPopupMenu( function, false );
        //menuItem.addActionListener ( function );
        //menuItem.setText ( function.getName() );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.ADD_SECTION_IN );
        menuItem    = new AddSectionPopupMenu ( function, true );
        projectPanel.addPopupMenu ( menuItem );

        // -------------- separator -------------
        projectPanel.addPopupSeparator();

        // Копировать-Вырезать-Вставить

        function    = Par.GM.getFm().get ( FunctionId.MOVE_BOOK );
        //menuItem    = new BookPopupMenu ( function );
        // Меню доступно как для Сектора, так и для Книги
        menuItem    = new WEMenuItem ( function.getId().toString(), function.getIcon ( Par.MENU_ICON_SIZE ) );
        menuItem.addActionListener ( function );
        menuItem.setText ( function.getName() );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.CUT_PROJECT_ELEMENT );
        //menuItem    = new BookPopupMenu ( function );
        // Меню доступно как для Сектора, так и для Книги
        menuItem    = new WEMenuItem ( function.getId().toString(), function.getIcon ( Par.MENU_ICON_SIZE ) );
        menuItem.addActionListener ( function );
        menuItem.setText ( function.getName() );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.PASTE_PROJECT_ELEMENT_AFTER );
        // Меню доступно как для Сектора, так и для Книги
        menuItem    = new WEMenuItem ( function.getId().toString(), function.getIcon ( Par.MENU_ICON_SIZE ) );
        menuItem.addActionListener ( function );
        menuItem.setText ( function.getName() );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.PASTE_PROJECT_ELEMENT_IN );
        // Меню доступно как для Сектора, так и для Книги
        menuItem    = new WEMenuItem ( function.getId().toString(), function.getIcon ( Par.MENU_ICON_SIZE ) );
        menuItem.addActionListener ( function );
        menuItem.setText ( function.getName() );
        projectPanel.addPopupMenu ( menuItem );


        // -------------- separator -------------
        projectPanel.addPopupSeparator();

        function    = Par.GM.getFm().get ( FunctionId.EDIT_SECTION );
        menuItem    = new AddSectionPopupMenu ( function, true );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.DELETE_SECTION );
        menuItem    = new AddSectionPopupMenu ( function, false );
        projectPanel.addPopupMenu ( menuItem );

        // -------------- separator -------------
        projectPanel.addPopupSeparator();

        function    = Par.GM.getFm().get ( FunctionId.CREATE_BOOK );
        menuItem    = new AddSectionPopupMenu ( function, true );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.EDIT_BOOK_TITLE );
        menuItem    = new BookPopupMenu( function );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.OPEN_BOOK );
        menuItem    = new BookPopupMenu ( function );
        projectPanel.addPopupMenu ( menuItem );
        projectPanel.setDoubleClickAction ( function );

        function    = Par.GM.getFm().get ( FunctionId.DELETE_BOOK );
        menuItem    = new BookPopupMenu ( function );
        projectPanel.addPopupMenu ( menuItem );

        /* Не исп
        function    = Par.GM.getFm().get ( FunctionId.MOVE_BOOK );
        menuItem    = new BookPopupMenu ( function );
        projectPanel.addPopupMenu ( menuItem );
        */

        projectPanel.addPopupSeparator();

        // imports
        function    = Par.GM.getFm().get ( FunctionId.IMPORT_FROM_WE1_BOOK );
        menuItem    = new AddSectionPopupMenu ( function, true );
        projectPanel.addPopupMenu ( menuItem );

        function    = Par.GM.getFm().get ( FunctionId.IMPORT_FROM_TXT );
        menuItem    = new AddSectionPopupMenu ( function, true );
        projectPanel.addPopupMenu ( menuItem );
    }


    /**
     * Добавить функции в тул-бар Сборника.
     * 
     * @param projectsPanel Панель Сборника.
     */
    public static void createProjectToolBar(WorkPanel<TreePanel<Project>> projectsPanel) {
        projectsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.OPEN_PROJECT ) );
        //projectsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.MOVE_BOOK ) );
        projectsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.CLOSE_PROJECT ) );
        projectsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.CUT_PROJECT_ELEMENT ) );
        projectsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.PASTE_PROJECT_ELEMENT_AFTER ) );
        projectsPanel.addIconFunction ( Par.GM.getFm().get ( FunctionId.PASTE_PROJECT_ELEMENT_IN ) );
    }

    /**
     * Прописать все функции Системы.
     *
     * @param fm Менеджер функций.
     */
    public static void initFunctions ( FunctionManager fm )
    {
        // ---------------- Project ---------------
        fm.add ( new SyncProjectFunction()     );
        fm.add ( new ArchiveProjectFunction()  );
        fm.add ( new NewProjectFunction()      );
        fm.add ( new OpenProjectFunction()     );
        fm.add ( new CloseProjectFunction()    );
        fm.add ( new ReopenProjectFunction()   );
        fm.add ( new SaveAllProjectsFunction() );
        fm.add ( new SaveAbsoluteAllProjectsFunction() );
        fm.add ( new ZipAllProjectsFunction()  );

        fm.add ( new AddSectionAfterFunction() );
        fm.add ( new AddSectionInFunction()    );
        fm.add ( new EditSectionFunction()     );
        fm.add ( new EditBookTitleFunction()   );
        fm.add ( new EditBookParamsFunction()  );
        fm.add ( new DeleteSectionFunction()   );

        fm.add ( new CreateBookFunction()      );
        fm.add ( new OpenBookFunction()        );
        fm.add ( new DeleteBookFunction()      );

        // Перенос книги или раздела.
        fm.add ( new MoveBookFunction()                 );
        fm.add ( new CutProjectNodeFunction()           );
        fm.add ( new PasteProjectNodeAfterFunction()    );
        fm.add ( new PasteProjectNodeInFunction()       );

        // ---------------- Book -----------------------

        fm.add ( new ReloadBookFunction()   );
        fm.add ( new RewriteBookTreeFunction()     );
        fm.add ( new CheckNodeIdFunction()         );

        // - элементы книги
        fm.add ( new AddBookNodeInFunction()       );
        fm.add ( new AddBookNodeAfterFunction()    );
        fm.add ( new CopyBookNodeFunction()        );
        fm.add ( new CutBookNodeFunction()         );
        fm.add ( new PasteBookNodeInFunction()     );
        fm.add ( new PasteBookNodeAfterFunction()  );
        fm.add ( new DeleteBookNodeFunction()      );
        fm.add ( new EditBookNodeFunction()        );


        // ---------------- Контекстное меню на дереве книги -----------------------
        fm.add ( new GroupEditNodeTypeFunction()   );

        // - Описание элементов книги
        fm.add ( new EditDescElementFunction()     );
        fm.add ( new EditDescElementsFunction()    );

        // - Tools For Current Book
        fm.add ( new TrimBookTextFunction()        );
        fm.add ( new ReplaceTextFunction()         );
        fm.add ( new ReplaceBlockTextFunction()    );
        fm.add ( new FindDotTextFunction()         );
        fm.add ( new FindZapTextFunction()         );

        // ------------------ Options ------------------------
        fm.add ( new DecoratorFunction()               );
        fm.add ( new ChangeToolBarIconSizeFunction()   );
        fm.add ( new ChangePanelIconSizeFunction()     );
        fm.add ( new ChangeMenuIconSizeFunction()      );

        // ---------------- Text -----------------------
        fm.add ( new OpenTextFunction()        );
        fm.add ( new SaveTextFunction()        );
        fm.add ( new CloseTextFunction()       );
        fm.add ( new CloseAllTextTabFunction() );
        fm.add ( new CloseAllTextTabExcludeCurrentFunction() );
        fm.add ( new InfoElementTypeFunction() );
        fm.add ( new SelectElementFunction()   );
        fm.add ( new SelectAlignFunction()     );
        fm.add ( new SelectStyleFunction()     );
        fm.add ( new SelectImageFunction()     );
        fm.add ( new UndoFunction()            );
        fm.add ( new RedoFunction()            );
        fm.add ( new ViewTreeFromTabFunction() );
        fm.add ( new InsertTableFunction()     );
        fm.add ( new SetAllTextAsSimpleFunction()     );

        // ---------------- Statistic -----------------------
        fm.add ( new StatAllOpenFunction()         );
        fm.add ( new StatAllBookFunction()         );
        fm.add ( new StatEditBookInfoFunction()    );

        // ---------------- Convert -----------------------
        fm.add ( new SaveAsRTFSelectFunction()     );
        fm.add ( new ConvertToRtfFunction()        );
        fm.add ( new ConvertToHtmlFunction()       );
        fm.add ( new SelectedToHtmlFunction()      );
        fm.add ( new ConvertContentToRtfFunction() );
        fm.add ( new ConvertToDoc() );
        fm.add ( new ConvertToDocForLitres() );
        fm.add ( new ConvertToTxt() );
        fm.add ( new ConvertToFB2() );
        fm.add ( new ConvertSelectToFB2() );

        fm.add ( new SimpleSearchFunction() );

        // ----------------- System -----------------------
        fm.add ( new MemoryCheckFunction()     );

        // ----------------- Import -----------------------
        fm.add ( new ImportFromWe1Function()           );
        fm.add ( new ImportBookFromTxtFunction()       );
        fm.add ( new ImportBookFromDocFunction()       );

        fm.add ( new BookmarkFunction()       );
    }

}
