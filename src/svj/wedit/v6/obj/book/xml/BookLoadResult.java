package svj.wedit.v6.obj.book.xml;


import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Hashtable;
import java.util.Properties;


/**
 * Результат загрузки XML книги и ее распарсивания.
 * <BR> Результирующие обьекты:
 * <LI> Книга - DefaultMutableTreeNode </LI>
 * <LI> Обьект со спсиком открытых страниц. </LI>
 * <LI> Обьект с динамическими параметрами функций типа 'Книга'. </LI>
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 24.01.2012 9:13:10
 */
public class BookLoadResult
{
    private DefaultMutableTreeNode book;
    private Properties openChapters;
    private Properties  openContent;
    private Hashtable functionParams;
    private String      codePage;
}
