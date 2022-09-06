package svj.wedit.v6.obj.book;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.gui.IId;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.Project;
import svj.wedit.v6.obj.TreeObjType;
import svj.wedit.v6.obj.WTreeObj;
import svj.wedit.v6.tools.BookTools;
import svj.wedit.v6.tools.Convert;
import svj.wedit.v6.tools.StringTools;
import svj.wedit.v6.tools.Utils;

import javax.swing.text.AttributeSet;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Деревянный обьект дерева книги (элемент книги).
 * <BR/> Входит в BookContent и является собственно содержимым всей книги.
 * <BR/>
 <bookNode name="Сказки-1">
      <node name="глава 0">
          <type>work</type>
      </node>
      <node name="глава 1">
          <type>work</type>
          <node name="el-1-01">
              <type>work</type>
          </node>
      </node>
      <node name="глава 2">
          <type>work</type>
          <annotation>
             Глава, в которой главный герой наконец-то что-то понимает.
          </annotation>
          <text>
              <str>Пролетев несколько метров, Сергей пулей влетел в мягкий сугроб, вскольз плечом больно ударившись обо что-то
                  твердое. И тот час тело обожгло ледянным холодом жесткого снега, накрывшего его с головой. Судорожно замахав
                  руками, он тут же попытался выбраться, но не смог - голова сильно кружилась, неимоверно тошнило, и от всего
                  этого он все никак не мог сориентироваться - где же здесь верх, а где - низ.
              </str>
              <eol/>

 * <BR/>
 * <BR/> User: svj
 * <BR/> Date: 19.08.2011 15:55:11
 */
public class BookNode  extends WTreeObj implements Comparable<BookNode>, IId
{
    private final List<BookNode> nodes;     // List - т.к. нужен get

    // text
    /** Текст данного фрагмента. */
    private     final Collection<TextObject>      text;
    /** Аннотация данного фрагмента. */
    //private     String      annotation;   -- есть уже
    //private     BookNode    parentNode;

    // тип - уже описан в BookElement
    private     String      type    = null;          // work, hidden

    /* Ссылка на книгу, которой принадлежит данный элемент книги. Ссылка содержится только в рутовом элементе BookNode. */
    private BookContent bookContent;

    /* Пример: Разное_1391411769437 */
    private String id;

    // BookElement здесь НЕ прописываем, т.к. легко рассинхронизироваться из-за переносов узлов выше или ниже уровня.


    public BookNode ( String name, BookNode parentNode )
    {
        // Убрать из титла все запрещенные для XML символы - " - ломают структуру в атрибуте name="..."
        if ( name != null )  name = name.replace ( '\"', '\'' );
        setName ( name );

        bookContent = null;

        nodes       = new ArrayList<BookNode>();
        text        = new ArrayList<TextObject>();

        // зачем? - какая-то путаница в парентах.
        setParent ( parentNode );
    }

    public BookNode clone ()
    {
        BookNode result;

        result  = new BookNode ( getName(), getParentNode() );
        result.setId ( getId() );

        // nodes
        for ( BookNode node : nodes )
            result.addBookNode ( node.clone() );

        // text
        for ( TextObject to : text )
            result.addText ( to.clone() );

        // annotation
        result.setAnnotation ( getAnnotation() );

        // type
        result.setElementType ( getElementType() );
        
        return result;
    }

    @Override
    public void toXml ( int level, OutputStream out ) throws WEditException
    {
        int     ic;
        String  tagName, str;

        try
        {
            if ( level == 1 ) tagName = "bookNode";
            else    tagName = "node";

            ic  = level + 1;

            // node
            str = getName();
            if ( str == null || str.isEmpty () )  str = "Error_"+Long.toString ( System.currentTimeMillis() );
            outTitle ( level, tagName, str, out );

            outTag ( ic, "id", getId(), out );

            // type
            str = getElementType();
            if ( (str != null) && ( ! str.isEmpty() ) )   outTag ( ic, "type", str, out );

            // annotation
            str = getAnnotation();
            //Log.file.info ( "-- BookNode = %s; annotation = [%s]", getName(), str );
            if ( (str != null) && ( ! str.isEmpty() ) )   outTag ( ic, "annotation", str, out );

            // text
            if ( ! text.isEmpty() )
            {
                outTitle ( ic, "text", out );
                for ( TextObject textObj : text )
                {
                    textObj.toXml ( ic, out );
                }
                endTag ( ic, "text", out );
            }

            // подглавы
            for ( BookNode node : nodes )
            {
                node.toXml ( ic, out );
            }

            endTag ( level, tagName, out );

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.file.error ( "err", e );
            throw new WEditException ( e, "Ошибка записи XML представления части\n книги '",getName(),"' в поток :\n", e );
        }
    }

    @Override
    public int getSize ()
    {
        int result;

        result  = getSize ( getName() );

        // nodes
        for ( BookNode node : nodes )
            result = result + node.getSize();

        // text
        for ( TextObject to : text )
            result = result + to.getSize();

        // annotation
        result = result + getSize(getAnnotation());

        return result;
    }

    public String toString()
    {
        StringBuilder result;
        result  = new StringBuilder();

        result.append ( "[ BookNode: name=" );
        result.append ( getName() );
        result.append ( "; level=" );
        result.append ( getLevel() );
        result.append ( "; type=" );
        result.append ( getElementType() );
        result.append ( "; id=" );
        result.append ( getId() );
        //result.append ( "; annotation='" );
        //result.append ( getAnnotation() );
        result.append ( "'; child_size=" );
        result.append ( getNodes().size() );
        result.append ( " ] " );

        result.append ( super.toString() );

        return result.toString();
    }

    public void addText ( String textStr, AttributeSet style )
    {
        TextObject  textObj;
        int         ic;

        ic = textStr.indexOf ( '\n' );
        //Log.l.debug ( "BookNode: addText. text = '%s'; eol ic = %d", textStr, ic );
        if ( ic >= 0 )
            textObj = new SlnTextObject ( textStr );   // текст с переводом строки в конце.
        else
            textObj = new TextObject ( textStr );
        //textObj.setText ( textStr );
        if ( style != null )  textObj.setStyle ( style );
        text.add ( textObj );
    }

    public void addText ( TextObject textObj )
    {
        text.add ( textObj );
    }

    public void addEol ()
    {
        text.add ( new EolTextObject() );
    }

    public Collection<TextObject> getText ()
    {
        return text;
    }

    // Сравнение по имени, кол-во вложений, аннотации.
    // По идее - лучше уникальный ИД который генерится при создании обьекта.
    // А полный путь - высчитывается, т.к. он может меняться.
    @Override
    public int compareTo ( BookNode bookNode )
    {
        int iName, iSect;

        if ( bookNode == null )  return 1;

        iName   = Utils.compareToWithNull ( getName(), bookNode.getName() );

        if ( iName == 0 )
        {
            iSect   = Utils.compareToWithNull ( getChildrens().size(), bookNode.getChildrens().size() );
            if ( iSect == 0 )
                return Utils.compareToWithNull ( getAnnotation(), bookNode.getAnnotation() );
            else
                return iSect;
        }
        else
            return iName;
    }

    public boolean equals ( Object obj )
    {
        if ( obj == null )  return false;
        if ( obj instanceof BookNode )
        {
            BookNode bookNode = (BookNode) obj;
            return compareTo ( bookNode ) == 0;
        }
        return false;
    }

    @Override
    public Collection<WTreeObj> getChildrens()
    {
        Collection<WTreeObj>    result;
        result  = new ArrayList<WTreeObj> ( nodes.size() );
        result.addAll ( nodes );
        return result;
    }

    @Override
    public TreeObjType getType ()
    {
        return TreeObjType.BOOK_NODE;
    }

    @Override
    public String getTreeIconFilePath ()
    {
        return Convert.concatObj ( "img/tree/", Par.TREE_ICON_SIZE, "/book.png" );
    }

    /**
     *
     * @param inum  Номер места в дереве, куда добавляем-вставляем обьект.
     * @param bookNode Собственно добавляемый обьект.
     */
    public void addBookNode ( int inum, BookNode bookNode )
    {
        //Log.l.debug ( "--- inum = ", inum );
        //Log.l.debug ( "--- bookNode = ", bookNode );
        //Log.l.debug ( "--- nodes = ", nodes );
        bookNode.setParent ( this );
        //bookNode.setLevel ( getLevel() + 1 );
        nodes.add ( inum, bookNode );
    }

    public void addBookNode ( BookNode bookNode )
    {
        bookNode.setParent ( this );
        nodes.add ( bookNode );
    }

    public List<BookNode> getNodes ()
    {
        return nodes;
    }

    public BookNode getParentNode ()
    {
        return (BookNode) getParent();
    }

    public void setElementType ( String type )
    {
        this.type = type;
    }

    public String getElementType ()
    {
        return type;
    }

    public void delete ( BookNode bookNode )
    {
        nodes.remove ( bookNode );
    }

    public int getChildSize ()
    {
        return nodes.size();
    }

    public void merge ( BookNode node )
    {
        setName ( node.getName() );
        setElementType ( node.getElementType() );
        setAnnotation ( node.getAnnotation() );
    }

    // Выдать полный путь на основе индексов дочерних элементов через разделитель - запятая.
    public String getFullPath ()
    {
        StringBuilder   result;
        BookNode        tn, tn2;
        int             ic;

        result  = new StringBuilder ( 32 );
        tn      = this;
        while ( tn != null  )
        {
            tn2 = tn.getParentNode();
            if ( tn2 == null )  break;
            ic  = tn2.getChildIndex ( tn );
            result.insert ( 0, Convert.concatObj ( ic, WCons.COMMA ) );
            //result.insert ( 0, ""+ic+',' );
            tn  = tn2;
        }
        return result.toString();
    }

    /**
     * Выдать полный путь как наименования родительских глав.
     * @return  Строка в виде "Название книги/Название главы/Название подглавы".
     */
    public String getFullPathAsTitles ()
    {
        StringBuilder   result;
        BookNode        tn;

        result  = new StringBuilder ( 32 );
        tn      = this;
        while ( tn != null  )
        {
            result.insert ( 0, Convert.concatObj ( tn.getName(), WCons.SEP ) );
            tn = tn.getParentNode();
        }
        return result.toString();
    }

    /**
     * Полное имя, с учетом Сборника, Сектора.
     * @return
     */
    public String getFullName ()
    {
        StringBuilder   result;
        Project project;

        result  = new StringBuilder ( 128 );

        project = getBookContent().getProject ();
        result.append ( project.getName() );
        result.append ( "|" );
        result.append ( getFullPathAsTitles() );

        return result.toString();
    }

    /* Выдать номер-индекс дочернего обьекта. */
    public int getChildIndex ( BookNode node )
    {
        int ic;

        ic  = 0;
        for ( BookNode bn : getNodes() )
        {
            if ( bn.equals ( node ) ) return ic;
            ic++;
        }
        return -1;
    }


    // По идее - лучше уникальный ИД который генерится при начальном создании обьекта.
    // А полный путь - высчитывается, т.к. он может меняться.
    @Override
    public String getId ()
    {
        //return getFullPath();
        if ( StringTools.isEmpty ( id ) )  id = BookTools.createBookNodeId ( getName() );
        return id;
    }

    public void setId ( String id )
    {
        this.id = id;
    }

    public BookNode getChildAt ( int index )
    {
        if ( (index < 0) || ( index >= getChildSize() ) )
            return null;
        else
            return nodes.get(index);
    }

    public BookNode getLastNode ()
    {
        if ( getChildSize() == 0 )
            return null;
        else
            return nodes.get(getChildSize()-1);
    }

    public void clear ()
    {
        super.clear(); // чистим имя и аннотацию
        nodes.clear();
        text.clear();
    }

    public void setBookContent ( BookContent bookContent )
    {
        this.bookContent = bookContent;
    }

    /* Выдать реальную книгу. Т.е. пробегаемся по парентам до самого верха.
       А в корневом узле - находится ссылка на BookContent. */
    public BookContent getBookContent ()
    {
        BookNode node;

        if ( bookContent != null )
        {
            return bookContent;
        }
        else
        {
            node = getParentNode();
            if ( node != null )
                return node.getBookContent();
            else
                return null;
        }
    }

}
