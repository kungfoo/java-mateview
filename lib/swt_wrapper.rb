

module Swt
  import org.eclipse.swt.SWT
  
  module Widgets
    import org.eclipse.swt.widgets.Display
    import org.eclipse.swt.widgets.Shell
    import org.eclipse.swt.widgets.Composite
    import org.eclipse.swt.widgets.Menu
    import org.eclipse.swt.widgets.MenuItem
  end
  
  module Layout
    import org.eclipse.swt.layout.FillLayout
  end
  
  module Graphics
    import org.eclipse.swt.graphics.Font
    import org.eclipse.swt.graphics.Point
    import org.eclipse.swt.graphics.RGB
  end
end

module Jface
  import org.eclipse.jface.action.Action
  import org.eclipse.jface.action.MenuManager
  import org.eclipse.jface.window.ApplicationWindow
end