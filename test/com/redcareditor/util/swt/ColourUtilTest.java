package com.redcareditor.util.swt;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * User: Mat Schaffer <mat.schaffer@gmail.com>
 * Date: Sep 6, 2009
 * Time: 1:08:54 PM
 */
public class ColourUtilTest {
  @Test
  public void shouldHandleTwilightColors() {
    assertEquals("#FFFFFF", ColourUtil.mergeColour("#FFFFFF", "#FFFFFF08"));
  }
}
