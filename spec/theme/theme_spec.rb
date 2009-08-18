require File.join(File.dirname(__FILE__), "..", "spec_helper.rb")

describe JavaMateView::Theme do
  describe "it has global settings" do
    before(:all) do
      dict = Plist::Dict.parse_file("spec/fixtures/Railscasts.tmTheme")
      @theme = JavaMateView::Theme.new(dict)
    end
    
    it "sets the background" do
      @theme.globalSettings.get("background").should == "#2B2B2B"
    end
  end
end