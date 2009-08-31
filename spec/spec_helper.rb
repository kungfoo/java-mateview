
require File.join(File.dirname(__FILE__), *%w(.. src ruby java-mateview))


JavaMateView::Bundle.load_bundles("input/")
JavaMateView::ThemeManager.load_themes("input/")