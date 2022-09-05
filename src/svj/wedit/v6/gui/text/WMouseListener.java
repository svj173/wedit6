package svj.wedit.v6.gui.text;


import svj.wedit.v6.Par;
import svj.wedit.v6.function.FunctionId;
import svj.wedit.v6.function.text.InfoElementTypeFunction;
import svj.wedit.v6.function.text.SelectElementFunction;
import svj.wedit.v6.gui.dialog.ShowImageDialog;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.function.Function;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Ловим щелчок мышки на тексте - чтобы изменять выпадашки стилей, цветов, элементов - для информации.
 *
 * - text
 * --- MouseListener.mouseClicked: charAttr = LeafElement(content) 72,78
 * --- MouseListener.mouseClicked: inputAttr =
 * --- MouseListener.mouseClicked: paragraphAttr = BranchElement(paragraph) 72,78
 * --- MouseListener.mouseClicked: logicalStyle = NamedStyle:default {bold=false,name=default,foreground=sun.swing.PrintColorUIResource[r=51,g=51,b=51],
 * italic=false,FONT_ATTRIBUTE_KEY=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12],size=12,family=Dialog,}
 *
 * - подглава
 * --- MouseListener.mouseClicked: charAttr = LeafElement(content) 39,49
 * --- MouseListener.mouseClicked: inputAttr = Alignment=0 FirstLineIndent=1.0 styleName=Подглава_work foreground=java.awt.Color[r=0,g=128,b=0] size=14 family=Dialog
 * --- MouseListener.mouseClicked: paragraphAttr = BranchElement(paragraph) 39,50
 * --- MouseListener.mouseClicked: logicalStyle = NamedStyle:default {bold=false,name=default,foreground=sun.swing.PrintColorUIResource[r=51,g=51,b=51],
 * italic=false,FONT_ATTRIBUTE_KEY=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12],size=12,family=Dialog,}
 *
 * Юзаем inputAttr
 *
 * Имя стиля
 * 1) глава: Глава_work
 * 2) текст: null
 *
 * source = JTextPane
 *
 *
 * Синонимы: кавалерия — конница, смелый — храбрый, идти — шагать.
 * Омонимы:  кран, ключ, бор.
 * Антонимы: правда — ложь, добрый — злой, говорить — молчать.
 *
 * <BR/>
 * <BR/>
 *
 * Работа с фотографиями-картинками
 *
 * 1) Выбранная фотография сохраняется в директории книги, в поддиректории image.
 * 2) Из этой фотографии создается фото меньшего размера (200 пикселей). И заносится в поддриектории книги image_small.
 * 3) Создание иконки - вычисляется что у нее больше - высота или ширина - и эту часть и приводят к 200 пикселям.
 * 4) При двойном клике (левой кнопкой) на уменьшенном изображении вытаскивается из текстовой панели
 * иконка и из нее берется description - именно там также сохраняется полное имя файла (если descr не задан - как в нашем случае).
 * (параметр имени файла почему-то недоступен в этом обьекте)
 * 5) Из имени файла удаляется текст "_small" и в отдельном диалоге загружается уже полное изображение - Для просмотра.
 * 6) Пример имени файла
 * /home/svj/Serg/stories/Test/sec02/image_small/masha_marta_2014-07-27.jpg
 * /home/svj/Serg/stories/Test/sec02/image/masha_marta_2014-07-27.jpg
 * 7) Ф-я загрузки изображений - SelectImageFunction
 *
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 07.02.2013 16:28
 */
public class WMouseListener implements MouseListener {

    // mousePressed + mouseReleased
    @Override
    public void mouseClicked(MouseEvent event) {
        Log.l.info("WMouseListener.mouseClicked: event = %s", event.getSource());


        if (event.isConsumed()) return;


        // Взять функцию отображения стиля и изменить в выпадашке занчение.
        Function function;

        function = Par.GM.getFrame().getTextsPanel().getFunction(FunctionId.TEXT_INFO_ELEMENT);
        if (function instanceof InfoElementTypeFunction) {
            InfoElementTypeFunction elementFunction = (InfoElementTypeFunction) function;
            //elementFunction.setCurrentStyle ( styleName );
            elementFunction.rewrite();
        }

        function = Par.GM.getFrame().getTextsPanel().getFunction(FunctionId.TEXT_SELECT_ELEMENT);
        Log.l.debug("----- function (SelectElementFunction) = %s", function);
        if (function instanceof SelectElementFunction) {
            SelectElementFunction elementFunction = (SelectElementFunction) function;
            elementFunction.setCurrentStyle(null); // reset
        }

        int count = event.getClickCount();
        Log.l.info("WMouseListener.mouseClicked: count = %d", count);
        if (count > 1) {
            // двойной щелчок
            // - смотрим - не кпратинка ли это
            String iconSmallFile = getIconFile(event);
            Log.l.info("WMouseListener.iconSmallFile: iconSmallFile = %s", iconSmallFile);
            if (iconSmallFile != null) {
                String bigFileName = iconSmallFile.replace("_small", "");
                Log.l.info("WMouseListener.iconSmallFile: bigFileName = %s", bigFileName);
                ShowImageDialog dialog = new ShowImageDialog(bigFileName);
                //dialog.init ( htmlText );
                dialog.showDialog();
            }

        }


    }

    private String getIconFile(MouseEvent e) {
        JTextPane text = (JTextPane) e.getSource();
        Point mouseLocation = new Point(e.getX(), e.getY());
        int pos = text.viewToModel(mouseLocation);

        if (pos >= 0) {
            try {
                Rectangle rect = text.modelToView(pos);
                int lowerCorner = rect.y + rect.height;
                if (e.getX() < rect.x && e.getY() < lowerCorner && pos > 0) {
                    pos--;
                }
            } catch (Exception ex) {
                Log.l.error("event = " + e, ex);
            }
            StyledDocument doc = text.getStyledDocument();
            Element element = doc.getCharacterElement(pos);
            AttributeSet style = element.getAttributes();
            Icon icon = StyleConstants.getIcon(style);
            if (icon != null) {
                Log.l.info("WMouseListener.mouseClicked: icon = %s", icon.getClass().getName());
                if (icon instanceof ImageIcon) {
                    ImageIcon image = (ImageIcon) icon;
                    return image.getDescription();
                }
            }
        }
        return null;
    }


    @Override
    public void mousePressed(MouseEvent e) {
        //Log.l.debug ( "--- MouseListener.mousePressed: event = ", e );
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //Log.l.debug ( "--- MouseListener.mouseReleased: event = ", e );
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //Log.l.debug ( "--- MouseListener.mouseEntered: event = ", e );
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //Log.l.debug ( "--- MouseListener.mouseExited: event = ", e );
    }

}
