require 'rake/clean'
require 'net/http'

JRUBY_VERSION = '1.3.1'

jruby_command = case Config::CONFIG["host_os"]
  when /darwin/i
    'jruby -J-XstartOnFirstThread '
  else
    'jruby '
end

task :default => 'jruby:test'

namespace :java do
  desc "Rebuild the java class files"
  task :compile do
    puts "Compiling java files to *.class files"
    sh %+ant compile+
  end
  
  desc "Run jUnit tests against freshly compiled java classes"
  task :test do
    puts "Running JUnit Tets"
    sh %+ant test+
  end
  
  desc "Run Benchmarks"
  task :benchmark do
    puts "Compiling java files to *.class files"
    sh %+ant compile-bench+
    runner = 'ch.mollusca.benchmarking.BenchmarkRunner'
    classes = ['com.redcareditor.mate.GrammarBenchmark']
    classpath = '.:bench/:bin/:lib/joni.jar:lib/jdom.jar:lib/jcodings.jar'
    classes.each do |clazz|
      sh "java -cp #{classpath} #{runner} #{clazz}"
    end
  end
end

namespace :jruby do
  desc "Run ruby tests against a freshly compiled build"
  task :test => ['java:test'] do
    puts "Running RSpec Tests"
    sh %+#{jruby_command} -S spec spec/+
  end
end


namespace :build do
  desc "Get jruby-complete to build release jar"
  task :get_jruby do
    jruby_complete = "jruby-complete-#{JRUBY_VERSION}.jar"
    location = "http://dist.codehaus.org/jruby/#{JRUBY_VERSION}/#{jruby_complete}"
    local_path = "lib/#{jruby_complete}"
    unless File.exists?(local_path)
      puts "Getting required #{jruby_complete}"
      response = Net::HTTP.get(URI.parse(location))
      File.open(local_path, "wb") { |file| file.write(response) }
    else
      puts "Already have required #{jruby_complete}, skipping download"
    end
  end
  
  desc "Build the release *.jar"
  task :release => [:get_jruby] do
    puts "Building release *.jar"
    
  end
end