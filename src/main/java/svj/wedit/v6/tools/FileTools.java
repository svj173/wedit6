package svj.wedit.v6.tools;


import svj.wedit.v6.Par;
import svj.wedit.v6.WCons;
import svj.wedit.v6.exception.WEditException;
import svj.wedit.v6.logger.Log;
import svj.wedit.v6.obj.*;
import svj.wedit.v6.obj.book.BookContent;
import svj.wedit.v6.obj.book.BookTitle;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.zip.*;


/**
 * Сервисные утилиты по работе с файлами и их именами.
 * <BR>
 * <BR> User: Zhiganov
 * <BR> Date: 21.06.2010 14:19:32
 */
public class FileTools
{
    /**
     * Загрузить файл по ссылке из ресурса.
     * @param path
     * @param descr
     * @return
     */
    public static ImageIcon createImageFromUrl ( String path, String descr )
    {
        ImageIcon   result;
        URL         imgURL;

        try
        {
            imgURL = FileTools.class.getResource ( path);
            if (imgURL == null)
                result = null;
            else
            {
                if ( descr != null )
                    result = new ImageIcon ( imgURL, descr );
                else
                    result = new ImageIcon ( imgURL );
            }
        } catch ( Throwable t ) {
            Log.l.error ( "createImageFromUrl Error: path = "+path+"; descr = "+descr, t );
            result = createEmptyImage(16, 16);
        }
        return result;
    }

    public static ImageIcon createImageFromFileName ( String fileName, String descr )
    {
        ImageIcon   result;

        try
        {
            if ( descr != null )
                result = new ImageIcon ( fileName, descr );
            else
                result = new ImageIcon ( fileName );

        } catch ( Throwable t ) {
            Log.l.error ( "createImageFromFileName Error: fileName = "+fileName+"; descr = "+descr, t );
            result = createEmptyImage(16, 16);
        }
        return result;
    }

    /**
     * Создать белый рисунок заданного размера.
     * @param w  Ширина в пиксеклях.
     * @param h  Высота.
     * @return   Пустой рисунок.
     */
    public static ImageIcon createEmptyImage ( int w, int h )
    {
        ImageIcon       imageIcon = null;
        BufferedImage   resizedImg;
        Graphics2D      g2;

        try
        {
            resizedImg = new BufferedImage ( w, h, BufferedImage.TYPE_INT_ARGB );
            g2 = resizedImg.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);
            g2.dispose();

            imageIcon = new ImageIcon ( resizedImg );
            
        } catch(Throwable t) {
            Log.l.error ( "createEmptyImage Error: ", t );
        }
        return imageIcon;
    }

    /**
     * Копируем файл в диреткорию.
     * Если результирующая директория является файлом - взять парент, как директорию. Если отсутствует - пока не реализовал.
     * @param srcFile   Имя исходного файла. Абс или относительное.
     * @param targetDir Результирующая директория. Только как абс путь.
     * @throws WEditException Ошибки копирования.
     */
    public static String copyFileToDir ( String srcFile, String targetDir ) throws WEditException
    {
        String  absFileName;
        File    file;

        absFileName = FileTools.createFileName ( Par.MODULE_HOME, srcFile );
        file        = new File ( targetDir );
        if ( file.isFile () )  file = file.getParentFile();

        return copyFile ( new File(absFileName), file.getAbsolutePath() );
    }

    public static String copyFile ( File srcFile, String targetDir ) throws WEditException
    {
        FileInputStream     fis;
        FileOutputStream    fos;
        String              targetFileName;
        File                targetFile;
        byte[]              buf;
        int                 ic;

        //Log.l.debug ( "Start loadFile: Name = ", fileName );

        buf = new byte[16384];

        try
        {
            targetFileName  = Convert.concatObj ( targetDir, "/", srcFile.getName() );
            targetFile      = new File ( targetFileName );

            // Если уже есть такой файл - ничего не делать
            if ( targetFile.exists () )  return null;

            fis             = new FileInputStream ( srcFile );
            fos             = new FileOutputStream ( targetFileName );

            while ( (ic = fis.read ( buf )) != -1 )
            {
                fos.write ( buf, 0, ic );
            }

            fos.flush();
            fos.close ();
            fis.close();

        } catch ( Exception e )        {
            //LogWriter.file.error ( e, "Load file ERROR. codePage = '", codePage, "', fileName = '", fileName, "'" );
            throw new WEditException ( e, "Ошибка копирования файла '", srcFile, "' в '", targetDir, "' :\n", e );
        }
        //LogWriter.file.debug ( "load: Finish" );
        return targetFileName;
    }

    public static void closeFileStream ( OutputStream fos, String fileName )
    {
        if ( fos != null )
        {
            try
            {
                fos.close();
            } catch ( Exception e )                {
                Log.l.error ( Convert.concatObj ( "File '", fileName, "' close error." ), e);
            }
        }
    }

    public static String getExtension ( File f )
    {
        String  ext, s;
        int     i;

        ext = null;
        s   = f.getName();
        i   = s.lastIndexOf('.');

        if ( (i > 0) &&  ( i < (s.length() - 1) ) )
        {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }


    public static boolean deleteRecursive ( File path ) throws WEditException
    {
        if ( ! path.exists() )
            throw new WEditException ( null, "Нет такого файла '", path.getAbsolutePath(), "'." );

        boolean ret = true;

        if ( path.isDirectory () )
        {
            for ( File f : path.listFiles () )
            {
                ret = ret && deleteRecursive ( f );
            }
        }

        return ret && path.delete ();
    }

    /**
     * Удалить файл.
     * @param fileName Абсолютное имя файла.
     */
    public static boolean deleteFile ( String fileName ) // throws WEditException
    {
        File file;
        file = new File ( fileName );
        return file.delete();
    }

    /**
     * Создать полное имя файла - с абсолютным путем.
     *
     * @param fileName Имя файла
     * @return Полное имя файла
     */
    public static String createFileName ( String fileName )
    {
        if ( fileName == null ) return null;

        if ( fileName.startsWith ( "/" ) || ( fileName.indexOf ( ':' ) > 0 ) )
        {
            // Это абсолютный путь - взять как есть
            return fileName;
        }
        else
        {
            // Добавить абс путь
            String path = Par.MODULE_HOME;

            // добавить разделитель - если необходимо
            String sep = File.separator;
            if ( path.endsWith ( "/" ) || path.endsWith ( "\\" ) ) sep = "";
            return path + sep + fileName;
        }
    }

    public static String createFileName ( String path, String fileName )
    {
        if ( fileName == null ) return null;

        if ( fileName.startsWith ( "/" ) || ( fileName.indexOf ( ':' ) > 0 ) )
        {
            // Это абсолютный путь - взять как есть
            return fileName;
        }
        else
        {
            // Добавить абс путь

            // добавить разделитель - если необходимо
            String sep = File.separator;    // "/"
            if ( path.endsWith ( "/" ) || path.endsWith ( "\\" ) ) sep = "";
            return path + sep + fileName;
        }
    }

    /**
     * Прочитать файл с текстом. Выдать результат.
     * Если ошибка - Exception.
     */
    public static String loadFile ( String fileName ) throws WEditException
    {
        return loadFile ( fileName, null );
    }


    /**
     * Прочитать файл с текстом. Выдать результат.
     * Если ошибка - Exception.
     * @param fileName    имя файла
     * @param codePage    кодировка файла
     * @return            Текстовое содержимое файла
     * @throws WEditException ошибка чтения файла
     */
    public static String loadFile ( String fileName, String codePage ) throws WEditException
    {
        Log.file.debug ( "Start loadFile: Name = " + fileName );
        String result = "";

        try
        {
            // Загрузить файл
            File f = new File ( fileName );
            FileInputStream fis = new FileInputStream ( f );
            byte[] buf = new byte[( int ) f.length ()];
            fis.read ( buf );
            //
            if ( codePage == null )
                result = new String ( buf );
            else
                result = new String ( buf, codePage );

        } catch ( Exception e )        {
            Log.file.error ( Convert.concatObj ( "Load file ERROR. codePage = '", codePage, "', fileName = '", fileName, "'" ), e);
            throw new WEditException ( e, "Ошибка чтения файла '", fileName, "' с кодировкой '", codePage, "'" );
        }
        Log.file.debug ( "load: Finish" );

        return result;
    }

    public static Properties loadProperties ( String fileName ) throws WEditException
    {
        Properties result = new Properties ();
        FileInputStream fis;
        try
        {
            fis = new FileInputStream ( fileName );
            if ( fileName.endsWith ( "xml" ) )
            {
                // XML файл
                result.loadFromXML ( fis );
            }
            else
            {
                // Текстовый файл
                result.load ( fis );
            }
        } catch ( Exception e )         {
            //Log.file.error ()
            throw new WEditException ( "Load file '" + fileName + "' properties error :\n" + e, e );
        }
        return result;
    }

    /**
     * Прочитать ява-объект из файла.
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static Object loadObject ( String fileName ) throws Exception
    {
        Object result;
        FileInputStream fis;
        ObjectInputStream ois;

        fis = new FileInputStream ( fileName );
        ois = new ObjectInputStream ( fis );
        result = ois.readObject ();
        ois.close ();

        return result;
    }

    /**
     * Сохранить ява-объект в файле.
     *
     * @param fileName
     * @param object
     * @throws Exception
     */
    public static void save ( String fileName, Object object ) throws Exception
    {
        FileOutputStream fos;
        ObjectOutputStream oos;

        fos = new FileOutputStream ( fileName );
        oos = new ObjectOutputStream ( fos );

        //oos.writeInt(12345);
        oos.writeObject ( object );
        oos.close ();
    }

    public static void save ( String fileName, byte[] bytes ) throws Exception
    {
        FileOutputStream fos;

        fos = new FileOutputStream ( fileName );

        //oos.writeInt(12345);
        fos.write ( bytes );
        fos.close ();
    }

    /**
     * Сохранить текст в файле.
     *
     * @param fileName
     * @param text
     * @throws Exception
     */
    public static void save ( String fileName, String text ) throws Exception
    {
        FileWriter fw;

        fw = new FileWriter ( fileName );
        // Записать в основной
        fw.write ( text );
        fw.flush ();
        fw.close ();
    }

    
    public static void saveProps ( String fileName, Properties props ) throws WEditException
    {
        saveProps ( fileName, props, null );
    }

    public static void saveProps ( String fileName, Properties props, String title ) throws WEditException
    {
        OutputStream    os;
        File            file, folder;
        String          str;

        Log.file.debug ("Start");

        os = null;
        try
        {
            file   = new File(fileName);

            // создать папку (рекурсивно) если не существует
            folder = file.getParentFile ();
            if ( ! folder.exists() )          createFolder ( folder );

            os = new FileOutputStream (file);

            if ( fileName.endsWith ( "xml" ) )
            {
                // XML файл
                str = " User XML props";
                if ( title != null )  str = str + ": " + title;
                props.storeToXML ( os, str );
            }
            else
            {
                // Текстовый файл
                str = " User TXT props";
                if ( title != null )  str = str + ": " + title;
                props.store ( os, str );
            }
            os.flush ();
            os.close();
        } catch ( Exception e )         {
            FileTools.closeFileStream ( os, fileName );
            Log.file.error ("err", e );
            throw new WEditException ( "Системная ошибка сохранения данных в файле '" + fileName + "' :\n" + e, e );
        }
        Log.file.debug ("Finish");
    }

    public static void saveWithTmp ( File file, StringBuilder text ) throws Exception
    {
        FileWriter fw, tmp;
        String str;
        // Создать промежуточный
        str = file.getAbsoluteFile() + ".tmp";
        tmp = new FileWriter ( str );
        // Записать в промежуточный
        tmp.write ( text.toString () );
        tmp.close ();
        // Создать основной
        fw = new FileWriter ( file );
        // Записать в основной
        fw.write ( text.toString () );
        fw.close ();
        // TODO в конце работы промежуточный файл удалить
        //   - tempFile.deleteOnExit(); - удалится при закрытии java
    }

    /**
     * Запрашивает и осуществляет выборку имени файла и директории.
     *
     * @param frame            Родительский фрейм для диалогового окна. Может быть NULL.
     * @param currentDirectory Исходный файл. Может быть NULL.
     * @return File или NULL, если была отмена операции.
     */
    public static File selectFileName ( JFrame frame, File currentDirectory, JComponent additionalPanel )
    {
        File result;
        JFileChooser fc;
        int returnVal;

        result = null;
        if ( currentDirectory == null )
            fc = new JFileChooser ();
        else
            fc = new JFileChooser ( currentDirectory );

        if ( additionalPanel != null )  fc.setAccessory ( additionalPanel );

        returnVal = fc.showOpenDialog ( frame );

        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            result = fc.getSelectedFile ();
        }

        return result;
    }

    public static File selectFileName ( JFrame frame, File currentDirectory )
    {
        return selectFileName ( frame, currentDirectory, null );
    }

    public static synchronized boolean createFolder ( final File folder )
    {
        if ( !folder.exists () && createFolder ( folder.getParentFile () ) )
            folder.mkdir ();

        return folder.exists ();
    }

    /**
     * Упаковать все файлы из заданной директории.
     *
     * @param sourceDir     Исходная директория
     * @param targetZipFile ЗИП файл. Например "/u/arhive/myfiles.zip"
     */
    public static synchronized void zipAllFiles ( String sourceDir, String targetZipFile )
    {
        BufferedInputStream origin = null;
        FileOutputStream dest;
        ZipOutputStream out = null;
        FileInputStream fi;
        ZipEntry entry;
        byte data[];
        File f;
        String files[];
        int i, size;

        size = 2048;

        try
        {
            dest = new FileOutputStream ( targetZipFile );
            out = new ZipOutputStream ( new BufferedOutputStream ( dest ) );
            //out.setMethod(ZipOutputStream.DEFLATED);
            data = new byte[size];
            // get a list of files from current directory
            f = new File ( sourceDir );
            files = f.list ();

            for ( i = 0; i < files.length; i++ )
            {
                //System.out.println ( "Adding: " + files[i] );
                fi = new FileInputStream ( files[ i ] );
                origin = new BufferedInputStream ( fi, size );
                entry = new ZipEntry ( files[ i ] );
                out.putNextEntry ( entry );
                int count;
                while ( ( count = origin.read ( data, 0, size ) ) != -1 )
                {
                    out.write ( data, 0, count );
                }
                origin.close ();
            }
            out.close ();
        } catch ( Exception e )
        {
            try
            {
                if ( origin != null ) origin.close ();
            } catch ( Exception ex1 )
            {
            }
            try
            {
                if ( out != null ) out.close ();
            } catch ( Exception ex1 )
            {
            }
            //e.printStackTrace ();
        }
    }

    /**
     * Архивировать заданный файл.
     * Файл зипуется в заданную директорию, после чего - удаляется.
     *
     * @param sourceFile Исходный файл
     * @param targetDir  Директория для хранения архивов
     * @throws java.io.IOException Ошибки работы
     */
    public static synchronized void archiveFile ( File sourceFile, String targetDir )
            throws IOException
    {
        BufferedInputStream origin = null;
        FileOutputStream dest;
        ZipOutputStream out = null;
        FileInputStream fi;
        ZipEntry entry;
        byte data[];
        File ft;
        String arhiveFileName, sourceFileName;
        int size;

        size = 2048;

        try
        {
            // Проверить директорию архивирования
            ft = new File ( targetDir );
            if ( !createFolder ( ft ) )
                throw new IOException ( "Ошибка архивирования файла '" + sourceFile + "'. Ошибка создания директории для архивов '" + targetDir + "'." );

            if ( sourceFile == null )
                throw new IOException ( "Ошибка архивирования файла '" + sourceFile + "'. Файл не задан." );
            if ( !sourceFile.exists () )
                throw new IOException ( "Ошибка архивирования файла '" + sourceFile + "'. Файл не существует." );
            if ( sourceFile.isDirectory () )
                throw new IOException ( "Ошибка архивирования файла '" + sourceFile + "'. Это директория." );

            sourceFileName = sourceFile.getCanonicalPath ();
            // Создать имя архивного файла
            arhiveFileName = targetDir + File.separator + sourceFile.getName () + ".zip";
            Log.file.info ( "Arhive file  '" + sourceFileName + "' to '" + arhiveFileName + "'." );
            dest = new FileOutputStream ( arhiveFileName );
            out = new ZipOutputStream ( new BufferedOutputStream ( dest ) );
            //out.setMethod(ZipOutputStream.DEFLATED);
            data = new byte[size];

            fi = new FileInputStream ( sourceFile );
            origin = new BufferedInputStream ( fi, size );
            entry = new ZipEntry ( sourceFileName );
            out.putNextEntry ( entry );

            int count;
            while ( ( count = origin.read ( data, 0, size ) ) != -1 )
            {
                out.write ( data, 0, count );
            }
            origin.close ();
            out.close ();

            // Удалить исходный файл
            sourceFile.delete ();

        } catch ( Exception e )         {
            try
            {
                if ( origin != null ) origin.close ();
            } catch ( Exception ex1 )            {
                Log.file.error ( ex1 );
            }
            try
            {
                if ( out != null ) out.close ();
            } catch ( Exception ex1 )            {
                Log.file.error ( ex1 );
            }
            Log.file.error ( e );
            if ( e instanceof IOException )
                throw ( IOException ) e;
            else
                throw new IOException ( "Ошибка архивирования файла '" + sourceFile + "' в директорию '" + targetDir + "' : " + e.toString () );
            //e.printStackTrace ();
        } finally         {

        }
    }

    public static synchronized void archiveFile ( String sourceFile, String targetDir )
            throws IOException
    {
        File f = new File ( sourceFile );
        archiveFile ( f, targetDir );
    }

    public static List<String> getTextFromResource ( String fileName )
    {
        return null;
    }

    public static StringBuilder createNodeFilePath ( Project project, TreeObj selectNode ) throws WEditException
    {
        StringBuilder   filePath;
        File            projectDir;

        projectDir = project.getProjectDir();
        if ( projectDir == null )
            throw new WEditException ( null, "У Сборника '", project.getName(), "' \nотсутствует описание на корневой файл." );

        // - Сложить директории по парентам Разделов, исключая корневой (т.к. он уже входит в путь Проекта).
        filePath    = new StringBuilder (128);
        TreeObjTools.createFilePath ( selectNode, filePath );
        Log.l.debug ( "filePath 1 = '%s'", filePath );

        filePath.insert ( 0, projectDir.getAbsolutePath() );

        return filePath;
    }

    /**
     * Сохранить книгу в указанном файле. Перезаписывается.
     * @param fileBook    Файл для книги.
     * @param bookTitle   Книга как полный обьект.
     * @throws WEditException    Ошибки сохранения.
     */
    public static void saveBook ( File fileBook, BookTitle bookTitle ) throws WEditException
    {
        FileOutputStream newFile;
        String text;

        try
        {
            newFile = new FileOutputStream ( fileBook );

            // Залить шаблон книги
            text    = "<?xml version='1.0' encoding='UTF-8'?>\n\n";
            newFile.write ( text.getBytes( WCons.CODE_PAGE) );

            // bookContent
            bookTitle.getBookContent().toXml ( 0, newFile );
            
            newFile.flush();

            newFile.close();
            /*
            b   = fileBook.createNewFile();
            if ( ! b ) throw new WEditException ( null, "Не удалось создать файл новой книги \n'", fileBook, "'." );
            */
        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            Log.l.error ( Convert.concatObj ( "Не удалось создать файл новой книги \n'", fileBook, "'."  ), e);
            throw new WEditException ( e, "Не удалось создать файл новой книги \n'", fileBook, "'." );
        }
    }

    /**
     * Сохранить книгу в файле.
     * <BR/> Сначала пишем в tmp-файл - чтобы при ошибках записи старый файл не ломался.
     * <BR/> Данные из текстовых панелей уже должны быть скинуты в обьекты.
     * <BR/>
     * @param book Книга
     * @return     Размер книги в байт.
     * @throws WEditException   Ошибки сохранения
     */
    public static long saveBook ( BookContent book ) throws WEditException
    {
        FileOutputStream    newFile;
        String              text, fileName, fileNameTmp;
        File                file, file2, fileTmp;
        boolean             ok;
        long                fileSize0, fileSize1, fileSize2;

        if ( book == null )   throw new WEditException ( "Книга для сохранения не задана." );

        try
        {
            fileName        = book.getFileName();

            // -------------------------- TMP ------------------------
            fileNameTmp     = fileName + ".tmp";
            fileTmp         = new File ( fileNameTmp );

            // Сформировать файл для сохранения
            newFile = new FileOutputStream ( fileTmp );

            // Залить xml-шаблон книги
            text    = "<?xml version='1.0' encoding='UTF-8'?>\n\n";  // WCons.CODE_PAGE
            newFile.write ( text.getBytes( WCons.CODE_PAGE) );

            // Скинуть текст из открытых панелей в соответствующие обьекты дерева - должно быть перед этим.

            // bookContent скинуть в указанный файл в xml виде.
            book.toXml ( 0, newFile );

            newFile.flush();

            newFile.close();
            fileSize0   = fileTmp.length();        // новый размер

            // Удаляем старый
            file        = new File ( fileName );
            fileSize1   = file.length();        // старый размер
            ok          = file.delete();
            if ( ! ok )   throw new WEditException ( null, "Книга '", fileName, "' не смогла удалиться." );

            // Переименовать tmp в файл книги
            file2   = new File ( fileNameTmp );
            ok      = file2.renameTo ( file );
            if ( ! ok )   throw new WEditException ( null, "Книга '", fileNameTmp, "' не переименовалась в '", fileName, "'." );

            fileSize2   = file2.length();       // новый размер - почему-то 0

            Log.l.debug ( "Book name: ", book.getFileName(), "; old book size = ", book.getFileSize(), ": fileSize0 = ", fileSize0, "; fileSize1 = ", fileSize1, "; fileSize2 = ", fileSize2 );

            // отключаем флаг - было редактирование
            //book.setEdit ( false );

            // Размер файла
            return fileSize0;

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            text    = Convert.concatObj ( "Не удалось создать файл новой книги \n'", book.getFileName(), "'." );
            Log.l.error ( text, e );
            throw new WEditException ( e, text, e );
        }
    }

    public static WPair<Long,String> zipProject ( Project project ) throws WEditException
    {
        String              text, fileName;
        File                file, file2;
        boolean             ok;
        long                fileSize;
        WPair<Long,String>  result;

        result  = new WPair<Long,String> ( -1l, "" );
        //if ( project == null )   throw new WEditException ( "Сборник не задан." );
        if ( project == null )   return result;

        fileName = null;
        try
        {
            file        = project.getProjectDir();

            // Подняться выше и взять директорию archive. Если ее нет - создать.
            //fileParent  = file.getParentFile();
            fileName    = file.getParent() + "/archive";
            file2       = new File ( fileName );
            if ( ! file2.exists() )
            {
                ok = file2.mkdir();
                if ( ! ok ) throw new WEditException ( null, "Директория для архивирования '", file2, "' не смогла создастся." );
            }

            // Сформировать файл для сохранения
            fileName    = fileName + "/" + project.getFolderName() + "_" + Convert.getDateAsStr ( new Date(), "yyyy_MM_dd_HH_mm" ) + ".zip";
            // Заменить пробелы подчеркиванием
            fileName    = fileName.replace ( " ", "_" );
            Log.l.debug ( "ZIP project file name = %s", fileName );

            pack ( file, fileName );

            file2       = new File ( fileName );
            fileSize    = file2.length();

            result.setParam1 ( fileSize );
            result.setParam2 ( fileName );

            // Размер файла
            return result;

        } catch ( WEditException we )        {
            throw we;
        } catch ( Exception e )        {
            text    = Convert.concatObj ( "Не удалось создать архив сборника \n'", project.getName(), "' (",fileName,")" );
            Log.l.error ( text, e );
            throw new WEditException ( e, text, " :\n ", e );
        }
    }

    /**
     * Упаковать файлы.
     * Этот метод интересен тем, что, в отличие от большинства методов для манипуляции с файлами, он нерекурсивный.
     * Тут для обработки вложенных директорий используется очередь. 
     * @param directory Директория для архивации.
     * @param to       Имя нового файла, в который архивируем.
     * @throws WEditException
     */
    public static void pack ( File directory, String to ) throws WEditException
    {
        URI             base;
        Deque<File>     queue;
        OutputStream    out;
        Closeable       res;
        ZipOutputStream zout;
        String          name;
        InputStream     in;
        byte[]          buffer;
        File[]          files;

        res = null;

        try
        {
            base    = directory.toURI();
            queue   = new LinkedList<File> ();
            queue.push ( directory );

            out     = new FileOutputStream ( new File(to) );
            res     = out;

            zout    = new ZipOutputStream ( out );
            //zout.setLevel( Deflater.BEST_COMPRESSION) ; // укажем уровень сжатия будущего архива  -- 0-9

            res     = zout;

            while ( ! queue.isEmpty() )
            {
                directory   = queue.pop();
                files       = directory.listFiles();
                if ( files == null )  continue;

                for ( File child : files )
                {
                    name = base.relativize ( child.toURI() ).getPath();
                    if ( child.isDirectory() )
                    {
                        queue.push ( child );
                        name = name.endsWith ( "/" ) ? name : name + "/";
                        zout.putNextEntry ( new ZipEntry ( name ) );
                    }
                    else
                    {
                        zout.putNextEntry ( new ZipEntry ( name ) );
                        in = new FileInputStream ( child );
                        try
                        {
                            buffer = new byte[1024];
                            while ( true )
                            {
                                int readCount = in.read ( buffer );
                                if ( readCount < 0 )
                                {
                                    break;
                                }
                                zout.write ( buffer, 0, readCount );
                            }
                        } finally    {
                            in.close();
                        }
                        zout.closeEntry();
                    }
                }
            }

            res.close();

            // валидируем результат
            FileTools.isValid ( new File(to) );

        } catch ( Exception e )  {
            Log.file.error ( Convert.concatObj ( "error. directory = ", directory, "; toFile = ", to ), e );
        } finally     {
            if ( res != null )
            {
                try
                {
                    res.close();
                } catch ( IOException e )                {
                    Log.file.error ( Convert.concatObj ( "res.close error. directory = ", directory, "; toFile = ", to ), e );
                }
            }
        }
    }

    /**
     * Обратный процесс также реализуем нерекурсивным методом.Проходим по всем entry в архиве, директории сразу создаём, файлы добавляем в очередь.
     * Потом проходим по очереди и создаём файлы, копируя их из ZipInputStream в FileOutputStream
     * @param path
     * @param dir_to
     * @throws IOException
     */
    public static void unpack ( String path, String dir_to ) throws IOException
    {
        ZipFile zip = new ZipFile ( path );
        Enumeration entries = zip.entries ();
        LinkedList<ZipEntry> zfiles = new LinkedList<ZipEntry> ();
        while ( entries.hasMoreElements () )
        {
            ZipEntry entry = ( ZipEntry ) entries.nextElement ();
            if ( entry.isDirectory () )
            {
                new File ( dir_to + "/" + entry.getName () ).mkdir ();
            }
            else
            {
                zfiles.add ( entry );
            }
        }
        for ( ZipEntry entry : zfiles )
        {
            InputStream in = zip.getInputStream ( entry );
            OutputStream out = new FileOutputStream ( dir_to + "/" + entry.getName () );
            byte[] buffer = new byte[1024];
            int len;
            while ( ( len = in.read ( buffer ) ) >= 0 )
                out.write ( buffer, 0, len );
            in.close ();
            out.close ();
        }
        zip.close ();
    }

    public static boolean isValid ( final File file )
    {
        ZipFile zipfile = null;
        try
        {
            zipfile = new ZipFile ( file );
            return true;
        } catch ( ZipException e )       {
            return false;
        } catch ( IOException e )        {
            return false;
        } finally        {
            try
            {
                if ( zipfile != null )
                {
                    zipfile.close();
                    zipfile = null;
                }
            } catch ( IOException e )             {
            }
        }
    }

    public static String createFullFileName(Project project, TreeObj parentNode, WTreeObj wObj) throws WEditException
    {
        File projectDir = project.getProjectDir();
        if ( projectDir == null )
            throw new WEditException ( null, "У Сборника '", project.getName(), "' отсутствует описание на корневой файл." );

        // - Сложить директории по парентам Разделов, исключая корневой (т.к. он уже входит в путь Проекта).
        StringBuilder   filePath;
        filePath    = new StringBuilder (128);
        TreeObjTools.createFilePath ( parentNode, filePath );
        Log.l.debug ( "filePath 1 = '", filePath, "'" );

        filePath.insert ( 0, projectDir.getAbsolutePath() );

        if ( wObj != null) {
            filePath.append('/');
            if (wObj instanceof Section) {
                Section section = (Section) wObj;
                filePath.append(section.getFileName());
            }
            else if (wObj instanceof BookTitle) {
                BookTitle bookTitle = (BookTitle) wObj;
                filePath.append(bookTitle.getFileName());
            }
        }

        return filePath.toString();
    }

    public static String createFullFileName(Project project, TreeObj node) throws WEditException
    {

        File projectDir = project.getProjectDir();
        if ( projectDir == null )
            throw new WEditException ( null, "У Сборника '", project.getName(), "' отсутствует описание на корневой файл." );

        // - Сложить директории по парентам Разделов, исключая корневой (т.к. он уже входит в путь Проекта).
        StringBuilder   filePath;
        filePath    = new StringBuilder (128);
        TreeObjTools.createFilePath ( node, filePath );
        Log.l.debug ( "filePath 1 = '", filePath, "'" );

        filePath.insert ( 0, projectDir.getAbsolutePath() );

        return filePath.toString();
    }

    public static String createFullFileName(Project project, WTreeObj node) throws WEditException
    {

        File projectDir = project.getProjectDir();
        if ( projectDir == null )
            throw new WEditException ( null, "У Сборника '", project.getName(), "' отсутствует описание на корневой файл." );

        // - Сложить директории по парентам Разделов, исключая корневой (т.к. он уже входит в путь Проекта).
        StringBuilder   filePath;
        filePath    = new StringBuilder (128);
        TreeObjTools.createFilePath ( node, filePath );
        Log.l.debug ( "filePath 1 = '", filePath, "'" );

        filePath.insert ( 0, projectDir.getAbsolutePath() );

        return filePath.toString();
    }

    public static String createImageFileName(BookContent bookContent, String filename) {
        // Сформировать директорию книги
        File bookFile = new File(bookContent.getFileName());
        // - создать имя директории для картинок книг
        String bookImgDir = bookFile.getParent() + "/image";
        File bookImgDirFile = new File(bookImgDir);
        FileTools.createFolder(bookImgDirFile);

        // Сформирвоать полное имя файла картинки - /home/svj/Serg/SvjStores/zs/zs-6/image/Barracuda_01.jpg
        String targetFileName = bookImgDirFile + File.separator + filename;
        return targetFileName;
    }

    public static String createSmallImageFileName(BookContent bookContent, String filename) {
        // Сформировать директорию книги
        File bookFile = new File(bookContent.getFileName());
        // - создать имя директории для картинок книг
        String bookImgDir = bookFile.getParent() + "/image_small";
        File bookImgDirFile = new File(bookImgDir);
        FileTools.createFolder(bookImgDirFile);

        // Сформирвоать полное имя файла картинки - /home/svj/Serg/SvjStores/zs/zs-6/image/Barracuda_01.jpg
        String targetFileName = bookImgDirFile + File.separator + filename;
        return targetFileName;
    }



    public static void moveFile (String src, String dest) throws WEditException {
        try {
            Files.move(Paths.get(src), Paths.get(dest));
            Log.l.debug("File moved successfully.");
        } catch (Exception e) {
            Log.l.error("Exception while moving file from '" + src + "' to '" + dest + "' : " + e.getMessage(), e);
            throw new WEditException(null, "Ошибка перемещения файла '" + src + "'\n в '" + dest + "'.");
        }
    }

    public static void saveIcon(ImageIcon icon, String fileName, String imgType) throws WEditException {

        try {
            Image img = icon.getImage();

            // type - -1) TYPE_INT_ARGB 2) TYPE_3BYTE_BGR  3) TYPE_INT_RGB
            BufferedImage bi = new BufferedImage(
                    img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_3BYTE_BGR);

            Graphics2D g2 = bi.createGraphics();
            g2.drawImage(img, 0, 0, null);
            g2.dispose();
            ImageIO.write(bi, imgType, new File(fileName));
        } catch (Exception e) {
            String errMsg = "Save icon to file error. File = " + fileName + "; imgType = " + imgType;
            Log.file.error(errMsg, e);
            throw new WEditException(e, errMsg);
        }
    }

    /**
     * Т.к. в OpenJDK нет поддержки формата JPG, то его надо обрабатывать отдельно через тип  TYPE_INT_RGB
     * @param bi   Исходный образ
     * @return     Преобразованный образ
     */
    private static BufferedImage ensureOpaque(BufferedImage bi) {
        if (bi.getTransparency() == BufferedImage.OPAQUE)
            return bi;
        
        int w = bi.getWidth();
        int h = bi.getHeight();
        int[] pixels = new int[w * h];
        bi.getRGB(0, 0, w, h, pixels, 0, w);
        BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        bi2.setRGB(0, 0, w, h, pixels, 0, w);
        return bi2;
    }

    public static String createDir(String dir, String name) {
        String result = dir + File.separator + name;
        createFolder(new File(result));
        return result;
    }

}
