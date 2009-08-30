require 'java'

$CLASSPATH << File.expand_path(File.join(File.dirname(__FILE__), *%w(.. .. bin)))
$:.push(File.expand_path(File.join(File.dirname(__FILE__), *%w(.. .. lib))))

require 'jdom'

require 'rbconfig'

if Config::CONFIG["host_os"] =~ /darwin/
  require 'swt-osx'
else
  require 'swt-linux'
end

require 'swt_wrapper'
require 'org.eclipse.core.commands'
require 'org.eclipse.core.runtime_3.5.0.v20090525'
require 'org.eclipse.equinox.common'
require 'org.eclipse.jface.databinding_1.3.0.I20090525-2000'
require 'org.eclipse.jface'
require 'org.eclipse.jface.text_3.5.0'
require 'org.eclipse.osgi'
require 'org.eclipse.text_3.5.0.v20090513-2000'

unless defined?(JavaMateView)

  class JavaMateView
    import com.redcareditor.mate.MateText
    import com.redcareditor.mate.Grammar
    import com.redcareditor.mate.Bundle
    import com.redcareditor.mate.TextLocation
    import com.redcareditor.theme.Theme
    import com.redcareditor.theme.ThemeManager
  end

  class Plist
    import com.redcareditor.plist.Dict
    import com.redcareditor.plist.PlistNode
    import com.redcareditor.plist.PlistPropertyLoader
  end

  class Onig
    import com.redcareditor.onig.Rx
    import com.redcareditor.onig.Match
  end
end
