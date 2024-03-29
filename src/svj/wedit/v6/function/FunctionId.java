package svj.wedit.v6.function;


/**
 * Идентификаторы функций.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 15.07.2011 16:34:58
 */
public enum FunctionId
{
    // Project
    NEW_PROJECT, OPEN_PROJECT, CLOSE_PROJECT, REOPEN_PROJECT, SYNC_PROJECT, ARCHIVE_PROJECT,
    SAVE_ALL_PROJECTS, SAVE_ABSOLUTE_ALL_PROJECTS, ZIP_ALL_PROJECTS,
    ADD_SECTION_AFTER, ADD_SECTION_IN, DELETE_SECTION, EDIT_SECTION,
    CREATE_BOOK, OPEN_BOOK, DELETE_BOOK, RELOAD_BOOK, CLOSE_BOOK, EDIT_BOOK_TITLE, EDIT_BOOK_PARAMS,
    ADD_ELEMENT_IN, ADD_ELEMENT_AFTER, COPY_ELEMENT, PASTE_ELEMENT_IN, PASTE_ELEMENT_AFTER, CUT_ELEMENT, DELETE_ELEMENT, EDIT_ELEMENT, // Элемент книги (BookNode)
    EDIT_DESC_ELEMENT,
    EDIT_DESC_ALL_ELEMENTS,
    DECORATOR, CHANGE_TOOL_BAR_ICON_SIZE, CHANGE_PANEL_ICON_SIZE, CHANGE_MENU_ICON_SIZE, CHANGE_TEXT_SIZE,
    MOVE_BOOK,
    // Копировать-Вырезать-Вставить (Секцию, Книгу, либо несколько, но однородных для верхнего уровня)
    COPY_PROJECT_ELEMENT, PASTE_PROJECT_ELEMENT_IN, PASTE_PROJECT_ELEMENT_AFTER, CUT_PROJECT_ELEMENT,

    // Book tree - toolbar
    VIEW_BOOK_FROM_SOURCE, EDIT_NODE_TYPE, REWRITE_BOOK_TREE, CHECK_NODE_ID,

    // Text
    OPEN_TEXT, CLOSE_TEXT, CLOSE_ALL_TEXT, CLOSE_ALL_TEXT_EXCLUDE_CURRENT, SAVE_TEXT,
    STAT_OPEN,      // меню - Статистика / Инфа обо всех открытых
    STAT_BOOK,      // меню - Статистика / Список книг
    STAT_BOOK_EDIT,      // меню - Статистика по редактированию Эпизодов. Смотреть исправления за - день, неделю, месяц...
    TEXT_INFO_ELEMENT, TEXT_SELECT_ELEMENT, TEXT_SELECT_ALIGN, TEXT_SELECT_STYLE, SELECT_IMAGE,
    UNDO_TEXT, REDO_TEXT, VIEW_ELEMENT_FROM_SOURCE,
    INSERT_TABLE,

    // Convert
    CONVERT_SELECTION_TO_RTF, CONVERT_SELECTION_TO_RTF_2, CONVERT_TO_HTML, CONVERT_SELECTED_TO_HTML,
    CONVERT_CONTENT_TO_RTF, CONVERT_TO_DOC, CONVERT_TO_DOC_LITRES, CONVERT_TO_TXT, CONVERT_TO_FB2, CONVERT_SELECTED_TO_FB2,

    // Import
    IMPORT_FROM_WE1_BOOK, IMPORT_FROM_TXT, IMPORT_FROM_DOC,

    // System
    MEMORY_CHECK, SEARCH,

    // Tools For Current Book
    TRIM_BOOK_TEXT, REPLACE_BOOK_TEXT, BLOCK_REPLACE,
    SET_ALL_TEXT_AS_SIMPLE, FIND_LOW_POINT, FIND_ZAP,

    BOOKMARK
}
