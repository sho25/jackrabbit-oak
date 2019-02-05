begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|run
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|Profiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|run
operator|.
name|commons
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|threadDump
operator|.
name|ThreadDumpThreadNames
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|threadDump
operator|.
name|ThreadDumpCleaner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|threadDump
operator|.
name|ThreadDumpConverter
import|;
end_import

begin_class
specifier|public
class|class
name|ThreadDumpCommand
implements|implements
name|Command
block|{
specifier|public
specifier|final
specifier|static
name|String
name|THREADDUMP
init|=
literal|"threaddump"
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|convertSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"convert"
argument_list|,
literal|"convert the thread dumps to the standard format"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|filterSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"filter"
argument_list|,
literal|"filter the thread dumps, only keep working (running), interesting threads "
operator|+
literal|"(for example, threads that read from sockets are ignored, "
operator|+
literal|"as they are typically waiting for input; "
operator|+
literal|"system threads such as GC are also ignored)"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|threadNamesSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"threadNames"
argument_list|,
literal|"create a summary of thread names"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|profileSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"profile"
argument_list|,
literal|"profile the thread dumps"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|profileClassesSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"profileClasses"
argument_list|,
literal|"profile classes"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|profileMethodsSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"profileMethods"
argument_list|,
literal|"profile methods"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|profilePackagesSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"profilePackages"
argument_list|,
literal|"profile packages"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|helpSpec
init|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|asList
argument_list|(
literal|"h"
argument_list|,
literal|"?"
argument_list|,
literal|"help"
argument_list|)
argument_list|,
literal|"show help"
argument_list|)
operator|.
name|forHelp
argument_list|()
decl_stmt|;
name|OptionSet
name|options
init|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|parser
operator|.
name|nonOptions
argument_list|(
literal|"file or directory containing thread dumps "
operator|+
literal|"(ensure it does not contain other files, such as binaries)"
argument_list|)
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|helpSpec
argument_list|)
operator|||
name|options
operator|.
name|nonOptionArguments
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Mode: "
operator|+
name|THREADDUMP
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
return|return;
block|}
name|boolean
name|convert
init|=
name|options
operator|.
name|has
argument_list|(
name|convertSpec
argument_list|)
decl_stmt|;
name|boolean
name|filter
init|=
name|options
operator|.
name|has
argument_list|(
name|filterSpec
argument_list|)
decl_stmt|;
name|boolean
name|threadNames
init|=
name|options
operator|.
name|has
argument_list|(
name|threadNamesSpec
argument_list|)
decl_stmt|;
name|boolean
name|profile
init|=
name|options
operator|.
name|has
argument_list|(
name|profileSpec
argument_list|)
decl_stmt|;
name|boolean
name|profileClasses
init|=
name|options
operator|.
name|has
argument_list|(
name|profileClassesSpec
argument_list|)
decl_stmt|;
name|boolean
name|profileMethods
init|=
name|options
operator|.
name|has
argument_list|(
name|profileMethodsSpec
argument_list|)
decl_stmt|;
name|boolean
name|profilePackages
init|=
name|options
operator|.
name|has
argument_list|(
name|profilePackagesSpec
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|fileName
range|:
operator|(
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|options
operator|.
name|nonOptionArguments
argument_list|()
operator|)
control|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
operator|||
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".gz"
argument_list|)
condition|)
block|{
name|file
operator|=
name|combineAndExpandFiles
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Combined into "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|convert
condition|)
block|{
name|file
operator|=
name|ThreadDumpConverter
operator|.
name|process
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Converted to "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|threadNames
condition|)
block|{
name|File
name|f
init|=
name|ThreadDumpThreadNames
operator|.
name|process
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Thread names written to "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filter
condition|)
block|{
name|file
operator|=
name|ThreadDumpCleaner
operator|.
name|process
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Filtered into "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|threadNames
condition|)
block|{
name|File
name|f
init|=
name|ThreadDumpThreadNames
operator|.
name|process
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Thread names written to "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|profile
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|profileClasses
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
literal|"-classes"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|profileMethods
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
literal|"-methods"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|profilePackages
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
literal|"-packages"
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|Profiler
operator|.
name|main
argument_list|(
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|File
name|combineAndExpandFiles
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".gz"
argument_list|)
operator|&&
operator|!
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
condition|)
block|{
return|return
name|file
return|;
block|}
block|}
name|File
name|target
init|=
operator|new
name|File
argument_list|(
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|".txt"
argument_list|)
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|target
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|count
init|=
name|processFileOrDirectory
argument_list|(
name|file
argument_list|,
name|writer
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    (total "
operator|+
name|count
operator|+
literal|" full thread dumps)"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|target
return|;
block|}
specifier|private
specifier|static
name|int
name|processFileOrDirectory
parameter_list|(
name|File
name|file
parameter_list|,
name|PrintWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
return|return
name|processFile
argument_list|(
name|file
argument_list|,
name|writer
argument_list|)
return|;
block|}
name|int
name|count
init|=
literal|0
decl_stmt|;
name|File
index|[]
name|list
init|=
name|file
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|<
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|File
name|o1
parameter_list|,
name|File
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|f
range|:
name|list
control|)
block|{
name|count
operator|+=
name|processFileOrDirectory
argument_list|(
name|f
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
specifier|private
specifier|static
name|int
name|processFile
parameter_list|(
name|File
name|file
parameter_list|,
name|PrintWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
try|try
block|{
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".DS_Store"
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|fullThreadDumps
init|=
literal|0
decl_stmt|;
name|String
name|fileModifiedTime
init|=
operator|new
name|Timestamp
argument_list|(
name|file
operator|.
name|lastModified
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"lastModified "
operator|+
name|fileModifiedTime
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".gz"
argument_list|)
condition|)
block|{
comment|// System.out.println("Extracting " + file.getAbsolutePath());
name|InputStream
name|fileStream
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|InputStream
name|gzipStream
init|=
operator|new
name|GZIPInputStream
argument_list|(
name|fileStream
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|gzipStream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
name|fileStream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Warning: file skipped. Please extract first: "
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Reading "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|LineNumberReader
name|in
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|s
decl_stmt|;
try|try
block|{
name|s
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// EOFException: Unexpected end of ZLIB input stream
break|break;
block|}
catch|catch
parameter_list|(
name|ZipException
name|e
parameter_list|)
block|{
comment|// java.util.zip.ZipException: invalid block type
break|break;
block|}
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"Full thread dump"
argument_list|)
operator|||
name|s
operator|.
name|startsWith
argument_list|(
literal|"Full Java thread dump"
argument_list|)
condition|)
block|{
name|fullThreadDumps
operator|++
expr_stmt|;
block|}
name|writer
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fullThreadDumps
operator|>
literal|0
condition|)
block|{
name|count
operator|++
expr_stmt|;
comment|// System.out.println("    (contains " + fullThreadDumps + " full thread dumps; " + fileModifiedTime + ")");
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

