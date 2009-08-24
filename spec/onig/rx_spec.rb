require File.join(File.dirname(__FILE__), "..", "spec_helper.rb")

describe Onig::Rx do
  it "should match" do
    rx = Onig::Rx.createRx("f.o")
    rx.search("foo").should be_an_instance_of(Onig::Match)
  end
  
  it "should return nil if no match" do
    rx = Onig::Rx.createRx("f.o")
    rx.search("bar").should be_nil
  end
end