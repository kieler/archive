#!/usr/bin/perl
#
# branchfilter.pl - filters git repositories
#

use Getopt::Std;
use Array::Utils qw(:all);

getopts("f:r");

print "Usage: branchfilter.pl -f <pathsFile> [-r]\n" if (!$opt_f);

# Read paths from file and construct a valid string to pass to ls"

open (PATHFILE, "< $opt_f");
my $paths = do { local $/; <PATHFILE> };  
$paths =~ s/\n/\* /g;
close (PATHFILE);
 
# construct list of paths to delete
my @preservePaths = `ls -d $paths`;
my @allPaths = `ls`;
my @deleteList = array_minus(@allPaths, @preservePaths);

# need the paths on one line, no newlines
my $deleteLine = join(" ",@deleteList);
$deleteLine =~ s/\n//g;

print "git filter-branch --tree-filter \'rm -rf $deleteLine\' HEAD\n" if (!$opt_r);
print "git filter-branch --index-filter \'rm -rf $deleteLine\' HEAD\n" if ($opt_r);
