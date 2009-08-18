require 'rake/clean'

task :default => 'jruby:test'

namespace :java do
  desc "Rebuild the java class files"
  task :compile do
    puts "Compiling java files to *.class files"
    sh %+ant compile+
  end
  
  desc "Run jUnit tests against freshly compiled java classes"
  task :test => [:compile] do
    puts "Running JUnit Tets"
    sh %+ant test+
  end
end

namespace :jruby do
  desc "Run ruby tests against a freshly compiled build"
  task :test => ['java:test'] do
    puts "Running RSpec Tests"
    sh %+jruby -S spec spec/+
  end
end