package xyz.connorchickenway.towers.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils
{

    public static void delete( File file )
    {
        File[] listFiles = file.listFiles();
        if ( listFiles != null )
        {
            for ( File f : listFiles )
            {
                if ( f.isDirectory() ) delete( f );
                else f.delete();
            }
        }
        file.delete();
    }

    public static void copyDirectory( File source, File destination )
    {
        if ( source.isDirectory() )
        {
            if ( !destination.exists() )
                destination.mkdirs();

            String files[] = source.list();

            for ( String file : files )
            {
                File srcFile = new File( source, file );
                File destFile = new File( destination, file );

                copyDirectory( srcFile, destFile );
            }
        }
        else 
            copyFile(source, destination);
    }

    public static void copyFile( File source, File destination )
    {
        InputStream in = null;
        OutputStream out = null;

        try
        {
            in = new FileInputStream( source );
            out = new FileOutputStream( destination );

            byte[] buffer = new byte[ 1024 ];

            int length;
            while( ( length = in.read( buffer ) ) > 0 )
                out.write( buffer, 0, length );
        } catch ( Exception e )
        {
            try
            {
                in.close();
            } catch ( IOException e1 )
            {
                e1.printStackTrace();
            }

            try
            {
                out.close();
            } catch ( IOException e1 )
            {
                e1.printStackTrace();
            }
        }
    }

}
