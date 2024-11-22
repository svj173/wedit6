package svj.wedit.v6.gui.layout;


import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Менеджер компоновки вертикальных элементов.
 * <BR/> Прижимает их друг к другу.
 * <BR/> Используется в отображении вертикально расположенных кнопок - чтобы растягивал их по ширине.
 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 09.08.2016 16:19:30
 */
public class VerticalLayout implements LayoutManager2, Serializable
{
    /** вертикальные промежутки между компонентами. Но не от краев. */
    private final int  vgap;

    private final Collection<Component> compList = new ArrayList<Component> ();


    public VerticalLayout ()
    {
        this ( 0 );
    }

    public VerticalLayout ( int vgap )
    {
        this.vgap = vgap;
    }

    /**
     * Главный метод. Собственно компоновка gui-элементов.
     * <br/>  Здесь мы вычисляем вес координаты для физического расположения компонент.
     * <br/> Если требуется, изменяем и размеры компонент. Например, максимально растянуть по ширине (с учетом отступов по краям).
     * <br/>
     * @param parent   Панель. в которой мы располагаем свои компоненты.
     */
    @Override
    public void layoutContainer ( Container parent )
    {
        synchronized ( parent.getTreeLock () )
        {
            Insets      insets;
            int         x, y, width;
            Dimension   d;

            // вычисляем размеры, в которых нам придется компоновать свои компоненты.
            insets  = parent.getInsets();  // отступы по краям
            y       = insets.top;          // начальная координата по Y - расположения компонент. Увеличивается для последующих компонент.
            width   = parent.getWidth() - insets.right - insets.left;  // рабочая ширина панели, где компонуем.
            x       = insets.left;         // начальная координата по Х - расположения компонент. Не изменяется.

            for ( Component c : compList )
            {
                d   = c.getPreferredSize();
                // ширину задаем максимальную
                d.width = width;
                // изменяем размер компоненты
                c.setSize ( d );
                // Физически устанавливаем компоненту
                c.setBounds ( x, y, d.width, d.height );
                y   = y + d.height + vgap;;
            }
        }
    }

    @Override
    public void addLayoutComponent ( Component comp, Object constraints )
    {
        if ( comp != null )  compList.add ( comp );
    }

    // not use
    @Override
    public void addLayoutComponent ( String name, Component comp )     {    }

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    @Override
    public float getLayoutAlignmentX ( Container target )
    {
        //return 0.5f;
        return 0f;
    }

    @Override
    public float getLayoutAlignmentY ( Container target )
    {
        //return 0.5f;
        return 0f;
    }

    @Override
    public void invalidateLayout ( Container target )
    {
    }

    // not use
    @Override
    public void removeLayoutComponent ( Component comp )     {    }

    /**
     * Вычислить предпочтительный размер всех наших компонент.
     * @param parent  Панель, в которой располагаем наши компоненты.
     * @return    Размер.
     */
    @Override
    public Dimension preferredLayoutSize ( Container parent )
    {
        synchronized ( parent.getTreeLock() )
        {
            Dimension   result, d;
            Insets      insets;
            int         w, resultH;

            result      = new Dimension ( 0, 0 );
            w           = 0;
            resultH     = 0;

            for ( Component c : compList )
            {
                d   = c.getPreferredSize ();
                // ширина - берем максимальную  из всех минимальных
                w   = Math.max ( d.width, w );
                //result.height = Math.max ( d.height, result.height );
                // суммируем.
                resultH = resultH + d.height + vgap;
                //Logger.getInstance().debug ( "--- VerticalPairLayout.minimumLayoutSize: power = ", result );
            }

            insets = parent.getInsets();
            result.width  = w + insets.left + insets.right;
            result.height = resultH + insets.top + insets.bottom;

            //Logger.getInstance().debug ( "--- VerticalPairLayout.preferredLayoutSize: size = ", result );
            return result;
        }
    }

    // getMinimumSize
    @Override
    public Dimension minimumLayoutSize ( Container parent )
    {
        synchronized ( parent.getTreeLock() )
        {
            Dimension   result, d;
            Insets      insets;
            int         w, resultH;

            result  = new Dimension ( 0, 0 );
            w       = 0;
            resultH = 0;

            for ( Component c : compList )
            {
                d   = c.getMinimumSize();
                // ширину - берем максимальную  из всех минимальных
                w   = Math.max ( d.width, w );
                // высоту суммируем.
                resultH = resultH + d.height + vgap;
            }

            insets = parent.getInsets();
            result.width  = w + insets.left + insets.right;
            result.height = resultH + insets.top + insets.bottom;

            return result;
        }
    }

    @Override
    public Dimension maximumLayoutSize ( Container target )
    {
        return new Dimension ( Integer.MAX_VALUE, Integer.MAX_VALUE );
    }

}
