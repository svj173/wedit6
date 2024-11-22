package svj.wedit.v6.gui.dialog;


import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.widget.ComboBoxWidget;
import svj.wedit.v6.gui.widget.IconWidget;
import svj.wedit.v6.obj.IName;

import javax.swing.*;

import java.io.File;


/**
 * Выбрать картинку.
 * <BR/> - размер иконки в тексте - треть страницы текста, четверть текста, 5, 6
 * - по высоте либо по ширине
 * - расположение на странице - по центру, справа, слева
 * - применить обтекание текста
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 28.10.2012 11:13:31
 */
public class LoadImageDialog  extends WDialog<File, Icon>
{
    public enum Size implements IName {
        S3("треть страницы текста"),
        S4("четверть страницы текста"),
        S5("пятая чать страницы текста"),
        S6("шестая чать страницы текста"),
        ;

        private final String title;

        Size(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String getName() {
            return getTitle();
        }
    }

    public enum SizeType implements IName {
        WIDTH("по горизонтали"),
        HIGHT("по вертикали");

        private final String title;

        SizeType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String getName() {
            return getTitle();
        }
    }

    // выборка картинки
    private IconWidget treeIconWidget;

    // выборка размера уменьшенной кратинки в тексте
    private ComboBoxWidget<Size> sizeWidget;
    private ComboBoxWidget<SizeType> sizeTypeWidget;

    // вид уменьшения - по вертикали или по горизонтали

    public LoadImageDialog ( File currentDir )
    {
        super ( "Загрузить картинку" );

        int width = 300;

        // Изображение
        treeIconWidget = new IconWidget ( "Иконка", true, currentDir );
        treeIconWidget.setTitleWidth ( width );
        addToNorth ( treeIconWidget );

        /*
        // Размер для уменьшения
        sizeWidget = new ComboBoxWidget<Size>
                ("Размер уменьшенного изображения", Size.values());
        sizeWidget.setComboRenderer(new INameRenderer());
        sizeWidget.setToolTip("Эта уменьшенная иконка будет показана в тексте");
        sizeWidget.setTitleWidth ( width );
        addToCenter(sizeWidget);

        // Вид уменьшения
        sizeTypeWidget = new ComboBoxWidget<SizeType>
                ("Тип уменьшения", SizeType.values());
        sizeTypeWidget.setComboRenderer(new INameRenderer());
        sizeTypeWidget.setTitleWidth ( width );
        addToSouth(sizeTypeWidget);
        */
    }
    
    /* Занести в диалог исходную директорию. */
    @Override
    public void init ( File initObject ) throws WEditException
    {
        //treeIconWidget.set
    }

    /* Выдать выбранный файл. NULL - ничего не выбрано. */
    @Override
    public Icon getResult () throws WEditException
    {
        return treeIconWidget.getValue();
    }

    public File getIconFile ()
    {
        return treeIconWidget.getIconFile();
    }

}
