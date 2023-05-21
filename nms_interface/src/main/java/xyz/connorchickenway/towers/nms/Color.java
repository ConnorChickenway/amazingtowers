package xyz.connorchickenway.towers.nms;

public enum Color
{

    WHITE( 0x0 ), ORANGE( 0x1 ), MAGENTA( 0x2 ), LIGHT_BLUE( 0x3 ), YELLOW( 0x4 ), LIME( 0x5 ), PINK( 0x6 ),
    GRAY( 0x7 ), LIGHT_GRAY( 0x8 ), CYAN( 0x9 ), PURPLE( 0xA ), BLUE( 0xB ), BROWN( 0xC ), GREEN( 0xD ), RED( 0xE ),
    BLACK( 0xF );

    private byte data;

    Color( int data )
    {
        this.data = ( byte )data;
    }

    public byte getData()
    {
        return data;
    }

}
