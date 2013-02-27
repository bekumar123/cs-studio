#*******************************************************************************
#
# Copyright (c) 2013 Philip Wenig
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
# 
# Contributors:
#	Philip Wenig
#
#*******************************************************************************/

#
# Reads the file plugins.txt and creates a pom.xml file
# in each listed project.
#
Write-Host "Start creating pom files ..."

$plugins = Get-Content .\plugins.txt
foreach ($plugin in $plugins) {
	Write-Host "create pom.xml for "$plugin
	cd $plugin
	cd "META-INF"
	$line = Get-Content MANIFEST.MF | Select-String "Bundle-Version: "
	$data = "$line".split(" ")
	$version = $data[1].trim()
	$artifactId = "$plugin".trim()
	cd ..
	"<?xml version=`"1.0`" encoding=`"UTF-8`"?>`n<project>`n  <modelVersion>4.0.0</modelVersion>`n`n  <parent>`n    <groupId>org.csstudio</groupId>`n    <artifactId>org.csstudio.plugins.parent</artifactId>`n    <version>BUILD-VERSION</version>`n  </parent>`n`n  <artifactId>$artifactId</artifactId>`n  <packaging>eclipse-plugin</packaging>`n  <version>$version</version>`n`n</project>`n" | out-file -Encoding "UTF8" ".\pom.xml"
#	"<?xml version=`"1.0`" encoding=`"UTF-8`"?>" | out-file ".\pom.xml"
#	"<project>" | out-file ".\pom.xml" -append
#	"  <modelVersion>4.0.0</modelVersion>" | out-file ".\pom.xml" -append
#	"" | out-file ".\pom.xml" -append
#	"  <parent>" | out-file ".\pom.xml" -append
#	"    <groupId>org.csstudio</groupId>" | out-file ".\pom.xml" -append
#	"    <artifactId>org.csstudio.plugins.parent</artifactId>" | out-file ".\pom.xml" -append
#	"    <version>BUILD-VERSION</version>" | out-file ".\pom.xml" -append
#	"  </parent>" | out-file ".\pom.xml" -append
#	"" | out-file ".\pom.xml" -append
#	"  <artifactId>$artifactId</artifactId>" | out-file ".\pom.xml" -append
#	"  <packaging>eclipse-plugin</packaging>" | out-file ".\pom.xml" -append
#	"  <version>$version</version>" | out-file ".\pom.xml" -append
#	"" | out-file ".\pom.xml" -append
#	"</project>" | out-file ".\pom.xml" -append
	cd ..
}

Write-Host "Finished successfully"
