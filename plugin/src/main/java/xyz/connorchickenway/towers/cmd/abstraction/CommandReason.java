package xyz.connorchickenway.towers.cmd.abstraction;

public enum CommandReason
{
   
    OK( true ), MISSING_ARGUMENTS( false ), RETURN( true ), USAGE( false ), WRONG_ARGS( false ), ERROR( false );

    private boolean ret;

    CommandReason( boolean ret )
    {
        this.ret = ret;
    }

    public boolean getReturn()
    {
        return ret;
    }

}
