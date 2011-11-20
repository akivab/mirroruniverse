package mirroruniverse.sim;

public interface Player 
{
	public abstract int lookAndMove( int[][] aintViewL, int[][] aintViewR );
	public abstract void setStarting( int[] sl, int[] sr );

}
