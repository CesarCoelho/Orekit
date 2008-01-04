package fr.cs.orekit.errors;

public class ConvergenceException
extends OrekitException {
  
  /** Simple constructor.
   * @param iterations number of iterations already realized
   */
  public ConvergenceException(int iterations) {
    super("unable to converge after {0} iterations",
          new Object[] { new Integer(iterations) });
  }
  
  private static final long serialVersionUID = -1389664478543066679L;
  
}
