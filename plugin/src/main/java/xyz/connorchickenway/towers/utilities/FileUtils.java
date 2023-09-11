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
            out.flush();    
        } catch ( Exception e )
        {
            e.printStackTrace();
        }
        if ( in != null )
        {
            try
            {
                in.close();
            } catch ( IOException e1 )
            {
                e1.printStackTrace();
            }
        }

        if ( out != null )
        {
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
